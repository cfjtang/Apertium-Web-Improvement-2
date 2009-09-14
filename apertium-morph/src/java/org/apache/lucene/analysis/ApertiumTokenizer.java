package org.apache.lucene.analysis;

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

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Token;

import org.apache.lucene.apertium.ApertiumWord;
import org.apache.lucene.apertium.ApertiumLexicalForm;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;

import java.util.Deque;
import java.util.ArrayDeque;

import java.util.Set;
import java.util.HashSet;

/** Generates tokens from an ApertiumWord.
 *
 * The following tokens are generated for the Apertium word
 * ^houses/house &lt;noun&gt;&lt;pl&gt;$ <br/><br/>
 *
 * sf#houses <br/> lemma#house <br/> tags#noun <br/> tags#noun.pl <br/><br/>
 *
 * Tokens for the same word are placed in the same postion (as
 * synonymous), that is, the first token will have a position
 * increment equals 1, the rest of tokens will have a position
 * increment equals 0.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */

public final class ApertiumTokenizer extends Tokenizer {

  private Deque<Token> tokensSamePosDeque;

  private static Set<String> tagsFound = new HashSet<String>();
  
  public ApertiumTokenizer(Reader input) {
    super(new BufferedReader(input));
    tokensSamePosDeque = new ArrayDeque<Token>();
  }

  /** 
   * Returns the next token in the stream, or null at EOS. 
   */
  public Token next() throws IOException {

    if (tokensSamePosDeque.size() > 0) {
      //System.err.println("Token: "+tokensSamePosDeque.element().termText());
      //System.err.println("Increment: "+tokensSamePosDeque.element().getPositionIncrement());
      //System.err.println("Start: "+tokensSamePosDeque.element().startOffset());
      //System.err.println("End: "+tokensSamePosDeque.element().endOffset());
      //System.err.println();
      return tokensSamePosDeque.poll();
    }

    genTokensFromApertiumWord(ApertiumWord.nextApertiumWord(input));

    if (tokensSamePosDeque.size() > 0) {
      //System.err.println("Token: "+tokensSamePosDeque.element().termText());
      //System.err.println("Increment: "+tokensSamePosDeque.element().getPositionIncrement());
      //System.err.println("Start: "+tokensSamePosDeque.element().startOffset());
      //System.err.println("End: "+tokensSamePosDeque.element().endOffset());
      //System.err.println();
      return tokensSamePosDeque.poll();
    } else
      return null;
  }

  /**
   * Returns a set containing all the distinct tags found while
   * indexing.
   */
  public static Set<String> getTagsFound() {
    return tagsFound;
  }
  
  /** 
   * Update tokensSamePosDeque to include tokens generated from the
   * Apertium word received as input.
   */
  private void genTokensFromApertiumWord(ApertiumWord word) throws IOException {
    if (word == null)
      return;
    
    Token t;
    
    //System.err.println(word.getSurfaceForm() + " " + word.startOffset() + " " + word.endOffset());
    
    //Superficial forms
    //////////////////////////////////////////////
    t = new Token(ApertiumAnalyzer.SURFACE_FORM + word.getSurfaceForm(), 
                  word.startOffset(), word.endOffset());
    t.setPositionIncrement(1);
    tokensSamePosDeque.add(t);
    
    //Lemmas
    //////////////////////////////////////////////    
    if (word.isUnknown()) {
      t = new Token(ApertiumAnalyzer.LEMMA + word.getSurfaceForm(), 
                    word.startOffset(), word.endOffset());
      t.setPositionIncrement(0);
      tokensSamePosDeque.add(t);
    } else {
      for (int i = 0; i < word.nLexicalForms(); i++) {
        ApertiumLexicalForm lexForm = word.getLexicalForm(i);
        for (int j = 0; j < lexForm.size(); j++) {
          if (lexForm.getLemmas()[j] != null) {
            t = new Token(ApertiumAnalyzer.LEMMA + lexForm.getLemmas()[j], 
                          word.startOffset(), word.endOffset());
            t.setPositionIncrement(0);
            tokensSamePosDeque.add(t);
          }
        }
      }
    }
    
    //Tags
    //////////////////////////////////////////////    
    if (word.isUnknown()) {
      t = new Token(ApertiumAnalyzer.TAGS + "unknown", 
                    word.startOffset(), word.endOffset());
      t.setPositionIncrement(0);
      tokensSamePosDeque.add(t);
      tagsFound.add("unknown");
    } else {
      for (int i = 0; i < word.nLexicalForms(); i++) {
        ApertiumLexicalForm lexForm = word.getLexicalForm(i);
        for (int j = 0; j < lexForm.size(); j++) {
          //All tags together
          //String str = lexForm.getStringTags(j);
          //if (str.length() > 0) {
          //  t = new Token(ApertiumAnalyzer.TAGS + str, word.startOffset(), word.endOffset());
          //  t.setPositionIncrement(0);
          //  tokensSamePosDeque.add(t);
          //  tagsFound.add(str);
          //}
          String[] str = lexForm.getStringTagsByAnalysisLevel(j);
          for(int k = 0; k < str.length; k++) {
            if (str[k].length() > 0) {
              t = new Token(ApertiumAnalyzer.TAGS + str[k], word.startOffset(), word.endOffset());
              t.setPositionIncrement(0);
              tokensSamePosDeque.add(t);
              tagsFound.add(str[k]);
            }
          }
        }
      }
    }
    
    //Lemmas chosen by the part-of-speech tagger
    //////////////////////////////////////////////
    if (word.isUnknown()) {
      t = new Token(ApertiumAnalyzer.LEMMA_TAGGER + word.getSurfaceForm(), 
                    word.startOffset(), word.endOffset());
      t.setPositionIncrement(0);
      tokensSamePosDeque.add(t);
    } else {
      ApertiumLexicalForm lexForm = word.getSelectedLexicalForm();
      for (int i = 0; i < lexForm.size(); i++) {
        if (lexForm.getLemmas()[i] != null) {
          t = new Token(ApertiumAnalyzer.LEMMA_TAGGER + lexForm.getLemmas()[i], 
                        word.startOffset(), word.endOffset());
          t.setPositionIncrement(0);
          tokensSamePosDeque.add(t);
        }
      }
    }
    
    //Tags chosen  by the part-of-speech tagger
    //////////////////////////////////////////////
    if (word.isUnknown()) {
      t = new Token(ApertiumAnalyzer.TAGS_TAGGER + "unknown", 
                    word.startOffset(), word.endOffset());
      t.setPositionIncrement(0);
      tokensSamePosDeque.add(t);
      //return;
    } else {
      ApertiumLexicalForm lexForm = word.getSelectedLexicalForm();
      for (int i = 0; i < lexForm.size(); i++) {
        //All tags together
        //String str = lexForm.getStringTags(i);
        //if (str.length() > 0) {
        //  t = new Token(ApertiumAnalyzer.TAGS_TAGGER + str, word.startOffset(), word.endOffset());
        //  t.setPositionIncrement(0);
        //  tokensSamePosDeque.add(t);            
        //}
        String[] str = lexForm.getStringTagsByAnalysisLevel(i);
        for(int k = 0; k < str.length; k++) {
          if (str[k].length() > 0) {
            t = new Token(ApertiumAnalyzer.TAGS_TAGGER + str[k], word.startOffset(), word.endOffset());
            t.setPositionIncrement(0);
            tokensSamePosDeque.add(t);
          }
        }
      }
    }
  }  
}
