/*
 * Copyright (C) 2012 Mikel Artetxe
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
package org.apertium.caffeine;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Mikel Artetxe
 */
public class SettingsDialog extends javax.swing.JDialog {

    /** Creates new form SettingsDialog */
    public SettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        hideMarksCheckBox.setEnabled(highlightMarkedWordsCheckBox.isSelected());
        this.setLocationRelativeTo(parent);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        installButton = new javax.swing.JButton();
        checkUpdatesCheckBox = new javax.swing.JCheckBox();
        checkUpdatesButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        packagesLabel = new javax.swing.JLabel();
        editPackagesButton = new javax.swing.JButton();
        packagesTextField = new javax.swing.JTextField();
        highlightMarkedWordsCheckBox = new javax.swing.JCheckBox();
        wrapLinesCheckBox = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        hideMarksCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Settings");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        installButton.setText("Install / Uninstall language pairs...");
        installButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installButtonActionPerformed(evt);
            }
        });

        checkUpdatesCheckBox.setSelected(org.apertium.caffeine.ApertiumCaffeine.prefs.getBoolean("checkUpdates", true));
        checkUpdatesCheckBox.setText("Check updates on startup");

        checkUpdatesButton.setText("Check updates...");
        checkUpdatesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkUpdatesButtonActionPerformed(evt);
            }
        });

        packagesLabel.setText("Packages directory:");

        editPackagesButton.setText("Edit...");
        editPackagesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPackagesButtonActionPerformed(evt);
            }
        });

        packagesTextField.setText(org.apertium.caffeine.ApertiumCaffeine.prefs.get("packagesPath", null));
        packagesTextField.setEnabled(false);

        highlightMarkedWordsCheckBox.setSelected(org.apertium.caffeine.ApertiumCaffeine.prefs.getBoolean("highlightMarkedWords", true));
        highlightMarkedWordsCheckBox.setText("Highlight marked words");
        highlightMarkedWordsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highlightMarkedWordsCheckBoxActionPerformed(evt);
            }
        });

        wrapLinesCheckBox.setSelected(org.apertium.caffeine.ApertiumCaffeine.prefs.getBoolean("wrapLines", true));
        wrapLinesCheckBox.setText("Wrap lines");

        hideMarksCheckBox.setSelected(org.apertium.caffeine.ApertiumCaffeine.prefs.getBoolean("hideMarks", true));
        hideMarksCheckBox.setText("Hide marks");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(packagesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(packagesTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editPackagesButton))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(installButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(highlightMarkedWordsCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hideMarksCheckBox))
                    .addComponent(wrapLinesCheckBox)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(checkUpdatesCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                        .addComponent(checkUpdatesButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editPackagesButton)
                    .addComponent(packagesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(packagesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(installButton)
                .addGap(12, 12, 12)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(highlightMarkedWordsCheckBox)
                    .addComponent(hideMarksCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wrapLinesCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkUpdatesButton)
                    .addComponent(checkUpdatesCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void installButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_installButtonActionPerformed
        try {
            new InstallDialog(this, true).setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_installButtonActionPerformed

    private void editPackagesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPackagesButtonActionPerformed
        JFileChooser fc = new JFileChooser(ApertiumCaffeine.prefs.get("packagesPath", null));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setApproveButtonText("OK");
        fc.setDialogTitle("Choose a directory");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dir = fc.getSelectedFile();
            if (new File(dir, ".apertium-omegat").exists())
                JOptionPane.showMessageDialog(null,
                    "The selected directory is being used by Apertium OmegaT plug-in.\n"
                    + "Please, select a different one.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            else if (!dir.getPath().equals(ApertiumCaffeine.prefs.get("packagesPath", null)) &&
                    JOptionPane.showConfirmDialog(rootPane,
                    "This will remove all the language pairs that you have installed.\n"
                    + "You can manually reinstall them by copying them to the new directory,\n"
                    + "or you can install them from the app as well.\n"
                    + "Continue?",
                    "Warning", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                new File(ApertiumCaffeine.prefs.get("packagesPath", null), ".apertium-caffeine").delete();
                ApertiumCaffeine.prefs.put("packagesPath", dir.getPath());
                packagesTextField.setText(dir.getPath());
                try {
                    new File(dir, ".apertium-caffeine").createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(ApertiumCaffeine.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_editPackagesButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        ApertiumCaffeine.prefs.putBoolean("highlightMarkedWords", highlightMarkedWordsCheckBox.isSelected());
        ApertiumCaffeine.prefs.putBoolean("hideMarks", hideMarksCheckBox.isSelected());
        ApertiumCaffeine.prefs.putBoolean("wrapLines", wrapLinesCheckBox.isSelected());
        ApertiumCaffeine.prefs.putBoolean("checkUpdates", checkUpdatesCheckBox.isSelected());
        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void checkUpdatesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkUpdatesButtonActionPerformed
        try {
            UpdateDialog up = new UpdateDialog(this, true);
            if (up.updatesAvailable())
                up.setVisible(true);
            else
                JOptionPane.showMessageDialog(this, "All the installed language pairs are updated.", "Nothing to update!", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            Logger.getLogger(SettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_checkUpdatesButtonActionPerformed

    private void highlightMarkedWordsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_highlightMarkedWordsCheckBoxActionPerformed
        hideMarksCheckBox.setEnabled(highlightMarkedWordsCheckBox.isSelected());
    }//GEN-LAST:event_highlightMarkedWordsCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton checkUpdatesButton;
    private javax.swing.JCheckBox checkUpdatesCheckBox;
    private javax.swing.JButton editPackagesButton;
    private javax.swing.JCheckBox hideMarksCheckBox;
    private javax.swing.JCheckBox highlightMarkedWordsCheckBox;
    private javax.swing.JButton installButton;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel packagesLabel;
    private javax.swing.JTextField packagesTextField;
    private javax.swing.JCheckBox wrapLinesCheckBox;
    // End of variables declaration//GEN-END:variables
}
