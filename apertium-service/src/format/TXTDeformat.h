/*
 * Copyright (C) 2009  Pasquale Minervini <p.minervini@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#ifndef TXTDEFORMAT_H_
#define TXTDEFORMAT_H_

#include <cstdlib>
#include <iostream>
#include <string>
#include <map>
#include <vector>
#include <regex.h>
#include <string>
#include <lttoolbox/lt_locale.h>
#include <lttoolbox/ltstr.h>
#ifdef WIN32
#if defined(__MINGW32__)
#define __MSVCRT_VERSION__  0x0800
#endif
#include <io.h>
#include <fcntl.h>
#endif

// #ifndef __FLEX_LEXER_H
// #define yyFlexLexer TXTDeformatFlexLexer
// #include <FlexLexer.h>
// #undef yyFlexLexer
// #endif

// Flex expects the signature of yylex to be defined in the macro YY_DECL, and
// the C++ parser expects it to be declared. We can factor both as follows.

#ifndef YY_DECL

#define	YY_DECL	\
 int TXTDeformat::lex()
#endif

using namespace std;

class TXTDeformat: public TXTDeformatFlexLexer {
public:
	TXTDeformat(std::istream* arg_yyin = 0, std::ostream* arg_yyout = 0);
	virtual ~TXTDeformat();

	virtual int lex();

	void set_debug(bool b);

private:

	string wstring2string(wstring wstr) {
		string str(wstr.length(), ' ');
		copy(wstr.begin(), wstr.end(), str.begin());
		return str;
	}

	wstring string2wstring(string str) {
		wstring wstr(str.length(), L' ');
		copy(str.begin(), str.end(), wstr.begin());
		return wstr;
	}

	wstring buffer;
	string symbuf;
	bool isDot, hasWrite_dot, hasWrite_white;
	FILE *formatfile;
	string last;
	int current;
	long int offset;

	vector<long int> offsets;
	vector<wstring> tags;
	vector<int> orders;

	regex_t escape_chars;
	regex_t names_regexp;

	void bufferAppend(wstring &buf, string const &str) {
		symbuf.append(str);

		for (size_t i = 0, limit = symbuf.size(); i < limit;) {
			wchar_t symbol;
			int gap = mbtowc(&symbol, symbuf.c_str() + i, MB_CUR_MAX);
			if (gap == -1) {
				if (i + MB_CUR_MAX < limit) {
					buf += L'?';
					gap = 1;
				} else {
					symbuf = symbuf.substr(i);
					return;
				}
			} else {
				buf += symbol;
			}

			i += gap;
		}

		symbuf = "";
		return;
	}

	void init_escape() {
		if (regcomp(&escape_chars, "[][\\\\/@<>^$]", REG_EXTENDED)) {
			cerr << "ERROR: Illegal regular expression for escape characters"
					<< endl;
			exit(EXIT_FAILURE);
		}
	}

	void init_tagNames() {
		if (regcomp(&names_regexp, "''", REG_EXTENDED)) {
			cerr << "ERROR: Illegal regular expression for tag-names" << endl;
			exit(EXIT_FAILURE);
		}
	}

	string backslash(string const &str) {
		string new_str = "";

		for (unsigned int i = 0; i < str.size(); i++) {
			if (str[i] == '\\') {
				new_str += str[i];
			}
			new_str += str[i];
		}

		return new_str;
	}

	wstring escape(string const &str) {
		regmatch_t pmatch;

		char const *mystring = str.c_str();
		int base = 0;
		wstring result =L"" ;

		while (!regexec(&escape_chars, mystring + base, 1, &pmatch, 0)) {
			bufferAppend(result, str.substr(base, pmatch.rm_so));
			result += L'\\';
			wchar_t micaracter;
			int pos = mbtowc(&micaracter, str.c_str() + base + pmatch.rm_so,MB_CUR_MAX);
	    if (pos == -1) {
	      wcerr << L"Uno" << endl;
	      wcerr << L"Encoding error." << endl;
	      exit(EXIT_FAILURE);
	    }

	    result += micaracter;
	    base += pmatch.rm_eo;
	  }

	  bufferAppend(result, str.substr(base));
	  return result;
	}


	wstring escape(wstring const &str) {
		string dest = "";
		for(size_t i = 0, limit = str.size(); i < limit; i++) {
	    char symbol[MB_CUR_MAX+1];
	    int pos = wctomb(symbol, str[i]);
	    if (pos == -1) {
	      symbol[0]='?';
	      pos = 1;
	    }
	    symbol[pos] = 0;
	    dest.append(symbol);
	  }
	  return escape(dest);
	}

	string get_tagName(string tag) {
	  regmatch_t pmatch;

	  char const *mystring = tag.c_str();
	  string result = "";
	  if (!regexec(&names_regexp, mystring, 1, &pmatch, 0)) {
	    result = tag.substr(pmatch.rm_so, pmatch.rm_eo - pmatch.rm_so);
	    return result;
	  }

	  return "";
	}

	void printBuffer() {
	  if (isDot) {
		  *yyout << ".[]";
		  //fputws_unlocked(L".[]", yyout);
	    isDot = false;
	  }

	  if (buffer.size() > 8192) {
	    string filename = tmpnam(NULL);
	    FILE *largeblock = fopen(filename.c_str(), "w");
	    fputws_unlocked(buffer.c_str(), largeblock);
	    fclose(largeblock);
	    *yyout << '[';
	    *yyout << '@';
	    wchar_t cad[filename.size()];
	    size_t pos = mbstowcs(cad, filename.c_str(), filename.size());

	    if (pos == (size_t) -1) {
	      wcerr << L"Tres" << endl;
	      wcerr << L"Encoding error." << endl;
	      exit(EXIT_FAILURE);
	    }
	    cad[pos] = 0;
	    wstring wcad(cad);
	    *yyout << wstring2string(wcad);
	    *yyout << ']';
	  } else if (buffer.size() > 1) {
		  *yyout << '[';
	    wstring const tmp = escape(buffer);
	    if ((char)tmp[0] == '@') {
	    	*yyout << '\\';
	    }
	    *yyout << wstring2string(tmp);
	    *yyout << ']';
	  } else if(buffer.size() == 1 && buffer[0] != L' ') {
	    *yyout << '[';
	    wstring const tmp = escape(buffer);
	    if((char)(tmp[0]) == '@') {
	    	*yyout << '\\';
	    }
	    *yyout << wstring2string(tmp);
	    *yyout << ']';
	  } else {
		  *yyout << wstring2string(buffer);
	  }

	  buffer = L"";
	}
};

#endif /* TXTDEFORMAT_H_ */
