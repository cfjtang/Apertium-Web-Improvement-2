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
#include "marker_word.h"
#include "utils.h"

#include <iostream>

MarkerWord::MarkerWord(string str) {
  vector<string> v;
  v=Utils::split_string(str,":",1);

  surface_forms_str=v[0];
  surface_forms=Utils::split_string(surface_forms_str," ");

  for(unsigned i=1; i<v.size(); i++)
    categories.push_back(v[i]);
}

MarkerWord::MarkerWord(const MarkerWord& mw) {
  surface_forms_str=mw.surface_forms_str;
  surface_forms=mw.surface_forms;
  categories=mw.categories;
}

string
MarkerWord::to_string() {
  string s=surface_forms_str;
  
  for(unsigned i=0; i<categories.size(); i++)
    s+=":"+categories[i];

  return s;
}

bool 
MarkerWord::equals(const vector<string> &vector_words, int n, int &numwords) const {

  numwords=0;

  for(unsigned i=0; i<surface_forms.size(); i++) {
    if ( ((i+n) >= vector_words.size()) || (vector_words[i+n] != surface_forms[i]) )
      return false;
  }

  numwords=(surface_forms.size()-1);

  return true;
}
