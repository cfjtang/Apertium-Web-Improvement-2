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
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import subtitles.Subtitle;
import subtitles.Subtitles;
import utils.SRTReader;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class TranslationFrame extends javax.swing.JInternalFrame implements ThreadListener {

    private File file;
    static int openFrameCount = 0;
    static final int xOffset = 30,  yOffset = 30;
    private TableModel tm;
    private String sl;
    private String tl;
    private SubtitleTranslationThread stt;
    private Subtitles translation;

    /** Creates new form TranslationFrame */
    public TranslationFrame() {

        super("Subtitles #" + (++openFrameCount),
                false, //resizable
                true, //closable
                false, //maximizable
                true);//iconifiable

        //Set the window's location.
        setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
        initComponents();

    }

    public void setFile(File file) {
        this.file = file;
        subtitleFileNameLabel.setText(file.getName());
        this.saveAsButton.setVisible(false);
        this.fillOutTable();
        this.fillOutLanguagePairs();
    }

    private final void fillOutLanguagePairs() {
        // better: load this info from modes
        this.langPairSelection.removeAllItems();

        LanguagePair eues = new LanguagePair("eu", "es", "Basque", "Spanish");
        this.langPairSelection.addItem(eues);

        LanguagePair enes = new LanguagePair("en", "es", "English", "Spanish");
        LanguagePair esen = new LanguagePair("es", "en", "Spanish", "English");
        this.langPairSelection.addItem(enes);
        this.langPairSelection.addItem(esen);

        LanguagePair cyen = new LanguagePair("cy", "en", "Welsh", "English");
        this.langPairSelection.addItem(cyen);

        LanguagePair fres = new LanguagePair("fr", "es", "French", "Spanish");
        this.langPairSelection.addItem(fres);
        LanguagePair esfr = new LanguagePair("es", "fr", "Spanish", "French");
        this.langPairSelection.addItem(esfr);

        LanguagePair eseo = new LanguagePair("es", "eo", "Spanish", "Esperanto");
        this.langPairSelection.addItem(eseo);

        LanguagePair caeo = new LanguagePair("ca", "eo", "Catalan", "Esperanto");
        this.langPairSelection.addItem(caeo);

        LanguagePair enca = new LanguagePair("en", "ca", "English", "Catalan");
        this.langPairSelection.addItem(enca);
        LanguagePair caen = new LanguagePair("ca", "en", "Catalan", "English");
        this.langPairSelection.addItem(caen);

        LanguagePair ptca = new LanguagePair("pt", "ca", "Portuguese", "Catalan");
        this.langPairSelection.addItem(ptca);
        LanguagePair capt = new LanguagePair("ca", "pt", "Catalan", "Portuguese");
        this.langPairSelection.addItem(capt);

        LanguagePair ptgl = new LanguagePair("pt", "gl", "Portuguese", "Galician");
        this.langPairSelection.addItem(ptgl);
        LanguagePair glpt = new LanguagePair("gl", "pt", "Galician", "Portuguese");
        this.langPairSelection.addItem(glpt);

        LanguagePair esca = new LanguagePair("es", "ca", "Spanish", "Catalan");
        this.langPairSelection.addItem(esca);
        LanguagePair caes = new LanguagePair("ca", "es", "Catalan", "Spanish");
        this.langPairSelection.addItem(caes);

        LanguagePair esgl = new LanguagePair("es", "gl", "Spanish", "Galician");
        this.langPairSelection.addItem(esgl);
        LanguagePair gles = new LanguagePair("gl", "es", "Galician", "Spanish");
        this.langPairSelection.addItem(gles);

        LanguagePair espt = new LanguagePair("es", "pt", "Spanish", "Portuguese");
        this.langPairSelection.addItem(espt);
        LanguagePair ptes = new LanguagePair("pt", "es", "Portuguese", "Spanish");
        this.langPairSelection.addItem(ptes);

        LanguagePair esro = new LanguagePair("es","ro","Spanish","Romanian");
        this.langPairSelection.addItem(esro);
        LanguagePair roes = new LanguagePair("ro","es","Romanian","Spanish");
        this.langPairSelection.addItem(roes);

        LanguagePair frca = new LanguagePair("fr","ca","French","Catalan");
        this.langPairSelection.addItem(frca);
        LanguagePair cafr = new LanguagePair("ca","fr","Catalan","French");
        this.langPairSelection.addItem(cafr);

        LanguagePair occa = new LanguagePair("oc","ca","Occcitan","Catalan");
        this.langPairSelection.addItem(occa);
        LanguagePair caoc = new LanguagePair("ca","oc","Catalan","Occitan");
        this.langPairSelection.addItem(caoc);

        LanguagePair oces = new LanguagePair("oc","es","Occcitan","Spanish");
        this.langPairSelection.addItem(oces);
        LanguagePair esoc = new LanguagePair("es","oc","Spanish","Occitan");
        this.langPairSelection.addItem(esoc);

        LanguagePair engl = new LanguagePair("en","gl","English","Galician");
        this.langPairSelection.addItem(engl);
        LanguagePair glen = new LanguagePair("gl","en","Galician","English");
        this.langPairSelection.addItem(glen);
       
    }

    private final void fillOutTable() {
        SRTReader srtReader = new SRTReader(this.file.getAbsolutePath());
        Subtitles blockList = srtReader.read();
        String[][] data = new String[blockList.size() + 1][4];
        int i = 1;
        for (Subtitle s : blockList) {
            String id = s.getId();
            String time = s.getTime();
            String source = s.getTake();

            data[i][0] = id;
            data[i][1] = time;
            String ssource = source.replaceAll("<br/>", " // ");
            data[i][2] = ssource;

            // for debugging
            //System.out.println(i + " de " + blockList.size());
            //System.out.println("Adding: " + id + " / " + time + " / " + source);
            i++;

        }
        String[] columnNames = {"ID", "Time", "Source", "Translation"};
        tm = new DefaultTableModel(data, columnNames);
        table.setModel(tm);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progressBar = new javax.swing.JProgressBar();
        subtitleFileTitleLabel = new javax.swing.JLabel();
        subtitleFileNameLabel = new javax.swing.JLabel();
        translationButton = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        langPairSelection = new javax.swing.JComboBox();
        stopTranslationButton = new javax.swing.JButton();
        saveAsButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximizable(true);
        setResizable(true);
        setName("Form"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(apertiumsubtitletranslator.ApertiumsubtitletranslatorApp.class).getContext().getResourceMap(TranslationFrame.class);
        subtitleFileTitleLabel.setText(resourceMap.getString("subtitleFileTitleLabel.text")); // NOI18N
        subtitleFileTitleLabel.setName("subtitleFileTitleLabel"); // NOI18N

        subtitleFileNameLabel.setText(resourceMap.getString("subFileName.text")); // NOI18N
        subtitleFileNameLabel.setName("subFileName"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(apertiumsubtitletranslator.ApertiumsubtitletranslatorApp.class).getContext().getActionMap(TranslationFrame.class, this);
        translationButton.setAction(actionMap.get("translateSubtitles")); // NOI18N
        translationButton.setText(resourceMap.getString("translationButton.text")); // NOI18N
        translationButton.setName("translationButton"); // NOI18N
        translationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                translationButtonActionPerformed(evt);
            }
        });

        scrollPane.setName("scrollPane"); // NOI18N

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Time", "Source", "Translation"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table.setName("table"); // NOI18N
        scrollPane.setViewportView(table);

        langPairSelection.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        langPairSelection.setName("langPairSelection"); // NOI18N

        stopTranslationButton.setText(resourceMap.getString("stopTranslationButton.text")); // NOI18N
        stopTranslationButton.setEnabled(false);
        stopTranslationButton.setName("stopTranslationButton"); // NOI18N
        stopTranslationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopTranslationButtonActionPerformed(evt);
            }
        });

        saveAsButton.setText(resourceMap.getString("saveAsButton.text")); // NOI18N
        saveAsButton.setName("saveAsButton"); // NOI18N
        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(langPairSelection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 199, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 69, Short.MAX_VALUE)
                        .add(saveAsButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(stopTranslationButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(translationButton))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(subtitleFileTitleLabel)
                        .add(18, 18, 18)
                        .add(subtitleFileNameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 291, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(subtitleFileTitleLabel)
                    .add(subtitleFileNameLabel))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(translationButton)
                    .add(langPairSelection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(stopTranslationButton)
                    .add(saveAsButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void translationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_translationButtonActionPerformed
        LanguagePair lp = (LanguagePair) this.langPairSelection.getSelectedItem();
        stt = new SubtitleTranslationThread(this.progressBar, this.file, this.tm, lp.getSl(), lp.getTl(), this);
        stt.start();

        this.saveAsButton.setVisible(false);
        this.translationButton.setEnabled(false);
        this.stopTranslationButton.setEnabled(true);
    }//GEN-LAST:event_translationButtonActionPerformed

    private void stopTranslationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopTranslationButtonActionPerformed
        try {
            stt.interrupt();
        } catch (Exception e) {
            System.err.println(e);
        }

        this.translationButton.setEnabled(true);
        this.stopTranslationButton.setEnabled(false);
    }//GEN-LAST:event_stopTranslationButtonActionPerformed

    private void saveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
        OpenSubtitleDialog osd = new OpenSubtitleDialog();
        int returnValue = osd.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File fileToSave = osd.getSelectedFile();
            String tlFileName = fileToSave.getAbsolutePath();

            if (tlFileName != null) {
                translation.printTo(tlFileName, "UTF-8");
            }
        } else {
        }
    }//GEN-LAST:event_saveAsButtonActionPerformed

    public void threadComplete(Subtitles translation) {
        this.saveAsButton.setVisible(true);
        this.translationButton.setEnabled(true);
        this.stopTranslationButton.setEnabled(false);
        this.translation = translation;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TranslationFrame().setVisible(true);

            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox langPairSelection;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton saveAsButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton stopTranslationButton;
    private javax.swing.JLabel subtitleFileNameLabel;
    private javax.swing.JLabel subtitleFileTitleLabel;
    private javax.swing.JTable table;
    private javax.swing.JButton translationButton;
    // End of variables declaration//GEN-END:variables
}


