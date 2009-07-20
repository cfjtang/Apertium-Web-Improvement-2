#include <iostream>
#include <cwchar>
#include <string>

using namespace std;

wchar_t
readBlock(FILE *input)
{
	wchar_t val = static_cast<wchar_t>(fgetwc_unlocked(input));

	if(feof(input)) {
		return -1;
	}

	return val;
}

int
collect(FILE *input, FILE *output)
{
	int seen = 0;

	while(wchar_t val = readBlock(input)) {
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
	
	return 1;
}


int 
main(int argc, char **argv) 
{
	if(setlocale(LC_CTYPE, "") == NULL) {
		wcerr << L"Warning: unsupported locale, fallback to \"C\"" << endl;
		setlocale(LC_ALL, "C");
	}

	FILE *input = stdin;
	FILE *output = stdout;		

	return collect(input, output);
}
