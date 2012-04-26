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


import org.scalemt.rmi.exceptions.TranslationEngineException;
import org.scalemt.rmi.transferobjects.DaemonConfiguration;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.TranslationServerId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.map.ValueSortedHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scalemt.rmi.exceptions.RouterTimeoutException;
import org.scalemt.rmi.transferobjects.AdditionalTranslationOptions;
import org.scalemt.rmi.transferobjects.Content;

/**
 * Queue to send translation requests of a single language pair to
 * the servers that support it.
 * <br/>
 * Translation requests are put in a priority queue. Priority of a request depends on the time
 * it arrived to the queue and the user type. Requests from registered users have more priority.
 * Priority is the time the object is created, plus and increment if the user
 * is not registered. The lower priority, the sooner the request is processed.This increment is
 * read from the property <code>scheduler_not_registered_priority_increment</code> from <code>configuration.properties</code>.
 *<br/>
 * To decide the server that will receive a translation request, we keep track of the number of characters
 * sent to each server and not returned, and send the translation request to the server with less characters.
 * <br/>
 * But the a request is only sent to a server if its number of characters is lower than
 * a certain threshold, or the number of requests sent to that server and not returned is
 * lower than another treshold. The first treshold is read from the configuration property
 * <code>scheduler_maxcharacters_in_daemon_queue</code> and the second from <code>scheduler_maxelements_in_daemon_queue</code>.
 *
 *
 * @author vitaka
 */
class QueueScheduler {
    
    static Log logger = LogFactory.getLog(QueueScheduler.class);

    /**
     * If processing a request takes a server more than this time, an error is returned.
     */
    private long timeout;

    /**
      * References to translation servers that allow communication with them
      */
    private final ITranslationServerReferences serverReferences;

    /**
     * Information about all the servers, including the daemons running on them, i.e., the language
     * pairs they can translate.
     */
    private final Map<TranslationServerId, TranslationServerInformation> serversInformation;

    /**
     * How load for the language pair represented by this queue is distributed among the servers
     */
    private Map<TranslationServerId, Integer> loadDistribution;

    /**
     * Servers that can translate requests made with the language pair represented by this queue
     */
    private Set<TranslationServerId> supportedServers;

    /**
     * Priority queue with all the translation requests
     */
    private final BlockingQueue<QueueElement> queue;

    /**
     * Language pair of all the requests waiting in this queue
     */
    //private LanguagePair pair;
    private final DaemonConfiguration daemonConfiguration;

    /**
     * Map containing each supported server and the number of characters sent to it and not returned
     */
    private final ValueSortedHashMap charactersInServerQueue;

    /**
     * Map containing each supported server and the number of requests sent to it and not returned
     */
    private final Map<TranslationServerId, Integer> elementsInServerQueue;

    /**
     * Property <code>scheduler_maxcharacters_in_daemon_queue</code>
     */
    private int maxQueuedCharactersInServer;
    
    /**
     * Property <code>scheduler_maxelements_in_daemon_queue</code>
     */
    private int maxQueuedElementsInServer;

    /**
     * Count of the characters and requests waiting in the queue.
     */
    private RequestsHistoryTO requestsPrediction;

    /**
     * Increment of the priority of non-registered users
     */
    private long notRegisteredPriorityIncrement;

    
    private LoadPredictor loadPredictor;

    private Scheduler scheduler;
    
    /**
     * Sends a translation request to a server
     */
    private class TranslationSender implements Runnable {

        /**
         * Element to send
         */
        private QueueElement queueElement;

        /**
         * Server ID
         */
        private TranslationServerId translationServerId;

        /**
         * Constructor receiving the translation request and the server where the request will be sent.
         *
         * @param queueElement Translation request
         * @param translationServerId Server id
         */
        public TranslationSender(QueueElement queueElement, TranslationServerId translationServerId) {
            this.queueElement = queueElement;
            this.translationServerId = translationServerId;
        }

        /**
         * Sends the request to the server.<br/>
         * When the response from the server is received, the thread that put the request in the queue is interrupted.
         */
        @Override
        public void run() {
            Content translation = null;
            try {
                    translation = serverReferences.getTranslationEngine(translationServerId).translate(queueElement.getSource(),daemonConfiguration.getLanguagePair(),queueElement.getAdditionalTranslationOptions());
                }
            catch(TranslationEngineException te)
            {
                queueElement.setException(te);
            }
             catch (Exception e) {
                queueElement.setException(new TranslationEngineException(e));
            }
            requestsPrediction.removeFromNumberOfChars(queueElement.getFormat(), queueElement.getSource().getLength());

            synchronized (charactersInServerQueue) {
                Object objOldCharacterAmount = charactersInServerQueue.get(translationServerId);
                if(objOldCharacterAmount!=null)
                {
                    int oldCharacterAmount = (Integer)  objOldCharacterAmount;
                    int oldElements = elementsInServerQueue.get(translationServerId);
                    //charactersInServerQueue.put(translationServerId, oldCharacterAmount - queueElement.getSourceText().length() - constatRequestCPUcost);
                    charactersInServerQueue.put(translationServerId, oldCharacterAmount - queueElement.getSource().getLength() - loadPredictor.getConstantCpuCostPerRequest(daemonConfiguration, queueElement.getSource().getFormat()));
                    elementsInServerQueue.put(translationServerId, oldElements - 1);
                }
            }
            logger.trace("Queue Scheduler "+daemonConfiguration.toString()+": request result received. unpausing");
            unpause();
            
            queueElement.setTranslation(translation);
            queueElement.getCaller().interrupt();
        }
    }

    /**
     * Takes translation requests from the queue, and chooses the server where they
     * should be sent. Then, invokes a {@link TranslationSender} to send them.
     *
     */
    private class Scheduler extends Thread {

        public boolean pleaseWait=false;
        
        @Override
        public void run() {
            boolean running=true;
            while (running) {
                
               
                
                
                try {
                    QueueElement queueElement = queue.take();
                    if(queueElement==stopMark)
                        running=false;
                    else
                    {
                        logger.trace("Queue Scheduler "+daemonConfiguration.toString()+": processing request.");
                        //Keep list of available servers ordered by load (or characters in queue)
                        //send request to the less loaded server
                        
                        
                        boolean sent = false;
                        while (!sent) {
                            
                             synchronized(this)
                            {
                                while (pleaseWait) {
                                    try {
                                        wait();
                                    } catch (Exception e) {
                                    }
                                }
                            }
                            
                            boolean foundServer = false;

                            synchronized (charactersInServerQueue) {
                                OrderedMapIterator iterator = charactersInServerQueue.orderedMapIterator();
                                if (iterator.hasNext()) {
                                    TranslationServerId translationServerId = (TranslationServerId) iterator.next();
                                    Integer charactersQueued = (Integer) charactersInServerQueue.get(translationServerId);
                                    int elementsQueued = elementsInServerQueue.get(translationServerId);
                                    if (supportedServers.contains(translationServerId)) {
                                        foundServer = true;
                                        //if (charactersQueued.intValue() < maxQueuedCharactersInServer)
                                        if (charactersQueued.intValue() < maxQueuedCharactersInServer || elementsQueued < maxQueuedElementsInServer)
                                        {
                                            //Send translation to server
                                            sent = true;
                                            logger.trace("Queue Scheduler "+daemonConfiguration.toString()+": Sending request to slave.");

                                            //charactersInServerQueue.put(translationServerId, charactersQueued.intValue() + queueElement.getSourceText().length() + constatRequestCPUcost);
                                            charactersInServerQueue.put(translationServerId, charactersQueued.intValue() + queueElement.getSource().getLength()+loadPredictor.getConstantCpuCostPerRequest(daemonConfiguration,queueElement.getSource().getFormat() ));
                                            elementsInServerQueue.put(translationServerId, elementsQueued + 1);
                                            TranslationSender translationSender = new TranslationSender(queueElement, translationServerId);
                                            Thread t = new Thread(translationSender);
                                            t.start();
                                        }

                                    }
                                }
                            }
                            /*
                            if (!foundServer) {
                                sent = true;
                                queueElement.setException(new NoEngineForThatPairException());
                                queueElement.getCaller().interrupt();
                            }*/
                            if (!sent) {
                                //Wait
                                logger.trace("Queue Scheduler "+daemonConfiguration.toString()+": no available server. waiting");
                                //Thread.sleep(100);
                                this.pleaseWait=true;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    //TODO: Something
                }

            }
        }
    }

    /**
     * <code>QueueElement</code> that stops {@link Scheduler}
     * when is found in the queue.
     */
    private QueueElement stopMark;

    /**
     * Constructor.
     *
     * @param pair Language pair of all the request sent to this queue.
     * @param references References to translation servers that allow communication with them.
     * @param serversInfo Information about all the servers, including the daemons running on them, i.e., the language pairs they can translate.
     * @param maxQueuedCharactersServer Property <code>scheduler_maxcharacters_in_daemon_queue</code>
     * @param maxQueuedElementsServer Property <code>scheduler_maxelements_in_daemon_queue</code>
     */
    public QueueScheduler(DaemonConfiguration dc, ITranslationServerReferences references, Map<TranslationServerId, TranslationServerInformation> serversInfo, int maxQueuedCharactersServer,int maxQueuedElementsServer,LoadPredictor predictor) {

        this.serverReferences = references;
        this.serversInformation = serversInfo;
        //this.pair = pair;
        this.daemonConfiguration = dc;
        this.maxQueuedCharactersInServer = maxQueuedCharactersServer;
        this.maxQueuedElementsInServer = maxQueuedElementsServer;
        this.loadPredictor=predictor;
        charactersInServerQueue = new ValueSortedHashMap(true);
        elementsInServerQueue = new HashMap<TranslationServerId, Integer>();
        loadDistribution = new HashMap<TranslationServerId, Integer>();
        requestsPrediction=new RequestsHistoryTO();
        supportedServers=new HashSet<TranslationServerId>();
        stopMark=new QueueElement(null,  UserType.anonymous, Thread.currentThread(),notRegisteredPriorityIncrement,null);
        //this.queue = new LinkedBlockingQueue<QueueElement>();
        this.queue=new PriorityBlockingQueue<QueueElement>();

        this.timeout=10*60*1000;
        try
        {
            timeout=Long.parseLong(Util.readConfigurationProperty("scheduler_timeout"));
        }
        catch(Exception e)
        {}

        this.notRegisteredPriorityIncrement=1000;
        try
        {
            notRegisteredPriorityIncrement=Long.parseLong(Util.readConfigurationProperty("scheduler_not_registered_priority_increment"));
        }
        catch(Exception e)
        {}

        scheduler = new Scheduler();
        scheduler.start();
        
        
    }

    /**
     * Stops the scheduler. No more elements will be taken from the queue and sent to a server.
     */
    public void stop()
    {
        try
        {
            queue.put(stopMark);
        }
        catch(Exception e)
        {}

    }
    
    public void pause()
    {
        synchronized(scheduler)
        {
            scheduler.pleaseWait=true;
        }
    }
    
    
    public void unpause()
    {
         synchronized(scheduler)
        {
            scheduler.pleaseWait=false;
            scheduler.notify();
        }
    }

    /**
     * Translates a piece of text (or HTML, or another supported format)
     * @param source Source to be translated
     * @param format Format of the source: text, HTML, etc.
     * @param userType User type: registered or not
     * @return Translated source
     * @throws com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException If there is an error translating the source, or sending it to the server.
     */
    public Content translate(Content source,  UserType userType, AdditionalTranslationOptions to) throws TranslationEngineException {
        QueueElement queueElement = new QueueElement(source,  userType, Thread.currentThread(),notRegisteredPriorityIncrement, to);
        requestsPrediction.addToNumberOfChars(source.getFormat(), source.getLength());
        queue.add(queueElement);

        try {
            Thread.sleep(timeout);
            queue.remove(queueElement);
            throw new RouterTimeoutException();
        } catch (InterruptedException e) {
            if (queueElement.getException() != null) {
                throw queueElement.getException();
            }
            return queueElement.getTranslation();
        }
    }

    /**
     * Gets the load distribution between servers assigned to this pair.
     * @return Map associating each server with the amount of load assigned.
     */
    public Map<TranslationServerId, Integer> getLoadDistribution() {
        return loadDistribution;
    }

    /**
     * Sets the load distribution between servers assigned to the language pair represented by this queue,
     * and the servers that can translate with this pair. Servers with no load assigned but with a running daemon can exist.
     * @param supServers
     * @param loadDistribution Map associating each server with the amount of load assigned.
     */
    public void setLoadDistribution(Set<TranslationServerId> supServers, Map<TranslationServerId, Integer> loadDistribution) {
        this.loadDistribution = loadDistribution;
        this.supportedServers = supServers;
        updateAvailableServers();

    }
    
    public void finishSettingLoadDistribution()
    {
        updateAvailableServersStep2();
    }

    /**
     * Tells the Scheduler not to send translation request to the given server.
     *
     * @param server ID of the server to be removed
     */
    public void removeAvailableServer(TranslationServerId server)
    {
         synchronized (charactersInServerQueue) {
            charactersInServerQueue.remove(server);
         }
    }

    /**
     * Updates the servers that can receive translation request from the field <code>serversInformation</code>
     */
    public void updateAvailableServers() {
        //Remove from charactersInServerQueue servers that can't translate this pair
        Set<TranslationServerId> availableServers = new HashSet<TranslationServerId>(this.supportedServers);
        availableServers.retainAll(serversInformation.keySet());
        synchronized (charactersInServerQueue) {
            List<Object> serversToRemove = new ArrayList();
            for (Object serverIdObj : charactersInServerQueue.keySet()) {
                if (!availableServers.contains(serverIdObj)) {
                    serversToRemove.add(serverIdObj);
                }
            }
            for (Object serverToRemove : serversToRemove) {
                charactersInServerQueue.remove(serverToRemove);
                elementsInServerQueue.remove(serverToRemove);
            }

            
        }
    }
    
    public void updateAvailableServersStep2() {
        Set<TranslationServerId> availableServers = new HashSet<TranslationServerId>(this.supportedServers);
        int numAddedServers=0;
        synchronized (charactersInServerQueue) {
            //Add to charactersInServerQueue new servers
            for (Object server : availableServers) {
                if (!charactersInServerQueue.containsKey(server)) {
                    charactersInServerQueue.put(server, new Integer(0));
                    numAddedServers++;
                }
                if (!elementsInServerQueue.containsKey(server)) {
                    elementsInServerQueue.put((TranslationServerId) server, new Integer(0));
                }
            }
        }
        if(numAddedServers>0)
        {
            logger.trace("Queue Scheduler "+daemonConfiguration.toString()+": Adding new servers. unpausing");
            unpause();
        }
    }

    /**
     * Gets information about the number of requests and the amount of characters in the queue:
     * @return Information about the number of requests and the amount of characters in the queue
     */
    public RequestsHistoryTO getRequestsSummary() {
        return requestsPrediction.clone();
    }
}
