/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.ws;

/**
 *
 * @author vmsanchez
 */
public class WrongFormatException extends Exception {

    private String wrongfield;

    public WrongFormatException(String wrongfield) {
        super();
        this.wrongfield = wrongfield;
    }

    public String getWrongfield() {
        return wrongfield;
    }

    public void setWrongfield(String wrongfield) {
        this.wrongfield = wrongfield;
    }

    

}
