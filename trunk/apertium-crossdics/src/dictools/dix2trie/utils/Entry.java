/*
 * Copyright (C) 2007 Universitat d'Alacant / Universidad de Alicante
 * Author: Enrique Benimeli Bofarull
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package dictools.dix2trie.utils;

import dics.elements.dtd.SElement;
import dics.elements.utils.SElementList;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Entry {

    /**
     * 
     */
    private String key;
    
    /**
     * 
     */
    private SElementList keyAttr;
    
    /**
     * 
     */
    private String value;
    
    /**
     * 
     */
    private SElementList valueAttr;

    /**
     * 
     * @param key
     * @param value
     */
    public Entry(final String key, final String value) {
        this.key = key;
        keyAttr = new SElementList();
        this.value = value;
        valueAttr = new SElementList();
    }

    /**
     * 
     * @return The key
     */
    public final String getKey() {
        return this.key;
    }

    /**
     * 
     * @param e
     */
    public final void addKeyAttr(final SElement e) {
        this.keyAttr.add(e);
    }

    /**
     * 
     * @param keyAttr
     */
    public final void setKeyAttr(final SElementList keyAttr) {
        this.keyAttr = keyAttr;
    }

    /**
     * 
     * @return The attributes
     */
    public final SElementList getKeyAttr() {
        return this.keyAttr;
    }

    /**
     * 
     * @return The value
     */
    public final String getValue() {
        return this.value;
    }

    /**
     * 
     * @param e
     */
    public final void addValueAttr(final SElement e) {
        this.valueAttr.add(e);
    }

    /**
     * 
     * @param valueAttr
     */
    public final void setValueAttr(final SElementList valueAttr) {
        this.valueAttr = valueAttr;
    }

    /**
     * 
     * @return The list of attributes
     */
    public final SElementList getValueAttr() {
        return this.valueAttr;
    }
}
