/*  Copyright 2010 Christopher Hall
    This file is part of Apertium-mediawiki, see http://code.google.com/p/apertium-mediawiki/.

    Apertium-mediawiki is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Apertium-mediawiki is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Apertium-mediaiki.  If not, see <http://www.gnu.org/licenses/>.
*/
#include<string>
#include<iostream>
using namespace std;

#ifndef _MAIN
#define _MAIN
string link_normal(string s);
int main()
{
  cout << link_normal("[[link|text]]");
}
#endif

#include "simple_tag.cpp"

// deal with [[link|text]] correctly
string link_normal(string s)
{
  string::iterator si=s.begin();
  // for [[link|text]]
  // --head   | text | tail--
  // {[[link|} {text} {]]}
  // if given [[link|arg|arg|text]]
  // will treat final text as tail
  // and the rest as head
  string head, text, tail;

  // build head
  for( ; si!=s.end(); ++si){
    head += *si;
    if(*si == '|'){
      // increment iterator past |
      ++si;
      break;
    }
  }

  // build text
  for( ; si!=s.end(); ++si){
    if(*si == ']')
      break;
    text += *si;
    if(*si == '|'){
      head += text;
      text = "";
    }
  }

  // build tail
  for( ; si!=s.end(); ++si)
    tail += *si;

  return simple_tag(head) + text + simple_tag(tail);
}


