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


import com.gsoc.apertium.translationengines.rmi.transferobjects.LanguagePair;
import com.gsoc.apertium.translationengines.rmi.transferobjects.TranslationServerId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Starts and stops application following the placement matrix returned by
 * {@link PlacementController}.
 *
 * @author vmsanchez
 */
public class PlacementExecutor {

    /**
     * Commons-logging logger
     */
     static Log logger = LogFactory.getLog(PlacementExecutor.class);

     /**
      * References to translation servers that allow communication with them
      */
    private ITranslationServerReferences translationServerReferences;

    //private Map<TranslationServerId,Map<LanguagePair,Integer>> oldPlacementMap;

    /**
     * Constructor that needs an instance of {@link ITranslationServerReferences} to
     * be able to communicate with translation servers.
     * @param references
     */
    public PlacementExecutor(ITranslationServerReferences references) {
        translationServerReferences=references;
        //oldPlacementMap=new HashMap<TranslationServerId, Map<LanguagePair,Integer>>();
       
    }

    /**
     * Starts and stops applications following the a new placement matrix.
     * @param apps List of applications
     * @param servers List of servers
     * @param oldI Old placement matrix
     * @param I New placement matrix
     */
    public void executeNewPlacement(List<LanguagePair> apps,List<TranslationServerId> servers,int[][] oldI,int[][] I)
    {
        Map<TranslationServerId,Map<LanguagePair,Integer>> oldPlacementMap = new HashMap<TranslationServerId, Map<LanguagePair,Integer>>();
        for(int j=0;j<servers.size();j++)
        {
            TranslationServerId serverId=servers.get(j);
            oldPlacementMap.put(serverId, new HashMap<LanguagePair,Integer>());
            for(int i=0; i<apps.size();i++)
            {
                LanguagePair pair=apps.get(i);
                oldPlacementMap.get(serverId).put(pair,new Integer(oldI[i][j]));
            }
        }

        Map<TranslationServerId,Map<LanguagePair,Integer>> newPlacementMap = new HashMap<TranslationServerId, Map<LanguagePair,Integer>>();
        for(int j=0;j<servers.size();j++)
        {
            TranslationServerId serverId=servers.get(j);
            newPlacementMap.put(serverId, new HashMap<LanguagePair,Integer>());
            for(int i=0; i<apps.size();i++)
            {
                LanguagePair pair=apps.get(i);
                newPlacementMap.get(serverId).put(pair,new Integer(I[i][j]));
            }
        }

        for(TranslationServerId serverId: newPlacementMap.keySet())
        {
            if(oldPlacementMap.containsKey(serverId))
            {
                Map<LanguagePair,Integer> removeApps = new HashMap<LanguagePair,Integer>(oldPlacementMap.get(serverId));
                for(LanguagePair pair: apps)
                {
                    removeApps.put(pair, removeApps.get(pair)-newPlacementMap.get(serverId).get(pair));
                }
                 Map<LanguagePair,Integer> startApps = new HashMap<LanguagePair,Integer>(newPlacementMap.get(serverId));

                for(LanguagePair pair:apps)
                {
                    startApps.put(pair, startApps.get(pair)-oldPlacementMap.get(serverId).get(pair));
                }

                //Stop apps
                for(Entry<LanguagePair,Integer> entry: removeApps.entrySet())
                {
                     if(entry.getValue()>0)
                    {
                        try
                        {
                            logger.info("Stopping "+entry.getValue()+" "+entry.getKey()+" on "+serverId);
                            translationServerReferences.getTranslationEngine(serverId).stopDaemons(entry.getKey(), entry.getValue());
                        }
                        catch(Exception e)
                        {
                            //logger.....
                            e.printStackTrace();
                        }
                     }
                }

                //Start apps
                for(Entry<LanguagePair,Integer> entry: startApps.entrySet())
                {
                     if(entry.getValue()>0)
                    {
                        try
                        {
                            logger.info("Starting "+entry.getValue()+" "+entry.getKey()+" on "+serverId);
                            translationServerReferences.getTranslationEngine(serverId).startDaemons(entry.getKey(),entry.getValue());
                         }
                        catch(Exception e)
                        {
                            //logger.....
                            e.printStackTrace();
                        }
                     }
                }
            }
            else
            {
                //Start all apps
                for(Entry<LanguagePair,Integer> entry: newPlacementMap.get(serverId).entrySet())
                {
                    if(entry.getValue()>0)
                    {
                         try
                         {
                            logger.info("Starting "+entry.getValue()+" "+entry.getKey()+" on "+serverId);
                            translationServerReferences.getTranslationEngine(serverId).startDaemons(entry.getKey(),entry.getValue());
                         }
                        catch(Exception e)
                        {
                            //logger.....
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        oldPlacementMap=newPlacementMap;
    }

}
