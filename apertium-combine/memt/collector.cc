#include <iostream>
#include <cwchar>
#include <string>

#include "collector.hh"

using namespace std;


Collector::Collector()
{

}

Collector::~Collector()
{

}

void
Collector::collect(FILE *input, FILE *output)
{
	int seen = 0;

	while(wchar_t val = static_cast<wchar_t>(fgetwc_unlocked(input))) {

		if(seen == 0 && val == L'[') {
			seen++;
		}

		if(seen == 1 && val == L']') {
			seen++;
		}

		fputwc_unlocked(val, output);

		if(seen == 2) {
			fputwc_unlocked(L'@', output);
			seen = 0;
		}
	}
	
	return;
}

