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
#include <iostream>
#include <string>
using namespace std;

bool getch(char& c)
{
  char ch, next;
  // if input fails, stop getting input
  if(! cin.get(ch) ) return false;
  if(ch=='[' || ch==']'){
    // do not return this character, but keep getting input
    c=NULL;
    return true;
}
  if(ch=='\\'){
    cin.get(next);
    if(next=='[' || next==']'){
      // return this character but not the \, and keep getting input
      c=next;
      return true;
    }
    cin.putback(ch);
  }
  // return this character and keep getting input
  c=ch;
  return true;
}

int main()
{
  char ch;
  while( getch(ch) )
    if(ch != NULL)
      cout << ch;
}
