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

import org.scalemt.daemon.Daemon;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import java.util.List;
import org.scalemt.rmi.transferobjects.AdditionalTranslationOptions;
import org.scalemt.rmi.transferobjects.Content;


/**
 * Transfer Object with all the information needed to send a translation to Apertium
 * 
 *
 */
public class QueueElement {

    /**
     * Translation id. There is no translation with the same id.
     */
	private long id;

    /**
     * Source text
     */
	private Content source;

        private byte[] rawContent;

    /**
     * Language pair: source language and target language
     */
	private LanguagePair languagePair;

    

    /**
     * Translated source
     */
	private Content translation;

        private byte[] rawTranslation;

    /**
     * Thread to be interrupted when translation is performed
     */
	private Thread caller;

        private Daemon daemon;

    /**
     * Exception produced when translating
     */
	private Exception exception;

    private AdditionalTranslationOptions additionalTranslationOptions;

    /**
     * Constructor with id and source txt.
     * It is not recommended to create instances with this constructor because
     * <code>caller</code> and <code>languagePair</code> fields are <code>null</code> and
     * this can cause unexpected exceptions.
     * @param id Id
     * @param text Source text
     */
	public QueueElement(long id, Content text) {
		super();
		this.id = id;
		this.source = text;
		this.exception=null;
	}
	

    /**
     * Recommended constructor.
     * @param id ID
     * @param text Source text
     * @param languagePair Language pair
     * @param caller Caller thread
     */
	public QueueElement(long id, Content text,
			LanguagePair languagePair, Thread caller, AdditionalTranslationOptions to) {
		super();
		this.id = id;
		this.source = text;

                
		this.languagePair = languagePair;
		this.caller = caller;
		this.exception=null;
                this.additionalTranslationOptions=to;
	}



	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Content getSource() {
		return source;
	}
	public void setSource(Content text) {
		this.source = text;
	}



	public LanguagePair getLanguagePair() {
		return languagePair;
	}



	public void setLanguagePair(LanguagePair languagePair) {
		this.languagePair = languagePair;
	}



	public Format getFormat() {
		return source.getFormat();
	}



	

	public Content getTranslation() {
		return translation;
	}



	public void setTranslation(Content translation) {
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

    public AdditionalTranslationOptions getAdditionalTranslationOptions() {
        return additionalTranslationOptions;
    }

    public void setAdditionalTranslationOptions(AdditionalTranslationOptions additionalTranslationOptions) {
        this.additionalTranslationOptions = additionalTranslationOptions;
    }

  

    public Daemon getDaemon() {
        return daemon;
    }

    public void setDaemon(Daemon daemon) {
        this.daemon = daemon;
    }

    public byte[] getRawContent() {
        return rawContent;
    }

    public void setRawContent(byte[] rawContent) {
        this.rawContent = rawContent;
    }

    public byte[] getRawTranslation() {
        return rawTranslation;
    }

    public void setRawTranslation(byte[] rawTranslation) {
        this.rawTranslation = rawTranslation;
    }
	
	
	

}
