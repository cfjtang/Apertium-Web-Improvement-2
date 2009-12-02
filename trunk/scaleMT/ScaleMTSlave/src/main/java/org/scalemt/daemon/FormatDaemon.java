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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.scalemt.main.FormatQueueElement;
import org.scalemt.rmi.exceptions.TranslationEngineException;

/**
 * UNUSED CLASS
 * @author vmsanchez
 */
public class FormatDaemon {
	static Log logger = LogFactory.getLog(FormatDaemon.class);
	
	private class FormatReader implements Runnable
	{

		@Override
		public void run() {
			try {

				String line;
				StringBuilder buffer=new StringBuilder();
				while ((line = pReader.readLine()) != null) {
					if(line.equals("\0\0"))
					{
						FormatQueueElement element = resultsQueue.take();
						element.setProcessedText(buffer.toString());
						buffer=new StringBuilder();
						element.getCaller().interrupt();
					}
					else
					{
						buffer.append(line);
						buffer.append("\n");
					}
				}
				
			}
			catch (FileNotFoundException e) {
				logger.warn( "Cannot find pipe to read from");
				try {
					Thread.sleep(10000);
				} catch (Exception ex) {
				}
			} catch (Exception ie) {
				logger.error("Exception while reading pipe", ie);
			}
		}
		
	}
	
	private class FormatWriter implements Runnable
	{

		@Override
		public void run() {
			FormatQueueElement queueElement;
			try
			{
			while ((queueElement = writingQueue.take()) != stopMark) {
				StringBuilder textToWrite = new StringBuilder(queueElement.getSourceText());
				textToWrite.append("\n");
				char nullchar = 0;
				textToWrite.append(nullchar);
				textToWrite.append(nullchar);
				
				resultsQueue.put(queueElement);
				pWriter.write(textToWrite.toString());
				pWriter.flush();
				
			}
		} catch (Exception e) {
			logger.error("Can't write to Apertium daemon: ",e);
		}
		try {
			pWriter.close();
		} catch (Exception e) {
			logger.error("Can't stop Apertium daemon");
		}
		}
		
	}
	
	private BlockingQueue<FormatQueueElement> writingQueue;
	private BlockingQueue<FormatQueueElement> resultsQueue;
	
	private String program;
	private String apertiumPath;
	
	private String[] command;
	private Thread tReader;
	private Thread tWriter;
	
	private Process p;
	private BufferedReader pReader = null;
	private BufferedWriter pWriter = null;
	
	private FormatQueueElement stopMark;
	
	public FormatDaemon(String program,String apertiumPath) {
		this.program=program;
		this.apertiumPath=apertiumPath;
		
		writingQueue=new LinkedBlockingQueue<FormatQueueElement>();
		resultsQueue=new LinkedBlockingQueue<FormatQueueElement>();
		
		command= new String[1];
		command[0]=apertiumPath+"/"+program;
		
		tReader = new Thread(new FormatReader());
		tWriter = new Thread(new FormatWriter());
		
		stopMark=new FormatQueueElement("",null);
	}
	
	public void start()
	{
		Runtime rt = Runtime.getRuntime();
		try {
		Process p = rt.exec(command);
		pReader = new BufferedReader(new InputStreamReader(p
				.getInputStream()));
		pWriter = new BufferedWriter(new OutputStreamWriter(p
				.getOutputStream()));
		
		tReader.start();
		tWriter.start();
		
		} catch (Exception e) {
			logger.fatal("Can't start Apertium daemon",e);
		}
	}
	
	public void stop()
	{
		try
		{
		writingQueue.put(stopMark);
		}
		catch (Exception e) {
			logger.error("Coultn't write into writing queue to stop daemon",e);
		}
	}
	
	public String reDeFormat(String source,long timeout) throws TranslationEngineException
	{
		FormatQueueElement queueElement = new FormatQueueElement(source,Thread.currentThread());
		try
		{
		writingQueue.put(queueElement);
		
		
		try
		{
		Thread.sleep(timeout);
		throw new TranslationEngineException("Timeout while waiting");
		}
		catch (InterruptedException e) {
			return queueElement.getProcessedText();
		}
		
		}
		catch (Exception e) {
			throw new TranslationEngineException("Unexpected exception");
		}
	}
}
