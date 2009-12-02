

%{

#include <cstdlib>
#include <iostream>
#include <map>
#include <vector>
#include <regex.h>
#include <string>


using namespace std;




%}


%option nounput
%option noyywrap
%option caseless
%option stack

%%




\0 {
	//cerr<<"_";
	//cerr.flush();
	fputc('_',yyout);
	//fputc('\0',yyout);
	fflush(yyout);	
}



.|\n {
 
	//cerr<<yytext;
	//cerr.flush();
	fputs(yytext,yyout);

 
}


<<EOF>>	{
  return 0;
}

%%



void usage(string const &progname)
{

  cerr << "USAGE: " << progname << " [input_file [output_file]" << ']' << endl;
  
  cerr << "txt format processor " << endl;
  exit(EXIT_SUCCESS);  
}

int main(int argc, char *argv[])
{

 if(argc > 3)
  {
    usage(argv[0]);
  }
 
  switch(argc)
  {
    case 3:
      yyout = fopen(argv[2], "w");
      if(!yyout)
      {
        usage(argv[0]);
      }
    case 2:
      yyin = fopen(argv[1], "r");
      if(!yyin)
      {
        usage(argv[0]);
      }
      break;
    default:
      break;
  }
  

  //yypush_buffer_state(yy_new_buffer ( yyin, YY_BUF_SIZE ));
  yylex();

  fclose(yyin);
  fclose(yyout);
}
