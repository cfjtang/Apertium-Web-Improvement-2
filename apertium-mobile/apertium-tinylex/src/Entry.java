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


import java.util.Vector;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class Entry {

    /**
     * Lemma in source language
     */
    private String slLemma;
    /**
     * Lemma in target language
     */
    private String tlLemma;
    /**
     * PoS in source language
     */
    private Vector slPoS;
    /**
     * PoS in target language
     */
    private Vector tlPoS;

    /**
     * Constructor
     */
    public Entry() {
        this.slPoS = new Vector();
        this.tlPoS = new Vector();
    }

    /**
     * 
     * @return
     */
    public String getSlLemma() {
        return slLemma;
    }

    /**
     * 
     * @param slLemma
     */
    public void setSlLemma(String slLemma) {
        this.slLemma = slLemma;
    }

    /**
     * 
     * @return
     */
    public String getTlLemma() {
        return tlLemma;
    }

    /**
     * 
     * @param tlLemma
     */
    public void setTlLemma(String tlLemma) {
        this.tlLemma = tlLemma;
    }

    /**
     * 
     * @return
     */
    public Vector getSlPoS() {
        return slPoS;
    }

    /**
     * 
     * @param slPoS
     */
    public void setSlPoS(Vector slPoS) {
        this.slPoS = slPoS;
    }

    /**
     * 
     * @param value
     */
    public void addSlPoS(String value) {
        this.slPoS.addElement(value);
    }

    /**
     * 
     * @return
     */
    public Vector getTlPoS() {
        return tlPoS;
    }

    /**
     * 
     * @param tlPoS
     */
    public void setTlPoS(Vector tlPoS) {
        this.tlPoS = tlPoS;
    }

    /**
     * 
     * @param value
     */
    public void addTlPoS(String value) {
        this.tlPoS.addElement(value);
    }

    /**
     * 
     * @return
     */
    public String getSlPoSString() {
        return this.vector2String(this.slPoS);
    }

    /**
     * 
     * @return
     */
    public String getTlPoSString() {
        return this.vector2String(this.tlPoS);
    }

    /**
     * 
     * @param vector
     * @return
     */
    private String vector2String(final Vector vector) {
        String str = "";
        int s = vector.size();

        if (vector.size() > 0) {
            int i = 0;
            for (i = 0; i < (s - 1); i++) {
                str += vector.elementAt(i) + ".";
            }
            str += vector.elementAt(i);
            str = "(" + str + ")";
        }
        return str;
    }
}
