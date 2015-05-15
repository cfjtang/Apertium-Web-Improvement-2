/*
 * Copyright 2015 Jacob Nordfalk <jacob.nordfalk@gmail.com>, Mikel Artetxe <artetxem@gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 */
package apertiumview;

import apertiumview.sourceeditor.SourceEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
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
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import static java.lang.Integer.parseInt;
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
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.apertium.Translator;
import org.apertium.pipeline.Mode;
import org.apertium.pipeline.Program;
import org.apertium.utils.IOUtils;

/**
 * The application's main frame.
 * See  http://wiki.apertium.org/wiki/Apertium-viewer
 */
public class ApertiumView extends javax.swing.JFrame {
	private final Container mainPanel;

	ArrayList<TextWidget> textWidgets = new ArrayList<>();
	ArrayList<JSplitPane> splitPanes = new ArrayList<>();

	private boolean popupMenuVisible;

	JSplitPane lastSplitPane;
	boolean ignoreEvents = true;
	private Mode currentMode;

	private boolean local = true;
	JFileChooser modeFileChooser;
	public final static Preferences prefs = Preferences.userNodeForPackage(ApertiumView.class);

	private JDialog aboutBox;

	ArrayList<Mode> modes = new ArrayList<Mode>();
	ArrayList<String> onlineModes;
	HashMap<String, URLClassLoader> onlineModeToLoader;
	HashMap<String, String> onlineModeToCode;

	Font textWidgetFont = null;


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


	private HashMap <String, SourceEditor> openSourceEditors = new HashMap<>();
	private final ApertiumViewMain app;
	public void openSourceEditor(URL url) {
		String path = url.getPath();

		SourceEditor se0 = openSourceEditors.get(path);
		System.out.println("openSourceEditor("+path+" ->"+se0);
		if (se0 != null) {
//			se0.setVisible(false);
//			se0.setVisible(true);
			se0.setState(java.awt.Frame.NORMAL);
			se0.toFront();
//			se0.repaint();
			return;
		}
		try {
			SourceEditor se = new SourceEditor(this, path, url.getQuery());
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

	public void closeSourceEditor(String path) {
		SourceEditor se = openSourceEditors.remove(path);
		System.out.println("closeSourceEditor("+path+" "+se);
	}
	public void compiledSourceEditor(String path) {
		System.out.println("compiledWithSourceEditor("+path);
		Translator.clearCache();
		textWidget1.textChg(true);
	}



	ApertiumView(ApertiumViewMain app) {
		mainPanel = getContentPane();
		this.app = app;
		initComponents();
		textWidget1.setup(this, 0, textWidget1);
		try { ((BasicSplitPaneUI) jSplitPane1.getUI()).getDivider().addMouseListener(turnAutofitOff); } catch (Exception e) { e.printStackTrace(); }

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
					warnUser("Please use File | Load mode to add modes/language pairs that were missing "
							+ "\n(you'll have to download and compile the pair first). For example, Esperanto-English pair is called eo-en.mode."
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
			markUnknownWordsMenuItem.setSelected(prefs.getBoolean("markUnknownWords", true));
			showCommandsMenuItem.setSelected(prefs.getBoolean("showCommands", true));
			showCommandsMenuItemActionPerformed(null); // this is necesary, i.a. to hide 1st panels commands
			transferRuleTracingMenuItem.setSelected(prefs.getBoolean("transferRuleTracing", true));

			for (int i = 0; i < 10; i++) {
				final String s = prefs.get("storedTexts." + i, "");
				if (s != null && s.length() > 0) {
					addStoredText(s);
				}
			}
			fitToTextButton.setSelected(prefs.getBoolean("fitToText", true));
			hideIntermediateButton.setSelected(prefs.getBoolean("hideIntermediate", false));

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
			updateSelectedMode();
		} catch (Exception e) {
			e.printStackTrace();
			warnUser("An error occured during startup:\n\n" + e);
		}

		textWidget1.commandTextPane.requestFocusInWindow();

		menuBar.addKeyListener(switchFocus);
		copyTextButton.addKeyListener(switchFocus);
		fitToTextButton.addKeyListener(switchFocus);

		//app.getMainFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void shutdown() {
		for (SourceEditor se : new ArrayList<SourceEditor>(openSourceEditors.values())) {
			if (!se.okToClose()) return;
			se.close();
		}

		prefs.putBoolean("fitToText", fitToTextButton.isSelected());
		prefs.putBoolean("hideIntermediate", hideIntermediateButton.isSelected());
		prefs.putBoolean("showCommands", showCommandsMenuItem.isSelected());
		prefs.putBoolean("markUnknownWords", markUnknownWordsMenuItem.isSelected());
		prefs.putBoolean("transferRuleTracing", transferRuleTracingMenuItem.isSelected());
		prefs.putBoolean("local", local);
		String divLoc = "";
		for (JSplitPane p : splitPanes) divLoc += "," + p.getDividerLocation();
		prefs.put("dividerLocation", divLoc);
		prefs.put("inputText", textWidget1.getText());

		// Store current text under current language
		String currentLanguage = currentMode==null?null:getSourceLanguageCode(currentMode);
		prefs.put("inputText "+currentLanguage, textWidget1.getText());

		Pipeline.getPipeline().shutdown();
		app.shutdown();
	}


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

	private void updateSelectedMode() {
		final Object item = modesComboBox.getSelectedItem();

		if (item instanceof Mode) { // local
			IOUtils.setClassLoader(null);
			setMode((Mode) item);
		} else { // online
			if (item==null || item.equals("SELECT A MODE")) return;

			final JDialog dialog = showDialog("Please wait while downloading "+item+"..."
					+ "\n(this need to be done again each time you start the program and will take some time\nconsider installing the language pair locally)");

			new Thread() {
				@Override
				public void run() {
					try {
						SwingUtilities.invokeAndWait(new Runnable() { public void run() {}}); // Let the UI thread settle
						URLClassLoader classLoader = onlineModeToLoader.get((String) item);
						IOUtils.setClassLoader(classLoader);
						String filename = "data/modes/" + onlineModeToCode.get((String) item) + ".mode";
						System.out.println("Loading "+filename+" from "+Arrays.toString(classLoader.getURLs()));
						final Mode m = new Mode(filename);
						System.out.println("Loaded "+filename+" - "+m.toString());
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								dialog.setVisible(false);
								setMode(m);
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

	MouseListener turnAutofitOff = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			fitToTextButton.setSelected(false);
			hideIntermediateButton.setSelected(false);
		}
	};

	private void setMode(Mode m) {
		if (m.getPipelineLength() + 1 != textWidgets.size()) {
			// Number of text widgets are different, dispose all and regenerate
			lastSplitPane = jSplitPane1;
			jSplitPane1.setBottomComponent(null);

			TextWidget lastTextWidget = textWidget1;

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
				/*
        tw.owner = this;
				tw.priority = i + 1;
				lastTextWidget.next = tw;
				*/
				tw.setup(this, i + 1, lastTextWidget);

				textWidgets.add(tw);
				lastTextWidget = tw;
				//lastTextWidget.textEditor.setFocusAccelerator((char)('0'+i+2));

				if (i < m.getPipelineLength() - 1) {
					JSplitPane sp = new JSplitPane();
					sp.setBorder(BorderFactory.createEmptyBorder());
					sp.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
					sp.setOneTouchExpandable(true);
					sp.setContinuousLayout(true);
					sp.setTopComponent(tw);
					try { ((BasicSplitPaneUI) sp.getUI()).getDivider().addMouseListener(turnAutofitOff); } catch (Exception e) { e.printStackTrace(); }
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
		}

		Pipeline.getPipeline().externalProcessing = local && useCppVersion.isSelected();
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
			//System.err.println("Pipeline.getPipeline().execPath = " + Pipeline.getPipeline().execPath);
			//new Exception().printStackTrace();

			String envVars = prefs.get("envVars", "").trim();
			Pipeline.getPipeline().envp = envVars.isEmpty() ? null : envVars.split("\n");
			//System.err.println("Pipeline.getPipeline().envp = " + envVars);

			Pipeline.getPipeline().ignoreErrorMessages = Boolean.parseBoolean(prefs.get("ignoreErrorMessages", "false"));
		}

		Pipeline.getPipeline().markUnknownWords = markUnknownWordsMenuItem.isSelected();
		Pipeline.getPipeline().traceTransferInterchunk = transferRuleTracingMenuItem.isSelected();

		// Set title to mode - easens window tabbing
		setTitle("Apertium-viewer (" + m + ")");

		// Update input text to match the current input language
		try {
			String language = getSourceLanguageCode(m);
			System.out.println("language="+language);
			String currentLanguage = currentMode==null?null:getSourceLanguageCode(currentMode);
			if (!language.equals(currentLanguage)) {
				prefs.put("inputText "+currentLanguage, textWidget1.getText());
				String txt = prefs.get("inputText "+language, null);
				if (txt==null) {
					ResourceBundle samples = ResourceBundle.getBundle("apertiumview.resources.sample_phrases");
					if (samples.containsKey(language)) txt = samples.getString(language);
				}
				if (txt!=null && txt.length()>0) textWidget1.setText(txt);
			}
		} catch (Exception e) { e.printStackTrace(); }

		currentMode = m;

		if (ignoreEvents) return;

		textWidget1.textChg(true);

	}



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


	private int insetHeight(JComponent c) {
		Insets i = c.getInsets();
		return i.top + i.bottom;
	}

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

	private JDialog showDialog(String message) {
		final JDialog dialog = new JDialog(this, "Downloading, please wait...", false);
		dialog.setContentPane(new JOptionPane(message,
				JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null));
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
		return dialog;
	}



	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
 always regenerated by the Form SourcecodeFinder.
	 */
	@SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    buttonGroup1 = new javax.swing.ButtonGroup();
    jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
    buttonGroup2 = new javax.swing.ButtonGroup();
    modesComboBox = new javax.swing.JComboBox();
    fitToTextButton = new javax.swing.JToggleButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    textWidgetsPanel = new javax.swing.JPanel();
    jSplitPane1 = new javax.swing.JSplitPane();
    textWidget1 = new apertiumview.TextWidget();
    copyTextButton = new javax.swing.JButton();
    jLabelMode = new javax.swing.JLabel();
    hideIntermediateButton = new javax.swing.JToggleButton();
    rdbtnLocal = new javax.swing.JRadioButton();
    rdbtnOnline = new javax.swing.JRadioButton();
    jLabelUse = new javax.swing.JLabel();
    useJavaVersion = new javax.swing.JRadioButton();
    useCppVersion = new javax.swing.JRadioButton();
    menuBar = new javax.swing.JMenuBar();
    javax.swing.JMenu fileMenu = new javax.swing.JMenu();
    loadModeMenuItem = new javax.swing.JMenuItem();
    editModesMenuItem = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JSeparator();
    javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
    toolsMenu = new javax.swing.JMenu();
    makeTestCaseMenuItem = new javax.swing.JMenuItem();
    importTestCaseMenuItem = new javax.swing.JMenuItem();
    storeMenuItem = new javax.swing.JMenuItem();
    storedTextsMenu = new javax.swing.JMenu();
    showMenu = new javax.swing.JMenu();
    markUnknownWordsMenuItem = new javax.swing.JCheckBoxMenuItem();
    showCommandsMenuItem = new javax.swing.JCheckBoxMenuItem();
    transferRuleTracingMenuItem = new javax.swing.JCheckBoxMenuItem();
    javax.swing.JMenu helpMenu = new javax.swing.JMenu();
    changeFontMenuItem = new javax.swing.JMenuItem();
    optionsMenuItem = new javax.swing.JMenuItem();
    helpMenuItem = new javax.swing.JMenuItem();
    javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

    jCheckBoxMenuItem1.setMnemonic('O');
    jCheckBoxMenuItem1.setSelected(true);
    jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

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
    jSplitPane1.setOneTouchExpandable(true);

    textWidget1.setPreferredSize(new java.awt.Dimension(200, 93));
    jSplitPane1.setTopComponent(textWidget1);

    javax.swing.GroupLayout textWidgetsPanelLayout = new javax.swing.GroupLayout(textWidgetsPanel);
    textWidgetsPanel.setLayout(textWidgetsPanelLayout);
    textWidgetsPanelLayout.setHorizontalGroup(
      textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 743, Short.MAX_VALUE)
      .addGroup(textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 743, Short.MAX_VALUE))
    );
    textWidgetsPanelLayout.setVerticalGroup(
      textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 238, Short.MAX_VALUE)
      .addGroup(textWidgetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
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

    jLabelMode.setDisplayedMnemonic('M');
    jLabelMode.setLabelFor(modesComboBox);
    jLabelMode.setText("Mode:");
    jLabelMode.setToolTipText("Change mode (language pair)");

    hideIntermediateButton.setMnemonic('D');
    hideIntermediateButton.setText("Hide intermediate");
    hideIntermediateButton.setToolTipText("Hides all but input and output");
    hideIntermediateButton.setMargin(new java.awt.Insets(0, 4, 0, 4));
    hideIntermediateButton.addActionListener(new java.awt.event.ActionListener() {
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

    jLabelUse.setText("Use");

    buttonGroup2.add(useJavaVersion);
    useJavaVersion.setSelected(true);
    useJavaVersion.setText("Java");
    useJavaVersion.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        useJavaVersionActionPerformed(evt);
      }
    });

    buttonGroup2.add(useCppVersion);
    useCppVersion.setText("C++ version");
    useCppVersion.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        useCppVersionActionPerformed(evt);
      }
    });

    fileMenu.setMnemonic('F');
    fileMenu.setText("File");

    loadModeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
    loadModeMenuItem.setMnemonic('L');
    loadModeMenuItem.setText("Load mode");
    loadModeMenuItem.setToolTipText("<html>Load an Apertium .mode file");
    loadModeMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        loadMode(evt);
      }
    });
    fileMenu.add(loadModeMenuItem);

    editModesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
    editModesMenuItem.setText("Edit modes");
    editModesMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        editModesMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(editModesMenuItem);
    fileMenu.add(jSeparator1);

    exitMenuItem.setText("Exit");
    exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exitMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(exitMenuItem);

    menuBar.add(fileMenu);

    toolsMenu.setMnemonic('T');
    toolsMenu.setText("Tools");

    makeTestCaseMenuItem.setText("Make Test Case");
    makeTestCaseMenuItem.setToolTipText("Creates text suitable for inserting in a Wiki test page");
    makeTestCaseMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        makeTestCase(evt);
      }
    });
    toolsMenu.add(makeTestCaseMenuItem);

    importTestCaseMenuItem.setText("Import Test Case...");
    importTestCaseMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        importTestCase(evt);
      }
    });
    toolsMenu.add(importTestCaseMenuItem);

    storeMenuItem.setMnemonic('S');
    storeMenuItem.setText("Store text");
    storeMenuItem.setToolTipText("Stores input text for later use");
    storeMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        storeMenuItemActionPerformed(evt);
      }
    });
    toolsMenu.add(storeMenuItem);

    storedTextsMenu.setMnemonic('D');
    storedTextsMenu.setText("Stored texts");
    toolsMenu.add(storedTextsMenu);

    menuBar.add(toolsMenu);

    showMenu.setMnemonic('S');
    showMenu.setText("Show");

    markUnknownWordsMenuItem.setMnemonic('u');
    markUnknownWordsMenuItem.setSelected(true);
    markUnknownWordsMenuItem.setText("Mark unknown words with a *");
    markUnknownWordsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markUnknownWordsMenuItemActionPerformed(evt);
      }
    });
    showMenu.add(markUnknownWordsMenuItem);

    showCommandsMenuItem.setMnemonic('C');
    showCommandsMenuItem.setSelected(true);
    showCommandsMenuItem.setText("Show commands (and buttons to edit source)");
    showCommandsMenuItem.setToolTipText("Shiow what shell commands are actually invoked, and buttons to edit source code files, freeze text, ...");
    showCommandsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        showCommandsMenuItemActionPerformed(evt);
      }
    });
    showMenu.add(showCommandsMenuItem);

    transferRuleTracingMenuItem.setMnemonic('T');
    transferRuleTracingMenuItem.setSelected(true);
    transferRuleTracingMenuItem.setText("Trace transfer & interchunk rules");
    transferRuleTracingMenuItem.setToolTipText("Shows which rules are matched");
    transferRuleTracingMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        transferRuleTracingMenuItemActionPerformed(evt);
      }
    });
    showMenu.add(transferRuleTracingMenuItem);

    menuBar.add(showMenu);

    helpMenu.setMnemonic('V');
    helpMenu.setText("View");

    changeFontMenuItem.setText("Change font");
    changeFontMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        changeFont(evt);
      }
    });
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

    aboutMenuItem.setText("About...");
    aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        showAboutBox(evt);
      }
    });
    helpMenu.add(aboutMenuItem);

    menuBar.add(helpMenu);

    setJMenuBar(menuBar);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jLabelUse)
        .addGap(4, 4, 4)
        .addComponent(useJavaVersion)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(useCppVersion)
        .addGap(18, 18, 18)
        .addComponent(fitToTextButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(hideIntermediateButton)
        .addGap(18, 18, 18)
        .addComponent(copyTextButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jLabelMode)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(modesComboBox, 0, 65, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(rdbtnLocal)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(rdbtnOnline)
        .addContainerGap())
      .addComponent(jScrollPane1)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(modesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(fitToTextButton)
          .addComponent(copyTextButton)
          .addComponent(hideIntermediateButton)
          .addComponent(rdbtnLocal)
          .addComponent(rdbtnOnline)
          .addComponent(jLabelMode)
          .addComponent(jLabelUse)
          .addComponent(useJavaVersion)
          .addComponent(useCppVersion))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE))
    );

    pack();
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
	updateSelectedMode();
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
	op.workingDirTextField.setText(prefs.get("workingDir", ""));
	op.envVarsTextArea.setText(prefs.get("envVars", ""));
	op.ignoreStdErr.setSelected(Boolean.parseBoolean(prefs.get("ignoreStdErr", "false")));
	int ret = JOptionPane.showConfirmDialog(mainPanel, op, "Edit Options", JOptionPane.OK_CANCEL_OPTION);
	if (ret == JOptionPane.OK_OPTION) {
		prefs.put("workingDir", op.workingDirTextField.getText());
		prefs.put("envVars", op.envVarsTextArea.getText());
		prefs.put("ignoreStdErr", Boolean.toString(op.ignoreStdErr.isSelected()));
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
	textChanged(); // invokes hideIntermediate();
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



private void fitToText(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fitToText
	if (fitToTextButton.isSelected()) textChanged(); // invokes fitToText();
}//GEN-LAST:event_fitToText

private void fitToText() {
	int toth = 0;
	for (int i=0; i<=splitPanes.size(); i++) {
		TextWidget w = textWidgets.get(i);
		if (w.getStatus() == w.STATUS_EQUAL) {
			w.setPreferredSize(new Dimension());
			w.setMinimumSize(new Dimension());
		} else {
			w.setPreferredSize(null);
			w.setMinimumSize(null);
			//if (i==0 || i==splitPanes.size()-1) w.setMinimumSize(w.getPreferredSize());
		}
		toth += w.getPreferredSize().height + 12;
		toth += insetHeight(w);
		if (i<splitPanes.size()) {
			JSplitPane s = splitPanes.get(i);
			s.setDividerLocation(-1);
			toth += s.getDividerSize() + insetHeight(s);
		}
	}
	textWidgetsPanel.setMinimumSize(new Dimension(0, toth));
	textWidgetsPanel.setPreferredSize(new Dimension(0, toth));
	mainPanel.validate();
	int extraSpace = (jScrollPane1.getHeight() - toth) / 2;
	if (extraSpace>0) {
			JSplitPane s = splitPanes.get(0);
			System.out.println("setDividerLocation( "+s.getDividerLocation());
			s.setDividerLocation(s.getDividerLocation() + extraSpace);
			System.out.println("setDividerLocation2( "+s.getDividerLocation());
	}
}

	public void hideIntermediate() {
		textWidgets.get(0).setMinimumSize(null);
		textWidgets.get(0).setPreferredSize(null);
		splitPanes.get(0).setDividerLocation(-1);
		for (int i=1; i<splitPanes.size(); i++) {
			textWidgets.get(i).setMinimumSize(new Dimension());
			textWidgets.get(i).setPreferredSize(new Dimension());
			splitPanes.get(i).setDividerLocation(-1);
		}
	}


	void textChanged() {
		textWidget1.scrollRectToVisible(new Rectangle());
		if (hideIntermediateButton.isSelected()) { hideIntermediate(); return; }
		if (fitToTextButton.isSelected()) { fitToText(); return; }
	}


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
				final JDialog dialog = showDialog("Please wait while downloading the list of pairs");

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

  private void showAboutBox(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAboutBox
		if (aboutBox == null) {
			aboutBox = new ApertiumViewAboutBox(this);
			aboutBox.setLocation(this.getLocation());
			aboutBox.pack();
		}
		aboutBox.setVisible(true);
  }//GEN-LAST:event_showAboutBox


  private void loadMode(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMode
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
			rdbtnLocal.setSelected(true);
			for (File f : fs) {
				loadMode(f.getPath());
			}
			prefs.putInt("modesComboBoxLocal", modesComboBox.getItemCount()-1);

			updateModesComboBox();
			String mpref = "";
			for (Mode mo : modes) mpref = mpref + mo.getFilename() + "\n";
			prefs.put("modeFiles", mpref);
		}
  }//GEN-LAST:event_loadMode

  private void changeFont(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeFont
		FontDialog fd = new FontDialog((Frame) SwingUtilities.getWindowAncestor(mainPanel));
		fd.setInitialFont(textWidget1.textEditor.getFont());
		fd.setLocationRelativeTo(this);
		fd.setVisible(true);
		textWidgetFont = fd.getFont();
		for (TextWidget t : textWidgets) {
			t.textEditor.setFont(textWidgetFont);
		}
		System.out.println("f = " + textWidgetFont);
  }//GEN-LAST:event_changeFont

  public static String getSourceLanguageCode(Mode m) {
		// TODO: use m.getSourceLanguageCode()
		return new File(m.getFilename()).getName().split("-",2)[0];
  }


  private void makeTestCase(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeTestCase
		String[] firstTxt = null;
		String[] lastTxt = null;
		for (TextWidget w : textWidgets) { // ugly but it works :-)
			String[] txt = w.getText().split("\n");
			if (firstTxt == null) firstTxt = txt;
			lastTxt = txt;
		}

		if (!local) {
			warnUser("Sorry, not supported for online modes");
			return;
		}
		Mode mode = (Mode) modesComboBox.getSelectedItem();
		String sourceLanguage = getSourceLanguageCode(mode);

		String tottxt = "";
		for (int i = 0; i < firstTxt.length; i++) {
			tottxt = tottxt + "* (" + sourceLanguage + ") ''" + firstTxt[i] + "'' → " + lastTxt[i] + "\n";
		}

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(tottxt.trim()), null);
		JOptionPane.showMessageDialog(mainPanel, new JTextArea(tottxt.trim()), "Paste into a Wiki test page", JOptionPane.INFORMATION_MESSAGE);
  }//GEN-LAST:event_makeTestCase

  private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
		shutdown();
  }//GEN-LAST:event_exitMenuItemActionPerformed

  private void storeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_storeMenuItemActionPerformed
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
			warnUser(e.toString());
		}
  }//GEN-LAST:event_storeMenuItemActionPerformed

  private void markUnknownWordsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markUnknownWordsMenuItemActionPerformed
		setMode(currentMode); // Makes the pipeline update itself
  }//GEN-LAST:event_markUnknownWordsMenuItemActionPerformed

  private void showCommandsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showCommandsMenuItemActionPerformed
		boolean show = showCommandsMenuItem.isSelected();
		//System.out.println("show = " + show);
		for (TextWidget w : textWidgets) w.setShowCommands(show);
		textWidget1.setShowCommands(false);
		textChanged();
		mainPanel.validate();
  }//GEN-LAST:event_showCommandsMenuItemActionPerformed

  private void transferRuleTracingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transferRuleTracingMenuItemActionPerformed
		setMode(currentMode); // Makes the pipeline update itself
  }//GEN-LAST:event_transferRuleTracingMenuItemActionPerformed

  private void useJavaVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useJavaVersionActionPerformed
		setMode(currentMode); // Makes the pipeline update itself
  }//GEN-LAST:event_useJavaVersionActionPerformed

  private void useCppVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCppVersionActionPerformed
		setMode(currentMode); // Makes the pipeline update itself
  }//GEN-LAST:event_useCppVersionActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  javax.swing.ButtonGroup buttonGroup1;
  private javax.swing.ButtonGroup buttonGroup2;
  javax.swing.JMenuItem changeFontMenuItem;
  javax.swing.JButton copyTextButton;
  javax.swing.JMenuItem editModesMenuItem;
  javax.swing.JToggleButton fitToTextButton;
  javax.swing.JMenuItem helpMenuItem;
  javax.swing.JToggleButton hideIntermediateButton;
  javax.swing.JMenuItem importTestCaseMenuItem;
  private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
  javax.swing.JLabel jLabelMode;
  private javax.swing.JLabel jLabelUse;
  javax.swing.JScrollPane jScrollPane1;
  javax.swing.JSeparator jSeparator1;
  javax.swing.JSplitPane jSplitPane1;
  javax.swing.JMenuItem loadModeMenuItem;
  javax.swing.JMenuItem makeTestCaseMenuItem;
  private javax.swing.JCheckBoxMenuItem markUnknownWordsMenuItem;
  javax.swing.JMenuBar menuBar;
  javax.swing.JComboBox modesComboBox;
  javax.swing.JMenuItem optionsMenuItem;
  javax.swing.JRadioButton rdbtnLocal;
  javax.swing.JRadioButton rdbtnOnline;
  private javax.swing.JCheckBoxMenuItem showCommandsMenuItem;
  private javax.swing.JMenu showMenu;
  private javax.swing.JMenuItem storeMenuItem;
  javax.swing.JMenu storedTextsMenu;
  apertiumview.TextWidget textWidget1;
  javax.swing.JPanel textWidgetsPanel;
  javax.swing.JMenu toolsMenu;
  private javax.swing.JCheckBoxMenuItem transferRuleTracingMenuItem;
  private javax.swing.JRadioButton useCppVersion;
  private javax.swing.JRadioButton useJavaVersion;
  // End of variables declaration//GEN-END:variables

}
