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
package org.scalemt.daemon;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.scalemt.main.QueueElement;
import org.scalemt.rmi.exceptions.DaemonDeadException;
import org.scalemt.rmi.exceptions.TranslationEngineException;
import org.scalemt.rmi.transferobjects.DaemonConfiguration;
import org.scalemt.rmi.transferobjects.DaemonInformation;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.util.ServerUtil;
import org.scalemt.util.StreamGobbler;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import org.apache.commons.lang.SerializationUtils;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ptql.ProcessFinder;
import org.scalemt.main.TranslationEnginePool;
import org.scalemt.rmi.exceptions.NonZeroExitValueException;
import org.scalemt.rmi.exceptions.SlaveTimeoutException;
import org.scalemt.rmi.transferobjects.AdditionalTranslationOptions;
import org.scalemt.rmi.transferobjects.BinaryDocument;
import org.scalemt.rmi.transferobjects.TextContent;

/**
 * An object on type ApertiumDaemon has an Apertium instance attached, and allows
 * sending a retrieving translations from that instance.
 * <br/>
 * It reads the following properties from <code>configuration.properties</code>:
 * <ul>
 * <li><code>daemon_frozen_time</code>:If a daemon doesn't emit any output during this time, having received an input, we assume it is frozen.</li>
 * <li><code>daemon_check_status_period</code>: Daemon status checking period</li>
 * </ul>
 * @author vmsanchez
 * 
 */
public class Daemon {

    /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(Daemon.class);

    private void stopBackingQueue() {
        synchronized (writingQueue) {
            while (writingQueue.size() > 0) {
                QueueElement element = writingQueue.poll();
                writingQueueBackup.add(element);
                //element.setException(new DaemonDeadException());
                //element.getCaller().interrupt();
            }
        }
        stop();
    }

    /**
     * Reads translations from apertium standard output and wakes up threads
     * that are waiting for these translations.
     *
     * @author vmsanchez
     *
     */
    private class EngineReader implements Runnable {

        /**
         * ID of the translation currently being read
         */
        private long currentId;
        /**
         * Translation currently being read
         */
        private StringBuilder text;

        private final BlockingQueue<QueueElement> localResultsQueue;
        private BufferedReader pReader;
        private final Process localp;
        private boolean errorFlag;

        public EngineReader(BlockingQueue<QueueElement> resultsQueue, BufferedReader pReader, Process myp) {
            localResultsQueue=resultsQueue;
            this.pReader=pReader;
            localp=myp;
            errorFlag=false;
        }

        public boolean isErrorFlag() {
            return errorFlag;
        }

        public void setErrorFlag(boolean errorFlag) {
            this.errorFlag = errorFlag;
        }



        @Override
        public void run() {

            currentId = -1;
            List<String> stackBefore = new LinkedList<String>();
            List<String> stackAfter = new LinkedList<String>();
            int numMatchesBefore = 0;
            int numMatchesAfter = 0;
            long stackedCurrentId = -1;
            long stackedEndId = -1;
            List<SeparatorRegexp> separatorBefore = translationEngine.getTranslationCore().getRegexpsBefore();
            List<SeparatorRegexp> separatorAfter = translationEngine.getTranslationCore().getRegexpsAfter();

            boolean endWithError=false;
            try {

                String buffer;
                //final Pattern pattern = Pattern.compile("^\\[--apertium-translation id=\"(\\d+)\" dict=\"(([-]?\\d+(,[-]?\\d+)*)?)\"--\\]$");

                logger.debug(daemonInformation.getId() + ": Starting EngineReader");
                while ((buffer = pReader.readLine()) != null) {
                    boolean crashed=false;
                    logger.trace(daemonInformation.getId() +
                            ": read \"" + buffer + "\"");
                    lastRead = System.currentTimeMillis();

//                    boolean matchesBefore=false, matchesAfter=false;

                    Matcher matcherBefore = separatorBefore.get(numMatchesBefore).getRegexp().matcher(buffer);
                    if (matcherBefore.matches()) {
                        int idfield = separatorBefore.get(numMatchesBefore).getIdField();
                        if (idfield > -1) {
                            stackedCurrentId = Long.parseLong(matcherBefore.group(idfield));
                        }
                        stackBefore.add(buffer);
                        numMatchesBefore++;
                        logger.trace(daemonInformation.getId()+". start "+numMatchesBefore+"/"+separatorBefore.size());
                        if (numMatchesBefore == separatorBefore.size()) {
                            text = new StringBuilder();
                            currentId = stackedCurrentId;
                            stackedCurrentId = -1;

                            if (!translationEngine.getTranslationCore().isSeparateAfterDeformat()) {
                                for(int i=0;i<numMatchesBefore-numMatchesAfter;i++)
                                {
                                    String line = stackBefore.get(i);
                                    text.append(line);
                                    text.append("\n");
                                }
                            }
                            stackBefore.clear();
                            numMatchesBefore=0;
                            
                            logger.debug(daemonInformation.getId() + ": read TRANSLATION_START. ID="+currentId);
                        }
                    }
                    else
                    {
                            for(int i=0;i<numMatchesBefore-numMatchesAfter;i++)
                            {
                                String line = stackBefore.get(i);
                                if(text!=null)
                                {
                                    text.append(line);
                                    text.append("\n");
                                }
                            }
                            stackBefore.clear();
                            numMatchesBefore=0;
                    }

                    Matcher matcherAfter = separatorAfter.get(numMatchesAfter).getRegexp().matcher(buffer);
                    if (matcherAfter.matches()) {
                        int idfield = separatorAfter.get(numMatchesAfter).getIdField();
                        if (idfield > -1) {
                            stackedEndId = Long.parseLong(matcherAfter.group(idfield));
                        }

                        stackAfter.add(buffer);
                        numMatchesAfter++;

                        logger.trace(daemonInformation.getId()+". end "+numMatchesAfter+"/"+separatorAfter.size());

                        if (numMatchesAfter == separatorAfter.size()) {
                            if (stackedEndId == currentId || idfield==-1) {
                                
                                stackedEndId = -1;

                                if (!translationEngine.getTranslationCore().isSeparateAfterDeformat()) {
                                for(int i=0;i<numMatchesAfter-numMatchesBefore;i++)
                                {
                                    String line = stackAfter.get(i);
                                    if(text!=null)
                                    {
                                        text.append(line);
                                        text.append("\n");
                                    }
                                }
                            }
                            stackAfter.clear();
                            numMatchesAfter=0;
           
                                logger.debug(daemonInformation.getId() + ": read TRANSLATION_END. ID="+currentId);

                                QueueElement element = localResultsQueue.poll();
                                if (element != null) {
                                    if (element.getId() == currentId) {
                                        element.setRawTranslation(text.toString().getBytes("UTF-8"));
                                    } else {
                                        logger.error("Daemon - EngineReader " + daemonInformation.getId() + ": Read translation id (" + currentId + ") that doesn't match translation queue element(" + element.getId() + ")");
                                        element.setTranslation(null);
                                        element.setException(new DaemonDeadException());
                                        logger.error("Daemon " + daemonInformation.getId() + " crashed");
                                        crashed = true;
                                        endWithError=true;
                                        //restart();

                                    }
                                    if (element.getCaller() != null) {
                                        element.getCaller().interrupt();
                                    }

                                } else {
                                    logger.error("Daemon - EngineReader " + daemonInformation.getId() + ": Translation queue is empty");
                                    crashed=true;
                                    endWithError=true;
                                }

                                
                                currentId = -1;

                            } else {
                                
                            }
                        }
                    }
                    else
                    {
                         for(int i=0;i<numMatchesAfter-numMatchesBefore;i++)
                        {
                            String line = stackAfter.get(i);
                            if(text!=null)
                            {
                            text.append(line);
                            text.append("\n");
                            }
                        }
                         stackAfter.clear();
                         numMatchesAfter=0;
                    }

                    if(!matcherAfter.matches() && !matcherBefore.matches())
                    {
                        if(text!=null)
                        {
                            text.append(buffer);
                            text.append("\n");
                        }
                    }
                    if(crashed)
                        break;
                }
            } catch (FileNotFoundException e) {
                logger.warn(daemonInformation.getId() + ": Cannot find pipe to read from");
                endWithError=true;
            } catch (IOException ie) {
                logger.warn(daemonInformation.getId() + ": Exception while reading pipe", ie);
                endWithError=true;
            }


            if(endWithError || this.isErrorFlag())
            {


            synchronized(localResultsQueue)
            {

                while(localResultsQueue.size()>0)
                {
                    QueueElement element = localResultsQueue.poll();
                    element.setException(new DaemonDeadException());
                    element.getCaller().interrupt();
                }
            }



            stopBackingQueue();
            start();
            OutputCleaner oc= new OutputCleaner(pReader);
            Thread t = new Thread(oc);
            t.start();
            try {
                t.join(10000);
            } catch (InterruptedException ex) {
                
            }
            if (t.isAlive())
            {
                logger.warn("Killing daemon "+daemonInformation.getId());
                localp.destroy();
            }

            }
             logger.debug("Finished EngineReader from " + daemonInformation.getId()+". EndWithError="+Boolean.toString(endWithError || this.isErrorFlag()));
           
        }
    }

    private class OutputCleaner implements Runnable
    {
        private final BufferedReader r;
       
        OutputCleaner(BufferedReader br)
        {
            r=br;
            
        }

        @Override
        public void run() {
             logger.trace("Extracting remaining output from EngineReader of daemon "+daemonInformation.getId());
            try{
             while (r.readLine() != null) {}
            }
            catch(Exception e){
            }
            logger.debug("Finished OutputCleaner from " + daemonInformation.getId());
        }
    }

    /**
     * Send translation requests to Apertium
     *
     * @author vmsanchez
     *
     */
    private class EngineWriter implements Runnable {

        private final BlockingQueue<QueueElement> localWritingQueue;
        private final BufferedWriter pWriter;
        private Set<Long> requestsBeforeCore;
       

        public EngineWriter(BlockingQueue<QueueElement> writingQueue, BufferedWriter pWriter) {
            localWritingQueue=writingQueue;
            this.pWriter=pWriter;
            requestsBeforeCore=new HashSet<Long>();
        }

        public Set<Long> getRequestsBeforeCore() {
            return requestsBeforeCore;
        }

        

        @Override
        public void run() {

            try {
                QueueElement queueElement;
                String trash=getTranslationEngine().getTranslationCore().getTrash();
                String startText;
                String endText;
                StringBuilder textToWrite;

                while ((queueElement = localWritingQueue.take()) != stopMark) {
                    StringBuilder dictString=new StringBuilder("");
                    
                    AdditionalTranslationOptions ato=queueElement.getAdditionalTranslationOptions();
                    if(ato!=null)
                    {
                    List<Long> dictionaries = ato.getDictionaries();
                    for(int i=0; i< dictionaries.size();i++)
                    {
                        dictString.append(dictionaries.get(i));
                        if(i<dictionaries.size()-1)
                            dictString.append(",");
                    }
                    }

                    startText = translationEngine.getTranslationCore().getTextBefore().replaceAll("\\$id", Long.toString(queueElement.getId())).replaceAll("\\$dicts", dictString.toString());
                    endText = translationEngine.getTranslationCore().getTextAfter().replaceAll("\\$id", Long.toString(queueElement.getId()));
                    textToWrite = new StringBuilder();
                     if(translationEngine.getTranslationCore().isSeparateAfterDeformat())
                     {
                        textToWrite.append(startText);
                        textToWrite.append("\n");
                     }
                    /*
                    StringBuilder textToWrite = new StringBuilder(
                    "[--apertium-translation id=\"");
                    textToWrite.append(queueElement.getId());
                    textToWrite.append("\" dict=\"");
                    if(queueElement.getDictionaries()!=null)
                    {
                    for(int i=0; i<queueElement.getDictionaries().size();i++)
                    {
                    textToWrite.append(queueElement.getDictionaries().get(i));
                    if(i<queueElement.getDictionaries().size()-1)
                    textToWrite.append(",");
                    }
                    }
                    textToWrite.append("\"--]\n");*/
                    textToWrite.append(new String(queueElement.getRawContent(),"UTF-8"));
                    textToWrite.append("\n");
                    if(translationEngine.getTranslationCore().isSeparateAfterDeformat())
                    {
                        textToWrite.append(endText);
                        textToWrite.append("\n");
                    }
                    if (translationEngine.getTranslationCore().isNullFlush()) {
                        char nullchar = 0;
                        textToWrite.append(nullchar);
                        textToWrite.append("\n");
                    }

                    logger.trace("numRequestsBeforeCore="+requestsBeforeCore.size());
                    if(trash!=null)
                    {
                        if(requestsBeforeCore.size()<=1)
                        {
                            for(int i=0; i<trashNeededToFlush*2;i++)
                            {
                                textToWrite.append(trash);
                                textToWrite.append("\n");
                            }
                        }
                    }
                    logger.trace("Daemon "+ instance.getId() +". Translation "+queueElement.getId()+". Writing to translation engine: "+textToWrite.toString());
                    resultsQueue.put(queueElement);
                    lastWrite = System.currentTimeMillis();
                    synchronized(pWriter)
                    {
                        pWriter.write(textToWrite.toString());
                        pWriter.flush();
                    }
                    requestsBeforeCore.remove(queueElement.getId());

                }
            } catch (Exception e) {
                logger.error("Can't write to daemon: ", e);               
            }
            try {
                pWriter.close();
            } catch (Exception e) {
                logger.error("Can't stop daemon",e);
            }
            /*
            synchronized(localWritingQueue)
            {
                while(localWritingQueue.size()>0)
                {
                    QueueElement element = localWritingQueue.poll();
                    element.setException(new DaemonDeadException());
                    element.getCaller().interrupt();
                }
            }
             * 
             */
            logger.debug("Finished EngineWriter from " + daemonInformation.getId());
        }
    }

    /**
     *  Checks if the daemon process has finished or is frozen.
     */
    private class StatusReader extends TimerTask {

        @Override
        public void run() {

            // Check if process has finished
            int exitValue = -1;
            boolean finished,frozen,passmemorylimit;
            frozen=false;
            passmemorylimit=false;   
            finished=false;
            try {
                if(killFrozenDaemons)
                {
                    exitValue = p.exitValue();
                }
                else
                {
                    exitValue=p.waitFor();
                }
                
                finished = true;
            } catch (IllegalThreadStateException e) {
                finished = false;
            }
            catch(InterruptedException ie)
            {
                
            }
            if (finished) {
                logger.error("Daemon " + daemonInformation.getId() + " finished with status " + exitValue);
            } else {
                
                if(killFrozenDaemons)
                {    
                    
                    // Check if process is frozen
                    if (lastRead < lastWrite && System.currentTimeMillis() - lastWrite > frozenTime) {
                        logger.error("Daemon " + daemonInformation.getId() + " frozen");
                        frozen=true;
                    }
                    else
                    {
                        Long memused=getMemoryUsed();
                        if(memused!=null)
                        {
                            if(memused.longValue() > maxMemoryPerDaemon)
                            {
                                passmemorylimit=true;
                                 logger.error("Daemon " + daemonInformation.getId() + " excedeed memory limit");
                            }
                        }

                    }
                }
            }

            if ((frozen || passmemorylimit) && killFrozenDaemons) {
                //try {
                    //crashed = true;
                    //TranslationEnginePool.sigar.kill(daemonInformation.getPid(), 9);
                    engineReader.setErrorFlag(true);
                    killProcessTree(daemonInformation.getPid());
                    
                    //p.destroy();
                //} catch (SigarException ex) {
                  //  logger.error("Couldn't kill process (daemon "+daemonInformation.getId()+")");
                //}
            }


        }


    }
    /**
     * Daemon information
     */
    private final DaemonInformation daemonInformation;
    /**
     * Translation engine core process
     */
    private Process p;
   
    /**
     * Command to invoke the translation engine
     */
    private String[] command;
    /**
     * Thread running {@link ApertiumReader}
     */
    private Thread tReader;
    /**
     * Thread running {@link ApertiumWriter}
     */
    private Thread tWriter;

    private EngineWriter engineWriter;
    private EngineReader engineReader;

    private BufferedWriter processWriter;

    /**
     * Associates translation ids with the length of their source texts
     */
    private Map<Long, Long> charactersPerTranslation;


    /**
     * Lock protecting <code>daemonInformation</code>
     */
    private ReadWriteLock readWriteLock;
    /**
     * Queue where translation requests are put before writing them to the Apertium pipeline
     */
    private final BlockingQueue<QueueElement> writingQueue;

    private final List<QueueElement> writingQueueBackup;

    /**
     * Queue where translation requests are put after writing them to the Apertium pipeline
     */
    private final BlockingQueue<QueueElement> resultsQueue;
    /**
     * Queue element that stops all the running threads
     */
    private QueueElement stopMark;
   
    /**
     * Timer that runs {@link StatusReader} periodically
     */
    private Timer statusTimer;
    /**
     * Last time something was written to Apertium pipeline
     */
    private long lastWrite;
    /**
     * Last time something was read from Apertium pipeline
     */
    private long lastRead;
    /**
     * Daemon status checking period
     */
    private long checkStatusPeriod;

    /**
     * Maximum amount of memory a daemon can have (measured in megabytes)
     */
    private long maxMemoryPerDaemon;


    /**
     * If a daemon doesn't emit any output during this time, having received
     * an input, we assume it is frozen.
     */
    private long frozenTime;
    /**
     * Has the Apertium process crashed?
     */
    //private boolean crashed = false;
    /**
     * Information about the translation engine associated with this daemon
     */
    private TranslationEngineInfo translationEngine;

    private final String tmpDir;

    private int trashNeededToFlush=0;
    
    private boolean killFrozenDaemons=false;

    public static final Sigar sigar = new Sigar();

    private Daemon instance=this;

    Daemon(long id, DaemonConfiguration c, TranslationEngineInfo te) {
        translationEngine = (TranslationEngineInfo) SerializationUtils.clone(te);
        daemonInformation = new DaemonInformation(id, c);
        charactersPerTranslation = Collections.synchronizedMap(new HashMap<Long, Long>());
        readWriteLock = new ReentrantReadWriteLock();
       
        writingQueueBackup=new ArrayList<QueueElement>();

        stopMark = new QueueElement(-100, null);
        
        writingQueue = new LinkedBlockingQueue<QueueElement>();
        resultsQueue = new LinkedBlockingQueue<QueueElement>();

        frozenTime = 20000;
        try {
            frozenTime = Long.parseLong(ServerUtil.readProperty("daemon_frozen_time"));
        } catch (Exception e) {
            logger.warn("Exception reading daemon_frozen_time", e);
        }

        checkStatusPeriod = 10000;
        try {
            checkStatusPeriod = Long.parseLong(ServerUtil.readProperty("daemon_check_status_period"));
        } catch (Exception e) {
            logger.warn("Exception reading daemon_check_status_period", e);
        }

        maxMemoryPerDaemon=2000;
        try {
            maxMemoryPerDaemon = Long.parseLong(ServerUtil.readProperty("max_memory_per_daemon"));
        } catch (Exception e) {
            logger.warn("Exception reading max_memory_per_daemon", e);
        }
        
        if ("yes".equals(ServerUtil.readProperty("kill_frozen_daemons")))
        {
            this.killFrozenDaemons=true;
        }

        //TODO: incluir formato
        //TODO: Y si quiero usar otro separador?
        String commandCore = translationEngine.getTranslationCore().getCommand().replaceAll("\\$p", c.getLanguagePair().getSource() + "-" + c.getLanguagePair().getTarget());
        String execPrefix="";
        String[] path=commandCore.split("/");

        for(int i=0; i<path.length-1; i++)
        {
            if(i==0 && commandCore.startsWith("/"))
                execPrefix="/";
            execPrefix=execPrefix+path[i]+"/";
        }
        

        command = new String[3];
        command[0]="bash";
        command[1]="-c";
        command[2] = execPrefix+"execAndGetPID.sh " + commandCore;
        

        tmpDir=System.getProperty("java.io.tmpdir");

    }

    /**
     * Creates a wrapper class for an Apertium process.
     *
     * @param id Id of the daemon
     * @param pair Language pair the daemon will translate
     * @param suffix Suffix that, added to the language pairs, makes the name
     * of the mode of Apertium that will be invoked. For example, if pair is es-en and mode "-null",
     * Apertium will be invoked with the mode <code>es-en-null</code>
     * @param apertiumPath
     */
    /*
    Daemon(long id, LanguagePair pair, String suffix,
    String apertiumPath) {

    daemonInformation = new DaemonInformation(id, pair);

    charactersPerTranslation = Collections
    .synchronizedMap(new HashMap<Long, Long>());
    readWriteLock = new ReentrantReadWriteLock();
    writingQueue = new LinkedBlockingQueue<ApertiumQueueElement>();
    resultsQueue = new LinkedBlockingQueue<ApertiumQueueElement>();
    stopMark = new ApertiumQueueElement(-100, "");
    this.apertiumPath = apertiumPath;

    command = new String[5];
    command[0] = apertiumPath + "/execAndGetPID.sh";
    command[1] = apertiumPath + "/apertium";
    command[2] = "-f";
    command[3] = "none";
    command[4] = pair.getSource() + "-" + pair.getTarget() + suffix;

    tReader = new Thread(new ApertiumReader());
    tWriter = new Thread(new ApertiumWriter());
    statusTimer = new Timer();

    frozenTime=20000;
    try
    {
    frozenTime=Long.parseLong(ServerUtil.readProperty("daemon_frozen_time"));
    }
    catch(Exception e){logger.warn("Exception reading daemon_frozen_time", e);}

    checkStatusPeriod=5000;
    try
    {
    checkStatusPeriod=Long.parseLong(ServerUtil.readProperty("daemon_check_status_period"));
    }
    catch(Exception e){logger.warn("Exception reading daemon_check_status_period", e);}

    }
     */
    /**
     * Starts daemon. Daemon must be started before sending any translation
     *
     */
    public void start() {
        daemonInformation.setCharactersInside(0);
        Runtime r = Runtime.getRuntime();
        try {
            p = r.exec(command);
            BufferedReader pReader = new BufferedReader(new InputStreamReader(p.getInputStream(),"UTF-8"));
            BufferedWriter pWriter = new BufferedWriter(new OutputStreamWriter(p.getOutputStream(),"UTF-8"));
            processWriter=pWriter;

            synchronized(writingQueue)
            {
                writingQueue.clear();
                writingQueue.addAll(writingQueueBackup);
                writingQueueBackup.clear();
            }
            resultsQueue.clear();

            engineReader=new EngineReader(resultsQueue,pReader,p);
            tReader = new Thread(engineReader);
            engineWriter=new EngineWriter(writingQueue,pWriter);
            tWriter = new Thread(engineWriter);
            tReader.start();
            tWriter.start();

            lastRead = 0;
            lastWrite = 0;

            charactersPerTranslation.clear();
            getDaemonInformation().setCharactersInside(0);
            //numRequestsBeforeCore=0;
            
            if(killFrozenDaemons)
            {
                statusTimer = new Timer();
                statusTimer.schedule(new StatusReader(), checkStatusPeriod, checkStatusPeriod);
            }
            else
            {
                Thread t = new Thread(new StatusReader());
                t.start();
            }

            try {
                String pid = new BufferedReader(new InputStreamReader(p.getErrorStream())).readLine();
                daemonInformation.setPid(Integer.parseInt(pid));
                logger.debug("Started daemon with pid=" + daemonInformation.getPid());
            } catch (Exception e) {
                logger.error("Cannot get process pid", e);
            }

        } catch (Exception e) {
            logger.fatal("Can't start daemon", e);
        }

    }

    /**
     * Stops daemon. It simply closes Apertium input. Apertium will stop when
     * translations inside it are completed
     */
    public void stop() {
        statusTimer.cancel();
        try {
            writingQueue.put(stopMark);
            //writingQueue.
        } catch (Exception e) {
            logger.error("Coultn't write into writing queue to stop daemon", e);
        }
        

    }

    private void dirtyStop()
    {
        statusTimer.cancel();
        this.p.destroy();
        start();
    }

     private void restart() {
            stopBackingQueue();
            start();
        }

    /**
     * Requests a translation. When it finishes, thread specified in
     * <code>element</code> will be interrupted, and result will be in
     * <code>translation</code> property
     *
     * @param element
     */
    public  void translate(QueueElement element, long timeout) throws TranslationEngineException{

        List<Program> programs =translationEngine.getPrograms();
        Map<Integer,byte[]> memoryVars= new HashMap<Integer,byte[]>();
        Map<Integer,String> fileVars= new HashMap<Integer, String>();
        Map<Integer,String> dirVars= new HashMap<Integer, String>();
        int output=-10;
        int input=-1;
        String programCommand;
        boolean inputBinary=false;
        if(element.getSource().isBinary())
            inputBinary=true;

        logger.debug("Daemon "+ this.getId() +". Translating "+element.getId()+". Source text: "+element.getSource().toString());

        try
        {

        for(Entry<Integer, VariableType> entry: translationEngine.getVariables().entrySet())
        {
            if(entry.getValue()==VariableType.file)
                fileVars.put(entry.getKey(), tmpDir+"/"+"scalablewebservice-"+this.getId()+"-"+element.getId()+"-"+entry.getKey());
            else if(entry.getValue()==VariableType.dir)
            {
                String dirPath=tmpDir+"/"+"dirscalablewebservice-"+this.getId()+"-"+element.getId()+"-"+entry.getKey();
                dirVars.put(entry.getKey(), dirPath);
                new File(dirPath).mkdir();
            }
            else
                memoryVars.put(entry.getKey(), new byte[1]);
        }

        byte[] inVariable;
        if(translationEngine.getTranslationCore().isSeparateAfterDeformat())
        {
            inVariable=element.getSource().toByteArray();
        }
        else
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String startText = translationEngine.getTranslationCore().getTextBefore().replaceAll("\\$id", Long.toString(element.getId()));
            String endText = translationEngine.getTranslationCore().getTextAfter().replaceAll("\\$id", Long.toString(element.getId()));
            byte[] startBytes = startText.getBytes("UTF-8");
            byte[] endBytes = endText.getBytes("UTF-8");
            byte[] newLineBytes ="\n".getBytes("UTF-8");
            byte[] inputTextBytes = element.getSource().toByteArray();

            baos.write(startBytes, 0, startBytes.length);
            baos.write(newLineBytes, 0, newLineBytes.length);
            baos.write(inputTextBytes, 0, inputTextBytes.length);
            baos.write(newLineBytes, 0, newLineBytes.length);
            baos.write(endBytes, 0, endBytes.length);
            baos.close();
            inVariable=baos.toByteArray();
            
        }
        
        memoryVars.put(-1,inVariable );
        memoryVars.put(-2, new byte[10]);

        while(output!=-2)
        {
            if(translationEngine.getTranslationCore().getInput()==input)
            {
                output=translationEngine.getTranslationCore().getOutput();
                logger.debug("Daemon "+ this.getId() +". Translation "+element.getId()+". Calling translation engine");
                boolean isTimeout=true;
                element.setRawContent(memoryVars.get(input));
                 try {
                    element.setException(null);
                    writingQueue.put(element);
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    isTimeout=false;
                    if(element.getException()!=null)
                    {
                            throw element.getException();
                    }
                    memoryVars.put(output,element.getRawTranslation());
                }
                if(isTimeout)
                    throw new SlaveTimeoutException("Timeout waiting for translation core");
                logger.trace("Daemon "+ this.getId() +". Translation "+element.getId()+". Translation engine output:"+memoryVars.get(output).toString());               
               
            }
            else
            {
                for(Program program: programs)
                {
                    if(program.getInput()==input)
                    {
                         output=program.getOutput();

                         boolean meetsRestrictions=true;

                         for(Entry<String,String> restriction: program.getRestrictions().entrySet())
                         {
                             String value=element.getAdditionalTranslationOptions().getOptions().get(restriction.getKey());
                             if( !(value!=null && value.equals(restriction.getValue())) )
                             {
                                 meetsRestrictions=false;
                             }
                         }

                        if(program.getOnlyFormats().contains(element.getFormat()) && meetsRestrictions)
                        {
                            synchronized(program)
                            {
                            String[] fullCommand= new String[3];

                            //TODO: Make it platform-independent
                            fullCommand[0]="/bin/bash";
                            fullCommand[1]="-c";
                            //Prepare command
                            programCommand=program.getCommand();
                            for(Entry<Integer,String> entry: fileVars.entrySet())
                                programCommand=programCommand.replaceAll("\\$"+entry.getKey(), entry.getValue());
                            for(Entry<Integer,String> entry: dirVars.entrySet())
                                programCommand=programCommand.replaceAll("\\$"+entry.getKey(), entry.getValue());
                            programCommand=programCommand.replaceAll("\\$f", element.getFormat().name());
                            programCommand=programCommand.replaceAll("\\$p", element.getLanguagePair().toString());

                            if(fileVars.containsKey(program.getInput()))
                                programCommand=programCommand+" <"+fileVars.get(program.getInput());
                            if(fileVars.containsKey(program.getOutput()))
                                programCommand=programCommand+" >"+fileVars.get(program.getOutput());

                            fullCommand[2]=programCommand;
                            logger.debug("Daemon "+ this.getId() +". Translation "+element.getId()+". Launching "+programCommand);

                            //Execute command
                            StreamGobbler stdoutGobbler;

                             Process localp=null;
                            try
                            {

                            //Execute command
                            localp =Runtime.getRuntime().exec(fullCommand);

                            //Create threads for reading stdout and stderr
                            stdoutGobbler = new StreamGobbler(localp.getInputStream());
                            StreamGobbler stderrGobbler = new StreamGobbler(localp.getErrorStream());
                            stdoutGobbler.start();
                            stderrGobbler.start();

                            if(memoryVars.containsKey(program.getInput()))
                            {
                                logger.trace("Writing input ("+input+") to program "+programCommand+" ("+memoryVars.get(program.getInput()).length+" bytes)");
                                //Write source text
                                //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                                if(program.getInFilter()!=null)
                                {
                                     localp.getOutputStream().write(program.getInFilter().replaceAll("\\$in", new String(memoryVars.get(program.getInput()),"UTF-8")).getBytes("UTF-8") );
                                }
                                else
                                {
                                    localp.getOutputStream().write(memoryVars.get(program.getInput()));
                                }
                                //writer.flush();
                                //writer.close();
                               
                            }
                             localp.getOutputStream().close();

                            //Wait for stdout reader thread to end reading
                            try
                            {
                            stdoutGobbler.join();
                            }catch (InterruptedException e) {
                                logger.error("Program interrupted", e);
                            }

                             if(memoryVars.containsKey(output))
                            {
                                //Read process output
                                 memoryVars.put(output, stdoutGobbler.getReadBytes());
                            }
                            if(localp.waitFor()!=0)
                                throw new NonZeroExitValueException("Exit value of program "+programCommand+" != 0");

                            }
                            catch(IOException e)
                            {
                                logger.error("IOException executing program", e);
                            }

                            byte[] traceMemVar=memoryVars.get(output);
                            String traceMemVarStr=traceMemVar.toString();
                            try
                            {
                                traceMemVarStr=new String(traceMemVar, "UTF-8");
                            }
                            catch(Exception e)
                            {
                                
                            }
                            
                            logger.trace("Daemon "+ this.getId() +". Translation "+element.getId()+". "+programCommand+" output: "+ traceMemVarStr);
                            }
                        }
                        else
                        {
                            if(memoryVars.containsKey(input))
                            {
                                byte[] inputBytes = memoryVars.get(input);
                                if(memoryVars.containsKey(output))
                                {
                                    memoryVars.put(output, inputBytes);
                                }
                                else if(fileVars.containsKey(output))
                                {
                                    FileOutputStream fos = new FileOutputStream(fileVars.get(output));
                                    fos.write(inputBytes);
                                    fos.close();
                                }
                            }
                            else if(fileVars.containsKey(program.getInput()))
                            {
                                if(memoryVars.containsKey(output))
                                {
                                    FileInputStream fis = new FileInputStream(fileVars.get(input));
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    byte[] buffer= new byte[100];
                                    int read=fis.read(buffer);
                                    while(read!=-1)
                                    {
                                        baos.write(buffer, 0, read);
                                        read=fis.read(buffer);
                                    }
                                    fis.close();
                                    baos.close();
                                    memoryVars.put(output, baos.toByteArray());
                                }
                                else if(fileVars.containsKey(output))
                                {
                                    FileOutputStream fos = new FileOutputStream(fileVars.get(output));
                                    FileInputStream fis = new FileInputStream(fileVars.get(input));
                                    byte[] buffer= new byte[100];
                                    int read=fis.read(buffer);
                                    while(read!=-1)
                                    {
                                        fos.write(buffer, 0, read);
                                        read=fis.read(buffer);
                                    }
                                    fis.close();
                                    fos.close();
                                }
                            }
                        }
                    }
                }
            }
            input=output;

        }

        StringBuilder outputStr = new StringBuilder(new String(memoryVars.get(-2),"UTF-8"));
        if(!translationEngine.getTranslationCore().isSeparateAfterDeformat())
        {
            outputStr.delete(0, outputStr.indexOf("\n")+1);
            outputStr.delete(outputStr.lastIndexOf("\n"), outputStr.length());
        }        
        else
        {
            if(outputStr.charAt(outputStr.length()-1)=='\n')
                outputStr.deleteCharAt(outputStr.length()-1);
        }
        memoryVars.put(-2,outputStr.toString().getBytes("UTF-8"));

        if(inputBinary)
            element.setTranslation(new BinaryDocument(element.getFormat(), memoryVars.get(-2)));
        else
            element.setTranslation(new TextContent(element.getFormat(),new String(memoryVars.get(-2),"UTF-8")));

        }
        catch(SlaveTimeoutException ste)
        {
            logger.error("Slave timeout exception", ste);
            throw ste;
        }
        catch(TranslationEngineException te)
        {
            //te.printStackTrace();
            logger.error("Unexpected exception while translating", te);
            throw te;
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            logger.error("Unexpected exception while translating", e);
            throw new TranslationEngineException(e);
        }
            finally
            {
                engineWriter.getRequestsBeforeCore().remove(element.getId());
                if (charactersPerTranslation.containsKey(element.getId())) {
                long translationSize = charactersPerTranslation.get(element.getId());
                try
                {
                readWriteLock.writeLock().lock();
                daemonInformation.setCharactersInside(daemonInformation.getCharactersInside() - translationSize);
                    }
                finally{
                readWriteLock.writeLock().unlock();
                    }
                charactersPerTranslation.remove(element.getId());

                for(Entry<Integer,String> entry: dirVars.entrySet())
                {
                    String dirPath=tmpDir+"/"+"dirscalablewebservice-"+this.getId()+"-"+element.getId()+"-"+entry.getKey();
                    ServerUtil.deleteDirectory(new File(dirPath));
                }

            }
            
        }

    }

    public synchronized void assignQueueElement(QueueElement element) {
        try{
        readWriteLock.writeLock().lock();
        engineWriter.getRequestsBeforeCore().add(element.getId());
        daemonInformation.setCharactersInside(daemonInformation.getCharactersInside() + element.getSource().getLength());
        }
        finally
        {
        readWriteLock.writeLock().unlock();
        }
        charactersPerTranslation.put(element.getId(), (long) (element.getSource().getLength()));
        element.setDaemon(this);
    }

    /**
     * Get number of characters currently being processed by the daemon. It is a
     * good way to measure daemon load
     *
     * @return
     */
    public long getCharactersInside() {

        try
        {
        readWriteLock.readLock().lock();
        long returnValue = daemonInformation.getCharactersInside();
        return returnValue;
        }
        finally{
        readWriteLock.readLock().unlock();}

        
    }

    /**
     * Gets the language pair this daemon can translate.
     * @return anguage pair this daemon can translate.
     */
    public DaemonConfiguration getConfiguration() {
        return daemonInformation.getConfiguration();
    }

    /**
     * Gets the unique id of this daemon
     * @return Unique id of this daemon
     */
    public long getId() {
        return daemonInformation.getId();
    }

    /**
     * Gets all the relevant daemon information
     * @return Relevant daemon information
     */
    public DaemonInformation getDaemonInformation() {

        synchronized (daemonInformation) {
            return daemonInformation.copy();
        }

    }

    /**
     *
     * @return
     */
    /*
    public boolean isCrashed() {
        return crashed;
    }
    */
    /**
     * Gets the % of system's memory consumed by the Apertium process
     * @return % of system's memory consumed by the Apertium process, or <code>null</code> if there is any error
     */
    /*
    public Double getMemoryUsage() {
        String information = "";
        try {
            String[] command = {"getProcessInfo.sh", Integer.toString(daemonInformation.getPid())};
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            information = reader.readLine();
            reader.close();
            String[] pieces = information.split(" ");

            return Double.parseDouble(pieces[1]);
        } catch (Exception e) {
            logger.error("Exception reading memory usage", e);
        }

        return null;
    }
     *
     */

    /**
     *
     * @return Memory used, in MiB
     */
    public Long getMemoryUsed()
    {
        Set<Long> finalProcesses= new HashSet<Long>();
        Set<Long> unexploredProcesses= new HashSet<Long>();

        long parentPid=daemonInformation.getPid();
        unexploredProcesses.add(parentPid);

        synchronized(sigar)
        {
        ProcessFinder pf = new ProcessFinder(sigar);
        String bquery="State.Ppid.eq=";

        try
        {

            while(!unexploredProcesses.isEmpty())
            {
                Long unexploredProcess = unexploredProcesses.iterator().next();
                unexploredProcesses.remove(unexploredProcess);
                finalProcesses.add(unexploredProcess);

                long[] children=pf.find(bquery+unexploredProcess.toString());
                for(long child: children)
                {
                    unexploredProcesses.add(child);
                }
            }
            


            long totalMem=0;

            for(Long processPid: finalProcesses)
                totalMem+=TranslationEnginePool.sigar.getProcMem(processPid).getResident();
            
            return totalMem/1024/1024;
           
        }
        catch(SigarException e)
        {
            
        }
        }

        return null;
    }

    /**
     * Gets the % of system's CPU consumed by the Apertium process
     * @return % of system's CPU consumed by the Apertium process, or <code>null</code> if there is any error
     */
    public Double getCPUUsage() {
        String information = "";
        try {
            String[] myCommand = {"getProcessInfo.sh", Integer.toString(daemonInformation.getPid())};
            Process p = Runtime.getRuntime().exec(myCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            information = reader.readLine();
            reader.close();
            String[] pieces = information.split(" ");

            return Double.parseDouble(pieces[0]);
        } catch (Exception e) {
            logger.error("Exception reading memory usage", e);
        }

        return null;
    }

    private synchronized void killProcessTree( long parentPid)
    {
        Set<Long> finalProcesses= new HashSet<Long>();
        Set<Long> unexploredProcesses= new HashSet<Long>();

        
        unexploredProcesses.add(parentPid);
        synchronized(sigar)
        {
        ProcessFinder pf = new ProcessFinder(sigar);
        String bquery="State.Ppid.eq=";


            while(!unexploredProcesses.isEmpty())
            {
                Long unexploredProcess = unexploredProcesses.iterator().next();
                unexploredProcesses.remove(unexploredProcess);
                finalProcesses.add(unexploredProcess);

                long[] children=new long[0];
            try {
                children = pf.find(bquery + unexploredProcess.toString());
            } catch (SigarException ex) {
                
            }
                for(long child: children)
                {
                    unexploredProcesses.add(child);
                }
            }



          
            logger.debug("Killing processs tree. father="+Long.toString(parentPid));
            for(Long processPid: finalProcesses)
                try {
            logger.debug("killing PID="+Long.toString(processPid.longValue()));
            sigar.kill(processPid, 9);
        } catch (SigarException ex) {
           
        }
        logger.debug("End killing processs tree. father="+Long.toString(parentPid));
        }
        
       
    }

    public TranslationEngineInfo getTranslationEngine() {
        return translationEngine;
    }

    private class TrashSender implements Runnable
    {
        private int numberSent;
        private boolean stop;
        private final BufferedWriter pWriter;

        public TrashSender(BufferedWriter pWriter) {
            numberSent=0;
            stop=false;
            this.pWriter=pWriter;
        }

        public int getNumberSent() {
            return numberSent;
        }

        public void setNumberSent(int numberSent) {
            this.numberSent = numberSent;
        }

        public boolean isStop() {
            return stop;
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }


        @Override
        public void run() {
            while(!stop)
            {
                try
                {
                    synchronized(pWriter)
                    {
                    pWriter.write(translationEngine.getTranslationCore().getTrash());
                    pWriter.newLine();
                     pWriter.flush();
                    }
                    numberSent++;
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                try
                {
                Thread.sleep(1000);
                }
                catch(InterruptedException ie)
                {}
            }
        }
        
    }

    public int computeThrashNeededToFlush()
    {
        if(translationEngine.getTranslationCore().getTrash()!=null)
        {
        start();
        statusTimer.cancel();
        TrashSender tsender = new TrashSender(processWriter);
        Thread t = new Thread(tsender);
        QueueElement element=new QueueElement(1, new TextContent( Format.txt,translationEngine.getTranslationCore().getTrash()), this.getConfiguration().getLanguagePair(), Thread.currentThread(), null);
        boolean error=true;

       
         try {
            element.setException(null);
            writingQueue.put(element);
             t.start();
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            if(element.getException()==null)
            {
                    error=false;
            }
        }
        finally
        {
            tsender.setStop(true);
        }
          
        stop();

        if(!error)
            return tsender.getNumberSent();
        else
            return 0;
        }
        else
            return 0;
    }

    public int getTrashNeededToFlush() {
        return trashNeededToFlush;
    }

    public void setTrashNeededToFlush(int trashNeededToFlush) {
        this.trashNeededToFlush = trashNeededToFlush;
    }

    
}
