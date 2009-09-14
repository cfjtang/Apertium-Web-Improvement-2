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

import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

import java.util.Set;
import java.util.HashSet;

/** 
 *  Lucene analyzer that in conjunction with {@link
 *  LinguisticQueryTokenizer} may be used to analyze queries in the
 *  standard lucene query language. In that query some prefixes
 *  (defined in {@link ApertiumAnalyzer}) can be used to define query
 *  terms using morhpological information.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */

public final class LinguisticQueryAnalyzer extends Analyzer {

  private Set<String> stopItems;  

  public LinguisticQueryAnalyzer() {
    stopItems = new HashSet<String>();
  }

  public LinguisticQueryAnalyzer(File stopWordsFile, File stopTagsFile, File stopLemmasFile) {
    stopItems = new HashSet<String>();

    if (stopWordsFile != null)
      ApertiumAnalyzer.readStopItems(stopItems, stopWordsFile, ApertiumAnalyzer.SURFACE_FORM);

    if (stopTagsFile != null) {
      ApertiumAnalyzer.readStopItems(stopItems, stopTagsFile, ApertiumAnalyzer.TAGS);
      ApertiumAnalyzer.readStopItems(stopItems, stopTagsFile, ApertiumAnalyzer.TAGS_TAGGER);
    }

    if (stopLemmasFile != null) {
      ApertiumAnalyzer.readStopItems(stopItems, stopLemmasFile, ApertiumAnalyzer.LEMMA);
      ApertiumAnalyzer.readStopItems(stopItems, stopLemmasFile, ApertiumAnalyzer.LEMMA_TAGGER);
    }
  }

  /**
   * Creates a TokenStream which tokenizes all the text in the
   * provided Reader.
   */
  public TokenStream tokenStream(String fieldName, Reader input) {
    //return new LinguisticQueryTokenizer(input);
    return new OriginalPosStopFilter(new LinguisticQueryTokenizer(input), stopItems);
  }
}
