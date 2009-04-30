/*
 * ApertiumView.java
 */

package apertiumview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Stroke;
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
import javax.swing.JMenuItem;
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
                warnUser("Welcome to Apertium-viewer.\nIt seems this is first time you run this program. I will therefore try to search install language pairs ('modes') from standart places. Use the File menu to install others.");
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
            
            for (int i=0; i<10; i++) {
              final String s = prefs.get("storedTexts."+i,"");
              if (s!=null && s.length()>0) {
                addStoredText(s);
              }
            }
            
        } catch (Exception e) {
           e.printStackTrace();
            warnUser("An error occured during startup:\n\n"+e);
        }
        
        
        startingUp = false;
        
        String txt = prefs.get("inputText", textWidget1.getText());        
        System.err.println("startup txt = " + txt);
        try {
          textWidget1.setText(txt);
        } catch (Exception e) {
           e.printStackTrace();
            warnUser("An error occured during startup:\n\n"+e);
        }
        
        textWidget1.commandTextField.requestFocusInWindow();
        textWidget1.textEditor.addFocusListener(scrollToVisibleFocusListener); // done here to avoid multiple adds
        textWidget1.textEditor.addKeyListener(switchFocus);

        menuBar.addKeyListener(switchFocus);
        markUnknownWordsCheckBox.addKeyListener(switchFocus);
        showCommandsCheckBox.addKeyListener(switchFocus);        
        storeTextButton.addKeyListener(switchFocus);
        copyTextButton.addKeyListener(switchFocus);
        fitToTextButton.addKeyListener(switchFocus);
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
            final TextWidget tw = (TextWidget) ((JComponent) e.getSource()).getParent().getParent().getParent();
            // Scroll so that the TextWidget's scoll pane (containing the text area) is fully visible
            Rectangle b = tw.jScrollPane1.getBounds();
            Rectangle b2 = tw.getBounds();
            //System.out.println("b = " + b);
            //System.out.println("b2 = " + b2);
            b.x += b2.x;
            b.y += b2.y;
            tw.scrollRectToVisible(b);

            
            // Make it blink for a short while
            Graphics2D g = (Graphics2D) tw.getGraphics();
            g.setColor(Color.RED);
            g.drawRect(1, 1, tw.getWidth()-2, tw.getHeight()-2);

            /*
            // Make it blink for a short while
            comp = (JComponent) (comp.getParent());
            b = comp.getBounds(b);
            comp = (JComponent) (comp.getParent());
            Graphics2D g = (Graphics2D) comp.getGraphics();
            g.setColor(Color.RED);
            g.drawRect(b.x+1, b.y+1, b.width-2, b.height-2);

            
            b = comp.getBounds(b);
            comp = (JComponent) (comp.getParent().getParent());
            g = (Graphics2D) comp.getGraphics();
            g.setColor(Color.BLUE);
            g.drawRect(b.x+1, b.y+1, b.width-2, b.height-2);
            g.setColor(Color.CYAN);
            g.drawRect(b.x, b.y-5, b.width, b.height+10);
             */

            //tw.repaint(100); // behaves as 0, but the blink is visible for me
            // this is a waste of threads,but who cares, this is a GUI program!
            new Thread() { public void run() {
                    try {
                        Thread.sleep(200);
                        tw.repaint(0);
                    } catch (InterruptedException ex) {
                    }
            }
            }.start();
        }
    };

  private void addStoredText(final String s) {
      String menT = s.length()<4000? s : s.substring(0,40)+"...";
      JMenuItem mi = new JMenuItem(menT,storedTextsMenu.getMenuComponentCount()+1);
      mi.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        textWidget1.setText(s);
      }
        
      });
      storedTextsMenu.add(mi);
  }

  public static String notNull(String text) {
    if (text==null) return "";
   return text;
  }

    KeyListener switchFocus = new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
            if (!e.isControlDown() && !e.isAltDown() && !e.isAltGraphDown()) return;
            int keyCode =   e.getKeyCode();



            int nowFocus=-1;

            if (keyCode >=  e.VK_0 && keyCode <=  e.VK_9) {
                nowFocus = Math.max(0, keyCode -  e.VK_1);  // Alt-0 and Alt-1 gives first widget
                nowFocus = Math.min(nowFocus, textWidgets.size()-1); // Alt-9 gives last widget

            } else if (keyCode ==  e.VK_PAGE_UP || keyCode ==  e.VK_PAGE_DOWN)  {

                for (int i=0; i<textWidgets.size(); i++) { // find widget which has the focus
                    TextWidget tw = textWidgets.get(i);
                    if (SwingUtilities.findFocusOwner(tw)!=null) nowFocus = i;
                }

                if (nowFocus!=-1) {
                    if (keyCode ==  e.VK_PAGE_UP) {
                        do
                            nowFocus = (nowFocus-1 + textWidgets.size()) % textWidgets.size(); // cycle upwards
                        while (textWidgets.get(nowFocus).getVisibleRect().isEmpty());
                    } else  {
                        do
                            nowFocus = (nowFocus+1) % textWidgets.size(); // cycle downwards
                        while (textWidgets.get(nowFocus).getVisibleRect().isEmpty());
                    }
                } else {
                    System.err.println("Hm! No widget has focus!" );
                    if (keyCode ==  e.VK_PAGE_UP) {
                        nowFocus = 0; // cycle upwards
                    } else  {
                        nowFocus = textWidgets.size()-1; // cycle downwards
                    }
                }
            } else return;

            menuBar.requestFocusInWindow(); // crude way to force focus notify
            textWidgets.get(nowFocus).textEditor.requestFocusInWindow();
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
            //lastTextWidget.textEditor.setFocusAccelerator('1');
            textWidget1.setCommand("");
            textWidget1.priority = 0;

            // should dispose old GUI components here...
            //for (TextWidget tw : textWidgets) tw.removeKeyListener(switchFocus);
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
                tw.textEditor.addKeyListener(switchFocus);
                textWidgets.add(tw);
                tw.priority = i+1;
                lastTextWidget.next = tw;
                lastTextWidget = tw;
                //lastTextWidget.textEditor.setFocusAccelerator((char)('0'+i+2));
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
                    //lastTextWidget.textEditor.setFocusAccelerator('9');
                }
            }

            if (textWidgetFont != null) for (TextWidget t : textWidgets) {
                t.textEditor.setFont(textWidgetFont);        
            }


        }

        TextWidget firstWithChangedCommand = null;
        TextWidget lastTextWidget = textWidget1;

        for (int i = 0; i < m.commandChain.length; i++) {
            String cmd = m.commandChain[i];            
            cmd = cmd.replaceAll("\\$1", markUnknownWordsCheckBox.isSelected()?"-g":"-n");
            cmd = cmd.replaceAll("\\$2", ""); // What is this $2 ??!??
            cmd = cmd.replaceAll("\\$3", ""); // What is this $3 ??!??
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
    storeTextButton = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    menuBar = new javax.swing.JMenuBar();
    javax.swing.JMenu fileMenu = new javax.swing.JMenu();
    loadModeMenuItem = new javax.swing.JMenuItem();
    editModesMenuItem = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JSeparator();
    javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
    toolsMenu = new javax.swing.JMenu();
    makeTestCaseMenuItem = new javax.swing.JMenuItem();
    importTestCaseMenuItem = new javax.swing.JMenuItem();
    storedTextsMenu = new javax.swing.JMenu();
    javax.swing.JMenu helpMenu = new javax.swing.JMenu();
    changeFontMenuItem = new javax.swing.JMenuItem();
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
      .addGap(0, 980, Short.MAX_VALUE)
      .addGroup(textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE))
    );
    textWidgetsPanelLayout.setVerticalGroup(
      textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 398, Short.MAX_VALUE)
      .addGroup(textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
    );

    jScrollPane1.setViewportView(textWidgetsPanel);

    copyTextButton.setAction(actionMap.get("copyText")); // NOI18N
    copyTextButton.setMnemonic('C');
    copyTextButton.setMargin(new java.awt.Insets(0, 4, 0, 4));

    showCommandsCheckBox.setMnemonic('S');
    showCommandsCheckBox.setSelected(true);
    showCommandsCheckBox.setText("Show commands");
    showCommandsCheckBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        showCommandsCheckBoxActionPerformed(evt);
      }
    });

    markUnknownWordsCheckBox.setMnemonic('U');
    markUnknownWordsCheckBox.setSelected(true);
    markUnknownWordsCheckBox.setText("Mark unknown words");
    markUnknownWordsCheckBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markUnknownWordsCheckBoxActionPerformed(evt);
      }
    });

    storeTextButton.setMnemonic('S');
    storeTextButton.setText("Store");
    storeTextButton.setMargin(new java.awt.Insets(0, 4, 0, 4));
    storeTextButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        storeTextButtonActionPerformed(evt);
      }
    });

    jLabel1.setDisplayedMnemonic('M');
    jLabel1.setLabelFor(modesComboBox);
    jLabel1.setText("Mode");

    javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
    mainPanel.setLayout(mainPanelLayout);
    mainPanelLayout.setHorizontalGroup(
      mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(mainPanelLayout.createSequentialGroup()
        .addComponent(markUnknownWordsCheckBox)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(showCommandsCheckBox)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(fitToTextButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(copyTextButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(storeTextButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 151, Short.MAX_VALUE)
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(modesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 982, Short.MAX_VALUE)
    );
    mainPanelLayout.setVerticalGroup(
      mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(mainPanelLayout.createSequentialGroup()
        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(modesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(markUnknownWordsCheckBox)
          .addComponent(showCommandsCheckBox)
          .addComponent(fitToTextButton)
          .addComponent(copyTextButton)
          .addComponent(storeTextButton)
          .addComponent(jLabel1))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
    );

    fileMenu.setMnemonic('F');
    fileMenu.setText("File");

    loadModeMenuItem.setAction(actionMap.get("loadMode")); // NOI18N
    fileMenu.add(loadModeMenuItem);

    editModesMenuItem.setText("Edit modes");
    editModesMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        editModesMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(editModesMenuItem);
    fileMenu.add(jSeparator1);

    exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
    fileMenu.add(exitMenuItem);

    menuBar.add(fileMenu);

    toolsMenu.setMnemonic('T');
    toolsMenu.setText("Tools");

    makeTestCaseMenuItem.setAction(actionMap.get("makeTestCase")); // NOI18N
    makeTestCaseMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
    toolsMenu.add(makeTestCaseMenuItem);

    importTestCaseMenuItem.setAction(actionMap.get("importTestCase")); // NOI18N
    importTestCaseMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
    toolsMenu.add(importTestCaseMenuItem);

    storedTextsMenu.setMnemonic('S');
    storedTextsMenu.setText("Stored texts");
    toolsMenu.add(storedTextsMenu);

    menuBar.add(toolsMenu);

    helpMenu.setMnemonic('V');
    helpMenu.setText("View");

    changeFontMenuItem.setAction(actionMap.get("changeFont")); // NOI18N
    changeFontMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
    helpMenu.add(changeFontMenuItem);

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

private void storeTextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_storeTextButtonActionPerformed

  try {
  addStoredText(textWidget1.getText());

  // Store last 10 in prefs
  for (int i=0; i<10; i++) {
    int n = storedTextsMenu.getMenuComponentCount() - 10 + i;
    String s = "";
    if (n>=0) {
       s = ((JMenuItem) storedTextsMenu.getMenuComponent(n)).getText();
    }
    
    prefs.put("storedTexts."+i,s);
  }
    warnUser("Your text is stored for future use. \nRetrieve it in the menu View | Stored text.\nNote: On restart only the last 10 stored texts will be remenbered.");

  } catch (Exception e) {
    warnUser(e.getLocalizedMessage());
  }
}//GEN-LAST:event_storeTextButtonActionPerformed


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
  private javax.swing.JMenuItem changeFontMenuItem;
  private javax.swing.JButton copyTextButton;
  private javax.swing.JMenuItem editModesMenuItem;
  private javax.swing.JButton fitToTextButton;
  private javax.swing.JMenuItem importTestCaseMenuItem;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JMenuItem loadModeMenuItem;
  javax.swing.JPanel mainPanel;
  private javax.swing.JMenuItem makeTestCaseMenuItem;
  private javax.swing.JCheckBox markUnknownWordsCheckBox;
  private javax.swing.JMenuBar menuBar;
  private javax.swing.JComboBox modesComboBox;
  private javax.swing.JCheckBox showCommandsCheckBox;
  private javax.swing.JButton storeTextButton;
  private javax.swing.JMenu storedTextsMenu;
  private apertiumview.TextWidget textWidget1;
  private javax.swing.JPanel textWidgetsPanel;
  private javax.swing.JMenu toolsMenu;
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

    Font textWidgetFont = null;
    
    @Action
    public void changeFont() {
        FontDialog fd = new FontDialog((Frame) SwingUtilities.getWindowAncestor(mainPanel));
        fd.setInitialFont(textWidget1.textEditor.getFont());
        fd.setVisible(true);
        textWidgetFont = fd.getFont();
        for (TextWidget t : textWidgets) {
            t.textEditor.setFont(textWidgetFont);        
        }
        System.out.println("f = " + textWidgetFont);
    }

  @Action
  public void makeTestCase() {
        String[] firstTxt = null;
        String[] lastTxt = null;
        for (TextWidget w : textWidgets) { // ugly but it works :-)
            String[] txt = w.getText().split("\n");
            if (firstTxt==null) firstTxt=txt;
            lastTxt = txt;
        }

        Mode mode = (Mode) modesComboBox.getSelectedItem();
        String sourceLanguage = mode.name.split("-")[0];
        
        String tottxt = "";
        for (int i=0; i<firstTxt.length; i++) {
          tottxt = tottxt + "* ("+sourceLanguage+") ''" + firstTxt[i] + "'' → " + lastTxt[i] + "\n";
        }          
  
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(tottxt.trim()), null);
    		JOptionPane.showMessageDialog(mainPanel,new JTextArea(tottxt.trim()),"Paste into a Wiki test page", JOptionPane.INFORMATION_MESSAGE);
  }


   @Action
    public void importTestCase() {
       String text = "Here you can import input sentences you want to test.\n"+
           "For example, go to http://wiki.apertium.org/wiki/English_and_Esperanto/Regression_tests\nand copy some tests into this window, like this:\n\n"+
           "# (en) My dog → Mia hundo\n"+
           "# (en) My big dogs → Miaj grandaj hundoj\n"+
           "# (en) Your big dogs → Viaj grandaj hundoj\n"+
           "# (en) Her cat → Ŝia kato\n"+
           "# (en) Her small cats → Ŝiaj malgrandaj katoj \n"+
           "# (en) The fastest snails -> La plej rapidaj helikoj \n\n"+
           "All souce text will be extracted.";
      JTextArea ta = new JTextArea(text);
      String res = "";
      int ret = JOptionPane.showConfirmDialog(mainPanel, ta,"Past test cases from a Wiki page", JOptionPane.OK_CANCEL_OPTION);
      if (ret == JOptionPane.OK_OPTION) {
            for (String s : ta.getText().split("\n")) {
                int x1 = s.indexOf(')');
                if (x1==-1) continue; // no ), so skip line
                int x2 = s.indexOf('→');
                if (x2==-1) x2 = s.indexOf("->");
                if (x2==-1) x2 = s.indexOf("=>");
                if (x2<x1) continue; // no → found after ), so skip line
                res = res + "\n" + s.substring(x1+1, x2).trim();
            }
            if (res.trim().length()==0) {
                JOptionPane.showMessageDialog(mainPanel, "You didn't enter any test cases with text between a ) and a →. \nTry again.");
            } else {
              textWidget1.setText(res.trim());
            }
      }
    }


}
