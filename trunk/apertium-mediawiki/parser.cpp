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
#include <string>
#include <iostream>
#include <sstream>
using namespace std;

#ifndef MAIN
#define MAIN
#include"token.hpp"
#include"tokenizer.hpp"
#include"parser.hpp"
int main()
{
  TokenStream ts;
  Token t;
  Parser p;
  try{
    while(ts >> t)
      cout << p.parse(t).value;
    cout << ".[][\n]";
  }
  catch(Token::Invalid_type i){
    cerr << "Invalid token type";
  }
}
#endif

#ifndef TOKEN
#include"token.hpp"
#endif

#ifndef TOKENSTREAM
#include"tokenizer.hpp"
#endif

#ifndef PARSER
#include"parser.cpp"
#endif

// deal with [[link|text]] correctly
string Parser::link_normal(string s)
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

// [[test]]ing -> [[test|testing]]
string Parser::link_normalize(string s)
{
  // [[head]]tail
  string head;
  string tail;
  string::iterator it=s.begin();

  // build head
  for( ; it!=s.end(); ++it)
    if( ! ispunct(*it) )
      head += *it;
    else if (head.size())
      break;

  // build tail
  for( ; it!=s.end(); ++it)
    if( ! ispunct(*it) )
	tail += *it;

  return "[[" + head + "|" + head + tail + "]]";
}

// escapes all [ and ], then returns the string wrapped in [ ]
string Parser::simple_tag(string s)
{
  for (int i=0; i<s.size(); ++i)
    if( (s[i]=='[') || (s[i]==']') ){
      s.insert(i, "\\");
      // increment i so that it now points to the new position of the [ or ] we just escaped
      ++i;
    }
  return "[" + s + "]";
}

// prefix all [ and ] with \ then wrap all other punctuation within [ ]
string Parser::simple_text(string s)
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

// stand in
string Parser::table(string s)
{
  string done, line;
  istringstream is(s);
  while( getline(is, line) ){
    string::iterator it = line.begin();
    switch( *it ){
    case '{':
      done += "[" + simple_text(line) + "]\n";
      break;
    case '!':{
      string head, tail;
      //build head
      for(; it!= line.end(); ++it){
	head += *it;
	if( *it == '|' )
	  break;
      }

      //build tail
      for(; it != line.end(); ++it){
	tail += *it;
	// if we find a |, everything upto here was head and tail starts here
	if( *it == '|' ){
	  head += tail;
	  tail = "";
	}
      }

      // if tail is empty, then we never found a | and the whole line is data
      if( tail == "" ){
	done += simple_text(head) + '\n';
	break;
      }
      done += "[" + head + "]" + simple_text(tail) + '\n';
      break;
    }
    case '|':{
      // current 'token'
      string t;
      for( ; it != line.end(); ++it){
	if( *it == '|' ){
	  // if we see a '||' then the previous token was text
	  if( *(it+1) == '|' ){
	    done += simple_text(t) + "[||]";
	    t = "";
	  }
	  // otherwise we saw a '|' and the previous token was formatting
	  else {
	    done += '[' + t + "|]";
	    t = "";
	  }
	  continue;
	}
	t += *it;
      }
      done += simple_text(t) + '\n';
      break;
    }
    default:
      done += simple_text(line) + '\n';
      break;
    }
  }
  return done;
}

Token Parser::parse(Token t)
{
  switch(t.type){
  case Token::simple_tag:
    return simple_tag(t);
    break;
  case Token::simple_text:
    return simple_text(t);
    break;
  case Token::link_normalize:
    t = link_normalize(t);
  case Token::link_normal:
    return link_normal(t);
    break;
  case Token::table:
    return table(t);
    break;
  default:
    throw Token::Invalid_type();
    break;
  }
}

