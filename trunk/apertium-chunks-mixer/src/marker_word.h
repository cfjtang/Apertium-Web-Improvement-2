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
#ifndef __MARKER_WORD_H_
#define __MARKER_WORD_H_

using namespace std;

#include <string>
#include <vector>

class MarkerWord {
private:
  string surface_forms_str;
  vector<string> surface_forms; //Some marker words consist of more than one word

  vector<string> categories;
public: 

  MarkerWord(string str);

  MarkerWord(const MarkerWord& mw);

  //Returns true if the n-th word in vector_words equal this marker word
  //In numwords the number of words after the n-th word that also equals this marker-word is returned
  bool equals(const vector<string> &vector_words, int n, int &numwords) const;

  string to_string();
};

#endif
