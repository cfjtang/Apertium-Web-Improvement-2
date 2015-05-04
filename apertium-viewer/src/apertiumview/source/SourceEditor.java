/*
 * Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License 
 *       at http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 */
package apertiumview.source;

import apertiumview.ApertiumView;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.SyntaxDocument;
import jsyntaxpane.Token;
import jsyntaxpane.actions.ActionUtils;
import jsyntaxpane.actions.CaretMonitor;
import jsyntaxpane.syntaxkits.XmlSyntaxKit;
import org.apertium.pipeline.Program;
import static org.apertium.pipeline.Program.ProgEnum.*;

public class SourceEditor extends javax.swing.JFrame {
	private String loadedFilename;
	private HashMap<String,String> loadedFileProperties;
	private Program.ProgEnum loadedFileProgram;
	private ApertiumView owner;

	public SourceEditor(ApertiumView aThis) {
		owner = aThis;
		initComponents();
		jCmbLangs.setModel(new DefaultComboBoxModel(DefaultSyntaxKit.getContentTypes()));
		jCmbLangs.setSelectedItem("text/xml");
		new CaretMonitor(jEdtTest, lblCaretPos);
	}

	public SourceEditor() {
		this(null);
	}

  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    lblCaretPos = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    jEdtTest = new javax.swing.JEditorPane();
    jCmbLangs = new javax.swing.JComboBox();
    jToolBar1 = new javax.swing.JToolBar();
    jButtonValidate = new javax.swing.JButton();
    jButtonSave = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("apertiumview/source/Bundle"); // NOI18N
    setTitle(bundle.getString("SourceEditor.title")); // NOI18N
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });

    lblCaretPos.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    lblCaretPos.setText(bundle.getString("SourceEditor.lblCaretPos.text")); // NOI18N
    lblCaretPos.setToolTipText(bundle.getString("SourceEditor.lblCaretPos.toolTipText")); // NOI18N

    jEdtTest.setContentType("text/java"); // NOI18N
    jScrollPane1.setViewportView(jEdtTest);

    jCmbLangs.setMaximumRowCount(20);
    jCmbLangs.setFocusable(false);
    jCmbLangs.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        jCmbLangsItemStateChanged(evt);
      }
    });

    jToolBar1.setRollover(true);
    jToolBar1.setFocusable(false);

    jButtonValidate.setText(bundle.getString("SourceEditor.jButtonValidate.text")); // NOI18N
    jButtonValidate.setFocusable(false);
    jButtonValidate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonValidate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonValidate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonValidateActionPerformed(evt);
      }
    });
    jToolBar1.add(jButtonValidate);

    jButtonSave.setText(bundle.getString("SourceEditor.jButtonSave.text")); // NOI18N
    jButtonSave.setFocusable(false);
    jButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    jButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jButtonSave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonSaveActionPerformed(evt);
      }
    });
    jToolBar1.add(jButtonSave);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jCmbLangs, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(18, 18, 18)
        .addComponent(lblCaretPos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, 0)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
        .addGap(0, 0, 0)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lblCaretPos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jCmbLangs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(0, 0, 0))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void jCmbLangsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCmbLangsItemStateChanged
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			String lang = jCmbLangs.getSelectedItem().toString();

			// save the state of the current JEditorPane, as it's Document is about
			// to be replaced.
			String oldText = jEdtTest.getText();

			// install a new DefaultSyntaxKit on the JEditorPane for the requested language.
			jEdtTest.setContentType(lang);
			// Recreate the Toolbar
			jToolBar1.removeAll();
			EditorKit kit = jEdtTest.getEditorKit();
			if (kit instanceof DefaultSyntaxKit) {
				DefaultSyntaxKit defaultSyntaxKit = (DefaultSyntaxKit) kit;
				defaultSyntaxKit.addToolBarActions(jEdtTest, jToolBar1);
			}
	    jToolBar1.add(jButtonValidate);
	    jToolBar1.add(jButtonSave);
			jToolBar1.validate();
			try {
				// setText should not be called (read the JavaDocs).  Better use the read
				// method and create a new document.
				Document doc = kit.createDefaultDocument();
				doc.insertString(0, oldText, null);				
				jEdtTest.setDocument(doc);
				//jEdtTest.read(new StringReader(oldText), lang);
			} catch (Exception ex) {
				Logger.getLogger(SourceEditor.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		jEdtTest.requestFocusInWindow();
    }//GEN-LAST:event_jCmbLangsItemStateChanged

  private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
		if (!validateFile() &&
			JOptionPane.showConfirmDialog(this, "The file doesent validate. Are you sure you want to save it?", "Save file?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
			return;
		
		try {
			String text = jEdtTest.getText();
			Path filePath = Paths.get(loadedFilename);
			Files.write(filePath, text.getBytes("UTF-8"));
			LinkedHashSet<Path> compileDirs = new LinkedHashSet<>();
			compileDirs.add(filePath.getParent()); // Directory of the source file
			compileDirs.add(Paths.get(loadedFileProperties.get("dir"))); // Directory of the binary file

			for (Path dir : compileDirs) {
				Process p = new ProcessBuilder("make").directory(dir.toFile()).redirectErrorStream(true).start();
				BufferedReader std = new BufferedReader(new InputStreamReader(p.getInputStream(),"UTF-8"));
				final StringBuilder outputsb = new StringBuilder();
				String lin;
				while ( (lin=std.readLine())!=null) {
					System.out.println(lin);
					outputsb.append(lin).append('\n');
				}
				if (p.waitFor()!=0) {
					JOptionPane.showMessageDialog(this, outputsb.toString(), "Compiler error", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
			lblCaretPos.setText("The pair was compiled sucessfully");
			owner.compiledWithSourceEditor(loadedFilename);

		} catch (Exception ex) {
			Logger.getLogger(SourceEditor.class.getName()).log(Level.SEVERE, null, ex);
		}
  }//GEN-LAST:event_jButtonSaveActionPerformed


	private boolean validateFile() {
		try {
			String cmd = null;
			switch (loadedFileProgram) {
				case LT_PROC: cmd = "apertium-validate-dictionary"; break;
				case TRANSFER:
					if (loadedFilename.contains("dix")) cmd = "apertium-validate-dictionary"; // guess
					else cmd = "apertium-validate-transfer";
					break;
				case INTERCHUNK: cmd = "apertium-validate-interchunk"; break;
				case POSTCHUNK: cmd = "apertium-validate-postchunk"; break;
			}
			if (cmd==null) {
				lblCaretPos.setText("Don't know how to validate this file");
				return true;
			}
			String[] fn = new File(loadedFilename).getName().split("\\.", 2);
			File tmpFileContent = File.createTempFile(fn[0], fn[1]);
			tmpFileContent.deleteOnExit();
			String text = jEdtTest.getText();
			Files.write(Paths.get(tmpFileContent.getPath()), text.getBytes("UTF-8"));

			Process p = new ProcessBuilder(cmd, tmpFileContent.getPath()).redirectErrorStream(true).start();
			String lin = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8")).readLine();
			p.destroy();
			if (lin == null) {
				lblCaretPos.setText("File looks OK, you can save it");
				return true;
			}
			System.out.println(lin);
			String[] err = lin.split(":", 3);
			if (err.length < 3) {
				lblCaretPos.setText("File looks OK, you can save it");
				return true;
			}
			// tmpfile.dix:1: parser error : Start tag expected, '<' not found
			int lineNo = Integer.parseInt(err[1]);
			String msg = err[2];

			Element root = jEdtTest.getDocument().getDefaultRootElement();
			lineNo = Math.min(lineNo, root.getElementCount());
			int startOfLineOffset = root.getElement( lineNo - 1 ).getStartOffset();
			jEdtTest.setCaretPosition( startOfLineOffset );

			lblCaretPos.setText(msg);
		} catch (Exception e) {
			e.printStackTrace();
			lblCaretPos.setText(e.toString());
		}
		return false;
	}


  private void jButtonValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonValidateActionPerformed
    validateFile();
  }//GEN-LAST:event_jButtonValidateActionPerformed

  private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    // TODO add your handling code here:
		owner.closeSourceEditor(loadedFilename);
  }//GEN-LAST:event_formWindowClosing


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButtonSave;
  private javax.swing.JButton jButtonValidate;
  private javax.swing.JComboBox jCmbLangs;
  private javax.swing.JEditorPane jEdtTest;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JLabel lblCaretPos;
  // End of variables declaration//GEN-END:variables

	public void loadFile(String filename, String properties) throws BadLocationException, IOException {
		setTitle(filename);
		loadedFilename = filename;
		loadedFileProperties = new HashMap<>();
		for (String pair : properties.split("&")) {
			String[] vv = pair.split("=");
			loadedFileProperties.put(vv[0], vv[1]);
		}
		loadedFileProgram = Program.ProgEnum.valueOf(loadedFileProperties.get("program"));
		System.out.println(loadedFileProgram+ " "+properties);
		EnumSet<Program.ProgEnum> xmlFiles = EnumSet.of(LT_PROC, TAGGER, PRETRANSFER, TRANSFER, INTERCHUNK, POSTCHUNK);
		if (xmlFiles.contains(loadedFileProgram)) {
			jEdtTest.setContentType("text/xml");
		} else {
			jEdtTest.setContentType("text/plain");
		}
		jCmbLangs.setSelectedItem(jEdtTest.getContentType());
		Document doc = jEdtTest.getEditorKit().createDefaultDocument();
		String str = new String(Files.readAllBytes(Paths.get(filename)), "UTF-8");
		doc.insertString(0,str,null);
		jEdtTest.setDocument(doc);
	}


	private static SourceEditor jFrame;
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) throws Exception {

		java.awt.EventQueue.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				try {
					DefaultSyntaxKit.initKit();
					jFrame = new SourceEditor();
					jFrame.setVisible(true);
//					jFrame.read("/home/j/esperanto/apertium/trunk/apertium-eo-en/apertium-eo-en.eo-en.dix");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
