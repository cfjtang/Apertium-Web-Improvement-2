#include "case_insensitive_morph_matcher.hh"
#include <iostream>
#include <cctype>
#include <utility>
#include <algorithm>
#include <string>


using namespace std;

void inline to_lower(wstring& to_lower)
{
    transform(to_lower.begin(), to_lower.end(),
      to_lower.begin(), towlower);
    return;
}

Case_Insensitive_Morph_Matcher::Case_Insensitive_Morph_Matcher()
{

    readBil("/Users/snippy/apertium/trunk/apertium-cy-en/en-cy.automorf.bin");                                 
}

Case_Insensitive_Morph_Matcher::~Case_Insensitive_Morph_Matcher()
{

}



int Case_Insensitive_Morph_Matcher::match(const wstring& left, const wstring& right)
{
    wstring l = wstring(left);
    wstring r = wstring(right);
    to_lower(l);
    to_lower(r);
    if (!l.compare(r))
        return 0;

    wstring tr_right, tr_left;
    tr_right = fstp.biltrans(r, false);
    tr_left = fstp.biltrans(l, false);
    wstring::size_type first = tr_left.find(L"<");
    wstring::size_type end = tr_left.find(L">");
    while (first != string::npos) {
        end = tr_left.find(L">");
        tr_left.erase(first, end-first+1);
        first = tr_left.find(L"<");
    }
    first = tr_right.find(L"<");
    while (first != string::npos) {
        end = tr_right.find(L">");
        tr_right.erase(first, end-first+1);
        first = tr_right.find(L"<");
    }

    wcout << L"r: " << tr_right << endl;
    wcout << L"l: " << tr_left << endl;
    if (!tr_left.compare(tr_right))
        return 0;

    return 42;
}

void Case_Insensitive_Morph_Matcher::readBil(string const &fstfile)
{
  FILE *in = fopen(fstfile.c_str(), "rb");
  if(!in)
  {
    cerr << "Error: Could not open file '" << fstfile << "'." << endl;
    exit(EXIT_FAILURE);
  }
  fstp.load(in);
  fstp.initBiltrans();
  fclose(in);

  cerr << "Loaded transducer from " << fstfile << endl;
}

