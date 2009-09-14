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

import org.apache.lucene.analysis.LinguisticQueryAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 *  JUnit Test for LinguisticQueryAnalyzer class.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */
public class TestLinguisticQueryAnalyzer extends TestCase {
  private static final String[] inputs = { 
    "lem#gato", "lem#coche", "tags#vblex.inf", "sf#de", "lem#parlamento",
    "lem#titular lem#finanza", "lem#educación",
    "lem#titular finanzas",
    "lem#deber de tags#vblex.inf", 
    "lem#estar a punto de"};

  private static final String[][] outputs = { 
    {"lem#gato"}, {"lem#coche"}, {"tags#vblex.inf"}, {"sf#de"}, {"lem#parlamento"},
    {"lem#titular", "lem#finanza"}, {"lem#educación"},
    {"lem#titular", "sf#finanzas"},
    {"lem#deber", "sf#de", "tags#vblex.inf"}, 
    {"lem#estar", "sf#a", "sf#punto", "sf#de"}};

  private void assertAnalyzesTo(Analyzer a, String input, String[] output) throws Exception {
    TokenStream ts = a.tokenStream("dummy", new StringReader(input));

    for (int i = 0; i < output.length; i++) {
      Token t = ts.next();
      assertNotNull(t);
      assertEquals(t.termText(), output[i]);
      //System.out.println(t.termText() + " " + output[i]);
    }
    assertNull(ts.next());
    ts.close();
  }

  public void testAnalyzer() throws Exception {
    LinguisticQueryAnalyzer analyzer = new LinguisticQueryAnalyzer();

    for (int i=0; i<inputs.length; i++) {
      //System.out.println("Analyzing \"" + inputs[i] + "\"");
      assertAnalyzesTo(analyzer, inputs[i], outputs[i]); 
      //System.out.println("\n");
    }
  }
}
