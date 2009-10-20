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
#ifndef __ALIGNMENT_H_
#define __ALIGNMENT_H_

using namespace std;

#include <vector>
#include <string>
#include <iostream>
#include <map>
#include <set>
#include <fstream>

#include "marker_word.h"

class Alignment {
private:
  int count; //Num. of occurrences
  double score; // log prob(source)
  double prob_ts; //log prob(target|source)

  vector<string> source;
  vector<string> target;

  static vector<MarkerWord> source_marker_words;
  static vector<MarkerWord> target_marker_words;
  static vector<string> forbidden_strings;

  //lexprob[target][source] = prob(target | source)
  static map<string, map<string, double> > lexprob;

  string source_str;
  string target_str;
  string alignment_str;

  //Matrix source.size x target.size with the alignments
  map<int, map<int, bool> > alignment;

  //Change source side by target side, and vice versa, updating the alignment matrix accordingly
  void switch_alignment();

  static void read_marker_words(istream& is, vector<MarkerWord>& vector_marker_words);

  //Returns true if the n-th word in vector_words is a marker word in vector_marker_words
  //In numwords the number of words after the n-th word that are also a marker-word is returned
  bool is_marker_word(const vector<MarkerWord> &vector_marker_words, const vector<string> &vector_words, int n, int &numwords);

  //Return true if the string contains at least one alphabetic character
  bool is_word (const string &str);

  bool open_words_aligned();

  struct statistics {
    int nchunks;
    int ndis;
    int ndis_count;
    int ndis_ratio;
    int ndis_open;
    int ndis_forbid;
  };

  static struct statistics stats;

public: 

  static bool debug;

  Alignment();

  Alignment(const string& al);

  Alignment(const Alignment& al);
    
  inline static void read_source_marker_words(istream &is) {read_marker_words(is, source_marker_words);}
  inline static void read_target_marker_words(istream &is) {read_marker_words(is, target_marker_words);}

  static void read_forbidden_strings(istream &is);

  static void init_stats();

  static void print_stats();

  static void read_lexicon(istream &is);

  bool pass_filter_test(bool check_open_words, bool check_words, int count, double ratio);

  bool pass_lexprob_filter_test(double min_prob);

  inline string get_source_str() {return source_str;};
  inline string get_target_str() {return target_str;};
  inline double get_count() {return count;};
  inline double get_score() {return score;};
  inline double get_prob_ts() {return prob_ts;};

  inline vector<string> get_source() {return source;};
  inline vector<string> get_target() {return target;};

  //All the alignments in setalig should have the same SL part
  static void score_alignments(vector<Alignment>& setalig, int total_count); //Use vector for efficiency, no duplicates will happen

  friend ostream& operator<<(ostream& os, Alignment& al);

  friend class AlignmentGreaterThanByProb_ts;

  //////////////////////////////////////////////////////////////////////////

  //True if source size and the target size of both alignments (*this
  //and al2) are equal
  bool are_the_same_alignment(const Alignment& al2);

  //(*this) becomes the intersected alignment
  bool intersection(Alignment& al2);

  //(*this) becomes the united alignment
  bool unionn(Alignment& al2);

  //(*this) becomes the refined intersected alignment
  bool refined_intersection(Alignment& al2);
};


//Definition of the compare function used to sort the
//alignments by their prob_ts
class AlignmentGreaterThanByProb_ts {
public:
  bool operator()(const Alignment &at1, const Alignment &at2)  const {
    return (at1.prob_ts>at2.prob_ts);
  }
};

#endif
