#ifndef CASE_INSENSITIVE_MORPH_MATCHER_HH
#define CASE_INSENSITIVE_MORPH_MATCHER_HH


#include <lttoolbox/alphabet.h>
#include <lttoolbox/buffer.h>
#include <lttoolbox/fst_processor.h>
#include <lttoolbox/ltstr.h>
#include <lttoolbox/match_exe.h>
#include <lttoolbox/match_state.h>

#include "matcher.hh"

using namespace std;

class Case_Insensitive_Morph_Matcher: public Matcher
{
    private:
        FSTProcessor fstp;

    public: 
        Case_Insensitive_Morph_Matcher(string const &filename);
        ~Case_Insensitive_Morph_Matcher();
        int match(const wstring& left, const wstring& right);
        void readBil(string const &filename);


};
#endif
