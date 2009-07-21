/*
 * A word-to-word aligner between two hypotheses:
 * - one that comes from stdin, from lt-proc -g (Apertium)
 * - one that comes from tmp buffer, from Moses
 */

#ifndef ALIGNMENT_HH
#define ALIGNMENT_HH
#include <iostream>
#include <vector>
#include <list>
#include <utility>
#include <fstream>

using namespace std;


class Alignment
{
    public: 
        std::vector<wstring> _words_left;
        std::vector<wstring> _words_right;
        std::vector<int> _final_alignment; // result of align()
        std::vector<int> _final_alignment_left; // result of align()
        Alignment(wstring& left, wstring& right);
        Alignment(const wchar_t* left, const wchar_t* right);
        ~Alignment();
        void align();
        void align(std::list<pair<int, int> >& leftright);
        void print();
        void generate_graphviz();
    private:
        void initialize();
        void inline match();
        void inline unmatch();
        void inline complete();
        std::vector<std::vector<bool> > matching;
        int total_matches;
        void to_vec(wstring& line, std::vector<wstring>& words);
        void to_lower(wstring& to_lower);
        int exact_match(const wstring& left, 
                const wstring& right);
        int case_insensitive_match(const wstring& left, 
                const wstring& right);
};
#endif
