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

#ifndef TOKEN
#include"token.hpp"
#endif

#ifndef TOKENSTREAM
#include"tokenizer.hpp"
#endif

Token TokenStream::get()
{
  char ch;
  string s="";
  // flags
  bool seenbrace=false, talpha=false, seenpipe=false;

  while( cin.get(ch) ){
    switch(ch){

    case '{':
      if(s.length()){
	cin.putback(ch);
	if(!nowiki && seenbrace) return Token(Token::simple_tag, s);
	return Token(Token::simple_text, s);
      }
      s+=ch;

      //handle table case
      cin.get(ch);
      if(ch == '|')
	do{
	  s+=ch;
	  if(ch == '}')
	    return Token(Token::table, s);
	} while(cin.get(ch));

      // wasn't what we wanted, put the character back
      cin.putback(ch);
      // and reset ch, not necessary, but clean
      ch = '{';
      //end of table case

      while(cin.get(ch)){
	if( ch!='{'){
	  cin.putback(ch);
	  break;
	}
	s+=ch;
      } //end of while(cin.get(ch))
      break;

    case '<':
      if(s.length()){
	cin.putback(ch);
	if(!nowiki && seenbrace) return Token(Token::simple_tag, s);
	return Token(Token::simple_text, s);
      }
      s+=ch;
      while(cin.get(ch)){
	if(ch!='<'){
	  cin.putback(ch);
	  break;
	}
	s+=ch;
      } //end of while(cin.get(ch))
      break;

    case '[':
      if(s.length()){
	cin.putback(ch);
	if(!nowiki && seenbrace) return Token(Token::simple_tag, s);
	return Token(Token::simple_text, s);
      }
      seenbrace=true;
      s+=ch;
      while(cin.get(ch)){
	if( ch!='['){
	  cin.putback(ch);
	  break;
	}
	s+=ch;
      } //end of while(cin.get(ch))
      break;

    case '}':
      s+=ch;
      while(cin.get(ch)){
	if(ch!='}'){
	  cin.putback(ch);
	  break;
	}
	s+=ch;
      } //end of while(cin.get(ch))
      if (nowiki) return Token(Token::simple_text, s);
      return Token(Token::simple_tag, s);
      break;

    case '>':
      s+=ch;
      while(cin.get(ch)){
	if(ch!='>'){
	  cin.putback(ch);
	  break;
	}
	s+=ch;
      } //end of while(cin.get(ch))
      if(s=="<nowiki>")
	nowiki = true;
      else if(s=="</nowiki>")
	nowiki=false;
      return Token(Token::simple_tag, s);
      break;

    case ']':
      s+=ch;
      while(cin.get(ch)){
	if( ch == ']')
	  s+=ch;
	else if( !seenpipe && !ispunct(ch) ){
	  s+=ch;
	  talpha=true;
	}
	else{
	  cin.putback(ch);
	  break;
	}
      } //end of while(cin.get(ch))
      if(nowiki)
	return Token(Token::simple_text, s);
      if(talpha)
	return Token(Token::link_normalize, s);
      if(seenpipe)
	return Token(Token::link_normal, s);
      // otherwise
      return Token(Token::simple_tag, s);
      break;

    case '|':
      s+=ch;
      seenpipe=true;
      break;

    case ' ':
      s+=ch;
      if(!seenbrace)
	return Token(Token::simple_text, s);
      break;

    default:
      s+=ch;
      break;

    }
  }
  if(!nowiki && seenbrace) return Token(Token::simple_tag, s);
  return Token(Token::simple_text, s);
}
