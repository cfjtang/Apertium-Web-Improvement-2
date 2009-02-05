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

import engine.subtitles.Subtitle;
import engine.subtitles.Subtitles;
import javax.swing.JProgressBar;
import javax.swing.table.TableModel;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class SubtitleTranslator {

    private String sl;
    private String tl;
    private Subtitles subtitles;
    private Subtitles t_subtitles;
    private JProgressBar progressBar;
    private TableModel tableModel;
    private int nSubtitles;
    private int j = 1;
    static int LOCAL = 0;
    static int WS = 1;
    private int mode = LOCAL;

    public SubtitleTranslator(String sl, String tl, Subtitles subtitles) {
        this.sl = sl;
        this.tl = tl;
        this.subtitles = subtitles;
        t_subtitles = new Subtitles();
    }

    public Subtitles translate() {
        this.processSubtitles(subtitles);
        return this.t_subtitles;
    }

    public Subtitles translateGUI(JProgressBar progressBar, TableModel tableModel) {
        this.progressBar = progressBar;
        this.tableModel = tableModel;
        this.processSubtitles(subtitles);
        return this.t_subtitles;

    }

    private void processSubtitles(Subtitles blockList) {
        Subtitles toTranslate = new Subtitles();
        double i = 0;

        for (Subtitle block : blockList) {

            String take = block.getTake();
            String newTake = take + "<B/>";
            block.setTake(newTake);
            toTranslate.add(block);
            //block.print();
            String cTake = take.replaceAll("<br/>", "");
            if (this.isEndOfSentence(cTake)) {
                if (!translateBlockList(toTranslate)) {
                    return;
                }
                this.updateProgress(blockList, i);
                toTranslate = new Subtitles();
            }
            i++;

            if (Thread.currentThread().isInterrupted()) {
                return;
            }

        }
    }

    private void updateProgress(Subtitles blockList, double i) {
        if (this.progressBar != null) {

            //System.out.println(i + "/" + blockList.size());
            double dSize = (new Double(blockList.size())).doubleValue();
            double progress = (i / dSize) * 100.00;


            Double dProgress = new Double(progress);
            //System.out.println(dProgress.intValue() + " %");
            this.progressBar.setValue(dProgress.intValue());

        }
    }

    private boolean isEndOfSentence(final String line) {
        if (line.endsWith(".")) {
            return true;
        }
        if (line.endsWith("\"")) {
            return true;
        }
        if (line.endsWith("?")) {
            return true;
        }
        if (line.endsWith("...")) {
            return true;
        }
        if (line.endsWith("!")) {
            return true;
        }
        return false;
    }

    public boolean translateBlockList(Subtitles blockList) {
        String sentence = "";

        try {
            for (Subtitle block : blockList) {
                sentence = sentence + block.getTake();

            }
            sentence = sentence.replaceAll("\"", "&quot;");
            String translation = translateSentence(sentence);
            translation = translation.replaceAll("&quot;", "\"");
            String[] parts = translation.split("<B/>");

            int i = 0;
            this.nSubtitles = blockList.size();


            for (Subtitle block : blockList) {

                String id = block.getId();
                String time = block.getTime();
                String trans = parts[i];

                Subtitle blockTrans = new Subtitle();
                blockTrans.setId(id);
                blockTrans.setTime(time);
                //blockTrans.setTake(trans);
                blockTrans.addLine(trans);
                t_subtitles.add(blockTrans);

                //System.out.println("valueAt( " + j + ", 3 ) = " + trans);
                String strans = trans.replaceAll("<br/>", " | ");
                if (this.tableModel != null) {
                    this.tableModel.setValueAt(strans, j, 3);
                    j++;
                }
                //blockTrans.print();
                i++;


            }

        } catch (Exception e) {
            System.err.println("Error: subtitles can't be translated. Please check if Apertium is installed.");
            return false;
        }
        return true;
    }

    public String translateSentence(String sentence) {
        ApertiumTranslation translation = new ApertiumTranslation(this.sl, this.tl, false);
        String result = "";
        if (this.mode == this.LOCAL) {
            result = translation.translate_local(sentence);
        } else {
            result = translation.translate_ws(sentence);
        }
        return result;
    }

    /**
     * @return the mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(int mode) {
        this.mode = mode;
    }
}
