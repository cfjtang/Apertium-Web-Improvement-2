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
package com.gsoc.apertium.translationengines.main;

import com.gsoc.apertium.translationengines.rmi.slave.ITranslationEngine;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gsoc.apertium.translationengines.daemon.ApertiumDaemon;
import com.gsoc.apertium.translationengines.daemon.ApertiumDaemonFactory;
import com.gsoc.apertium.translationengines.rmi.exceptions.NotAvailableDaemonException;
import com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException;
import com.gsoc.apertium.translationengines.exceptions.UnsupportedLanguagePairException;
import com.gsoc.apertium.translationengines.rmi.transferobjects.DaemonInformation;
import com.gsoc.apertium.translationengines.rmi.transferobjects.Format;
import com.gsoc.apertium.translationengines.rmi.transferobjects.LanguagePair;
import com.gsoc.apertium.translationengines.transferobjects.ServerCPUInformation;
import com.gsoc.apertium.translationengines.rmi.transferobjects.ServerInformationTO;
import com.gsoc.apertium.translationengines.rmi.transferobjects.ServerStatusTO;
import com.gsoc.apertium.translationengines.util.ServerUtil;
import com.gsoc.apertium.translationengines.util.StreamGobbler;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * Translation Engine implementation with a pool of Apertium daemons.<br/>
 * Reads the following properties from <code>configuration.properties</code>:
 * <ul>
 * <li><code>apertium_path</code>:Path where Apertium executable can be found</li>
 * <li><code>apertium_timeout</code>: Maximum time waiting for a translation from a daemon. Once this time is passed, an error is returned.</li>
 * <li><code>apertium_max_deformat</code>: Maximum number of deformatters running simultaneously</li>
 * <li><code>apertium_max_reformat</code>: Maximum number of reformatters running simultaneously</li>
 * <li><code>apertium_supported_pairs</code>: Comma-separated list of supported language pairs</li>
 *
 * </ul>
 */
public class ApertiumTranslationEnginePool implements ITranslationEngine, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2215612722973686962L;

    /**
     * Commmons-logging logger
     */
	static Log logger = LogFactory.getLog(ApertiumTranslationEnginePool.class);

    /**
     * Maximum time waiting for a translation from a daemon. Once this time is passed, an error is returned.
     */
	private long timeout;

    /**
     * Queue where all the translation requests are put. There is a thread that selects requests from
     * that queue and sends them to the right daemon.
     */
	private final BlockingQueue<ApertiumQueueElement> queue;

    /**
     * Selects requests from the queue and sends them to the right daemon.
     */
    private final DaemonSelector daemonSelector;

    /**
     * Running daemons
     */
	private final List<ApertiumDaemon> daemons;

    /**
     * Path where Apertium executable can be found
     */
	private String apertiumPath;

    /**
     * Id of the next translation requested.
     */
	private long translationId = 1;

    /**
     * Supported language pairs. When a trsnlation for a pair that doesn't belong to this set
     * is requested, a {@link UnsupportedLanguagePairException} is thrown.
     */
	private final Set<LanguagePair> supportedPairs;

    /**
     * Semaphore that limits the number of running deformatters
     */
	private final Semaphore deformatSemaphore;

    /**
     * Semaphore that limits the number of running reformatters
     */
	private final Semaphore reformatSemaphore;

    /**
     * Interface to get some OS specyfic data, like system load
     */
	private  OperatingSystemMXBean operativeSystemMXBean;

    /**
     * Static server information
     */
	private ServerInformationTO serverInformationTO;

    /**
     * Translation request that makes the thread running {@link DaemonSelector} stop.
     */
    private ApertiumQueueElement stopMark;
	
	/**
	 * Takes translations requests from a queue and sends them to the right daemon.
	 * If there is more than one daemon that can translate the request's language pair,
     * the request is sent to the less loaded daemon.
	 * 
	 */
	private class DaemonSelector extends Thread {

         boolean running =true;

        @Override
		public void run() {
           
			while (running) {
				
				//Take translation request from queue
				ApertiumQueueElement queueElement = null;
				try {
					queueElement = queue.take();
				} catch (InterruptedException e) {
					try {
						// Sleep to prevent high CPU consumption in case if
						// continuous exceptions
						Thread.sleep((long) (Math.random() + 1) * 1000);
					} catch (InterruptedException e1) {
					}
				}

				if (queueElement != null) {
					
                        if(queueElement!=stopMark)
                        {

                            //Check daemons
                            ApertiumDaemon lowerLanguageDaemon=null;
                            long lowerLanguageDaemonCharsInside = -1;
                            synchronized(daemons)
                            {
                                Iterator<ApertiumDaemon> iterator = daemons.iterator();
                                while(iterator.hasNext())
                                {
                                    ApertiumDaemon d = iterator.next();
                                    if(d.isCrashed())
                                        iterator.remove();
                                    else
                                    {
                                        //logger.debug("Daemon "+d.getId()+": chars inside: "+d.getCharactersInside());
                                        if(d.getPair().equals(queueElement.getLanguagePair()))
                                        {
                                            if(lowerLanguageDaemon==null)
                                            {
                                                lowerLanguageDaemon=d;
                                                lowerLanguageDaemonCharsInside=d.getCharactersInside();
                                            }
                                            else if(d.getCharactersInside()<lowerLanguageDaemonCharsInside)
                                            {
                                                lowerLanguageDaemon=d;
                                                lowerLanguageDaemonCharsInside=d.getCharactersInside();
                                            }
                                        }
                                    }
                                }
                            }

                            //Send translation to the less charged daemon
                            if(lowerLanguageDaemon!=null)
                            {
                                lowerLanguageDaemon.translateAsync(queueElement);
                                logger.debug("Sending translation "+queueElement.getId()+" "+queueElement.getLanguagePair()+" to daemon "+lowerLanguageDaemon.getId());
                            }
                            else
                            {
                                queueElement.setException(new NotAvailableDaemonException());
                                queueElement.getCaller().interrupt();
                            }

                        }
                        else
                            running=false;
				}
			}
             logger.debug("Finished Daemon Selector");
		}
	}

    private class TranslationCaller extends Thread
	{
		private String text;
		private LanguagePair pair;
        private List<Long> dictionaries;

		private TranslationEngineException exception;


		public TranslationCaller(String text, LanguagePair pair, List<Long> dictionaries) {
			super();
			this.text = text;
			this.pair = pair;
            this.dictionaries=dictionaries;
			this.exception=null;
		}



		@Override
		public void run(){
			try
			{
			translateText(text, pair,dictionaries);
			}
			catch (TranslationEngineException e) {
				exception=e;
			}
		}



		public TranslationEngineException getException() {
			return exception;
		}



		public void setException(TranslationEngineException exception) {
			this.exception = exception;
		}


	}
	
	/**
     * Creates a translation engine with no running daemons.
     */
	public ApertiumTranslationEnginePool() {
		
		queue = new LinkedBlockingQueue<ApertiumQueueElement>();
		stopMark=new ApertiumQueueElement(-1, "");
		
		/*
		 *  Read some configuration properties
		 */
		
		daemons = Collections.synchronizedList(new ArrayList<ApertiumDaemon>());
		supportedPairs = readSupportedPairs();
		
		
		apertiumPath = ServerUtil.readProperty("apertium_path");
		if (apertiumPath == null)
			apertiumPath = "/usr/local/bin";

		try {
			timeout = Long.parseLong(ServerUtil
					.readProperty("apertium_timeout"));
		} catch (Exception e) {
			timeout = 20000;
			logger.warn("Can't load apertium timeout from config file");
		}
		
		int maxDeformat = 3;
		try
		{
			maxDeformat=Integer.parseInt(ServerUtil.readProperty("apertium_max_deformat"));
		}
		catch (Exception e) {
			logger.warn("Can't load maximum number of deformatters from config file. Using defaults (3)", e);
		}
		
		int maxReformat = 3;
		try
		{
			maxReformat=Integer.parseInt(ServerUtil.readProperty("apertium_max_reformat"));
		}
		catch (Exception e) {
			logger.warn("Can't load maximum number of reformatters from config file. Using defaults (3)", e);
		}
		
		
		deformatSemaphore = new Semaphore(maxDeformat,true);
		reformatSemaphore = new Semaphore(maxReformat,true);

        serverInformationTO=new ServerInformationTO();
		serverInformationTO.setSupportedPairs(getSupportedPairs());
		
		operativeSystemMXBean=ManagementFactory.getOperatingSystemMXBean();
		
		//Start daemon selector thread
        daemonSelector=new DaemonSelector();
        daemonSelector.start();	
		
	}

    /**
     * Calculates server memory capacity, CPU capacity and maximum capacity per daemon.
     *
     */
	public void guessServerInformation()
	{
		//get server information
		serverInformationTO.setMemoryCapacity(computeServerFreeMemory());
		ServerCPUInformation cpuInfo=computeServerCpuCapacity();
		serverInformationTO.setCpuCapacity(cpuInfo.getCapacity());
		serverInformationTO.setCpuCapacityPerDaemon(cpuInfo.getMaxCapacityPerDaemon());
		
	}
	
	

    @Override
    public void stop()
    {
        logger.debug("Stopping..");
        synchronized(daemons)
        {
            Iterator<ApertiumDaemon> iterator = daemons.iterator();
            while(iterator.hasNext())
            {
                ApertiumDaemon d = iterator.next();
                d.stop();
            }
        }
        try
        {
            queue.put(stopMark);
        }
        catch(InterruptedException ie){}

        try
        {
         logger.debug("De-registering.");
         Registry registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
         registry.unbind(Main.rminame);
         logger.info("Server stopped successfully");
        }
        catch(Exception e)
        {
            logger.error("Exception stopping server", e);
        }
    }

    /**
     * Calculates server CPU information.<br/>
     * To calculate the cpu capacity, in es-ca text characters per second,
     * a long text is translated many times with 1, 2, 3, 4.. daemons, until
     * adding a new daemon doesn't increase the speed. To calculate the maximum capacity
     * per daemon, a long text is translated with a single daemon
     *
     * @return CPU information.
     */
	private ServerCPUInformation computeServerCpuCapacity()
	{
		long startTime,endTime,timeEllapsed;
		int optimumNumber=-1;
		int optimumCapacity=0;
		int capacity=0;
		int maxCapacityPerDaemon=0;
		
		LanguagePair pairesca=new LanguagePair("es","ca");
		String quijote = ServerUtil.readFileFromClasspath("/corpora/quijoteFragment.txt");
		int numChars = quijote.length();
		
		try
		{
			do
			{
				optimumNumber++;
				optimumCapacity=capacity;		
				int numDaemons= optimumNumber+1;
				DaemonInformation newDaemon= startDaemon(pairesca);
				TranslationCaller[] translateThreads = new TranslationCaller[numDaemons];
				for(int i=0; i<numDaemons;i++)
					translateThreads[i]=new TranslationCaller(quijote,pairesca,null);
				startTime=System.currentTimeMillis();
				for(int i=0; i<numDaemons;i++)
					translateThreads[i].start();
				for(int i=0; i<numDaemons;i++)
					translateThreads[i].join();
				endTime=System.currentTimeMillis();
				timeEllapsed=endTime-startTime;
				for(int i=0; i<numDaemons;i++)
					if(translateThreads[i].getException()!=null)
						throw translateThreads[i].getException();
				capacity=(int) ( (long)numDaemons*numChars*1000/timeEllapsed);
				if(numDaemons==1)
					maxCapacityPerDaemon=capacity;
				logger.debug("Number of daemons: "+numDaemons+" Number of chars: "+numChars+" Time ellapsed: "+timeEllapsed+"Capacity: "+capacity);
			}
			while(capacity*0.95>optimumCapacity);
			
			stopAllDaemonsLanguage(pairesca);
		
		return  new ServerCPUInformation(optimumCapacity,maxCapacityPerDaemon,optimumNumber);
		
		}
		catch (Exception e) {
			logger.error("Cannot guess cpu capacity",e);
			
			//return default value
			return new ServerCPUInformation(10000,10000,1);
		}
	}

    /**
     * Calculates server's free memory reading it from <code>/proc/meminfo</code>.
     * <br/>
     * If server is running on a 64-bit operative system, the value read from <code>/proc/meminfo</code>
     * is reduced using the ratio read from the configuration property <code>memoryrate_64bit</code> because
     * the same program consumes more memory running on 64-bit operative system than running on 32-bit ones.
     *
     *
     *
     * @return Server's free memory, in MegaBytes.
     */
	private int computeServerFreeMemory()
	{
		try
		{
			int freemem = readFromMemInfo("MemFree");
			
			//Guess if we are in a 64-bit system
			String[] cmd = {"uname","-m"};
			Process p =Runtime.getRuntime().exec(cmd);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line=reader.readLine();
			reader.close();
			if(line.trim().equals("x86_64"))
			{
				try
				{
				String strRatio = ServerUtil.readProperty("memoryrate_64bit");
				Double ratio= Double.parseDouble(strRatio);
				freemem = (int) (ratio*freemem);
				}
				catch (Exception e) {
					logger.error("Exception converting memory capacity", e);
				}
			}
			
			return freemem; 
		}
		catch (Exception e) {
			//return default memory capacity
			return 1000;
		}
	}

   /**
    *  Calculates server's total memory reading it from <code>/proc/meminfo</code>
    *
    * @return Server's total memory, in MegaBytes.
    *
    */
	private int computeServerTotalMemory()
	{
		try
		{
		return readFromMemInfo("MemTotal");
		}
		catch (Exception e) {
			//return default memory capacity
			return 0;
		}
	}

    /**
     * Reads a property from <code>/proc/meminfo</code> and converts to MegaBytes its value in KiloBytes
     * @param property Name of the property to read
     * @return property value, in megaBytes
     * @throws java.lang.Exception
     */
	private int readFromMemInfo(String property) throws Exception
	{
		int memoryCapacity=0;
		BufferedReader reader=null;
		try
		{
		final Pattern pattern = Pattern.compile("^"+property+":[ \t]+([0-9]+)[ \t]*kB");
		reader = new BufferedReader(new FileReader("/proc/meminfo"));
		String line;
		while((line=reader.readLine())!=null)
		{
			Matcher matcher = pattern.matcher(line);
			if(matcher.matches())
			{
				memoryCapacity=Integer.parseInt(matcher.group(1))/1024;
				break;
			}
		}
		}
		catch (Exception e) {
			logger.error("Cannot guess memory capacity",e);
			throw new Exception();
		}
		finally
		{
			try
			{
		reader.close();
			}catch (Exception e) {
			}
		}
		
		return memoryCapacity;
	}

    /**
     * Reads the supported language pairs from <code>configuration.properties</code>
     * @return Set  of supported pairs
     */
	private Set<LanguagePair> readSupportedPairs() {
		Set<LanguagePair> supPairs = new HashSet<LanguagePair>();

		// We don't ask apertium about supported pairs because it could be very inefficient

		String strPairs = ServerUtil.readProperty("apertium_supported_pairs");
		if (strPairs != null) {
			String[] pairs = strPairs.split(",");
			if (pairs != null)
				for (String pair : pairs)
				{
					try
					{
					supPairs.add(new LanguagePair(pair,"-".toCharArray()));
					}
					catch (Exception e) {
						
					}
				}
		}

		return supPairs;
	}

    @Override
	public String translateHTML(String sourceHtml, LanguagePair pair, List<Long> dictionaries) throws TranslationEngineException
			 {

		if (!supportedPairs.contains(pair))
			throw new UnsupportedLanguagePairException();

		try {
			
			 return reformatHtml(translate(deformatHtml(sourceHtml), pair, dictionaries));
		} catch (Exception e) {
			logger.error("Couldn't perform translation Unexpected exception", e);
			throw new TranslationEngineException();
		}
	}

    @Override
	public  String translateText(String sourceText, LanguagePair pair, List<Long> dictionaries)
			throws TranslationEngineException {

		if (!supportedPairs.contains(pair))
			throw new UnsupportedLanguagePairException();

		try {
			
			return reformatTxt(translate(deformatTxt(sourceText), pair,dictionaries));
		}
		catch(TranslationEngineException te)
		{
			throw te;
		}
		catch (Exception e) {
			logger.error("Couldn't perform translation: Unexpected exception", e);
			throw new TranslationEngineException();
		}
	}

	/**
     * Translates a piece of text already deformatted
     *
     * @param sourceText Source deformatted text
     * @param pair language pair: source language and target language
     * @return Translated deformatted text
     * @throws java.lang.Exception If there is aerror translating the text
     */
	private String translate(String sourceText, LanguagePair pair, List<Long> dictionaries)
			throws Exception {
		
		
		/**
		 * 
		 * This piece of commented code invokes the whole Apertium
		 * pipeline to perform the translation. It is only use for
		 * testing purposes.
		 * 
		 */
		/*
		String command[];
		command = new String[4];
		command[0] = apertiumPath+"/apertium";
		command[1] = "-f";
		command[2] = "none";
		command[3] = pair.getSource()+"-"+pair.getTarget();
		Process p =Runtime.getRuntime().exec(command);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		writer.write(sourceText);
		writer.flush();
		writer.close();
		StringBuilder builder=new StringBuilder();
		String line;
		while((line=reader.readLine())!=null)
		{
			builder.append(line);
			builder.append("\n");
		}
		reader.close();
		return builder.toString();
		*/
		
		
		
		 
		String result;
		long id=-1;
		 
		//logger.debug("Translating: "+sourceText);
		
		
		ApertiumQueueElement queueElement = new ApertiumQueueElement(id, sourceText, 
				pair,Thread.currentThread(), dictionaries);
		synchronized (this) {
			id = getNewId();
			queueElement.setId(id);
			queue.put(queueElement);
		}
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			if(queueElement.getException()!=null)
			{
				throw queueElement.getException();
			}
			result = queueElement.getTranslation();
			return result;
		}

		throw new Exception(": Timeout reached while waiting for translation "
				+ id);
	}

    /**
     * Generates an unused id for a translation request
     * @return Unused ID for a translation request
     */
	private long getNewId() {
		long newId = translationId;
		translationId++;
		return newId;
	}

    /**
     * Gets the set of supported language pairs already read from <code>configuration.properties</code>
     * @return
     */
	private Set<LanguagePair> getSupportedPairs() {
		return supportedPairs;
	}

    /**
     * Calls <code>apertium-destxt</code> to deformat a piece of text.
     * @param text Text to deformat
     * @return Deformatted text
     * @throws java.io.IOException If there is a en error calling apertium-destxt
     */
	private String deformatTxt(String text) throws  IOException
	{
		
		//Acquire semaphre
		try {
			deformatSemaphore.acquire();
		} catch (InterruptedException e) {		
		}
		
		StreamGobbler stdoutGobbler;
		
		try
		{
			
		//Execute command
		String command=apertiumPath+"/apertium-destxt";
		Process p =Runtime.getRuntime().exec(command);
		
		//Create threads for reading stdout and stderr
		stdoutGobbler = new StreamGobbler(p.getInputStream());
		StreamGobbler stderrGobbler = new StreamGobbler(p.getErrorStream());
		stdoutGobbler.start();
		stderrGobbler.start();
		
		//Write source text
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		writer.write(text);
		writer.flush();
		writer.close();
		
		//Wait for stdout reader thread to end reading
		try
		{
		stdoutGobbler.join();
		}catch (InterruptedException e) {
		}
		
		}
		//release semaphore
		finally{
		deformatSemaphore.release();
		}
		
		//return string read by stdout reader 
		return stdoutGobbler.getReadedStr();
		
		
		//return detxtDaemon.reDeFormat(text, timeout);
	}

    /**
     * Calls <code>apertium-deshtml</code> to deformat a piece of html.
     * @param html Html to deformat
     * @return Deformatted html
     * @throws java.io.IOException If there is a en error calling apertium-deshtml
     */
	private String deformatHtml(String html) throws IOException
	{
		
		//Acquire semaphre
		try {
			deformatSemaphore.acquire();
		} catch (InterruptedException e) {		
		}
		
		StreamGobbler stdoutGobbler;
		
		try
		{
			
		//Execute command
		String command=apertiumPath+"/apertium-deshtml";
		Process p =Runtime.getRuntime().exec(command);
		
		//Create threads for reading stdout and stderr
		stdoutGobbler = new StreamGobbler(p.getInputStream());
		StreamGobbler stderrGobbler = new StreamGobbler(p.getErrorStream());
		stdoutGobbler.start();
		stderrGobbler.start();
		
		//Write source text
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		writer.write(html);
		writer.flush();
		writer.close();
		
		//Wait for stdout reader thread to end reading
		try
		{
		stdoutGobbler.join();
		}catch (InterruptedException e) {
		}
		
		}
		//release semaphore
		finally{
		deformatSemaphore.release();
		}
		
		//return string read by stdout reader 
		return stdoutGobbler.getReadedStr();
		
		//return dehtmlDaemon.reDeFormat(html, timeout);
	}

    /**
     * Reformats a flow of deformatted text calling <code>apertium-retxt</code>
     *
     * @param flow Deformatted flow
     * @return Formatted text
     * @throws java.io.IOException If there is an error calling apertium-retxt
     */
	private String reformatTxt(String flow) throws  IOException
	{
		
		//Acquire semaphre
		try {
			reformatSemaphore.acquire();
		} catch (InterruptedException e) {		
		}
		
		StreamGobbler stdoutGobbler;
		
		try
		{
			
		//Execute command
		String command=apertiumPath+"/apertium-retxt";
		Process p =Runtime.getRuntime().exec(command);
		
		//Create threads for reading stdout and stderr
		stdoutGobbler = new StreamGobbler(p.getInputStream());
		StreamGobbler stderrGobbler = new StreamGobbler(p.getErrorStream());
		stdoutGobbler.start();
		stderrGobbler.start();
		
		//Write source text
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		writer.write(flow);
		writer.flush();
		writer.close();
		
		//Wait for stdout reader thread to end reading
		try
		{
		stdoutGobbler.join();
		}catch (InterruptedException e) {
		}
		
		}
		//release semaphore
		finally{
		reformatSemaphore.release();
		}
		
		//return string read by stdout reader 
		return stdoutGobbler.getReadedStr();
		
		//return retxtDaemon.reDeFormat(flow, timeout);
	}

     /**
     * Reformats a flow of deformatted html calling <code>apertium-rehtml</code>
     *
     * @param flow Deformatted flow
     * @return Formatted html
     * @throws java.io.IOException If there is an error calling apertium-rehtml
     */
	private String reformatHtml(String flow) throws  IOException
	{
		
		//Acquire semaphre
		try {
			reformatSemaphore.acquire();
		} catch (InterruptedException e) {		
		}
		
		StreamGobbler stdoutGobbler;
		
		try
		{
			
		//Execute command
		String command=apertiumPath+"/apertium-rehtml";
		Process p =Runtime.getRuntime().exec(command);
		
		//Create threads for reading stdout and stderr
		stdoutGobbler = new StreamGobbler(p.getInputStream());
		StreamGobbler stderrGobbler = new StreamGobbler(p.getErrorStream());
		stdoutGobbler.start();
		stderrGobbler.start();
		
		//Write source text
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		writer.write(flow);
		writer.flush();
		writer.close();
		
		//Wait for stdout reader thread to end reading
		try
		{
		stdoutGobbler.join();
		}catch (InterruptedException e) {
		}
		
		}
		//release semaphore
		finally{
		reformatSemaphore.release();
		}
		
		//return string read by stdout reader 
		return stdoutGobbler.getReadedStr();
	
		
		//return rehtmlDaemon.reDeFormat(flow, timeout);
	}

	@Override
	public DaemonInformation startDaemon(LanguagePair pair) throws RemoteException,
			TranslationEngineException {
		
		//Check if pair is supported
		try
		{
			if(!getSupportedPairs().contains(pair))
				throw new UnsupportedLanguagePairException();
		}
		catch (Exception e) {
			throw new UnsupportedLanguagePairException();
		}
		
		//Check daemon amount limit
		//if(daemons.size()>=maxDaemons)
		//	throw new MaxDaemonsExceededException();
		
		//Create daemon
		ApertiumDaemon daemon =ApertiumDaemonFactory.getInstance().newDaemon(pair);
		daemon.start();
		daemons.add(daemon);
		
		return daemon.getDaemonInformation();
		
		
	}

	@Override
	public void stopDaemon(long daemonId) throws RemoteException,
			TranslationEngineException {
		
		boolean removed=false;
		Iterator<ApertiumDaemon> iterator = daemons.iterator();
		while(iterator.hasNext() && !removed)
		{
			ApertiumDaemon daemon = iterator.next();
			if(daemon.getId()==daemonId)
			{
				daemon.stop();
				iterator.remove();
				removed=true;
			}
		}
		
	}

	@Override
	public ServerInformationTO getServerInformation() throws RemoteException {
		return serverInformationTO;
	}

    public void setServerInformation(ServerInformationTO serverInformationTO) {
        this.serverInformationTO = serverInformationTO;
    }



	@Override
	public ServerStatusTO getServerStatus() throws RemoteException,
			TranslationEngineException {
        if(!daemonSelector.running)
            throw new TranslationEngineException("Server stopped");
		ServerStatusTO serverStatusTO = new ServerStatusTO();
		List<DaemonInformation> daemonList = new ArrayList<DaemonInformation>();
		
		for(ApertiumDaemon daemon: daemons)
		{
			if(!daemon.isCrashed())
				daemonList.add(daemon.getDaemonInformation());
		}
		serverStatusTO.setDaemonsInformation(daemonList);
		
		serverStatusTO.setLoad(0);
		double load = operativeSystemMXBean.getSystemLoadAverage();
		if(load>=0)
			serverStatusTO.setLoad(load);
		
		return serverStatusTO;
	}

	@Override
	public void stopAllDaemonsLanguage(LanguagePair pair)
			throws RemoteException, TranslationEngineException {
		synchronized (daemons) {
			Iterator<ApertiumDaemon> iterator = daemons.iterator();
			while(iterator.hasNext())
			{
				ApertiumDaemon daemon = iterator.next();
				if(daemon.getPair().equals(pair))
				{
					daemon.stop();
					iterator.remove();
				}
			}
		}
	}

    /**
     * Comparates the translation speed (in characters per second) of the different language
     * pairs, an returns the ratio between the different pairs and the reference pair. The ratio
     * is calculated as reference_speed / pair_speed, so a ratio greater than 1 means than the pair
     * is slower than the reference pair.<br/>
     * The comparation is made by translating different texts and measuring the translation time.
     * Of course, the fact the texts in different languages have different lengths is taken into account.
     *
     * @param comparationPair Reference language pair
     * @return Ratio between the supported pairs and the reference language pair
     */
	public Map<LanguagePair, Double> calculateLanguagesSpeedRatio(LanguagePair comparationPair) {
		Map<LanguagePair,Double> ratios = new HashMap<LanguagePair, Double>();
		String gplTranslation,udhrTranslation;
		
		Set<LanguagePair> supPairs =getSupportedPairs();
		long t1,t2;
		try
		{
		logger.info("Calculating pairs speed. Comparing with "+comparationPair);
		DaemonInformation daemonComparation =startDaemon(comparationPair);
		String uhdr = ServerUtil.readFileFromClasspath("/corpora/UDHR_"+comparationPair.getSource()+".txt");
		String gpl = ServerUtil.readFileFromClasspath("/corpora/gpl_"+comparationPair.getSource()+".txt");
		if(uhdr==null || gpl==null)
			throw new Exception("Cannot read corpora");
		
		//Send these translation first to ensure daemon is up
		translateText(uhdr, comparationPair,null);
		translateText(gpl, comparationPair,null);
		
		t1 = System.currentTimeMillis();
		translateText(uhdr, comparationPair,null);
		translateText(gpl, comparationPair,null);
		t2=System.currentTimeMillis();
		stopAllDaemonsLanguage(comparationPair);
		
		long comparationTime=t2-t1;
		double comparationSpeed = (uhdr.length()+gpl.length())*1000 / (double) comparationTime; 
		
		for(LanguagePair pair: supPairs)
		{
			udhrTranslation="";
			gplTranslation="";
			logger.info("Calculating "+pair+" speed");
			if(pair.equals(comparationPair))
				ratios.put(pair, 1d);
			else
			{
			try
			{
			daemonComparation =startDaemon(pair);
			uhdr = ServerUtil.readFileFromClasspath("/corpora/UDHR_"+pair.getSource()+".txt");
			gpl = ServerUtil.readFileFromClasspath("/corpora/gpl_"+pair.getSource()+".txt");
			if(uhdr==null && gpl==null)
				throw new Exception("Cannot read corpora");
			//Send these translation first to ensure daemon is up
			if(uhdr!=null)
				translateText(uhdr, pair, null);
			if(gpl!=null)
				translateText(gpl, pair, null);
			
			t1 = System.currentTimeMillis();
			if(uhdr!=null)
				udhrTranslation=translateText(uhdr, pair, null);
			if(gpl!=null)
				gplTranslation=translateText(gpl, pair, null);
			t2=System.currentTimeMillis();
			logger.debug(pair+" UDHR Translation: "+udhrTranslation+" GPL translation: "+gplTranslation);
			
			 long time = t2-t1;
			 double speed=((uhdr!=null ? uhdr.length(): 0 ) + ( gpl!=null ? gpl.length() : 0))*1000 / (double) time;
			 double ratio = comparationSpeed/speed;
			 ratios.put(pair, ratio);
			}
			catch (Exception e) {
				logger.error("Exception calculating "+pair+" spped",e);
			}
			finally{
				stopAllDaemonsLanguage(pair);
			}
			}
		}
		
		}
		catch (Exception e) {
			logger.error("Error calculating comparation language speed",e);
		}
		logger.info("Finished  pair speed calculation");
		return ratios;
	}

    /**
     * Comparates  the trsnlation speed of the different formats and returns the ratio between the different pairs and the text format. 
     * The ratio is calculated as text_speed / format_speed, so a ratio greater than 1 means than the format
     * is slower than the text format.<br/>
     * The comparation is made by translating different texts and measuring the translation time.
     * Of course, the fact the texts in different formats have different lengths is taken into account.
     *
     * @return ratio between the diffrent formats and text format
     */
	public Map<Format,Double> calculateFormatSpeedRatio()
	{
		Format comparationFormat=Format.text;
		Map<Format,Double> ratioMap = new HashMap<Format, Double>();
		long t1,t2;
		LanguagePair referencePair = new LanguagePair("es","ca");
		
		logger.info("Calculating formats speed. Comparing with "+comparationFormat);
		try
		{
		
		DaemonInformation daemonComparation =startDaemon(referencePair);
		String quijoteText = ServerUtil.readFileFromClasspath("/corpora/quijoteFragment.txt");
		String quijoteHtml = ServerUtil.readFileFromClasspath("/corpora/quijote-1a22.htm");
		
		//Calculate reference speed
		
		//ensure daemon is working
		translateText(quijoteText, referencePair, null);
		
		t1=System.currentTimeMillis();
		translateText(quijoteText, referencePair, null);
		t2=System.currentTimeMillis();
		
		double txtSpeed =   (quijoteText.length()*1000/ (double) (t2-t1));
		
		for(Format format: Format.values())
		{
			logger.info("Calculating "+format+" speed");
			if(format.equals(comparationFormat))
				ratioMap.put(format, 1d);
			else
			{
				if(format.equals(Format.html))
				{
					t1=System.currentTimeMillis();
					translateHTML(quijoteHtml, referencePair, null);
					t2=System.currentTimeMillis();
					
					double speed = quijoteHtml.length()*1000/ (double) (t2-t1);
					double ratio = txtSpeed/speed;
					ratioMap.put(format, ratio);
				}
					
				
			}
		}
		}
		catch (Exception e) {
			logger.error("Error calculating formats speed ratio",e);
		}
		finally{
			try
			{
			stopAllDaemonsLanguage(referencePair);
			}
			catch (Exception e) {}
		}
		
		logger.info("Finished  format speed calculation");
		return ratioMap;
	}

    /**
     * Calculates the memory Apertium requires to translate with each language pair.
     * <br/>
     * This method launch a daemon for each supported language pair, translates with it
     * and then measures its memory consumption.
     *
     * @return Memory requirements of supported pairs
     */
	public Map<LanguagePair, Integer> calculateMemoryRequirements() {
		Map<LanguagePair,Integer> resultMap = new HashMap<LanguagePair, Integer>();
		logger.info("Calculating pairs memory requirements.");
		
		//get machine memory capacity
		int memoryCapacity = computeServerTotalMemory();
		
		logger.info("Machine capacity: "+memoryCapacity);
		
		if(memoryCapacity>0)
		{
			for(LanguagePair pair: getSupportedPairs())
			{
				logger.info("Calculating "+pair.toString()+" memory requirements:");
				try
				{
				DaemonInformation daemonInfo=startDaemon(pair);
                String uhdr = ServerUtil.readFileFromClasspath("/corpora/UDHR_"+pair.getSource()+".txt");
                translateText(uhdr, pair, null);
                synchronized(daemons)
                {
                    for(ApertiumDaemon daemon: daemons)
                        if(daemon.getId()==daemonInfo.getId())
                        {                           
                            Double usage=daemon.getMemoryUsage();
                            if(usage!=null)
                            {
                                int require=(int) (usage/100.0*memoryCapacity);
                                logger.info("Usage: "+usage+" Memory requirement: "+require);
                                resultMap.put(pair, require);
                            }
                        }
                    }
                }
				catch (Exception e) {
					logger.error("Exception calculating "+pair+" memory requirement.", e);
				}
				finally
				{
					try
					{
					stopAllDaemonsLanguage(pair);
					}
					catch (Exception e) {
					}
				}
			}
		}
		logger.info("Finished  pairs memory requirements calculation");
		return resultMap;
	}

    /**
     * Measures and logs the time needed to translate the given files.
     *
     * @param pair Language pair the files wikll be translates with-
     * @param filenames Names of the files to be translated
     */
	public void testTime(LanguagePair pair, String[] filenames)
	{
		try
		{
		long startTime,endTime;
		startDaemon(pair);
		List<String> filesContent = new ArrayList<String>();
		for(String filename: filenames)
		{
			String content = ServerUtil.readFileFromFileSystem(filename);
			if(content!=null)
				filesContent.add(content);
		}
		
		startTime = System.currentTimeMillis();
		for(String text: filesContent)
			translateText(text, pair, null);
		endTime =System.currentTimeMillis();
		
		float time = (float) ((endTime-startTime) / 1000.0);
		logger.info("Total time: "+ time);
		
		stopAllDaemonsLanguage(pair);
		
		}
		catch (Exception e) {
			logger.error("Error calculating time",e);
		}
		
	}

	@Override
	public void startDaemons(LanguagePair pair, int numInstances)
			throws RemoteException, TranslationEngineException {
		for(int i=0;i<numInstances;i++)
			startDaemon(pair);
		
	}

	@Override
	public void stopDaemons(LanguagePair pair, int numInstances)
			throws RemoteException, TranslationEngineException {
		int stopped=0;
		synchronized (daemons) {
			Iterator<ApertiumDaemon> iterator = daemons.iterator();
			while(iterator.hasNext() && stopped<numInstances)
			{
				ApertiumDaemon daemon = iterator.next();
				if(daemon.getPair().equals(pair))
				{
					daemon.stop();
					iterator.remove();
					stopped++;
				}
			}
		}
		
	}

}
