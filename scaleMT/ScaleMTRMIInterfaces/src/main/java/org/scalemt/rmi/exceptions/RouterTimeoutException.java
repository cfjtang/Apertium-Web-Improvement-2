/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scalemt.rmi.exceptions;

/**
 *
 * @author vmsanchez
 */
public class RouterTimeoutException extends TranslationEngineException{

    public RouterTimeoutException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public RouterTimeoutException(Throwable arg0) {
        super(arg0);
    }

    public RouterTimeoutException(String arg0) {
        super(arg0);
    }

    public RouterTimeoutException() {
    }
    
}
