package org.apache.lucene.apertium.tools;

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
import org.apache.lucene.analysis.ApertiumTokenizer;
import org.apache.lucene.apertium.ApertiumWord;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.Analyzer;

import java.io.File;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

import java.lang.StringBuilder;

import java.util.zip.GZIPInputStream;
import java.util.Date;
import java.util.Iterator;

/**
 *  Class used to index new documents in the <a
 *  href="http://www.apertium.org">Apertium</a> text-based
 *  intermediate format.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */

public class Indexer {

  private boolean gzipped;
  private File stopWords;
  private File stopTags;
  private File stopLemmas;
  private File renameTags;

  private static String reader2string(Reader reader) throws IOException{
    StringBuilder fileData = new StringBuilder(1048576);
    char[] buf = new char[1024];
    int numRead=0;
    while((numRead=reader.read(buf)) != -1){
      fileData.append(buf, 0, numRead);
    }
    return fileData.toString();
  }

  /** 
   * @param gz flag to indicate whether the files to index are gzipped
   * or not
   * @param stopWordsFile file containing one word per line to ignore
   * while indexing
   * @param stopTagsFile file containing one PoS tag per line to
   * ignore while indexing
   * @param stopLemmasFile file containing one lemma per line to
   * ignore while indeixng
   * @param renameTagsFile File containing in each line a pair of
   * string, the fist one will be substituted by the second one
   */
  public Indexer(boolean gz, File stopWordsFile, File stopTagsFile, File stopLemmasFile,   File renameTagsFile) {
    gzipped = gz;
    stopWords = stopWordsFile;
    stopTags = stopTagsFile;
    stopLemmas = stopLemmasFile;
    renameTags = renameTagsFile;
  }

  private int index(String indexdir, String datadir) throws IOException {
    File indexDir = new File(indexdir);
    File dataDir = new File(datadir);

    if (!dataDir.exists() || !dataDir.isDirectory()) {
      throw new IOException(datadir + " does not exist or is not a directory");
    }

    IndexWriter writer;
    ApertiumAnalyzer analyzer = new ApertiumAnalyzer(stopWords, stopTags, stopLemmas, renameTags);
    writer = new IndexWriter(indexDir, analyzer, true);
    writer.setUseCompoundFile(false);
    writer.setMaxFieldLength(1000000); // default was 10000

    indexDirectory(writer, dataDir);

    int numIndexed = writer.docCount();
    writer.optimize();
    writer.close();

    //Return the number of indexed documents
    return numIndexed;
  }

  private void indexDirectory(IndexWriter writer, File dir) throws IOException {
    File[] files = dir.listFiles();
    
    String ending;
    if (gzipped)
      ending=".txt.pos.gz";
    else
      ending=".txt.pos";
      
    // All files found in the directory are indexed
    for (int i = 0; i < files.length; i++) {
      File f = files[i];
      if (f.isDirectory()) {
        indexDirectory(writer, f); // recurse
      } else if (f.getName().endsWith(ending)) {
        indexFile(writer, f);
      }
    }
  }

  private void indexFile(IndexWriter writer, File f) throws IOException {
    if (f.isHidden() || !f.exists() || !f.canRead()) {
      return;
    }
    
    System.out.println("Indexing '" + f.getCanonicalPath() +"'");

    Document doc = new Document();
    Reader reader;

    if (gzipped)
      reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))));
    else
      reader = new BufferedReader(new FileReader(f));

    String content_string_apertium=reader2string(reader);
    String content_string=ApertiumWord.onlySuperficialForms(content_string_apertium); //content_string contains only the superficial forms in content_string_apertium
    
    reader.close();
    
    doc.add(new Field("contents", content_string, Field.Store.COMPRESS, Field.Index.TOKENIZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
    doc.add(new Field("name", f.getCanonicalPath().replace(".txt.pos", ".txt"), Field.Store.YES, Field.Index.NO));
    
    ((ApertiumAnalyzer) writer.getAnalyzer()).setStringActuallyAnalyzed(content_string_apertium);
    writer.addDocument(doc);
  }

  private static String usage() {
    String usage = "Usage:\n\tjava " + Indexer.class.getName();
    usage += " --index|-i indexdir --data|-d datadir [--stopwords|-w file] [--stoptags|-t file] [--stoplemmas|-l file] [--renametags|-r file] [--gzip|-z] [--help|-h]";
    return usage;
  }

  private static String getParamValue(String[] args, int i) {
    if (i >= args.length) {
      System.err.println("Error: wrong number of parameters");
      System.out.println(usage());
      System.exit(1);
    } 

    return args[i];   
  }

  public static void main(String[] args) throws IOException {
    boolean gzipped = false;
    String indexdir = "";
    String datadir = "";

    File stopwordsFile = null;
    File stoptagsFile = null;
    File stoplemmasFile = null;
    File renametagsFile = null;

    String str;

    for (int i=0; i<args.length; i++) {
      if (args[i].equals("--index") || args[i].equals("-i"))
        indexdir = getParamValue(args, ++i);

      else if (args[i].equals("--data") || args[i].equals("-d"))
        datadir = getParamValue(args, ++i);

      else if (args[i].equals("--stopwords") || args[i].equals("-w")) {
        str = getParamValue(args, ++i);
        stopwordsFile = new File(str);
        if (!stopwordsFile.exists() || stopwordsFile.isDirectory()) {
          System.err.println("Error: stopword file '" + str + "' does not exit or is not a regular file");
          System.exit(1);
        }
      } 

      else if (args[i].equals("--stoptags") || args[i].equals("-t")) {
        str = getParamValue(args, ++i);
        stoptagsFile = new File(str);
        if (!stoptagsFile.exists() || stoptagsFile.isDirectory()) {
          System.err.println("Error: stoptags file '" + str + "' does not exit or is not a regular file");
          System.exit(1);
        }
      }
 
      else if (args[i].equals("--stoplemmas") || args[i].equals("-l")) {
        str = getParamValue(args, ++i);
        stoplemmasFile = new File(str);
        if (!stoplemmasFile.exists() || stoplemmasFile.isDirectory()) {
          System.err.println("Error: stoplemmas file '" + str + "' does not exit or is not a regular file");
          System.exit(1);
        }
      } 

      else if (args[i].equals("--renametags") || args[i].equals("-r")) {
        str = getParamValue(args, ++i);
        renametagsFile = new File(str);
        if (!renametagsFile.exists() || renametagsFile.isDirectory()) {
          System.err.println("Error: renametags file '" + str + "' does not exit or is not a regular file");
          System.exit(1);
        }
      }

      else if (args[i].equals("--gzip") || args[i].equals("-z"))
        gzipped=true;

      else if (args[i].equals("--help") || args[i].equals("-h")) {
        System.out.println(usage());
        System.exit(0);
      }

      else {
        System.err.println("Error: Unknown option '" + args[i] + "'");      
        System.out.println(usage());
        System.exit(0);
      }
    }

    if (indexdir.length() == 0) {
      System.err.println("Error: No index directory was given, use --index to do that");
      System.err.println(usage());
      System.exit(1);
    }

    if (datadir.length() == 0) {
      System.err.println("Error: No data directory to index was given, use --data to do that");
      System.err.println(usage());
      System.exit(1);
    }    

    Indexer indexer = new Indexer(gzipped, stopwordsFile, stoptagsFile, stoplemmasFile, renametagsFile);

    long start = new Date().getTime();
    int numIndexed = indexer.index(indexdir, datadir);
    long end = new Date().getTime();

    System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds");


    //Print out the set of different tags found while indexing
    Iterator<String> itags = ApertiumTokenizer.getTagsFound().iterator();
    while(itags.hasNext()) {
      System.err.println(itags.next());
    }
  }
}
