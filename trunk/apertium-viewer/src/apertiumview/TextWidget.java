/*
 * Copyright 2015 Jacob Nordfalk
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 */
package apertiumview;

import apertiumview.highlight.HighlightTextEditor;
import apertiumview.sourceeditor.SourcecodeFinder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.apertium.pipeline.Program;
import static org.apertium.pipeline.Program.ProgEnum.INTERCHUNK;
import static org.apertium.pipeline.Program.ProgEnum.TRANSFER;

/**
 *
 * @author  j
 */
public class TextWidget extends javax.swing.JPanel {
	private int priority;
	private ApertiumView owner;
	private Program program;
	private String commandsHtmlText;
	private String sourceFiles = "";
	private boolean changing = false;
	private TextWidget next;
	private String lastSetTxt;
	private JDialog zoomDialog;
	
	public static final Color STATUS_OK = Color.WHITE;
	public static final Color STATUS_EQUAL = new Color(248, 248, 210);
	public static final Color STATUS_ERROR = Color.RED.brighter();
	private Color status = STATUS_OK;

	/** Creates new form TextWidget */
	public TextWidget() {
		changing = true;
		initComponents();

		commandTextPane.setBackground(this.getBackground());

		// Observing text changes
		textEditor.getDocument().addDocumentListener(new AbstactDocumentListener() {
			@Override
			public void changed(DocumentEvent e) {
				textChg(false);
			}
		});

		// Listen for undo and redo events
		final UndoManager undo = new UndoManager();
		textEditor.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent evt) {
				undo.addEdit(evt.getEdit());
			}
		});

		// Create an undo action and add it to the text component
		textEditor.getActionMap().put("Undo",
				new AbstractAction("Undo") {
					public void actionPerformed(ActionEvent evt) {
						try {
							if (undo.canUndo()) {
								undo.undo();
							}
						} catch (CannotUndoException e) {
						}
					}
				});

		// Bind the undo action to ctl-Z
		textEditor.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

		// Create a redo action and add it to the text component
		textEditor.getActionMap().put("Redo",
				new AbstractAction("Redo") {
					public void actionPerformed(ActionEvent evt) {
						try {
							if (undo.canRedo()) {
								undo.redo();
							}
						} catch (CannotRedoException e) {
						}
					}
				});

		// Bind the redo action to ctl-Y
		textEditor.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

		textEditor.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int idx = textEditor.viewToModel(e.getPoint());
				//System.out.println("idx = " + idx);
				String txt = textEditor.getText();
				if (txt.length() == idx || idx < 2) return;
				int startLU = txt.substring(0, idx).lastIndexOf('^');
				int endLU = txt.indexOf('$', idx - 1);
				if (startLU < 0 || endLU < 0) return;

				String lu = txt.substring(startLU, endLU);
				System.out.println("lu = " + lu);
				idx = idx - startLU;
				if (idx <= 0 || idx >= lu.length() || lu.indexOf('$') != -1) return;
				int end = lu.indexOf('/', idx);
				int start = lu.substring(0, idx).lastIndexOf('/');
				if (end == -1 && lu.indexOf('/') != start) end = lu.length();
				if (start >= 0 && end > 0) {
					textEditor.select(start + startLU, end + startLU);
				}
			}
		});

		changing = false;
	}

	private FocusListener scrollToVisibleFocusListener = new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
			// Ugly hack to get the enclosing TextWidget
			final TextWidget tw = (TextWidget) ((JComponent) e.getSource()).getParent().getParent().getParent();
			// Scroll so that the TextWidget's scoll pane (containing the text area) is fully visible
			Rectangle b = tw.textScrollPane.getBounds();
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

	private HyperlinkListener hyperlinkListener = new HyperlinkListener() {
      @Override
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) return;
				owner.linkWasClicked(e.getURL());
			}
    };


	void setup(ApertiumView owner, int priority, TextWidget lastTextWidget) {
		this.owner = owner;
		this.priority = priority;
		commandTextPane.addHyperlinkListener(hyperlinkListener);
		errorTextPane.addHyperlinkListener(hyperlinkListener);
		textEditor.addFocusListener(scrollToVisibleFocusListener);
		textEditor.addKeyListener(owner.switchFocus);
		setError("");
		lastTextWidget.next = this;
	}

	public void setStatus(Color _status) {
		status = _status;
		textEditor.setBackground(_status);
	}

	public Color getStatus() {
		return status;
	}


	public String getText() {
		return textEditor.getText();
	}

	public void setText(String newTxt) {
        //System.out.println("setText"+priority + newTxt);
		//new Exception("setText"+priority + newTxt).printStackTrace();
		if (next != null && !next.freezeCheckBox.isSelected()) {
			Pipeline.getPipeline().queueAsyncProcessing(this, this.priority, newTxt, next);
		} else {
			owner.textChanged(); // this is the last widget that gets its text updated - notify so splitpanes can be fittet
		}
		changing = true;
		try {

			//System.err.println("newTxt = " + newTxt);
			textEditor.setText(newTxt);
		} catch (Exception e) {
            // For some reason this sometimes fails. Try again and then give up
			//e.printStackTrace();
			System.err.println(this + ".setText(" + newTxt + "): " + e + " - trying again...");
			try {
				Thread.sleep(50);
				textEditor.setText(newTxt);
				System.err.println(this.getClass() + ".setText() 2nd try went good");
			} catch (Exception e2) {
				System.err.println(this.getClass() + ".setText() 2nd try failed");
				//e2.printStackTrace();
			}
		}
		lastSetTxt = newTxt;
		changing = false;
	}

	public void textChg(boolean force) {
		textEditor.setForeground(Color.BLACK);
		if (changing) return;
		String newTxt = textEditor.getText();
		// workaround for delayed text events
		if (!force && newTxt.equals(lastSetTxt)) return;
		lastSetTxt = newTxt;

        //new Exception("textChn()"+priority + newTxt).printStackTrace();
		if (next != null && !next.freezeCheckBox.isSelected()) {
			Pipeline.getPipeline().queueAsyncProcessing(this, this.priority, textEditor.getText(), next);
		} else {
			owner.textChanged(); // this is the last widget that gets its text updated - notify so splitpanes can be fittet
		}
	}


	void setError(String err) {
		String html = SourcecodeFinder.createHtmlErr(program, err);
			System.out.println("\n\nHTML\n"+html+"\n");
			errorTextPane.setText(html);
//		errorTextPane.setContentType("text/html");
		errorScrollPane.setPreferredSize(err.isEmpty() ? new Dimension(1,1) : null); // make it fit
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;

		StringBuilder sourceFilesSb = new StringBuilder();
		commandsHtmlText = SourcecodeFinder.createHtmlLinkText(program, sourceFilesSb);
		commandTextPane.setText(commandsHtmlText);
		//else commandTextPane.setText("<html><div style='white-space:nowrap;font-size:12pt'>" + program.toString());
		commandTextPane.setCaretPosition(0); // scroll to start of text
		sourceFiles = sourceFilesSb.toString().trim();
		commandTextPane.setToolTipText(sourceFiles);
		jButtonEditSource.setVisible(sourceFilesSb.length() > 0);
	}

	void setShowCommands(boolean show) {
		commandTextPane.setVisible(show);
		commandScrollPane.setVisible(show);
		jButtonEditSource.setVisible(show && sourceFiles.length() > 0);
		zoomButton.setVisible(show);
		freezeCheckBox.setVisible(show);
//		setSize(getSize()); // why?
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    zoomButton = new javax.swing.JButton();
    commandScrollPane = new javax.swing.JScrollPane();
    commandTextPane = new javax.swing.JTextPane();
    freezeCheckBox = new javax.swing.JCheckBox();
    textScrollPane = new javax.swing.JScrollPane();
    textEditor = new apertiumview.highlight.HighlightTextEditor();
    jButtonEditSource = new javax.swing.JButton();
    errorScrollPane = new javax.swing.JScrollPane();
    errorTextPane = new javax.swing.JTextPane();

    zoomButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/apertiumview/resources/zoom-in.png"))); // NOI18N
    zoomButton.setToolTipText("Opens a separate window with the text");
    zoomButton.setBorder(null);
    zoomButton.setMargin(new java.awt.Insets(0, 5, 0, 5));
    zoomButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        zoomButtonActionPerformed(evt);
      }
    });

    commandScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
    commandScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    commandScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    commandScrollPane.setAlignmentY(1.0F);

    commandTextPane.setEditable(false);
    commandTextPane.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
    commandTextPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
    commandTextPane.setContentType("text/html"); // NOI18N
    commandTextPane.setText("<html>hej <b>verden");
    commandTextPane.setAlignmentY(1.0F);
    commandScrollPane.setViewportView(commandTextPane);

    freezeCheckBox.setFont(new java.awt.Font("SansSerif", 0, 11));
    freezeCheckBox.setText("Freeze");
    freezeCheckBox.setToolTipText("Freeze the view so that changes are not propagated further");

    textScrollPane.setBorder(null);
    textScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    textEditor.setBorder(null);
    textEditor.setFont(new java.awt.Font("Dialog", 0, 15)); // NOI18N
    textEditor.setText("Sample text");
    textEditor.setMargin(new java.awt.Insets(0, 3, 0, 3));
    textScrollPane.setViewportView(textEditor);

    jButtonEditSource.setText("Edit source");
    jButtonEditSource.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonEditSourceActionPerformed(evt);
      }
    });

    errorScrollPane.setBorder(null);
    errorScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    errorScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

    errorTextPane.setEditable(false);
    errorTextPane.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
    errorTextPane.setBorder(null);
    errorTextPane.setContentType("text/html"); // NOI18N
    errorTextPane.setForeground(new java.awt.Color(153, 0, 0));
    errorTextPane.setText("<html>\n  <head>\n\n  </head>\n  <body>\n    <p style=\"margin-top: 0\">\n      error\n    </p>\n  </body>\n</html>\n"); // NOI18N
    errorTextPane.setToolTipText("");
    errorScrollPane.setViewportView(errorTextPane);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addComponent(commandScrollPane)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButtonEditSource)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(freezeCheckBox)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(zoomButton, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addComponent(textScrollPane)
      .addComponent(errorScrollPane)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(commandScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
            .addComponent(zoomButton, 0, 0, Short.MAX_VALUE)
            .addComponent(jButtonEditSource, 0, 0, Short.MAX_VALUE)
            .addComponent(freezeCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        .addComponent(errorScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, 0)
        .addComponent(textScrollPane)
        .addGap(0, 0, 0))
    );
  }// </editor-fold>//GEN-END:initComponents

  private void zoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomButtonActionPerformed
		if (zoomDialog == null) {
			zoomDialog = new JDialog((Window) null, getProgram().toString());
			JTextComponent te = new HighlightTextEditor();
			te.setDocument(textEditor.getDocument());
			zoomDialog.add(new JScrollPane(te));
			JComponent src = (JComponent) evt.getSource();
			zoomDialog.setLocationRelativeTo(src);
			zoomDialog.setSize(textEditor.getSize());
			zoomDialog.validate();
		}
		zoomDialog.setVisible(!zoomDialog.isVisible()); // show/hide
}//GEN-LAST:event_zoomButtonActionPerformed

  private void jButtonEditSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditSourceActionPerformed
		// Find all the links in in the command HTML representation and open them
		Pattern pattern = Pattern.compile("<a href='([^']*)'>");

		Matcher matcher = pattern.matcher(commandsHtmlText);
		while (matcher.find()) try {
			String url = matcher.group(1);
			//System.out.println(url);
			owner.linkWasClicked(new URL(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
  }//GEN-LAST:event_jButtonEditSourceActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane commandScrollPane;
  javax.swing.JTextPane commandTextPane;
  private javax.swing.JScrollPane errorScrollPane;
  private javax.swing.JTextPane errorTextPane;
  private javax.swing.JCheckBox freezeCheckBox;
  private javax.swing.JButton jButtonEditSource;
  apertiumview.highlight.HighlightTextEditor textEditor;
  javax.swing.JScrollPane textScrollPane;
  private javax.swing.JButton zoomButton;
  // End of variables declaration//GEN-END:variables
}
