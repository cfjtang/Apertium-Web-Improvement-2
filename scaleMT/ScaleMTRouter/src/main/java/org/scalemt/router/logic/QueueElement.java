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
package org.scalemt.router.logic;

import org.scalemt.rmi.exceptions.TranslationEngineException;
import org.scalemt.rmi.transferobjects.Format;
import java.util.List;
import org.scalemt.rmi.transferobjects.AdditionalTranslationOptions;
import org.scalemt.rmi.transferobjects.Content;



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
    private Content sourceText;

    

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
    private Content translation;

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

   private AdditionalTranslationOptions additionalTranslationOptions;

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
    public QueueElement(Content sourceText,  UserType userType, Thread caller, long notRegisteredIncrement,AdditionalTranslationOptions to) {
        this.sourceText = sourceText;
        
        this.userType = userType;
        this.caller = caller;
        this.time= System.currentTimeMillis();
        this.priority=this.time;
        this.additionalTranslationOptions=to;
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
        return sourceText.getFormat();
    }

   

    public Content getSource() {
        return sourceText;
    }

    public void setSource(Content sourceText) {
        this.sourceText = sourceText;
    }

    public Content getTranslation() {
        return translation;
    }

    public void setTranslation(Content translation) {
        this.translation = translation;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public AdditionalTranslationOptions getAdditionalTranslationOptions() {
        return additionalTranslationOptions;
    }

    public void setAdditionalTranslationOptions(AdditionalTranslationOptions additionalTranslationOptions) {
        this.additionalTranslationOptions = additionalTranslationOptions;
    }






}
