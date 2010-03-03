/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.rmi.transferobjects;

import java.io.Serializable;
import java.util.Set;

/**
 * Something that can be translated
 *
 * @author vmsanchez
 */
public abstract class Content implements Serializable{

    private Format format;
    private int length;

    public Content(Format format) throws IllegalArgumentException{
        length=-1;
        setFormat(format);
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) throws IllegalArgumentException{
        if(!getValidFormats().contains(format))
            throw new IllegalArgumentException("Format not valid");
        this.format = format;
        length=-1;
    }

    public int getLength() {
        if(length==-1)
            length=calculateLength();
        return length;
    }



    protected abstract Set<Format> getValidFormats();
    public abstract boolean isBinary();
    @Override
    public abstract String toString();
    public abstract byte[] toByteArray();
    protected abstract int calculateLength();
}
