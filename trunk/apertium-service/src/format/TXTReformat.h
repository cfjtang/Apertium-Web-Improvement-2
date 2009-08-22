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
#ifdef WIN32
#if defined(__MINGW32__)
#define __MSVCRT_VERSION__  0x0800
#endif
#include <io.h>
#include <fcntl.h>
#endif

#include <boost/spirit/include/lex_lexer_lexertl.hpp>
#include <boost/bind.hpp>
#include <boost/ref.hpp>

using namespace std;
using namespace boost::spirit;
using namespace boost::spirit::lex;

/**
 * The TXTReformat class implements a text format processor. It restores the
 * original formatting the text had before being passed through the Deformat
 * processor.
 */
class TXTReformat {
public:

	template <typename Lexer> struct reformat_tokens: lexer_def<Lexer> {
		template<typename Self>
		void def(Self& self) {
			self.add
			(L"\"\\[\"|\"\\]\"", ID_SQUARES)
			(L"\"\\[@\"[^\\]]+\"\\]\"", ID_SQUARED)
			(L"\"\\[\\@\"", ID_SQUAREAT)
			(L"\".\\[\\]\"", ID_DOTSQUARES)
			(L"\"\\\\\"[\\]\\[\\\\/@<>^$]", ID_SPECIAL)
			(L".|\\n", ID_CHARORNEWLINE);
		}
	};

	TXTReformat(wstring in = L"", wostream* out = NULL) : yyin(in), yyout(out) {
		def = new reformat_tokens<lexer_type>();
		l = new lexer<reformat_tokens<lexer_type> >(*def);
	}

	virtual ~TXTReformat() {
		delete l;
		delete def;
	}

	void setYyin(wstring in) {
		yyin = in;
	}

	void setYyout(wostream* out) {
		yyout = out;
	}

	enum token_ids {
		ID_SQUARES = lex::min_token_id + 1, ID_SQUARED, ID_SQUAREAT, ID_DOTSQUARES, ID_SPECIAL, ID_CHARORNEWLINE
	};

	void handleSquares(wstring yytext) { }

	void handleSquared(wstring yytext) {
		 string filename(yytext.length(), ' ');
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

	void handleSquareAt(wstring yytext) {
		 *yyout << L'@';
	}

	void handleDotSquares(wstring yytext) { }

	void handleSpecial(wstring yytext) {
		 *yyout << yytext.substr(1, yytext.size() - 1);
	}

	void handleCharOrNewLine(wstring yytext) {
		 *yyout << yytext;
	}

	struct func {
		typedef bool result_type;
		template<typename Token> bool operator()(Token const& t, TXTReformat *r, std::wostream &o) const {
			//wcout << L"id is: " << t.id() << endl;
			//wcout << L"value is: " << t.value() << endl;

			wstringstream wss;
			wss << t.value();

			wstring yytext = wss.str();

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
		//typedef lexertl_token<wchar_t const*, boost::mpl::vector<std::wstring> > token_type;
		//typedef lexertl_lexer<token_type> lexer_type;
		//typedef lexer_iterator<reformat_tokens<lexer_type> >::type iterator_type;

		//reformat_tokens<lexer_type> def;
		//lexer<reformat_tokens<lexer_type> > l(def);

		wchar_t const* first = yyin.data();
		wchar_t const* last = &first[yyin.size()];

		bool ret = tokenize(first,
				last,
				//make_lexer(def),
				//lexer<reformat_tokens<lexer_type> >(def),
				*l,
				boost::bind(func(), _1, boost::ref(this), boost::ref(*yyout))
		);

		return(ret);
	}

private:
	wstring yyin;
	wostream *yyout;

	typedef lexertl_token<wchar_t const*, boost::mpl::vector<std::wstring> > token_type;
	typedef lexertl_lexer<token_type> lexer_type;
	typedef lexer_iterator<reformat_tokens<lexer_type> >::type iterator_type;

	reformat_tokens<lexer_type> *def;
	lexer<reformat_tokens<lexer_type> > *l;
};

#endif /* DEFORMAT_H_ */
