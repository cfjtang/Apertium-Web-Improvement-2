package org.apache.lucene.benckmark.byTask.feeds;

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
import org.apache.lucene.analysis.WhitespaceAnalyzer;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

import org.apache.lucene.benchmark.byTask.feeds.AbstractQueryMaker;
import org.apache.lucene.benchmark.byTask.feeds.QueryMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A QueryMaker that makes queries devised manually for searching 
 * using linguistic information.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */
public class ApertiumQueryMaker extends AbstractQueryMaker implements QueryMaker {

  private static String [] STANDARD_QUERIES = {
    //Start with some short queries
    "lem:gato", "lem:coche", "tags:vblex.inf", "de", "lem:parlamento",
    "lem:titular lem:finanza", "lem:educación",
    "lem:titular finanzas",
    //Try some phrase queries
    "\"lem:deber de tags:vblex.inf\"", 
    "\"lem:estar a punto de\"",
    "\"lem:comida tags:vblex.pri\"~3",    
    "(\"lem:comida tags:vblex.pri\"~3) OR (\"lem:comida tags:vblex.cni\"~3)",    
    "\"banco mundial\"^2 AND tags:np.loc", 
    "\"lem:banco lem:mundial\" -tags:np.loc",
    "\"lem:gato lem:coche\"~5",
    "\"lem:gato lem:árbol\"~5",
    //Try some longer queries
    "lem:línea lem:aéreo tags:np.loc lem:destino tags:np.loc",
    "tags:np.ant tags:np.ant no lem:dimitir ni lem:adelantar tags:det.def lem:elección",
    "\"tags:np.ant tags:np.ant tags:adv tags:vblex.fti tags:cnjcoo tags:vblex.fti tags:det.def tags:n\"",
  };
  
  private static Term[][] TERMS_MULTI_PHRASE_QUERY = {
    {new Term("contents", "lem:tener"), new Term("contents", "lem:deber")},  
    {new Term("contents", "que"), new Term("contents", "de")},
    {new Term("contents", "tags:vblex.inf"), new Term("contents", "tags:vbmod.inf"),
      new Term("contents", "tags:vbhaver.inf"), new Term("contents", "tags:vbser.inf")},
  };
  
  private static Query[] getPrebuiltQueries(String field) {
    
    MultiPhraseQuery mphq = new MultiPhraseQuery();
    for (int i=0; i<TERMS_MULTI_PHRASE_QUERY.length; i++)
      mphq.add(TERMS_MULTI_PHRASE_QUERY[i]);
    
    //  be wary of unanalyzed text
    return new Query[] {
        new SpanFirstQuery(new SpanTermQuery(new Term(field, "tags:np.loc")), 5),
        new SpanNearQuery(new SpanQuery[]{new SpanTermQuery(new Term(field, "lem:noche")), new SpanTermQuery(new Term(field, "lem:comercio"))}, 4, false),
        new SpanNearQuery(new SpanQuery[]{new SpanFirstQuery(new SpanTermQuery(new Term(field, "lem:banco")), 10), new SpanTermQuery(new Term(field, "crédito"))}, 10, false),
        //new WildcardQuery(new Term(field, "lem:ro*")),
        mphq,
    };
  }
  
  /**
   * Parse the strings containing Lucene queries.
   */
  private static List<Query> parseQueries(List<String> qs, Analyzer a) {
    QueryParser qp = new QueryParser("contents", a);
    List<Query> queries = new ArrayList<Query>();
    
    for (int i=0; i < qs.size(); i++)  {
      try {
        String query = qs.get(i);
        Query q = qp.parse(query);

        if (q != null)
          queries.add(q);

      } catch (Exception e)  {
        e.printStackTrace();
      }
    }

    return queries;
  }
  
  protected Query[] prepareQueries() throws Exception {   
    //We ignore the analyzer specified as it must be used only for indexing
    Analyzer anlzr = new WhitespaceAnalyzer();
    
    List<Query> queryList;
    queryList = parseQueries(Arrays.asList(STANDARD_QUERIES), anlzr);
    queryList.addAll(Arrays.asList(getPrebuiltQueries("contents")));
    
    return queryList.toArray(new Query[0]);
  }
  
  //This is only for use outside the standard benchmark framework of Lucene
  private Query[] queries;
  private int inext;
  public Query nextQuery() throws Exception {
    if (queries == null) {
      queries = prepareQueries();
      inext=0;
    } else {
      inext = ++inext % queries.length;
    }

    return queries[inext];
  }
}
