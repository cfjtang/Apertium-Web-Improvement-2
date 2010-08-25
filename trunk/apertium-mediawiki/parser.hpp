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
    along with Apertium-mediawiki.  If not, see <http://www.gnu.org/licenses/>.
*/

#ifndef PARSER
#define PARSER
class Parser{
public:
  Token operator()(Token t){
    return parse(t);
  }
  Token parse(Token t);

private:
  // 'components' dealing with individual cases, called via 'wrappers' by parse
  string link_normal(string s);
  string link_normalize(string s);
  string simple_tag(string s);
  string simple_text(string s);
  string table(string s);
  // 'wrappers' for components (that work on strings, not Tokens)
  Token link_normal(Token t){
    t.value = link_normal(t.value);
    return t;
  }
  Token link_normalize(Token t){
    t.value = link_normalize(t.value);
    return t;
  }
  Token simple_tag(Token t){
    t.value = simple_tag(t.value);
    return t;
  }
  Token simple_text(Token t){
    t.value = simple_text(t.value);
    return t;
  }
  Token table(Token t){
    t.value = table(t.value);
    return t;
  }

};
#endif //PARSER
