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
#include "aligner.hh"
#include "pairwise_alignment.hh"

using namespace std;


class Pairwise_Alignment;

class Alignment
{
    public:
        std::vector<Pairwise_Alignment> _pw_alignments; // for drawkcab compat
        std::vector<std::vector<wstring> > _mt_translations; //[transl][word]
        std::vector<std::vector<std::list<std::pair<unsigned int, 
            unsigned int> > > > _aligned;
            // _aligned[transl][word] = list of aligned words: (transl, word)
        Alignment(std::vector<wstring>& input_lines);
        ~Alignment();
        void match(Matcher& m);
        void align(Aligner& aligner);
        void print();
        void generate_graphviz();
};
#endif
