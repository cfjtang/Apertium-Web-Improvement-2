#include "exact_matcher.hh"
#include <iostream>
#include <cctype>
#include <utility>
#include <algorithm>
#include <string>

using namespace std;

int Exact_Matcher::match(const wstring& left, const wstring& right)
{
    return !left.compare(right);
}
