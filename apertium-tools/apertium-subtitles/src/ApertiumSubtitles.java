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

import translation.SubtitleTranslator;
import subtitles.Subtitles;
import utils.SRTReader;

/**
 * 
 * @author Enrique Benimeli Bofarull
 */
public class ApertiumSubtitles {

    /**
     * Source language file name
     */
    private String slFileName = "";
    /**
     * Target language file name
     */
    private String tlFileName;
    /**
     * Source language - Target language code
     */
    private String sltl;
    /**
     * Source language code
     */
    private String sl;
    /**
     * Target language code
     */
    private String tl;

    /**
     * Main class
     * @param args The arguments
     */
    public static void main(String[] args) {
        ApertiumSubtitles apertiumSub = new ApertiumSubtitles();
        apertiumSub.processArguments(args);
        apertiumSub.translate();
    }

    public final void processArguments(String[] args) {
        if (args.length >= 3) {
            this.slFileName = args[0];
            this.sltl = args[1];
            this.getSLTLCodes();
            if (args.length == 3) {
                this.tlFileName = new String(args[2]);
            }

        } else {
            System.err.println("Usage: apertium-subtitles <SRT-filename> <language-pair> <output>");
            System.exit(-1);
        }
    }

    private void getSLTLCodes() {
        String[] codes = sltl.split("-");
        this.sl = codes[0];
        this.tl = codes[1];
    }

    private void translate() {
        SRTReader srtReader = new SRTReader(this.slFileName);
        //String encoding = srtReader.getFileEncoding();
        Subtitles blockList = srtReader.read();
        SubtitleTranslator subTrans = new SubtitleTranslator(this.sl, this.tl, blockList);
        Subtitles translation = subTrans.translate();
        if (this.tlFileName != null) {
            System.out.println("Translation: '" + this.tlFileName + "'");
            translation.printTo(this.tlFileName, "UTF-8");
        }
    }
}
