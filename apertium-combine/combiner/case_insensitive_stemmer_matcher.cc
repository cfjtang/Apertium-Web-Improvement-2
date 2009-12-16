#include "case_insensitive_stemmer_matcher.hh"
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

int Case_Insensitive_Stemmer_Matcher::match(const wstring& left, const wstring& right)
{
    wstring l = wstring(left);
    wstring r = wstring(right);
    to_lower(l);
    to_lower(r);
    if (!l.compare(r))
        return 0;
    wstring sl;
    if (l.size() > 5) {
        sl = l.substr(0, l.size() - 3);
        if (!sl.compare(r)) {
            wcout << sl << " ** " << r << endl;
            return 0;
        }
    }
    if (r.size() > 5) {
        wstring sr = r.substr(0, r.size() - 3);
        if (!sr.compare(l)) {
            wcout << sr << " ** " << l << endl;
            return 0;
        }
        if (l.size() > 5)
            if (!sr.compare(sl))
                return 0;
    }
    return 42;
}
