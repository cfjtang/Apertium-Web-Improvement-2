package org.apache.lucene.search.highlight;

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

import org.apache.lucene.analysis.Token;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link Fragmenter} implementation which breaks text up into
 * fragments that correspond to senteces. 
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */
public class SentenceFragmenter implements Fragmenter {

  private static final String[] end_of_sentence = {"lem#.", "lem#:", "lem#!", "lem#?", 
                                                   "lem#)", "lem#]", "lem#}"};
  private Set<String> eos;
  private boolean nextTokenIsNewFragment;

  public SentenceFragmenter() {
    nextTokenIsNewFragment = true;

    eos = new HashSet<String>();

    for (int i=0; i < end_of_sentence.length; i++)
      eos.add(end_of_sentence[i]);
  }


  /* (non-Javadoc)
   * @see org.apache.lucene.search.highlight.TextFragmenter#start(java.lang.String)
   */
  public void start(String originalText) {

  }

  /* (non-Javadoc)
   * @see org.apache.lucene.search.highlight.TextFragmenter#isNewFragment(org.apache.lucene.analysis.Token)
   */
  public boolean isNewFragment(Token token) {

    if (nextTokenIsNewFragment) {
      nextTokenIsNewFragment = false;
      return true;
    }

    String termText = new String(token.termBuffer(), 0, token.termLength());

    if (eos.contains(termText)) 
      nextTokenIsNewFragment = true;

    return false;
  }
}
