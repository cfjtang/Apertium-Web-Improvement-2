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
package com.gsoc.apertium.translationengines.daemon;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gsoc.apertium.translationengines.rmi.exceptions.DaemonDeadException;
import com.gsoc.apertium.translationengines.main.ApertiumQueueElement;
import com.gsoc.apertium.translationengines.rmi.transferobjects.DaemonInformation;
import com.gsoc.apertium.translationengines.rmi.transferobjects.LanguagePair;
import com.gsoc.apertium.translationengines.util.ServerUtil;

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
public class ApertiumDaemon {

    /**
     * Commons-logging logger
     */
	static Log logger = LogFactory.getLog(ApertiumDaemon.class);

	/**
	 * Reads translations from apertium standard output and wakes up threads
	 * that are waiting for these translations.
	 * 
	 * @author vmsanchez
	 * 
	 */
	private class ApertiumReader implements Runnable {

        /**
         * ID of the translation currently being read
         */
		private long currentId;

        /**
         * Translation currently being read
         */
		private StringBuilder text;

        @Override
		public void run() {

			currentId = -1;

			try {

				String buffer;
				final Pattern pattern = Pattern
						.compile("^\\[--apertium-translation id=\"(\\d+)\" dict=\"(([-]?\\d+(,[-]?\\d+)*)?)\"--\\]$");

				logger.debug(daemonInformation.getId()
						+ ": Starting ApertiumReader");
				while ((buffer = pReader.readLine()) != null) {
					 logger.trace(daemonInformation.getId() +
					 ": read \""+buffer+"\"");
					lastRead = System.currentTimeMillis();
					Matcher matcher = pattern.matcher(buffer);
					if (matcher.matches()) {
						logger.debug(daemonInformation.getId()
								+ ": read <apertium-translation>");
						currentId = Long.parseLong(matcher.group(1));
						text = new StringBuilder();
						logger.debug(daemonInformation.getId() + ": id="
								+ currentId);
					} else if (buffer.equals("[--end-apertium-translation--]")) {
						logger.debug(daemonInformation.getId()
								+ ": finished <apertium-translation>");
						ApertiumQueueElement element = resultsQueue.poll();
						if (element != null) {
							if (element.getId() == currentId) {
								element.setTranslation(text.toString());
							} else {
								logger
										.error("ApertiumDaemon - ApertiumReader "
												+ daemonInformation.getId()
												+ ": Read translation id ("+currentId+") that doesn't match translation queue element("+element.getId()+")");
								element.setTranslation(null);
								element.setException(new DaemonDeadException());
								logger.error("Daemon " + daemonInformation.getId()
										+ " crashed");
								crashed=true;
								stop();
								
							}
							if (element.getCaller() != null)
								element.getCaller().interrupt();

						} else {
							logger.error("ApertiumDaemon - ApertiumReader "
									+ daemonInformation.getId()
									+ ": Translation queue is empty");
						}

						if (charactersPerTranslation.containsKey(currentId)) {
							long translationSize = charactersPerTranslation
									.get(currentId);
							readWriteLock.writeLock().lock();
							daemonInformation
									.setCharactersInside(daemonInformation
											.getCharactersInside()
											- translationSize);
							readWriteLock.writeLock().unlock();
							charactersPerTranslation.remove(currentId);
						}
						currentId = -1;

					} else {
						if (currentId != -1) {
							text.append(buffer);
							text.append("\n");
						}
					}
				}
			} catch (FileNotFoundException e) {
				logger.warn(daemonInformation.getId()
						+ ": Cannot find pipe to read from");
			} catch (IOException ie) {
				logger.warn(daemonInformation.getId()
						+ ": Exception while reading pipe", ie);
			}
            logger.debug("Finished ApertiumReader from"+daemonInformation.getId());
		}

	}

	/**
	 * Send translation requests to Apertium
	 * 
	 * @author vmsanchez
	 * 
	 */
	private class ApertiumWriter implements Runnable {

		public ApertiumWriter() {

		}

        @Override
		public void run() {

			try {
				ApertiumQueueElement queueElement;
				while ((queueElement = writingQueue.take()) != stopMark) {
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
                    textToWrite.append("\"--]\n");
					textToWrite.append(queueElement.getText());
					textToWrite.append("\n[--end-apertium-translation--]\n");

					resultsQueue.put(queueElement);
					lastWrite = System.currentTimeMillis();
                                        logger.trace("Writing :"+textToWrite.toString());
					pWriter.write(textToWrite.toString());
                                        pWriter.write(0);
                                        pWriter.write("\n");
					pWriter.flush();

				}
			} catch (Exception e) {
				logger.error("Can't write to Apertium daemon: ", e);
			}
			try {
				pWriter.close();
			} catch (Exception e) {
				logger.error("Can't stop Apertium daemon");
			}
             logger.debug("Finished ApertiumWriter from"+daemonInformation.getId());
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
				logger.error("Daemon " + daemonInformation.getId()
						+ " finished with status " + exitValue);
			} else {
				// Check if process is frozen
				//TODO: read time from config file
				if (lastRead < lastWrite
						&& System.currentTimeMillis() - lastWrite > frozenTime) {
					logger.error("Daemon " + daemonInformation.getId()
							+ " frozen");
					finished=true;
				}
			}
			
			if(finished)
			{
				crashed=true;
				stop();
			}
			  
			  
			  }
	}

    /**
     * Daemon information
     */
	private DaemonInformation daemonInformation;

    /**
     * Apertium process
     */
	private Process p;

    /**
     * Interface to read from Apertium pipeline
     */
	private BufferedReader pReader = null;

    /**
     * Interface to write to Apertium pipeline
     */
	private BufferedWriter pWriter = null;

    /**
     * Command to invoke Apertium
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
	private BlockingQueue<ApertiumQueueElement> writingQueue;

    /**
     * Queue where translation requests are put after writing them to the Apertium pipeline
     */
	private BlockingQueue<ApertiumQueueElement> resultsQueue;

    /**
     * Queue element that stops all the running threads
     */
	private ApertiumQueueElement stopMark;

    /**
     * Path where Apertium executable can be found
     */
	private String apertiumPath;

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
	private boolean crashed=false;

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
	ApertiumDaemon(long id, LanguagePair pair, String suffix,
			String apertiumPath) {

		daemonInformation = new DaemonInformation(id, pair);

		charactersPerTranslation = Collections
				.synchronizedMap(new HashMap<Long, Long>());
		readWriteLock = new ReentrantReadWriteLock();
		writingQueue = new LinkedBlockingQueue<ApertiumQueueElement>();
		resultsQueue = new LinkedBlockingQueue<ApertiumQueueElement>();
		stopMark = new ApertiumQueueElement(-100, "");
		this.apertiumPath = apertiumPath;
                String strCommand = apertiumPath + "/execAndGetPID.sh "+ apertiumPath + "/apertium -f none "+pair.getSource() + "-" + pair.getTarget() + suffix;
                logger.debug("Starting Apertium with command: "+strCommand);

		command = new String[3];
                command[0]="/bin/sh";
                command[1]="-c";
                command[2]= strCommand;


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

	/**
	 * Starts daemon. Daemon must be started before sending any translation
	 * 
	 */
	public void start() {
		daemonInformation.setCharactersInside(0);
		Runtime r = Runtime.getRuntime();
		try {
			p = r.exec(command);
			pReader = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			pWriter = new BufferedWriter(new OutputStreamWriter(p
					.getOutputStream()));

			tReader.start();
			tWriter.start();

			lastRead = 0;
			lastWrite = 0;

			
			
			statusTimer.schedule(new StatusReader(), checkStatusPeriod, checkStatusPeriod);

			try {
				String pid = new BufferedReader(new InputStreamReader(p
						.getErrorStream())).readLine();
				daemonInformation.setPid(Integer.parseInt(pid));
				logger.debug("Started daemon with pid="
						+ daemonInformation.getPid());
			} catch (Exception e) {
				logger.error("Cannot get process pid", e);
			}

		} catch (Exception e) {
			logger.fatal("Can't start Apertium daemon", e);
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

	/**
	 * Requests a translation. When it finishes, thread specified in
	 * <code>element</code> will be interrupted, and result will be in
	 * <code>translation</code> property
	 * 
	 * @param element
	 */
	public synchronized void translateAsync(ApertiumQueueElement element) {

		readWriteLock.writeLock().lock();
		daemonInformation.setCharactersInside(daemonInformation
				.getCharactersInside()
				+ element.getText().length());
		readWriteLock.writeLock().unlock();
		charactersPerTranslation.put(element.getId(), (long) (element.getText()
				.length()));

	try {
			writingQueue.put(element);
		} catch (Exception e) {

		}

	}

	/**
	 * Get number of characters currently being processed by Apertium. It is a
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
	public LanguagePair getPair() {
		return daemonInformation.getPair();
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
	public boolean isCrashed() {
		return crashed;
	}

    /**
     * Gets the % of system's memory consumed by the Apertium process
     * @return % of system's memory consumed by the Apertium process, or <code>null</code> if there is any error
     */
	public Double getMemoryUsage()
	{
		String information="";
		try
		{
			String[] command = {apertiumPath+"/getProcessInfo.sh",Integer.toString(daemonInformation.getPid())}; 
			Process p=Runtime.getRuntime().exec(command); 
			BufferedReader reader= new BufferedReader(new InputStreamReader(p.getInputStream()));
			information = reader.readLine(); 
			reader.close();
			String[] pieces = information.split(" ");
		
			return Double.parseDouble(pieces[1]);
		}
		catch (Exception e) {
			logger.error("Exception reading memory usage", e);
		}
		
		return null;
	}

    /**
     * Gets the % of system's CPU consumed by the Apertium process
     * @return % of system's CPU consumed by the Apertium process, or <code>null</code> if there is any error
     */
	public Double getCPUUsage()
	{
		String information="";
		try
		{
			String[] command = {apertiumPath+"/getProcessInfo.sh",Integer.toString(daemonInformation.getPid())};
			Process p=Runtime.getRuntime().exec(command);
			BufferedReader reader= new BufferedReader(new InputStreamReader(p.getInputStream()));
			information = reader.readLine();
			reader.close();
			String[] pieces = information.split(" ");

			return Double.parseDouble(pieces[0]);
		}
		catch (Exception e) {
			logger.error("Exception reading memory usage", e);
		}

		return null;
	}
	
	

}
