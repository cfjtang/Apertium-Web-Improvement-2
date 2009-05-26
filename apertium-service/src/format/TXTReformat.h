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

#ifndef TXTREFORMAT_H_
#define TXTREFORMAT_H_

#include <cstdlib>
#include <iostream>
#include <string>

#include <map>
#include <vector>

#include <stdlib.h>
#include <regex.h>

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
// #define yyFlexLexer TXTReformatFlexLexer
// #include <FlexLexer.h>
// #undef yyFlexLexer
// #endif

// Flex expects the signature of yylex to be defined in the macro YY_DECL, and
// the C++ parser expects it to be declared. We can factor both as follows.

#ifndef YY_DECL

#define	YY_DECL	\
 int TXTReformat::lex()
#endif

class TXTReformat: public TXTReformatFlexLexer {
public:
	TXTReformat(std::istream* arg_yyin = 0, std::ostream* arg_yyout = 0);
	virtual ~TXTReformat();

	virtual int lex();

	void set_debug(bool b);

private:

};

#endif /* TXTREFORMAT_H_ */
