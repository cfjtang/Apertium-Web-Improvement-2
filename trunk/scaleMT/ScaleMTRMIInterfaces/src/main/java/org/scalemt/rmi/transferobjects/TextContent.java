/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.rmi.transferobjects;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

/**
 * Piece of text to be translated
 * @author vmsanchez
 */
public class TextContent extends Content{

    private String string;

    public TextContent(Format f,String string) {
        super(f);
        this.string = string;
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public int calculateLength() {
        return string.length();
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public byte[] toByteArray() {
        try {
            return string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return string.getBytes();
        }
    }

    @Override
    protected Set<Format> getValidFormats() {
        Set<Format> validFormats = new HashSet<Format>();
       validFormats.add(Format.txt);
       validFormats.add(Format.latex);
       validFormats.add(Format.html);
       validFormats.add(Format.omegat);
       validFormats.add(Format.none);
        return validFormats;
    }
    
}
