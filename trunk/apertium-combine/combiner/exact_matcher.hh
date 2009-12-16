#ifndef EXACT_MATCHER_HH
#define EXACT_MATCHER_HH

#include "matcher.hh"

using namespace std;


class Exact_Matcher: public Matcher
{
    public: 
        ~Exact_Matcher() {}
        int match(const wstring& left, const wstring& right);
};
#endif
