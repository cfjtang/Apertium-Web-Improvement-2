/*
 * Copyright (C) 2008-2009 Enrique Benimeli Bofarull <ebenimeli.dev@gmail.com>
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
package apertiumsubtitletranslator;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class LanguagePair {

    private String sl;
    private String tl;
    private String slName;
    private String tlName;

    public LanguagePair(final String sl, final String tl, final String slName, final String tlName) {
        this.sl = sl;
        this.tl = tl;
        this.slName = slName;
        this.tlName = tlName;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return getSl() + "-" + getTl();
    }

    public String getName() {
        return this.slName + " - " + this.tlName;
    }

    public String toString() {
        return this.getName();
    }

    /**
     * @return the sl
     */
    public String getSl() {
        return sl;
    }

    /**
     * @param sl the sl to set
     */
    public void setSl(String sl) {
        this.sl = sl;
    }

    /**
     * @return the tl
     */
    public String getTl() {
        return tl;
    }

    /**
     * @param tl the tl to set
     */
    public void setTl(String tl) {
        this.tl = tl;
    }
}
