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
#include "sentence_translator.h"

#include <map>
#include <algorithm>
#include <cstdlib>
#include <cmath>

#include "utils.h"

bool SentenceTranslator::debug=false;

SentenceTranslator::SentenceTranslator(istream &ischunks) {
  string source_str="";
  vector<string> source_vector;
  vector<Alignment> auxset;

  max_words_chunk=0;

  while (!ischunks.eof()) {
    string str_chunk;
    getline(ischunks, str_chunk);

    if (str_chunk.length() > 0) {
      Alignment chunk(str_chunk);
      if (source_str != chunk.get_source_str()) {
        if (auxset.size() > 0) {
          if (auxset[0].get_source().size()>max_words_chunk)
            max_words_chunk=auxset[0].get_source().size();

          chunks_collection.push_back(auxset);
          int key=chunks_collection.size()-1;
          trie.insert(key, source_vector);
          auxset.clear();
        }

        source_str=chunk.get_source_str();
        source_vector=chunk.get_source();
        auxset.push_back(chunk);
      } else
        auxset.push_back(chunk);
    }
  }

  if (auxset.size() > 0) {
    if (auxset[0].get_source().size()>max_words_chunk)
      max_words_chunk=auxset[0].get_source().size();

    chunks_collection.push_back(auxset);
    int key=chunks_collection.size()-1;
    trie.insert(key, source_vector);
    auxset.clear();
  }

  if(debug) {
    cerr<<"Content of the trie:\n-----------------------\n";
    cerr<<trie<<"\n\n";
  }

  cerr<<"Max words chunk: "<<max_words_chunk<<"\n";

  defscore=0;
  use_pspts=false;

  stats.words_covered=0;
  stats.words_not_covered=0;
  stats.napp=0;
  stats.words_actually_covered=0;
  stats.actual_napp=0;
  stats.actual_napp_apertium=0;
  stats.words_actually_covered_apertium=0;

  stats.nsentences=0;
  stats.ngreedy=0;
}

pair<string, string> 
SentenceTranslator::next_word(istream &ins) {
  pair<string, string> ret;
  char c;
  string word="";
  string format="";

  bool readmore=true;
  do {
    if (!ins.get(c).eof()) {
      if ((c==' ') || (c=='\n') || (c=='\t')) {
        ins.unget();
        readmore=false;
      } else
        word+=c;

    } else
      readmore=false;
  } while (readmore);

  readmore=true;
  do {
    if (!ins.get(c).eof()) {
      if ((c==' ') || (c=='\n') || (c=='\t'))
        format+=c;
      else {
        ins.unget();
        readmore=false;
      }
    } else
      readmore=false;
  } while (readmore);

  ret.first=word;
  ret.second=format;

  return ret;
}

pair<string, string> 
SentenceTranslator::next_apertium_word(istream &ins) {
  pair<string, string> ret;
  char c;
  string word="";
  string format="";

  //There may be blanks before the first apertium word (first call)
  if (!ins.get(c).eof()) {
    if (c!='^') {
      format+=c;
      while ((c!='^')&&(!ins.get(c).eof())) {
        if (c!='^')
          format+=c;
      }
      if (!ins.eof())
        ins.unget();
      ret.first=word;
      ret.second=format;
      return ret;
    } else 
      ins.unget();
  }

  bool readmore=true;
  do {
    if (!ins.get(c).eof()) {
      if ((c=='$')) {
        readmore=false;
        word+=c;
      } else
        word+=c;

    } else
      readmore=false;
  } while (readmore);

  readmore=true;
  do {
    if (!ins.get(c).eof()) {
      if (c!='^')
        format+=c;
      else {
        ins.unget();
        readmore=false;
      }
    } else
      readmore=false;
  } while (readmore);

  ret.first=word;
  ret.second=format;

  return ret;
}

pair<double, vector<MyTrie<int,string>*> >
SentenceTranslator::best_cover(istream &ins, vector<pair<string,string> > &source) {

  //For a given end position (map index) returns the best sequence of
  //states covering the input text till that end position - The very
  //head of the trie means word not covered
  map<int, pair<double, vector<MyTrie<int,string>*> > > covers;
  pair<double, vector<MyTrie<int,string>*> > emptyvector_states_sequence;

  covers[-1]=emptyvector_states_sequence;
  covers[-1].first=0; //prob=1; we use log prob

  vector<MyTrie<int, string>*> alive_states_clean;
  alive_states_clean.push_back(&trie); 

  //Initial state is always alive to allow to start the search at
  //every word in the input text
  vector<MyTrie<int, string>*> alive_states=alive_states_clean;

  //In source the words that have been read are returned
  source.clear();

  //We need to keep in memory (max_words_chunks+1) covers
  int ncovers=max_words_chunk+1;

  if(debug) {
    cerr<<"\n------------------------------\n-- New call to best_cover --\n------------------------------\n";
    cerr<<"states alive (init state): ";
    for(unsigned i_alive=0; i_alive<alive_states.size(); i_alive++) {
      MyTrie<int, string>* &italive = alive_states[i_alive];
      cerr<<"("<<italive->state_id;
      if (italive->has_key)
        cerr<<"#"<<italive->data_length;
      cerr<<") ";
    }
    cerr<<"\n";
  }

  int i=-1;
  bool readmore=true;
  while (readmore) {
    pair<string,string> word = next_word(ins);
    source.push_back(word);
    i++;

    if (debug) {
      cerr<<"word='"<<word.first<<"' format='"<<word.second<<"'\n";
      cerr<<"---------------------------------------\n";
    }

    vector<MyTrie<int, string>*> alive_states_new=alive_states_clean;

    pair<double, vector<MyTrie<int,string>*> > new_best_cover;
    new_best_cover.first = -numeric_limits<double>::max();

    for(unsigned i_alive=0; i_alive<alive_states.size(); i_alive++) {
      MyTrie<int, string>* &italive = alive_states[i_alive];


      string str_search=Utils::strtolower(word.first);
      if (italive->childs.find(str_search) != italive->childs.end()) {
        MyTrie<int,string> *this_state = italive->childs[str_search];

        alive_states_new.push_back(this_state);

        if (this_state->has_key) { //This state recognizes a string
          if (debug) {
            cerr<<"\tState "<<this_state->state_id<<" recognizes a string of length "<<this_state->data_length<<"\n";
            cerr<<"\tIt must be added to states sequence in cover["<<(i-(this_state->data_length))<<"]\n";
          }
          pair<double, vector<MyTrie<int,string>*> > newseq = covers[(i-(this_state->data_length))%ncovers];

          if (debug) {
            cerr<<"\tAdding state "<<this_state->state_id<<" to states sequence ( ";
            for(unsigned k=0; k<newseq.second.size(); k++)
              cerr<<newseq.second[k]->state_id<<" ";
            cerr<<"prevsc:"<<newseq.first<<")\n";
          }

          //There may be more than one target side for the same source side
          //Anyway, the score is the same for all chunks with the same SL part
          if (use_pspts) 
            newseq.first=newseq.first + (chunks_collection[this_state->key_value][0].get_score() + 
                                         chunks_collection[this_state->key_value][0].get_prob_ts()); //We use log prob
          else
            newseq.first=newseq.first + chunks_collection[this_state->key_value][0].get_score(); //We use log prob

          if(newseq.first>new_best_cover.first) {
            newseq.second.push_back(this_state);
            new_best_cover=newseq;
          }
        }
      }      
    }

    //We also add to the cover in the previous step the null state
    //(head of the Trie) which means word not covered. 
    pair<double, vector<MyTrie<int,string>*> > newseq = covers[(i-1)%ncovers];

    if (debug) {
      cerr<<"\tAdding state "<<trie.state_id<<" to states sequence ( ";
      for(unsigned k=0; k<newseq.second.size(); k++)
        cerr<<newseq.second[k]->state_id<<" ";
      cerr<<"sc:"<<newseq.first<<" )\n";
    }          

    newseq.first=newseq.first + defscore; //defscore is log prob
    if(newseq.first>new_best_cover.first) {
      newseq.second.push_back(&trie);
      new_best_cover=newseq;
    }

    alive_states=alive_states_new;
    covers[i%ncovers]=new_best_cover;
  
    if(debug) {
      cerr<<"states alive: ";
      for(unsigned i_alive=0; i_alive<alive_states.size(); i_alive++) {
        MyTrie<int, string>* &italive = alive_states[i_alive];
        cerr<<"("<<italive->state_id;
        if (italive->has_key)
          cerr<<"#"<<italive->data_length;
        cerr<<") ";
      }
      cerr<<"\n";

      cerr<<"covers\n";
      cerr<<"---------------------------------------\n";
      int j=i-ncovers+1; if (j<-1) j=-1;
      for(; j<=i; j++) {
        cerr<<j<<": ";
        cerr<<"( ";
        for(unsigned i_cover=0; i_cover<covers[j%ncovers].second.size(); i_cover++)
          cerr<<covers[j%ncovers].second[i_cover]->state_id<<" ";
        cerr<<"sc:"<<covers[j%ncovers].first<<" )\n";
      }
      cerr<<"\n";
    }

    if (alive_states.size()==1) {
      //No alive states apart from the initial state which is always alive
      if (debug) cerr<<"------------------------------\n----- No states alive -----\n------------------------------\n";
      readmore=false;
    }
  }

  return covers[(source.size()-1)%ncovers];
}

pair<double[4], vector<MyTrie<int,string>*> >
SentenceTranslator::best_cover_num_chunks(istream &ins, vector<pair<string,string> > &source, bool min_nchunks) {

  //For a given end position (map index) returns the best sequence of
  //states covering the input text till that end position - The very
  //head of the trie means word not covered
  map<int, pair<double[4], vector<MyTrie<int,string>*> > > covers;
  pair<double[4], vector<MyTrie<int,string>*> > emptyvector_states_sequence;

  covers[-1]=emptyvector_states_sequence;
  covers[-1].first[SC_NCHUNKS]=0;  //Num of chunks covernig the input text (including uncovered words, they count 1) 
  covers[-1].first[SC_NCHUNKS_WITHOUT]=0;  //Num of chunks covernig the input text (not including uncovered words)
  covers[-1].first[SC_SCORE]=0; //prob=1; we use log prob - score using defsc for uncovered words
  covers[-1].first[SC_SCORE_WITHOUT]=0; //prob=1; we use log prob - score *not* using defsc for uncovered words

  vector<MyTrie<int, string>*> alive_states_clean;
  alive_states_clean.push_back(&trie); 

  //Initial state is always alive to allow to start the search at
  //every word in the input text
  vector<MyTrie<int, string>*> alive_states=alive_states_clean;

  //In source the words that have been read are returned
  source.clear();

  //We need to keep in memory (max_words_chunks+1) covers
  int ncovers=max_words_chunk+1;

  if(debug) {
    cerr<<"\n------------------------------\n-- New call to best_cover --\n------------------------------\n";
    cerr<<"states alive (init state): ";
    for(unsigned i_alive=0; i_alive<alive_states.size(); i_alive++) {
      MyTrie<int, string>* &italive = alive_states[i_alive];
      cerr<<"("<<italive->state_id;
      if (italive->has_key)
        cerr<<"#"<<italive->data_length;
      cerr<<") ";
    }
    cerr<<"\n";
  }

  int i=-1;
  bool readmore=true;
  while (readmore) {
    pair<string,string> word = next_word(ins);
    source.push_back(word);
    i++;

    if (debug) {
      cerr<<"word='"<<word.first<<"' format='"<<word.second<<"'\n";
      cerr<<"---------------------------------------\n";
    }

    vector<MyTrie<int, string>*> alive_states_new=alive_states_clean;

    pair<double[4], vector<MyTrie<int,string>*> > new_best_cover;
    new_best_cover.first[SC_NCHUNKS] = numeric_limits<int>::max();
    new_best_cover.first[SC_NCHUNKS_WITHOUT] = numeric_limits<int>::max();
    new_best_cover.first[SC_SCORE] = -numeric_limits<double>::max();
    new_best_cover.first[SC_SCORE_WITHOUT] = -numeric_limits<double>::max();

    for(unsigned i_alive=0; i_alive<alive_states.size(); i_alive++) {
      MyTrie<int, string>* &italive = alive_states[i_alive];

      string str_search=Utils::strtolower(word.first);
      if (italive->childs.find(str_search) != italive->childs.end()) {
        MyTrie<int,string> *this_state = italive->childs[str_search];

        alive_states_new.push_back(this_state);

        if (this_state->has_key) { //This state recognizes a string
          if (debug) {
            cerr<<"\tState "<<this_state->state_id<<" recognizes a string of length "<<this_state->data_length<<"\n";
            cerr<<"\tIt must be added to states sequence in cover["<<(i-(this_state->data_length))<<"]\n";
          }
          pair<double[4], vector<MyTrie<int,string>*> > newseq = covers[(i-(this_state->data_length))%ncovers];

          if (debug) {
            cerr<<"\tAdding state "<<this_state->state_id<<" to states sequence ( ";
            for(unsigned k=0; k<newseq.second.size(); k++)
              cerr<<newseq.second[k]->state_id<<" ";
            cerr<<"prevsc:"<<newseq.first[SC_NCHUNKS]<<":"<<newseq.first[SC_NCHUNKS_WITHOUT]<<":"<<newseq.first[SC_SCORE]<<":"<<newseq.first[SC_SCORE_WITHOUT]<<")\n";
          }


          //The score is the sum of the chunks covering the input text
          newseq.first[SC_NCHUNKS]=newseq.first[SC_NCHUNKS] + 1; //Num chunks (including uncovered words, they count as one chunk) 
          newseq.first[SC_NCHUNKS_WITHOUT]=newseq.first[SC_NCHUNKS_WITHOUT] + 1; //Num chunks (Not including uncovered words) 

          //There may be more than one target side for the same source side
          //Anyway, the score is the same for all chunks with the same SL part
          if (use_pspts) {
            newseq.first[SC_SCORE]=newseq.first[SC_SCORE] + (chunks_collection[this_state->key_value][0].get_score() + 
                                               chunks_collection[this_state->key_value][0].get_prob_ts()); //We use log prob
            newseq.first[SC_SCORE_WITHOUT]=newseq.first[SC_SCORE_WITHOUT] + (chunks_collection[this_state->key_value][0].get_score() + 
                                               chunks_collection[this_state->key_value][0].get_prob_ts()); //We use log prob
          } else {
            newseq.first[SC_SCORE]=newseq.first[SC_SCORE] + chunks_collection[this_state->key_value][0].get_score(); //We use log prob
            newseq.first[SC_SCORE_WITHOUT]=newseq.first[SC_SCORE_WITHOUT] + chunks_collection[this_state->key_value][0].get_score(); //We use log prob
          }

          if (min_nchunks) {
            //We want to use the least possible number of chunks
            if(newseq.first[SC_NCHUNKS]<new_best_cover.first[SC_NCHUNKS]) {
              newseq.second.push_back(this_state);
              new_best_cover=newseq;
            } else if (newseq.first[SC_NCHUNKS]==new_best_cover.first[SC_NCHUNKS]) { //same num of chunks 
              if (newseq.first[SC_NCHUNKS_WITHOUT]<new_best_cover.first[SC_NCHUNKS_WITHOUT]) { //we choose the one with the least number of uncovered words
                newseq.second.push_back(this_state);
                new_best_cover=newseq;
              } else if (newseq.first[SC_NCHUNKS_WITHOUT]==new_best_cover.first[SC_NCHUNKS_WITHOUT]) { //same num of chunks  

                //we choose the one with the higest score (not including not coverered words, not using default score)
                if(newseq.first[SC_SCORE_WITHOUT]>new_best_cover.first[SC_SCORE_WITHOUT]) {
                  newseq.second.push_back(this_state);
                  new_best_cover=newseq;
                }
              }
            }
          } else {
            if(newseq.first[SC_SCORE]>new_best_cover.first[SC_SCORE]) {
              newseq.second.push_back(this_state);
              new_best_cover=newseq;
            }
          }
        }
      }      
    }

    //We also add to the cover in the previous step the null state
    //(head of the Trie) which means word not covered. 
    pair<double[4], vector<MyTrie<int,string>*> > newseq = covers[(i-1)%ncovers];

    if (debug) {
      cerr<<"\tAdding state "<<trie.state_id<<" to states sequence ( ";
      for(unsigned k=0; k<newseq.second.size(); k++)
        cerr<<newseq.second[k]->state_id<<" ";
      cerr<<"sc:"<<newseq.first[SC_NCHUNKS]<<":"<<newseq.first[SC_NCHUNKS_WITHOUT]<<":"<<newseq.first[SC_SCORE]<<":"<<newseq.first[SC_SCORE_WITHOUT]<<" )\n";
    }          

    newseq.first[SC_NCHUNKS]=newseq.first[SC_NCHUNKS] + 1; //The score is the sum of the chunks covering the input text
    newseq.first[SC_SCORE]=newseq.first[SC_SCORE] + defscore; //defscore is log prob

    if (min_nchunks) {
      //We want to use the least possible number of chunks
      if(newseq.first[SC_NCHUNKS]<new_best_cover.first[SC_NCHUNKS]) {
        newseq.second.push_back(&trie);
        new_best_cover=newseq;
      } else if (newseq.first[SC_NCHUNKS]==new_best_cover.first[SC_NCHUNKS]) { //same num of chunks 
        if (newseq.first[SC_NCHUNKS_WITHOUT]<new_best_cover.first[SC_NCHUNKS_WITHOUT]) { //we choose the one with the least number of uncovered words
          newseq.second.push_back(&trie);
          new_best_cover=newseq;
        } else if (newseq.first[SC_NCHUNKS_WITHOUT]==new_best_cover.first[SC_NCHUNKS_WITHOUT]) { //same num of chunks  

          //we choose the one with the higest score (not including not coverered words, not using default score)
          if(newseq.first[SC_SCORE_WITHOUT]>new_best_cover.first[SC_SCORE_WITHOUT]) {
            newseq.second.push_back(&trie);
            new_best_cover=newseq;
          }
        }
      }
    } else {
      if(newseq.first[SC_SCORE]>new_best_cover.first[SC_SCORE]) {
        newseq.second.push_back(&trie);
        new_best_cover=newseq;
      }
    }

    alive_states=alive_states_new;
    covers[i%ncovers]=new_best_cover;
  
    if(debug) {
      cerr<<"states alive: ";
      for(unsigned i_alive=0; i_alive<alive_states.size(); i_alive++) {
        MyTrie<int, string>* &italive = alive_states[i_alive];
        cerr<<"("<<italive->state_id;
        if (italive->has_key)
          cerr<<"#"<<italive->data_length;
        cerr<<") ";
      }
      cerr<<"\n";

      cerr<<"covers\n";
      cerr<<"---------------------------------------\n";
      int j=i-ncovers+1; if (j<-1) j=-1;
      for(; j<=i; j++) {
        cerr<<j<<": ";
        cerr<<"( ";
        for(unsigned i_cover=0; i_cover<covers[j%ncovers].second.size(); i_cover++)
          cerr<<covers[j%ncovers].second[i_cover]->state_id<<" ";
        cerr<<"sc:"<<covers[j%ncovers].first[SC_NCHUNKS]<<":"<<covers[j%ncovers].first[SC_NCHUNKS_WITHOUT]<<":"<<covers[j%ncovers].first[SC_SCORE]<<":"<<covers[j%ncovers].first[SC_SCORE_WITHOUT]<<" )\n";
      }
      cerr<<"\n";
    }

    if (alive_states.size()==1) {
      //No alive states apart from the initial state which is always alive
      if (debug) cerr<<"------------------------------\n----- No states alive -----\n------------------------------\n";
      readmore=false;
    }
  }

  return covers[(source.size()-1)%ncovers];
}

void 
SentenceTranslator::translate(istream &ins) {

  while (!ins.eof()) {
    pair<double[4], vector<MyTrie<int,string>*> > cover;
    vector<pair<string,string> > source;
    cover=best_cover_num_chunks(ins, source, true);

    if (debug) {
      cerr<<"Best cover: ";
      for(unsigned i=0; i<cover.second.size(); i++)
        cerr<<cover.second[i]->state_id<<" ";
      cerr<<"sc:"<<cover.first[SC_NCHUNKS]<<":"<<cover.first[SC_NCHUNKS_WITHOUT]<<":"<<cover.first[SC_SCORE]<<":"<<cover.first[SC_SCORE_WITHOUT]<<"\n";
    }

    string sent_tl="";
    int index_sl=0;
    for(unsigned i=0; i<cover.second.size(); i++) {
      if (cover.second[i] != &trie) {
        if (!cover.second[i]->has_key) {
          cerr<<"Error: State "<<cover.second[i]->state_id<<" has no key\n";
          cerr<<"This should never happen\n";
          exit(EXIT_FAILURE);
        }

        //There may be more than one target side for the same source side
        //By now, we take the first one - most probable one
        Alignment& chunk=chunks_collection[cover.second[i]->key_value][0];

        if(debug) cerr<<chunk<<" -- "<<chunk.get_target_str()<<"\n";

        string chunk_id=Utils::itoa(cover.second[i]->key_value);

        sent_tl+="[__CHUNK#"+chunk_id+"__"+chunk.get_target_str()+"__CHUNK#"+chunk_id+"__]";
        index_sl+=chunk.get_source().size();
        sent_tl+=source[index_sl-1].second;

        stats.napp++;
        stats.words_covered+=chunk.get_source().size();
        stats.chunks_applied[cover.second[i]->key_value]++;
      } else { //Word not covered
        sent_tl+=source[index_sl].first+source[index_sl].second;
        index_sl++;
        stats.words_not_covered++;
      }
    }

    cout<<sent_tl<<flush;
  }
}

void 
SentenceTranslator::detect_chunks(istream &ins) {

  map<int,int> chunks_counter;

  while (!ins.eof()) {
    pair<double[4], vector<MyTrie<int,string>*> > cover;
    //pair<double, vector<MyTrie<int,string>*> > cover;
    vector<pair<string,string> > source;
    cover=best_cover_num_chunks(ins, source, true);
    //cover=best_cover(ins, source);
    if (debug) {
      cerr<<"Best cover: ";
      for(unsigned i=0; i<cover.second.size(); i++)
        cerr<<cover.second[i]->state_id<<" ";
      cerr<<"sc:"<<cover.first[SC_NCHUNKS]<<":"<<cover.first[SC_NCHUNKS_WITHOUT]<<":"<<cover.first[SC_SCORE]<<":"<<cover.first[SC_SCORE_WITHOUT]<<"\n";
    }

    string sent_tl="";
    int index_sl=0;
    for(unsigned i=0; i<cover.second.size(); i++) {
      if (cover.second[i] != &trie) {
        if (!cover.second[i]->has_key) {
          cerr<<"Error: State "<<cover.second[i]->state_id<<" has no key\n";
          cerr<<"This should never happen\n";
          exit(EXIT_FAILURE);
        }

        //There may be more than one target side for the same source side
        //By now, we take the first one - most probable one
        Alignment& chunk=chunks_collection[cover.second[i]->key_value][0];

        if(debug) cerr<<chunk<<" -- "<<chunk.get_source_str()<<"\n";

        string chunk_id=Utils::itoa(cover.second[i]->key_value);
        string chunk_count=Utils::itoa(chunks_counter[cover.second[i]->key_value]);
        string num_words=Utils::itoa(chunk.get_source().size());
        chunks_counter[cover.second[i]->key_value]++;

        sent_tl+=" [__BCHUNK_"+chunk_id+"_"+chunk_count+"_"+num_words+"__] ";
        for(unsigned j=0; j<chunk.get_source().size(); j++) {
          sent_tl+=source[index_sl].first+source[index_sl].second;
          index_sl++;
        }
        sent_tl+=" [__ECHUNK_"+chunk_id+"_"+chunk_count+"_"+num_words+"__] ";

        stats.napp++;
        stats.words_covered+=chunk.get_source().size();
        stats.chunks_applied[cover.second[i]->key_value]++;
      } else { //Word not covered
        sent_tl+=source[index_sl].first+source[index_sl].second;
        index_sl++;
        stats.words_not_covered++;
      }
    }

    cout<<sent_tl<<flush;
  }
}

void 
SentenceTranslator::replace_chunks(istream &ins) {

  int current_chunk_id=-1;
  int current_chunk_count=-1;
  //int current_word_count=-1;

  map<int, map<int, bool> > used_chunks;

  while (!ins.eof()) {
    pair<string, string> word;

    word=next_apertium_word(ins);
    //cerr<<"+"<<word.first<<"+"<<word.second<<"+\n";

    string::size_type p1=word.first.find("_x_");    
    if ((word.first.length()>0)&&(word.first[0]=='^')&&(word.first[word.first.length()-1]=='$')&&(p1!=string::npos)) {
      p1+=3;
      string::size_type p2=word.first.find("_",p1+1);
      string::size_type p3=word.first.find("_",p2+1);
      //string::size_type p4=word.first.find("<",p3+1);
      //cerr<<"POS: "<<p1<<" "<<p2<<" "<<p3<<" "<<p4<<"\n";

      int chunk_id=atoi(word.first.substr(p1, p2-p1).c_str());
      int chunk_count=atoi(word.first.substr(p2+1, p3-p2-1).c_str());
      //int word_count=atoi(word.first.substr(p3+1, p4-p3-1).c_str());

      //cerr<<"ID: +"<<word.first.substr(p1, p2-p1)<<"+\n";
      //cerr<<"CHUNK COUNT: +"<<word.first.substr(p2+1, p3-p2-1)<<"+\n";
      //cerr<<"WORD COUNT: +"<<word.first.substr(p3+1, p4-p3-1)<<"+\n";

      //cerr<<"ID: "<<chunk_id<<"\n";
      //cerr<<"CHUNK COUNT: "<<chunk_count<<"\n";
      //cerr<<"WORD COUNT: "<<word_count<<"\n";

      if(!used_chunks[chunk_id][chunk_count]) {
        //if ((current_chunk_id != chunk_id) || (current_chunk_count != chunk_count)) {
        used_chunks[chunk_id][chunk_count]=true;

        current_chunk_id=chunk_id;
        current_chunk_count=chunk_count;

        //There may be more than one target side for the same source side
        //By now, we take the first one - most probable one
        Alignment& chunk=chunks_collection[chunk_id][0];

        if(debug) cerr<<chunk<<" -- "<<chunk.get_target_str()<<"\n";

        cout<<chunk.get_target_str()<<word.second;
      } else  {
        cout<<word.second;
      }
    } else {
      cout<<word.first<<word.second;
    }
  }
}

void 
SentenceTranslator::score_replace_chunks_greedy(LanguageModel &lm, istream &ins) {
  while (!ins.eof()) {
    string sentence;
    getline(cin, sentence);

    string translation="";
    vector<string> words=Utils::split_string(sentence," ");
    for(unsigned i=0; i<words.size(); i++) {
      if (words[i].length()==0)
        continue;

      string::size_type p1=words[i].find("__BCHUNK_");
      if (p1 != string::npos) { //Begin-of-chunk mark
        string::size_type p2=words[i].find("_",p1+9);

        //cerr<<words[i]<<" pos: "<<p1<<" "<<p2<<"\n";

        string chunk_id_str=words[i].substr(p1+9, p2-p1-9);
        string chunk_remaining_str=words[i].substr(p2);

        //cerr<<"ID: +"<<chunk_id_str<<"+\n";
        //cerr<<"REMAINING: +"<<chunk_remaining_str<<"+\n";

        string search_str="__ECHUNK_"+chunk_id_str+chunk_remaining_str;

        //cerr<<"SEARCH STR: "<<search_str<<"\n";

        //Find end-of-chunk mark (should always be there)
        unsigned j;
        for (j=i+1; j<words.size(); j++) {
          if (words[j]==search_str)
            break;
        }

        if (j>=words.size()) {
          cerr<<"Error in SentenceTranslator::score_replace_chunks\n";
          exit(EXIT_FAILURE);
        }

        vector<string> possible_translations;

        //For eficiency we use the apertium translation as the translation for the remaining words
        //of the sentece (words on the right). 
        string post_translation="";
        for (unsigned k=j; k<words.size(); k++) {
          if ((words[k].find("__BCHUNK_") == string::npos) && (words[k].find("__ECHUNK_") == string::npos)) {
            if (post_translation.length()>0)
              post_translation += " ";
            post_translation += words[k];
          }
        }

        string apertium="";
        for (unsigned k=i+1; k<j; k++) {
          if (apertium.length()>0)
            apertium += " ";
          apertium += words[k];
        }
        possible_translations.push_back(apertium);


        //cerr<<"\nPrev. transaltion: "<<translation<<"\n";
        //cerr<<"Apertium translation: "<<apertium<<"\n";
        //cerr<<"Post. transaltion: "<<post_translation<<"\n";

        int chunk_id=atoi(chunk_id_str.c_str());
        vector<Alignment> &chunk = chunks_collection[chunk_id];
        for (unsigned k=0; k<chunk.size(); k++)
          possible_translations.push_back(chunk[k].get_target_str());

        cerr<<"Chunk ("<<i+1<<"-"<<j-1<<")\n";
        double best_score=-numeric_limits<double>::max();
        unsigned best_index=-1;
        for (unsigned k=0; k<possible_translations.size(); k++) {
          string str_to_eval=Utils::strtolower(translation+" " +possible_translations[k]+" "+post_translation);
          //cerr<<"STR FOR LM: "<<str_to_eval<<"\n";
          double sc=lm.log_prob(str_to_eval);
          cerr<<"     ["<<k<<"] "<<possible_translations[k]<<" [sc: "<<sc<<"]\n";
          if (sc>best_score) {
            best_index=k;
            best_score=sc;
          }
        }
        //cerr<<"   Best index: "<<best_index<<"\n";

        stats.napp++;
        stats.words_covered+=j-i-1;
        stats.chunks_applied[chunk_id]++;

        if (best_index!=0) { //It is not the apertium translation
          stats.chunks_actually_applied[chunk_id]++;
          stats.words_actually_covered+=j-i-1;
          stats.actual_napp++;
        }

        stats.translation_used[best_index]++;

        if (translation.length()>0)
          translation += " ";

        translation += possible_translations[best_index];

        i=j;
      } else {
        if (translation.length()>0)
          translation += " ";
        translation += words[i];
        stats.words_not_covered++;
      }
    }
    if (translation.length()>0)
      cout<<translation<<"\n";
  }
}

void 
SentenceTranslator::score_replace_chunks(LanguageModel &lm, istream &ins) {
  while (!ins.eof()) {
    string sentence;
    getline(cin, sentence);

    string translation="";
    vector<string> words=Utils::split_string(sentence," ");

    //Input string is divided into segment of one or more words, each segment may have more than one translation
    vector<vector<string> > text_segments;
    vector<int> chunkid_text_segments; //-1 for text segments not covered by words
    vector<bool> apertium_text_segments; //true if the chunk at the given postion provides the same trasnlation that apertium provides
    string segment="";

    for(unsigned i=0; i<words.size(); i++) {
      if (words[i].length()==0)
        continue;

      string::size_type p1=words[i].find("__BCHUNK_");
      if (p1 != string::npos) { //Begin-of-chunk mark

        if (segment.length()>0) {
          vector<string> emptyvec;
          emptyvec.push_back(segment);
          text_segments.push_back(emptyvec);
          chunkid_text_segments.push_back(-1);
          apertium_text_segments.push_back(false);
          segment="";
        }

        string::size_type p2=words[i].find("_",p1+9);

        //cerr<<words[i]<<" pos: "<<p1<<" "<<p2<<"\n";

        string chunk_id_str=words[i].substr(p1+9, p2-p1-9);
        string chunk_remaining_str=words[i].substr(p2);

        //cerr<<"ID: +"<<chunk_id_str<<"+\n";
        //cerr<<"REMAINING: +"<<chunk_remaining_str<<"+\n";

        string search_str="__ECHUNK_"+chunk_id_str+chunk_remaining_str;

        //cerr<<"SEARCH STR: "<<search_str<<"\n";

        //Find end-of-chunk mark (should always be there)
        unsigned j;
        for (j=i+1; j<words.size(); j++) {
          if (words[j]==search_str)
            break;
        }

        if (j>=words.size()) {
          cerr<<"Error in SentenceTranslator::score_replace_chunks\n";
          exit(EXIT_FAILURE);
        }

        string apertium="";
        for (unsigned k=i+1; k<j; k++) {
          if (apertium.length()>0)
            apertium += " ";
          apertium += words[k];
        }

        vector<string> emptyvec;
        string apertium_lower=Utils::strtolower(apertium);
        emptyvec.push_back(apertium_lower);

        int chunk_id=atoi(chunk_id_str.c_str());
        vector<Alignment> &chunk = chunks_collection[chunk_id];
        bool same_as_apertium=false;
        for (unsigned k=0; k<chunk.size(); k++) {
          if (apertium_lower!=chunk[k].get_target_str()) //one TL part may be equal to that of apertium
            emptyvec.push_back(chunk[k].get_target_str());
          else
            same_as_apertium=true;
        }

        text_segments.push_back(emptyvec);
        chunkid_text_segments.push_back(chunk_id);
        apertium_text_segments.push_back(same_as_apertium);

        stats.napp++;
        stats.words_covered+=j-i-1;
        stats.chunks_applied[chunk_id]++;

        i=j;
      } else {
        if (segment.length()>0)
          segment += " ";
        segment += words[i];

        stats.words_not_covered++;
      }
    }
    if (segment.length()>0) {
      vector<string> emptyvec;
      emptyvec.push_back(Utils::strtolower(segment));
      text_segments.push_back(emptyvec);
      chunkid_text_segments.push_back(-1);
      apertium_text_segments.push_back(false);
      segment="";
    }

    vector<int> best_combination;
    string best_translation=langmodel_score(lm, text_segments, best_combination);

    if (best_combination.size()!=chunkid_text_segments.size()) {
      cerr<<"SIZES DO NOT AGREE: (best_combination.size()!=chunkid_text_segments.size())\n";
    }
    if (best_combination.size()!=apertium_text_segments.size()) {
      cerr<<"SIZES DO NOT AGREE: (best_combination.size()!=apertium_text_segments.size())\n";
    }


    if (best_translation.length()>0) {
      cout<<best_translation<<"\n";

      for(unsigned j=0; j<best_combination.size(); j++) {
        stats.translation_used[best_combination[j]]++;
        if (best_combination[j]!=0) {

          if (chunkid_text_segments[j]==-1) {
            cerr<<"SOMETHING IS GOING BAD\n";
          }

          stats.chunks_actually_applied[chunkid_text_segments[j]]++;
          stats.chunks_actually_applied_apertium[chunkid_text_segments[j]]++;
          
          stats.actual_napp++;
          stats.words_actually_covered+=Utils::split_string(Utils::trim(text_segments[j][best_combination[j]])," ").size();

          stats.actual_napp_apertium++;
          stats.words_actually_covered_apertium+=Utils::split_string(Utils::trim(text_segments[j][best_combination[j]])," ").size();

        } else { //The Apertium translation was used
          if (apertium_text_segments[j]) { //The chunk provides the same translation provided by Apertium

            stats.chunks_actually_applied_apertium[chunkid_text_segments[j]]++;
            stats.actual_napp_apertium++;
            stats.words_actually_covered_apertium+=Utils::split_string(Utils::trim(text_segments[j][best_combination[j]])," ").size();
          }
        }
      }
    }
  }
}

string
SentenceTranslator::langmodel_score(LanguageModel &lm, const vector<vector<string> > &segments, vector<int> &best_combination) {

  vector<int> nfix_path;  //vector storing the number of times a possible translation of a segment
                          //mustt be used before using the next one - used for efficiency
  unsigned num_paths=1;

  if (segments.size()==0)
    return "";

  stats.nsentences++;

  for(unsigned i=0; i<segments.size(); i++) {
    int nfix=1;
    for (unsigned j=i+1; j<segments.size(); j++) {
      nfix*=segments[j].size();
    }
    nfix_path.push_back(nfix);
    num_paths*=segments[i].size();
  }

  cerr<<num_paths<<"\n";

  best_combination.clear();
  string best_path;
  if (num_paths>1000000) {
    stats.ngreedy++;
    //Too many paths, we follow a greedy approach
    string translation="";

    //cerr<<"----------- GREEDY -------------------------\n";
    for(unsigned i=0; i<segments.size(); i++) {

      //For eficiency we use the apertium translation as the translation for the remaining words
      //of the sentece (words on the right). 
      string post_translation="";
      for (unsigned j=i+1; j<segments.size(); j++) {
        if (post_translation.length()>0)
          post_translation += " ";
        post_translation += segments[j][0]; // Apertium translation is always the firstone
      }

      double best_score=-numeric_limits<double>::max();
      unsigned best_index=-1;
      for (unsigned j=0; j<segments[i].size(); j++) {
        string str_to_eval=Utils::strtolower(translation+" "+segments[i][j]+" "+post_translation);
        //cerr<<"STR FOR LM: "<<str_to_eval<<": ";
        double sc=lm.log_prob(str_to_eval);
        //cerr<<sc<<"\n";
        if (sc>best_score) {
          best_index=j;
          best_score=sc;
        }
      }
      //cerr<<"Best index: "<<best_index<<"\n";

      if (translation.length()>0)
        translation += " ";

      translation += segments[i][best_index];
      best_combination.push_back(best_index);
    }
    best_path=translation;
  } else {

    //cerr<<"COMBINATIONS: \n-------------------------------\n";
    double best_score=-numeric_limits<double>::max();

    for (unsigned npath=0; npath<num_paths; npath++) {
      string path="";
      vector<int> new_comb;   
      for(unsigned i=0; i<segments.size(); i++) {
        int position=((int)(npath/nfix_path[i]))%segments[i].size();
        if (path.length()>0)
          path+=" ";
        path+=segments[i][position];
        new_comb.push_back(position);
      }

      //cerr<<"PATH ("<<npath<<"): "<<path<<"\n";

      string str_to_eval=Utils::strtolower(path);    
      double sc=lm.log_prob(str_to_eval);
      //cerr<<"STR FOR LM: "<<str_to_eval<<" ";
      //cerr<<"SC: "<<sc<<"\n";
      if (sc>best_score) {
        best_score=sc;
        best_combination=new_comb;
        best_path=path;
      }
    }
  }

  return best_path;
}

void
SentenceTranslator::print_stats() {
  map<int,int>::iterator it;
  int nwords=stats.words_covered+stats.words_not_covered;

  cerr<<"\nSTATISTICS\n------------------------------ \n";
  cerr<<"# translated words:  "<<nwords<<"\n";
  cerr<<"# covered words:     "<<stats.words_covered<<" ("<<(((double)stats.words_covered)/((double)nwords))*100<<" %)\n";
  cerr<<"# not covered words: "<<stats.words_not_covered<<" ("<<(((double)stats.words_not_covered)/((double)nwords))*100<<" %)\n";
  cerr<<"# of chunks used: "<<stats.napp<<"\n\n";

  cerr<<"# uniq chunks used: "<<stats.chunks_applied.size()<<"\n";

  /*  cerr<<"\n---------------------------------------\n";

  for (it=stats.chunks_applied.begin(); it!=stats.chunks_applied.end(); it++) {
    cerr<<"ChunkId: "<<it->first<<"; len: "<<chunks_collection[it->first][0].get_source().size();
    cerr<<"; # app: "<<it->second<<" ("<<(((double)it->second)/((double)stats.napp))*100<<" %)";
    cerr<<"; chunk: "<<chunks_collection[it->first][0]<<"\n";
    }*/

  cerr<<"\n---------------------------------------\n";
  cerr<<"# actually covered words:     "<<stats.words_actually_covered<<" ("<<(((double)stats.words_actually_covered)/((double)nwords))*100<<" %)\n";
  cerr<<"# chunks actually used:       "<<stats.actual_napp<<"\n\n";

  cerr<<"# uniq chunks actually used: "<<stats.chunks_actually_applied.size()<<"\n";

  cerr<<"Index and number of times the given translation is applied (0 stands for Apertium)\n";
  for (it=stats.translation_used.begin(); it!=stats.translation_used.end(); it++) {
    cerr<<it->first<<" "<<it->second<<"\n";
  }

  cerr<<"\n---------------------------------------\n";
  cerr<<"# actually covered words (including chunks=apertium):     "<<stats.words_actually_covered_apertium<<" ("<<(((double)stats.words_actually_covered_apertium)/((double)nwords))*100<<" %)\n";
  cerr<<"# chunks actually used (including chunks=apertium):       "<<stats.actual_napp_apertium<<"\n\n";

  cerr<<"# uniq chunks actually used (including chunks=apertium): "<<stats.chunks_actually_applied_apertium.size()<<"\n";

  cerr<<"----------------------------------\n";
  cerr<<"# sent: "<<stats.nsentences<<"; # greedy: "<<stats.ngreedy<<" -> "<<(((double)stats.ngreedy)/((double)stats.nsentences))*100<<" %\n";

  /*cerr<<"\n---------------------------------------\n";

  for (it=stats.chunks_actually_applied.begin(); it!=stats.chunks_actually_applied.end(); it++) {
    cerr<<"ChunkId: "<<it->first<<"; len: "<<chunks_collection[it->first][0].get_source().size();
    cerr<<"; # actual app: "<<it->second<<" ("<<(((double)it->second)/((double)stats.actual_napp))*100<<" %)";
    cerr<<"; chunk: "<<chunks_collection[it->first][0]<<"\n";
    }*/
}


