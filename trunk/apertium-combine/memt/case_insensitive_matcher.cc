#include "case_insensitive_matcher.hh"
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

int Case_Insensitive_Matcher::match(const wstring& left, const wstring& right)
{
    wstring l = wstring(left);
    wstring r = wstring(right);
    to_lower(l);
    to_lower(r);
    return !l.compare(r);
}
