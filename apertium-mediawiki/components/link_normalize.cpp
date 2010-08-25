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

// [[test]]ing -> [[test|testing]]
string link_normalize(string s)
{
  // [[head]]tail
  string head;
  string tail;
  string::iterator it=s.begin();

  // build head
  for( ; it!=s.end(); ++it)
    if( isalnum(*it) )
      head += *it;
    else if (head.size())
      break;

  // build tail
  for( ; it!=s.end(); ++it)
    if( isalnum(*it) )
	tail += *it;

  return "[[" + head + "|" + head + tail + "]]";
}

#ifndef _MAIN
#define _MAIN
int main()
{
  cout << link_normalize("[[test]]ing");
}
#endif
