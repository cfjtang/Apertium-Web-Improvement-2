/*
 * A bag of hypotheses that provides the following:
 * - hypothesis creation by invoking a generator
 * - basic services on hypotheses
 * - hypothesis ranking by invoking a ranker
 */

#ifndef HYPOTHESES_HH
#define HYPOTHESES_HH
#include <iostream>
#include <list>
#include <utility>
#include <vector>
#include "alignment.hh"
#include "ranker.hh"
#include "generator.hh"

class Ranker;
class Generator;

using namespace std;

struct Hypothesis
{
    unsigned int score;
    std::list<wstring> words;
    Hypothesis(unsigned int s, std::list<wstring>& l) 
    {
        score = s;
        words = l;
    }
    Hypothesis(unsigned int s, std::list<wstring*>& l) 
    {
        score = s;
        for (std::list<wstring*>::iterator it = l.begin();
                it != l.end(); ++it) {
            if (*it != NULL)    // because of the sentinel
                words.push_back(**it);
        }
    }
    ~Hypothesis() { }
    bool operator<(const Hypothesis& h);
    /// bool inline operator<(const Hypothesis& h); TODO inline it
};

struct Word
{
    bool used;
    bool aligned;
    unsigned int side;
    unsigned int ind;
    wstring word;
    Word(bool u, bool a, unsigned int s, unsigned int i, wstring w) 
    {
        used = u;
        aligned = a;
        side = s;
        ind = i;
        word = w;
    }
    ~Word() { }
};

typedef std::vector<std::pair<unsigned int, std::vector<Word> > > 
    scored_phrases;

unsigned int max_length(scored_phrases& wv);

// for drawkcab compatibility
void fill_words(std::pair<unsigned int, std::vector<Word> >
        & words, Pairwise_Alignment& a, unsigned int length);

void fill_words(std::vector<std::vector<Word> >& words_vector, 
        Alignment& a);

class Hypotheses
{
    public: 
        Hypotheses(Alignment& a, Generator& g);
        ~Hypotheses();
        void rank(Ranker* r);
        void print();
        void print(wfstream* where);
        wstring best();
    private:
        std::list<Hypothesis> _hypotheses;
        void inline concatenate(std::list<wstring>& ls, wstring& s);
};
#endif
