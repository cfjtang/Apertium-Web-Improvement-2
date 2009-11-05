/*
 *  ApertiumServer. Highly scalable web service implementation for Apertium.
 *  Copyright (C) 2009  Víctor Manuel Sánchez Cartagena
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gsoc.apertium.translationengines.router.logic;


import com.gsoc.apertium.translationengines.rmi.transferobjects.DaemonInformation;
import com.gsoc.apertium.translationengines.rmi.transferobjects.LanguagePair;
import com.gsoc.apertium.translationengines.rmi.transferobjects.TranslationServerId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Wrapps a {@link PlacementController} instance that decides where the different
 * language pair instances should run and a {@link PlacementExecutor} instance
 * that start and stops these instances
 * @author vmsanchez
 */
public class PlacementControllerAdapter {

    /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(PlacementControllerAdapter.class);

    /**
     * Placement controller. Decides whete the different application instances should run.
     */
    private final PlacementController<LanguagePair,TranslationServerId> placementController;
    
    /**
     * Placement executor. Starts ant stops application instances applying the results of
     * placement controller.
     */
    private final PlacementExecutor executor;

    /**
     * Constructor that needs an instance of <code>ITranslationServerReferences</code> to
     * be able to communicate with the servers.
     * @param references
     */
    public PlacementControllerAdapter(ITranslationServerReferences references) {
        placementController=new PlacementController<LanguagePair, TranslationServerId>();
        executor = new PlacementExecutor(references);
    }

    /**
     * Tries to  satisfy the maximum amount of CPU demand doing the minimum amount of application starts and stops in
     * the servers.
     *
     * @param pairsInfo Language pairs CPU and memory demands
     * @param serversInfo Servers capacity
     * @return Map shoiwing how load is distributed among the servers
     */
    public  Map<LanguagePair,Map<TranslationServerId,Integer>> computeNewPlacement(Map<LanguagePair,LanguagePairInfo> pairsInfo,Map<TranslationServerId,TranslationServerInformation> serversInfo)
    {
        StringBuilder debugMessage = new StringBuilder("Computing new placemnt. Demands:");
        for(Entry<LanguagePair,LanguagePairInfo> entry: pairsInfo.entrySet())
        {
            debugMessage.append(entry.getKey()+":"+entry.getValue().getCpuDemand()+"\n");
        }
        logger.trace(debugMessage.toString());
        //TODO: ¿Some lock?      

        List<LanguagePair> apps = new ArrayList<LanguagePair>(pairsInfo.keySet());
        int[] cpuDemand = new int[apps.size()];
        int[] memoryDemand = new int[apps.size()];
        for(int i=0;i<apps.size();i++)
        {
            cpuDemand[i]=pairsInfo.get(apps.get(i)).getCpuDemand();
            memoryDemand[i]=pairsInfo.get(apps.get(i)).getMemoryDemand();
        }
        placementController.setApps(apps);
        placementController.setCpuDemand(cpuDemand);
        placementController.setMemoryDemand(memoryDemand);

        List<TranslationServerId> servers = new ArrayList<TranslationServerId>(serversInfo.keySet());
        int[] cpuCapacity = new int[servers.size()];
        int[] cpuCapacityPerDaemon= new int[servers.size()];
        int[] memoryCapacity = new int[servers.size()];

        for(int i=0;i<servers.size();i++)
        {
            cpuCapacity[i]=serversInfo.get(servers.get(i)).getServerInformation().getCpuCapacity();
            cpuCapacityPerDaemon[i]=serversInfo.get(servers.get(i)).getServerInformation().getCpuCapacityPerDaemon();
            memoryCapacity[i]=serversInfo.get(servers.get(i)).getServerInformation().getMemoryCapacity();
        }
        placementController.setServers(servers);
        placementController.setCpuCapacity(cpuCapacity);
        placementController.setCpuCapacitiesPerDaemon(cpuCapacityPerDaemon);
        placementController.setMemoryCapacity(memoryCapacity);

        int[][] R = new int[apps.size()][servers.size()];
        int[][] I = new int[apps.size()][servers.size()];
        
        for(int j=0;j<servers.size();j++)
        {
            TranslationServerInformation serverInfo = serversInfo.get(servers.get(j));
            List<LanguagePair> runningApps = new ArrayList<LanguagePair>();
            if(serverInfo.getServerStatus()!=null)
                for(DaemonInformation daemonInformation: serverInfo.getServerStatus().getDaemonsInformation())
                {
                    runningApps.add(daemonInformation.getPair());
                }
            
            for(int i=0; i<apps.size();i++)
            {
                LanguagePair pair = apps.get(i);
                if(serverInfo.getServerInformation().getSupportedPairs().contains(pair))
                    R[i][j]=1;
                else
                    R[i][j]=0;

                I[i][j]=0;
                for(LanguagePair p:runningApps)
                    if(p.equals(pair))
                        I[i][j]+=1;
            }
        }

        placementController.setR(R);
        placementController.setI(I);

        //Debug
        StringBuilder oldPlacementStr = new StringBuilder("I before placement: ");
        for(int i=0; i<apps.size();i++)
        {
            oldPlacementStr.append("Pair "+apps.get(i)+" - ");
            for(int j=0; j<servers.size();j++)
            {
                oldPlacementStr.append(servers.get(j)+":"+I[i][j]+" ");
            }
             oldPlacementStr.append("\n");
        }
        logger.trace(oldPlacementStr.toString());

        placementController.place();

        int[][] newL = placementController.getOutputL();
        int[][] newI=placementController.getOutputI();

        //Log new placement
        StringBuilder newPlacementStr = new StringBuilder();

        Map<LanguagePair,Map<TranslationServerId,Integer>> loadDistributionMatrix=new HashMap<LanguagePair, Map<TranslationServerId, Integer>>();
        for(int i=0; i<apps.size();i++)
        {
            newPlacementStr.append("Pair "+apps.get(i)+" - ");

            Map<TranslationServerId, Integer> appMap = new HashMap<TranslationServerId, Integer>();
            for(int j=0; j<servers.size();j++)
            {
                newPlacementStr.append(servers.get(j)+":"+newL[i][j]+" "+newI[i][j]+" ");
                //if(newL[i][j]>0)
                    appMap.put(servers.get(j), newL[i][j]);
            }
            loadDistributionMatrix.put(apps.get(i), appMap);
            newPlacementStr.append("\n");
        }

        logger.info(newPlacementStr.toString());
        

        executor.executeNewPlacement(apps, servers, I,placementController.getOutputI());

        return loadDistributionMatrix;
    }

    /**
     *
     * @return Map showing which applications are running on each server
     */
    public Map<LanguagePair,Set<TranslationServerId>> getI()
    {
        Map<LanguagePair,Set<TranslationServerId>> returnMap = new HashMap<LanguagePair, Set<TranslationServerId>>();
        int[][] I = placementController.getOutputI();
        List<LanguagePair> apps=placementController.getApps();
        List<TranslationServerId> servers = placementController.getServers();
        for(int i=0; i<apps.size();i++)
        {
            Set<TranslationServerId> runningOnServers = new HashSet<TranslationServerId>();
            for(int j=0; j<servers.size();j++)
            {
                if(I[i][j]>0)
                    runningOnServers.add(servers.get(j));
            }
            returnMap.put(apps.get(i), runningOnServers);
        }
        return returnMap;
    }

}
