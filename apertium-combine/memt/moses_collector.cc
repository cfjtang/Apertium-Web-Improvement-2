#include <iostream>
#include <cwchar>
#include <string>

#include "moses_collector.hh"

using namespace std;
using namespace Moses;

MosesCollector::MosesCollector()
{
	parameter = new Moses::Parameter();
}

MosesCollector::~MosesCollector()
{

}

void
MosesCollector::collect(FILE *input, FILE *output)
{
	wstring buf = L"";
	int seen = 0;

	while(wchar_t val = static_cast<wchar_t>(fgetwc_unlocked(input))) {

		if(feof(input)) {
			return;
		}	
		buf = buf + val;

		if(seen == 0 && val == L'[') {
			seen++;
		}

		if(seen == 1 && val == L']') {
			seen++;
		}

		//fputwc_unlocked(val, output);

		if(seen == 2) {
			fputwc_unlocked(L'@', output);
			translate(buf.c_str());
			seen = 0;
			buf = L"";
		}

	}
	
	return;
}

wchar_t*
MosesCollector::translate(const wchar_t* block)
{
	wcout << L"MosesCollector: " << block << endl;
	return (wchar_t *)block; // Dummy function returns original
}

