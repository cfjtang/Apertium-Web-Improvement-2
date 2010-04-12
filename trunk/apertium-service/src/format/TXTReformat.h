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
 * The TXTReformat class implements a text format processor. It restores the
 * original formatting the text had before being passed through the Deformat
 * processor.
 */

// XXX: still hacky

#ifndef TXTREFORMAT_H_
#define TXTREFORMAT_H_

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
 * The TXTReformat class implements a text format processor. It restores the
 * original formatting the text had before being passed through the TXTDeformat
 * processor.
 */
class TXTReformat : public Format {
public:

	enum token_ids {
		ID_SQUARES = lex::min_token_id + 1, ID_SQUARED, ID_SQUAREAT, ID_DOTSQUARES, ID_SPECIAL, ID_CHARORNEWLINE
	};

	template <typename Lexer> struct my_reformat_tokens : lex::lexer<Lexer> {
		my_reformat_tokens() {
			this->self.add
			(L"\"\\[\"|\"\\]\"", ID_SQUARES)
			(L"\"\\[@\"[^\\]]+\"\\]\"", ID_SQUARED)
			(L"\"\\[\\@\"", ID_SQUAREAT)
			(L"\".\\[\\]\"", ID_DOTSQUARES)
			(L"\"\\\\\"[\\]\\[\\\\/@<>^${}]", ID_SPECIAL)
			(L".|\\n", ID_CHARORNEWLINE);
		}
	};

	TXTReformat(std::wstring in = L"", std::wostream* out = NULL) : yyin(in), yyout(out) {
		def = new my_reformat_tokens<lexer_type>();
	}

	virtual ~TXTReformat() {
		delete def;
	}

	void setYyin(std::wstring in) {
		yyin = in;
	}

	void setYyout(std::wostream* out) {
		yyout = out;
	}

	void handleSquares(std::wstring &yytext) { }

	void handleSquared(std::wstring &yytext) {
		std::string filename(yytext.length(), ' ');
		 copy(yytext.begin(), yytext.end(), filename.begin());

		 filename = filename.substr(2, filename.size() - 3);

		 FILE *temp = fopen(filename.c_str(), "r");

		 wint_t mychar;

		#ifdef WIN32
		 _setmode(_fileno(temp), _O_U8TEXT);
		#endif

		 if (!temp) {
		  cerr << "ERROR: File '" << filename <<"' not found." << endl;
		  exit(EXIT_FAILURE);
		 }

		 while(static_cast<int>(mychar = fgetwc_unlocked(temp)) != EOF) {
		  *yyout << mychar;
		 }

		 fclose(temp);
		 unlink(filename.c_str());
	}

	void handleSquareAt(std::wstring &yytext) {
		 *yyout << L'@';
	}

	void handleDotSquares(std::wstring &yytext) { }

	void handleSpecial(std::wstring &yytext) {
		 *yyout << yytext.substr(1, yytext.size() - 1);
	}

	void handleCharOrNewLine(std::wstring &yytext) {
		 *yyout << yytext;
	}

	struct func {
		typedef bool result_type;
		template<typename Token> bool operator()(Token const& t, TXTReformat *r, std::wostream &o) const {
			std::wstringstream wss;
			wss << t.value();

			std::wstring yytext = wss.str();

			switch (t.id()) {
			case ID_SQUARES:
				r->handleSquares(yytext);
				break;
			case ID_SQUARED:
				r->handleSquared(yytext);
				break;
			case ID_SQUAREAT:
				r->handleSquareAt(yytext);
				break;
			case ID_DOTSQUARES:
				r->handleDotSquares(yytext);
				break;
			case ID_SPECIAL:
				r->handleSpecial(yytext);
				break;
			case ID_CHARORNEWLINE:
				r->handleCharOrNewLine(yytext);
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
				*def,
				boost::bind(func(), _1, boost::ref(this), boost::ref(*yyout))
		);

		return(ret);
	}

private:
	std::wstring yyin;
	std::wostream *yyout;

	typedef lex::lexertl::token<wchar_t const*, boost::mpl::vector<std::wstring> > token_type;
	typedef lex::lexertl::lexer<token_type> lexer_type;

	typedef my_reformat_tokens<lexer_type> reformat_tokens;
	typedef reformat_tokens::iterator_type iterator_type;

	my_reformat_tokens<lexer_type> *def;
};

#endif /* TXTREFORMAT_H_ */
