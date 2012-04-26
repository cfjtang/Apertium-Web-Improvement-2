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
package org.scalemt.main;

import org.scalemt.rmi.slave.ITranslationEngine;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.scalemt.daemon.Daemon;
import org.scalemt.daemon.DaemonFactory;
import org.scalemt.daemon.TranslationEngineInfo;
import org.scalemt.rmi.exceptions.NotAvailableDaemonException;
import org.scalemt.rmi.exceptions.TranslationEngineException;
import org.scalemt.exceptions.UnsupportedLanguagePairException;
import org.scalemt.rmi.transferobjects.DaemonConfiguration;
import org.scalemt.rmi.transferobjects.DaemonInformation;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.transferobjects.ServerCPUInformation;
import org.scalemt.rmi.transferobjects.ServerInformationTO;
import org.scalemt.rmi.transferobjects.ServerStatusTO;
import org.scalemt.util.ServerUtil;
import java.util.HashSet;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.scalemt.rmi.transferobjects.AdditionalTranslationOptions;
import org.scalemt.rmi.transferobjects.BinaryDocument;
import org.scalemt.rmi.transferobjects.Content;
import org.scalemt.rmi.transferobjects.TextContent;


/**
 * Translation Engine implementation with a pool of translation engine daemons.<br/>
 * Reads the following properties from <code>configuration.properties</code>:
 * <ul>
 * <li><code>request_timeout</code>: Maximum time waiting for a translation from a daemon. Once this time is passed, an error is returned.</li>*
 * </ul>
 */
public class TranslationEnginePool implements ITranslationEngine, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2215612722973686962L;

    /**
     * Commmons-logging logger
     */
	static Log logger = LogFactory.getLog(TranslationEnginePool.class);

    /**
     * Maximum time waiting for a translation from a daemon. Once this time is passed, an error is returned.
     */
	private long timeout;

    /**
     * Queue where all the translation requests are put. There is a thread that selects requests from
     * that queue and sends them to the right daemon.
     */
       //private final BlockingQueue<QueueElement> queue;

    /**
     * Selects requests from the queue and sends them to the right daemon.
     */
      //private final DaemonSelector daemonSelector;

    /**
     * Running daemons
     */
	private final List<Daemon> daemons;

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
//private final Semaphore deformatSemaphore;

    /**
     * Semaphore that limits the number of running reformatters
     */
//	private final Semaphore reformatSemaphore;

    /**
     * Interface to get some OS specyfic data, like system load
     */
	private  OperatingSystemMXBean operativeSystemMXBean;

    /**
     * Static server information
     */
	private ServerInformationTO serverInformationTO;

        /**
         * Class for guessing system information
         */
        public static Sigar sigar = new Sigar();

    /**
     * Translation request that makes the thread running {@link DaemonSelector} stop.
     */
    private QueueElement stopMark;

    private Daemon chooseDaemon(QueueElement queueElement) {
         synchronized(daemons)
            {
                Daemon lowerLanguageDaemon=null;
                long lowerLanguageDaemonCharsInside = -1;

                    Iterator<Daemon> iterator = daemons.iterator();
                    while(iterator.hasNext())
                    {
                        Daemon d = iterator.next();
                       // if(d.isCrashed())
                       //     iterator.remove();
                       // else
                        {
                            //logger.debug("Daemon "+d.getId()+": chars inside: "+d.getCharactersInside());
                            if(d.getConfiguration().getLanguagePair().equals(queueElement.getLanguagePair()) && d.getConfiguration().getFormats().contains(queueElement.getFormat()))
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


                //Send translation to the less charged daemon
                if(lowerLanguageDaemon!=null)
                {
                    lowerLanguageDaemon.assignQueueElement(queueElement);
                    logger.debug("Sending translation "+queueElement.getId()+" "+queueElement.getLanguagePair()+" to daemon "+lowerLanguageDaemon.getId());
                }
                return lowerLanguageDaemon;
            }
    }
	
	/**
	 * Takes translations requests from a queue and sends them to the right daemon.
	 * If there is more than one daemon that can translate the request's language pair,
     * the request is sent to the less loaded daemon.
	 * 
	 */
    /*
	private class DaemonSelector extends Thread {

         boolean running =true;

        @Override
		public void run() {
           
			while (running) {
				
				//Take translation request from queue
				QueueElement queueElement = null;
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
                            Daemon lowerLanguageDaemon=null;
                            long lowerLanguageDaemonCharsInside = -1;
                            synchronized(daemons)
                            {
                                Iterator<Daemon> iterator = daemons.iterator();
                                while(iterator.hasNext())
                                {
                                    Daemon d = iterator.next();
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
                                lowerLanguageDaemon.assignQueueElement(queueElement);
                                logger.debug("Sending translation "+queueElement.getId()+" "+queueElement.getLanguagePair()+" to daemon "+lowerLanguageDaemon.getId());
                            }
                            else
                            {
                                queueElement.setException(new NotAvailableDaemonException());
                            }
                            queueElement.getCaller().interrupt();

                        }
                        else
                            running=false;
				}
			}
             logger.debug("Finished Daemon Selector");
		}
	}
        */
    private class TranslationCaller extends Thread
	{
		private Content text;
		private LanguagePair pair;
        private AdditionalTranslationOptions additionalTranslationOptions;

		private TranslationEngineException exception;


		public TranslationCaller(Content text, LanguagePair pair, AdditionalTranslationOptions to) {
			super();
			this.text = text;
			this.pair = pair;
                        this.additionalTranslationOptions=to;
			this.exception=null;
		}



		@Override
		public void run(){
			try
			{
			translate(text, pair,additionalTranslationOptions);
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
	public TranslationEnginePool() {
		
		stopMark=new QueueElement(-1, null);
		
		/*
		 *  Read some configuration properties
		 */
		
		daemons = Collections.synchronizedList(new ArrayList<Daemon>());
                supportedPairs = readSupportedPairs();

		
		apertiumPath = ServerUtil.readProperty("apertium_path");
		if (apertiumPath == null)
			apertiumPath = "/usr/local/bin";

		try {
			timeout = Long.parseLong(ServerUtil
					.readProperty("request_timeout"));
		} catch (Exception e) {
			timeout = 20000;
			logger.warn("Can't load request timeout from config file");
		}

                /*
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
		*/
		
		//deformatSemaphore = new Semaphore(maxDeformat,true);
		//reformatSemaphore = new Semaphore(maxReformat,true);

        serverInformationTO=new ServerInformationTO();
		serverInformationTO.setSupportedPairs(getSupportedPairs());
		serverInformationTO.setSupportedConfigs(DaemonFactory.getInstance().getSupportedConfigs());
		operativeSystemMXBean=ManagementFactory.getOperatingSystemMXBean();
		
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
                serverInformationTO.setTrashNeededToFlush(computeTrashNeededToFlush());
		
	}
	
	

    @Override
    public void stop()
    {
        logger.debug("Stopping..");
        synchronized(daemons)
        {
            Iterator<Daemon> iterator = daemons.iterator();
            while(iterator.hasNext())
            {
                Daemon d = iterator.next();
                d.stop();
            }
        }
    }

    private Map<LanguagePair,Integer> computeTrashNeededToFlush()
    {
        Map<LanguagePair,Integer> returnMap = new HashMap<LanguagePair, Integer>();

        Set<LanguagePair> supportedPairs = getSupportedPairs();

        for(LanguagePair p: supportedPairs)
        {
            DaemonConfiguration dc = DaemonFactory.getInstance().searchConfiguration(p, Format.txt);
            Daemon d = DaemonFactory.getInstance().newDaemon(dc);
            TranslationEngineInfo e = d.getTranslationEngine();
            if(e.getTranslationCore().getTrash()!=null)
            {
                returnMap.put(p,d.computeThrashNeededToFlush());
            }
        }


        return returnMap;
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
		String quijote = ServerUtil.readFileFromClasspath("/corpora/quijoteFragmentBig.txt");
		int numChars = quijote.length();
		DaemonConfiguration cd = DaemonFactory.getInstance().searchConfiguration(pairesca, Format.txt);

		try
		{
			do
			{
				optimumNumber++;
				optimumCapacity=capacity;		
				int numDaemons= optimumNumber+1;
                                
				DaemonInformation newDaemon= startDaemon(cd);
				TranslationCaller[] translateThreads = new TranslationCaller[numDaemons];
				for(int i=0; i<numDaemons;i++)
					translateThreads[i]=new TranslationCaller(new TextContent(Format.txt,quijote),pairesca, new AdditionalTranslationOptions());
                                translate(new TextContent(Format.txt,quijote.substring(0, Math.max(quijote.length()/10000,1))), pairesca, new AdditionalTranslationOptions());
				startTime=System.currentTimeMillis();
				for(int i=0; i<numDaemons;i++)
					translateThreads[i].start();
				for(int i=0; i<numDaemons;i++)
					translateThreads[i].join();
				endTime=System.currentTimeMillis();
				timeEllapsed=endTime-startTime;
                                logger.trace("Time ellapsed calculating CPU capacity : "+timeEllapsed);
				for(int i=0; i<numDaemons;i++)
					if(translateThreads[i].getException()!=null)
						throw translateThreads[i].getException();
				capacity=(int) ( (long)numDaemons*numChars*1000/timeEllapsed);
				if(numDaemons==1)
					maxCapacityPerDaemon=capacity;
				logger.debug("Number of daemons: "+numDaemons+" Number of chars: "+numChars+" Time ellapsed: "+timeEllapsed+"Capacity: "+capacity);
			}
			while(capacity*0.95>optimumCapacity);
			
			stopAllDaemonsLanguage(cd);
		
		return  new ServerCPUInformation(optimumCapacity,maxCapacityPerDaemon,optimumNumber);
		
		}
		catch (Exception e) {
			logger.error("Cannot guess cpu capacity",e);
			
			//return default value
			return new ServerCPUInformation(10000,10000,1);
		}
	}

    /**
     * Calculates server's free memory
     * <br/>
     * If server is running on a 64-bit operative system, the value
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
                long mem=sigar.getMem().getActualFree()/1024/1024;
                return (int) mem;
            }
            catch(SigarException e)
            {
                logger.error("Cannot determine servers free memory",e);
                //return default memory capacity
                return 1000;
            }
            
	}

   /**
    *  Calculates server's total memory
    *
    * @return Server's total memory, in MegaBytes.
    *
    */
	private int computeServerTotalMemory()
	{
             try
            {
                long mem=sigar.getMem().getTotal()/1024/1024;
                return (int) mem;
            }
            catch(SigarException e)
            {
                logger.error("Cannot determine server total memory",e);
                //return default memory capacity
                return 1000;
            }
	}

    /**
     * Reads a property from <code>/proc/meminfo</code> and converts to MegaBytes its value in KiloBytes
     * @param property Name of the property to read
     * @return property value, in megaBytes
     * @throws java.lang.Exception
     */
       /*
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
        
        */

    /**
     * Reads the supported language pairs
     * @return Set  of supported pairs
     */
	private Set<LanguagePair> readSupportedPairs() {

                /*
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

                 */

                Set<LanguagePair> supPairs = new HashSet<LanguagePair>();
                for(DaemonConfiguration dc: DaemonFactory.getInstance().getSupportedConfigs())
                {
                    supPairs.add(dc.getLanguagePair());
                }
                return supPairs;
	}

   

	/**
     * Translates 
     *
     * @param sourceText Source deformatted text
     * @param pair language pair: source language and target language
     * @return Translated deformatted text
     * @throws java.lang.Exception If there is aerror translating the text
     */
    @Override
    public Content translate(Content source,LanguagePair pair, AdditionalTranslationOptions to)
			throws TranslationEngineException {
		
		
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

                QueueElement queueElement = new QueueElement(getNewId(), source,pair,Thread.currentThread(), to);
                Daemon d = chooseDaemon(queueElement);
		if(d==null)
                {
                    logger.error("Not available daemon for queue element from pair "+pair.toString());
                    throw new NotAvailableDaemonException();
                }

                
                d.translate(queueElement, timeout);
                return queueElement.getTranslation();
                		
	}

    /**
     * Generates an unused id for a translation request
     * @return Unused ID for a translation request
     */
	private synchronized long getNewId() {
		long newId = translationId;
		translationId++;
		return newId;
	}

    /**
     * Gets the set of supported language pairs already read from <code>configuration.properties</code>
     * @return
     */
	private Set<LanguagePair> getSupportedPairs() {
            logger.debug("SUpported pairs: "+supportedPairs);
		return supportedPairs;
	}


	@Override
	public DaemonInformation startDaemon(DaemonConfiguration c) throws RemoteException,
			TranslationEngineException {
		
		//Check if pair is supported
		try
		{
                    logger.debug("looking for "+c+" in "+ DaemonFactory.getInstance().getSupportedConfigs());
			if(!DaemonFactory.getInstance().getSupportedConfigs().contains(c))
				throw new UnsupportedLanguagePairException();
		}
		catch (Exception e) {
			throw new UnsupportedLanguagePairException();
		}
		
		//Check daemon amount limit
		//if(daemons.size()>=maxDaemons)
		//	throw new MaxDaemonsExceededException();
		
		//Create daemon
		Daemon daemon =DaemonFactory.getInstance().newDaemon(c);
                if(getServerInformation().getTrashNeededToFlush()!=null)
                {
                    Integer tr =getServerInformation().getTrashNeededToFlush().get(c.getLanguagePair());
                    if(tr!=null)
                        daemon.setTrashNeededToFlush(tr.intValue());
                }
		daemon.start();
		daemons.add(daemon);
		
		return daemon.getDaemonInformation();
		
		
	}

	@Override
	public void stopDaemon(long daemonId) throws RemoteException,
			TranslationEngineException {
		
		boolean removed=false;
		Iterator<Daemon> iterator = daemons.iterator();
		while(iterator.hasNext() && !removed)
		{
			Daemon daemon = iterator.next();
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

		ServerStatusTO serverStatusTO = new ServerStatusTO();
		List<DaemonInformation> daemonList = new ArrayList<DaemonInformation>();
		
		for(Daemon daemon: daemons)
		{
			//if(!daemon.isCrashed())
				daemonList.add(daemon.getDaemonInformation());
		}
		serverStatusTO.setDaemonsInformation(daemonList);
		
		serverStatusTO.setLoad(0);
                
                double actLoad=operativeSystemMXBean.getSystemLoadAverage();
                double numProc= operativeSystemMXBean.getAvailableProcessors();
                
		double load = actLoad / numProc;
                
                logger.debug("Load :"+Double.toString(actLoad)+"/"+Double.toString(numProc)+" = "+Double.toString(load));
                        
		if(load>=0)
			serverStatusTO.setLoad(load);
		        
		return serverStatusTO;
	}

	@Override
	public void stopAllDaemonsLanguage(DaemonConfiguration c)
			throws RemoteException, TranslationEngineException {
		synchronized (daemons) {
			Iterator<Daemon> iterator = daemons.iterator();
			while(iterator.hasNext())
			{
				Daemon daemon = iterator.next();
				if(daemon.getConfiguration().equals(c))
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
	public Map<DaemonConfiguration, Double> calculateLanguagesSpeedRatio(DaemonConfiguration comparationConf) {
		Map<DaemonConfiguration,Double> ratios = new HashMap<DaemonConfiguration, Double>();
		String gplTranslation,udhrTranslation;
		
		Set<DaemonConfiguration> supConfs = DaemonFactory.getInstance().getSupportedConfigs();
		long t1,t2;
		try
		{
		logger.info("Calculating pairs speed. Comparing with "+comparationConf);
		DaemonInformation daemonComparation =startDaemon(comparationConf);
		String uhdr = ServerUtil.readFileFromClasspath("/corpora/UDHR_"+comparationConf.getLanguagePair().getSource()+".txt");
		String gpl = ServerUtil.readFileFromClasspath("/corpora/gpl_"+comparationConf.getLanguagePair().getSource()+".txt");
		if(uhdr==null || gpl==null)
			throw new Exception("Cannot read corpora");
		
		//Send these translations first to ensure daemon is up
		translate(new TextContent(Format.txt,uhdr), comparationConf.getLanguagePair(),null);
		translate(new TextContent(Format.txt,gpl), comparationConf.getLanguagePair(),null);
		
		t1 = System.currentTimeMillis();
		translate(new TextContent(Format.txt,uhdr), comparationConf.getLanguagePair(),null);
		translate(new TextContent(Format.txt,gpl), comparationConf.getLanguagePair(),null);
		t2=System.currentTimeMillis();
		stopAllDaemonsLanguage(comparationConf);
		
		long comparationTime=t2-t1;
		double comparationSpeed = (uhdr.length()+gpl.length())*1000 / (double) comparationTime; 
		
		for(DaemonConfiguration c: supConfs)
		{
			udhrTranslation="";
			gplTranslation="";
			logger.info("Calculating "+c+" speed");
			if(c.equals(comparationConf))
				ratios.put(c, 1d);
			else
			{
			try
			{
                          
			daemonComparation =startDaemon(c);
			uhdr = ServerUtil.readFileFromClasspath("/corpora/UDHR_"+c.getLanguagePair().getSource()+".txt");
			gpl = ServerUtil.readFileFromClasspath("/corpora/gpl_"+c.getLanguagePair().getSource()+".txt");
			if(uhdr==null && gpl==null)
				throw new Exception("Cannot read corpora");
			//Send these translation first to ensure daemon is up
			if(uhdr!=null)
				translate(new TextContent(Format.txt,uhdr), c.getLanguagePair(), null);
			if(gpl!=null)
				translate(new TextContent(Format.txt,gpl), c.getLanguagePair(), null);
			
			t1 = System.currentTimeMillis();
			if(uhdr!=null)
				udhrTranslation=translate(new TextContent(Format.txt,uhdr), c.getLanguagePair(), null).toString();
			if(gpl!=null)
				gplTranslation=translate(new TextContent(Format.txt,gpl), c.getLanguagePair(), null).toString();
			t2=System.currentTimeMillis();
			logger.debug(c+" UDHR Translation: "+udhrTranslation+" GPL translation: "+gplTranslation);
			
			 long time = t2-t1;
			 double speed=((uhdr!=null ? uhdr.length(): 0 ) + ( gpl!=null ? gpl.length() : 0))*1000 / (double) time;
			 double ratio = comparationSpeed/speed;
			 ratios.put(c, ratio);
			}
			catch (Exception e) {
				logger.error("Exception calculating "+c+" speed",e);
			}
			finally{
				stopAllDaemonsLanguage(c);
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
	public Map<Format,Double> calculateFormatSpeedRatio(DaemonConfiguration referenceConf,Format comparationFormat)
	{
		
		Map<Format,Double> ratioMap = new HashMap<Format, Double>();
		long t1,t2;
                double txtSpeed=0;
		String quijoteText = ServerUtil.readFileFromClasspath("/corpora/quijoteFragment.txt");
		String quijoteHtml = ServerUtil.readFileFromClasspath("/corpora/quijoteFragment.html");

                Map<Format,byte[]> binaryFiles= new HashMap<Format, byte[]>();
                for(Format f: Format.values())
                {
                    if(!f.equals(Format.txt) && !f.equals(Format.html))
                    {
                        byte[] file = ServerUtil.readArrayFromClasspath("/corpora/quijoteFragment."+f.toString());
                        if(file!=null)
                            binaryFiles.put(f, file);
                    }
                }

		logger.info("Calculating formats speed. Comparing with "+comparationFormat);
		try
		{

                
		DaemonInformation daemonComparation =startDaemon(referenceConf);
		
		//Calculate reference speed
		
		//ensure daemon is working
		translate(new TextContent(Format.txt,quijoteText), referenceConf.getLanguagePair(), null);
		
		t1=System.currentTimeMillis();
		translate(new TextContent(Format.txt,quijoteText), referenceConf.getLanguagePair(), null);
		t2=System.currentTimeMillis();
		
		txtSpeed =   (quijoteText.length()*1000/ (double) (t2-t1));

                }
		catch (Exception e) {
			logger.error("Error calculating formats speed ratio",e);
		}
		finally{
			try
			{
			stopAllDaemonsLanguage(referenceConf);
			}
			catch (Exception e) {}
		}
		
		for(Format format: Format.values())
		{
			logger.info("Calculating "+format+" speed");
			if(format.equals(comparationFormat))
				ratioMap.put(format, 1d);
			else
			{
                            try
                            {
				if(format.equals(Format.html))
				{
                                    startDaemon(referenceConf);
                                    translate(new TextContent(Format.html,quijoteHtml), referenceConf.getLanguagePair(), null);
                                    t1=System.currentTimeMillis();
                                    translate(new TextContent(Format.html,quijoteHtml), referenceConf.getLanguagePair(), null);
                                    t2=System.currentTimeMillis();

                                    double speed = quijoteHtml.length()*1000/ (double) (t2-t1);
                                    double ratio = txtSpeed/speed;
                                    ratioMap.put(format, ratio);
				}
                                else /*if(format.equals(Format.odt) || format.equals(Format.rtf))*/
                                {
                                    byte[] file= binaryFiles.get(format);
                                    if(file!=null)
                                    {
                                        BinaryDocument doc = new BinaryDocument(format,file);
                                        startDaemon(referenceConf);
                                        translate(doc, referenceConf.getLanguagePair(), null);
                                        t1=System.currentTimeMillis();
                                        translate(doc, referenceConf.getLanguagePair(), null);
                                        t2=System.currentTimeMillis();

                                        double length =doc.getLength();
                                        //System.err.println("t1="+t1+" t2="+t2+" length="+length);
                                        double speed = length*1000/ (double) (t2-t1);
                                        double ratio = txtSpeed/speed;
                                        ratioMap.put(format, ratio);
                                    }
                                }
					
                            }
                            
                            catch (Exception e) {
                                    logger.error("Error calculating formats speed ratio",e);
                            }
                            finally{
                                    try
                                    {
                                    stopAllDaemonsLanguage(referenceConf);
                                    }
                                    catch (Exception e) {}
                            }
                                    }
                            }
		
		
		logger.info("Finished  format speed calculation");
		return ratioMap;
	}

    /**
     * Calculates the memory required to translate with each language pair.
     * <br/>
     * This method launches a daemon for each supported language pair, translates with it
     * and then measures its memory consumption.
     *
     * @return Memory requirements of supported pairs
     */
	public Map<DaemonConfiguration, Integer> calculateMemoryRequirements() {
		Map<DaemonConfiguration,Integer> resultMap = new HashMap<DaemonConfiguration, Integer>();
		logger.info("Calculating pairs memory requirements.");
		
		//get machine memory capacity
		int memoryCapacity = computeServerTotalMemory();
		
		logger.info("Machine capacity: "+memoryCapacity);
		
		if(memoryCapacity>0)
		{
			for(DaemonConfiguration c: DaemonFactory.getInstance().getSupportedConfigs())
			{
				logger.info("Calculating "+c+" memory requirements:");
				try
				{
				DaemonInformation daemonInfo=startDaemon(c);
                                String uhdr = ServerUtil.readFileFromClasspath("/corpora/UDHR_"+c.getLanguagePair().getSource()+".txt");
                                translate(new TextContent(Format.txt,uhdr), c.getLanguagePair(), null);
                                synchronized(daemons)
                                {
                                    for(Daemon daemon: daemons)
                                        if(daemon.getId()==daemonInfo.getId())
                                        {
                                            /*
                                            Double usage=daemon.getMemoryUsage();
                                            if(usage!=null)
                                            {
                                                int require=(int) (usage/100.0*memoryCapacity);
                                                logger.info("Usage: "+usage+" Memory requirement: "+require);
                                                resultMap.put(c, require);
                                            }
                                              */
                                            resultMap.put(c, (int) daemon.getMemoryUsed().longValue());

                                        }
                                    }
                                }
				catch (Exception e) {
					logger.error("Exception calculating "+c+" memory requirement.", e);
				}
				finally
				{
					try
					{
					stopAllDaemonsLanguage(c);
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
                 DaemonConfiguration chosenc=DaemonFactory.getInstance().searchConfiguration(pair, Format.txt);
		long startTime,endTime;
		startDaemon(chosenc);
		List<String> filesContent = new ArrayList<String>();
		for(String filename: filenames)
		{
			String content = ServerUtil.readFileFromFileSystem(filename);
			if(content!=null)
				filesContent.add(content);
		}
		
		startTime = System.currentTimeMillis();
		for(String text: filesContent)
			translate(new TextContent(Format.txt,text), pair, new AdditionalTranslationOptions());
		endTime =System.currentTimeMillis();
		
		float time = (float) ((endTime-startTime) / 1000.0);
		logger.info("Total time: "+ time);
		
		stopAllDaemonsLanguage(chosenc);
		
		}
		catch (Exception e) {
			logger.error("Error calculating time",e);
		}
		
	}

	@Override
	public void startDaemons(DaemonConfiguration c, int numInstances)
			throws RemoteException, TranslationEngineException {
		for(int i=0;i<numInstances;i++)
			startDaemon(c);
		
	}

	@Override
	public void stopDaemons(DaemonConfiguration c, int numInstances)
			throws RemoteException, TranslationEngineException {
		int stopped=0;
		synchronized (daemons) {
			Iterator<Daemon> iterator = daemons.iterator();
			while(iterator.hasNext() && stopped<numInstances)
			{
				Daemon daemon = iterator.next();
				if(daemon.getConfiguration().equals(c))
				{
					daemon.stop();
					iterator.remove();
					stopped++;
				}
			}
		}
		
	}

    public long getTimeout() {
        return timeout;
    }

        

}
