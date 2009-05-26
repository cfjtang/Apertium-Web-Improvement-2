%{ /*** C/C++ Declarations ***/

#include "TXTReformat.h"

using namespace std;

#define YY_NO_UNISTD_H

%}

/*** Flex Declarations and Options ***/

/* enable c++ scanner class generation */
%option c++

/* change the name of the scanner class. results in "TXTReformatFlexLexer" */
%option prefix="TXTReformat"

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

"["|"]"	{
 // do nothing
}

"[@"[^]]+"]"	{
 string filename = yytext;
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

"[\\@" {
 *yyout << L'@';
}

".[]" {
 // do nothing
}

"\\"[][\\/@<>^$] {
 char *t = yytext + 1;
 string temp(t, yyleng - 1);
 *yyout << temp;
}

.|\n {
 string yytext_conv(yytext, yyleng);
 *yyout << yytext_conv;
}

<<EOF>>	{
 return(0);
}

 /*** END - Change the lexer rules above ***/

%% /*** Additional Code ***/

TXTReformat::TXTReformat(std::istream* in, std::ostream* out) : TXTReformatFlexLexer(in, out) {

}

TXTReformat::~TXTReformat() {

}

void TXTReformat::set_debug(bool b) {
 yy_flex_debug = b;
}

/* This implementation of TXTReformatFlexLexer::yylex() is required to fill the
 * vtable of the class TXTReformatFlexLexer. We define the scanner's main yylex
 * function via YY_DECL to reside in the Scanner class instead. */

#ifdef yylex
#undef yylex
#endif

int TXTReformatFlexLexer::yylex() {
 std::cerr << "in TXTReformatFlexLexer::yylex() !" << std::endl;
 return(0);
}

/* When the scanner receives an end-of-file indication from YY_INPUT, it then
 * checks the yywrap() function. If yywrap() returns false (zero), then it is
 * assumed that the function has gone ahead and set up `yyin' to point to
 * another input file, and scanning continues. If it returns true (non-zero),
 * then the scanner terminates, returning 0 to its caller. */

int TXTReformatFlexLexer::yywrap() {
 return(1);
}