/*
 * ApertiumView.java
 */

package apertiumview;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.jdesktop.application.*;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Collections;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * See  http://wiki.apertium.org/wiki/Apertium-vju 
 */

/**
 * The application's main frame.
 */
public class ApertiumView extends FrameView {

    ArrayList<TextWidget> textWidgets = new ArrayList<TextWidget>();
    ArrayList<JSplitPane> splitPanes = new ArrayList<JSplitPane>();


    JSplitPane lastSplitPane;

    boolean startingUp = true;
    
    public ApertiumView(SingleFrameApplication app) {
        super(app);
        initComponents();

        try {
            String mpref = prefs.get("modeFiles", null);
            if (mpref != null) {
                for (String fn : mpref.split("\n")) {
                    loadMode(new File(fn));
                }
            } else {
                warnUser("Welcome to Apertium - JView.\nIt seems this is first time you run this program. I will therefore try to search install language pairs ('modes') from standart places. Use the File menu to install others.");
                LinkedHashSet<File> fal = new LinkedHashSet<File>();
                try {
                    fal.addAll(Arrays.asList(new File("/usr/share/apertium/modes/").listFiles()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    fal.addAll(Arrays.asList(new File("/usr/local/share/apertium/modes/").listFiles()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    fal.addAll(Arrays.asList(new File(".").listFiles()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (File f : fal) {
                    if (f.getName().endsWith(".mode")) {
                        loadMode(f);
                    }
                }
                editModesMenuItemActionPerformed(null);
            }

            
            if (modes.isEmpty()) {
                warnUser("No language pairs could be loaded. Making a 'fake' mode.\nPlease use the File menu to install others.");
                Mode m = new Mode();
                modes.add(m);
                modesComboBox.addItem(m);                
            }

            int idx = prefs.getInt("modesComboBox", -1);
            if (idx >= 0 && idx<modes.size()) {
                modesComboBox.setSelectedIndex(idx);
            }
            showCommandsCheckBox.setSelected(prefs.getBoolean("showCommands", true));
            showCommandsCheckBoxActionPerformed(null); // is this necesary?

        } catch (Exception e) {
            warnUser("An error occured during startup:\n\n"+e.getLocalizedMessage());
        }
        
        
        startingUp = false;
        
        textWidget1.setText(prefs.get("inputText", textWidget1.getText()));
        textWidget1.commandTextField.requestFocus();
        textWidget1.textEditor.addFocusListener(scrollToVisibleFocusListener); // done here to avoid multiple adds

    }
    
    
    public void saveSettings() {
        System.out.println("saveSettings()");
        prefs.putBoolean("showCommands", showCommandsCheckBox.isSelected());
        prefs.putInt("modesComboBox",modesComboBox.getSelectedIndex());
        String divLoc = "";
        for (JSplitPane p : splitPanes) divLoc += ","+p.getDividerLocation();
        prefs.put("dividerLocation", divLoc);
        prefs.put("inputText", textWidget1.getText());
    }
    
    
    FocusListener scrollToVisibleFocusListener = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            JComponent comp = (JComponent) e.getSource();
            // Ugly hack to get the enclosing TextWidget
            TextWidget tw = (TextWidget) ((JComponent) e.getSource()).getParent().getParent().getParent();
            // Scroll so that the TextWidget's scoll pane (containing the text area) is fully visible
            Rectangle b = tw.jScrollPane1.getBounds();
            Rectangle b2 = tw.getBounds();
            //System.out.println("b = " + b);
            //System.out.println("b2 = " + b2);
            b.x += b2.x;
            b.y += b2.y;
            tw.scrollRectToVisible(b);
        }
    };
    
    private void showMode(Mode m) {
        if (m==null) {
            return;
        }        

        if (m.commandChain.length+1 != textWidgets.size()) {
            // Number of text widgets are different, dispose all and regenerate
            lastSplitPane = jSplitPane1;
            jSplitPane1.setBottomComponent(null);

            TextWidget lastTextWidget = textWidget1;
            lastTextWidget.textEditor.setFocusAccelerator('0');
            textWidget1.setCommand("");
            textWidget1.priority = 0;

            textWidgets.clear();
            splitPanes.clear();

            textWidgets.add(textWidget1);
            splitPanes.add(jSplitPane1);
            
            String[] divLocs = prefs.get("dividerLocation", "").split(",");
            if (divLocs.length>1) try {
                jSplitPane1.setDividerLocation(Integer.parseInt(divLocs[1]));
            } catch (Exception e) { e.printStackTrace(); }
              else jSplitPane1.setDividerLocation(60);

            for (int i = 0; i < m.commandChain.length; i++) {
                TextWidget tw = new TextWidget();
                textWidgets.add(tw);
                tw.priority = i+1;
                lastTextWidget.next = tw;
                lastTextWidget = tw;
                lastTextWidget.textEditor.setFocusAccelerator((char)('0'+i+1));
                lastTextWidget.textEditor.addFocusListener(scrollToVisibleFocusListener);

                if (i < m.commandChain.length - 1) {
                    JSplitPane sp = new JSplitPane();
                    sp.setBorder(BorderFactory.createEmptyBorder());
                    sp.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
                    sp.setOneTouchExpandable(true);
                    sp.setContinuousLayout(true);
                    sp.setTopComponent(tw);
                    lastSplitPane.setBottomComponent(sp);
                    splitPanes.add(sp);
                    lastSplitPane = sp;
                    if (divLocs.length>2+i) try {
                        sp.setDividerLocation(Integer.parseInt(divLocs[2+i]));
                    } catch (Exception e) { e.printStackTrace(); }
                       else sp.setDividerLocation(60);
                } else {
                    lastSplitPane.setBottomComponent(tw);
                    lastTextWidget.textEditor.setFocusAccelerator('9');
                }
            }

        }

        TextWidget firstWithChangedCommand = null;
        TextWidget lastTextWidget = textWidget1;

        for (int i = 0; i < m.commandChain.length; i++) {
            String cmd = m.commandChain[i];            
            cmd = cmd.replaceAll("\\$1", markUnknownWordsCheckBox.isSelected()?"-g":"-n");
            TextWidget tw = textWidgets.get(i+1);

            if (!cmd.equals(tw.getCommand()) && firstWithChangedCommand==null) {
                //System.out.println("cmd="+cmd + "  ==? "+tw.getCommand());
                firstWithChangedCommand = lastTextWidget;
            }
            tw.setCommand(cmd);
            lastTextWidget = tw;
        }

        if (startingUp) return;

        if (firstWithChangedCommand != null) {
            //System.out.println("firstWithChangedCommand="+firstWithChangedCommand.priority + "  ==? "+firstWithChangedCommand.getText());
            firstWithChangedCommand.textChg(true);
        }
        
    }
    
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = ApertiumViewMain.getApplication().getMainFrame();
            aboutBox = new ApertiumViewAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        ApertiumViewMain.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        modesComboBox = new javax.swing.JComboBox();
        fitToTextButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textWidgetsPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        textWidget1 = new apertiumview.TextWidget();
        copyTextButton = new javax.swing.JButton();
        showCommandsCheckBox = new javax.swing.JCheckBox();
        markUnknownWordsCheckBox = new javax.swing.JCheckBox();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        loadModeMenuItem = new javax.swing.JMenuItem();
        editModesMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

        mainPanel.setAutoscrolls(true);

        modesComboBox.setToolTipText("Change mode (language)");
        modesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modesComboBoxActionPerformed(evt);
            }
        });

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(apertiumview.ApertiumViewMain.class).getContext().getActionMap(ApertiumView.class, this);
        fitToTextButton.setAction(actionMap.get("fitToText")); // NOI18N
        fitToTextButton.setMnemonic('I');
        fitToTextButton.setText("Fit text");
        fitToTextButton.setMargin(new java.awt.Insets(0, 4, 0, 4));

        textWidgetsPanel.setPreferredSize(new java.awt.Dimension(200, 93));

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(200, 93));

        textWidget1.setPreferredSize(new java.awt.Dimension(200, 93));
        jSplitPane1.setTopComponent(textWidget1);

        javax.swing.GroupLayout textWidgetsPanelLayout = new javax.swing.GroupLayout(textWidgetsPanel);
        textWidgetsPanel.setLayout(textWidgetsPanelLayout);
        textWidgetsPanelLayout.setHorizontalGroup(
            textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 530, Short.MAX_VALUE)
            .addGroup(textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE))
        );
        textWidgetsPanelLayout.setVerticalGroup(
            textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 412, Short.MAX_VALUE)
            .addGroup(textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(textWidgetsPanel);

        copyTextButton.setAction(actionMap.get("copyText")); // NOI18N
        copyTextButton.setMnemonic('C');
        copyTextButton.setText("Copy all");
        copyTextButton.setMargin(new java.awt.Insets(0, 4, 0, 4));

        showCommandsCheckBox.setMnemonic('S');
        showCommandsCheckBox.setSelected(true);
        showCommandsCheckBox.setText("Show commands");
        showCommandsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showCommandsCheckBoxActionPerformed(evt);
            }
        });

        markUnknownWordsCheckBox.setMnemonic('M');
        markUnknownWordsCheckBox.setSelected(true);
        markUnknownWordsCheckBox.setText("Mark unknown words");
        markUnknownWordsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                markUnknownWordsCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(markUnknownWordsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showCommandsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copyTextButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fitToTextButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addComponent(modesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(copyTextButton)
                    .addComponent(fitToTextButton)
                    .addComponent(modesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(markUnknownWordsCheckBox)
                    .addComponent(showCommandsCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE))
        );

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        fileMenu.add(exitMenuItem);

        loadModeMenuItem.setAction(actionMap.get("loadMode")); // NOI18N
        fileMenu.add(loadModeMenuItem);

        editModesMenuItem.setText("Edit modes");
        editModesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editModesMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(editModesMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setMnemonic('V');
        helpMenu.setText("View");

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

private void modesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modesComboBoxActionPerformed
// TODO add your handling code here:
    showMode((Mode) modesComboBox.getSelectedItem());
}//GEN-LAST:event_modesComboBoxActionPerformed

private void editModesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editModesMenuItemActionPerformed
    String mpref = "";
    for (Mode mo : modes) mpref = mpref + mo.file+"\n";
    JTextArea ta = new JTextArea(mpref);
    int ret = JOptionPane.showConfirmDialog(mainPanel,
        new JScrollPane(ta), "Edit the list of modes",
        JOptionPane.OK_CANCEL_OPTION);
    if (ret==JOptionPane.OK_OPTION) {
        modes.clear();
        modesComboBox.removeAllItems();
        mpref = ta.getText();
        for (String fn : mpref.split("\n")) {
            loadMode(new File(fn));
        }
        prefs.put("modeFiles", mpref);    
    }
}//GEN-LAST:event_editModesMenuItemActionPerformed

private void markUnknownWordsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markUnknownWordsCheckBoxActionPerformed
    showMode((Mode) modesComboBox.getSelectedItem());   
}//GEN-LAST:event_markUnknownWordsCheckBoxActionPerformed

private void showCommandsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showCommandsCheckBoxActionPerformed

    boolean show = showCommandsCheckBox.isSelected();
    //System.out.println("show = " + show);
    for (TextWidget w : textWidgets) {
        w.commandTextField.setVisible(show);
        w.zoomButton.setVisible(show);   
        w.freezeCheckBox.setVisible(show);   
        w.setSize(w.getSize());
    }
    textWidget1.commandTextField.setVisible(false);
    textWidget1.zoomButton.setVisible(false);            
    textWidget1.freezeCheckBox.setVisible(false);   
    mainPanel.validate();
}//GEN-LAST:event_showCommandsCheckBoxActionPerformed


    private int insetHeight(JComponent c) {
        Insets i = c.getInsets();
        return i.top + i.bottom;
    } 


    @Action
    public void fitToText() {
        //System.out.println();
        //System.out.println("============fitToText()");
        int toth = 0;
        for (TextWidget w : textWidgets) {

            Dimension d = w.textEditor.getPreferredSize();
            d.height += insetHeight(w.textEditor) + insetHeight(w) + insetHeight(w.jScrollPane1) +6;

            if (w.commandTextField.isVisible()) {
                d.height += w.commandTextField.getPreferredSize().height;
                d.height += insetHeight(w.commandTextField);
            }

            //System.out.println("d="+d.height);
            
            if (w.getStatus()==w.STATUS_EQUAL) d.height = 0;
            
            w.setMinimumSize(d);
            w.setPreferredSize(d);
            toth += d.height;
        }


        for (JSplitPane s : splitPanes) if (s.getDividerLocation()>2) {
            //System.out.println("getDividerLocation()"+s.getDividerLocation());
            Dimension d = s.getTopComponent().getMinimumSize();
            s.setDividerLocation(d.height);
            //System.out.println("dp="+d);
            toth += s.getDividerSize() + 2;
            //System.out.println("getDividerLocation()"+s.getDividerLocation());
        }
        
        Dimension d = textWidgetsPanel.getSize();
        d.height = toth; //Math.max(toth, d.height);
        textWidgetsPanel.setSize(d);
        d = textWidgetsPanel.getPreferredSize();
        d.height = toth;
        textWidgetsPanel.setMinimumSize(d);
        textWidgetsPanel.setPreferredSize(d);

        mainPanel.validate();

        for (TextWidget w : textWidgets) {
            w.jScrollPane1.setMinimumSize(null);
            w.textEditor.setMinimumSize(null);
            w.setMinimumSize(new Dimension(5,5));
            w.jScrollPane1.setPreferredSize(null);
            w.textEditor.setPreferredSize(null);
            w.setPreferredSize(null);
        }
    
    }

    JFileChooser modeFileChooser;
    
    @Action
    public void loadMode() {
        if (modeFileChooser == null) {
            modeFileChooser = new JFileChooser(prefs.get("lastModePath", "."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Apertium mode files", "mode");
            modeFileChooser.setFileFilter(filter);
            modeFileChooser.setMultiSelectionEnabled(true);
        }
        int returnVal = modeFileChooser.showOpenDialog(mainPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                    modeFileChooser.getSelectedFile().getName());
            prefs.put("lastModePath", modeFileChooser.getSelectedFile().getParent());
            File fs[] = modeFileChooser.getSelectedFiles();
            //System.out.println("txt=" + fs.length);
            for (File f : fs)  {
                loadMode(f);
            }
            String mpref = "";
            for (Mode mo : modes) mpref = mpref + mo.file+"\n";
            prefs.put("modeFiles", mpref);
            
            
        }
    }
    
    public final static Preferences prefs = Preferences.userNodeForPackage(ApertiumView.class);
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton copyTextButton;
    private javax.swing.JMenuItem editModesMenuItem;
    private javax.swing.JButton fitToTextButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenuItem loadModeMenuItem;
    javax.swing.JPanel mainPanel;
    private javax.swing.JCheckBox markUnknownWordsCheckBox;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JComboBox modesComboBox;
    private javax.swing.JCheckBox showCommandsCheckBox;
    private apertiumview.TextWidget textWidget1;
    private javax.swing.JPanel textWidgetsPanel;
    // End of variables declaration//GEN-END:variables

    private JDialog aboutBox;

    ArrayList<Mode> modes = new ArrayList<Mode>();

    public static String legu(File fil) throws IOException {
	FileChannel fc = new FileInputStream(fil).getChannel();
	MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fil.length());
	//CharBuffer cb = Charset.forName("ISO-8859-1").decode(bb);
	CharBuffer cb = Charset.forName("UTF-8").decode(bb);
	return new String(cb.array());
    }

    
    private void loadMode(File f) {
        try {
            String txt = legu(f).trim();
            Mode m = new Mode();
            m.commandChain = txt.split("\\|");
            m.name = f.getName();
            m.file = f;
            //System.out.println("txt=" + txt);
            modes.add(m);
            modesComboBox.addItem(m);            
        } catch (IOException ex) {
            Logger.getLogger(ApertiumView.class.getName()).log(Level.INFO, null, ex);
            warnUser("Loading of mode "+f+" failed:\n\n"+ex.toString()+"\n\nContinuing without this mode.");
        }
    }

    @Action
    public void copyText() {
        String tottxt = "";
        String lasttxt = "";
        for (TextWidget w : textWidgets) {
            if (!w.getVisibleRect().isEmpty()) {
                String txt = w.getText();
                if (!txt.equals(lasttxt)) {
                    tottxt += txt + "\n";
                }
                lasttxt = txt;
            }
        }
        
        System.out.println(tottxt);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(tottxt), null);
		JOptionPane.showMessageDialog(mainPanel,new JTextArea(tottxt),"Contents of clipboard", JOptionPane.INFORMATION_MESSAGE);
    }


	public void warnUser(String txt) {
		System.out.println("warnUser("+txt);
		JOptionPane.showMessageDialog(mainPanel,txt,"Warning", JOptionPane.WARNING_MESSAGE);
		//JOptionPane.showMessageDialog(null,txt,"Warning", JOptionPane.WARNING_MESSAGE);
	}



}
