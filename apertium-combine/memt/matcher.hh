#ifndef MATCHER_HH
#define MATCHER_HH
#include <string>

using namespace std;

class Matcher
{
    public: 
        virtual ~Matcher() {}
        virtual int match(const wstring& left, const wstring& right)=0; 

};
#endif
