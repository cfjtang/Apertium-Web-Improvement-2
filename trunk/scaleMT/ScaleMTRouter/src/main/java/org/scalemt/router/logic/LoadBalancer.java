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

import java.io.IOException;
import java.net.URL;
import org.scalemt.rmi.transferobjects.AdditionalTranslationOptions;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scalemt.rmi.exceptions.TranslationEngineException;
import org.scalemt.rmi.slave.ITranslationEngine;
import org.scalemt.rmi.transferobjects.DaemonConfiguration;
import org.scalemt.rmi.transferobjects.DaemonInformation;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.rmi.transferobjects.ServerInformationTO;
import org.scalemt.rmi.transferobjects.ServerStatusTO;
import org.scalemt.rmi.transferobjects.TranslationServerId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scalemt.rmi.transferobjects.Content;
import org.scalemt.rmi.transferobjects.TextContent;
import org.scalemt.router.ws.LoggerStatiticsWriter;

/**
 * Main request router class. Coordinates all the other classes.
 * Controls when placement controller should be executed,
 * receives servers status and does the required actions when
 * a new server appears or a server stops.
 * <br/>
 *
 * Reads many properties from <code>configuration.properties</code>:
 * <ul>
 * <li><code>admissioncontrol_interval</code> Period, in milliseconds, of Admission control updating</li>
 * <li><code>placement_controller_execution_period</code> Period, in milliseconds, of Placement controller execution</li>
 * <li><code>server_status_updater_execution_period</code> Period, in milliseconds, of server status checking</li>
 * </ul>
 *
 *
 * @author vitaka
 */
public class LoadBalancer {

    /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(LoadBalancer.class);

    /**
     * Singleton instance
     */
    private static LoadBalancer instance = null;

    /**
     * Gets the singleton instance
     * @return
     */
    public static LoadBalancer getInstance() {
        if (instance == null) {
            instance = new LoadBalancer();
        }
        return instance;
    }

    /**
     * Updates {@link AdmissionControl} with information about the load of the
     * different servers.
     */
    private class AdmissionControlUpdater extends TimerTask {

        @Override
        public void run() {
            synchronized(nonSingletonInstance)
            {
            double load = 0;
            int numServers = 0;
            for (TranslationServerInformation serverInfo : serversInformation.values()) {
                numServers++;
                if (serverInfo.getServerStatus() != null) {
                    load += serverInfo.getServerStatus().getLoad();
                }
            }
            
            if(numServers > 0)
                load /= numServers;

            AdmissionControl.getInstance().update(load);
            }
        }
    }

    /**
     * Executes placement controller periodically, and performs
     * the changes in servers requested by placement controller result.
     */
    private class PlacementControllerUpdater extends TimerTask {

        
        private long updaterExecutionPeriod;    
        private long lastExecution;

        public PlacementControllerUpdater(long updaterExecutionPeriod) {
            this.updaterExecutionPeriod = updaterExecutionPeriod;
            this.lastExecution = 0;
            
        }

        @Override
        public void run() {
            long period = updaterExecutionPeriod;
            long now = System.currentTimeMillis();
            if (lastExecution > 0) {
                period = now - lastExecution;
            }
            lastExecution = now;
            try {
                cdlSyncPlacementStatus.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(LoadBalancer.class.getName()).log(Level.SEVERE, null, ex);
            }

            synchronized (nonSingletonInstance) {
                try {

                    if (serversInformation.isEmpty()) {
                        dynamicServerManagement.startServer();
                    } else {

                        for (DaemonConfigurationInformation lpinfo : daemonsConfigInfo.values()) {
                            lpinfo.setCpuDemand(0);
                        }
                        Map<DaemonConfiguration, Integer> loadPrediction = loadPredictor.predictLoad(period,queues);
                        for(Entry<DaemonConfiguration, Integer> entry: loadPrediction.entrySet())
                            daemonsConfigInfo.get(entry.getKey()).setCpuDemand(entry.getValue());

                        Map<DaemonConfiguration, Map<TranslationServerId, Integer>> loadDistributionMatrix;
                        Map<DaemonConfiguration, Set<TranslationServerId>> supportedServersMatrix;
                        try {
                            loadDistributionMatrix = placementControllerAdapter.computeNewPlacement(daemonsConfigInfo, serversInformation);
                            supportedServersMatrix = placementControllerAdapter.getI();
                            List<TranslationServerId> serversToStop = dynamicServerManagement.processPlacementControllerResult(daemonsConfigInfo, loadDistributionMatrix);
                            secureStoppingServers.addAll(serversToStop);

                            for (TranslationServerId server : serversToStop) {
                                removeTranslationEngine(server);
                                translationEngines.getTranslationEngine(server).stop();
                            }

                        } catch (Exception e) {
                            throw e;
                        } finally {
                        }
                        
                        /*
                        logger.trace("Pausing queues start");
                        for (Entry<DaemonConfiguration, QueueScheduler> entry : queues.entrySet()) {
                            entry.getValue().pause();
                        }
                        logger.trace("Pausing queues finished");
                        
                         */
                        
                        logger.trace("queues update 1 start");
                        for (Entry<DaemonConfiguration, QueueScheduler> entry : queues.entrySet()) {
                            if (loadDistributionMatrix.containsKey(entry.getKey())) {
                                entry.getValue().setLoadDistribution(supportedServersMatrix.get(entry.getKey()), loadDistributionMatrix.get(entry.getKey()));
                            }
                        }
                        logger.trace("queues update 1 finished");
                        
                        logger.trace("Placement execution start");
                        placementControllerAdapter.executePlacement();
                        logger.trace("Placement execution finished");
                        
                        logger.trace("queues update 2 start");
                        for (Entry<DaemonConfiguration, QueueScheduler> entry : queues.entrySet()) {
                            if (loadDistributionMatrix.containsKey(entry.getKey())) {
                                entry.getValue().finishSettingLoadDistribution();
                            }
                        }
                        logger.trace("queues update 2 finished");
                        
                        /*
                        logger.trace("Unpausing queues start");
                        for (Entry<DaemonConfiguration, QueueScheduler> entry : queues.entrySet()) {
                            entry.getValue().unpause();
                        }
                        logger.trace("Unpausing queues fiched");
                         * 
                         */
                    }
                } catch (Exception e) {
                    logger.error("Exception Updating placement", e);
                }

                cdlSyncPlacementStatus=new CountDownLatch(1);
            }
        }
    }

    /**
     * Asks remote translation servers about their status.
     */
    private class ServersStatusUpdater extends TimerTask {

        @Override
        public void run() {
            synchronized (nonSingletonInstance) {

                try {
                    Set<TranslationServerId> serversToRemove = new HashSet<TranslationServerId>();
                    for (Entry<TranslationServerId, TranslationServerInformation> entry : serversInformation.entrySet()) {
                        try {
                            entry.getValue().setServerStatus(translationEngines.getTranslationEngine(entry.getKey()).getServerStatus());

                            //This piece of code is no longer necessary
                            /*
                            Set<DaemonConfiguration> notPresentConfigs = new HashSet<DaemonConfiguration>(supportedConfigurations);
                            List<DaemonInformation> daemonsInfo = entry.getValue().getServerStatus().getDaemonsInformation();
                            for (DaemonInformation daemonInformation : daemonsInfo) {
                                notPresentConfigs.remove(daemonInformation.getConfiguration());
                            }

                            //               supportedPairsLock.writeLock().lock();
                            for (DaemonConfiguration notPresentpair : notPresentConfigs) {
                                queues.get(notPresentpair).removeAvailableServer(entry.getKey());
                            }
                        //                supportedPairsLock.writeLock().unlock();
                             */
                        } catch (Exception e) {

                            serversToRemove.add(entry.getKey());

                            e.printStackTrace();
                        }
                    }
                    //Remove servers
                    for (TranslationServerId server : serversToRemove) {
                        removeTranslationEngine(server);
                        //¿?
                        dynamicServerManagement.notifyServerStop(server.getHost());
                    }

                    //logger.trace(secureStoppingServers.size() + " servers waiting for stopping");
                    Iterator<TranslationServerId> iterator = secureStoppingServers.iterator();
                    while (iterator.hasNext()) {
                        TranslationServerId server = iterator.next();
                        try {
                            translationEngines.getTranslationEngine(server).getServerStatus();
                            logger.trace("Got server status  OK from a stopped server");
                        } catch (Exception e) {
                            logger.trace("Exception getting server to stop status", e);
                            iterator.remove();
                            translationEngines.removeTranslationEngine(server);
                            dynamicServerManagement.notifyServerStop(server.getHost());
                        }
                    }


                } catch (Exception e) {
                    logger.error("Exception updating servers status", e);
                } finally {
                }
                
                
                cdlSyncPlacementStatus.countDown();
                
                new AdmissionControlUpdater().run();

            }
        }
    }

    /**
     * self-reference
     */
    private final LoadBalancer nonSingletonInstance;

    /**
     * References to translation servers, needed to communicate with them
     */
    private final ITranslationServerReferences translationEngines;

    /**
     * Translation servers
     */
    private final List<TranslationServerId> servers;

    /**
     * Map associating each server ID with its relevant information, like supported pairs, capacity, running daemons, etc,
     */
    private final Map<TranslationServerId, TranslationServerInformation> serversInformation;

    /**
     * Servers that have received a stop request via RMI but are still running
     */
    private final Set<TranslationServerId> secureStoppingServers;

    /**
     * Supported language pairs
     */
    private final List<LanguagePair> supportedPairs;

    private final Set<DaemonConfiguration> supportedConfigurations;

    /**
     * Queues where translation equests are sent. There is a queue for each supported daemon configuration.
     */
    private final Map<DaemonConfiguration, QueueScheduler> queues;

    /**
     * Information about the different language pairs: memory requirements, cpu demand, etc.
     */
    //private final Map<LanguagePair, LanguagePairInfo> languagePairsInfo;

    private final Map<DaemonConfiguration,DaemonConfigurationInformation> daemonsConfigInfo;

    /**
     * Interface to decide which pairs should run on which servers
     */
    private final PlacementControllerAdapter placementControllerAdapter;

    /**
     * Predicts the requests the system will receive in the future based on the requests it received in the past.
     */
    private final LoadPredictor loadPredictor;

    /**
     * Decides when to start and stop translation servers
     */
    private DynamicServerManagement dynamicServerManagement;

    /**
     * Timer to update AdmissionControl perioducally
     */
    private final Timer admissionControlUpdateTimer;

    /**
     * Timer to execute placemnt controller periodically
     */
    private final Timer placementControllerTimer;

    /**
     * TImer to check servers' status periodically
     */
    private final Timer serverStatusTimer;

    private CountDownLatch cdlSyncPlacementStatus;
    
    private  long maxSourceLength;

    /**
     * Creates an instance with no translation servers.
     */
    private LoadBalancer() {
        nonSingletonInstance=this;
        translationEngines = TranslationServerReferencesFactory.getInstance().getTranslationServerReferences();
        servers = new ArrayList<TranslationServerId>();
        serversInformation = new HashMap<TranslationServerId, TranslationServerInformation>();
        supportedPairs = new ArrayList<LanguagePair>();
        supportedConfigurations = new HashSet<DaemonConfiguration>();
        queues = new HashMap<DaemonConfiguration, QueueScheduler>();
        //languagePairsInfo = new HashMap<LanguagePair, LanguagePairInfo>();
        daemonsConfigInfo = new HashMap<DaemonConfiguration, DaemonConfigurationInformation>();
        placementControllerAdapter = new PlacementControllerAdapter(translationEngines);
        cdlSyncPlacementStatus=new CountDownLatch(0);

        loadPredictor = new LoadPredictor();
        this.dynamicServerManagement = new DynamicServerManagement();
        secureStoppingServers = new HashSet<TranslationServerId>();

        maxSourceLength=1048576;
        try {
            maxSourceLength = Long.parseLong(Util.readConfigurationProperty("max_source_length"));
        } catch (Exception e) {
            logger.warn("Cannot read \"max_source_length\" property. Using default value.", e);
            
        }
        
        long admissionControlPeriod = 1000;
        try {
            admissionControlPeriod = Long.parseLong(Util.readConfigurationProperty("admissioncontrol_interval"));
        } catch (Exception e) {
            logger.warn("Cannot read \"admission control update period\" property", e);
        }
        admissionControlUpdateTimer = new Timer();
        // admissionControlUpdateTimer.scheduleAtFixedRate(new AdmissionControlUpdater(), admissionControlPeriod, admissionControlPeriod);

        long updaterExecutionPeriod = 60000;
        try {
            updaterExecutionPeriod = Long.parseLong(Util.readConfigurationProperty("placement_controller_execution_period"));
        } catch (Exception e) {
            logger.warn("Cannot read \"placement controller execution period\" property", e);
        }
        placementControllerTimer = new Timer();        
        placementControllerTimer.schedule(new PlacementControllerUpdater(updaterExecutionPeriod), 1000, updaterExecutionPeriod);

        long serverStatutsUpdatePeriod = 1000;
        try {
            serverStatutsUpdatePeriod = Long.parseLong(Util.readConfigurationProperty("server_status_updater_execution_period"));
        } catch (Exception e) {
            logger.warn("Cannot read \"admission control update period\" property", e);
        }
        serverStatusTimer = new Timer();
        serverStatusTimer.schedule(new ServersStatusUpdater(),1000, serverStatutsUpdatePeriod);
    }

    /**
     * Adds a translation engine server to the list of translation engines
     *
     * @param serverid Server identification
     */
    void addTranslationEngine(TranslationServerId serverid) {
        synchronized (instance) {
            logger.info("Adding new translation engine: " + serverid.getHost() + ":" + serverid.getPort() + "/" + serverid.getObjectName());

            //Get remote object reference
            if (translationEngines.addTranslationEngine(serverid)) {

                try {
                    //Ask new translation engine for static information
                    ITranslationEngine newTranslationEngine = translationEngines.getTranslationEngine(serverid);
                    ServerInformationTO serverInformationTO = newTranslationEngine.getServerInformation();
                    logger.info("Received translation engine information from " + serverid.getHost() + ":" + serverid.getPort() + "/" + serverid.getObjectName() + ". Supported pairs: " + serverInformationTO.getSupportedPairs() + " Cpu capacity: " + serverInformationTO.getCpuCapacity());

                    //Store new server information
                    TranslationServerInformation information = new TranslationServerInformation();
                    information.setServerInformation(serverInformationTO);
                    ServerStatusTO initialStatus = new ServerStatusTO();
                    initialStatus.setDaemonsInformation(new ArrayList<DaemonInformation>());
                    information.setServerStatus(initialStatus);
                    //         readWriteServersInfoLock.writeLock().lock();
                    servers.add(serverid);
                    if (serversInformation.containsKey(serverid)) {
                        removeTranslationEngine(serverid);
                    }
                    serversInformation.put(serverid, information);
                    //         readWriteServersInfoLock.writeLock().unlock();
                    dynamicServerManagement.notifyServerStartup(serverid.getHost());
                    //Calculate system supported pairs
                    mergeSupportedPairs();

                } catch (Exception e) {
                    logger.warn("Cannot retrieve information from new translation server", e);
                    translationEngines.removeTranslationEngine(serverid);
                }

            }
        }
    }

    /**
     * Removes a translation engine server from the list of translation engines.
     * List of supported pairs can change if there was a pair only supported by that server
     *
     * @param serverid Server to remove identification
     */
    void removeTranslationEngine(TranslationServerId serverid) {
        logger.info("Removing " + serverid);
        synchronized (instance) {
            //        readWriteServersInfoLock.writeLock().lock();
            servers.remove(serverid);
            serversInformation.remove(serverid);
            //        readWriteServersInfoLock.writeLock().unlock();
            //        supportedPairsLock.writeLock().lock();
            for (QueueScheduler queue : queues.values()) {
                queue.updateAvailableServers();
            }
            //        supportedPairsLock.writeLock().unlock();
            mergeSupportedPairs();
        }
    }

    /**
     * Translates 
     *
     * @param source Source text/html
     * @param pair Language pair: source language and target language
     * @param format Format of the source
     * @return Translated source
     * @throws com.gsoc.apertium.translationengines.router.logic.TooMuchLoadException If the system is overloaded and can't perform the translation
     * @throws com.gsoc.apertium.translationengines.router.logic.NoEngineForThatPairException If the pair is not supported
     * @throws com.gsoc.apertium.translationengines.exceptions.TranslationEngineException If some other error happens
     */
    public Content translate(Content source, LanguagePair pair, String ip, String referer,String apiKey, AdditionalTranslationOptions to) throws TooMuchLoadException, NoEngineForThatPairException, TranslationEngineException, TooManyUserRequestsException, TooLongSourceException {

        DaemonConfiguration dc =getDaemonConfigurationToTranslate(pair, source.getFormat());
        if (dc == null) {
            throw new NoEngineForThatPairException();
        }
        QueueScheduler queue = queues.get(dc);
                
        Requester requester=new AnonymousRequester(ip);
        UserType type = UserType.anonymous;
        //Test if user is registered
        logger.trace("Checking key:"+apiKey);

        //TODO: ¿Check referer?
        
        String registeredUser = UserManagement.getInstance().checkKey(apiKey);
        if(registeredUser!=null)
        {
            type=UserType.registered;
            requester=new RegisteredRequester(registeredUser);
        }
        else
            registeredUser="anonymous";

        //Log request
        loadPredictor.getRequestHistory().addRequest(pair, source.getLength(), source.getFormat(),requester);
        //Thread.currentThread().getId();
        //logger.info("Request received");
        
        //Check source length
        if(source.getLength() <= this.maxSourceLength)
        {
        
        //Check user limit
        //if (AdmissionControl.getInstance().canAcceptRequest()) {
        if ( AdmissionControl.getInstance().canAcceptRequest()) {
            if(UserAdmissionControl.getInstance().canTranslate(requester))
            {
            try
            {
                int cost=loadPredictor.getLoadConverter().convertRequest(source.getLength(), dc, source.getFormat());
                 LoggerStatiticsWriter.getInstance().logRequestProcessing(registeredUser, source.getLength(), cost);
                logger.debug("requestprocessing "+registeredUser+" "+source.getLength()+" "+cost);
                return queue.translate(source, type, to);
            }
            catch(TranslationEngineException e)
            {
                //hasException=true;
                //logger.info("Request caused exception", e);
                throw e;
            }
            finally
            {
                //if(!hasException)
                    //logger.info("Request processed OK");
            }
            }
            else
            {
                throw new TooManyUserRequestsException();
            }

        } else {
            throw new TooMuchLoadException();
        }
        
        }
        else
            throw new TooLongSourceException();

    }

    /**
     * Gets a the list of supported language pairs
     *
     * @return List of supported language pairs
     */
    public List<LanguagePair> getSupportedPairs() {
        try {
            return supportedPairs;
        } finally {
        }
    }
    
    public LanguagePair convertPairSupported(LanguagePair pair)
    {
       List<LanguagePair> supPairs=getSupportedPairs();
       if (supPairs.contains(pair))
           return pair;
       else
       {
           String sl = pair.getSource();
           String tl = pair.getTarget();
           String tl_without_dialect,sl_without_dialect;
           if (tl.contains("_"))
               tl_without_dialect=tl.split("_")[0];
           else
               tl_without_dialect=tl;
           
           if (sl.contains("_"))
               sl_without_dialect=sl.split("_")[0];
           else
               sl_without_dialect=sl;
           
           LanguagePair testPair=null;
           
           testPair=new LanguagePair(sl_without_dialect, tl);
           if (supPairs.contains(testPair))
               return testPair;
           else
           {
               testPair=new LanguagePair(sl, tl_without_dialect);
               if (supPairs.contains(testPair))
                    return testPair;
               else
               {
                   testPair=new LanguagePair(sl_without_dialect, tl_without_dialect);
                   if (supPairs.contains(testPair))
                        return testPair;
                   else
                       return null;
               }
           }
           
       }
    }

    /**
     * Creates the list of supported language pairs by merging the pairs supported by
     * the different servers. It also adds or removes queues when supported pairs change.
     *
     */
    private void mergeSupportedPairs() {
        Set<LanguagePair> pairs = new HashSet<LanguagePair>();
        Set<DaemonConfiguration> confs = new HashSet<DaemonConfiguration>();

        for (Entry<TranslationServerId, TranslationServerInformation> entry : serversInformation.entrySet()) {
            pairs.addAll(entry.getValue().getServerInformation().getSupportedPairs());
            confs.addAll(entry.getValue().getServerInformation().getSupportedConfigs());
        }

        Set<LanguagePair> removedPairs = new HashSet<LanguagePair>(supportedPairs);
        Set<DaemonConfiguration> removedConfigs = new HashSet<DaemonConfiguration>(supportedConfigurations);
        removedPairs.removeAll(pairs);
        removedConfigs.removeAll(confs);
        Set<LanguagePair> newPairs = new HashSet<LanguagePair>(pairs);
        Set<DaemonConfiguration> newConfs = new HashSet<DaemonConfiguration>(confs);
        newPairs.removeAll(supportedPairs);
        newConfs.removeAll(supportedConfigurations);
        supportedPairs.clear();
        supportedPairs.addAll(pairs);
        supportedConfigurations.clear();
        supportedConfigurations.addAll(confs);
        for (DaemonConfiguration rc: removedConfigs) {
            //languagePairsInfo.remove(pair);
            daemonsConfigInfo.remove(rc);
            queues.get(rc).stop();
            queues.remove(rc);
        }
        for (DaemonConfiguration dc : newConfs) {
            //LoadConverter loadConverter = new LoadConverter(dc, Format.txt);
            //int constantConvertedLoad =loadConverter.convert(loadPredictor.getConstantCpuCostPerRequest(), new LanguagePair("es", "ca"), Format.txt);
            //int constantCostPerRequest=loadPredictor.getConstantCpuCostPerRequest();
            //int rate = loadPredictor.getLoadConverter().convert(10000, dc);
            
            queues.put(dc, new QueueScheduler(dc, translationEngines, serversInformation, Integer.parseInt(Util.readConfigurationProperty("scheduler_maxcharacters_in_daemon_queue")), Integer.parseInt(Util.readConfigurationProperty("scheduler_maxelements_in_daemon_queue")),loadPredictor));
            DaemonConfigurationInformation newInfo = new DaemonConfigurationInformation(dc);
            daemonsConfigInfo.put(dc, newInfo);
        }

        if(removedConfigs.size()>0 || newConfs.size()>0)
        {
           URL url=LoadBalancer.class.getResource("/updateJSLibrary.sh");
           String[] command = new String[2];
            command[0]="bash";
            //command[1]="-c";
            command[1] = url.getPath();

            logger.info("executing: "+url.getPath());
            try {
                Process p = Runtime.getRuntime().exec(command);

            } catch (IOException ex) {
              logger.error("Exception updating javascript library.",ex);
            }
            
           
        }

        logger.info("Suported pairs are: " + supportedPairs);
    }

    /**
     * Tells all the daemons of a language pair to unload
     * the dictioanries to make new versions of them available.
     *
     * WARNING: When a server have more than one daemon of the same pair,
     * only a dictionary from a daemon is unloaded.
     *
     * @param pair
     */
    public void reloadDictionaries(LanguagePair p)
    {
        for(TranslationServerId server:servers)
        {
            TranslationServerInformation info =serversInformation.get(server);
            List<DaemonInformation> daemonsInfo =info.getServerStatus().getDaemonsInformation();
            boolean hasDaemon=false;
            for(DaemonInformation daemonInfo: daemonsInfo)
            {
                if(daemonInfo.getConfiguration().getLanguagePair().equals(p))
                {
                    hasDaemon=true;
                    break;
                }
            }
            if(hasDaemon)
                try
                {
                    translationEngines.getTranslationEngine(server).translate(new TextContent(Format.txt,"a"), p,null);
                }
                catch(Exception e)
                {
                    logger.warn("Exception sending empty translation to server", e);
                }
        }
    }

    DaemonConfiguration getDaemonConfigurationToTranslate(LanguagePair p, Format format)
    {
         DaemonConfiguration chosenc=null;
             for(DaemonConfiguration c: supportedConfigurations)
             {
                 if(c.getLanguagePair().equals(p) && c.getFormats().contains(format))
                     chosenc=c;
             }
             return chosenc;
    }

    LoadPredictor getLoadPredictor() {
        return loadPredictor;
    }

    
}

