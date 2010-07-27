/*
 * Copyright (C) 2010 Stephen Tigner
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

package org.apertium.interchunk;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.apertium.lttoolbox.Alphabet;
import org.apertium.lttoolbox.Pair;
import org.apertium.transfer.ApertiumRE;
import org.apertium.transfer.BufferT;
import org.apertium.transfer.MatchExe;
import org.apertium.transfer.MatchState;
import org.apertium.transfer.TransferClassLoader;
import org.apertium.transfer.TransferToken;
import org.apertium.transfer.TransferWord;
import org.apertium.transfer.development.Timing;
import org.apertium.transfer.generated.GeneratedTransferBase;

/**
 * @author Stephen Tigner
 * 
 */
public class Interchunk {

    /*
     * This class is very similar to the Transfer class, so some code was just
     * copied straight over, with alot of commented-out code removed.
     */

    // Private members and methods
    private Alphabet alphabet;
    private String TRXReader__ANY_CHAR = "<ANY_CHAR>";
    private String TRXReader__ANY_TAG = "<ANY_TAG>";

    private MatchExe me; // Pointer in C++ version
    private MatchState ms;
    // private Map<String, ApertiumRE> attr_items;
    // private Map<String, String> variables;
    // private Map<String, Integer> macros;
    // private Map<String, Set<String>> lists;
    // private Map<String, Set<String>> listslow;

    // vector<xmlNode *> macro_map; -- not in Transfer
    private Method[] rule_map = null; // vector<xmlNode *> rule_map;
    // xmlDoc *doc; -- not in Transfer
    // xmlNode *root_element; -- not in Transfer

    private InterchunkWord[] word; // InterchunkWord **
    private String[] blank; // string **
    private int lword, lblank;
    private BufferT<TransferToken> input_buffer=new BufferT<TransferToken>(); // Buffer<TransferToken> input_buffer;
    private ArrayList<String> tmpword; // vector<wstring *> tmpword;
    private ArrayList<String> tmpblank; // vector<wstring *> tmpblank;
    private ArrayList<String> tmpword2=new ArrayList<String>();
    private ArrayList<String> tmpblank2=new ArrayList<String>();

    
    private OutputStreamWriter output;
    private int any_char;
    private int any_tag;

    private Method lastrule; // xmlNode *lastrule;
    private int nwords;
    public GeneratedTransferBase transferObject;

    // map<xmlNode *, TransferInstr> evalStringCache;
    private boolean inword;
    private boolean null_flush;
    private boolean internal_null_flush;

    public static boolean DEBUG = false;

    // Moved these up here, in contrast to Transfer, where they're stuck down
    // between some functions.
    public Timing timing;
    public static final boolean DO_TIMING = false;

    /**
     * Copied from {@link org.apertium.transfer.Transfer#readData(InputStream)}
     * 
     * @param in
     * @throws IOException
     */
    public void readData(InputStream in) throws IOException {
        // symbols
        alphabet = Alphabet.read(in);
        any_char = alphabet.cast(TRXReader__ANY_CHAR);
        any_tag = alphabet.cast(TRXReader__ANY_TAG);

        // faster - let it read itself, thus no need to make a big hashmap
        me = new MatchExe(in, alphabet.size());
        ms = new MatchState(me);
    }

    /**
     * Copied from
     * {@link org.apertium.transfer.Transfer#read(String, String, String)}
     * 
     * @param classFile
     * @param datafile
     * @param fstfile
     * @throws Exception
     */
    public void read(String classFile, String datafile) throws Exception {
        if (!classFile.endsWith(".class")) {
            System.err
                    .println("Warning: "
                            + classFile
                            + " should be a Java .class file. You probably got it wrong");
        }

        read(new TransferClassLoader().loadClassFile(classFile), datafile);
    }

    /**
     * Copied from
     * {@link org.apertium.transfer.Transfer#read(Class, String, String)}
     * 
     * @param transferClass
     * @param datafile
     * @param fstfile
     * @throws Exception
     */
    public void read(Class transferClass, String datafile) throws Exception {

        InputStream is = new BufferedInputStream(new FileInputStream(datafile));
        readData(is);
        is.close();

        Method[] mets = transferClass.getMethods();
        rule_map = new Method[mets.length];

        for (Method method : mets) {
            String name = method.getName();

            // System.err.println("n = " + name);
            if (!name.startsWith("rule"))
                continue;

            int number = Integer.parseInt(name.substring(4, name
                    .indexOf('_', 5)));
            rule_map[number] = method;

            if (DEBUG)
                System.err.println(method.getName() + "  - #words="
                        + method.getParameterTypes().length / 2);
        }

        transferObject = (GeneratedTransferBase) transferClass.newInstance();
        transferObject.debug = DEBUG;
        transferObject.init();

    }

    /**
     * Copied from
     * {@link org.apertium.transfer.Transfer#transfer_wrapper_null_flush(Reader, Writer)}, 
     * then modified slightly, in-line with the changes between transfer.cc
     * and interchunk.cc
     * 
     * @param input
     * @param output
     * @throws Exception
     */
    private void interchunk_wrapper_null_flush(Reader input, Writer output)
            throws Exception {
        null_flush = false;
        internal_null_flush = true;
        while (input.ready()) {
            interchunk(input, output);
            output.write('\0');
            try {
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Could not flush output");
            }
        }
        internal_null_flush = false;
        null_flush = true;
    }
    
    public boolean getNullFlush() {
        return null_flush;
    }

    public void setNullFlush(boolean b) {
        null_flush = b;
    }

    /**
     * Much of this code originally copied from {@link org.apertium.transfer.Transfer#transfer(Reader, Writer)}.
     * Modified to be in-line with the differences between transfer.cc and interchunk.cc
     * @param in
     * @param output
     * @throws Exception
     */
    public void interchunk(Reader in, Writer output) throws Exception {
        if (getNullFlush()) {
            interchunk_wrapper_null_flush(in, output);
        }

        int last = 0;
        ms.init(me.getInitial());
        if (DO_TIMING)
            timing = new Timing("Interchunk");
        while (true) {
            if (ms.size() == 0) {
                if (lastrule != null) {
                    applyRule(output);
                    input_buffer.setPos(last);
                } else {
                    if (tmpword.size() != 0) {
                        output.write('^');
                        output.write(tmpword.get(0));
                        output.write('$');
                        tmpword.clear();
                        input_buffer.setPos(last);
                        input_buffer.next();
                        last = input_buffer.getPos();
                        ms.init(me.getInitial());
                    } else if (tmpblank.size() != 0) {
                        output.write(tmpblank.get(0));
                        tmpblank.clear();
                        last = input_buffer.getPos();
                        ms.init(me.getInitial());
                    }
                }
            }
            if (DO_TIMING)
                timing.log("interchunk");
            int val = ms.classifyFinals();
            if (DO_TIMING)
                timing.log("interchunk/ms.classifyFinals");
            if (val != -1) {

                lastrule = rule_map[(val - 1)];
                // XXXXXXXX lastrule = rule_map.get(val-1);
                last = input_buffer.getPos();

                if (DEBUG)
                    System.err.println("lastrule = " + (val - 1) + " "
                            + lastrule.getName());
                if (DEBUG)
                    System.err.println("tmpword = " + tmpword.size()
                            + "  tmpblank = " + tmpblank.size());
                if (DEBUG)
                    System.err.println("tmpword = " + tmpword + "  tmpblank = "
                            + tmpblank);
                tmpword2.clear();
                tmpblank2.clear();
                tmpword2.addAll(tmpword);
                tmpblank2.addAll(tmpblank);
            }

            if (DO_TIMING)
                timing.log("interchunk");
            TransferToken current = readToken(in);
            if (DO_TIMING)
                timing.log("readToken");

            switch (current.type) {
                case tt_word:
                    applyWord(current.content);
                    tmpword.add(current.content);
                    break;

                case tt_blank:
                    ms.step(' ');
                    tmpblank.add(current.content);
                    break;

                case tt_eof:
                    if (tmpword.size() != 0) {
                        tmpblank.add(current.content);
                        ms.clear();
                    } else {
                        output.write(current.content);
                        tmpblank.clear();
                        if (DO_TIMING) {
                            timing.log("interchunk");
                            timing.report();
                        }
                        return;
                    }
                    break;
    
                default:

                    System.err.println("Error: Unknown input token.");
                    return;
            }
        }
    }

    /**
     * Much of this code originally copied from {@link org.apertium.transfer.Transfer#applyRule(Writer)}.
     * Modified to be in-line with the differences between transfer.cc and interchunk.cc
     * @param output
     * @throws Exception
     */
    private void applyRule(Writer output) throws Exception {
        if (DEBUG) System.err.println("tmpword = " + tmpword2+ "  tmpblank = " + tmpblank2);
        if (DO_TIMING) timing.log("other1");

        int limit=tmpword2.size();

        Object[] args = new Object[1+limit + limit -1];
        int argn = 0;
        args[argn++] = output;


        InterchunkWord[] word=null; // TransferWord **word;
        String[] blank=null; // string **blank;

      for (int i=0; i!=limit; i++)
        {
          if (i==0)
          {
            word=new InterchunkWord[limit];
            if (limit!=0)
            {
              blank=new String[limit-1];
            } else
            {
              blank=null;
            }
          }
          else
          {
            blank[i-1]=tmpblank2.get(i-1);
            args[argn++] = tmpblank2.get(i-1);
          }

          args[argn++] = word[i]=new InterchunkWord(tmpword2.get(i));
        }

        if (DEBUG) System.err.println("word = " + Arrays.toString(word));

        if (DEBUG) System.err.println("#args = " + args.length);
        if (DEBUG) System.err.println("processRule:"+lastrule.getName()+"("+Arrays.toString(args));
        try {
          if (DO_TIMING) timing.log("applyRule 1");
          lastrule.invoke(transferObject, args);
          if (DO_TIMING) timing.log("rule invoke");
        } catch (Exception e) {
          System.err.println("Error during invokation of "+lastrule);
          System.err.println("word = " + Arrays.toString(word));
          System.err.println("#args = " + args.length);
          System.err.println("processRule:"+lastrule.getName()+"("+Arrays.toString(args));
          throw e;
        }
        if (DEBUG) output.flush();
        
        //processRule(lastrule);
        lastrule=null;

        word=null;
        blank=null;
        tmpword.clear();
        tmpblank.clear();
        tmpword2.clear();
        tmpblank2.clear();
        if (DO_TIMING) timing.log("applyRule 1");
        ms.init(me.getInitial());
        if (DO_TIMING) timing.log("applyRule 2");
      }

    /**
     * Much of this code originally copied from {@link org.apertium.transfer.Transfer#applyWord(string)}.
     * Modified to be in-line with the differences between transfer.cc and interchunk.cc 
     * @param word_str
     */
    private void applyWord(String word_str) {
        if (DO_TIMING)
            timing.log("other");
        ms.step('^');
        for (int i = 0, limit = word_str.length(); i < limit; i++) {
            switch (word_str.charAt(i)) {
            case '\\':
                i++;
                ms.step(Character.toLowerCase(word_str.charAt(i)), any_char);
                break;

            case '<':
                for (int j = i + 1; j != limit; j++) {
                    if (word_str.charAt(j) == '>') {
                        int symbol = alphabet
                                .cast(word_str.substring(i, j + 1));
                        if (symbol != 0) {
                            ms.step(symbol, any_tag);
                        } else {
                            ms.step(any_tag);
                        }
                        i = j;
                        break;
                    }
                }
                break;

            case '{': //ignore the unmodifiable part of the chunk
                ms.step('$');
                return;

            default:
                ms.step(Character.toLowerCase(word_str.charAt(i)), any_char);
                break;
            }
        }
        ms.step('$');
        if (DO_TIMING)
            timing.log("applyWord");
    }
}
