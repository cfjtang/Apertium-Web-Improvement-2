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
import javax.swing.JProgressBar;
import javax.swing.table.TableModel;
import engine.subtitles.Subtitles;
import engine.translation.SubtitleTranslator;
import engine.utils.SRTReader;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class SubtitleTranslationThread extends Thread {

    private JProgressBar progressBar;
    private File file;
    private TableModel tableModel;
    private String sl;
    private String tl;
    private ThreadListener theListener;
    private int mode;

    public SubtitleTranslationThread(JProgressBar progressBar, File file, TableModel tableModel, String sl, String tl, ThreadListener threadListener, int mode) {
        this.progressBar = progressBar;
        this.file = file;
        this.tableModel = tableModel;
        this.sl = sl;
        this.tl = tl;
        this.theListener = threadListener;
        this.mode = mode;
    }

    @Override
    public void run() {
        try {
            SRTReader srtReader = new SRTReader(this.file.getAbsolutePath());
            Subtitles blockList = srtReader.read();
            SubtitleTranslator subTrans = new SubtitleTranslator(sl, tl, blockList);
            subTrans.setMode(mode);

            progressBar.setMaximum(100);
            progressBar.setMinimum(0);
            Subtitles translation = subTrans.translateGUI(progressBar, this.tableModel);
            progressBar.setValue(100);
            Thread.sleep(1000);

            if (this.theListener != null) {
                this.theListener.threadComplete(translation);
            }
        } catch (InterruptedException e) {
            //System.err.print("");
        }
    }
}
