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

import java.lang.StringBuilder;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;

/** 
 * Represents an Apertium lexical form (see {@link ApertiumWord}). A
 * lexical form may contain more than one lemma and tags because of
 * contractions (e.g. del = de&lt;pr&gt; +
 * el&lt;det&gt;&lt;def&gt;&lt;m&gt;&lt;sg&gt;)
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */
public class ApertiumLexicalForm {
  
  // lemmas[i] contains the i-th lemma
  // tags[i] contains the set of tags attached to the i-th lemma
  private String[] lemmas;
  private String[][] tags;

  private static Set<String> tagsToIgnore;
  private static Set<String> lemmasToIgnore;
  private static Map<String, String> renameTags;

  /**
   * Set the set of tags to ignore (stop tags)
   * @param tagsIgnore tags to ignore
   */
  public static void setTagsToIgnore(Set<String> tagsIgnore) {
    tagsToIgnore = tagsIgnore;
  }

  /**
   * Set the set of lemmas to ignore (stop lemmas)
   * @param lemmasIgnore lemmas to ignore
   */  
  public static void setLemmasToIgnore(Set<String> lemmasIgnore) {
    lemmasToIgnore = lemmasIgnore;
  }

  public static void setTagsToRename(Map<String, String> tagsToRename) {
    renameTags = tagsToRename;
  }

  public ApertiumLexicalForm(String lexicalForm) {
    String[] parts = lexicalForm.split("\\+");

    //System.err.println("LF: _" + lexicalForm + "_");

    lemmas = new String[parts.length];
    tags = new String[parts.length][];

    for (int i = 0; i < parts.length; i++) {
      int beginTags = parts[i].indexOf('<');

      lemmas[i] = parts[i].substring(0, beginTags);
      
      if (lemmasToIgnore.contains(lemmas[i]))
        lemmas[i] = null;

      String tg = parts[i].substring(beginTags, parts[i].length());

      tg = tg.replaceAll("><", "/");
      tg = tg.substring(1, tg.length() - 1);
      String[] tagparts = tg.split("/");

      tags[i] = new String[tagparts.length];
      for (int j = 0; j < tagparts.length; j++) {
        tags[i][j] = tagparts[j];
        
        if (tagsToIgnore.contains(tags[i][j]))
          tags[i][j] = null;
        else {
          if (renameTags.containsKey(tags[i][j]))
            tags[i][j]=renameTags.get(tags[i][j]);
        }
      }
    }
  }
  
  /**
   * Get the size, number of lemmas, in this Apertium lexical form.
   */
  public int size() {
    return lemmas.length;
  }

  /**
   * Get the (not-ignored) lemmas in this lexical form
   */
  public String[] getLemmas() {
    return lemmas;
  }

  /**
   * Get the (not-ignored) tags in this lexical form
   */
  public String[] getTags(int i) {
    return tags[i];
  }
  
  /** 
   * Return a string representing all (not-ignored) tags in the i-th position
   */ 
  public String getStringTags(int i) {
    StringBuilder str = new StringBuilder(32);
    for (int j=0; j<tags[i].length; j++) {
      if (tags[i][j] != null) {
        if (str.length()>0)
          str.append(".");
        str.append(tags[i][j]);
      }
    }
    
    return str.toString();
  }

 /** 
   * Return an array of strings in which each string represent
   * (not-ignored) tags in the i-th position. Each string shows a
   * different level of analysis, for instance, for the tags
   * "verb.present" this method returns two strings: "verb.present"
   * and "verb"
   */ 
  public String[] getStringTagsByAnalysisLevel(int i) {
    String strtags = getStringTags(i);
    StringBuilder str = new StringBuilder(32);

    //Note that when tags are renamed some of them can contain a dot
    //(".") and be at the same time in the same tags slot

    String[] arraystr = strtags.split("[.]");
    ArrayList<String> list = new ArrayList<String>();
    for (int j=0; j<arraystr.length; j++) {
      if (str.length()>0)
        str.append(".");
      str.append(arraystr[j]);
      list.add(str.toString());
    }

    return list.toArray(new String[0]);
  }
}
