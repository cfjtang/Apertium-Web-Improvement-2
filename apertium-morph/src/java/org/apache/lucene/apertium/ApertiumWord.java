package org.apache.lucene.apertium;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.Reader;
import java.lang.StringBuilder;

/**
 * Represents an Apertium word (<a
 *  href="http://www.apertium.org">http://www.apertium.org </a>).  The
 *  following example illustrates the text-based intermediate format
 *  used by Apertium:<br/><br/>
 *
 * ^books/book&lt;vblex&gt;&lt;pri&gt;&lt;p3&gt;&lt;sg&gt;/book&lt;n&gt;&lt;pl&gt;$
 * <br/><br/>
 * 
 * Here is the information extracted:<br/>
 * Surface form: books<br/>
 * Lexical form: book&lt;vblex&gt;&lt;pri&gt;&lt;p3&gt;&lt;sg&gt;<br/>
 *         lemma: book<br/>
 *         PoS: vblex.pri.p3.sg (verb, present tense, 3rd person, singular)<br/><br/>
 *
 * Lexical form: book&lt;n&gt;&lt;pl&gt;<br/>
 *         lemma: book<br/>
 *         PoS: n.pl (noun, plural)
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */

public class ApertiumWord {

  private String superficialForm;
  private ApertiumLexicalForm[] lexicalForms;
  private boolean isUnknown;

  private int offset_start;
  private int offset_end;

  private static int offset = 0;
 
  public ApertiumWord(String word, int offset_s, int offset_e) {
    isUnknown = false;
    offset_start = offset_s;
    offset_end = offset_e;

    //System.err.println("WORD: _" + word + "_");

    // Remove first ('^') and last ('$') characters
    String w = word.substring(1, word.length() - 1);
    String[] parts = w.split("/");

    superficialForm = parts[0];
    
    //System.err.println("SF: _" + superficialForm + "_");

    if (parts[1].startsWith("*")) {
      isUnknown = true;
      lexicalForms = null;
    } else {
      lexicalForms = new ApertiumLexicalForm[parts.length - 1];

      for (int i = 1; i < parts.length; i++) {        
        lexicalForms[i - 1] = new ApertiumLexicalForm(parts[i]);
      }
    }
  }

  /** 
   * Test wether this Apertium Word correspond to an unknown word or
   * not.
   */
  public boolean isUnknown() {
    return isUnknown;
  }

  /**
   * Get the surface form of this word.
   */
  public String getSurfaceForm() {
    return superficialForm;
  }

  /**
   * Get the number of lexical forms. Ambiguous words receive more
   * than one lexical form. If a part-of-speech tagger is used, the
   * first one is supossed to be the one chosen by the PoS tagger.
   */
  public int nLexicalForms() {
    if (lexicalForms == null)
      return 0;
    else
      return lexicalForms.length;
  }

  /**
   * Get the lexical form in the i-th postion. If a part-of-speech
   * tagger is used, the first one is supossed to be the one chosen by
   * the PoS tagger.
   */
  public ApertiumLexicalForm getLexicalForm(int i) {
    return lexicalForms[i];
  }

  /**
   * Get the lexical form selected by the part-of-speech tagger
   */
  public ApertiumLexicalForm getSelectedLexicalForm() {
    if (lexicalForms == null)
      return null;
    else
      return lexicalForms[0];
  }

  /**
   * Get the start offset of this Apertium word
   */
  public int startOffset() {
    return offset_start;
  }

  /**
   * Get the end offset of this Apertium word
   */
  public int endOffset() {
    return offset_end;
  }

  /**
   * From a string in the Apertium text-based intermediate format
   * removes all the inflection information and returns a string
   * containing only surface forms (in plain text format).
   *
   * @param apertiumstr String in the Apertium text-based
   * intermeadite format
   */
  public static String onlySuperficialForms(String apertiumstr) throws IOException{
    StringBuilder cleanstr = new StringBuilder(1024);
    char c, prev_c;
    boolean ignore = false;
    boolean readingWord = false;
    boolean append_char=false;
    prev_c= ' ';
    
    for(int i=0; i<apertiumstr.length(); i++) {
      c=apertiumstr.charAt(i);
            
      if ((c == '[') && (prev_c != '\\')) {
        ignore = true;
      } else if ((c == ']') && (prev_c != '\\')) {
        ignore = false;
      } else {
        if (!ignore) {
          if (c == '^') {
            if (readingWord) {
              throw new IOException("Found '^' while reading a word.");
            } else {
              readingWord = true;
              append_char = true;
            }
          }

          if (readingWord) {
            if (c == '/') {
              append_char = false;
            }
            if (c == '$') {
              readingWord = false;
              append_char = false;
              cleanstr.append(' ');
            }
            if ((append_char) && (c!='^'))
              cleanstr.append(c);
          }
        } 
      }

      prev_c = c;      
    }
    
    return cleanstr.toString();    
  }
 
  /**
   * Read an Apertium word from the input reader received as a
   * parameter.
   */ 
  public static ApertiumWord nextApertiumWord(Reader input) throws IOException {
    char c, prev_c;
    boolean ignore = false;
    boolean readingWord = false;
    boolean inc_offset = false;
    prev_c = ' ';
    int aux_c;

    StringBuilder wholeWord = new StringBuilder(512);

    int offset_start = 0, offset_end = 0;
    
    while (input.ready()) {
      aux_c = input.read();
      if (aux_c < 0)
        break;

      c = (char) aux_c;

      if ((c == '[') && (prev_c != '\\')) {
        ignore = true;
      } else if ((c == ']') && (prev_c != '\\')) {
        ignore = false;
      } else {
        if (!ignore) {
          if (c == '^') {
            if (readingWord) {
              throw new IOException("Found '^' while reading a word.");
            } else {
              readingWord = true;
              offset_start = offset;
              inc_offset = true;
            }
          }

          if (readingWord) {
            wholeWord.append(c);
            if (c == '/') {
              inc_offset = false;
            }
            if (c == '$') {
              readingWord = false;
              inc_offset = false;
              offset_end = offset;
              offset++;
              break;
            }
            if ((inc_offset) && (c != '^'))
              offset++;
          }
        }
      }

      prev_c = c;
    }

    if ((wholeWord.length() == 0) || (wholeWord.charAt(0) != '^') || (wholeWord.charAt(wholeWord.length() - 1) != '$')) {
      offset = 0;
      return null;
    }


    //System.err.println("WORD: _" + wholeWord.toString().toLowerCase() + "_");
    return new ApertiumWord(wholeWord.toString().toLowerCase(), offset_start, offset_end);
  }
}
