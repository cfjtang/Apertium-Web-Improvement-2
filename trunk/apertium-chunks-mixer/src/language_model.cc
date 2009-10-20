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
#include "language_model.h"

LanguageModel::LanguageModel() {
  _sri_vocab=NULL;
  _sri_ngramLM=NULL;
}

LanguageModel::~LanguageModel() {
  if (_sri_ngramLM != NULL)
    delete _sri_ngramLM;
  if (_sri_vocab != NULL)
    delete _sri_vocab;
}

void
LanguageModel::load(string filepath) {
  File file (filepath.c_str(), "r");

  _sri_vocab = new Vocab;
  _sri_ngramLM = new Ngram (*_sri_vocab, 5); // Language model of 5th order

  //cerr<<"Reading language model data ... "<<flush;
  _sri_ngramLM->read(file, 0); // Limit vocab = 0
  //cerr<<"done.\n";
}

double
LanguageModel::log_prob(string segment) {
  TextStats ts;
  VocabString words[maxWordsPerLine+1];
  char segment_str[segment.size()+1];

  segment.copy(segment_str, segment.size(), 0);
  segment_str[segment.size()]='\0';

  _sri_vocab->parseWords(segment_str, words, maxWordsPerLine+1);
  //double p = LogPtoProb(_sri_ngramLM->sentenceProb(words, ts));
  LogP p = _sri_ngramLM->sentenceProb(words, ts);

  //cerr<<"Segment: "<<segment<<"\n";
  //cerr<<"Num. words: "<<ts.numWords<<"\n";
  //cerr<<"Num. words OOV: "<<ts.numOOVs<<"\n";
  //cerr<<"Prob: "<<p<<"\n";

  return p/LOG_10_E; //We want log_e probabilities, SRILM uses log_10
}
