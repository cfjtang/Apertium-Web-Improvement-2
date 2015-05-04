/*
 * Copyright 2015 Jacob Nordfalk <jacob.nordfalk@gmail.com>, Mikel Artetxe <artetxem@gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 */
package apertiumview;

import apertiumview.source.SourcecodeFinder;
import apertiumview.source.SourceEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.PopupMenuEvent;
import org.jdesktop.application.*;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apertium.Translator;
import org.apertium.pipeline.Mode;
import org.apertium.pipeline.Program;
import org.apertium.utils.IOUtils;

/**
 * The application's main frame.
 * See  http://wiki.apertium.org/wiki/Apertium-viewer
 */
public class ApertiumView extends FrameView {

	ArrayList<TextWidget> textWidgets = new ArrayList<TextWidget>();
	ArrayList<JSplitPane> splitPanes = new ArrayList<JSplitPane>();

	private boolean popupMenuVisible;

	JSplitPane lastSplitPane;
	boolean ignoreEvents = true;
	private Mode currentMode;

	HyperlinkListener hyperlinkListener = new HyperlinkListener() {			
      @Override
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) return;
				openSourceEditor(e.getURL());
			}
    };

	private HashMap <String, SourceEditor> openSourceEditors = new HashMap<>();
	private void openSourceEditor(URL url) {
		String path = url.getPath();

		SourceEditor se0 = openSourceEditors.get(path);
		if (se0 != null) {
			se0.toFront();
			return;
		}
		try {
			SourceEditor se = new SourceEditor(this);
			se.setVisible(true);
			se.loadFile(path, url.getQuery());
			openSourceEditors.put(path, se);
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				java.awt.Desktop.getDesktop().edit(new File(path));
			} catch (Exception ex0) {
				try {
					java.awt.Desktop.getDesktop().open(new File(path));
				} catch (Exception ex1) {
					warnUser("Error opening "+path+ ":\n"+ex);
					ex.printStackTrace();
				}
			}

		}
	}
	public void closeSourceEditor(String loadedFilename) {
		openSourceEditors.remove(loadedFilename);
		System.out.println("closeSourceEditor("+loadedFilename);
	}
	public void compiledWithSourceEditor(String loadedFilename) {
		System.out.println("compiledWithSourceEditor("+loadedFilename);
		Translator.clearCache();
		textWidget1.textChg(true);
	}



	public ApertiumView(SingleFrameApplication app) {
		super(app);
		initComponents();
		textWidget1.commandTextPane.addHyperlinkListener(hyperlinkListener);

		modesComboBox.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				if (value instanceof Mode) {
					Mode m = (Mode) value;
					if (!popupMenuVisible) {
						renderer.setText(m.toString());
					} else {
						File f = new File(m.getFilename());
						renderer.setText("<html>" + m.toString() + "<small><br/>" + f.getParent() + File.separator + "<br/>" + f.getName());
					}
					renderer.setToolTipText(m.getFilename());
				} else if (value instanceof String && !local) {
					if (popupMenuVisible && !value.equals("SELECT A MODE")) {
						URLClassLoader cl = onlineModeToLoader.get(value);
						String url = cl == null ? "" : Arrays.toString(cl.getURLs());
						renderer.setText("<html>" + value + "<small><br/>" + onlineModeToCode.get(value) + ".mode "  + url);
					}
					String mode = (String) value;
					renderer.setToolTipText(mode != null || mode.equals("SELECT A MODE") ? "[Select a mode]" : onlineModeToCode.get(mode) + ".mode");
				}
				if (popupMenuVisible) {
					setHScrollFor(list);
					renderer.setBorder(new EmptyBorder(5, 0, 5, 0));
				}

				return renderer;
			}

			// Source: https://community.oracle.com/thread/1775495?tstart=0
			private void setHScrollFor(JList list) {
				JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, list);
				if (scrollPane.getHorizontalScrollBar() == null
						|| scrollPane.getHorizontalScrollBarPolicy() != JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED) {
					scrollPane.setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL));
					scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				}
			}
		});
		modesComboBox.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				popupMenuVisible = true;
			}
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				popupMenuVisible = false;
			}
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				popupMenuVisible = false;
			}
		});

		try {
			String mpref = prefs.get("modeFiles", null);
			if (mpref != null) {
				for (String fn : mpref.split("\n")) {
					if (fn.trim().isEmpty()) continue;
					loadMode(fn);
				}
				updateModesComboBox();
			} else {
				LinkedHashSet<File> fal = new LinkedHashSet<File>();
				try {
					fal.addAll(Arrays.asList(new File("/usr/share/apertium/modes/").listFiles()));
				} catch (Exception e) {}
				try {
					fal.addAll(Arrays.asList(new File("/usr/local/share/apertium/modes/").listFiles()));
				} catch (Exception e) {}
				try {
					fal.addAll(Arrays.asList(new File(".").listFiles()));
				} catch (Exception e) {}

				if (!fal.isEmpty()) {
					for (File f : fal) {
						if (f.getName().endsWith(".mode")) {
							loadMode(f.getPath());
						}
					}
					updateModesComboBox();
					warnUser("Welcome to Apertium-viewer.\nIt seems this is the first time you run this program."
							+ "\nI have searched in standard places for 'modes' (language translation pairs)"
							+ "\nI will let you revise the list now (you can always re-edit the list by choosing File | Edit modes)");
					editModesMenuItemActionPerformed(null);
					warnUser("If some modes (language pairs) were missing you can choose File | Load mode"
							+ "\nand select a mode to add it to the list (you'll have to download and compile the pair first)."
							+ "\n(for example, Esperanto-English pair is called eo-en.mode)"
							+ "\nYou can also download some pre-compiled pairs - choose 'Online' in upper right corner."
					);
				} else {
					warnUser("Welcome to Apertium-viewer.\nIt seems this is the first time you run this program."
							+ "\nI have searched in standard places for language translation pairs ('modes'), but I didn't find any."
							+ "\nI will now get download a list of 'online' modes, which are downloaded on the fly."
							+ "\nIf you DO have Apertium language pairs installed, please use File | Load mode to load them,"
							+ "\nand switch to 'Local' in the top right");
					prefs.putBoolean("local", false);
					prefs.put("modeFiles", "");
				}
			}

			if (prefs.getBoolean("local", true)) {
				rdbtnLocalActionPerformed(null);
			} else {
				rdbtnOnline.setSelected(true);
				rdbtnOnlineActionPerformed(null);
			}
			showCommandsCheckBox.setSelected(prefs.getBoolean("showCommands", true));
			showCommandsCheckBoxActionPerformed(null); // is this necesary?

			for (int i = 0; i < 10; i++) {
				final String s = prefs.get("storedTexts." + i, "");
				if (s != null && s.length() > 0) {
					addStoredText(s);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			warnUser("An error occured during startup:\n\n" + e);
			try {
				prefs.clear();
			} catch (Exception e1) {
				e1.printStackTrace();
				warnUser("An error ocurred while trying to reset the settings.\nIf the problem persists try removing your .java/.userPrefs/apertiumview/prefs.xml file manually.");
			}
			System.exit(1);
		}

		Translator.setCacheEnabled(true);

		ignoreEvents = false;


		try {
			showSelectedMode();
		} catch (Exception e) {
			e.printStackTrace();
			warnUser("An error occured during startup:\n\n" + e);
		}

		textWidget1.commandTextPane.requestFocusInWindow();
		textWidget1.textEditor.addFocusListener(scrollToVisibleFocusListener); // done here to avoid multiple adds
		textWidget1.textEditor.addKeyListener(switchFocus);

		menuBar.addKeyListener(switchFocus);
		markUnknownWordsCheckBox.addKeyListener(switchFocus);
		showCommandsCheckBox.addKeyListener(switchFocus);
		storeTextButton.addKeyListener(switchFocus);
		copyTextButton.addKeyListener(switchFocus);
		fitToTextButton.addKeyListener(switchFocus);

		//app.getMainFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void shutdown() {
		prefs.putBoolean("showCommands", showCommandsCheckBox.isSelected());
		prefs.putBoolean("local", local);
		String divLoc = "";
		for (JSplitPane p : splitPanes) divLoc += "," + p.getDividerLocation();
		prefs.put("dividerLocation", divLoc);
		prefs.put("inputText", textWidget1.getText());

		// Store current text under current language
		String currentLanguage = currentMode==null?null:currentMode.toString().split("\\s")[0];
		prefs.put("inputText "+currentLanguage, textWidget1.getText());

		Pipeline.getPipeline().shutdown();
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
			g.drawRect(1, 1, tw.getWidth() - 2, tw.getHeight() - 2);

			new Thread() {
				public void run() {
					try {
						Thread.sleep(300);
						tw.repaint();
					} catch (InterruptedException ex) {
					}
				}
			}.start();
		}
	};

	private void addStoredText(final String s) {
		String menT = s.length() < 4000 ? s : s.substring(0, 40) + "...";
		JMenuItem mi = new JMenuItem(menT, storedTextsMenu.getMenuComponentCount() + 1);
		mi.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				textWidget1.setText(s);
			}

		});
		storedTextsMenu.add(mi);
	}

	public static String notNull(String text) {
		if (text == null) return "";
		return text;
	}

	KeyListener switchFocus = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if (!e.isControlDown() && !e.isAltDown() && !e.isAltGraphDown()) return;
			int keyCode = e.getKeyCode();

			int nowFocus = -1;

			if (keyCode >= e.VK_0 && keyCode <= e.VK_9) {
				nowFocus = Math.max(0, keyCode - e.VK_1);  // Alt-0 and Alt-1 gives first widget
				nowFocus = Math.min(nowFocus, textWidgets.size() - 1); // Alt-9 gives last widget

			} else if (keyCode == e.VK_PAGE_UP || keyCode == e.VK_PAGE_DOWN) {

				for (int i = 0; i < textWidgets.size(); i++) { // find widget which has the focus
					TextWidget tw = textWidgets.get(i);
					if (SwingUtilities.findFocusOwner(tw) != null) nowFocus = i;
				}

				if (nowFocus != -1) {
					if (keyCode == e.VK_PAGE_UP) {
						do
							nowFocus = (nowFocus - 1 + textWidgets.size()) % textWidgets.size(); // cycle upwards
						while (textWidgets.get(nowFocus).getVisibleRect().isEmpty());
					} else {
						do
							nowFocus = (nowFocus + 1) % textWidgets.size(); // cycle downwards
						while (textWidgets.get(nowFocus).getVisibleRect().isEmpty());
					}
				} else {
					System.err.println("Hm! No widget has focus!");
					if (keyCode == e.VK_PAGE_UP) {
						nowFocus = 0; // cycle upwards
					} else {
						nowFocus = textWidgets.size() - 1; // cycle downwards
					}
				}
			} else return;

			menuBar.requestFocusInWindow(); // crude way to force focus notify
			textWidgets.get(nowFocus).textEditor.requestFocusInWindow();
		}
	};

	private void ajustSplitPaneHeights(int toth) {
		for (JSplitPane s : splitPanes) {
			if (s.getDividerLocation() > 2) {
				//System.out.println("getDividerLocation()"+s.getDividerLocation());
				Dimension d = s.getTopComponent().getMinimumSize();
				s.setDividerLocation(d.height);
				//System.out.println("dp="+d);
				toth += s.getDividerSize() + 2;
				//System.out.println("getDividerLocation()"+s.getDividerLocation());
			}
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
			w.setMinimumSize(new Dimension(5, 5));
			w.jScrollPane1.setPreferredSize(null);
			w.textEditor.setPreferredSize(null);
			w.setPreferredSize(null);
		}
	}

	private void showSelectedMode() {
		final Object item = modesComboBox.getSelectedItem();

		if (item instanceof Mode) { // local
			IOUtils.setClassLoader(null);
			showMode((Mode) item);
		} else { // online
			if (item==null || item.equals("SELECT A MODE")) return;

			final JDialog dialog = createDialog("Please wait while downloading "+item);

			new Thread() {
				@Override
				public void run() {
					try {
						SwingUtilities.invokeAndWait(new Runnable() { public void run() {}}); // Let the UI thread settle
						IOUtils.setClassLoader(onlineModeToLoader.get((String) item));
						final Mode m = new Mode("data/modes/" + onlineModeToCode.get((String) item) + ".mode");
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								dialog.setVisible(false);
								showMode(m);
							}
						});
					} catch (final Exception e) {
						e.printStackTrace();
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								dialog.setVisible(false);
								warnUser("Unexpected error while trying to load online package:\n" + e);
							}
						});
					}
				}
			}.start();
		}
	}

	private void showMode(Mode m) {
		if (m.getPipelineLength() + 1 != textWidgets.size()) {
			// Number of text widgets are different, dispose all and regenerate
			lastSplitPane = jSplitPane1;
			jSplitPane1.setBottomComponent(null);

			TextWidget lastTextWidget = textWidget1;
			//lastTextWidget.textEditor.setFocusAccelerator('1');
			//textWidget1.setCommand("");
			textWidget1.priority = 0;

			// should dispose old GUI components here...
			//for (TextWidget tw : textWidgets) tw.removeKeyListener(switchFocus);
			textWidgets.clear();
			splitPanes.clear();

			textWidgets.add(textWidget1);
			splitPanes.add(jSplitPane1);

			String[] divLocs = prefs.get("dividerLocation", "").split(",");
			if (divLocs.length > 1) try {
				jSplitPane1.setDividerLocation(Integer.parseInt(divLocs[1]));
			} catch (Exception e) { e.printStackTrace(); }
			else jSplitPane1.setDividerLocation(60);

			for (int i = 0; i < m.getPipelineLength(); i++) {
				TextWidget tw = new TextWidget();
				tw.textEditor.addKeyListener(switchFocus);
				tw.commandTextPane.addHyperlinkListener(hyperlinkListener);
				textWidgets.add(tw);
				tw.priority = i + 1;
				lastTextWidget.next = tw;
				lastTextWidget = tw;
				//lastTextWidget.textEditor.setFocusAccelerator((char)('0'+i+2));
				lastTextWidget.textEditor.addFocusListener(scrollToVisibleFocusListener);

				if (i < m.getPipelineLength() - 1) {
					JSplitPane sp = new JSplitPane();
					sp.setBorder(BorderFactory.createEmptyBorder());
					sp.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
					sp.setOneTouchExpandable(true);
					sp.setContinuousLayout(true);
					sp.setTopComponent(tw);
					lastSplitPane.setBottomComponent(sp);
					splitPanes.add(sp);
					lastSplitPane = sp;
					if (divLocs.length > 2 + i) try {
						sp.setDividerLocation(Integer.parseInt(divLocs[2 + i]));
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

		for (int i = 0; i < m.getPipelineLength(); i++) {
			Program p = m.getProgramByIndex(i);
			TextWidget tw = textWidgets.get(i + 1);
			tw.setProgram(p);

			StringBuilder linkspopup = new StringBuilder();
			String htmlText = SourcecodeFinder.createHtmlLinkText(p, linkspopup);
			tw.commandTextPane.setText(htmlText);
				//else commandTextPane.setText("<html><div style='white-space:nowrap;font-size:12pt'>" + program.toString());
			tw.commandTextPane.setCaretPosition(0); // scroll to start of text
			tw.commandTextPane.setToolTipText(linkspopup.toString().trim());
		}

		Pipeline.getPipeline().externalProcessing = local && Boolean.parseBoolean(prefs.get("externalProcessing", "false"));
		if (Pipeline.getPipeline().externalProcessing) {
			// Set the working directory of the mode. This is necesary in case the mode contains relative paths
			// to (development) files
			String workingDir = prefs.get("workingDir", "").trim();
			if (!workingDir.isEmpty()) {
				Pipeline.getPipeline().execPath = new File(workingDir);
			} else {
				File parentFile = new File(m.getFilename()).getParentFile();
				if (parentFile.getName().equals("modes"))
					Pipeline.getPipeline().execPath = parentFile.getParentFile();
				else
					Pipeline.getPipeline().execPath = parentFile;
			}
			System.err.println("Pipeline.getPipeline().execPath = " + Pipeline.getPipeline().execPath);
			//new Exception().printStackTrace();

			String envVars = prefs.get("envVars", "").trim();
			Pipeline.getPipeline().envp = envVars.isEmpty() ? null : envVars.split("\n");
			System.err.println("Pipeline.getPipeline().envp = " + envVars);

			Pipeline.getPipeline().ignoreErrorMessages = Boolean.parseBoolean(prefs.get("ignoreErrorMessages", "false"));
		}

		Pipeline.getPipeline().markUnknownWords = markUnknownWordsCheckBox.isSelected();

		// Set title to mode - easens window tabbing
		ApertiumViewMain.getApplication().getMainFrame().setTitle("Apertium-viewer (" + m + ")");

		// Update input text to match the current input language
		try {
			String language = m.toString().split("\\s")[0];
			String currentLanguage = currentMode==null?null:currentMode.toString().split("\\s")[0];
			if (!language.equals(currentLanguage)) {
				prefs.put("inputText "+currentLanguage, textWidget1.getText());
				String txt = prefs.get("inputText "+language, null);
				if (txt!=null) textWidget1.setText(txt);
			}
		} catch (Exception e) { e.printStackTrace(); }

		currentMode = m;

		if (ignoreEvents) return;

		textWidget1.textChg(true);

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
 always regenerated by the Form SourcecodeFinder.
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
        jButton1 = new javax.swing.JButton();
        rdbtnLocal = new javax.swing.JRadioButton();
        rdbtnOnline = new javax.swing.JRadioButton();
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
        optionsMenuItem = new javax.swing.JMenuItem();
        helpMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();

        mainPanel.setAutoscrolls(true);

        modesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modesComboBoxActionPerformed(evt);
            }
        });

        fitToTextButton.setMnemonic('I');
        fitToTextButton.setText("Fit");
        fitToTextButton.setToolTipText("Fits the text areas to the size of the contained text");
        fitToTextButton.setMargin(new java.awt.Insets(0, 4, 0, 4));
        fitToTextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fitToText(evt);
            }
        });

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
            .addGap(0, 1106, Short.MAX_VALUE)
            .addGroup(textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1106, Short.MAX_VALUE))
        );
        textWidgetsPanelLayout.setVerticalGroup(
            textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 414, Short.MAX_VALUE)
            .addGroup(textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(textWidgetsPanel);

        copyTextButton.setMnemonic('C');
        copyTextButton.setText("Copy");
        copyTextButton.setToolTipText("<html>Copy text from all stages to the clipboard.<br>Hidden stages and stages with no change are ignored.<br>Usefull for pasting into chat/email");
        copyTextButton.setMargin(new java.awt.Insets(0, 4, 0, 4));
        copyTextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyText(evt);
            }
        });

        showCommandsCheckBox.setMnemonic('H');
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
        storeTextButton.setToolTipText("Stores input text for later use");
        storeTextButton.setMargin(new java.awt.Insets(0, 4, 0, 4));
        storeTextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                storeTextButtonActionPerformed(evt);
            }
        });

        jLabel1.setDisplayedMnemonic('M');
        jLabel1.setLabelFor(modesComboBox);
        jLabel1.setText("Mode:");
        jLabel1.setToolTipText("Change mode (language pair)");

        jButton1.setMnemonic('D');
        jButton1.setText("Hide intermediate");
        jButton1.setToolTipText("Hides all but input and output");
        jButton1.setMargin(new java.awt.Insets(0, 4, 0, 4));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideIntermediate(evt);
            }
        });

        buttonGroup1.add(rdbtnLocal);
        rdbtnLocal.setSelected(true);
        rdbtnLocal.setText("Local");
        rdbtnLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbtnLocalActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdbtnOnline);
        rdbtnOnline.setText("Online");
        rdbtnOnline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbtnOnlineActionPerformed(evt);
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
                .addComponent(fitToTextButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copyTextButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(storeTextButton)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modesComboBox, 0, 229, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbtnLocal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbtnOnline)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1108, Short.MAX_VALUE)
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
                    .addComponent(jButton1)
                    .addComponent(rdbtnLocal)
                    .addComponent(rdbtnOnline)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
        );

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(apertiumview.ApertiumViewMain.class).getContext().getActionMap(ApertiumView.class, this);
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
        toolsMenu.add(makeTestCaseMenuItem);

        importTestCaseMenuItem.setText("Import Test Case...");
        importTestCaseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importTestCase(evt);
            }
        });
        toolsMenu.add(importTestCaseMenuItem);

        storedTextsMenu.setMnemonic('S');
        storedTextsMenu.setText("Stored texts");
        toolsMenu.add(storedTextsMenu);

        menuBar.add(toolsMenu);

        helpMenu.setMnemonic('V');
        helpMenu.setText("View");

        changeFontMenuItem.setAction(actionMap.get("changeFont")); // NOI18N
        helpMenu.add(changeFontMenuItem);

        optionsMenuItem.setMnemonic('O');
        optionsMenuItem.setText("Options");
        optionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editOptions(evt);
            }
        });
        helpMenu.add(optionsMenuItem);

        helpMenuItem.setText("Help...");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpMenuItem);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

private void modesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modesComboBoxActionPerformed
	if (ignoreEvents) return;
	if (local) {
		prefs.putInt("modesComboBoxLocal", modesComboBox.getSelectedIndex());
		Mode mode = (Mode) modesComboBox.getSelectedItem();
		String tooltip = "[No mode selected]";
		if (mode != null) {
			tooltip = mode.getFilename();
		}
		modesComboBox.setToolTipText(tooltip);
	} else {
		prefs.putInt("modesComboBoxOnline", modesComboBox.getSelectedIndex());
		String mode = (String) modesComboBox.getSelectedItem();
		String tooltip = mode != null && mode.equals("SELECT A MODE") ? "[No mode selected]" : onlineModeToCode.get(mode) + ".mode";
		modesComboBox.setToolTipText(tooltip);
	}
	Translator.clearCache();
	showSelectedMode();
}//GEN-LAST:event_modesComboBoxActionPerformed

private void editModesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editModesMenuItemActionPerformed
	String mpref = "";
	for (Mode mo : modes) mpref = mpref + mo.getFilename() + "\n";
	JTextArea ta = new JTextArea(mpref);
	// Fix for Jimmy: On my system, in the 'edit list of modes' part, the list of modes is
	// longer than can be displayed on screen. I know I'm not the typical
	// user, but do you think you could fit a scroll bar in there?
	JScrollPane sp = new JScrollPane(ta);
	Dimension ps = sp.getPreferredSize();
	Dimension sceen = Toolkit.getDefaultToolkit().getScreenSize();
	if (ps.height > sceen.height - 150) {
		ps.height = sceen.height - 150;
		ps.width += 50; // some space
		sp.setPreferredSize(ps);
	}
	int ret = JOptionPane.showConfirmDialog(mainPanel, sp,
			"Edit the list of modes", JOptionPane.OK_CANCEL_OPTION);
	if (ret == JOptionPane.OK_OPTION) {
		modes.clear();
		mpref = ta.getText();
		for (String fn : mpref.split("\n")) {
			loadMode(fn);
		}
		updateModesComboBox();
		prefs.put("modeFiles", mpref);
	}
}//GEN-LAST:event_editModesMenuItemActionPerformed

private void markUnknownWordsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markUnknownWordsCheckBoxActionPerformed
	showSelectedMode();
}//GEN-LAST:event_markUnknownWordsCheckBoxActionPerformed

private void showCommandsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showCommandsCheckBoxActionPerformed

	boolean show = showCommandsCheckBox.isSelected();
	//System.out.println("show = " + show);
	for (TextWidget w : textWidgets) {
		w.commandTextPane.setVisible(show);
		w.zoomButton.setVisible(show);
		w.freezeCheckBox.setVisible(show);
		w.setSize(w.getSize());
	}
	textWidget1.commandScrollPane.setVisible(false);
	textWidget1.zoomButton.setVisible(false);
	textWidget1.freezeCheckBox.setVisible(false);
	mainPanel.validate();
}//GEN-LAST:event_showCommandsCheckBoxActionPerformed

private void storeTextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_storeTextButtonActionPerformed

	try {
		addStoredText(textWidget1.getText());

		// Store last 10 in prefs
		for (int i = 0; i < 10; i++) {
			int n = storedTextsMenu.getMenuComponentCount() - 10 + i;
			String s = "";
			if (n >= 0) {
				s = ((JMenuItem) storedTextsMenu.getMenuComponent(n)).getText();
			}

			prefs.put("storedTexts." + i, s);
		}
		warnUser("Your text is stored for future use. \nRetrieve it in the menu View | Stored text.\nNote: On restart only the last 10 stored texts will be remenbered.");

	} catch (Exception e) {
		warnUser(e.getLocalizedMessage());
	}
}//GEN-LAST:event_storeTextButtonActionPerformed

private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
	try {
		// TODO add your handling code here:
		java.awt.Desktop.getDesktop().browse(new URI("http://wiki.apertium.org/wiki/Apertium-viewer#Features"));
	} catch (Exception ex) {
		Logger.getLogger(ApertiumView.class.getName()).log(Level.SEVERE, null, ex);
		warnUser(ex.toString());
	}


}//GEN-LAST:event_helpMenuItemActionPerformed

private void editOptions(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editOptions
	OptionsPanel op = new OptionsPanel();
	boolean externalProcessing = Boolean.parseBoolean(prefs.get("externalProcessing", "false"));
	op.externalProcessing.setSelected(externalProcessing);
	op.setExternalProcessingOptionsEnabled(externalProcessing);
	op.workingDirTextField.setText(prefs.get("workingDir", ""));
	op.envVarsTextArea.setText(prefs.get("envVars", ""));
	op.ignoreErrorMessages.setSelected(Boolean.parseBoolean(prefs.get("ignoreErrorMessages", "false")));
	int ret = JOptionPane.showConfirmDialog(mainPanel, op, "Edit Options", JOptionPane.OK_CANCEL_OPTION);
	if (ret == JOptionPane.OK_OPTION) {
		prefs.put("externalProcessing", Boolean.toString(op.externalProcessing.isSelected()));
		prefs.put("workingDir", op.workingDirTextField.getText());
		prefs.put("envVars", op.envVarsTextArea.getText());
		prefs.put("ignoreErrorMessages", Boolean.toString(op.ignoreErrorMessages.isSelected()));
		modesComboBoxActionPerformed(null); // reload the current mode
	}
}//GEN-LAST:event_editOptions

private void importTestCase(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importTestCase
	// TODO add your handling code here:
	String text = "Here you can import input sentences you want to test.\n"
			+ "For example, go to http://wiki.apertium.org/wiki/English_and_Esperanto/Regression_tests\nand copy some tests into this window, like this:\n\n"
			+ "# (en) My dog → Mia hundo\n"
			+ "# (en) My big dogs → Miaj grandaj hundoj\n"
			+ "# (en) Your big dogs → Viaj grandaj hundoj\n"
			+ "# (en) Her cat → Ŝia kato\n"
			+ "# (en) Her small cats → Ŝiaj malgrandaj katoj \n"
			+ "# (en) The fastest snails -> La plej rapidaj helikoj \n\n"
			+ "All souce text will be extracted.";
	JTextArea ta = new JTextArea(text);
	String res = "";
	int ret = JOptionPane.showConfirmDialog(mainPanel, ta, "Past test cases from a Wiki page", JOptionPane.OK_CANCEL_OPTION);
	if (ret == JOptionPane.OK_OPTION) {
		for (String s : ta.getText().split("\n")) {
			int x1 = s.indexOf(')');
			if (x1 == -1) continue; // no ), so skip line
			int x2 = s.indexOf('→');
			if (x2 == -1) x2 = s.indexOf("->");
			if (x2 == -1) x2 = s.indexOf("=>");
			if (x2 < x1) continue; // no → found after ), so skip line
			res = res + "\n" + s.substring(x1 + 1, x2).trim();
		}
		if (res.trim().length() == 0) {
			JOptionPane.showMessageDialog(mainPanel, "You didn't enter any test cases with text between a ) and a →. \nTry again.");
		} else {
			textWidget1.setText(res.trim());
		}
	}
}//GEN-LAST:event_importTestCase

private void hideIntermediate(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideIntermediate
	int toth = 0;
	for (TextWidget w : textWidgets) {
		Dimension d = w.textEditor.getPreferredSize();
		d.height += insetHeight(w.textEditor) + insetHeight(w) + insetHeight(w.jScrollPane1) + 6;

		if (w == textWidget1 || w == textWidgets.get(textWidgets.size() - 1)) {
			d.height += w.commandScrollPane.getPreferredSize().height;
			d.height += insetHeight(w.commandScrollPane);
		} else {
			d.height = 0;
		}

		w.setMinimumSize(d);
		w.setPreferredSize(d);
		toth += d.height;
	}

	ajustSplitPaneHeights(toth);
}//GEN-LAST:event_hideIntermediate

private void copyText(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyText
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
	JOptionPane.showMessageDialog(mainPanel, new JTextArea(tottxt), "Contents of clipboard", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_copyText

	private int insetHeight(JComponent c) {
		Insets i = c.getInsets();
		return i.top + i.bottom;
	}

private void fitToText(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fitToText
	//System.out.println();
	//System.out.println("============fitToText()");
	int toth = 0;
	for (TextWidget w : textWidgets) {

		Dimension d = w.textEditor.getPreferredSize();
		d.height += insetHeight(w.textEditor) + insetHeight(w) + insetHeight(w.jScrollPane1) + 6;

		if (w.commandScrollPane.isVisible()) {
			d.height += w.commandScrollPane.getPreferredSize().height;
			d.height += insetHeight(w.commandScrollPane);
		}

		//System.out.println("d="+d.height);
		if (w.getStatus() == w.STATUS_EQUAL) d.height = 0;

		w.setMinimumSize(d);
		w.setPreferredSize(d);
		toth += d.height;
	}

	ajustSplitPaneHeights(toth);
}//GEN-LAST:event_fitToText

	private void updateModesComboBox() {
		ignoreEvents = true;
		modesComboBox.removeAllItems();
		int index;
		if (local) {
			for (Mode m : modes) modesComboBox.addItem(m);
			index = prefs.getInt("modesComboBoxLocal", 0);
		} else {
			modesComboBox.addItem("SELECT A MODE");
			for (String s : onlineModes) modesComboBox.addItem(s);
			index = prefs.getInt("modesComboBoxOnline", 0);
		}
		modesComboBox.setSelectedIndex(index < modesComboBox.getModel().getSize() ? index : -1);
		// Ugly hack - see http://stackoverflow.com/questions/29841044/jcombobox-popup-cell-height-wrong-after-call-to-setselecteditem
		ComboBoxModel model = modesComboBox.getModel();
		modesComboBox.setModel(new DefaultComboBoxModel());
		modesComboBox.setModel(model);

		ignoreEvents = false;
		modesComboBoxActionPerformed(null);
	}

	private boolean local = true;

    private void rdbtnLocalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbtnLocalActionPerformed
			local = true;
			updateModesComboBox();
    }//GEN-LAST:event_rdbtnLocalActionPerformed

    private void rdbtnOnlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbtnOnlineActionPerformed
			local = false;
			if (onlineModeToLoader != null) {
				updateModesComboBox();
			} else if (prefs.get("onlineModes","").length()>0) {
				initOnlineModes();
				updateModesComboBox();
				// Download a fresh version of the online modes for next time the program is started
				new Thread() {
					public void run() {
						try {
							downloadOnlineModes();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
			} else {
				final JDialog dialog = createDialog("Please wait while downloading the list of pairs");

				new Thread() {
					@Override
					public void run() {
						try {
							downloadOnlineModes();
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									jSplitPane1.setBottomComponent(new JLabel("Please select a mode"));
									initOnlineModes();
									updateModesComboBox();
									modesComboBox.showPopup();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							warnUser("Error loading online modes: " + e);
						} finally {
							dialog.setVisible(false);
						}
					}
				}.start();
			}
    }//GEN-LAST:event_rdbtnOnlineActionPerformed

	private JDialog createDialog(String message) {
		final JDialog dialog = new JDialog(getFrame(), "Downloading, please wait...", false);
		dialog.setContentPane(new JOptionPane(message,
				JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null));
		dialog.pack();
		dialog.setLocationRelativeTo(getFrame());
		dialog.setVisible(true);
		return dialog;
	}

	void fitToText() {
		fitToText(null);
	}

	JFileChooser modeFileChooser;

	@Action
	public void loadMode() {
		if (modeFileChooser == null) {
			modeFileChooser = new JFileChooser(prefs.get("lastModePath", "."));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Apertium mode files", "mode");
			modeFileChooser.setFileFilter(filter);
			modeFileChooser.setMultiSelectionEnabled(true);
		}
		int returnVal = modeFileChooser.showOpenDialog(mainPanel);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: "
					+ modeFileChooser.getSelectedFile().getName());
			prefs.put("lastModePath", modeFileChooser.getSelectedFile().getParent());
			File fs[] = modeFileChooser.getSelectedFiles();
			//System.out.println("txt=" + fs.length);
			for (File f : fs) {
				loadMode(f.getPath());
			}
			updateModesComboBox();
			String mpref = "";
			for (Mode mo : modes) mpref = mpref + mo.getFilename() + "\n";
			prefs.put("modeFiles", mpref);
		}
	}

	public final static Preferences prefs = Preferences.userNodeForPackage(ApertiumView.class);


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.ButtonGroup buttonGroup1;
    javax.swing.JMenuItem changeFontMenuItem;
    javax.swing.JButton copyTextButton;
    javax.swing.JMenuItem editModesMenuItem;
    javax.swing.JButton fitToTextButton;
    javax.swing.JMenuItem helpMenuItem;
    javax.swing.JMenuItem importTestCaseMenuItem;
    javax.swing.JButton jButton1;
    javax.swing.JLabel jLabel1;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JSeparator jSeparator1;
    javax.swing.JSplitPane jSplitPane1;
    javax.swing.JMenuItem loadModeMenuItem;
    javax.swing.JPanel mainPanel;
    javax.swing.JMenuItem makeTestCaseMenuItem;
    javax.swing.JCheckBox markUnknownWordsCheckBox;
    javax.swing.JMenuBar menuBar;
    javax.swing.JComboBox modesComboBox;
    javax.swing.JMenuItem optionsMenuItem;
    javax.swing.JRadioButton rdbtnLocal;
    javax.swing.JRadioButton rdbtnOnline;
    javax.swing.JCheckBox showCommandsCheckBox;
    javax.swing.JButton storeTextButton;
    javax.swing.JMenu storedTextsMenu;
    apertiumview.TextWidget textWidget1;
    javax.swing.JPanel textWidgetsPanel;
    javax.swing.JMenu toolsMenu;
    // End of variables declaration//GEN-END:variables

	private JDialog aboutBox;

	ArrayList<Mode> modes = new ArrayList<Mode>();
	ArrayList<String> onlineModes;
	HashMap<String, URLClassLoader> onlineModeToLoader;
	HashMap<String, String> onlineModeToCode;


	private void downloadOnlineModes() throws IOException {
		final String REPO_URL = "https://apertium.svn.sourceforge.net/svnroot/apertium/builds/language-pairs";
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = new InputStreamReader(new URL(REPO_URL).openStream());
		int ch = isr.read();
		while (ch != -1) { sb.append((char)ch); ch = isr.read(); }
		isr.close();
		prefs.put("onlineModes", sb.toString());
	}

	private void initOnlineModes() {
		onlineModes = new ArrayList<String>();
		onlineModeToLoader = new HashMap<String, URLClassLoader>();
		onlineModeToCode = new HashMap<String, String>();
		for (String line : prefs.get("onlineModes","").split("\n")) try {
			String[] columns = line.split("\t");
			if (columns.length > 3) {
				URLClassLoader cl = new URLClassLoader(new URL[] { new URL(columns[1]) }, this.getClass().getClassLoader());
				String pairs[] = columns[3].split(",");
				for (int i = 0; i < pairs.length; i++) {
					final String pair = pairs[i].trim();
					if (!pair.contains("-")) continue;
					String title = Translator.getTitle(pair);
					onlineModes.add(title);
					onlineModeToCode.put(title, pair);
					onlineModeToLoader.put(title, cl);
				}
			}
		} catch (Exception e) { e.printStackTrace(); }
		// Collections.sort(onlineModes); // don't sort; better to keep original order intact as related modes are near to each other
	}

	public static String legu(File fil) throws IOException {
		FileChannel fc = new FileInputStream(fil).getChannel();
		MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fil.length());
		//CharBuffer cb = Charset.forName("ISO-8859-1").decode(bb);
		CharBuffer cb = Charset.forName("UTF-8").decode(bb);
		return new String(cb.array());
	}

	private void loadMode(String filename) {
		try {
			Mode m = new Mode(filename);
			modes.add(m);
			modesComboBox.addItem(m);
		} catch (IOException ex) {
			Logger.getLogger(ApertiumView.class.getName()).log(Level.INFO, null, ex);
			warnUser("Loading of mode " + filename + " failed:\n\n" + ex.toString() + "\n\nContinuing without this mode.");
		}
	}

	public void warnUser(String txt) {
		System.out.println("warnUser(" + txt);
		JOptionPane.showMessageDialog(mainPanel, txt, "Warning", JOptionPane.WARNING_MESSAGE);
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
			if (firstTxt == null) firstTxt = txt;
			lastTxt = txt;
		}

		Mode mode = (Mode) modesComboBox.getSelectedItem();
		String sourceLanguage = mode.toString().split("-")[0];

		String tottxt = "";
		for (int i = 0; i < firstTxt.length; i++) {
			tottxt = tottxt + "* (" + sourceLanguage + ") ''" + firstTxt[i] + "'' → " + lastTxt[i] + "\n";
		}

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(tottxt.trim()), null);
		JOptionPane.showMessageDialog(mainPanel, new JTextArea(tottxt.trim()), "Paste into a Wiki test page", JOptionPane.INFORMATION_MESSAGE);
	}

}
