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
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.Token;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;

/** Lucene tokenizer used by {@link LinguisticQueryAnalyzer} to
 *  analyze queries in the standard lucene quiery language. In that
 *  query some prefixes can be used to define query terms using
 *  morhpological information.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */

public class LinguisticQueryTokenizer extends Tokenizer {

  WhitespaceTokenizer wst;

  public LinguisticQueryTokenizer(Reader in) {
    super(in);
    wst = new WhitespaceTokenizer(in);    
  }

 /** 
   * Returns the next token in the stream, or null at EOS. 
   */
  public Token next() throws IOException {
    Token t = wst.next();

    if (t == null)
      return t;

    String termtext=t.termText();

    //System.err.println("TermText (in): _" + termtext + "_");

    if (!termtext.startsWith(ApertiumAnalyzer.LEMMA_TAGGER) &&
	!termtext.startsWith(ApertiumAnalyzer.TAGS_TAGGER) &&
        !termtext.startsWith(ApertiumAnalyzer.LEMMA) &&
        !termtext.startsWith(ApertiumAnalyzer.TAGS) &&
        !termtext.startsWith(ApertiumAnalyzer.SURFACE_FORM))
      t.setTermText(ApertiumAnalyzer.SURFACE_FORM + termtext);  

    //System.err.println("TermText (out): _" + t.termText() + "_");
    return t;
  }
}
