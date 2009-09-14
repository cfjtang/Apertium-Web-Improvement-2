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

import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.LinguisticQueryAnalyzer;
import org.apache.lucene.analysis.PositionsAccordingToOffsetsFilter;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.FilterIndexReader;
import org.apache.lucene.index.TermPositionVector;

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;

import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.highlight.SpanScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;

import org.apache.lucene.search.highlight.SentenceFragmenter;

import java.io.File;
import java.io.IOException;

import java.util.Date;

/**
 *  Class used to search for documents by using morphlogical
 *  information. It provides an example of how to use the {@link
 *  LinguisticQueryAnalyzer}.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */

public class Searcher {

  private IndexReader ireader;
  private int nhits;
  private File stopWords;
  private File stopTags;
  private File stopLemmas;

  public Searcher(String indexdir, int n, File stopWordsFile, File stopTagsFile, File stopLemmasFile) throws IOException {

    File indexDir = new File(indexdir);

    if (!indexDir.exists() || !indexDir.isDirectory()) {
      throw new IOException(indexDir + " does not exist or is not a directory.");
    }
    
    ireader = new FilterIndexReader(IndexReader.open(FSDirectory.getDirectory(indexDir)));
    nhits = n;

    stopWords = stopWordsFile;
    stopTags = stopTagsFile;
    stopLemmas = stopLemmasFile;
  }

  /**
   * Gets a query in the Lucene query language and performs the
   * search. In the query one can use the following prefixes to
   * specify query terms using linguistic information.<br/><br/>
   * 
   * 'lem#' for lemmas<br/>
   * 'tags#' for tags (morphological attributes)<br/><br/>
   *
   * Example of query:
   *     "lem#airline with lem#destination tags#noun.location"
   */
  public void search(String q) throws IOException {
    Query query = null;
    QueryParser qparser = null;

    qparser = new QueryParser("contents", new LinguisticQueryAnalyzer(stopWords, stopTags, stopLemmas));

    try {
      query = qparser.parse(q);
      System.err.println("Query: " + q);
      System.err.println("Analyzed query: " + query.toString());
      System.err.println("Class Query:" + query.getClass().getName());
      query = query.rewrite(ireader);
      System.err.println("Rewritten query: " + query.toString());
      System.err.println("Class Query:" + query.getClass().getName());
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
    
    Hits hits = new IndexSearcher(ireader).search(query);

    System.err.println("Total hists: " + hits.length());

    System.out.println("<html><body>"); 

    for (int i=0; (i<=nhits) && (i<hits.length()); i++) {
      String name = hits.doc(i).get("name");
      String contents = hits.doc(i).get("contents");


      PositionsAccordingToOffsetsFilter posOffFilter = new PositionsAccordingToOffsetsFilter(TokenSources.getTokenStream((TermPositionVector)ireader.getTermFreqVector(hits.id(i), "contents")));
      CachingTokenFilter stream = new CachingTokenFilter(posOffFilter);
      Highlighter highlighter = new Highlighter(new SpanScorer(query, "contents", stream));

      highlighter.setTextFragmenter(new SentenceFragmenter());

      stream.reset();
      posOffFilter.reset();

      String[] fragments = highlighter.getBestFragments(stream, contents, 5);

      System.out.println("<p><u>");
      System.out.println(name);
      System.out.println("</u></p>");
      for (int j=0; j<fragments.length; j++) {
        System.out.println("<p>");
        System.out.println("... " + fragments[j] + " ...");
        System.out.println("</p>");
      }
    }

    System.out.println("</body></html>"); 

    ireader.close();
  }

  private static String usage() {
    String usage = "Usage:\n\tjava " + Searcher.class.getName();
    usage += " --index|-i indexdir --nhits|-n number [--stopwords|-w file] [--stoptags|-t file] [--stoplemmas|-l file] [--help|-h] --query|-q query\n";
    usage += "NOTE: query must be in the standard lucene query language.\n";
    usage += "      --query must be *the last* argument to the program.";
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
    String indexdir = "";
    String luceneQuery = "";

    File stopwordsFile = null;
    File stoptagsFile = null;
    File stoplemmasFile = null;
    int n = 0;
    String str;

    for(int i=0; i<args.length; i++) {
      if (args[i].equals("--index") || args[i].equals("-i"))
        indexdir = getParamValue(args, ++i);

      else if (args[i].equals("--nhits") || args[i].equals("-n"))
        n = Integer.parseInt(getParamValue(args, ++i));

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

      else if (args[i].equals("--query") || args[i].equals("-q")) {
        StringBuilder str2 = new StringBuilder(2048);
        for (int j=++i; i<args.length; i++) {
          if(str2.length()>0)
            str2.append(" ");
          str2.append(args[i]);
        }
        luceneQuery = str2.toString();        
      }

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

    if (luceneQuery.length() == 0) {
      System.err.println("Error: No query was given");
      System.err.println(usage());
      System.exit(1);
    }


    if (n == 0) {
      System.err.println("Error: The number of hits to return per query has not been provided, use --nhits to do that");
      System.err.println(usage());
      System.exit(1);
    }

    Searcher searcher = new Searcher(indexdir, n, stopwordsFile, stoptagsFile, stoplemmasFile);

    long start = new Date().getTime();
    searcher.search(luceneQuery);
    long end = new Date().getTime();

    System.err.println("Search took " + (end - start) + " milliseconds");    
  }
}
