/*
 * A word-to-word aligner between two hypotheses:
 * - one that comes from stdin, from lt-proc -g (Apertium)
 * - one that comes from tmp buffer, from Moses
 */

#ifndef PAIRWISE_ALIGNMENT_HH
#define PAIRWISE_ALIGNMENT_HH
#include <iostream>
#include <vector>
#include <list>
#include <utility>
#include <fstream>
#include <string>
#include "matcher.hh"
#include "aligner.hh"

using namespace std;

void to_vec(wstring& line, std::vector<wstring>& words);

class Pairwise_Alignment
{
    public: 
        unsigned int _index_mte_left;  
        unsigned int _index_mte_right; 
        std::vector<wstring> _words_left;
        std::vector<wstring> _words_right;
        std::vector<unsigned int> _final_alignment; // result of align()
        std::vector<unsigned int> _final_alignment_left; // result of align()
        Pairwise_Alignment(wstring& left, wstring& right);
        Pairwise_Alignment(const wchar_t* left, const wchar_t* right);
        Pairwise_Alignment(std::vector<wstring>& left, 
                std::vector<wstring>& right, 
                unsigned int ileft, unsigned int iright);
        ~Pairwise_Alignment();
        int _score;
        std::vector<std::vector<bool> > _matching; // _matching[right][left]
        void align(std::list<pair<int, int> >& leftright);
        void print();
        void generate_graphviz();
        void match(Matcher& m);
        void unmatch();
    private:
        void initialize();
        void inline complete();
};
#endif
