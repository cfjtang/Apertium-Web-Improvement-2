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

import com.gsoc.apertium.translationengines.rmi.transferobjects.LanguagePair;
import java.util.List;


/**
 * Transfer Object with all the information needed to send a translation to Apertium
 * 
 *
 */
public class ApertiumQueueElement {

    /**
     * Translation id. There is no translation with the same id.
     */
	private long id;

    /**
     * Source text
     */
	private String text;

    /**
     * Language pair: source language and target language
     */
	private LanguagePair languagePair;

    /**
     * Input fomrat: plain text, html, etc.
     */
	private String format;

    /**
     * Translated source
     */
	private String translation;

    /**
     * Thread to be interrupted when translation is performed
     */
	private Thread caller;

    /**
     * Exception produced when translating
     */
	private Exception exception;

    private List<Long> dictionaries;

    /**
     * Constructor with id and source txt.
     * It is not recommended to create instances with this constructor because
     * <code>caller</code> and <code>languagePair</code> fields are <code>null</code> and
     * this can cause unexpected exceptions.
     * @param id Id
     * @param text Source text
     */
	public ApertiumQueueElement(long id, String text) {
		super();
		this.id = id;
		this.text = text;
		this.exception=null;
	}
	

    /**
     * Recommended constructor.
     * @param id ID
     * @param text Source text
     * @param languagePair Language pair
     * @param caller Caller thread
     */
	public ApertiumQueueElement(long id, String text, 
			LanguagePair languagePair, Thread caller, List<Long> dictionaries) {
		super();
		this.id = id;
		this.text = text;
	
		this.languagePair = languagePair;
		this.caller = caller;
		this.exception=null;
        this.dictionaries=dictionaries;
	}



	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}



	public LanguagePair getLanguagePair() {
		return languagePair;
	}



	public void setLanguagePair(LanguagePair languagePair) {
		this.languagePair = languagePair;
	}



	public String getFormat() {
		return format;
	}



	public void setFormat(String format) {
		this.format = format;
	}

	public String getTranslation() {
		return translation;
	}



	public void setTranslation(String translation) {
		this.translation = translation;
	}



	public Thread getCaller() {
		return caller;
	}



	public void setCaller(Thread caller) {
		this.caller = caller;
	}



	public Exception getException() {
		return exception;
	}



	public void setException(Exception exception) {
		this.exception = exception;
	}

    public List<Long> getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(List<Long> dictionaries) {
        this.dictionaries = dictionaries;
    }
	
	
	

}
