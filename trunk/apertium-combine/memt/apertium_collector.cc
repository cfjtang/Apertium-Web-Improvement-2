#include <iostream>
#include <cwchar>
#include <string>

#include "moses_collector.hh"

using namespace std;

int 
main(int argc, char **argv) 
{
	FILE *input = stdin;
	FILE *output = stdout;		

	MosesCollector c("/home/fran/statmt/corpora5/model/moses.ini");

	if(setlocale(LC_CTYPE, "") == NULL) {
		wcerr << L"Warning: unsupported locale, fallback to \"C\"" << endl;
		setlocale(LC_ALL, "C");
	}

	c.collect(input, output);

	return 0;
}
