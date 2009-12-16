#ifndef CASE_INSENSITIVE_MATCHER_HH
#define CASE_INSENSITIVE_MATCHER_HH

#include "matcher.hh"

using namespace std;

class Case_Insensitive_Matcher: public Matcher
{
    public: 
        ~Case_Insensitive_Matcher() {}
        int match(const wstring& left, const wstring& right);

};
#endif
