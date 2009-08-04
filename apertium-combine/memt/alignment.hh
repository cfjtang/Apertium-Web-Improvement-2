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
#include <string>
#include "matcher.hh"

using namespace std;


class Alignment
{
    public: 
        std::vector<wstring> _words_left;
        std::vector<wstring> _words_right;
        std::vector<int> _final_alignment; // result of align()
        std::vector<int> _final_alignment_left; // result of align()
        Alignment(wstring& left, wstring& right);
        Alignment(std::vector<wstring>& input_lines);
        Alignment(const wchar_t* left, const wchar_t* right);
        ~Alignment();
        int _score;
        std::vector<std::vector<bool> > _matching; // _matching[right][left]
        void align();
        void align(std::list<pair<int, int> >& leftright);
        void print();
        void generate_graphviz();
        void match(Matcher& m);
        void unmatch();
    private:
        void initialize();
        void inline complete();
        void to_vec(wstring& line, std::vector<wstring>& words);
};
#endif
