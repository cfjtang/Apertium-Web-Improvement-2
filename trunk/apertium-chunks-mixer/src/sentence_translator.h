/*
 * Copyright (C) 2009 Universitat d'Alacant / Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
#ifndef __SENTENCE_TRANSLATOR_H_
#define __SENTENCE_TRANSLATOR_H_

using namespace std;

#include <string>
#include <vector>
#include <iostream>
#include <cmath>

#include "alignment.h"
#include "my_trie.h"
#include "language_model.h"

#define SC_NCHUNKS           0
#define SC_NCHUNKS_WITHOUT   1
#define SC_SCORE             2
#define SC_SCORE_WITHOUT     3

class SentenceTranslator {
 private:
  //Contains sets of chunks, all chunks in the same set share the same SL part
  //For eficiency the set is implemented with a vector
  //No duplicate alignments will occur in the input files
  vector<vector<Alignment> > chunks_collection;

  //Contains SL parts of chunks; the key will be used to access to chunks_collection
  MyTrie<int, string> trie;

  unsigned max_words_chunk;

  //Returns next word in the first element of the pair, and the format after it in the second element
  pair<string, string> next_word(istream &ins);

  //Returns next apertium word in the first element of the pair, and the format after it in the second element
  //Apertium words are of the form ^lemma<tag1><tag2><..>$
  pair<string, string> next_apertium_word(istream &ins);

  //Return the best cover togther with its score
  pair<double, vector<MyTrie<int,string>*> > best_cover(istream &ins, vector<pair<string,string> > &source);

  //Return the best cover togther with its score (num of chunks covering the input text)
  pair<double[4], vector<MyTrie<int,string>*> > best_cover_num_chunks(istream &ins, vector<pair<string,string> > &source, bool min_nchunks);

  string langmodel_score(LanguageModel &lm, const vector<vector<string> > &segments, vector<int> &best_combination);

  double defscore;

  bool use_pspts;

  struct statistics {
    map<int,int> chunks_applied; //Counts the number of application of a given chunk
    int napp;
    int words_covered;
    int words_not_covered;

    //When using a LM, number of times a translation from the chunk is used
    map<int,int> chunks_actually_applied; 

    //When using a LM, number of times the trasnlation in the correspondig index is used
    //translation_used[0] = the translation of apertium is used
    //translation_used[1] = the first TL part is used
    // ...
    map<int, int> translation_used;

    int words_actually_covered;
    int actual_napp;

    map<int,int> chunks_actually_applied_apertium;
    int actual_napp_apertium;
    int words_actually_covered_apertium;

    //Statistics about LM application
    int nsentences;
    int ngreedy;
  } stats;

 public: 

  static bool debug;

  inline void set_default_score (double s) {defscore=log(s);} // We use log prob

  inline void set_use_pspts (bool b) {use_pspts=b;}

  SentenceTranslator(istream &ischunks);

  void translate(istream &ins);

  void detect_chunks(istream &ins);

  void replace_chunks(istream &ins);

  void score_replace_chunks(LanguageModel &lm, istream &ins);

  void score_replace_chunks_greedy(LanguageModel &lm, istream &ins);

  void print_stats();

};

#endif
