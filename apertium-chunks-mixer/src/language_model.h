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
#ifndef __LANGUAGE_MODEL_H_
#define __LANGUAGE_MODEL_H_

using namespace std;

#include <string>

// SRI language model includes
#include <Vocab.h>
#include <Ngram.h>
#include <File.h>
#include <Prob.h>
#include <TextStats.h>

#define LOG_10_E 0.434294481903251827651128918916605082294397005803666566114453783165864649208870774729224949338431748

class LanguageModel {
 private:
  Vocab* _sri_vocab;
  Ngram* _sri_ngramLM;

 public:
  LanguageModel();

  ~LanguageModel();

  /**
   *  Loads language model data from the given path.
   */
  void load(string filepath);

  /**
   *  Calculates the log probability of the given segment.
   */
  double log_prob(string segment);
};

#endif
