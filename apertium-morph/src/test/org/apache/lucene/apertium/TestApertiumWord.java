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

import org.apache.lucene.apertium.ApertiumWord;
import org.apache.lucene.apertium.ApertiumLexicalForm;

import java.util.Set;
import java.util.HashSet;

import java.util.Map;
import java.util.HashMap;

import junit.framework.TestCase;

import java.io.StringReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *  JUnit Test for ApertiumWord class.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */

public class TestApertiumWord extends TestCase {
  private static final String[] inputs = { 
      "[  ]^Moscu/Moscu<np><loc>$^,/,<cm>$ ^1/1<num>$ ^ene/*ene$ ^(/(<lpar>$^EFE/EFE<n><f><sg>$^)/)<rpar>$^./.<sent>$",
      "^01/01<num>$\\/^01/01<num>$\\/^01/01<num>$-^02/02<num>$\\/^95/95<num>$^./.<sent>$[]", "^toman/tomar<vblex><pri><p3><pl>$ ^medidas/medida<n><f><pl>/medir<vblex><pp><f><pl>$",
      " ^del/de<pr>+el<det><def><m><sg>$ ^Nuevo/Nuevo<adj><m><sg>$ ^Anyo/Anyo<n><m><sg>$" };

  private static final String[][] surface_forms = {
      {"moscu", ",", "1", "ene", "(", "efe", ")", "."},
      {"01","01", "01", "02", "95", "."},
      {"toman", "medidas"},
      {"del", "nuevo", "anyo"}};

  private static final int[][] start_offsets = {
      {0, 6, 8, 10, 14, 16, 20, 22},
      {0, 3, 6, 9, 12, 15},
      {0, 6},
      {0, 4, 10}};

  private static final int[][] end_offsets = {
      {5, 7, 9, 13, 15, 19, 21, 23},
      {2, 5, 8, 11, 14, 16},
      {5, 13},
      {3, 9, 14}};

  private static void assertsApertiumWord(String text, String[] sf, int[] s_off, int[] e_off) throws IOException {

    //System.out.println("Analyzing \"" + text + "\"");

    BufferedReader reader = new BufferedReader(new StringReader(text));
    ApertiumWord apertiumWord;

    for (int i=0; i<sf.length; i++) {
      apertiumWord = ApertiumWord.nextApertiumWord(reader);
      assertNotNull(apertiumWord);

      //System.out.print(apertiumWord.getSurfaceForm() + " ");
      //System.out.print(apertiumWord.startOffset() + " ");
      //System.out.println(apertiumWord.endOffset());

      assertEquals(apertiumWord.getSurfaceForm(), sf[i]);
      assertEquals(apertiumWord.startOffset(), s_off[i]);
      assertEquals(apertiumWord.endOffset(), e_off[i]);
    }  
    assertNull(ApertiumWord.nextApertiumWord(reader));  
  }

  public void testApertiumWord() throws Exception {
    Set voidSet = new HashSet<String>();
    Map voidMap = new HashMap<String, String>();

    ApertiumLexicalForm.setTagsToIgnore(voidSet);
    ApertiumLexicalForm.setLemmasToIgnore(voidSet);
    ApertiumLexicalForm.setLemmasToIgnore(voidSet);
    ApertiumLexicalForm.setTagsToRename(voidMap);

    for (int i=0; i < inputs.length; i++) 
      assertsApertiumWord(inputs[i], surface_forms[i], start_offsets[i], end_offsets[i]);
  }
}
