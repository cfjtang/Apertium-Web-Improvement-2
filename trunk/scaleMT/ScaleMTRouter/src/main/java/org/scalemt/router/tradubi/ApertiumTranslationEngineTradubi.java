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
package org.scalemt.router.tradubi;

import org.scalemt.rmi.exceptions.TranslationEngineException;
import org.scalemt.rmi.exceptions.UnsupportedLanguagePairException;
import org.scalemt.rmi.router.ITradubiTranslationEngine;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.router.logic.LoadBalancer;
import org.scalemt.router.logic.Util;
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
import org.scalemt.rmi.transferobjects.TextContent;
import org.scalemt.rmi.transferobjects.AdditionalTranslationOptions;
import org.scalemt.router.ws.LoggerStatiticsWriter;


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
	private int maxCompilationInstances;
	private long compilationTimeout;

	
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
            int responseCode=200;
         String errorMessage="";
        try
        {

         LoggerStatiticsWriter.getInstance().logRequestReceived("tradubi-ip", "tradubi-ref","tradubi", sourceLang+"|"+targetLang,"html");
         logger.debug("requestreceived "+"tradubi-ip"+" tradubi-ref" +"tradubi"+" "+sourceLang+"|"+targetLang+" "+"html");
		 List<LanguagePair> supPairs =LoadBalancer.getInstance().getSupportedPairs();

             LanguagePair reqPair = new LanguagePair(sourceLang, targetLang);
            if (!supPairs.contains(reqPair))
            {
                responseCode=451;
                throw new UnsupportedLanguagePairException();
            }

		try {
			return  LoadBalancer.getInstance().translate(new TextContent(Format.html,sourceHtml), reqPair, "tradubi-ip","tradubi","tradubi",new AdditionalTranslationOptions(dictionaries)).toString() ;
		} catch (Exception e) {
			logger.error("Couldn't perform translation", e);
                        responseCode=500;
			throw new TranslationEngineException(e);
		}
        }
        finally{
            LoggerStatiticsWriter.getInstance().logRequestProcessed(Integer.toString(responseCode));
            logger.debug("requesprocessed "+responseCode+" "+errorMessage);
        }
	}

    @Override
	public  String translateText(String sourceText, String sourceLang,
			String targetLang, List<Long> dictionaries)
			throws TranslationEngineException,UnsupportedLanguagePairException {
          int responseCode=200;
         String errorMessage="";
        try
        {
       
         LoggerStatiticsWriter.getInstance().logRequestReceived("tradubi-ip","tradubi-ref", "tradubi", sourceLang+"|"+targetLang,"txt");
         logger.debug("requestreceived "+"tradubi-ip"+" tradubi-ref "+"tradubi"+" "+sourceLang+"|"+targetLang+" "+"txt");
		 List<LanguagePair> supPairs =LoadBalancer.getInstance().getSupportedPairs();

             LanguagePair reqPair = new LanguagePair(sourceLang, targetLang);
            if (!supPairs.contains(reqPair))
            {
                responseCode=451;
                throw new UnsupportedLanguagePairException();
            }

		try {
			return  LoadBalancer.getInstance().translate(new TextContent(Format.txt,sourceText), reqPair, "tradubi-ip","tradubi","tradubi",new AdditionalTranslationOptions(dictionaries)).toString() ;
		} catch (Exception e) {
			logger.error("Couldn't perform translation", e);
                        responseCode=500;
			throw new TranslationEngineException(e);
		}
        }
        finally{
            LoggerStatiticsWriter.getInstance().logRequestProcessed(Integer.toString(responseCode));
            logger.debug("requesprocessed "+responseCode+" "+errorMessage);
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
