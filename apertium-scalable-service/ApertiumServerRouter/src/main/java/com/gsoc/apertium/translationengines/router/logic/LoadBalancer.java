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

import com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException;
import com.gsoc.apertium.translationengines.rmi.slave.ITranslationEngine;
import com.gsoc.apertium.translationengines.rmi.transferobjects.DaemonInformation;
import com.gsoc.apertium.translationengines.rmi.transferobjects.Format;
import com.gsoc.apertium.translationengines.rmi.transferobjects.LanguagePair;
import com.gsoc.apertium.translationengines.rmi.transferobjects.ServerInformationTO;
import com.gsoc.apertium.translationengines.rmi.transferobjects.ServerStatusTO;
import com.gsoc.apertium.translationengines.rmi.transferobjects.TranslationServerId;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

            synchronized (nonSingletonInstance) {
                try {

                    if (serversInformation.isEmpty()) {
                        dynamicServerManagement.startServer();
                    } else {

                        for (LanguagePairInfo lpinfo : languagePairsInfo.values()) {
                            lpinfo.setCpuDemand(0);
                        }
                        Map<LanguagePair, Integer> loadPrediction = loadPredictor.predictLoad(period,queues);
                        for(Entry<LanguagePair, Integer> entry: loadPrediction.entrySet())
                            languagePairsInfo.get(entry.getKey()).setCpuDemand(entry.getValue());

                        Map<LanguagePair, Map<TranslationServerId, Integer>> loadDistributionMatrix;
                        Map<LanguagePair, Set<TranslationServerId>> supportedServersMatrix;
                        try {
                            loadDistributionMatrix = placementControllerAdapter.computeNewPlacement(languagePairsInfo, serversInformation);
                            supportedServersMatrix = placementControllerAdapter.getI();
                            List<TranslationServerId> serversToStop = dynamicServerManagement.processPlacementControllerResult(languagePairsInfo, loadDistributionMatrix);
                            secureStoppingServers.addAll(serversToStop);

                            for (TranslationServerId server : serversToStop) {
                                removeTranslationEngine(server);
                                translationEngines.getTranslationEngine(server).stop();
                            }

                        } catch (Exception e) {
                            throw e;
                        } finally {
                        }

                        for (Entry<LanguagePair, QueueScheduler> entry : queues.entrySet()) {
                            if (loadDistributionMatrix.containsKey(entry.getKey())) {
                                entry.getValue().setLoadDistribution(supportedServersMatrix.get(entry.getKey()), loadDistributionMatrix.get(entry.getKey()));
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Exception Updating placement", e);
                }
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

                            Set<LanguagePair> notPresentPairs = new HashSet<LanguagePair>(supportedPairs);
                            List<DaemonInformation> daemonsInfo = entry.getValue().getServerStatus().getDaemonsInformation();
                            for (DaemonInformation daemonInformation : daemonsInfo) {
                                notPresentPairs.remove(daemonInformation.getPair());
                            }

                            //               supportedPairsLock.writeLock().lock();
                            for (LanguagePair notPresentpair : notPresentPairs) {
                                queues.get(notPresentpair).removeAvailableServer(entry.getKey());
                            }
                        //                supportedPairsLock.writeLock().unlock();

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

    /**
     * Queues where translation equests are sent. There is a queue for each supported language pair.
     */
    private final Map<LanguagePair, QueueScheduler> queues;

    /**
     * Information abou the different language pairs: memory requirements, cpu demand, etc.
     */
    private final Map<LanguagePair, LanguagePairInfo> languagePairsInfo;

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

    /**
     * Creates an instance with no translation servers.
     */
    private LoadBalancer() {
        nonSingletonInstance=this;
        translationEngines = TranslationServerReferencesFactory.getInstance().getTranslationServerReferences();
        servers = new ArrayList<TranslationServerId>();
        serversInformation = new HashMap<TranslationServerId, TranslationServerInformation>();
        supportedPairs = new ArrayList<LanguagePair>();
        queues = new HashMap<LanguagePair, QueueScheduler>();
        languagePairsInfo = new HashMap<LanguagePair, LanguagePairInfo>();
        placementControllerAdapter = new PlacementControllerAdapter(translationEngines);
       
        loadPredictor = new LoadPredictor();
        this.dynamicServerManagement = new DynamicServerManagement();
        secureStoppingServers = new HashSet<TranslationServerId>();

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
        placementControllerTimer.scheduleAtFixedRate(new PlacementControllerUpdater(updaterExecutionPeriod), 1000, updaterExecutionPeriod);

        long serverStatutsUpdatePeriod = 1000;
        try {
            serverStatutsUpdatePeriod = Long.parseLong(Util.readConfigurationProperty("server_status_updater_execution_period"));
        } catch (Exception e) {
            logger.warn("Cannot read \"admission control update period\" property", e);
        }
        serverStatusTimer = new Timer();
        serverStatusTimer.scheduleAtFixedRate(new ServersStatusUpdater(),1000, serverStatutsUpdatePeriod);
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
    public String translate(String source, LanguagePair pair, Format format, UserType userType, List<Long> dictionaries) throws TooMuchLoadException, NoEngineForThatPairException, TranslationEngineException {

        QueueScheduler queue = queues.get(pair);
        if (queue == null) {
            throw new NoEngineForThatPairException();
        }

        //Log request
        loadPredictor.getRequestHistory().addRequest(pair, source.length(), format);
        //Thread.currentThread().getId();
        //logger.info("Request received");

        if (AdmissionControl.getInstance().canAcceptRequest()) {
            boolean hasException=false;
            try
            {
            return queue.translate(source, format, userType, dictionaries);

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

        } else {
            throw new TooMuchLoadException();
        }

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

    /**
     * Creates the list of supported language pairs by merging the pairs supported by
     * the different servers. It also adds or removes queues when supported pairs change.
     *
     */
    private void mergeSupportedPairs() {
        Set<LanguagePair> pairs = new HashSet<LanguagePair>();

        for (Entry<TranslationServerId, TranslationServerInformation> entry : serversInformation.entrySet()) {
            pairs.addAll(entry.getValue().getServerInformation().getSupportedPairs());
        }

        Set<LanguagePair> removedPairs = new HashSet<LanguagePair>(supportedPairs);
        removedPairs.removeAll(pairs);
        Set<LanguagePair> newPairs = new HashSet<LanguagePair>(pairs);
        newPairs.removeAll(supportedPairs);
        supportedPairs.clear();
        supportedPairs.addAll(pairs);
        for (LanguagePair pair : removedPairs) {
            languagePairsInfo.remove(pair);
            queues.get(pair).stop();
            queues.remove(pair);
        }
        for (LanguagePair pair : newPairs) {
             LoadConverter loadConverter = new LoadConverter(pair, Format.text);
            int constantConvertedLoad =loadConverter.convert(loadPredictor.getConstantCpuCostPerRequest(), new LanguagePair("es", "ca"), Format.text);
            queues.put(pair, new QueueScheduler(pair, translationEngines, serversInformation, Integer.parseInt(Util.readConfigurationProperty("scheduler_maxcharacters_in_daemon_queue")), Integer.parseInt(Util.readConfigurationProperty("scheduler_maxelements_in_daemon_queue")),constantConvertedLoad));
            LanguagePairInfo newInfo = new LanguagePairInfo(pair);
            languagePairsInfo.put(pair, newInfo);
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
    public void reloadDictionaries(LanguagePair pair)
    {
        for(TranslationServerId server:servers)
        {
            TranslationServerInformation info =serversInformation.get(server);
            List<DaemonInformation> daemonsInfo =info.getServerStatus().getDaemonsInformation();
            boolean hasDaemon=false;
            for(DaemonInformation daemonInfo: daemonsInfo)
            {
                if(daemonInfo.getPair().equals(pair))
                {
                    hasDaemon=true;
                    break;
                }
            }
            if(hasDaemon)
                try
                {
                    translationEngines.getTranslationEngine(server).translateText("a", pair,null);
                }
                catch(Exception e)
                {
                    logger.warn("Eception sending empty translation to server", e);
                }
        }
    }
}
