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

#ifndef HTMLDEFORMAT_H_
#define HTMLDEFORMAT_H_

#include <cstdlib>

#include <iostream>
#include <sstream>

#include <map>
#include <stack>
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

#include <boost/spirit/include/qi.hpp>
#include <boost/spirit/include/lex_lexer_lexertl.hpp>

#include <boost/spirit/include/phoenix_operator.hpp>
#include <boost/spirit/include/phoenix_statement.hpp>
#include <boost/spirit/include/phoenix_core.hpp>

#include <boost/bind.hpp>
#include <boost/ref.hpp>

#include "format/Format.h"

using namespace std;

using namespace boost::spirit;
using namespace boost::spirit::qi;
using namespace boost::spirit::lex;

/**
 * The HTMLDeformat class implements a HTML format processor. Data should be
 * passed through this processor before beign processed by Apertium.
 */
class HTMLDeformat : public Format {
public:

	template <typename Lexer> struct deformat_tokens : lexer_def<Lexer> {

	    typedef boost::spirit::lex::token_def<std::basic_string<wchar_t>, wchar_t>  token_def;

		token_def begincomment_i, begincomment_s, begincomment_n,
			endcomment_i, endcomment_s, endcomment_n,
			beginscript, endscript,
			beginnotrans, endnotrans,
			d, e, f, g, h, i, j, k, l, m, n, o, p, q,
			whitespace, special, chr_i, chr_ci, chr_cs, chr_cn, chr_s, chr_n;

	   deformat_tokens() {

			begincomment_i = token_def(L"\"<!--\"", ID_BEGINCOMMENT_I);
			begincomment_s = token_def(L"\"<!--\"", ID_BEGINCOMMENT_S);
			begincomment_n = token_def(L"\"<!--\"", ID_BEGINCOMMENT_N);

			endcomment_i = token_def(L"\"-->\"", ID_ENDCOMMENT_I);
			endcomment_s = token_def(L"\"-->\"", ID_ENDCOMMENT_S);
			endcomment_n = token_def(L"\"-->\"", ID_ENDCOMMENT_N);

	        beginscript = token_def(L"\"<script\"(\" \"[^>]*)?\">\"", ID_BEGINSCRIPT);
	        endscript = token_def(L"\"</style\"(\" \"[^>]*)?\">\"", ID_ENDSCRIPT);

	        beginnotrans = token_def(L"\"<apertium-notrans>\"", ID_BEGINNOTRANS);
	        endnotrans = token_def(L"\"</apertium-notrans>\"", ID_ENDNOTRANS);

	        e = token_def(L"\"<br\"(\" \"[^>]*)?\">\"|\"<hr\"(\" \"[^>]*)?\">\"|\"<p\"(\" \"[^>]*)?\">\"", E);
	        f = token_def(L"\"<li\"(\" \"[^>]*)?\">\"|\"<ul\"(\" \"[^>]*)?\">\"|\"<ol\"(\" \"[^>]*)?\">\"", F);
	        g = token_def(L"\"<tr\"(\" \"[^>]*)?\">\"|\"<td\"(\" \"[^>]*)?\">\"|\"<th\"(\" \"[^>]*)?\">\"", G);

	        h = token_def(L"\"</br\"(\" \"[^>]*)?\">\"|\"</hr\"(\" \"[^>]*)?\">\"|\"</p\"(\" \"[^>]*)?\">\"", H);

	        i = token_def(L"\"</li\"(\" \"[^>]*)?\">\"|\"</ul\"(\" \"[^>]*)?\">\"|\"</ol\"(\" \"[^>]*)?\">\"", I);
	        j = token_def(L"\"</tr\"(\" \"[^>]*)?\">\"|\"</td\"(\" \"[^>]*)?\">\"|\"</th\"(\" \"[^>]*)?\">\"", J);
	        k = token_def(L"\"<title\"(\" \"[^>]*)?\">\"|\"<div\"(\" \"[^>]*)?\">\"|\"<option\"(\" \"[^>]*)?\">\"|\"<h\"[1-6](\" \"[^>]*)?\">\"", K);
	        l = token_def(L"\"</title\"(\" \"[^>]*)?\">\"|\"</div\"(\" \"[^>]*)?\">\"|\"</option\"(\" \"[^>]*)?\">\"|\"</h\"[1-6](\" \"[^>]*)?\">\"", L);
	        m = token_def(L"\"<\"(\"img\"|\"link\")(\" \"[^>]*)?\">\"", M);

			n = token_def(L"(\"<!\"|\"<?\")[a-zA-Z][^>]*\">\"", N);
			o = token_def(L"\"<\"[a-zA-Z][^>]*\">\"", O);
			p = token_def(L"\"</\"[a-zA-Z][^>]*\">\"", P);

			q = token_def(L"\"&\"([a-zA-Z]+|\"#x\"[0-9a-fA-F]{1,4}|\"#\"[0-9]{1,8});", Q);

	        whitespace = token_def(L"[ \\n\\t\\r<>{}]", ID_WHITESPACE);
	        special = token_def(L"[\\]\\[^@\\\\/$]", ID_SPECIAL);

	        chr_i = token_def(L".", ID_CHAR_I);

	        chr_ci = token_def(L"\\n|.", ID_CHAR_CI);
	        chr_cs = token_def(L"\\n|.", ID_CHAR_CS);
	        chr_cn = token_def(L"\\n|.", ID_CHAR_CN);

	        chr_s = token_def(L"\\n|.", ID_CHAR_S);
	        chr_n = token_def(L"\\n|.", ID_CHAR_N);
	   }

	   template <typename Self> void def(Self &self) {
		   using boost::spirit::arg_names::_1;

			self = begincomment_i [ set_state(L"COMMENT_I") ]
			               | beginscript [ set_state(L"SCRIPT") ]
			               | beginnotrans [ set_state(L"NOTRANS") ]

	                       | e | f | g | h | i | j | k | l | m
	                       | n | o | p
	                       | q

	                       | whitespace
	                       | special

			               | chr_i;

			self(L"COMMENT_I") = endcomment_i [ set_state(L"INITIAL") ]
			               | chr_ci;

			self(L"COMMENT_S") = endcomment_s [ set_state(L"SCRIPT") ]
			               | chr_cs;

			self(L"COMMENT_N") = endcomment_n [ set_state(L"NOTRANS") ]
			               | chr_cn;

			self(L"SCRIPT") = begincomment_s [ set_state(L"COMMENT_S") ]
						   | endscript [ set_state(L"INITIAL") ]
			               | chr_s;

			self(L"NOTRANS") = begincomment_n [ set_state(L"COMMENT_N") ]
						   | endnotrans [ set_state(L"INITIAL") ]
			               | chr_n;


	   }
	};

	struct func {
		typedef bool result_type;
		template<typename Token> bool operator()(Token const& t, HTMLDeformat *d, std::wostream &o) const {

			//wcout << "id: " << t.id() << " value: " << t.value() << endl;

			wstringstream wss;
			wss << t.value();

			wstring yytext = wss.str();

			switch (t.id()) {

			case ID_BEGINCOMMENT_I:
			case ID_BEGINCOMMENT_S:
			case ID_BEGINCOMMENT_N:
			case ID_BEGINSCRIPT:
			case ID_BEGINNOTRANS:
				d->handleBeginComment(yytext);
				break;

			case ID_ENDCOMMENT_I:
			case ID_ENDCOMMENT_S:
			case ID_ENDCOMMENT_N:
			case ID_ENDSCRIPT:
			case ID_ENDNOTRANS:
				d->handleEndComment(yytext);
				break;

			case E:
			case F:
			case G:
			case H:
			case I:
			case J:
			case K:
			case L:
				d->handleEFGHIJKL(yytext);
				break;

			case M:
			case N:
			case O:
			case P:
				d->handleMNOP(yytext);
				break;

			case Q:
				d->handleQ(yytext);
				break;

			case ID_CHAR_CI:
			case ID_CHAR_CN:
			case ID_CHAR_CS:
			case ID_CHAR_N:
			case ID_CHAR_S:
				d->handleNewLineOrChar(yytext);
				break;

			case ID_CHAR_I:
				d->handleChar(yytext);
				break;

			case ID_WHITESPACE:
				d->handleWhiteSpace(yytext);
				break;

			case ID_SPECIAL:
				d->handleSpecial(yytext);
				break;

			default:
				d->handleDefault(yytext);
				break;
			}

			return(true);
		}
	};

	HTMLDeformat(wstring in = L"", wostream* out = NULL) : yyin(in), yyout(out) {
		reset();
		init_escape();
		init_tagNames();

		S1_init();

		def = new deformat_tokens<lexer_type>();
		l = new lexer<deformat_tokens<lexer_type> >(*def);
	}

	virtual ~HTMLDeformat() {
		delete l;
		delete def;

		regfree(&escape_chars);
		regfree(&names_regexp);
	}

	void reset() {
		last = "";
		buffer = L"";
		isDot = hasWrite_dot = hasWrite_white = false;
		current = 0;
		offset = 0;
	}

	void setYyin(wstring in) {
		yyin = in;
	}

	void setYyout(wostream* out) {
		yyout = out;
	}

	enum token_ids {
		ID_CHARORNEWLINE = lex::min_token_id + 1, ID_WHITESPACE, ID_SPECIAL, ID_CHAR_I, ID_CHAR_CI, ID_CHAR_CS, ID_CHAR_CN,
		ID_CHAR_S, ID_CHAR_N, ID_BEGINCOMMENT_I, ID_BEGINCOMMENT_S, ID_BEGINCOMMENT_N,
		ID_ENDCOMMENT_I, ID_ENDCOMMENT_S, ID_ENDCOMMENT_N,
		ID_BEGINSCRIPT, ID_ENDSCRIPT, ID_BEGINNOTRANS, ID_ENDNOTRANS,
		D, E, F, G, H, I, J, K, L, M, N, O, P, Q,
		ID_EOL, ID_ANY
	};

	void handleBeginComment(wstring &yytext) {
		//wcout << L">> handleBeginComment: " << yytext << endl;
		buffer += yytext;
	}

	void handleEndComment(wstring &yytext) {
		//wcout << L">> handleEndComment: " << yytext << endl;
		last = "buffer";
		buffer += yytext;
	}

	void handleEFGHIJKL(wstring &yytext) {
		//wcout << L">> handleEFGHIJKL: " << yytext << endl;
		isDot = true;
		buffer += yytext;
	}

	void handleMNOP(wstring &yytext) {
		//wcout << L">> handleMNOP: " << yytext << endl;
		buffer += yytext;
	}

	void handleQ(wstring &yytext) {
		//wcout << L">> handleQ: " << yytext << endl;
		if (S1_substitution.find(yytext) != S1_substitution.end()) {
			printBuffer();
			*yyout << S1_substitution[yytext];
			offset += S1_substitution[yytext].size();
			hasWrite_dot = hasWrite_white = true;
		} else {
			last = "buffer";
			buffer += yytext;
		}
	}

	void handleWhiteSpace(wstring &yytext) {
		//wcout << L">> handleWhiteSpace: " << yytext << endl;
		if (last == "open_tag") {
			tags.back() += yytext;
		} else {
			buffer += yytext;
		}
	}

	void handleSpecial(wstring &yytext) {
		//wcout << L">> handleSpecial: " << yytext << endl;
		printBuffer();
		*yyout << L'\\';
		offset++;

		*yyout << yytext;
		offset++;
		hasWrite_dot = hasWrite_white = true;
	}

	void handleNewLineOrChar(wstring &yytext) {
		//wcout << L">> handleNewLineOrChar: " << yytext << endl;
		last = "buffer";
		buffer += yytext;
	}

	void handleChar(wstring &yytext) {
		//wcout << L">> handleChar: " << yytext << endl;
		printBuffer();
		symbuf = "";
		*yyout << yytext;
		offset++;
		hasWrite_dot = hasWrite_white = true;
	}

	void handleDefault(wstring yytext) {
		//wcout << L">> handleDefault: " << yytext << endl;
	}

	virtual bool lex() {
		wchar_t const* first = yyin.data();
		wchar_t const* last = &first[yyin.size()];

		bool ret = tokenize(first, last, *l,
				boost::bind(func(), _1, boost::ref(this), boost::ref(*yyout))
		);

		isDot = true;
		printBuffer();

		reset();

		return(ret);
	}

private:
	map<wstring, wstring, Ltstr> S1_substitution;

	wstring yyin;
	wostream *yyout;

	typedef lexertl_token<wchar_t const*, boost::mpl::vector<std::wstring> > token_type;
	typedef lexertl_actor_lexer<token_type> lexer_type;
	typedef lexer_iterator<deformat_tokens<lexer_type> >::type iterator_type;

	deformat_tokens<lexer_type> *def;
	lexer<deformat_tokens<lexer_type> > *l;

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
		if (regcomp(&escape_chars, "[][^@\\\\/$]", REG_EXTENDED)) {
			cerr << "ERROR: Illegal regular expression for escape characters"
					<< endl;
			exit(EXIT_FAILURE);
		}
	}

	void init_tagNames() {
		if (regcomp(&names_regexp, "[a-zA-Z]+", REG_EXTENDED)) {
			cerr << "ERROR: Illegal regular expression for tag-names" << endl;
			exit(EXIT_FAILURE);
		}
	}

	void S1_init() {
	  S1_substitution[L"&Agrave;"] = L"À";
	  S1_substitution[L"&#192;"] = L"À";
	  S1_substitution[L"&#xC0;"] = L"À";
	  S1_substitution[L"&#xc0;"] = L"À";
	  S1_substitution[L"&Aacute;"] = L"Á";
	  S1_substitution[L"&#193;"] = L"Á";
	  S1_substitution[L"&#xC1;"] = L"Á";
	  S1_substitution[L"&#xc1;"] = L"Á";
	  S1_substitution[L"&Acirc;"] = L"Â";
	  S1_substitution[L"&#194;"] = L"Â";
	  S1_substitution[L"&#xC2;"] = L"Â";
	  S1_substitution[L"&#xc2;"] = L"Â";
	  S1_substitution[L"&Atilde;"] = L"Ã";
	  S1_substitution[L"&#195;"] = L"Ã";
	  S1_substitution[L"&#xC3;"] = L"Ã";
	  S1_substitution[L"&#xc3;"] = L"Ã";
	  S1_substitution[L"&Auml;"] = L"Ä";
	  S1_substitution[L"&#196;"] = L"Ä";
	  S1_substitution[L"&#xC4;"] = L"Ä";
	  S1_substitution[L"&#xc4;"] = L"Ä";
	  S1_substitution[L"&Aring;"] = L"Å";
	  S1_substitution[L"&#197;"] = L"Å";
	  S1_substitution[L"&#xC5;"] = L"Å";
	  S1_substitution[L"&#xc5;"] = L"Å";
	  S1_substitution[L"&AElig;"] = L"Æ";
	  S1_substitution[L"&#198;"] = L"Æ";
	  S1_substitution[L"&#xC6;"] = L"Æ";
	  S1_substitution[L"&#xc6;"] = L"Æ";
	  S1_substitution[L"&Ccedil;"] = L"Ç";
	  S1_substitution[L"&#199;"] = L"Ç";
	  S1_substitution[L"&#xC7;"] = L"Ç";
	  S1_substitution[L"&#xc7;"] = L"Ç";
	  S1_substitution[L"&Egrave;"] = L"È";
	  S1_substitution[L"&#200;"] = L"È";
	  S1_substitution[L"&#xC8;"] = L"È";
	  S1_substitution[L"&#xc8;"] = L"È";
	  S1_substitution[L"&Eacute;"] = L"É";
	  S1_substitution[L"&#201;"] = L"É";
	  S1_substitution[L"&#xC9;"] = L"É";
	  S1_substitution[L"&#xc9;"] = L"É";
	  S1_substitution[L"&Ecirc;"] = L"Ê";
	  S1_substitution[L"&#202;"] = L"Ê";
	  S1_substitution[L"&#xCA;"] = L"Ê";
	  S1_substitution[L"&#xca;"] = L"Ê";
	  S1_substitution[L"&Euml;"] = L"Ë";
	  S1_substitution[L"&#203;"] = L"Ë";
	  S1_substitution[L"&#xCB;"] = L"Ë";
	  S1_substitution[L"&#xcb;"] = L"Ë";
	  S1_substitution[L"&Igrave;"] = L"Ì";
	  S1_substitution[L"&#204;"] = L"Ì";
	  S1_substitution[L"&#xCC;"] = L"Ì";
	  S1_substitution[L"&#xcc;"] = L"Ì";
	  S1_substitution[L"&Iacute;"] = L"Í";
	  S1_substitution[L"&#205;"] = L"Í";
	  S1_substitution[L"&#xCD;"] = L"Í";
	  S1_substitution[L"&#xcd;"] = L"Í";
	  S1_substitution[L"&Icirc;"] = L"Î";
	  S1_substitution[L"&#206;"] = L"Î";
	  S1_substitution[L"&#xCE;"] = L"Î";
	  S1_substitution[L"&#xce;"] = L"Î";
	  S1_substitution[L"&Iuml;"] = L"Ï";
	  S1_substitution[L"&#207;"] = L"Ï";
	  S1_substitution[L"&#xCF;"] = L"Ï";
	  S1_substitution[L"&#xcf;"] = L"Ï";
	  S1_substitution[L"&ETH;"] = L"Ð";
	  S1_substitution[L"&#208;"] = L"Ð";
	  S1_substitution[L"&#xD0;"] = L"Ð";
	  S1_substitution[L"&#xd0;"] = L"Ð";
	  S1_substitution[L"&Ntilde;"] = L"Ñ";
	  S1_substitution[L"&#209;"] = L"Ñ";
	  S1_substitution[L"&#xD1;"] = L"Ñ";
	  S1_substitution[L"&#xd1;"] = L"Ñ";
	  S1_substitution[L"&Ograve;"] = L"Ò";
	  S1_substitution[L"&#210;"] = L"Ò";
	  S1_substitution[L"&#xD2;"] = L"Ò";
	  S1_substitution[L"&#xd2;"] = L"Ò";
	  S1_substitution[L"&Oacute;"] = L"Ó";
	  S1_substitution[L"&#211;"] = L"Ó";
	  S1_substitution[L"&#xD3;"] = L"Ó";
	  S1_substitution[L"&#xd3;"] = L"Ó";
	  S1_substitution[L"&Ocirc;"] = L"Ô";
	  S1_substitution[L"&#212;"] = L"Ô";
	  S1_substitution[L"&#xD4;"] = L"Ô";
	  S1_substitution[L"&#xd4;"] = L"Ô";
	  S1_substitution[L"&Otilde;"] = L"Õ";
	  S1_substitution[L"&#213;"] = L"Õ";
	  S1_substitution[L"&#xD5;"] = L"Õ";
	  S1_substitution[L"&#xd5;"] = L"Õ";
	  S1_substitution[L"&Ouml;"] = L"Ö";
	  S1_substitution[L"&#214;"] = L"Ö";
	  S1_substitution[L"&#xD6;"] = L"Ö";
	  S1_substitution[L"&#xd6;"] = L"Ö";
	  S1_substitution[L"&Oslash;"] = L"Ø";
	  S1_substitution[L"&#216;"] = L"Ø";
	  S1_substitution[L"&#xD8;"] = L"Ø";
	  S1_substitution[L"&#xd8;"] = L"Ø";
	  S1_substitution[L"&Ugrave;"] = L"Ù";
	  S1_substitution[L"&#217;"] = L"Ù";
	  S1_substitution[L"&#xD9;"] = L"Ù";
	  S1_substitution[L"&#xd9;"] = L"Ù";
	  S1_substitution[L"&Uacute;"] = L"Ú";
	  S1_substitution[L"&#218;"] = L"Ú";
	  S1_substitution[L"&#xDA;"] = L"Ú";
	  S1_substitution[L"&#xda;"] = L"Ú";
	  S1_substitution[L"&Ucirc;"] = L"Û";
	  S1_substitution[L"&#219;"] = L"Û";
	  S1_substitution[L"&#xDB;"] = L"Û";
	  S1_substitution[L"&#xdb;"] = L"Û";
	  S1_substitution[L"&Uuml;"] = L"Ü";
	  S1_substitution[L"&#220;"] = L"Ü";
	  S1_substitution[L"&#xDC;"] = L"Ü";
	  S1_substitution[L"&#xdc;"] = L"Ü";
	  S1_substitution[L"&Yacute;"] = L"Ý";
	  S1_substitution[L"&#221;"] = L"Ý";
	  S1_substitution[L"&#xDD;"] = L"Ý";
	  S1_substitution[L"&#xdd;"] = L"Ý";
	  S1_substitution[L"&THORN;"] = L"Þ";
	  S1_substitution[L"&#222;"] = L"Þ";
	  S1_substitution[L"&#xDE;"] = L"Þ";
	  S1_substitution[L"&#xde;"] = L"Þ";
	  S1_substitution[L"&szlig;"] = L"ß";
	  S1_substitution[L"&#223;"] = L"ß";
	  S1_substitution[L"&#xDF;"] = L"ß";
	  S1_substitution[L"&#xdf;"] = L"ß";
	  S1_substitution[L"&agrave;"] = L"à";
	  S1_substitution[L"&#224;"] = L"à";
	  S1_substitution[L"&#xE0;"] = L"à";
	  S1_substitution[L"&#xe0;"] = L"à";
	  S1_substitution[L"&aacute;"] = L"á";
	  S1_substitution[L"&#225;"] = L"á";
	  S1_substitution[L"&#xE1;"] = L"á";
	  S1_substitution[L"&#xe1;"] = L"á";
	  S1_substitution[L"&acirc;"] = L"â";
	  S1_substitution[L"&#226;"] = L"â";
	  S1_substitution[L"&#xE2;"] = L"â";
	  S1_substitution[L"&#xe2;"] = L"â";
	  S1_substitution[L"&atilde;"] = L"ã";
	  S1_substitution[L"&#227;"] = L"ã";
	  S1_substitution[L"&#xE3;"] = L"ã";
	  S1_substitution[L"&#xe3;"] = L"ã";
	  S1_substitution[L"&auml;"] = L"ä";
	  S1_substitution[L"&#228;"] = L"ä";
	  S1_substitution[L"&#xE4;"] = L"ä";
	  S1_substitution[L"&#xe4;"] = L"ä";
	  S1_substitution[L"&aring;"] = L"å";
	  S1_substitution[L"&#229;"] = L"å";
	  S1_substitution[L"&#xE5;"] = L"å";
	  S1_substitution[L"&#xe5;"] = L"å";
	  S1_substitution[L"&aelig;"] = L"æ";
	  S1_substitution[L"&#230;"] = L"æ";
	  S1_substitution[L"&#xE6;"] = L"æ";
	  S1_substitution[L"&#xe6;"] = L"æ";
	  S1_substitution[L"&ccedil;"] = L"ç";
	  S1_substitution[L"&#231;"] = L"ç";
	  S1_substitution[L"&#xE7;"] = L"ç";
	  S1_substitution[L"&#xe7;"] = L"ç";
	  S1_substitution[L"&egrave;"] = L"è";
	  S1_substitution[L"&#232;"] = L"è";
	  S1_substitution[L"&#xE8;"] = L"è";
	  S1_substitution[L"&#xe8;"] = L"è";
	  S1_substitution[L"&eacute;"] = L"é";
	  S1_substitution[L"&#233;"] = L"é";
	  S1_substitution[L"&#xE9;"] = L"é";
	  S1_substitution[L"&#xe9;"] = L"é";
	  S1_substitution[L"&ecirc;"] = L"ê";
	  S1_substitution[L"&#234;"] = L"ê";
	  S1_substitution[L"&#xEA;"] = L"ê";
	  S1_substitution[L"&#xea;"] = L"ê";
	  S1_substitution[L"&euml;"] = L"ë";
	  S1_substitution[L"&#235;"] = L"ë";
	  S1_substitution[L"&#xEB;"] = L"ë";
	  S1_substitution[L"&#xeb;"] = L"ë";
	  S1_substitution[L"&igrave;"] = L"ì";
	  S1_substitution[L"&#236;"] = L"ì";
	  S1_substitution[L"&#xEC;"] = L"ì";
	  S1_substitution[L"&#xec;"] = L"ì";
	  S1_substitution[L"&iacute;"] = L"í";
	  S1_substitution[L"&#237;"] = L"í";
	  S1_substitution[L"&#xED;"] = L"í";
	  S1_substitution[L"&#xed;"] = L"í";
	  S1_substitution[L"&icirc;"] = L"î";
	  S1_substitution[L"&#238;"] = L"î";
	  S1_substitution[L"&#xEE;"] = L"î";
	  S1_substitution[L"&#xee;"] = L"î";
	  S1_substitution[L"&iuml;"] = L"ï";
	  S1_substitution[L"&#239;"] = L"ï";
	  S1_substitution[L"&#xEF;"] = L"ï";
	  S1_substitution[L"&#xef;"] = L"ï";
	  S1_substitution[L"&eth;"] = L"ð";
	  S1_substitution[L"&#240;"] = L"ð";
	  S1_substitution[L"&#xF0;"] = L"ð";
	  S1_substitution[L"&#xf0;"] = L"ð";
	  S1_substitution[L"&ntilde;"] = L"ñ";
	  S1_substitution[L"&#241;"] = L"ñ";
	  S1_substitution[L"&#xF1;"] = L"ñ";
	  S1_substitution[L"&#xf1;"] = L"ñ";
	  S1_substitution[L"&ograve;"] = L"ò";
	  S1_substitution[L"&#242;"] = L"ò";
	  S1_substitution[L"&#xF2;"] = L"ò";
	  S1_substitution[L"&#xf2;"] = L"ò";
	  S1_substitution[L"&oacute;"] = L"ó";
	  S1_substitution[L"&#243;"] = L"ó";
	  S1_substitution[L"&#xF3;"] = L"ó";
	  S1_substitution[L"&#xf3;"] = L"ó";
	  S1_substitution[L"&ocirc;"] = L"ô";
	  S1_substitution[L"&#244;"] = L"ô";
	  S1_substitution[L"&#xF4;"] = L"ô";
	  S1_substitution[L"&#xf4;"] = L"ô";
	  S1_substitution[L"&otilde;"] = L"õ";
	  S1_substitution[L"&#245;"] = L"õ";
	  S1_substitution[L"&#xF5;"] = L"õ";
	  S1_substitution[L"&#xf5;"] = L"õ";
	  S1_substitution[L"&ouml;"] = L"ö";
	  S1_substitution[L"&#246;"] = L"ö";
	  S1_substitution[L"&#xF6;"] = L"ö";
	  S1_substitution[L"&#xf6;"] = L"ö";
	  S1_substitution[L"&oslash;"] = L"ø";
	  S1_substitution[L"&#248;"] = L"ø";
	  S1_substitution[L"&#xF8;"] = L"ø";
	  S1_substitution[L"&#xf8;"] = L"ø";
	  S1_substitution[L"&ugrave;"] = L"ù";
	  S1_substitution[L"&#249;"] = L"ù";
	  S1_substitution[L"&#xF9;"] = L"ù";
	  S1_substitution[L"&#xf9;"] = L"ù";
	  S1_substitution[L"&uacute;"] = L"ú";
	  S1_substitution[L"&#250;"] = L"ú";
	  S1_substitution[L"&#xFA;"] = L"ú";
	  S1_substitution[L"&#xfa;"] = L"ú";
	  S1_substitution[L"&ucirc;"] = L"û";
	  S1_substitution[L"&#251;"] = L"û";
	  S1_substitution[L"&#xFB;"] = L"û";
	  S1_substitution[L"&#xfb;"] = L"û";
	  S1_substitution[L"&uuml;"] = L"ü";
	  S1_substitution[L"&#252;"] = L"ü";
	  S1_substitution[L"&#xFC;"] = L"ü";
	  S1_substitution[L"&#xfc;"] = L"ü";
	  S1_substitution[L"&yacute;"] = L"ý";
	  S1_substitution[L"&#253;"] = L"ý";
	  S1_substitution[L"&#xFD;"] = L"ý";
	  S1_substitution[L"&#xfd;"] = L"ý";
	  S1_substitution[L"&thorn;"] = L"þ";
	  S1_substitution[L"&#254;"] = L"þ";
	  S1_substitution[L"&#xFE;"] = L"þ";
	  S1_substitution[L"&#xfe;"] = L"þ";
	  S1_substitution[L"&yuml;"] = L"ÿ";
	  S1_substitution[L"&#255;"] = L"ÿ";
	  S1_substitution[L"&#xFF;"] = L"ÿ";
	  S1_substitution[L"&#xff;"] = L"ÿ";
	  S1_substitution[L"&middot;"] = L"·";
	  S1_substitution[L"&#183;"] = L"·";
	  S1_substitution[L"&#xB7;"] = L"·";
	  S1_substitution[L"&#xb7;"] = L"·";
	  S1_substitution[L"&laquo;"] = L"«";
	  S1_substitution[L"&#171;"] = L"«";
	  S1_substitution[L"&#xAB;"] = L"«";
	  S1_substitution[L"&#xab;"] = L"«";
	  S1_substitution[L"&raquo;"] = L"»";
	  S1_substitution[L"&#187;"] = L"»";
	  S1_substitution[L"&#xBB;"] = L"»";
	  S1_substitution[L"&#xbb;"] = L"»";
	  S1_substitution[L"&rsquo;"] = L"'";
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
		wstring result = L"" ;

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
			*yyout << L".[]";
			isDot = false;
		}
		if (buffer.size() > 8192) {
			string filename = tmpnam(NULL);
			FILE *largeblock = fopen(filename.c_str(), "w");
			fputws_unlocked(buffer.c_str(), largeblock);
			fclose(largeblock);
			*yyout << L'[';
			*yyout << L'@';
			wchar_t cad[filename.size()];
			size_t pos = mbstowcs(cad, filename.c_str(), filename.size());
			if (pos == (size_t) -1) {
				wcerr << L"Tres" << endl;
				wcerr << L"Encoding error." << endl;
				exit(EXIT_FAILURE);
			}
			cad[pos] = 0;
			wstring wcad(cad);
			*yyout << wcad;
			*yyout << L']';
		} else if (buffer.size() > 1) {
			*yyout << L'[';
			wstring const tmp = escape(buffer);
			if (tmp[0] == L'@') {
				*yyout << L'\\';
			}
			*yyout << tmp;
			*yyout << L']';
		} else if(buffer.size() == 1 && buffer[0] != L' ') {
			*yyout << L'[';
			wstring const tmp = escape(buffer);
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

#endif /* HTMLDEFORMAT_H_ */
