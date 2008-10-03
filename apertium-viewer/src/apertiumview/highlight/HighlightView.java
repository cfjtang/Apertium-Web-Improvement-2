/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package apertiumview.highlight;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.GlyphView;
import javax.swing.text.LabelView;
import javax.swing.text.PlainDocument;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;
import javax.swing.text.WrappedPlainView;

/**
 * Thanks: http://www.boplicity.net/confluence/display/Java/Xml+syntax+highlighting+in+Swing+JTextPane
 * 
 * 
 * Using WrappedPlainView here because we DO want line wrapping to occur.
 * 
 * @author kees
 * @date 13-jan-2006
 *
 */
public class HighlightView extends WrappedPlainView {//PlainView 


        public HighlightView(Element element) {

        super(element, true); // wraps whole units
        //super(element, false); // wraps a char at a time
    }


    private static HashMap<Pattern, Color> patternColors;
 // IMPORTANT NOTE: regex should contain 1 group. Groups other than group one is ignored
    private static String TAG = "(<[\\w\\s]+)>?";
    private static String LEMMA = "\\^(\\w+)";
    //private static String UNKNOWN_LEMMA = "\\^(\\w+\\/\\*\\w+)\\$";
    private static String UNKNOWN_LEMMA = "\\^(\\w+)\\/\\*\\w+\\$";
    private static String AMBIGIOUS_LEMMA = "\\^(\\w+)/[^\\$]+(/[^\\$]+)+\\$";
    private static String COMMENT = "(<!--.*-->)";
   
    static {
        // NOTE: the order is important!
        patternColors = new HashMap<Pattern, Color>();
        // tags are #aaaaaa, ^ and $ are #009900 { } are #999900 @ * # are #990000 and [] are #aaaaff 
        patternColors.put(Pattern.compile(TAG),  Color.decode("#aaaaaa"));
        patternColors.put(Pattern.compile(LEMMA),  Color.BLUE);
        patternColors.put(Pattern.compile(UNKNOWN_LEMMA),  Color.RED);
        patternColors.put(Pattern.compile(AMBIGIOUS_LEMMA),  Color.RED.darker());
        patternColors.put(Pattern.compile("([\\^\\$]+)"),  Color.decode("#009900"));
        patternColors.put(Pattern.compile("([{}]+)"),  Color.decode("#999900"));
        patternColors.put(Pattern.compile("([@\\*#]+)"),  Color.decode("#990000"));
        patternColors.put(Pattern.compile("([\\[\\]]+)"),  Color.decode("#aaaaff"));
        patternColors.put(Pattern.compile(COMMENT), Color.LIGHT_GRAY);
        /*
        patternColors.put(Pattern.compile(TAG),  Color.GREEN.darker());
        patternColors.put(Pattern.compile(LEMMA),  Color.BLUE);
        patternColors.put(Pattern.compile(COMMENT), Color.LIGHT_GRAY);
         */
    }


    @Override
    protected int drawUnselectedText(Graphics graphics, int x, int y, int p0,  int p1) throws BadLocationException {

        Document doc = getDocument();
        String text = doc.getText(p0, p1 - p0);

        Segment segment = getLineBuffer();

        SortedMap<Integer, Integer> startMap = new TreeMap<Integer, Integer>();
        SortedMap<Integer, Color> colorMap = new TreeMap<Integer, Color>();

        // Match all regexes on this snippet, store positions
        for (Map.Entry<Pattern, Color> entry : patternColors.entrySet()) {

            Matcher matcher = entry.getKey().matcher(text);

            while (matcher.find()) {
                startMap.put(matcher.start(1), matcher.end(1));
                colorMap.put(matcher.start(1), entry.getValue());
            }
        }

        // TODO: check the map for overlapping parts
        
        int i = 0;

        // Colour the parts
        for (Map.Entry<Integer, Integer> entry : startMap.entrySet()) {
            int start = entry.getKey();
            int end = entry.getValue();

            if (i < start) {
                graphics.setColor(Color.black);
                doc.getText(p0 + i, start - i, segment);
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
            }

            graphics.setColor(colorMap.get(start));
            i = end;
            doc.getText(p0 + start, i - start, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
        }

        // Paint possible remaining text black
        if (i < text.length()) {
            graphics.setColor(Color.black);
            doc.getText(p0 + i, text.length() - i, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
        }

        return x;
    }
}