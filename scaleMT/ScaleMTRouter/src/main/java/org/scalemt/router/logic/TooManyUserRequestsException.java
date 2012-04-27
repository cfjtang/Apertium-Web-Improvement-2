/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scalemt.router.logic;

/**
 *
 * @author vmsanchez
 */
public class TooManyUserRequestsException extends Exception {

    public TooManyUserRequestsException(Throwable cause) {
        super(cause);
    }

    public TooManyUserRequestsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManyUserRequestsException(String message) {
        super(message);
    }

    public TooManyUserRequestsException() {
    }
    
    
}
