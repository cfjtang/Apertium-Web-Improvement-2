%{ /*** C/C++ Declarations ***/

#include "TXTDeformat.h"

using namespace std;

#define YY_NO_UNISTD_H

%}

/*** Flex Declarations and Options ***/

/* enable c++ scanner class generation */
%option c++

/* change the name of the scanner class. results in "TXTDeformatFlexLexer" */
%option prefix="TXTDeformat"

/* the manual says "somewhat more optimized" */
%option batch

/* enable scanner to generate debug output. disable this for release versions. */
%option debug

/* no support for include files is planned */
%option yywrap nounput 

/* enables the use of start condition stacks */
%option stack

/* The following paragraph suffices to track locations accurately. Each time
 * yylex is invoked, the begin position is moved onto the end position. */
%{
// #define YY_USER_ACTION  yylloc->columns(yyleng);
%}

%% /*** Regular Expressions Part ***/

 /* code to place at the beginning of yylex() */
%{
    // reset location
    // yylloc->step();
%}

 /*** BEGIN - Change the lexer rules below ***/

(("\n\n")|("\r\n\r\n"))+ {
 isDot = true;
 bufferAppend(buffer, yytext);
}

[ \n\t\r] {
 if (last == "open_tag") 
  bufferAppend(tags.back(), yytext);
 else
  bufferAppend(buffer, yytext);    
}

[][\\/@<>^$] {
 printBuffer();
 *yyout << '\\';
 //fputwc_unlocked(L'\\', yyout);
 offset++;
 wchar_t symbol;
 int pos = mbtowc(&symbol, yytext, MB_CUR_MAX);
 if (pos == -1) { 
  wcerr << L"Cuatro" << endl;
  wcerr << L"Encoding error." << endl;
  exit(EXIT_FAILURE);
 }
 *yyout << (char)symbol;
 //fputwc_unlocked(symbol, yyout);
 offset++;
 hasWrite_dot = hasWrite_white = true;
}

. {
 printBuffer();
 symbuf += yytext;
 wchar_t symbol;
 int pos = mbtowc(&symbol, symbuf.c_str(), MB_CUR_MAX);
 if (pos == -1) {
  if (symbuf.size() > (size_t) MB_CUR_MAX) {
   // unknown character
   symbuf = "";
   *yyout << '?';
   //fputwc_unlocked(L'?', yyout);
   offset++;
   hasWrite_dot = hasWrite_white = true;
  }
 } else {
  symbuf = "";
  *yyout << (char)symbol;
  //fputwc_unlocked(symbol, yyout);
  offset++;
  hasWrite_dot = hasWrite_white = true;
 }
}

<<EOF>>	{
 isDot = true;
 printBuffer();
 return(0);
}

 /*** END - Change the lexer rules above ***/

%% /*** Additional Code ***/

TXTDeformat::TXTDeformat(std::istream* in, std::ostream* out) : TXTDeformatFlexLexer(in, out) {
 last = "";
 buffer = L"";
 isDot = hasWrite_dot = hasWrite_white = false;
 current=0;
 offset = 0;
 init_escape();
 init_tagNames();
}

TXTDeformat::~TXTDeformat() {

}

void TXTDeformat::set_debug(bool b) {
 yy_flex_debug = b;
}

/* This implementation of TXTDeformatFlexLexer::yylex() is required to fill the
 * vtable of the class TXTDeformatFlexLexer. We define the scanner's main yylex
 * function via YY_DECL to reside in the Scanner class instead. */

#ifdef yylex
#undef yylex
#endif

int TXTDeformatFlexLexer::yylex() {
 std::cerr << "in TXTDeformatFlexLexer::yylex() !" << std::endl;
 return(0);
}

/* When the scanner receives an end-of-file indication from YY_INPUT, it then
 * checks the yywrap() function. If yywrap() returns false (zero), then it is
 * assumed that the function has gone ahead and set up `yyin' to point to
 * another input file, and scanning continues. If it returns true (non-zero),
 * then the scanner terminates, returning 0 to its caller. */

int TXTDeformatFlexLexer::yywrap() {
 return(1);
}