/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.rmi.transferobjects;

/**
 *
 * @author vmsanchez
 */
public class BinaryDocument extends Content{

    byte[] bytes;

    public BinaryDocument(byte[] bytes) {
        this.bytes = bytes;
    }


    @Override
    public boolean isBinary() {
        return true;
    }

    @Override
    public int length() {
        return bytes.length;
    }

    @Override
    public String toString() {
        return new String(bytes);
    }

    @Override
    public byte[] toByteArray() {
        return bytes;
    }

    

}
