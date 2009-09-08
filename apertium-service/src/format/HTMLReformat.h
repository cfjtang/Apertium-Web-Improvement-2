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

#ifndef HTMLREFORMAT_H_
#define HTMLREFORMAT_H_

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

#include <boost/spirit/include/lex_lexer_lexertl.hpp>
#include <boost/bind.hpp>
#include <boost/ref.hpp>

#include "format/Format.h"

using namespace std;
using namespace boost::spirit;
using namespace boost::spirit::lex;

/**
 * The HTMLReformat class implements a HTML format processor. It restores the
 * original formatting the text had before being passed through the HTMLDeformat
 * processor.
 */
class HTMLReformat : public Format {
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

	HTMLReformat(wstring in = L"", wostream* out = NULL) : yyin(in), yyout(out) {
		S1_init();

		def = new reformat_tokens<lexer_type>();
		l = new lexer<reformat_tokens<lexer_type> >(*def);
	}

	virtual ~HTMLReformat() {
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

	void handleSquares(wstring &yytext) { }

	void handleSquared(wstring &yytext) {
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

	void handleSquareAt(wstring &yytext) {
		 *yyout << L'@';
	}

	void handleDotSquares(wstring &yytext) { }

	void handleSpecial(wstring &yytext) {
		 *yyout << yytext.substr(1, yytext.size() - 1);
	}

	void handleCharOrNewLine(wstring &yytext) {
		if (S1_substitution.find(yytext) != S1_substitution.end()) {
			*yyout << S1_substitution[yytext];
		} else {
			*yyout << yytext;
		}
	}

	struct func {
		typedef bool result_type;
		template<typename Token> bool operator()(Token const& t, HTMLReformat *r, std::wostream &o) const {
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
	map<wstring, wstring, Ltstr> S1_substitution;

	wstring yyin;
	wostream *yyout;

	typedef lexertl_token<wchar_t const*, boost::mpl::vector<std::wstring> > token_type;
	typedef lexertl_lexer<token_type> lexer_type;
	typedef lexer_iterator<reformat_tokens<lexer_type> >::type iterator_type;

	reformat_tokens<lexer_type> *def;
	lexer<reformat_tokens<lexer_type> > *l;

	void S1_init() {
	  S1_substitution[L"À"] = L"&Agrave;";
	  S1_substitution[L"Á"] = L"&Aacute;";
	  S1_substitution[L"Â"] = L"&Acirc;";
	  S1_substitution[L"Ã"] = L"&Atilde;";
	  S1_substitution[L"Ä"] = L"&Auml;";
	  S1_substitution[L"Å"] = L"&Aring;";
	  S1_substitution[L"Æ"] = L"&AElig;";
	  S1_substitution[L"Ç"] = L"&Ccedil;";
	  S1_substitution[L"È"] = L"&Egrave;";
	  S1_substitution[L"É"] = L"&Eacute;";
	  S1_substitution[L"Ê"] = L"&Ecirc;";
	  S1_substitution[L"Ë"] = L"&Euml;";
	  S1_substitution[L"Ì"] = L"&Igrave;";
	  S1_substitution[L"Í"] = L"&Iacute;";
	  S1_substitution[L"Î"] = L"&Icirc;";
	  S1_substitution[L"Ï"] = L"&Iuml;";
	  S1_substitution[L"Ð"] = L"&ETH;";
	  S1_substitution[L"Ñ"] = L"&Ntilde;";
	  S1_substitution[L"Ò"] = L"&Ograve;";
	  S1_substitution[L"Ó"] = L"&Oacute;";
	  S1_substitution[L"Ô"] = L"&Ocirc;";
	  S1_substitution[L"Õ"] = L"&Otilde;";
	  S1_substitution[L"Ö"] = L"&Ouml;";
	  S1_substitution[L"Ø"] = L"&Oslash;";
	  S1_substitution[L"Ù"] = L"&Ugrave;";
	  S1_substitution[L"Ú"] = L"&Uacute;";
	  S1_substitution[L"Û"] = L"&Ucirc;";
	  S1_substitution[L"Ü"] = L"&Uuml;";
	  S1_substitution[L"Ý"] = L"&Yacute;";
	  S1_substitution[L"Þ"] = L"&THORN;";
	  S1_substitution[L"ß"] = L"&szlig;";
	  S1_substitution[L"à"] = L"&agrave;";
	  S1_substitution[L"á"] = L"&aacute;";
	  S1_substitution[L"â"] = L"&acirc;";
	  S1_substitution[L"ã"] = L"&atilde;";
	  S1_substitution[L"ä"] = L"&auml;";
	  S1_substitution[L"å"] = L"&aring;";
	  S1_substitution[L"æ"] = L"&aelig;";
	  S1_substitution[L"ç"] = L"&ccedil;";
	  S1_substitution[L"è"] = L"&egrave;";
	  S1_substitution[L"é"] = L"&eacute;";
	  S1_substitution[L"ê"] = L"&ecirc;";
	  S1_substitution[L"ë"] = L"&euml;";
	  S1_substitution[L"ì"] = L"&igrave;";
	  S1_substitution[L"í"] = L"&iacute;";
	  S1_substitution[L"î"] = L"&icirc;";
	  S1_substitution[L"ï"] = L"&iuml;";
	  S1_substitution[L"ð"] = L"&eth;";
	  S1_substitution[L"ñ"] = L"&ntilde;";
	  S1_substitution[L"ò"] = L"&ograve;";
	  S1_substitution[L"ó"] = L"&oacute;";
	  S1_substitution[L"ô"] = L"&ocirc;";
	  S1_substitution[L"õ"] = L"&otilde;";
	  S1_substitution[L"ö"] = L"&ouml;";
	  S1_substitution[L"ø"] = L"&oslash;";
	  S1_substitution[L"ù"] = L"&ugrave;";
	  S1_substitution[L"ú"] = L"&uacute;";
	  S1_substitution[L"û"] = L"&ucirc;";
	  S1_substitution[L"ü"] = L"&uuml;";
	  S1_substitution[L"ý"] = L"&yacute;";
	  S1_substitution[L"þ"] = L"&thorn;";
	  S1_substitution[L"ÿ"] = L"&yuml;";
	  S1_substitution[L"·"] = L"&middot;";
	  S1_substitution[L"«"] = L"&laquo;";
	  S1_substitution[L"»"] = L"&raquo;";
	}

};

#endif /* HTMLREFORMAT_H_ */
