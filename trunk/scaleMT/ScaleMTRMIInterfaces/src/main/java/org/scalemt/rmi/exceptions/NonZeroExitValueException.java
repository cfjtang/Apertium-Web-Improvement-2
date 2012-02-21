/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scalemt.rmi.exceptions;

/**
 *
 * @author vmsanchez
 */
public class NonZeroExitValueException extends TranslationEngineException{

    public NonZeroExitValueException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public NonZeroExitValueException(Throwable arg0) {
        super(arg0);
    }

    public NonZeroExitValueException(String arg0) {
        super(arg0);
    }

    public NonZeroExitValueException() {
    }
    
}
