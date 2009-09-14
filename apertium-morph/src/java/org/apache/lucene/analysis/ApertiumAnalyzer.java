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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import org.apache.lucene.apertium.ApertiumLexicalForm;

import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/** 
 *  Lucene Analyzer that (in conjunction with {@link
 *  ApertiumTokenizer}) interprets the Apertium (<a
 *  href="http://www.apertium.org">http://www.apertium.org </a>)
 *  text-based intermediate format and allows the Lucene engine to use
 *  morphological information initially developed for the Apertium
 *  open-source machine translation platform.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */

public final class ApertiumAnalyzer extends Analyzer {
  /** Prefix to prepend to each surface form*/
  public static final String SURFACE_FORM = "sf#";

  /** Prefix to prepend to the lemma selected by the Apertium
   * part-of-speech tagger*/
  public static final String LEMMA_TAGGER = "lemt#";

  /** Prefix to prepend to the tags (morphological attributes)
   * selected by the Apertium part-of-speech tagger*/
  public static final String TAGS_TAGGER = "tagst#";

  /** Prefix to prepend to each possible lemma of an Apertium word*/
  public static final String LEMMA = "lem#";

  /** Prefix to prepend to each possible tag of an Apertium word*/
  public static final String TAGS = "tags#";
  
  private String  stringToAnalyze;
  private Set<String> stopWords;

  /**
   * Read a list of stop items to ignore by the analyzer.
   * @param stopItems set of items to ignore
   * @param stopItemsFile file containing one item per line to ignore
   * @param prefix prefix to add to each item being ignored. The
   * prefix will determine if the items being ignored correspond to
   * lemmas or tags.
   */
  public static void readStopItems(Set<String> stopItems, File stopItemsFile, String prefix) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(stopItemsFile));
      while (reader.ready()) {
        String str = reader.readLine().trim();
        if (str.length() > 0)
          stopItems.add(prefix + str);
      }
      reader.close();
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }   
  }

  private static void  readRenameItems(Map<String, String> renameItems, File renameItemsFile) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(renameItemsFile));
      while (reader.ready()) {
        String str = reader.readLine().trim();
        if (str.length() > 0) {
          String[] tags = str.split("\\s+");
          if (tags.length != 2) {
            System.err.println("Error: " + tags.length + " items in renametags file '" + renameItemsFile.getCanonicalPath() + "'");
            System.err.println("Line: " + str);
          }
          renameItems.put(tags[0], tags[1]);

          //System.err.println(tags[0] + " -> " + tags[1]);
        }
      }
      reader.close();
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }   
  }

  public ApertiumAnalyzer() {
    stringToAnalyze = null;
    
    stopWords = new HashSet<String>();
    
    Set<String> stopTags = new HashSet<String>();
    Set<String> stopLemmas = new HashSet<String>();
    Map<String, String> renameTags = new HashMap<String, String>();

    //The filtering of the stoptags and stoplemmas will not be done by the OriginalPosStopFilter
    ApertiumLexicalForm.setTagsToIgnore(stopTags);
    ApertiumLexicalForm.setLemmasToIgnore(stopLemmas); 
    ApertiumLexicalForm. setTagsToRename(renameTags);
  }
  
  /** 
   * @param stopWordsFile File with a list of words (surface forms) to
   * ignore
   * @param stopTagsFile File with a list of par-of-speech tags to
   * ignore 
   * @param stopLemmasFile File with a list of lemmas to ignore
   * @param renameTagsFile File containing in each line a pair of
   * string, the fist one will be substituted by the second one
   */
  public ApertiumAnalyzer(File stopWordsFile, File stopTagsFile, File stopLemmasFile, File renameTagsFile) {
    stringToAnalyze = null;
    
    stopWords = new HashSet<String>();

    Set<String> stopTags = new HashSet<String>();
    Set<String> stopLemmas = new HashSet<String>();
    Map<String, String> renameTags = new HashMap<String, String>();
    
    if (stopWordsFile != null)
      readStopItems(stopWords, stopWordsFile, ApertiumAnalyzer.SURFACE_FORM);
    if (stopTagsFile != null)
      readStopItems(stopTags, stopTagsFile, "");
    if (stopLemmasFile != null)
      readStopItems(stopLemmas, stopLemmasFile, "");
    if (renameTagsFile != null)
      readRenameItems(renameTags, renameTagsFile);
    
    //The filtering of the stoptags and stoplemmas will not be done by the OriginalPosStopFilter
    ApertiumLexicalForm.setTagsToIgnore(stopTags);
    ApertiumLexicalForm.setLemmasToIgnore(stopLemmas);
    ApertiumLexicalForm. setTagsToRename(renameTags);
  }
  
  /**
   * Set the string that is the string actually analyzed. This method
   * is provided so as to store a plain-text string in the index but
   * analyze a string in the Apertium text-based intermediate format.
   */
  public void setStringActuallyAnalyzed(String s) {
    stringToAnalyze=s;
  }
  
  /**
   * Creates a TokenStream which tokenizes all the text in the
   * provided Reader.
   */
  public TokenStream tokenStream(String fieldName, Reader input) {
    if (stringToAnalyze != null)
      return new OriginalPosStopFilter(new ApertiumTokenizer( new BufferedReader(new StringReader(stringToAnalyze))), stopWords);
    else
      return new OriginalPosStopFilter(new ApertiumTokenizer(input), stopWords);
  }
}
