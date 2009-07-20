#include <iostream>
#include <cwchar>
#include <string>

#include "collector.hh"

using namespace std;

int 
main(int argc, char **argv) 
{
	FILE *input = stdin;
	FILE *output = stdout;		
	Collector c;

	if(setlocale(LC_CTYPE, "") == NULL) {
		wcerr << L"Warning: unsupported locale, fallback to \"C\"" << endl;
		setlocale(LC_ALL, "C");
	}

	c.collect(input, output);

	return 0;
}
