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
package com.gsoc.apertium.translationengines.router.tradubi;

import com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException;
import com.gsoc.apertium.translationengines.rmi.exceptions.UnsupportedLanguagePairException;
import com.gsoc.apertium.translationengines.rmi.router.ITradubiTranslationEngine;
import com.gsoc.apertium.translationengines.rmi.transferobjects.Format;
import com.gsoc.apertium.translationengines.rmi.transferobjects.LanguagePair;
import com.gsoc.apertium.translationengines.router.logic.LoadBalancer;
import com.gsoc.apertium.translationengines.router.logic.UserType;
import com.gsoc.apertium.translationengines.router.logic.Util;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Interface with the Tradubi social translation web aplication.
 * 
 * 
 */
public class ApertiumTranslationEngineTradubi implements ITradubiTranslationEngine, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2215612722973686962L;

	static Log logger = LogFactory.getLog(ApertiumTranslationEngineTradubi.class);

	private static final String PERSONAL_DICTIONARY_MODE_SUFFIX = "-tradubidu"; 
	
	private int maxCompilationInstances;
	private long timeout;
	private long compilationTimeout;
	private String modeSuffix;

	
	private final String tmpdir = System.getProperty("java.io.tmpdir");

    private final BlockingQueue<CompilationQueueElement> compilationQueue;

      private String dictionaryPath;
	
	/**
	 * Compiles personal dictionaries
	 */ 
	private class Compiler implements Runnable
	{

        public Compiler() {            
        }

        @Override
		public void run() {
			
			while(true)
			{
				try
				{
				CompilationQueueElement queueElement = compilationQueue.take();
				
				logger.trace("Compiling dictionary "+queueElement.getIdDictionary()+ " "+queueElement.getSourceLanguage()+"-"+queueElement.getTargetLanguage());
				
				//TODO: Cuidado con el orden de los pares!
				String alphabet;//=ServerUtil.readProperty("apertium_alphabet_"+queueElement.getSourceLanguage()+"-"+queueElement.getTargetLanguage()+"."+queueElement.getSourceLanguage());
				//if(alphabet==null || alphabet.trim().isEmpty())
					alphabet="<alphabet>"+StringEscapeUtils.escapeXml("!¡1234567890ÀÁÂÄÇÈÉÊËÌÍÎÏÑÒÓÔÖÙÚÛÜàáâäçèéêëìíîïñòóôöùúûüABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,;:<>[]()#*\\/\"")+"</alphabet>\n";
					
				
				try
				{
				
				StringBuilder strBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				strBuilder.append("<dictionary>\n");
				strBuilder.append(alphabet+"\n"); 
				strBuilder.append("<section  id=\"main\" type=\"standard\">\n");
				for(Entry<String,String> entry: queueElement.getEntries().entrySet())
				{
					strBuilder.append("<e><p>\n");
					strBuilder.append("<l>"+StringEscapeUtils.escapeXml(entry.getKey()).replace(" ", "<b/>")+"</l>\n");
					strBuilder.append("<r>"+StringEscapeUtils.escapeXml(entry.getValue())+"</r>\n");
					strBuilder.append("</p></e>\n");
				}
				strBuilder.append("</section>\n");
				strBuilder.append("</dictionary>\n");
				
				try
				{
				FileWriter writer = new FileWriter(tmpdir+"/"+queueElement.getIdDictionary()+".xml");
				writer.write(strBuilder.toString());
				writer.close();
				}
				catch (Exception e) {
					throw new TranslationEngineException("Error writing file to compile dictionary",e);
				}
				
				// A Runtime object has methods for dealing with the OS
				Runtime r = Runtime.getRuntime();

				// A process object tracks one external running process
				Process p;
				
				String[] command = new String[4];
				command[0]="lt-comp";
				command[1]="lr";
				command[2]=tmpdir+"/"+queueElement.getIdDictionary()+".xml";
				command[3]=dictionaryPath+"/"+queueElement.getIdDictionary()+".bin";
				
				try
				{
				p = r.exec(command);
				
				int result = p.waitFor();
				if(result!=0)
					throw new Exception("Exit value of lt-comp is not zero"); 
				}
				catch (Exception e) {
					logger.error("Error compiling dictionary",e);
					throw new TranslationEngineException("Error compiling dictionary",e);
				}
				
				}
				catch (TranslationEngineException e) {
					queueElement.setException(e);
				}
				
				queueElement.getCaller().interrupt();
				
				}
				catch (InterruptedException e) {
					logger.warn("InterruptedException in compiler",e);
				}
				
			}
			
			
		}
		
	}

	public ApertiumTranslationEngineTradubi() {

		try {
			compilationTimeout = Long.parseLong(Util.readConfigurationProperty("apertium_compilation_timeout"));
		} catch (Exception e) {
			compilationTimeout = 20000;
			logger.warn("Can't load apertium compilation timeout from config file");
		}
		
		
		try {
			maxCompilationInstances = Integer.parseInt(Util.readConfigurationProperty("apertium_num_compilation_instances"));
		} catch (Exception e) {
			maxCompilationInstances = 2;
			logger.warn("Can't load  number of apertium compilation instances from config file");
		}

        String dpath = Util.readConfigurationProperty("tradubi_dictionaries_dir");
            if(dpath==null)
                dictionaryPath="/tmp/tradubidics";
            else
                dictionaryPath=dpath;

        compilationQueue=new LinkedBlockingQueue<CompilationQueueElement>();
		
		for(int i=0; i<maxCompilationInstances;i++)
		{
			Thread th = new Thread(new Compiler());
			th.start();
		}


		
	}
   

     @Override
    public Set<LanguagePair> getSupportedPairs() throws RemoteException {
       return new HashSet<LanguagePair>(LoadBalancer.getInstance().getSupportedPairs());
    }


    @Override
	public String translateHTML(String sourceHtml, String sourceLang,
			String targetLang, List<Long> dictionaries) throws UnsupportedLanguagePairException,TranslationEngineException
	{
             List<LanguagePair> supPairs =LoadBalancer.getInstance().getSupportedPairs();
             
             LanguagePair reqPair = new LanguagePair(sourceLang, targetLang);
            if (!supPairs.contains(reqPair))
                throw new UnsupportedLanguagePairException();

		try {
			return  LoadBalancer.getInstance().translate(sourceHtml, reqPair, Format.html, UserType.registered,dictionaries) ;
		} catch (Exception e) {
			logger.error("Couldn't perform translation", e);
			throw new TranslationEngineException(e);
		}
	}

    @Override
	public  String translateText(String sourceText, String sourceLang,
			String targetLang, List<Long> dictionaries)
			throws TranslationEngineException,UnsupportedLanguagePairException {

		 List<LanguagePair> supPairs =LoadBalancer.getInstance().getSupportedPairs();

             LanguagePair reqPair = new LanguagePair(sourceLang, targetLang);
            if (!supPairs.contains(reqPair))
                throw new UnsupportedLanguagePairException();

		try {
			return  LoadBalancer.getInstance().translate(sourceText, reqPair, Format.text, UserType.registered,dictionaries) ;
		} catch (Exception e) {
			logger.error("Couldn't perform translation", e);
			throw new TranslationEngineException(e);
		}
	}


	@Override
	public void compileDictionary(long dictionaryId, Map<String, String> entries, String pair)
			throws TranslationEngineException {
		
		CompilationQueueElement queueElement=null;
		try
		{
			String[] langs = pair.split("-");
			queueElement = new CompilationQueueElement(dictionaryId,entries,langs[0],langs[1],Thread.currentThread());
		compilationQueue.put(queueElement);
		}
		catch (Exception e) {
			throw new TranslationEngineException("",e);
		}
		
		try
		{
			Thread.sleep(compilationTimeout);
		}
		catch (InterruptedException e) {
			if(queueElement.getException()!=null)
				throw queueElement.getException();
			
			//Send empty translation without dictionary to all daemons that work
			//with the dictionary pair of languages, to force reloading of user module

            LoadBalancer.getInstance().reloadDictionaries(new LanguagePair(pair, "-".toCharArray()));
            
			return;
		}
		//¿Por qué estaba esto antes?
		//compilationQueue.remove(queueElement);
		throw new TranslationEngineException("Timeout reached while waiting for compilation");
		
	}

    @Override
	public void removeDictionary(long dictionaryid) throws TranslationEngineException{

        /*
		if(apertiumDictionaryDirectory==null)
			throw new TranslationEngineException("Dictionaries path not defined");
		*/
		File dictionaryFile = new File(dictionaryPath+"/"+dictionaryid+".bin");
		//File xmlFile = new File(apertiumDictionaryDirectory+"/"+dictionaryid+".xml");
		if(dictionaryFile.exists())
		{
			//xmlFile.delete();
			if(!dictionaryFile.delete())
				throw new TranslationEngineException("Could't delete dictionary "+dictionaryid);
		}
		else
			throw new TranslationEngineException("Dictionary file not found: "+dictionaryid);

	}

}
