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
package engine.translation;

import engine.utils.ApertiumWS;
import engine.utils.CommandExec;


/**
 * 
 * @author Enrique Benimeli Bofarull
 */
public class ApertiumTranslation {

    /**
     * Source language code
     */
    private String sl;
    /**
     * 
     */
    private String tl;
    /**
     * Mark unknown words or not
     */
    private boolean markUnknownWords;
    /**
     * 
     */
    private String lingDataPath;

    /**
     * 
     * @param sl Source language
     * @param tl Target language
     * @param tempFilePath
     * @param markUnknownWords Mark unknown words
     */
    public ApertiumTranslation(final String sl, final String tl, boolean markUnknownWords) {
        this.sl = sl;
        this.tl = tl;
        this.markUnknownWords = markUnknownWords;
    }

    /**
     * 
     * @return The translation
     */
    public final String translate_local(String text) {
        CommandExec command = null;
        String cmdStr;
        String mark = "";
        if (!this.markUnknownWords) {
            mark = " -u";
        }
        if (this.lingDataPath == null) {
            cmdStr = "echo \"" + text + "\" | apertium -f txt" + mark + " " + sl + "-" + tl;
        } else {
            cmdStr = "echo \"" + text + "\" | apertium -d " + this.lingDataPath + " -f txt" + mark + " " + sl + "-" + tl;
        }
        String[] cmd = {"/bin/sh", "-c", cmdStr};
        command = new CommandExec(cmd);
        command.exec();
        String result = command.getStdOut();
        return result;
    }

    public final String translate_ws(String text) {
        ApertiumWS aWS = new ApertiumWS(this.sl, this.tl, text);
        return aWS.translate();
    }

    /**
     * 
     * @param path
     */
    public final void setLingDataPath(final String path) {
        this.lingDataPath = path;
    }

    /**
     * 
     * @return Linguistic data path
     */
    public final String getLingDataPath() {
        return this.lingDataPath;
    }
}
