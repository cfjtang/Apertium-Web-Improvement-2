/*
* Copyright (C) 2007-2010, GrammarSoft ApS
* Developed by Tino Didriksen <tino@didriksen.cc>
* Design by Eckhard Bick <eckhard.bick@mail.dk>, Tino Didriksen <tino@didriksen.cc>
*
* This file is part of VISL CG-3
*
* VISL CG-3 is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* VISL CG-3 is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with VISL CG-3.  If not, see <http://www.gnu.org/licenses/>.
*/

#include "Strings.h"

namespace CG3 {

UnicodeString flags[FLAGS_COUNT] = {
	UNICODE_STRING_SIMPLE("NEAREST"),
	UNICODE_STRING_SIMPLE("ALLOWLOOP"),
	UNICODE_STRING_SIMPLE("DELAYED"),
	UNICODE_STRING_SIMPLE("IMMEDIATE"),
	UNICODE_STRING_SIMPLE("LOOKDELETED"),
	UNICODE_STRING_SIMPLE("LOOKDELAYED"),
	UNICODE_STRING_SIMPLE("UNSAFE"),
	UNICODE_STRING_SIMPLE("SAFE"),
	UNICODE_STRING_SIMPLE("REMEMBERX"),
	UNICODE_STRING_SIMPLE("RESETX"),
	UNICODE_STRING_SIMPLE("KEEPORDER"),
	UNICODE_STRING_SIMPLE("VARYORDER"),
	UNICODE_STRING_SIMPLE("ENCL_INNER"),
	UNICODE_STRING_SIMPLE("ENCL_OUTER"),
	UNICODE_STRING_SIMPLE("ENCL_FINAL"),
	UNICODE_STRING_SIMPLE("ENCL_ANY"),
	UNICODE_STRING_SIMPLE("ALLOWCROSS")
};

UnicodeString keywords[KEYWORD_COUNT] = {
	UNICODE_STRING_SIMPLE("1f283fc29adb937a892e09bbc124b85c this is a dummy keyword to hold position 0"),
	UNICODE_STRING_SIMPLE("SETS"),
	UNICODE_STRING_SIMPLE("LIST"),
	UNICODE_STRING_SIMPLE("SET"),
	UNICODE_STRING_SIMPLE("DELIMITERS"),
	UNICODE_STRING_SIMPLE("SOFT-DELIMITERS"),
	UNICODE_STRING_SIMPLE("PREFERRED-TARGETS"),
	UNICODE_STRING_SIMPLE("MAPPING-PREFIX"),
	UNICODE_STRING_SIMPLE("MAPPINGS"),
	UNICODE_STRING_SIMPLE("CONSTRAINTS"),
	UNICODE_STRING_SIMPLE("CORRECTIONS"),
	UNICODE_STRING_SIMPLE("SECTION"),
	UNICODE_STRING_SIMPLE("BEFORE-SECTIONS"),
	UNICODE_STRING_SIMPLE("AFTER-SECTIONS"),
	UNICODE_STRING_SIMPLE("NULL-SECTION"),
	UNICODE_STRING_SIMPLE("ADD"),
	UNICODE_STRING_SIMPLE("MAP"),
	UNICODE_STRING_SIMPLE("REPLACE"),
	UNICODE_STRING_SIMPLE("SELECT"),
	UNICODE_STRING_SIMPLE("REMOVE"),
	UNICODE_STRING_SIMPLE("IFF"),
	UNICODE_STRING_SIMPLE("APPEND"),
	UNICODE_STRING_SIMPLE("SUBSTITUTE"),
	UNICODE_STRING_SIMPLE("START"),
	UNICODE_STRING_SIMPLE("END"),
	UNICODE_STRING_SIMPLE("ANCHOR"),
	UNICODE_STRING_SIMPLE("EXECUTE"),
	UNICODE_STRING_SIMPLE("JUMP"),
	UNICODE_STRING_SIMPLE("REMVARIABLE"),
	UNICODE_STRING_SIMPLE("SETVARIABLE"),
	UNICODE_STRING_SIMPLE("DELIMIT"),
	UNICODE_STRING_SIMPLE("MATCH"),
	UNICODE_STRING_SIMPLE("SETPARENT"),
	UNICODE_STRING_SIMPLE("SETCHILD"),
	UNICODE_STRING_SIMPLE("ADDRELATION"),
	UNICODE_STRING_SIMPLE("SETRELATION"),
	UNICODE_STRING_SIMPLE("REMRELATION"),
	UNICODE_STRING_SIMPLE("ADDRELATIONS"),
	UNICODE_STRING_SIMPLE("SETRELATIONS"),
	UNICODE_STRING_SIMPLE("REMRELATIONS"),
	UNICODE_STRING_SIMPLE("TEMPLATE"),
	UNICODE_STRING_SIMPLE("MOVE"),
	UNICODE_STRING_SIMPLE("MOVE-AFTER"),
	UNICODE_STRING_SIMPLE("MOVE-BEFORE"),
	UNICODE_STRING_SIMPLE("SWITCH")
};

UnicodeString stringbits[STRINGS_COUNT] = {
	UNICODE_STRING_SIMPLE("1f283fc29adb937a892e09bbc124b85c this is a dummy string to hold position 0"),
	UNICODE_STRING_SIMPLE("|"),
	UNICODE_STRING_SIMPLE("TO"),
	UNICODE_STRING_SIMPLE("OR"),
	UNICODE_STRING_SIMPLE("+"),
	UNICODE_STRING_SIMPLE("-"),
	UNICODE_STRING_SIMPLE("*"),
	UNICODE_STRING_SIMPLE("**"),
	UNICODE_STRING_SIMPLE("^"),
	UNICODE_STRING_SIMPLE("\\"),
	UNICODE_STRING_SIMPLE("#"),
	UNICODE_STRING_SIMPLE("!"),
	UNICODE_STRING_SIMPLE("NOT"),
	UNICODE_STRING_SIMPLE("NEGATE"),
	UNICODE_STRING_SIMPLE("ALL"),
	UNICODE_STRING_SIMPLE("NONE"),
	UNICODE_STRING_SIMPLE("LINK"),
	UNICODE_STRING_SIMPLE("BARRIER"),
	UNICODE_STRING_SIMPLE("CBARRIER"),
	UNICODE_STRING_SIMPLE("<STREAMCMD:FLUSH>"),
	UNICODE_STRING_SIMPLE("<STREAMCMD:EXIT>"),
	UNICODE_STRING_SIMPLE("<STREAMCMD:IGNORE>"),
	UNICODE_STRING_SIMPLE("<STREAMCMD:RESUME>"),
	UNICODE_STRING_SIMPLE("TARGET"),
	UNICODE_STRING_SIMPLE("AND"),
	UNICODE_STRING_SIMPLE("IF"),
	UNICODE_STRING_SIMPLE("_S_DELIMITERS_"),
	UNICODE_STRING_SIMPLE("_S_SOFT_DELIMITERS_"),
	UNICODE_STRING_SIMPLE(">>>"),
	UNICODE_STRING_SIMPLE("<<<"),
	UNICODE_STRING_SIMPLE(" LINK 0 "),
	UNICODE_STRING_SIMPLE(" "),
	UNICODE_STRING_SIMPLE("_LEFT_"),
	UNICODE_STRING_SIMPLE("_RIGHT_"),
	UNICODE_STRING_SIMPLE("_PAREN_"),
	UNICODE_STRING_SIMPLE("_TARGET_"),
	UNICODE_STRING_SIMPLE("_MARK_"),
	UNICODE_STRING_SIMPLE("_ATTACHTO_"),
	UNICODE_STRING_SIMPLE("<.*>"),
	UNICODE_STRING_SIMPLE("\".*\""),
	UNICODE_STRING_SIMPLE("\"<.*>\""),
	UNICODE_STRING_SIMPLE("AFTER"),
	UNICODE_STRING_SIMPLE("BEFORE"),
	UNICODE_STRING_SIMPLE("WITH"),
	UNICODE_STRING_SIMPLE("?"),
	UNICODE_STRING_SIMPLE("$1"),
	UNICODE_STRING_SIMPLE("$2"),
	UNICODE_STRING_SIMPLE("$3"),
	UNICODE_STRING_SIMPLE("$4"),
	UNICODE_STRING_SIMPLE("$5"),
	UNICODE_STRING_SIMPLE("$6"),
	UNICODE_STRING_SIMPLE("$7"),
	UNICODE_STRING_SIMPLE("$8"),
	UNICODE_STRING_SIMPLE("$9"),
	UNICODE_STRING_SIMPLE("%u"),
	UNICODE_STRING_SIMPLE("%U"),
	UNICODE_STRING_SIMPLE("%l"),
	UNICODE_STRING_SIMPLE("%L")
};

std::vector< std::vector<UChar> > gbuffers(NUM_GBUFFERS, std::vector<UChar>(CG3_BUFFER_SIZE, 0));
std::vector<std::string> cbuffers(NUM_CBUFFERS, std::string(CG3_BUFFER_SIZE, 0));

}
