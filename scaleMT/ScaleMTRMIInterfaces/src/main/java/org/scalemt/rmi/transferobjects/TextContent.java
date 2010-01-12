/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.rmi.transferobjects;

/**
 *
 * @author vmsanchez
 */
public class TextContent extends Content{

    private String string;

    public TextContent(String string) {
        this.string = string;
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public int length() {
        return string.length();
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public byte[] toByteArray() {
        return string.getBytes();
    }
    
}
