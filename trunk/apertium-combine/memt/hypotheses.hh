/*
 * A bag of hypotheses that provides the following:
 * - hypothesis generation
 * - hypothesis ranking
 */

#include <iostream>
#include <list>
#include <vector>
#include "alignment.hh"

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
    ~Hypothesis() { }
};

struct Word
{
    bool used;
    int side;
    unsigned int ind;
    wstring word;
    Word(bool u, int s, unsigned int i, wstring w) 
    {
        used = u;
        side = s;
        ind = i;
        word = w;
    }
    ~Word() { }
};

class Hypotheses
{
    public: 
        Hypotheses(Alignment& a);
        ~Hypotheses();
        void rank();
        void print();
    private:
        std::list<Hypothesis> _hypotheses;
        //void generate(unsigned int begin, unsigned int end, Alignment& a, 
        //        std::vector<Hypothesis>&  vh);
        void inline expand(std::vector<std::vector<Word> >& wv,
                Alignment& a, unsigned int j);      
        void inline fill_words(std::vector<Word>& words, Alignment& a,
                unsigned int length);
};
