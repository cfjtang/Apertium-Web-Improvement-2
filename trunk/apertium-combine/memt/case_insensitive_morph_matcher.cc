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

int Case_Insensitive_Morph_Matcher::match(const wstring& left, const wstring& right)
{
    wstring l = wstring(left);
    wstring r = wstring(right);
    to_lower(l);
    to_lower(r);
    if (!l.compare(r))
        return 0;

    readBil("/build/svnroot/apertium/trunk/apertium-cy-en/en-cy.automorf.bin");                                 

    wstring tr_right, tr_left;
    tr_right = fstp.biltrans(r, false);
    tr_left = fstp.biltrans(l, false);

    wcout << L"r: " << tr_right << endl;
    wcout << L"l: " << tr_left << endl;

    return 42;
}

void
Case_Insensitive_Morph_Matcher::readBil(string const &fstfile)
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

