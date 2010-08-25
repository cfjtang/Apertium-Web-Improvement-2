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

// escapes all [ and ], then returns the string wrapped in [ ]
string simple_tag(string s)
{
  for (int i=0; i<s.size(); ++i)
    if( (s[i]=='[') || (s[i]==']') ){
      s.insert(i, "\\");
      // increment i so that it now points to the new position of the [ or ] we just escaped
      ++i;
    }
  return "[" + s + "]";
}

#ifndef _MAIN
#define _MAIN
int main()
{
  cout << simple_tag("[[heheeh]]");
}
#endif
