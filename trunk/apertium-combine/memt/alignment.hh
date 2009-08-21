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
        std::vector<Pairwise_Alignment> _pw_alignments; 
        /// _mt_translations[#transl][#word] = word #word in transl #transl
        std::vector<std::vector<wstring> > _mt_translations;  
        /// _aligned[#transl][#word] == 
        /// list of aligned words: (#transl2, #word2)
        std::vector<std::vector<std::list<
            std::pair<unsigned int, int> > > > _aligned;
        /// _ngrams_aligned[0] = bigrams, _ngrams_aligned[1] = trigrams, etc.
        /// _ngrams_aligned[0][0][0] = (1, 2) means that the first bigram
        /// consituted of word 0 and 1 of mt=0 is aligned with the first one
        /// of word 2 and 3 of mt=1. [Notations from the first word]
        std::vector<std::vector<std::vector<std::list<
            std::pair<unsigned int, int> > > > > _ngrams_aligned; 
        Alignment(std::vector<wstring>& input_lines);
        ~Alignment();
        void match(Matcher& m);
        void align(Aligner& aligner, unsigned int ngram_level);
        void print();
        void generate_graphviz();
        void print_aligned(); // TODO templatize
        void print_mt_translations(); // TODO templatize
        void print_ngrams_aligned(); // TODO templatize
};
#endif
