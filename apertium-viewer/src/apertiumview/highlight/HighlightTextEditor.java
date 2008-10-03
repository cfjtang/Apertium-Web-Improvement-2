package apertiumview.highlight;

import java.awt.Dimension;
import javax.swing.JTextPane;
import javax.swing.text.StyledEditorKit;

import javax.swing.text.Element;
import javax.swing.text.*;

/**
 *
 * @author j
 */
public class HighlightTextEditor extends JTextPane {
//public class HighlightTextEditor extends  JTextArea { 

    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        Dimension size = null;
        if (ui != null) {
            size = ui.getPreferredSize(this);
        }
        return (size != null) ? size : super.getPreferredSize();
    }
    
    
    public boolean getScrollableTracksViewportWidth() { return true; }
    /*
    public HighlightTextEditor() {
        
        // Set editor kit
        this.setEditorKitForContentType(CONTENTTYPE, ;
        this.setContentType(CONTENTTYPE);        
    }*/
    protected EditorKit createDefaultEditorKit() {
        return new HighlightEditorKitViewFactory();
    }

    
    public static final String CONTENTTYPE = "text/apertium-stream-format";
    
    public class HighlightEditorKitViewFactory extends StyledEditorKit  implements ViewFactory {

    @Override
    public ViewFactory getViewFactory() {
        return this;
    }

    @Override
    public String getContentType() {
        return CONTENTTYPE;
    }
    
    /**
     * @see javax.swing.text.ViewFactory#create(javax.swing.text.Element)
     */
    public View create(Element element) {
        //String kind = element.getName();
        //System.out.println("kind = " + kind);
        view = new HighlightView(element);
        return view;
    }
    }
    HighlightView view;
}

/*    
    public static void main(String[] args) {
        //JTextComponent e = new JTextArea(); //HighlightTextEditor();
        //((JTextArea)e).setLineWrap(true);
        JTextComponent e = new HighlightTextEditor();
        e.setText("sdlkjsd d");
        //JScrollPane sp = new JScrollPane(e);
        JScrollPane sp = new JScrollPane(e);
        sp.setHorizontalScrollBarPolicy(sp.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(sp.VERTICAL_SCROLLBAR_NEVER);
        JFrame jf = new JFrame();
        jf.add(sp);
        jf.pack();
        
        jf.show();
    }
*/
