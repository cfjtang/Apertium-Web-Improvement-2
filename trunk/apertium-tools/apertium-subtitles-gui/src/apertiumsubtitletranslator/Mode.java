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

import java.io.File;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
class Mode {

    private String name;
    private File file;
    private String fileName;
    private String sltl;
    private String sl;
    private String tl;
    private String slName;
    private String tlName;

    public Mode(String fileName) {
        this.getLangCodeFromFileName(fileName);
    }

    private final void getLangCodeFromFileName(final String fileName) {
        try {
            this.fileName = fileName;
            String[] txt = fileName.split("\\.");
            sltl = txt[0];
            String[] sltlStr = sltl.split("-");

            if (sltlStr.length == 2) {
                // like 'es-ca'
                setSl(sltlStr[0]);
                setTl(sltlStr[1]);
            } else {

                if (this.isVariant(sltlStr[0])) {
                    // like 'eco-fr-es'
                    setSl(sltlStr[0] + "-" + sltlStr[1]);
                    setTl(sltlStr[2]);
                } else {
                    if (this.isVariant(sltlStr[2])) {
                        // like 'ca-es-multi'
                        setSl(sltlStr[0]);
                        setTl(sltlStr[1] + "-" + sltlStr[2]);
                    }
                }
            }
            this.slName = this.getFullName(getSl());
            this.tlName = this.getFullName(getTl());


        } catch (ArrayIndexOutOfBoundsException aioofe) {
        } catch (Exception e) {
        }
    }

    private final boolean isVariant(final String code) {
        if (code.equals("multi")) {
            return true;
        }
        if (code.equals("eco")) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    public final String getSLTL() {
        return sltl;
    }

    public final String getSL() {
        return getSl();
    }

    public final String getTL() {
        return getTl();
    }

    /**
     * @return the slName
     */
    public String getSlName() {
        return slName;
    }

    /**
     * @param slName the slName to set
     */
    public void setSlName(String slName) {
        this.slName = slName;
    }

    /**
     * @return the tlName
     */
    public String getTlName() {
        return tlName;
    }

    /**
     * @param tlName the tlName to set
     */
    public void setTlName(String tlName) {
        this.tlName = tlName;
    }

    private String getFullName(String code) {
        if (code.equals("es")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Spanish");
        }
        if (code.equals("oc_aran")) {
            return "Occitan (Aranese)";
        }
        if (code.equals("eco-es")) {
            return "(Eco.) Spanish";
        }
        if (code.equals("eco-ca")) {
            return "(Eco.) Catalan";
        }
        if (code.equals("es-multi")) {
            return "Spanish (multi)";
        }
        if (code.equals("ca-multi")) {
            return "Catalan (multi)";
        }
        if (code.equals("ca")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Catalan");
        }
        if (code.equals("en")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("English");
        }
        if (code.equals("ro")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Romanian");
        }
        if (code.equals("gl")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Galician");
        }
        if (code.equals("pt")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Portuguese");
        }
        if (code.equals("pt_BR")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Portuguese_(Brazil)");
        }
        if (code.equals("fr")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("French");
        }
        if (code.equals("eco-fr")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("French_(eco)");
        }
        if (code.equals("oc")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Occitan");
        }
        if (code.equals("it")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Italian");
        }
        if (code.equals("af")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Afrikaans");
        }
        if (code.equals("eu")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Basque");
        }
        if (code.equals("eo")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Esperanto");
        }
        if (code.equals("cy")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Welsh");
        }
        if (code.equals("ast")) {
            return java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Asturian");
        }
        return code;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
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