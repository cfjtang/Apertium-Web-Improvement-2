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

import org.apache.lucene.analysis.ApertiumAnalyzer;

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
 *  JUnit Test for ApertiumAnalyzer class.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */
public class TestApertiumAnalyzer extends TestCase {
  private static final String[] inputs = { 
      "^toman/tomar<vblex><pri><p3><pl>$ ^medidas/medida<n><f><pl>/medir<vblex><pp><f><pl>$",
      " ^del/de<pr>+el<det><def><m><sg>$ ^Nuevo/Nuevo<adj><m><sg>$ ^Año/Año<n><m><sg>$" };

  private static final String[][] outputs = {
    {"sf#toman", "lem#tomar", "tags#vblex", "tags#vblex.pri", "tags#vblex.pri.p3", 
     "tags#vblex.pri.p3.pl", "lemt#tomar", "tagst#vblex", "tagst#vblex.pri", 
     "tagst#vblex.pri.p3", "tagst#vblex.pri.p3.pl",
     "sf#medidas", "lem#medida", "lem#medir", "tags#n", "tags#n.f", "tags#n.f.pl", 
     "tags#vblex", "tags#vblex.pp", "tags#vblex.pp.f", "tags#vblex.pp.f.pl", 
     "lemt#medida", "tagst#n", "tagst#n.f", "tagst#n.f.pl"},
    {"sf#del", "lem#de", "lem#el", "tags#pr", "tags#det", "tags#det.def", "tags#det.def.m", 
     "tags#det.def.m.sg", "lemt#de", "lemt#el", "tagst#pr", "tagst#det", "tagst#det.def", 
     "tagst#det.def.m", "tagst#det.def.m.sg",
     "sf#nuevo", "lem#nuevo", "tags#adj", "tags#adj.m", "tags#adj.m.sg", "lemt#nuevo", 
     "tagst#adj", "tagst#adj.m", "tagst#adj.m.sg", 
     "sf#año", "lem#año", "tags#n", "tags#n.m", "tags#n.m.sg", "lemt#año", "tagst#n", 
     "tagst#n.m", "tagst#n.m.sg"}};

  private static final int[][] increments = {
    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
     1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
     1, 0, 0, 0, 0, 0, 0, 0, 0, 
     1, 0, 0, 0, 0, 0, 0, 0, 0}};


  private void assertAnalyzesTo(Analyzer a, String input, String[] output, int[] increments) throws Exception {
    TokenStream ts = a.tokenStream("dummy", new StringReader(input));

    for (int i = 0; i < output.length; i++) {
      Token t = ts.next();
      assertNotNull(t);

      //System.out.println(t.termText() + " " + t.getPositionIncrement());

      assertEquals(t.termText(), output[i]);
      assertEquals(t.getPositionIncrement(), increments[i]);
    }
    assertNull(ts.next());
    ts.close();
  }

  public void testAnalyzer() throws Exception {
    ApertiumAnalyzer analyzer = new ApertiumAnalyzer();

    for (int i=0; i<inputs.length; i++) {
      //System.out.println("Analyzing \"" + inputs[i] + "\"");
      assertAnalyzesTo(analyzer, inputs[i], outputs[i], increments[i]); 
      //System.out.println("\n");
    }
  }
}
