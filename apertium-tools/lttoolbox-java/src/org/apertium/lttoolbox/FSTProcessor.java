package org.apertium.lttoolbox;/*
 * Copyright (C) 2005 Universitat d'Alacant / Universidad de Alicante
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


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.Collator;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class FSTProcessor {

    public enum GenerationMode {

        gm_clean, // clear all
        gm_unknown, // display unknown words, clear transfer and generation tags
        gm_all         // display all
    }
    Collator myCollator = Collator.getInstance();
    /**
     * Transducers in FSTP
     */
    Map<String, TransExe> transducers = new TreeMap<String, TransExe>(myCollator);
    /**
     * Current state of lexical analysis
     */
    State current_state;
    /**
     * Initial state of every token
     */
    State initial_state;
    /**
     * Set of final states of incoditional sections in the dictionaries
     */
    Set<Node> inconditional;
    /**
     * Set of final states of standard sections in the dictionaries
     */
    Set<Node> standard;
    /**
     * Set of final states of postblank sections in the dictionaries
     */
    Set<Node> postblank;
    /**
     * Set of final states of preblank sections in the dictionaries
     */
    Set<Node> preblank;
    /**
     * Merge of 'inconditional', 'standard', 'postblank' and 'preblank' sets
     */
    Set<Node> all_finals;
    /**
     * Queue of blanks, used in reading methods
     */
    Queue<String> blankqueue;
    /**
     * Set of characters being considered alphabetics
     */
    Set<Character> alphabetic_chars = new HashSet<Character>();
    /**
     * Set of characters to escape with a backslash
     */
    Set<Character> escaped_chars = new HashSet<Character>();
    /**
     * Alphabet
     */
    Alphabet alphabet = new Alphabet();
    /**
     * Input buffer
     */
    Buffer input_buffer;
    /**
     * Begin of the transducer
     */
    Node root;
    /**
     * true if the position of input stream is out of a word
     */
    boolean outOfWord;
    /**
     * if true, makes always difference between uppercase and lowercase
     * characters
     */
    boolean caseSensitive;
    /**
     * if true, flush the output when the null character is found
     */
    boolean nullFlush;

    public FSTProcessor() {
        // escaped_chars chars
        escaped_chars.add('[');
        escaped_chars.add(']');
        escaped_chars.add('^');
        escaped_chars.add('$');
        escaped_chars.add('/');
        escaped_chars.add('\\');
        escaped_chars.add('@');
        escaped_chars.add('<');
        escaped_chars.add('>');
        caseSensitive = false;
        nullFlush = false;
    }

    void streamError() {
        throw new RuntimeException("Error: Malformed input stream.");
    }

    Character readEscaped(FileInputStream input) throws IOException {
        
        if (input.available() == 0) {
            streamError();
        }

        Character val = (char)  input.read();

        if (input.available() == 0 || (!escaped_chars.contains(val))) {
            streamError();
        }

        return val;
    }

    String readFullBlock(FileInputStream input, Character delim1, Character delim2) throws IOException {
        String result = "";
        result += delim1;
        Character c = delim1;

        while (input.available() != 0 && c != delim2) {
            c=(char)input.read();
            result += c;
            if (c != '\\') {
                continue;
            } else {
                result += readEscaped(input);
            }
        }

        if (c != delim2) {
            streamError();
        }

        return result;
    }

    char readAnalysis(FileInputStream input) throws IOException {
        if (!input_buffer.isEmpty()) {
            return input_buffer.next();
        }

        Character val = (char) input.read();
        char altval = (char) 0;
        if (input.available() == 0) {
            return (char) 0;
        }

        if (escaped_chars.contains(val)) {
            switch (val) {
                case '<':
                    altval = (char) (Alphabet.A.cast(readFullBlock(input, '<', '>')));
                    input_buffer.add(altval);
                    return altval;

                case '[':
                    blankqueue.add(readFullBlock(input, '[', ']'));
                    input_buffer.add((' '));
                    return (' ');

                case '\\':
                    val = (char) (input.read());
                    if (!escaped_chars.contains(val)) {
                        streamError();
                    }
                    input_buffer.add((val));
                    return val;

                default:
                    streamError();
            }
        }

        input_buffer.add(val);
        return val;
    }

    char readPostgeneration(FileInputStream input) throws IOException {
        if (!input_buffer.isEmpty()) {
            return input_buffer.next();
        }

        Character val = (char) (input.read());
        char altval = (char)0;
        if (input.available() == 0) {
            return (char)0;
        }

        switch (val) {
            case '<':
                altval = (char) (Alphabet.A.cast(readFullBlock(input, '<', '>')));
                input_buffer.add(altval);
                return altval;

            case '[':
                blankqueue.add(readFullBlock(input, '[', ']'));
                input_buffer.add((' '));
                return  (' ');

            case '\\':
                val = (char) (input.read());
                if (!escaped_chars.contains(val)) {
                    streamError();
                }
                input_buffer.add((val));
                return val;

            default:
                input_buffer.add(val);
                return val;
        }
    }

    void skipUntil(FileInputStream input, Writer output, char character) throws IOException {
        while (true) {
            char val = (char) input.read();
            if (input.available() == 0) {
                return;
            }

            if (val == '\\') {
                val = (char) input.read();
                if (input.available() == 0) {
                    return;
                }
                output.write('\\');
                output.write(val);
            } else if (val == character) {
                return;
            } else {
                output.write(val);
            }
        }
    }

    int readGeneration(FileInputStream input, Writer output) throws IOException {
        char val = (char) input.read();

        if (input.available() == 0) {
            return 0x7fffffff;
        }

        if (outOfWord) {
            if (val == '^') {
                val = (char) input.read();
                if (input.available() == 0) {
                    return 0x7fffffff;
                }
            } else if (val == '\\') {
                output.write(val);
                val = (char) input.read();
                if (input.available() == 0) {
                    return 0x7fffffff;
                }
                output.write(val);
                skipUntil(input, output, '^');
                val = (char) input.read();
                if (input.available() == 0) {
                    return 0x7fffffff;
                }
            } else {
                output.write(val);
                skipUntil(input, output, '^');
                val = (char) input.read();
                if (input.available() == 0) {
                    return 0x7fffffff;
                }
            }
            outOfWord = false;
        }

        if (val == '\\') {
            val = (char) input.read();
            return (int) (val);
        } else if (val == '$') {
            outOfWord = true;
            return (int) ('$');
        } else if (val == '<') {
            String cad = "";
            cad += val;
            
            while ((val = (char) input.read()) != '>') {
                if (input.available() == 0) {
                    streamError();
                }
                cad += val;
            }
            cad += val;
            return Alphabet.A.cast(cad);
        } else if (val == '[') {
            output.write(readFullBlock(input, '[', ']'));
            return readGeneration(input, output);
        } else {
            return (int) (val);
        }

    // return 0x7fffffff;
    }

    void flushBlanks(Writer output) throws IOException {
        for (int i = blankqueue.size(); i > 0; i--) {
            output.write(blankqueue.peek());
            blankqueue.poll();
        }
    }

    void calcInitial() {
        for (String first : transducers.keySet()) {
            final TransExe second = transducers.get(first);
            root.addTransition(0, 0, second.getInitial());
        }
        initial_state.init(root);
    }

    boolean endsWith(String str, String suffix) {
        if (str.length() < suffix.length()) {
            return false;
        } else {
            return str.substring(str.length() - suffix.length()).equals(suffix);
        }
    }

    void classifyFinals() {
        for (String first : transducers.keySet()) {
            final TransExe second = transducers.get(first);
            if (endsWith(first, "@inconditional")) {
                inconditional.addAll(second.getFinals());
            } else if (endsWith(first, "@standard")) {
                standard.addAll(second.getFinals());
            } else if (endsWith(first, "@postblank")) {
                postblank.addAll(second.getFinals());
            } else if (endsWith(first, "@preblank")) {
                preblank.addAll(second.getFinals());
            } else {
                throw new RuntimeException("Error: Unsupported transducer type for '" + first + "'.");
            }
        }
    }

    void writeEscaped(String str, Writer output) throws IOException {
        for (int i = 0,  limit = str.length(); i < limit; i++) {
            if (escaped_chars.contains(str.charAt(i))) {
                output.write('\\');
            }
            output.write(str.charAt(i));
        }
    }

    void printWord(String sf, String lf, Writer output) throws IOException {
        output.write('^');
        writeEscaped(sf, output);
        output.write(lf);
        output.write('$');
    }

    void printUnknownWord(String sf, Writer output) throws IOException {
        output.write('^');
        writeEscaped(sf, output);
        output.write('/');
        output.write('*');
        writeEscaped(sf, output);
        output.write('$');
    }

    int lastBlank(String str) {
        for (int i = str.length() - 1; i >= 0; i--) {
            if (!alphabetic_chars.contains(str.charAt(i))) {
                return (i);
            }
        }
        return 0;
    }

    void printSpace(Character val, Writer output) throws IOException {
        if (blankqueue.size() > 0) {
            flushBlanks(output);
        } else {
            output.write(val);
        }
    }

    boolean isEscaped(Character c) {
        return escaped_chars.contains(c);
    }

    boolean isAlphabetic(Character c) {
        return alphabetic_chars.contains(c);
    }

    void load( FileInputStream input) throws IOException {
        // letters
    /* old  version    
         * int len = input.read();
         * while (len > 0) {
         * alphabetic_chars.add((char) (input.read()));
         * len--;
         */
        int len = Compression.multibyte_read(input);
        while (len > 0) {
            alphabetic_chars.add((char) Compression.multibyte_read(input));
            len--;
        }

        // symbols
       alphabet= alphabet.read(input);

       
        len = Compression.multibyte_read(input);
        
        Writer output = new OutputStreamWriter(System.out);
        
        output.write("longueur "+len);
            
        while (len > 0) {
            int len2 = Compression.multibyte_read(input);
            String name = "";
            while (len2 > 0) {
                output.write("longueur2 "+len2);
                char temp = (char) (Compression.multibyte_read(input));
                name += temp;
                len2--;
                output.write(temp);
            }
            output.close();
            transducers.get(name).read(input, alphabet);
            len--;
            System.out.println(len);
        }

    }

    void initAnalysis() {
        calcInitial();
        classifyFinals();
        all_finals = standard;
        all_finals.addAll(inconditional);
        all_finals.addAll(postblank);
        all_finals.addAll(preblank);
    }

    void initGeneration() {
        calcInitial();
        for (String first : transducers.keySet()) {
            final TransExe second = transducers.get(first);
            all_finals.addAll(second.getFinals());
        }
    }

    void initPostgeneration() {
        initGeneration();
    }

    void initBiltrans() {
        initGeneration();
    }

    void analysis(FileInputStream input, Writer output) throws IOException {
        if (getNullFlush()) {
            analysis_wrapper_null_flush(input, output);
        }

        boolean last_incond = false;
        boolean last_postblank = false;
        boolean last_preblank = false;
        State current_state = initial_state;
        String lf = "";
        String sf = "";
        int last = 0;

        Character val;
        while ((val = readAnalysis(input)) != null) {
            // test for final states
            if (current_state.isFinal(all_finals)) {
                if (current_state.isFinal(inconditional)) {
                    boolean firstupper = Character.isUpperCase(sf.charAt(0));
                    boolean uppercase = firstupper && Character.isUpperCase(sf.charAt(sf.length() - 1));

                    lf = current_state.filterFinals(all_finals, alphabet,
                            escaped_chars,
                            uppercase, firstupper);
                    last_incond = true;
                    last = input_buffer.getPos();
                } else if (current_state.isFinal(postblank)) {
                    boolean firstupper = Character.isUpperCase(sf.charAt(0));
                    boolean uppercase = firstupper && Character.isUpperCase(sf.charAt(sf.length() - 1));

                    lf = current_state.filterFinals(all_finals, alphabet,
                            escaped_chars,
                            uppercase, firstupper);
                    last_postblank = true;
                    last = input_buffer.getPos();
                } else if (current_state.isFinal(preblank)) {
                    boolean firstupper = Character.isUpperCase(sf.charAt(0));
                    boolean uppercase = firstupper && Character.isUpperCase(sf.charAt(sf.length() - 1));

                    lf = current_state.filterFinals(all_finals, alphabet,
                            escaped_chars,
                            uppercase, firstupper);
                    last_preblank = true;
                    last = input_buffer.getPos();
                } else if (!isAlphabetic(val)) {
                    boolean firstupper = Character.isUpperCase(sf.charAt(0));
                    boolean uppercase = firstupper && Character.isUpperCase(sf.charAt(sf.length() - 1));

                    lf = current_state.filterFinals(all_finals, alphabet,
                            escaped_chars,
                            uppercase, firstupper);
                    last_postblank = false;
                    last_preblank = false;
                    last_incond = false;
                    last = input_buffer.getPos();
                }
            } else if (sf.equals("") && Character.isSpaceChar(val)) {
                lf = "/*" + sf;
                last_postblank = false;
                last_preblank = false;
                last_incond = false;
                last = input_buffer.getPos();
            }

            if (!Character.isUpperCase(val) || caseSensitive) {
                current_state.step(val);
            } else {
                current_state.step(val, Character.toLowerCase(val));
            }

            if (current_state.size() != 0) {
                alphabet.getSymbol(sf, val);
            } else {
                if (!isAlphabetic(val) && sf.equals("")) {
                    if (Character.isSpaceChar(val)) {
                        printSpace(val, output);
                    } else {
                        if (isEscaped(val)) {
                            output.write('\\');
                        }
                        output.write(val);
                    }
                } else if (last_incond) {
                    printWord(sf.substring(0, sf.length() - input_buffer.diffPrevPos(last)),
                            lf, output);
                    input_buffer.setPos(last);
                    input_buffer.back(1);
                } else if (last_postblank) {
                    printWord(sf.substring(0, sf.length() - input_buffer.diffPrevPos(last)),
                            lf, output);
                    output.write(' ');
                    input_buffer.setPos(last);
                    input_buffer.back(1);
                } else if (last_preblank) {
                    output.write(' ');
                    printWord(sf.substring(0, sf.length() - input_buffer.diffPrevPos(last)),
                            lf, output);
                    input_buffer.setPos(last);
                    input_buffer.back(1);
                } else if (isAlphabetic(val) &&
                        ((sf.length() - input_buffer.diffPrevPos(last)) > lastBlank(sf) ||
                        lf.equals(""))) {
                    do {
                        alphabet.getSymbol(sf, val);
                    } while ((val = readAnalysis(input)) != null && isAlphabetic(val));

                    int limit = firstNotAlpha(sf);
                    int size = sf.length();
                    limit = (limit == Integer.MAX_VALUE ? size : limit);
                    if (limit == 0) {
                        input_buffer.back(sf.length());
                        output.write(sf.charAt(0));
                    } else {
                        input_buffer.back(1 + (size - limit));
                        printUnknownWord(sf.substring(0, limit), output);
                    }
                } else if (lf.equals("")) {
                    int limit = firstNotAlpha(sf);
                    int size = sf.length();
                    limit = (limit == Integer.MAX_VALUE ? size : limit);
                    if (limit == 0) {
                        input_buffer.back(sf.length());
                        output.write(sf.charAt(0));
                    } else {
                        input_buffer.back(1 + (size - limit));
                        printUnknownWord(sf.substring(0, limit), output);
                    }
                } else {
                    printWord(sf.substring(0, sf.length() - input_buffer.diffPrevPos(last)),
                            lf, output);
                    input_buffer.setPos(last);
                    input_buffer.back(1);
                }

                current_state = initial_state;
                lf = "";
                sf = "";
                last_incond = false;
                last_postblank = false;
                last_preblank = false;
            }
        }

        // print remaining blanks
        flushBlanks(output);
    }

    void analysis_wrapper_null_flush(FileInputStream input, Writer output) throws IOException {
        setNullFlush(false);
        while (input.available() >0) {
            analysis(input, output);
            output.write('\0');
            output.flush();
        }
    }

    void generation_wrapper_null_flush(FileInputStream input, Writer output,
            GenerationMode mode) throws IOException {
        setNullFlush(false);
        while (input.available() >0) {
            generation(input, output, mode);
            output.write('\0');
            output.flush();
        }
    }

    void postgeneration_wrapper_null_flush(FileInputStream input, Writer output) throws IOException {
        setNullFlush(false);
        while (input.available() >0) {
            postgeneration(input, output);
            output.write('\0');
            output.flush();
        }
    }

    void transliteration_wrapper_null_flush(FileInputStream input, Writer output) throws IOException {
        setNullFlush(false);
        while (input.available() >0) {
            transliteration(input, output);
            output.write('\0');
            output.flush();
        }
    }

    void generation(FileInputStream input, Writer output, GenerationMode mode) throws IOException {
        if (getNullFlush()) {
            generation_wrapper_null_flush(input, output, mode);
        }

        State current_state = initial_state;
        String sf = "";

        outOfWord = false;

        skipUntil(input, output, '^');
        int val;
        while ((val = readGeneration(input, output)) != 0x7fffffff) {
            if (sf.equals("") && val == '=') {
                output.write('=');
                val = readGeneration(input, output);
            }
            if (val == '$' && outOfWord) {
                if (sf.charAt(0) == '*' || sf.charAt(0) == '%') {
                    if (mode != GenerationMode.gm_clean) {
                        writeEscaped(sf, output);
                    } else {
                        writeEscaped(sf.substring(1), output);
                    }
                } else if (sf.charAt(0) == '@') {
                    if (mode == GenerationMode.gm_all) {
                        writeEscaped(sf, output);
                    } else if (mode == GenerationMode.gm_clean) {
                        writeEscaped(removeTags(sf.substring(1)), output);
                    } else if (mode == GenerationMode.gm_unknown) {
                        writeEscaped(removeTags(sf), output);
                    }
                } else if (current_state.isFinal(all_finals)) {
                    boolean uppercase = sf.length() > 1 && Character.isUpperCase(sf.charAt(1));
                    boolean firstupper = Character.isUpperCase(sf.charAt(0));

                    output.write(current_state.filterFinals(all_finals, alphabet,
                            escaped_chars,
                            uppercase, firstupper).substring(1));
                } else {
                    if (mode == GenerationMode.gm_all) {
                        output.write('#');
                        writeEscaped(sf, output);
                    } else if (mode == GenerationMode.gm_clean) {
                        writeEscaped(removeTags(sf), output);
                    } else if (mode == GenerationMode.gm_unknown) {
                        output.write('#');
                        writeEscaped(removeTags(sf), output);
                    }
                }

                current_state = initial_state;
                sf = "";
            } else if (Character.isSpaceChar((char) val) && sf.length() == 0) {
            // do nothing
            } else if (sf.length() > 0 && (sf.charAt(0) == '*' || sf.charAt(0) == '%')) {
                alphabet.getSymbol(sf, val);
            } else {
                alphabet.getSymbol(sf, val);
                if (current_state.size() > 0) {
                    if (!alphabet.isTag(val) && Character.isUpperCase(val) && !caseSensitive) {
                        current_state.step(val, Character.toLowerCase(val));
                    } else {
                        current_state.step(val);
                    }
                }
            }
        }
    }

    void postgeneration(FileInputStream input, Writer output) throws IOException {
        if (getNullFlush()) {
            postgeneration_wrapper_null_flush(input, output);
        }

        boolean skip_mode = true;
        State current_state = initial_state;
        StringBuffer lf = new StringBuffer("");
        String sf = "";
        int last = 0;

        Character val;
        while ((val = readPostgeneration(input)) != null) {

            if (val == '~') {
                skip_mode = false;
            }

            if (skip_mode) {
                if (Character.isSpaceChar(val)) {
                    printSpace(val, output);
                } else {
                    if (isEscaped(val)) {
                        output.write('\\');
                    }
                    output.write(val);
                }
            } else {
                // test for final states
                if (current_state.isFinal(all_finals)) {
                    boolean firstupper = Character.isUpperCase(sf.charAt(1));
                    boolean uppercase = sf.length() > 1 && firstupper && Character.isUpperCase(sf.charAt(2));
                    lf = new StringBuffer(current_state.filterFinals(all_finals, alphabet,
                            escaped_chars,
                            uppercase, firstupper, 0));

                    // case of the beggining of the next word

                    String mybuf = "";
                    for (int i = sf.length() - 1; i >= 0; i--) {
                        if (!Character.isLetter(sf.charAt(i))) {
                            break;
                        } else {
                            mybuf = sf.charAt(i) + mybuf;
                        }
                    }

                    if (mybuf.length() > 0) {
                        boolean myfirstupper = Character.isUpperCase(mybuf.charAt(0));
                        boolean myuppercase = mybuf.length() > 1 && Character.isUpperCase(mybuf.charAt(1));

                        for (int i = lf.length() - 1; i >= 0; i--) {
                            if (!Character.isLetter(lf.charAt(i))) {
                                if (myfirstupper && i != lf.length() - 1) {
                                    lf.setCharAt(i + 1, Character.toLowerCase(lf.charAt(i + 1)));
                                } else {
                                    lf.setCharAt(i + 1, Character.toLowerCase(lf.charAt(i + 1)));
                                }
                                break;
                            } else {
                                if (myuppercase) {
                                    lf.setCharAt(i, Character.toLowerCase(lf.charAt(i)));
                                } else {
                                    lf.setCharAt(i, Character.toLowerCase(lf.charAt(i)));
                                }
                            }
                        }
                    }

                    last = input_buffer.getPos();
                }

                if (!Character.isUpperCase(val) || caseSensitive) {
                    current_state.step(val);
                } else {
                    current_state.step(val, Character.toLowerCase(val));
                }

                if (current_state.size() != 0) {
                    alphabet.getSymbol(sf, val);
                } else {
                    if (lf.length() == 0) {
                        int mark = sf.length();
                        for (int i = 1,  limit = sf.length(); i < limit; i++) {
                            if (sf.charAt(i) == '~') {
                                mark = i;
                                break;
                            }
                        }
                        output.write(sf.substring(1, mark - 1));
                        if (mark == sf.length()) {
                            input_buffer.back(1);
                        } else {
                            input_buffer.back(sf.length() - mark);
                        }
                    } else {
                        output.write(lf.substring(1, lf.length() - 3));
                        input_buffer.setPos(last);
                        input_buffer.back(2);
                        val = lf.charAt(lf.length() - 2);
                        if (Character.isSpaceChar(val)) {
                            printSpace(val, output);
                        } else {
                            if (isEscaped(val)) {
                                output.write('\\');
                            }
                            output.write(val);
                        }
                    }

                    current_state = initial_state;
                    lf.setLength(0);
                    sf = "";
                    skip_mode = true;
                }
            }
        }

        // print remaining blanks
        flushBlanks(output);
    }

    void transliteration(FileInputStream input, Writer output) throws IOException {
        if (getNullFlush()) {
            transliteration_wrapper_null_flush(input, output);
        }

        State current_state = initial_state;
        String lf = "";
        String sf = "";
        int last = 0;

        Character val;
        while ((val = readPostgeneration(input)) != null) {
            if (iswpunct(val) || Character.isSpaceChar(val)) {
                boolean firstupper = Character.isUpperCase(sf.charAt(1));
                boolean uppercase = sf.length() > 1 && firstupper && Character.isUpperCase(sf.charAt(2));
                lf = current_state.filterFinals(all_finals, alphabet, escaped_chars,
                        uppercase, firstupper, 0);
                if (lf.length() > 0) {
                    output.write(lf.substring(1));
                    current_state = initial_state;
                    lf = "";
                    sf = "";
                }
                if (Character.isSpaceChar(val)) {
                    printSpace(val, output);
                } else {
                    if (isEscaped(val)) {
                        output.write('\\');
                    }
                    output.write(val);
                }
            } else {
                if (current_state.isFinal(all_finals)) {
                    boolean firstupper = Character.isUpperCase(sf.charAt(1));
                    boolean uppercase = sf.length() > 1 && firstupper && Character.isUpperCase(sf.charAt(2));
                    lf = current_state.filterFinals(all_finals, alphabet, escaped_chars,
                            uppercase, firstupper, 0);
                    last = input_buffer.getPos();
                }

                current_state.step(val);
                if (current_state.size() != 0) {
                    alphabet.getSymbol(sf, val);
                } else {
                    if (lf.length() > 0) {
                        output.write(lf.substring(1));
                        input_buffer.setPos(last);
                        input_buffer.back(1);
                        val = lf.charAt(lf.length() - 1);
                    } else {
                        if (Character.isSpaceChar(val)) {
                            printSpace(val, output);
                        } else {
                            if (isEscaped(val)) {
                                output.write('\\');
                            }
                            output.write(val);
                        }
                    }
                    current_state = initial_state;
                    lf = "";
                    sf = "";
                }
            }
        }
        // print remaining blanks
        flushBlanks(output);
    }

    private boolean iswpunct(Character val) {
        return val.toString().matches("\\{pPunctuation}");
    }

    String biltrans(String input_word, boolean with_delim) {
        State current_state = initial_state;
        StringBuffer result = new StringBuffer("");
        int start_point = 1;
        int end_point = input_word.length() - 2;
        StringBuffer queue = new StringBuffer("");
        boolean mark=false;
        
        if (!with_delim) {
            start_point = 0;
            end_point = input_word.length() - 1;
        }

        if (input_word.charAt(start_point) == '*') {
            return input_word;
        }
        
        if(input_word.charAt(start_point) == '=') {
            start_point++;
            mark = true;
        }
        boolean firstupper = Character.isUpperCase(input_word.charAt(start_point));
        boolean uppercase = firstupper && Character.isUpperCase(input_word.charAt(start_point + 1));

        for (int i = start_point; i <= end_point; i++) {
            int val;
            String symbol = "";

            if (input_word.charAt(i) == '\\') {
                i++;
                val = (int) (input_word.charAt(i));
            } else if (input_word.charAt(i) == '<') {
                symbol = "<";
                for (int j = i + 1; j <= end_point; j++) {
                    symbol += input_word.charAt(j);
                    if (input_word.charAt(j) == '>') {
                        i = j;
                        break;
                    }
                }
                val = Alphabet.A.cast(symbol);
            } else {
                val = (int) (input_word.charAt(i));
            }
            if (current_state.size() != 0) {
                if (!alphabet.isTag(val) && Character.isUpperCase(val) && !caseSensitive) {
                    current_state.step(val, Character.toLowerCase(val));
                } else {
                    current_state.step(val);
                }
            }
            if (current_state.isFinal(all_finals)) {
                result = new StringBuffer(current_state.filterFinals(all_finals, alphabet,
                        escaped_chars,
                        uppercase, firstupper, 0));
                if (with_delim) {
                    if(mark) {
                        result = new StringBuffer("^="+result.substring(1));
                    } else {
                        result.setCharAt(0, '^');
                    }
                } else {
                    if(mark) {
                        result = new StringBuffer("="+result.substring(1));
                    } else {
                        result = new StringBuffer(result.substring(1));
                    }
                }
            }

            if (current_state.size() == 0) {
                if (!symbol.equals("") && !result.equals( "")){
                    queue.append(symbol);
                } else {
                    // word is not present
                    String res;
                    if (with_delim) {
                        res = "^@" + input_word.substring(1);
                    } else {
                        res = "@" + input_word;
                    }
                    return res;
                }
            }
        }

        // attach unmatched queue automatically

        if (queue.length() != 0) {
            StringBuffer result_with_queue = new StringBuffer("");
            boolean multiple_translation = false;
            for (int i = 0,  limit = result.length(); i != limit; i++) {
                switch (result.charAt(i)) {
                    case '\\':
                        result_with_queue.append('\\');
                        i++;
                        break;

                    case '/':
                        result_with_queue.append(queue);
                        multiple_translation = true;
                        break;

                    default:
                        break;
                }
                result_with_queue.append(result.charAt(i));
            }
            result_with_queue.append(queue);

            if (with_delim) {
                result_with_queue.append('$');
            }
            return result_with_queue.toString();
        } else {
            if (with_delim) {
                result.append('$');
            }
            return result.toString();
        }
    }

    Pair<String, Integer> biltransWithQueue(String input_word, boolean with_delim) {
        State current_state = initial_state;
        StringBuffer result = new StringBuffer("");
        int start_point = 1;
        int end_point = input_word.length() - 2;
        StringBuffer queue = new StringBuffer("");
        boolean mark=false;
        
        if (!with_delim) {
            start_point = 0;
            end_point = input_word.length() - 1;
        }

        if (input_word.charAt(start_point) == '*') {
            return new Pair<String, Integer>(input_word, 0);
        }

        if (input_word.charAt(start_point) == '=') {
            start_point++;
            mark = true;
        }

        boolean firstupper = Character.isUpperCase(input_word.charAt(start_point));
        boolean uppercase = firstupper && Character.isUpperCase(input_word.charAt(start_point + 1));

        for (int i = start_point; i <= end_point; i++) {
            int val;
            String symbol = "";

            if (input_word.charAt(i) == '\\') {
                i++;
                val = input_word.charAt(i);
            } else if (input_word.charAt(i) == '<') {
                symbol = "<";
                for (int j = i + 1; j <= end_point; j++) {
                    symbol += input_word.charAt(j);
                    if (input_word.charAt(j) == '>') {
                        i = j;
                        break;
                    }
                }
                val = Alphabet.A.cast(symbol);
            } else {
                val = input_word.charAt(i);
            }
            if (current_state.size() != 0) {
                if (!alphabet.isTag(val) && Character.isUpperCase(val) && !caseSensitive) {
                    current_state.step(val, Character.toLowerCase(val));
                } else {
                    current_state.step(val);
                }
            }
            if (current_state.isFinal(all_finals)) {
                result = new StringBuffer(current_state.filterFinals(all_finals, alphabet,
                        escaped_chars,
                        uppercase, firstupper, 0));
                if (with_delim) {
                    if (mark) {
                        result = new StringBuffer("^=" + result.substring(1));
                    } else {
                        result.setCharAt(0, '^');
                    }
                } else {
                    if (mark) {
                        result = new StringBuffer("=" + result.substring(1));
                    } else {
                    result = new StringBuffer(result.substring(1));
                    }
                }
            }

            if (current_state.size() == 0) {
                if (!symbol.equals("") && !result.equals("")) {
                    queue.append(symbol);
                } else {
                    // word is not present
                    String res;
                    if (with_delim) {
                        res = "^@" + input_word.substring(1);
                    } else {
                        res = "@" + input_word;
                    }
                    return new Pair<String, Integer>(res, 0);
                }
            }
        }

        // attach unmatched queue automatically

        if (queue.length() > 0) {
            StringBuffer result_with_queue = new StringBuffer("");
            boolean multiple_translation = false;
            for (int i = 0,  limit = result.length(); i != limit; i++) {
                switch (result.charAt(i)) {
                    case '\\':
                        result_with_queue.append('\\');
                        i++;
                        break;

                    case '/':
                        result_with_queue.append(queue);
                        multiple_translation = true;
                        break;

                    default:
                        break;
                }
                result_with_queue.append(result.charAt(i));
            }
            result_with_queue.append(queue);

            if (with_delim) {
                result_with_queue.append('$');
            }
            return new Pair<String, Integer>(result_with_queue.toString(), queue.length());
        } else {
            if (with_delim) {
                result.append('$');
            }
            return new Pair<String, Integer>(result.toString(), 0);
        }
    }

    String biltransWithoutQueue(String input_word, boolean with_delim) {
        State current_state = initial_state;
        StringBuffer result = new StringBuffer("");
        int start_point = 1;
        int end_point = input_word.length() - 2;
        boolean mark = false;
        
        if (!with_delim) {
            start_point = 0;
            end_point = input_word.length() - 1;
        }

        if (input_word.charAt(start_point) == '*') {
            return input_word;
        }

        if(input_word.charAt(start_point) == '=')  {
            start_point++;
            mark = true;
        }
        
        boolean firstupper = Character.isUpperCase(input_word.charAt(start_point));
        boolean uppercase = firstupper && Character.isUpperCase(input_word.charAt(start_point + 1));

        for (int i = start_point; i <= end_point; i++) {
            int val;
            String symbol = "";

            if (input_word.charAt(i) == '\\') {
                i++;
                val = (int) (input_word.charAt(i));
            } else if (input_word.charAt(i) == '<') {
                symbol = "<";
                for (int j = i + 1; j <= end_point; j++) {
                    symbol += input_word.charAt(j);
                    if (input_word.charAt(j) == '>') {
                        i = j;
                        break;
                    }
                }
                val = Alphabet.A.cast(symbol);
            } else {
                val = (int) (input_word.charAt(i));
            }
            if (current_state.size() != 0) {
                if (!alphabet.isTag(val) && Character.isUpperCase(val) && !caseSensitive) {
                    current_state.step(val, Character.toLowerCase(val));
                } else {
                    current_state.step(val);
                }
            }
            if (current_state.isFinal(all_finals)) {
                result = new StringBuffer(current_state.filterFinals(all_finals, alphabet,
                        escaped_chars,
                        uppercase, firstupper, 0));
                if (with_delim) {
                    if (mark) {
                        result = new StringBuffer("^=" + result.substring(1));
                    } else {
                        result.setCharAt(0, '^');
                    }
                } else {
                    if (mark) {
                        result = new StringBuffer("=" + result.substring(1));
                    } else {
                        result = new StringBuffer(result.substring(1));
                    }
                }
            }

            if (current_state.size() == 0) {
                if (symbol.equals("")) {
                    // word is not present
                    String res;
                    if (with_delim) {
                        res = "^@" + input_word.substring(1);
                    } else {
                        res = "@" + input_word;
                    }
                    return res;
                }
            }
        }

        if (with_delim) {
            result.append('$');
        }
        return result.toString();
    }

    boolean valid() {
        if (initial_state.isFinal(all_finals)) {
            throw new RuntimeException("Error: Invalid dictionary (hint: the left side of an entry is empty)");

        } else {
            State s = initial_state;
            s.step(' ');
            if (s.size() != 0) {
                throw new RuntimeException("Error: Invalid dictionary (hint: entry beginning with whitespace)");

            }
        }

        return true;
    }

    char readSAO(FileInputStream input) throws IOException {
        if (!input_buffer.isEmpty()) {
            return input_buffer.next();
        }

        Character val = (char) (input.read());
        if (input.available() == 0) {
            return 0;
        }

        if (escaped_chars.contains(val)) {
            if (val == '<') {
                String str = readFullBlock(input, '<', '>');
                if (str.substring(0, 9).equals("<![CDATA[")) {
                    while (!"]]>".equals(str.substring(str.length() - 3))) {
                        str += readFullBlock(input, '<', '>').substring(1);
                    }
                    blankqueue.add(str);
                    input_buffer.add((' '));
                    return (int) (' ');
                } else {
                    streamError();
                }
            } else if (val == '\\') {
                val = (char) (input.read());
                if (isEscaped(val)) {
                    input_buffer.add(val);
                    return (val);
                } else {
                    streamError();
                }
            } else {
                streamError();
            }
        }

        input_buffer.add(val);
        return (val);
    }

    void printSAOWord(String lf, Writer output) throws IOException {
        for (int i = 1,  limit = lf.length(); i != limit; i++) {
            if (lf.charAt(i) == '/') {
                break;
            }
            output.write(lf.charAt(i));
        }
    }

    void SAO(FileInputStream input, Writer output) throws IOException {
        boolean last_incond = false;
        boolean last_postblank = false;
        State current_state = initial_state;
        String lf = "";
        String sf = "";
        int last = 0;

        escaped_chars.clear();
        escaped_chars.add('\\');
        escaped_chars.add(('<'));
        escaped_chars.add(('>'));

        Character val;
        while ((val = readSAO(input)) != null) {
            // test for final states
            if (current_state.isFinal(all_finals)) {
                if (current_state.isFinal(inconditional)) {
                    boolean firstupper = Character.isUpperCase(sf.charAt(0));
                    boolean uppercase = firstupper && Character.isUpperCase(sf.charAt(sf.length() - 1));

                    lf = current_state.filterFinalsSAO(all_finals, alphabet,
                            escaped_chars,
                            uppercase, firstupper, 0);
                    //same call in the C++ code, but without the last argument 0.   ?????
                    
                    last_incond = true;
                    last = input_buffer.getPos();
                } else if (current_state.isFinal(postblank)) {
                    boolean firstupper = Character.isUpperCase(sf.charAt(0));
                    boolean uppercase = firstupper && Character.isUpperCase(sf.charAt(sf.length() - 1));

                    lf = current_state.filterFinalsSAO(all_finals, alphabet,
                            escaped_chars,
                            uppercase, firstupper, 0);
                    //same call in the C++ code, but without the last argument 0.   ?????
                    
                    last_postblank = true;
                    last = input_buffer.getPos();
                } else if (!isAlphabetic(val)) {
                    boolean firstupper = Character.isUpperCase(sf.charAt(0));
                    boolean uppercase = firstupper && Character.isUpperCase(sf.charAt(sf.length() - 1));

                    lf = current_state.filterFinalsSAO(all_finals, alphabet,
                            escaped_chars,
                            uppercase, firstupper, 0);
                    //same call in the C++ code, but without the last argument 0.   ?????
                    
                    last_postblank = false;
                    last_incond = false;
                    last = input_buffer.getPos();
                }
            } else if (sf.equals("") && Character.isSpaceChar(val)) {
                lf = "/*" + sf;
                last_postblank = false;
                last_incond = false;
                last = input_buffer.getPos();
            }

            if (!Character.isUpperCase(val) || caseSensitive) {
                current_state.step(val);
            } else {
                current_state.step(val, Character.toLowerCase(val));
            }

            if (current_state.size() != 0) {
                alphabet.getSymbol(sf, val);
            } else {
                if (!isAlphabetic(val) && sf.equals("")) {
                    if (Character.isSpaceChar(val)) {
                        printSpace(val, output);
                    } else {
                        if (isEscaped(val)) {
                            output.write('\\');
                        }
                        output.write(val);
                    }
                } else if (last_incond) {
                    printSAOWord(lf, output);
                    input_buffer.setPos(last);
                    input_buffer.back(1);
                } else if (last_postblank) {
                    printSAOWord(lf, output);
                    output.write(' ');
                    input_buffer.setPos(last);
                    input_buffer.back(1);
                } else if (isAlphabetic(val) &&
                        ((sf.length() - input_buffer.diffPrevPos(last)) > lastBlank(sf) ||
                        lf.equals(""))) {
                    do {
                        alphabet.getSymbol(sf, val);
                    } while ((val = readSAO(input)) != null && isAlphabetic(val));

                    int limit = firstNotAlpha(sf);
                    int size = sf.length();
                    limit = (limit == Integer.MAX_VALUE ? size : limit);
                    input_buffer.back(1 + (size - limit));
                    output.write("<d>");
                    output.write(sf);
                    output.write("</d>");
                } else if (lf.equals("")) {
                    int limit = firstNotAlpha(sf);
                    int size = sf.length();
                    limit = (limit == Integer.MAX_VALUE ? size : limit);
                    input_buffer.back(1 + (size - limit));
                    output.write("<d>" + sf + "</d>");
                } else {
                    printSAOWord(lf, output);
                    input_buffer.setPos(last);
                    input_buffer.back(1);
                }

                current_state = initial_state;
                lf = "";
                sf = "";
                last_incond = false;
                last_postblank = false;
            }
        }

        // print remaining blanks
        flushBlanks(output);
    }

    String removeTags(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '<' && i >= 1 && str.charAt(i - 1) != '\\') {
                return str.substring(0, i);
            }
        }

        return str;
    }

    void setCaseSensitiveMode(boolean value) {
        caseSensitive = value;
    }

    void setNullFlush(boolean value) {
        nullFlush = value;
    }

    boolean getNullFlush() {
        return nullFlush;
    }

    int firstNotAlpha(String sf) {
        for (int i = 0,  limit = sf.length(); i < limit; i++) {
            if (!isAlphabetic(sf.charAt(i))) {
                return i;
            }
        }
        throw new RuntimeException("Should not have gotten here");
    }
}
