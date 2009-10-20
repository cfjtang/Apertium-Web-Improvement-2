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
#include "alignment.h"
#include "utils.h"

#include <cstdlib>
#include <iostream>
#include <cmath>
#include <algorithm>

#include "utf_converter.h"

bool Alignment::debug;
vector<MarkerWord> Alignment::source_marker_words;
vector<MarkerWord> Alignment::target_marker_words;
vector<string> Alignment::forbidden_strings;
struct Alignment::statistics Alignment::stats;
map<string, map<string, double> > Alignment::lexprob;

Alignment::Alignment() {
}

Alignment::Alignment(const string& al) {
  vector<string> v;
  vector<string> alig;

  v=Utils::split_string(al, " ||| ");

  //if (v.size()==(unsigned)(nfields-1)) { //The alignment information is missing
  //  cerr<<"Warning: Following alignment has all words unaligned: '"<<al<<"'\n";
  //  v.push_back("");
  //}

  if (v.size()!=4) {
    cerr<<"Error in Alignment::Alignment when reading alignment from string '"<<al<<"'\n";
    cerr<<"Unespected number of fields separated by ' ||| '\n";
    cerr<<"Num. fields: "<<v.size()<<"\n";
    for (unsigned i=0; i<v.size(); i++)
      cerr<<"v["<<i<<"]=+"<<v[i]<<"+\n";

    exit(EXIT_FAILURE); 
  }

  vector<string> scores=Utils::split_string(v[0]," ");
  if(scores.size()==1) {
    count=atoi(Utils::trim(scores[0]).c_str()); 
    score=-numeric_limits<double>::max();
    prob_ts=-numeric_limits<double>::max();
  } else if (scores.size()==3) {
    count=atoi(Utils::trim(scores[0]).c_str()); 
    score=atof(Utils::trim(scores[1]).c_str()); 
    prob_ts=atof(Utils::trim(scores[2]).c_str()); 
  } else {
    cerr<<"Error in Alignment::Alignment when reading scores from string '"<<v[0]<<"'\n";
    cerr<<"Unexpected number of scores\n";
    exit(EXIT_FAILURE);
  }

  source_str=Utils::trim(v[1]);
  source=Utils::split_string(source_str, " ");

  target_str=Utils::trim(v[2]);
  target=Utils::split_string(target_str, " ");

  alignment_str=Utils::trim(v[3]);
  alig=Utils::split_string(alignment_str, " ");

  for(unsigned i=0; i<alig.size(); i++) {
    vector<string> an_alig;

    an_alig=Utils::split_string(alig[i], "-");
    if (an_alig.size()!=2) {
      cerr<<"Error in Alignment::Alignment when reading alignment from string '"<<al<<"'\n";
      cerr<<"Unexpected number of alignment values separated by '-'\n";
      exit(EXIT_FAILURE);
    }
    alignment[atoi(an_alig[0].c_str())][atoi(an_alig[1].c_str())]=true;
  }
}

Alignment::Alignment(const Alignment& al) {
  count=al.count;
  score=al.score;
  prob_ts=al.prob_ts;

  source=al.source;
  target=al.target;

  source_str=al.source_str;
  target_str=al.target_str;

  alignment=al.alignment;
  alignment_str=al.alignment_str;

  source_marker_words=al.source_marker_words;
  target_marker_words=al.target_marker_words;
}

void 
Alignment::read_marker_words(istream& is, vector<MarkerWord>& vector_marker_words) {

  while (!is.eof()) {
    string str;
    getline(is, str);

    if (str.length()>0) {
      MarkerWord marker_word(str);
      vector_marker_words.push_back(marker_word);
    }
  }
}

void 
Alignment::init_stats() {

  stats.nchunks=0;
  stats.ndis=0;
  stats.ndis_count=0;
  stats.ndis_ratio=0;
  stats.ndis_open=0;
  stats.ndis_forbid=0;
}

void 
Alignment::print_stats() {

  cerr<<"# chunks:     "<<stats.nchunks<<"\n";
  cerr<<"# dis:        "<<stats.ndis<<"\n";
  cerr<<"% dis:        "<<(((double)stats.ndis)/((double)stats.nchunks))*100<<"\n";
  cerr<<"# dis count:  "<<stats.ndis_count<<" ("<<(((double)stats.ndis_count)/((double)stats.ndis))*100<<" %)\n";
  cerr<<"# dis ratio:  "<<stats.ndis_ratio<<" ("<<(((double)stats.ndis_ratio)/((double)stats.ndis))*100<<" %)\n";
  cerr<<"# dis open:   "<<stats.ndis_open<<" ("<<(((double)stats.ndis_open)/((double)stats.ndis))*100<<" %)\n";
  cerr<<"# dis forbid: "<<stats.ndis_forbid<<" ("<<(((double)stats.ndis_forbid)/((double)stats.ndis))*100<<" %)\n";
}

void
Alignment::read_forbidden_strings(istream &is) {

  while (!is.eof()) {
    string str;
    getline(is, str);

    if (str.length()>0) 
      forbidden_strings.push_back(str);
  }
}

bool 
Alignment::is_marker_word(const vector<MarkerWord> &vector_marker_words, const vector<string> &vector_words, int n, int &numwords) {

  for(unsigned i=0; i<vector_marker_words.size(); i++) {
    if (vector_marker_words[i].equals(vector_words, n, numwords))
      return true;
  }

  return false;
}

bool 
Alignment::is_word (const string &str) {
  //Convert to wstring
  wstring wstr=UtfConverter::fromUtf8(str);

  //At least one letter
  bool oneletter=false;
  for(unsigned i=0; (i<wstr.size()) && (!oneletter); i++) {
    if (iswalpha(wstr[i]))
      oneletter=true;
  }

  //No numbers
  bool onedigit=false;
  for(unsigned i=0; (i<wstr.size()) && (!onedigit); i++) {
    if (iswdigit(wstr[i]))
      onedigit=true;
  }

  return ((oneletter)&&(!onedigit));
}

bool
Alignment::open_words_aligned() {

  map<int, bool> open_source_words;
  map<int, bool> open_target_words;

  for(unsigned i=0; i<source.size(); i++) {
    int numwords;
    if (is_marker_word(source_marker_words, source, i, numwords))
      i+=numwords;
    else 
      open_source_words[i]=true;
  }

  for(unsigned i=0; i<target.size(); i++) {
    int numwords;
    if (is_marker_word(target_marker_words, target, i, numwords))
      i+=numwords;
    else 
      open_target_words[i]=true;
  }

  //Each open SL word must be aligned with _at least_ one open TL word
  for(unsigned i=0; i<source.size(); i++) {
    if (open_source_words[i]) {
      bool aligned=false;
      for (unsigned j=0; (j<target.size()) && (!aligned); j++) {
	if ((open_target_words[j]) && (alignment[i][j]))
          aligned=true;	
      }
      if (!aligned)
        return false;
    }
  }

  //Each open TL word must be aligned with _at least_ one open SL word
  for(unsigned j=0; j<target.size(); j++) {
    if (open_target_words[j]) {
      bool aligned=false;
      for (unsigned i=0; (i<source.size()) && (!aligned); i++) {
	if ((open_source_words[i]) && (alignment[i][j]))
          aligned=true;	
      }
      if (!aligned)
        return false;
    }
  }

  return true;
}

bool 
Alignment::pass_filter_test(bool check_open_words, bool check_words, int min_count, double ratio) {
  bool ret_value=true;

  stats.nchunks++;

  //Check number of ocurrences
  if (count<min_count) {
    ret_value=false;
    stats.ndis_count++;
  }

  //Check ratio of source-target words
  double r;
  if (source.size()>target.size())
    r=((double)source.size())/((double)target.size());
  else
    r=((double)target.size())/((double)source.size());
  if(r>ratio) {
    ret_value=false;
    stats.ndis_ratio++;
  }

  //Check that there are not fobidden strings in either side (source and target)
  bool cont=true;
  for(unsigned i=0; (i<forbidden_strings.size()) && cont; i++) {
    for(unsigned j=0; (j<source.size()) && cont; j++) {
      if (source[j] == forbidden_strings[i]) {
        ret_value=false;
        stats.ndis_forbid++;
        cont=false;
      }
    }
    for(unsigned j=0; (j<target.size()) && cont; j++) {
      if (target[j] == forbidden_strings[i]) {
        ret_value=false;
        stats.ndis_forbid++;
        cont=false;
      }
    }
  }

  //Check open words are aligned
  if (check_open_words) {
    if (!open_words_aligned()) {
      stats.ndis_open++;
      ret_value=false;
    }
  }

  cont=true;
  if (check_words) {
    for(unsigned i=0; (i<source.size()) && cont; i++) {
      if (!is_word(source[i])) {
        cont=false;
        ret_value=false;
      }
    }
    for(unsigned i=0; (i<target.size()) && cont; i++) {
      if (!is_word(target[i])) {
        cont=false;
        ret_value=false;
      }
    }
  }

  if (!ret_value)
    stats.ndis++;

  return ret_value;
}

void
Alignment::score_alignments(vector<Alignment>& setalig, int total_count) {
  int sum_count=0;

  for(unsigned i=0; i<setalig.size(); i++) {
    sum_count+=setalig[i].count;
  }

  //The score refers to the SL part of the chunk. All chunks with the
  //same SL part will have the same score
  double sl_score=log(((double)sum_count)/((double)total_count));

  for(unsigned i=0; i<setalig.size(); i++) {
    setalig[i].score = sl_score;
    setalig[i].prob_ts = log(((double)setalig[i].count)/((double)sum_count));
  }

  AlignmentGreaterThanByProb_ts comparer;
  sort(setalig.begin(), setalig.end(), comparer); //Most probable chunks will appear first
}

void
Alignment::read_lexicon (istream &is) {

  while (!is.eof()) {
    string str;
    getline(is, str);
    if (str.length()>0) {
      vector<string> fields;
      fields = Utils::split_string(str, " ");
      lexprob[fields[0]][fields[1]]=atof(fields[2].c_str());
    }
  }
}

bool
Alignment::pass_lexprob_filter_test(double min_prob) {

  unsigned count_below_min=0;
  for(unsigned j=0; j<target.size(); j++) {
    bool above_min=false;
    for(unsigned i=0; (i<source.size()) && (!above_min); i++) {
      if ((alignment[i][j]) && (lexprob[target[j]][source[i]]>=min_prob)) {
        above_min=true;
      }
    }
    if (!above_min) count_below_min++;
  }

  //return (count_below_min!=target.size());
  return (count_below_min==0);
}

ostream& operator<<(ostream& os, Alignment& al) {

  os<<al.count<<" ";
  if (al.score!=-numeric_limits<double>::max())
    os<<al.score<<" ";
  if (al.prob_ts!=-numeric_limits<double>::max())
    os<<al.prob_ts<<" ";

  os<<"|||";

  for(unsigned i=0; i<al.source.size(); i++) {
    os<<" "<<al.source[i];
  }
  os<<" |||";

  for(unsigned i=0; i<al.target.size(); i++) 
    os<<" "<<al.target[i];
  os<<" |||";

  for (unsigned i=0; i<al.source.size(); i++) {
    for(unsigned j=0; j<al.target.size(); j++) {
      if (al.alignment[i][j])
	os<<" "<<i<<"-"<<j;
    }
  }

  return os;
}

//////////////////////////////////////////////////////////////

void 
Alignment::switch_alignment() {
  vector<string> aux;

  unsigned source_size = source.size();
  unsigned target_size = target.size();

  aux = source;
  source = target;
  target = aux;

  map<int, map<int, bool> > new_alig;

  for(unsigned i=0; i<source_size; i++) {
    for(unsigned j=0; j<target_size; j++) {
      new_alig[j][i]=alignment[i][j];
    }
  }

  alignment=new_alig;
}

bool 
Alignment::are_the_same_alignment(const Alignment& al2) {
  bool ok=true;

  if ((source.size()!=al2.source.size())||
      (target.size()!=al2.target.size())) {
    ok=false;
  }

  for(unsigned i=0; (i<source.size()) && (ok); i++) {
    if (source[i]!=al2.source[i])
      ok=false;
  }

  for(unsigned i=0; (i<target.size()) && (ok); i++) {
    if (target[i]!=al2.target[i])
      ok=false;
  }
  return ok;
}

bool 
Alignment::intersection(Alignment& al2) {
  if (!are_the_same_alignment(al2)) {
    cerr<<"Error when intersecting the following two alignments:\n";
    cerr<<*this<<"\n";
    cerr<<al2<<"\n";
    return false;
  }

  for(unsigned i=0; i<source.size(); i++) {
    for(unsigned j=0; j<target.size(); j++) {
      if ((alignment[i][j]) && (!al2.alignment[i][j])) {
        alignment[i][j]=false;
      }
    }
  }

  return true;
}

bool
Alignment::unionn(Alignment& al2) {
  if (!are_the_same_alignment(al2)) {
    cerr<<"Error when uniting the following two alignments:\n";
    cerr<<*this<<"\n";
    cerr<<al2<<"\n";
    return false;
  }

  for(unsigned i=0; i<source.size(); i++) {
    for(unsigned j=0; j<target.size(); j++) {
      if (al2.alignment[i][j]) {
        alignment[i][j]=true;
      }
    }
  }

  return true;
}

bool 
Alignment::refined_intersection(Alignment& al2) {
  if (!are_the_same_alignment(al2)) {
    cerr<<"Error when performing the refined intersection of the following two alignments:\n";
    cerr<<*this<<"\n";
    cerr<<al2<<"\n";
    return false;
  }

  //We save the alignment of (*this) before intersecting
  map<int, map<int, bool> > al1;
  for(unsigned i=0; i<source.size(); i++) {
    for(unsigned j=0; j<target.size(); j++) {
      al1[i][j]=alignment[i][j];
    }
  }

  //First, the intersection
  intersection(al2);
  //cerr<<"(0) "<<to_string()<<"\n";
  bool alignments_changed;
  //int nit=0;
  do {
    alignments_changed=false;
    //nit++;
    for (unsigned i=0; i<source.size(); i++) {
      for(unsigned j=0; j<target.size(); j++) {
        if (!alignment[i][j]) {
          if ((al1[i][j]) || (al2.alignment[i][j])) {
            //cerr<<"   considering ("<<i<<","<<j<<")\n";
            bool add_alignment=true;
            for(unsigned k=0; k<target.size(); k++) {
              if (alignment[i][k])
                add_alignment=false;
            }
            for(unsigned k=0; k<source.size(); k++) {
              if (alignment[k][j])
                add_alignment=false;
            }
            if (!add_alignment) {
              //cerr<<"   ("<<i<<","<<"*) or (*,"<<j<<") are already in A\n";
              //The aligment can still be added if it has an horizontal
              //neighbor or a vertical neighbor already in 'alignment'
              if ( ((alignment[i-1][j])||(alignment[i+1][j])) ||
                   ((alignment[i][j-1])||(alignment[i][j+1])) ) {
                //cerr<<"   ("<<i<<","<<j<<") has an horizontal or a vertical neighbor in A\n";
                alignment[i][j]=true; 
                //Now we that test there is no aligments in 'alignment' with
                //both horizontal and vertical neighbors
                bool neighbors=false;
                for(unsigned ii=0; (ii<source.size()) && (!neighbors); ii++) {
                  for(unsigned jj=0; (jj<target.size()) && (!neighbors); jj++) {
                    if (alignment[ii][jj]) {
                      if ( ((alignment[ii-1][jj])||(alignment[ii+1][jj])) &&
                           ((alignment[ii][jj-1])||(alignment[ii][jj+1])) ) {
                        //cerr<<"   ("<<i<<","<<j<<") has both horizontal and vertical neighbors\n";
                        neighbors=true;
                      }
                    }
                  }
                }
                alignment[i][j]=false;
                if(!neighbors)
                  add_alignment=true;
              }
            }
            if (add_alignment) {
              //cerr<<"   adding ("<<i<<","<<j<<")\n";
              alignment[i][j]=true;
              alignments_changed=true;
            }
          }
        }
      }
    }
    //cerr<<"("<<nit<<") "<<to_string()<<"\n";
  } while(alignments_changed);

  return true;
}
