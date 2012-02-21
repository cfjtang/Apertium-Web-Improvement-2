/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scalemt.rmi.exceptions;

/**
 *
 * @author vmsanchez
 */
public class SlaveTimeoutException extends TranslationEngineException{

    public SlaveTimeoutException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public SlaveTimeoutException(Throwable arg0) {
        super(arg0);
    }

    public SlaveTimeoutException(String arg0) {
        super(arg0);
    }

    public SlaveTimeoutException() {
    }
    
}
