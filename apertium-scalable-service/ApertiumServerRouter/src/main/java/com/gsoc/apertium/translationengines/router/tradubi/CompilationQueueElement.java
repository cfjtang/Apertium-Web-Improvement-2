package com.gsoc.apertium.translationengines.router.tradubi;

import com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException;
import java.util.Map;

/**
 * Transfer Object that contains all data needed to compile a dictionary
 * 
 * @author vmsanchez
 *
 */
public class CompilationQueueElement {

	
	private Thread caller;
	private TranslationEngineException exception=null;
	private long idDictionary;
	private Map<String,String> entries;
	private String sourceLanguage;
	private String targetLanguage;
	
	
	
	public CompilationQueueElement(long idDictionary,Map<String,String> entries,String sourceLang, String targetLang, Thread caller) {
		super();
		this.idDictionary=idDictionary;
		this.entries=entries;
		this.caller = caller;
		this.sourceLanguage=sourceLang;
		this.targetLanguage=targetLang;
	}
	
	
	public Thread getCaller() {
		return caller;
	}
	public void setCaller(Thread caller) {
		this.caller = caller;
	}
	public TranslationEngineException getException() {
		return exception;
	}
	public void setException(TranslationEngineException exception) {
		this.exception = exception;
	}


	public long getIdDictionary() {
		return idDictionary;
	}


	public void setIdDictionary(long idDictionary) {
		this.idDictionary = idDictionary;
	}


	public Map<String, String> getEntries() {
		return entries;
	}


	public void setEntries(Map<String, String> entries) {
		this.entries = entries;
	}


	public String getSourceLanguage() {
		return sourceLanguage;
	}


	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}


	public String getTargetLanguage() {
		return targetLanguage;
	}


	public void setTargetLanguage(String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}
	
	
	
	
	
}
