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

import engine.subtitles.Subtitle;
import engine.subtitles.Subtitles;
import engine.utils.SRTReader;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.table.TableModel;

/**
 *
 * @author Enrique Benimeli Bofarull
 */
public class TranslationFrame extends javax.swing.JInternalFrame implements ThreadListener {

    private File file;
    static int openFrameCount = 0;
    static final int xOffset = 30,  yOffset = 30;
    private TableModel tm;
    private SubtitleTranslationThread stt;
    private String fileEncoding;
    private Subtitles translation;
    ArrayList<Mode> modes;
    ArrayList<Mode> modesWS = new ArrayList<Mode>();
    private int mode;
    SubtitleTableModelListener tml;

    /** Creates new form TranslationFrame */
    public TranslationFrame(ArrayList<Mode> modes) {

        super(java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("saveAsButton.text") + (++openFrameCount),
                false, //resizable
                true, //closable
                false, //maximizable
                true);//iconifiable

        //Set the window's location.
        setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
        initComponents();
        initMyComponents();
        this.modes = modes;

    }

    private final void initMyComponents() {
    }

    public void setFile(File file) {
        this.file = file;
        subtitleFileNameLabel.setText(file.getName());
        this.title = file.getName();
        this.saveAsButton.setVisible(false);
        this.fillOutTable();
        this.fillOutLanguagePairs();
    }

    private final void fillOutLanguagePairs() {
        this.langPairSelection.removeAllItems();
        for (Mode m : modes) {
            LanguagePair lp = new LanguagePair(m.getSL(), m.getTL(), m.getSlName(), m.getTlName());
            this.langPairSelection.addItem(lp);
        }
    }

    private final void addModeWS(String sl, String tl, String fsl, String ftl, String dir) {
        if (dir.contains("LR")) {
            Mode mode = new Mode(null);
            mode.setSl(sl);
            mode.setTl(tl);
            mode.setSlName(fsl);
            mode.setTlName(ftl);
            modesWS.add(mode);
        }

        if (dir.contains("RL")) {
            Mode mode2 = new Mode(null);
            mode2.setSl(tl);
            mode2.setTl(sl);
            mode2.setSlName(ftl);
            mode2.setTlName(fsl);
            modesWS.add(mode2);
        }


    }

    private final void fillOutLanguagePairsWS() {
        this.langPairSelection.removeAllItems();

        this.addModeWS("es", "ca", "Spanish", "Catalan", "LRRL");
        this.addModeWS("en", "ca", "English", "Catalan", "LRRL");
        this.addModeWS("en", "es", "English", "Spanish", "LRRL");
        this.addModeWS("es", "gl", "Spanish", "Galician", "LRRL");
        this.addModeWS("es", "pt", "Spanish", "Portuguese", "LRRL");
        this.addModeWS("es", "fr", "Spanish", "French", "LRRL");
        this.addModeWS("es", "ro", "Spanish", "Romanian", "LRRL");
        this.addModeWS("es", "oc", "Spanish", "Occitan", "LRRL");
        this.addModeWS("fr", "ca", "French", "Catalan", "LRRL");
        this.addModeWS("oc", "ca", "Occitan", "Catalan", "LRRL");
        this.addModeWS("cy", "en", "Welsh", "English", "LRRL");
        this.addModeWS("ca", "eo", "Catalan", "Esperanto", "LR");
        this.addModeWS("es", "eo", "Spanish", "Esperanto", "LR");
        this.addModeWS("pt", "ca", "Portuguese", "Catalan", "LRRL");
        this.addModeWS("pt", "gl", "Portuguese", "Galician", "LRRL");
        this.addModeWS("eu", "es", "Basque", "Spanish", "LRRL");

        for (Mode m : modesWS) {
            LanguagePair lp = new LanguagePair(m.getSL(), m.getTL(), m.getSlName(), m.getTlName());
            this.langPairSelection.addItem(lp);
        }
    }

    private final void fillOutTable() {
        SRTReader srtReader = new SRTReader(this.file.getAbsolutePath());
        this.fileEncoding = srtReader.getFileEncoding();
        Subtitles blockList = srtReader.read();
        String[][] data = new String[blockList.size() + 1][4];
        int i = 1;
        for (Subtitle s : blockList) {
            String id = s.getId();
            String time = s.getTime();
            String source = s.getTake();

            data[i][0] = id;
            data[i][1] = time;
            String ssource = source.replaceAll("<br/>", " | ");
            data[i][2] = ssource;
            i++;
        }
        String[] columnNames = {"ID", java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Time"), java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Source"), java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation").getString("Translation")};
        //tm = new DefaultTableModel(data, columnNames);
        tm = new SubtitleTableModel(data, columnNames, blockList);
        tml = new SubtitleTableModelListener(table, this.translation);
        table.setModel(tm);
        table.getModel().addTableModelListener(tml);


    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        progressBar = new javax.swing.JProgressBar();
        subtitleFileTitleLabel = new javax.swing.JLabel();
        subtitleFileNameLabel = new javax.swing.JLabel();
        translationButton = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        langPairSelection = new javax.swing.JComboBox();
        stopTranslationButton = new javax.swing.JButton();
        saveAsButton = new javax.swing.JButton();
        localApertiumOption = new javax.swing.JRadioButton();
        wsApertiumOption = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximizable(true);
        setResizable(true);
        setName("Form"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("apertiumsubtitletranslator/resources/locale/translation"); // NOI18N
        subtitleFileTitleLabel.setText(bundle.getString("subtitlesFileNameLabel")); // NOI18N
        subtitleFileTitleLabel.setName("subtitleFileTitleLabel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(apertiumsubtitletranslator.ApertiumsubtitletranslatorApp.class).getContext().getResourceMap(TranslationFrame.class);
        subtitleFileNameLabel.setText(resourceMap.getString("subFileName.text")); // NOI18N
        subtitleFileNameLabel.setName("subFileName"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(apertiumsubtitletranslator.ApertiumsubtitletranslatorApp.class).getContext().getActionMap(TranslationFrame.class, this);
        translationButton.setAction(actionMap.get("translateSubtitles")); // NOI18N
        translationButton.setText(bundle.getString("translationButton.text")); // NOI18N
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

        stopTranslationButton.setText(bundle.getString("stopTranslationButton.text")); // NOI18N
        stopTranslationButton.setEnabled(false);
        stopTranslationButton.setName("stopTranslationButton"); // NOI18N
        stopTranslationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopTranslationButtonActionPerformed(evt);
            }
        });

        saveAsButton.setText(bundle.getString("saveAsButton.text")); // NOI18N
        saveAsButton.setName("saveAsButton"); // NOI18N
        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(localApertiumOption);
        localApertiumOption.setSelected(true);
        localApertiumOption.setText(resourceMap.getString("localApertiumOption.text")); // NOI18N
        localApertiumOption.setName("localApertiumOption"); // NOI18N
        localApertiumOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localApertiumOptionActionPerformed(evt);
            }
        });

        buttonGroup1.add(wsApertiumOption);
        wsApertiumOption.setText(resourceMap.getString("wsApertiumOption.text")); // NOI18N
        wsApertiumOption.setName("wsApertiumOption"); // NOI18N
        wsApertiumOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wsApertiumOptionActionPerformed(evt);
            }
        });
        wsApertiumOption.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                wsApertiumOptionMouseClicked(evt);
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
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(localApertiumOption)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(wsApertiumOption)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 58, Short.MAX_VALUE)
                        .add(saveAsButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(stopTranslationButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(translationButton))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1051, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(subtitleFileTitleLabel)
                        .add(18, 18, 18)
                        .add(subtitleFileNameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 291, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1051, Short.MAX_VALUE))
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
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(translationButton)
                        .add(langPairSelection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(stopTranslationButton)
                        .add(saveAsButton))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(localApertiumOption)
                        .add(wsApertiumOption)))
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
        stt = new SubtitleTranslationThread(this.progressBar, this.file, this.tm, lp.getSl(), lp.getTl(), this, mode);
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

    private void wsApertiumOptionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wsApertiumOptionMouseClicked
        // TODO add your handling code here:
}//GEN-LAST:event_wsApertiumOptionMouseClicked

    private void wsApertiumOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wsApertiumOptionActionPerformed
        // TODO add your handling code here:
        this.mode = 1;
        this.fillOutLanguagePairsWS();
    }//GEN-LAST:event_wsApertiumOptionActionPerformed

    private void localApertiumOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localApertiumOptionActionPerformed
        // TODO add your handling code here:
        this.mode = 0;
        this.fillOutLanguagePairs();
    }//GEN-LAST:event_localApertiumOptionActionPerformed

    public void threadComplete(Subtitles translation) {
        this.saveAsButton.setVisible(true);
        this.translationButton.setEnabled(true);
        this.stopTranslationButton.setEnabled(false);
        this.translation = translation;
        this.tml.setSubtitles(translation);

    }

    // main class is not needed
    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {

    public void run() {
    new TranslationFrame().setVisible(true);

    }
    });
    }
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox langPairSelection;
    private javax.swing.JRadioButton localApertiumOption;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton saveAsButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton stopTranslationButton;
    private javax.swing.JLabel subtitleFileNameLabel;
    private javax.swing.JLabel subtitleFileTitleLabel;
    private javax.swing.JTable table;
    private javax.swing.JButton translationButton;
    private javax.swing.JRadioButton wsApertiumOption;
    // End of variables declaration//GEN-END:variables
}


