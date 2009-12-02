/*
 *  ScaleMT. Highly scalable framework for machine translation web services
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
package org.scalemt.router.logic;

import org.scalemt.rmi.transferobjects.DaemonConfiguration;
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.rmi.transferobjects.TranslationServerId;
import org.scalemt.router.ondemandservers.IOnDemandServerInterface;
import org.scalemt.router.ondemandservers.OnDemandServerInterfaceFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dynamically starts or stops servers through {@link com.gsoc.apertium.translationengines.router.ondemandservers.IOnDemandServerInterface}.
 * The decision of starting or stopping servers is based on the different language pairs predicted demand and
 * the placement made by the placement controller.<br/>
 * This class reads the following properties from <code>OnDemandServerInterface.properties</code>:
 * <ul>
 * <li><code>maxInactivityTime</code> is the maximum time a server can be running without receiving any load from the placement controller.</li>
 * <li><code>startUpTimeout</code> is the maximum time the system waits for  newly started server to contact the request router.</li>
 * </ul>
 *
 * @author vitaka
 */
public class DynamicServerManagement {

    /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(DynamicServerManagement.class);


    /**
     * Information about a server started by this class that hasn't contacted the request router yet.
     */
    private class WaitingStartingServer
    {
        /**
         * Server's public host
         */
        private String host;

        /**
         * Time when the server was started
         */
        private long startTime;

        public WaitingStartingServer(String host, long startTime) {
            this.host = host;
            this.startTime = startTime;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        
    }

    /**
     * Servers are started and stopped through this interface
     */
    private IOnDemandServerInterface onDemandServerInterface;

    /**
     * Servers that have been started but haven't contacted request router
     */
    private Set<WaitingStartingServer> waitingStartingServers;

    /**
     * Servers that have been started a long time ago but haven't contacted request router
     */
    private Set<String> timeoutStartingServers;
    /**
     * Servers started by this class and running OK
     */
    private Set<String> runningServers;

    /**
     * For each server, the map stores the last time they received load from placement controller
     */
    private Map<TranslationServerId,Long> lastTimeWithLoad;

    /**
     * <code>startUpTimeout</code> from <code>OnDemandServerInterface.properties</code>
     */
    private long startUpTimeout;

    /**
     * <code>maxInactivityTime</code> from <code>OnDemandServerInterface.properties</code>
     */
    private long maxInactivityTime;

    public DynamicServerManagement() {
        onDemandServerInterface=OnDemandServerInterfaceFactory.getOnDemandServerInterface();
        waitingStartingServers=new HashSet<WaitingStartingServer>();
        runningServers=new HashSet<String>();
        timeoutStartingServers=new HashSet<String>();
        lastTimeWithLoad=new HashMap<TranslationServerId, Long>();
        startUpTimeout=10*60*1000;
        try
        {
            startUpTimeout=Long.parseLong(Util.readPropertyFromFile("startUpTimeout", "/OnDemandServerInterface.properties"));
        }catch(Exception e){}
        maxInactivityTime=3000000;
        try
        {
            maxInactivityTime=Long.parseLong(Util.readPropertyFromFile("maxInactivityTime", "/OnDemandServerInterface.properties"));
        }catch(Exception e){}
    }

    /**
     * Processes the load demand of the different language pairs and the current allocation of this load
     * in the diffrent running servers to determine if any server should be started or stopped.<br/>
     *
     * Servers to be stopped are returned. The application ApertiumServerSlave running on that servers should be shutted down via RMI, and then
     * the method {@link DynamicServerManagement#notifyServerStop(java.lang.String) } called.
     * <br/>
     * 
     * Servers to be started are started via {@link com.gsoc.apertium.translationengines.router.ondemandservers.IOnDemandServerInterface}. When
     * a newly started server contacts request router, method {@link DynamicServerManagement#notifyServerStartup(java.lang.String) } should be called.
     *
     *
     * @param languagePairsInfo Information about language pairs CPU demands.
     * @param loadDistributionMatrix Load assigned to each server
     * @return Servers to be stopped
     */
   public  List<TranslationServerId> processPlacementControllerResult(Map<DaemonConfiguration, DaemonConfigurationInformation> languagePairsInfo, Map<DaemonConfiguration, Map<TranslationServerId, Integer>> loadDistributionMatrix) {

        synchronized(this)
        {
            List<TranslationServerId> serversToStop = new ArrayList<TranslationServerId>();
            
            Iterator<WaitingStartingServer> it = waitingStartingServers.iterator();
            long now=System.currentTimeMillis();
            while(it.hasNext()){
                WaitingStartingServer wsv = it.next();
                if(now-wsv.getStartTime()>startUpTimeout)
                {
                    logger.error("Timeout waiting for "+wsv.getHost()+" startup");
                    it.remove();
                }
            }

            int totalLoadDemand =0;
            for(DaemonConfigurationInformation lpi: languagePairsInfo.values())
                totalLoadDemand+=lpi.getCpuDemand();

            int totalSatisfiedLoad=0;
            Map<TranslationServerId,MutableInt> serversLoad=new HashMap<TranslationServerId, MutableInt>();
            for(Map<TranslationServerId, Integer> mmap:  loadDistributionMatrix.values())
            {
                for(Entry<TranslationServerId, Integer> entry: mmap.entrySet())
                {
                    if(serversLoad.containsKey(entry.getKey()))
                        serversLoad.get(entry.getKey()).add(entry.getValue());
                    else
                        serversLoad.put(entry.getKey(), new MutableInt(entry.getValue()));
                    totalSatisfiedLoad+=entry.getValue();
                }
            }

            for(Entry<TranslationServerId,MutableInt> entry:serversLoad.entrySet()){
                if(!lastTimeWithLoad.containsKey(entry.getKey()))
                    lastTimeWithLoad.put(entry.getKey(), now);
                else if(entry.getValue().intValue()>0)
                    lastTimeWithLoad.put(entry.getKey(), now);
            }

            if(totalLoadDemand>totalSatisfiedLoad)
            {
                startServer();
            }
            else
            {
                //Stop server?
                for(Entry<TranslationServerId,MutableInt> entry:serversLoad.entrySet()){
                    if(entry.getValue().intValue()==0 && runningServers.contains(entry.getKey().getHost()))
                    {
                        if(serversLoad.size()-serversToStop.size()>1 && lastTimeWithLoad.containsKey(entry.getKey()) && now-lastTimeWithLoad.get(entry.getKey()) >= maxInactivityTime)
                        {
                            try
                            {
                            logger.info("Server "+entry.getKey().getHost()+" will be stopped");

                            serversToStop.add(entry.getKey());
                            //onDemandServerInterface.stopServer(entry.getKey().getHost());
                            }
                            catch(Exception e)
                            {
                                logger.error("Exception stopping server "+entry.getKey().getHost(), e);
                            }
                        }
                    }
                }
            }
            return serversToStop;
        }
    }

    public void startServer() {
        synchronized(this)
        {
            //Start new server?
            if (onDemandServerInterface.getAvailableServers() > 0) {
                if (waitingStartingServers.isEmpty()) {
                    try {
                        logger.info("Starting new server");
                        String newServerHost = onDemandServerInterface.startServer();
                        waitingStartingServers.add(new WaitingStartingServer(newServerHost, System.currentTimeMillis()));
                    } catch (Exception e) {
                        logger.error("Exception starting server", e);
                    }
                }
            }
        }
    }


    /**
     * Tells the dynamic server management system that a new server has been started successfully.
     * This method must be called each time a new server contacts request router.
     *
     * @param host Host of the new server.
     */
    public void notifyServerStartup(String host)
    {
        synchronized(this)
        {
            Iterator<WaitingStartingServer> it = waitingStartingServers.iterator();
            while(it.hasNext()){
                WaitingStartingServer wsv = it.next();
                if(wsv.getHost().equals(host))
                {
                    it.remove();
                    runningServers.add(host);
                    break;
                }
            }

            if(timeoutStartingServers.remove(host))
                runningServers.add(host);
        }
    }

    /**
     * Tells the dynamic server management system that a server has been stopped.
     * This methdo must be called each time that a server status can't be checked.
     *
     * @param host Host of the server that can't be contacted
     */
    public void notifyServerStop(String host)
    {
        synchronized(this)
        {
            Iterator<WaitingStartingServer> it = waitingStartingServers.iterator();
            while(it.hasNext()){
                WaitingStartingServer wsv = it.next();
                if(wsv.getHost().equals(host))
                {
                    it.remove();
                    break;
                }
            }
            if(runningServers.remove(host))
            {
                try
                {
                onDemandServerInterface.stopServer(host);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


}
