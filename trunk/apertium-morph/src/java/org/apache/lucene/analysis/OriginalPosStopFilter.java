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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;

/**
 * Removes stop words from a token stream, but respecting the original
 * position.  <br/> 
 * Example: Original: "the sky is blue": <br/>
 * Tokens(Position): the(1) sky(2) is(3) blue(4) <br/>
 *
 * Using StopFilter: Tokens(Position): sky(1) blue(2) <br/>
 * Using this filter: Tokens(Position): sky(2) blue(4)
 */

public final class OriginalPosStopFilter extends TokenFilter {

  private final Set<String> stopWords;

  private final boolean ignoreCase;

  /**
   * Construct a token stream filtering the given input.
   */
  public OriginalPosStopFilter(TokenStream input, String[] stopWords) {
    this(input, stopWords, false);
  }

  /**
   * Constructs a filter which removes words from the input TokenStream that are
   * named in the array of words.
   */
  public OriginalPosStopFilter(TokenStream in, String[] stopWords, boolean ignoreCase) {
    super(in);
    this.ignoreCase = ignoreCase;
    this.stopWords = makeStopSet(stopWords, ignoreCase);
  }

  /**
   * Construct a token stream filtering the given input.
   * 
   * @param input
   * @param stopWords
   *          The set of Stop Words, as Strings. If ignoreCase is true, all
   *          strings should be lower cased
   * @param ignoreCase
   *          -Ignore case when stopping. The stopWords set must be setup to
   *          contain only lower case words
   */
  public OriginalPosStopFilter(TokenStream input, Set<String> stopWords, boolean ignoreCase) {
    super(input);
    this.ignoreCase = ignoreCase;
    this.stopWords = stopWords;
  }

  /**
   * Constructs a filter which removes words from the input TokenStream that are
   * named in the Set. It is crucial that an efficient Set implementation is
   * used for maximum performance.
   * 
   * @see #makeStopSet(java.lang.String[])
   */
  public OriginalPosStopFilter(TokenStream in, Set<String> stopWords) {
    this(in, stopWords, false);
  }

  /**
   * Builds a Set from an array of stop words, appropriate for passing into the
   * StopFilter constructor. This permits this stopWords construction to be
   * cached once when an Analyzer is constructed.
   * 
   * @see #makeStopSet(java.lang.String[], boolean) passing false to ignoreCase
   */
  public static final Set<String> makeStopSet(String[] stopWords) {
    return makeStopSet(stopWords, false);
  }

  /**
   * 
   * @param stopWords
   * @param ignoreCase
   *          If true, all words are lower cased first.
   * @return a Set containing the words
   */
  public static final Set<String> makeStopSet(String[] stopWords, boolean ignoreCase) {
    HashSet<String> stopTable = new HashSet<String>(stopWords.length);
    for (int i = 0; i < stopWords.length; i++)
      stopTable.add(ignoreCase ? stopWords[i].toLowerCase() : stopWords[i]);
    return stopTable;
  }

  /**
   * Returns the next input Token whose termText() is not a stop word. This is
   * the only method differing from the Lucence StopFilter. The original
   * position of each token is preserved.
   */
  public final Token next() throws IOException {
    int posIncrement = 0;
    for (Token token = input.next(); token != null; token = input.next()) {
      String termText = ignoreCase ? token.termText().toLowerCase() : token.termText();
      if (!stopWords.contains(termText)) {
        int newIncrement = token.getPositionIncrement() + posIncrement;
        token.setPositionIncrement(newIncrement);
        //System.err.println(token.termText());
        return token;
      } else {
        posIncrement += token.getPositionIncrement();
      }
    }
    return null;
  }
}
