/*
 *  ApertiumServer. Highly scalable web service implementation for Apertium.
 *  Copyright (C) 2009  Víctor Manuel Sánchez Cartagena
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gsoc.apertium.translationengines.rmi.transferobjects;

import java.io.Serializable;
 
/**
 * Translation language pair. Source language and target language.
 * @author vitaka
 */
public class LanguagePair implements Serializable{

    /**
     * Source language's code
     */
    private String source;

    /**
     * Target language's code
     */
    private String target;

    public LanguagePair(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public LanguagePair(String pair,char[] delimiter)
    {
        String[] langs= pair.split(new String(delimiter));
        if(langs!=null && langs.length==2)
        {
            this.source=langs[0];
            this.target=langs[1];
        }
        else
            throw new IllegalArgumentException();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LanguagePair))
            return false;
        else
        {
            LanguagePair comp = (LanguagePair) obj;
            return ((this.getSource()==null ? comp.getSource()==null : this.getSource().equals(comp.getSource()) ) && (this.getTarget()==null ? comp.getTarget()==null : this.getTarget().equals(comp.getTarget()) ));
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 29 * hash + (this.target != null ? this.target.hashCode() : 0);
        return hash;
    }



    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return getSource()+"-"+getTarget();
    }



    

}
