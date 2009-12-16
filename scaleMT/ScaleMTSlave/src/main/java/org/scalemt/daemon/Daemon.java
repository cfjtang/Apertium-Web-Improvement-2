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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.util.ServerUtil;
import org.scalemt.util.StreamGobbler;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import org.apache.commons.lang.SerializationUtils;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarFileNotFoundException;
import org.hyperic.sigar.ptql.ProcessFinder;
import org.hyperic.sigar.ptql.ProcessQuery;
import org.scalemt.main.TranslationEnginePool;

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

        private BlockingQueue<QueueElement> localResultsQueue;
        private BufferedReader pReader;

        public EngineReader(BlockingQueue<QueueElement> resultsQueue, BufferedReader pReader) {
            localResultsQueue=resultsQueue;
            this.pReader=pReader;
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


            try {

                String buffer;
                //final Pattern pattern = Pattern.compile("^\\[--apertium-translation id=\"(\\d+)\" dict=\"(([-]?\\d+(,[-]?\\d+)*)?)\"--\\]$");

                logger.debug(daemonInformation.getId() + ": Starting EngineReader");
                while ((buffer = pReader.readLine()) != null) {
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
                                        element.setTranslation(text.toString());
                                    } else {
                                        logger.error("Daemon - EngineReader " + daemonInformation.getId() + ": Read translation id (" + currentId + ") that doesn't match translation queue element(" + element.getId() + ")");
                                        element.setTranslation(null);
                                        element.setException(new DaemonDeadException());
                                        logger.error("Daemon " + daemonInformation.getId() + " crashed");
                                        //crashed = true;
                                        restart();

                                    }
                                    if (element.getCaller() != null) {
                                        element.getCaller().interrupt();
                                    }

                                } else {
                                    logger.error("Daemon - EngineReader " + daemonInformation.getId() + ": Translation queue is empty");
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
                }
            } catch (FileNotFoundException e) {
                logger.warn(daemonInformation.getId() + ": Cannot find pipe to read from");
            } catch (IOException ie) {
                logger.warn(daemonInformation.getId() + ": Exception while reading pipe", ie);
            }
            synchronized(localResultsQueue)
            {

                while(localResultsQueue.size()>0)
                {
                    QueueElement element = localResultsQueue.poll();
                    element.setException(new DaemonDeadException());
                    element.getCaller().interrupt();
                }
            }
            logger.debug("Finished EngineReader from " + daemonInformation.getId());
        }
    }

    /**
     * Send translation requests to Apertium
     *
     * @author vmsanchez
     *
     */
    private class EngineWriter implements Runnable {

        private BlockingQueue<QueueElement> localWritingQueue;
        private BufferedWriter pWriter;
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
                    startText = translationEngine.getTranslationCore().getTextBefore().replaceAll("\\$id", Long.toString(queueElement.getId()));
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
                    textToWrite.append(queueElement.getText());
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
            synchronized(localWritingQueue)
            {

                while(localWritingQueue.size()>0)
                {
                    QueueElement element = localWritingQueue.poll();
                    element.setException(new DaemonDeadException());
                    element.getCaller().interrupt();
                }
            }
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
            boolean finished;
            try {
                exitValue = p.exitValue();
                finished = true;
            } catch (IllegalThreadStateException e) {
                finished = false;
            }
            if (finished) {
                logger.error("Daemon " + daemonInformation.getId() + " finished with status " + exitValue);
            } else {
                // Check if process is frozen
                if (lastRead < lastWrite && System.currentTimeMillis() - lastWrite > frozenTime) {
                    logger.error("Daemon " + daemonInformation.getId() + " frozen");
                    finished = true;
                }
            }

            if (finished) {
                //crashed = true;
                restart();
            }


        }


    }
    /**
     * Daemon information
     */
    private DaemonInformation daemonInformation;
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
    private BlockingQueue<QueueElement> writingQueue;
    /**
     * Queue where translation requests are put after writing them to the Apertium pipeline
     */
    private BlockingQueue<QueueElement> resultsQueue;
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

    private Daemon instance=this;

    Daemon(long id, DaemonConfiguration c, TranslationEngineInfo te) {
        SerializationUtils seUtils = new SerializationUtils();
        translationEngine = (TranslationEngineInfo) seUtils.clone(te);
        daemonInformation = new DaemonInformation(id, c);
        charactersPerTranslation = Collections.synchronizedMap(new HashMap<Long, Long>());
        readWriteLock = new ReentrantReadWriteLock();
       
        stopMark = new QueueElement(-100, "");
        
       

        frozenTime = 20000;
        try {
            frozenTime = Long.parseLong(ServerUtil.readProperty("daemon_frozen_time"));
        } catch (Exception e) {
            logger.warn("Exception reading daemon_frozen_time", e);
        }

        checkStatusPeriod = 5000;
        try {
            checkStatusPeriod = Long.parseLong(ServerUtil.readProperty("daemon_check_status_period"));
        } catch (Exception e) {
            logger.warn("Exception reading daemon_check_status_period", e);
        }

        //TODO: incluir formato
        //TODO: Y si quiero usar otro separador?
        String commandCore = translationEngine.getTranslationCore().getCommand().replaceAll("\\$p", c.getLanguagePair().getSource() + "-" + c.getLanguagePair().getTarget());
        command = new String[3];
        command[0]="bash";
        command[1]="-c";
        command[2] = "execAndGetPID.sh " + commandCore;
        

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
            BufferedReader pReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedWriter pWriter = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            processWriter=pWriter;

            writingQueue = new LinkedBlockingQueue<QueueElement>();
            resultsQueue = new LinkedBlockingQueue<QueueElement>();

            tReader = new Thread(new EngineReader(resultsQueue,pReader));
            engineWriter=new EngineWriter(writingQueue,pWriter);
            tWriter = new Thread(engineWriter);
            tReader.start();
            tWriter.start();

            lastRead = 0;
            lastWrite = 0;

            charactersPerTranslation.clear();
            getDaemonInformation().setCharactersInside(0);
            //numRequestsBeforeCore=0;
            
            statusTimer = new Timer();
            statusTimer.schedule(new StatusReader(), checkStatusPeriod, checkStatusPeriod);

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
     * translation inside it are completed
     */
    public void stop() {
        statusTimer.cancel();
        try {
            writingQueue.put(stopMark);
        } catch (Exception e) {
            logger.error("Coultn't write into writing queue to stop daemon", e);
        }
        

    }

     private void restart() {
            stop();
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
        Map<Integer,StringBuilder> memoryVars= new HashMap<Integer, StringBuilder>();
        Map<Integer,String> fileVars= new HashMap<Integer, String>();
        int output=-10;
        int input=-1;
        String programCommand;

        logger.debug("Daemon "+ this.getId() +". Translating "+element.getId()+". Source text: "+element.getText());

        try
        {

        for(Entry<Integer, VariableType> entry: translationEngine.getVariables().entrySet())
        {
            if(entry.getValue()==VariableType.file)
                fileVars.put(entry.getKey(), tmpDir+"/"+"scalablewebservice-"+this.getId()+"-"+element.getId()+"-"+entry.getKey());
            else
                memoryVars.put(entry.getKey(), new StringBuilder());
        }
        StringBuilder inBuilder,outBuilder;
        if(translationEngine.getTranslationCore().isSeparateAfterDeformat())
        {
            inBuilder=new StringBuilder(element.getText());
            //inBuilder.append("\n");
        }
        else
        {
            String startText = translationEngine.getTranslationCore().getTextBefore().replaceAll("\\$id", Long.toString(element.getId()));
            String endText = translationEngine.getTranslationCore().getTextAfter().replaceAll("\\$id", Long.toString(element.getId()));
            inBuilder=new StringBuilder(startText);
            inBuilder.append("\n");
            inBuilder.append(element.getText());
            inBuilder.append("\n");
            inBuilder.append(endText);
            //inBuilder.append("\n");
        }
        outBuilder=new StringBuilder();
        memoryVars.put(-1, inBuilder);
        memoryVars.put(-2, outBuilder);



        while(output!=-2)
        {
            if(translationEngine.getTranslationCore().getInput()==input)
            {
                 output=translationEngine.getTranslationCore().getOutput();
                logger.debug("Daemon "+ this.getId() +". Translation "+element.getId()+". Calling translation engine");
                element.setText(memoryVars.get(input).toString());
                 try {
                    element.setException(null);
                    writingQueue.put(element);
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    if(element.getException()!=null)
                    {
                            throw element.getException();
                    }
                    memoryVars.get(output).append(element.getTranslation());
                }
                logger.trace("Daemon "+ this.getId() +". Translation "+element.getId()+". Translation engine output:"+memoryVars.get(output).toString());
                
               
            }
            else
            {
                for(Program program: programs)
                {
                    if(program.getInput()==input)
                    {
                        synchronized(program)
                        {
                        
                        output=program.getOutput();

                        //Prepare command
                        programCommand=program.getCommand();
                        for(Entry<Integer,String> entry: fileVars.entrySet())
                            programCommand=programCommand.replaceAll("\\$"+entry.getKey(), entry.getValue());
                        programCommand=programCommand.replaceAll("\\$f", element.getFormat().name());
                        programCommand=programCommand.replaceAll("\\$p", element.getLanguagePair().toString());
                        
                        if(fileVars.containsKey(program.getInput()))
                            programCommand=programCommand+" <"+fileVars.get(program.getInput());
                        if(fileVars.containsKey(program.getOutput()))
                            programCommand=programCommand+" >"+fileVars.get(program.getOutput());

                        logger.debug("Daemon "+ this.getId() +". Translation "+element.getId()+". Launching "+programCommand);

                        //Execute command
                        StreamGobbler stdoutGobbler;

                        try
                        {

                        //Execute command
                        Process p =Runtime.getRuntime().exec(programCommand);

                        //Create threads for reading stdout and stderr
                        stdoutGobbler = new StreamGobbler(p.getInputStream());
                        StreamGobbler stderrGobbler = new StreamGobbler(p.getErrorStream());
                        stdoutGobbler.start();
                        stderrGobbler.start();

                        if(memoryVars.containsKey(program.getInput()))
                        {
                            //Write source text
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                            if(program.getInFilter()!=null)
                            {
                                writer.write(program.getInFilter().replaceAll("\\$in", memoryVars.get(program.getInput()).toString()));
                            }
                            else
                            {
                                writer.write(memoryVars.get(program.getInput()).toString());
                            }
                            writer.flush();
                            writer.close();
                        }

                        //Wait for stdout reader thread to end reading
                        try
                        {
                        stdoutGobbler.join();
                        }catch (InterruptedException e) {
                        }

                         if(memoryVars.containsKey(output))
                        {
                            //Read process output
                             StringBuilder outputBuilder = memoryVars.get(output);
                             outputBuilder.append(stdoutGobbler.getReadedStr());
                        }
                        if(p.waitFor()!=0)
                            throw new TranslationEngineException("Exit value of program "+programCommand+" != 0");

                        }
                        catch(IOException e)
                        {
                            //TODO:something
                        }

                        logger.trace("Daemon "+ this.getId() +". Translation "+element.getId()+". "+programCommand+" output: "+memoryVars.get(output).toString());
                        }
                    }
                }
            }
            input=output;

        }

        StringBuilder outputStr =memoryVars.get(-2);
        if(!translationEngine.getTranslationCore().isSeparateAfterDeformat())
        {
            outputStr.delete(0, outputStr.indexOf("\n")+1);
            outputStr.delete(outputStr.lastIndexOf("\n"), outputStr.length());
        }

        element.setTranslation(outputStr.toString());


        }
        catch(Exception e)
        {
            throw new TranslationEngineException(e);
        }
            finally
            {
                engineWriter.getRequestsBeforeCore().remove(element.getId());
                if (charactersPerTranslation.containsKey(element.getId())) {
                long translationSize = charactersPerTranslation.get(element.getId());
                readWriteLock.writeLock().lock();
                daemonInformation.setCharactersInside(daemonInformation.getCharactersInside() - translationSize);
                readWriteLock.writeLock().unlock();
                charactersPerTranslation.remove(element.getId());
            }
            
        }

    }

    public synchronized void assignQueueElement(QueueElement element) {
        readWriteLock.writeLock().lock();
        engineWriter.getRequestsBeforeCore().add(element.getId());
        daemonInformation.setCharactersInside(daemonInformation.getCharactersInside() + element.getText().length());
        readWriteLock.writeLock().unlock();
        charactersPerTranslation.put(element.getId(), (long) (element.getText().length()));
        element.setDaemon(this);
    }

    /**
     * Get number of characters currently being processed by the daemon. It is a
     * good way to measure daemon load
     *
     * @return
     */
    public long getCharactersInside() {

        readWriteLock.readLock().lock();
        long returnValue = daemonInformation.getCharactersInside();
        readWriteLock.readLock().unlock();

        return returnValue;
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

        ProcessFinder pf = new ProcessFinder(TranslationEnginePool.sigar);
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

        return null;
    }

    /**
     * Gets the % of system's CPU consumed by the Apertium process
     * @return % of system's CPU consumed by the Apertium process, or <code>null</code> if there is any error
     */
    public Double getCPUUsage() {
        String information = "";
        try {
            String[] command = {"getProcessInfo.sh", Integer.toString(daemonInformation.getPid())};
            Process p = Runtime.getRuntime().exec(command);
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

    public TranslationEngineInfo getTranslationEngine() {
        return translationEngine;
    }

    private class TrashSender implements Runnable
    {
        private int numberSent;
        private boolean stop;
        private BufferedWriter pWriter;

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
        QueueElement element=new QueueElement(1, translationEngine.getTranslationCore().getTrash(), Format.txt, this.getConfiguration().getLanguagePair(), Thread.currentThread(), null);
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
