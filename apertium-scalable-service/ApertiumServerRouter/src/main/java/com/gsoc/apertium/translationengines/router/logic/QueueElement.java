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
package com.gsoc.apertium.translationengines.router.logic;

import com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException;
import com.gsoc.apertium.translationengines.rmi.transferobjects.Format;
import java.util.List;



/**
 * Element of a translation queue. Contains a numeric field called <code>priority</code>.
 * The lower priority, the sooner the translation request is processed.
 *
 * @author vitaka
 */
public class QueueElement implements Comparable<QueueElement>{
    /**
     * Text to be translated
     */
    private String sourceText;

    /**
     * Format: plain text, html, etc
     */
    private Format format;

    /**
     * User type: registered or not
     */
    private UserType userType;

    /**
     * Caller thread. It will be interrupted when translation finishes
     */
    private Thread caller;

    /**
     * Translated text
     */
    private String translation;

    /**
     * Exception thrown when processing request
     */
    private TranslationEngineException exception;

    /**
     * Time when the element was put in the queue
     */
    private long time;

    /**
     * Priority
     */
    private long priority;

    private List<Long> dictionaries;

    /**
     * Constructor. Calculates priority.
     * Priority is the time the object is created, plus and increment if the user
     * is not registered.
     *
     * @param sourceText Text to translate.
     * @param format FOrmat of the text
     * @param userType User type: registered or not.
     * @param caller Caller thread
     */
    public QueueElement(String sourceText, Format format, UserType userType, Thread caller, long notRegisteredIncrement,List<Long> dictionaries) {
        this.sourceText = sourceText;
        this.format = format;
        this.userType = userType;
        this.caller = caller;
        this.time= System.currentTimeMillis();
        this.priority=this.time;
        this.dictionaries=dictionaries;
        if(this.userType!=UserType.registered)
            this.priority+=notRegisteredIncrement;
    }

    public int compareTo(QueueElement o) {
        return   (int) (o.priority - this.priority);
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

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public List<Long> getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(List<Long> dictionaries) {
        this.dictionaries = dictionaries;
    }






}
