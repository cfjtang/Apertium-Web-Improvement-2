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
#include <string>
#include <iostream>
using namespace std;

// prefix all [ and ] with \ then wrap all other punctuation within [ ]
string simple_text(string s)
{
  string r="";
  // flag used to determine if we have started a 'block'
  // (sequence of punctuation enclosed with [])
  bool enclosed=false;
  for (int i=0; i<s.size(); ++i){
    if( ispunct(s[i]) ){
      // if we havent opened a block, open one
      if( !enclosed ){
	enclosed = true;
	r += "[";
      }
      if( s[i]=='[' || s[i]==']')
	r += "\\";
    } //end of if( ispunct(s[i]) ){

    // if this character is NOT punctuation,
    // but we have already opened a block,
    // then close the block
    else if( enclosed ){
	enclosed=false;
	r += "]";
    }
    // either way, add 'it' to r
    r += s[i];
  } //end of for
  // if we have opened a tag, better close it before returning
  if( enclosed )
    return r+"]";
  // otherwise just return it
  return r;
}

#ifndef _MAIN
#define _MAIN
int main()
{
  cout << simple_text("something wacky [hehe|boo]] & ");
}
#endif
