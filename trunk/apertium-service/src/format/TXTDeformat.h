/**
 * @file
 * @author  Pasquale Minervini <p.minervini@gmail.com>
 * @version 1.0
 *
 * @section LICENSE
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
 *
 * @section DESCRIPTION
 *
 * The TXTDeformat class implements a text format processor. Data should be
 * passed through this processor before beign processed by Apertium.
 */

// XXX: still hacky

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

#ifdef _MSC_VER
#include <io.h>
#include <fcntl.h>
#endif

#include <boost/spirit/include/qi.hpp>
#include <boost/spirit/include/lex_lexertl.hpp>
#include <boost/spirit/include/phoenix_operator.hpp>

#include <boost/bind.hpp>
#include <boost/ref.hpp>

#include "format/Format.h"

namespace lex = boost::spirit::lex;

/**
 * The TXTDeformat class implements a text format processor. Data should be
 * passed through this processor before being processed by Apertium.
 */
class TXTDeformat : public Format {
public:

	enum token_ids {
		ID_NEWLINES = lex::min_token_id + 1, ID_WHITESPACE, ID_SPECIAL, ID_CHAR
	};

	template <typename Lexer> struct my_deformat_tokens : lex::lexer<Lexer> {
		my_deformat_tokens() {
			this->self.add
			(L"((\"\\n\\n\")|(\"\\r\\n\\r\\n\"))+", ID_NEWLINES)
			(L"[ \\n\\t\\r]", ID_WHITESPACE)
			(L"[\\]\\[\\\\/@<>^${}]", ID_SPECIAL)
			(L".", ID_CHAR);
		}
	};

	TXTDeformat(std::wstring in = L"", std::wostream* out = NULL) : yyin(in), yyout(out) {
		reset();
		init_escape();
		init_tagNames();

		def = new my_deformat_tokens<lexer_type>();
	}

	virtual ~TXTDeformat() {
		delete def;

		regfree(&escape_chars);
		regfree(&names_regexp);
	}

	void setYyin(std::wstring in) {
		yyin = in;
	}

	void setYyout(std::wostream* out) {
		yyout = out;
	}

	void handleNewLines(std::wstring &yytext) {
		isDot = true;
		buffer += yytext;
	}

	void handleSpecial(std::wstring &yytext) {
		printBuffer();
		*yyout << L'\\';
		offset++;

		*yyout << yytext;
		offset++;
		hasWrite_dot = hasWrite_white = true;
	}

	void handleWhiteSpace(std::wstring &yytext) {
		if (last == "open_tag") {
			tags.back() += yytext;
		} else {
			buffer += yytext;
		}
	}

	void handleChar(std::wstring &yytext) {
		printBuffer();
		*yyout << yytext;
		offset++;
		hasWrite_dot = hasWrite_white = true;
	}

	struct func {
		typedef bool result_type;
		template<typename Token> bool operator()(Token const& t, TXTDeformat *d, std::wostream &o) const {
			std::wstringstream wss;
			wss << t.value();

			std::wstring yytext = wss.str();

			switch (t.id()) {
			case ID_NEWLINES:
				d->handleNewLines(yytext);
				break;
			case ID_SPECIAL:
				d->handleSpecial(yytext);
				break;
			case ID_WHITESPACE:
				d->handleWhiteSpace(yytext);
				break;
			case ID_CHAR:
				d->handleChar(yytext);
				break;
			}

			return(true);
		}
	};

	virtual bool lex() {
		wchar_t const* first = yyin.data();
		wchar_t const* last = &first[yyin.size()];

		bool ret = tokenize(first,
				last,
				//make_lexer(def),
				//lexer<deformat_tokens<lexer_type> >(def),
				*def,
				boost::bind(func(), _1, boost::ref(this), boost::ref(*yyout))
		);

		isDot = true;
		printBuffer();

		reset();

		return(ret);
	}

private:
	std::wstring yyin;
	std::wostream *yyout;

	typedef lex::lexertl::token<wchar_t const*, boost::mpl::vector<std::wstring> > token_type;
	typedef lex::lexertl::lexer<token_type> lexer_type;

	typedef my_deformat_tokens<lexer_type> deformat_tokens;
	typedef deformat_tokens::iterator_type iterator_type;

	my_deformat_tokens<lexer_type> *def;

	std::wstring buffer;
	std::string symbuf;
	bool isDot, hasWrite_dot, hasWrite_white;
	FILE *formatfile;
	std::string last;
	int current;
	long int offset;

	std::vector<long int> offsets;
	std::vector<std::wstring> tags;
	std::vector<int> orders;

	regex_t escape_chars;
	regex_t names_regexp;

	void reset() {
		last = "";
		buffer = L"";
		isDot = hasWrite_dot = hasWrite_white = false;
		current = 0;
		offset = 0;
	}

	void bufferAppend(std::wstring &buf, std::string const &str) {
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

	std::string backslash(std::string const &str) {
		std::string new_str = "";
		for (unsigned int i = 0; i < str.size(); i++) {
			if (str[i] == '\\') {
				new_str += str[i];
			}
			new_str += str[i];
		}
		return new_str;
	}

	std::wstring escape(std::string const &str) {
		regmatch_t pmatch;

		char const *mystring = str.c_str();
		int base = 0;
		std::wstring result = L"" ;

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

	std::wstring escape(std::wstring const &str) {
		std::string dest = "";
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

	std::string get_tagName(std::string tag) {
		regmatch_t pmatch;
		char const *mystring = tag.c_str();
		std::string result = "";
		if (!regexec(&names_regexp, mystring, 1, &pmatch, 0)) {
			result = tag.substr(pmatch.rm_so, pmatch.rm_eo - pmatch.rm_so);
			return result;
		}
		return "";
	}

	void printBuffer() {
		if (isDot) {
			*yyout << L".[]";
			isDot = false;
		}
		if (buffer.size() > 8192) {
			std::string filename = tmpnam(NULL);
			FILE *largeblock = fopen(filename.c_str(), "w");
			fputws_unlocked(buffer.c_str(), largeblock);
			fclose(largeblock);
			*yyout << L'[';
			*yyout << L'@';
			wchar_t cad[filename.size()];
			size_t pos = mbstowcs(cad, filename.c_str(), filename.size());
			if (pos == (size_t) -1) {
				std::wcerr << L"Tres" << endl;
				std::wcerr << L"Encoding error." << endl;
				exit(EXIT_FAILURE);
			}
			cad[pos] = 0;
			std::wstring wcad(cad);
			*yyout << wcad;
			*yyout << L']';
		} else if (buffer.size() > 1) {
			*yyout << L'[';
			std::wstring const tmp = escape(buffer);
			if (tmp[0] == L'@') {
				*yyout << L'\\';
			}
			*yyout << tmp;
			*yyout << L']';
		} else if(buffer.size() == 1 && buffer[0] != L' ') {
			*yyout << L'[';
			std::wstring const tmp = escape(buffer);
			if(tmp[0] == L'@') {
				*yyout << L'\\';
			}
			*yyout << tmp;
			*yyout << L']';
		} else {
			*yyout << buffer;
		}
		buffer = L"";
	}
};

#endif /* TXTDEFORMAT_H_ */
