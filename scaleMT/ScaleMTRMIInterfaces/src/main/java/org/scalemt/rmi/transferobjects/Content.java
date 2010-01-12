/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.rmi.transferobjects;

import java.io.Serializable;

/**
 *
 * @author vmsanchez
 */
public abstract class Content implements Serializable{

    public abstract boolean isBinary();
    public abstract int length();
    @Override
    public abstract String toString();
    public abstract byte[] toByteArray();
}
