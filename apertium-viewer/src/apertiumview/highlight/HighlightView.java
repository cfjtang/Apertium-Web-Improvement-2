/*
 * Copyright 2015 Jacob Nordfalk
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
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
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;
import javax.swing.text.WrappedPlainView;

/**
 * Thanks: http://www.boplicity.net/confluence/display/Java/Xml+syntax+highlighting+in+Swing+JTextPane
 * 
 * Using WrappedPlainView here because we DO want line wrapping to occur.
 * 
 * @author Jacob Nordfalk
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

    // http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html
    private static String TAG = "(<[\\p{L}\\d\\.\\s]+>)";
    private static String W = "[\\p{L}\\d\\._ ]+";
    private static String ANALYSIS_UNKNOWN_WORD = "(\\^"+W+"/)\\*"+W+"+\\$";
    private static String ANALYSIS_AMBIGIOUS_WORD = "(\\^"+W+"/)[^\\$]+(?:/[^\\$]+)+\\$";
    private static String ANALYSIS_UNAMBIGIOUS_WORD = "(\\^"+W+"/)[^/\\*\\$]+\\$";
    private static String LEMMA = "\\^("+W+")(<[\\p{L}\\d\\.\\s]+>)*\\$";
    private static String UNKNOWN_LEMMA = "\\^(\\*"+W+")\\$";
    private static String CHUNK = "\\^("+W+")(<[\\p{L}\\d\\.\\s]+>)*\\{";



    static {
        patternColors = new HashMap<Pattern, Color>();
        patternColors.put(Pattern.compile(TAG),  Color.decode("#aaaaaa"));
        patternColors.put(Pattern.compile(ANALYSIS_UNKNOWN_WORD),  Color.RED);
        patternColors.put(Pattern.compile(ANALYSIS_AMBIGIOUS_WORD),  Color.RED);//Color.ORANGE.darker());
        patternColors.put(Pattern.compile(ANALYSIS_UNAMBIGIOUS_WORD),  Color.BLUE);
        patternColors.put(Pattern.compile(LEMMA),  Color.BLUE);
        patternColors.put(Pattern.compile(UNKNOWN_LEMMA),  Color.RED.darker());
        patternColors.put(Pattern.compile(CHUNK),  Color.CYAN.darker());
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