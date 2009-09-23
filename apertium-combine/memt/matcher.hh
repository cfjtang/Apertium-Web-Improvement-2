#ifndef MATCHER_HH
#define MATCHER_HH
#include <string>

using namespace std;

class Matcher
{
    public: 
        virtual ~Matcher() {}
        /***
         * 0 if DOESN'T MATCH, a score a score in [1,INT_MAX] if it MATCHES
         */
        virtual int match(const wstring& left, const wstring& right)=0; 

};
#endif
