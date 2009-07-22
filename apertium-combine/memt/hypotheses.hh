/*
 * A bag of hypotheses that provides the following:
 * - hypothesis generation
 * - hypothesis ranking
 */

#include <iostream>
#include <list>
#include <vector>
#include "alignment.hh"
#include <utility>

using namespace std;
enum side {LEFT, RIGHT};

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
    bool aligned;
    enum side side;
    unsigned int ind;
    wstring word;
    Word(bool u, bool a, enum side s, unsigned int i, wstring w) 
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

class Hypotheses_Naive_Beam
{
    public: 
        Hypotheses_Naive_Beam(Alignment& a);
        ~Hypotheses_Naive_Beam();
        void rank();
        void print();
    private:
        std::list<Hypothesis> _hypotheses;
        //void generate(unsigned int begin, unsigned int end, Alignment& a, 
        //        std::vector<Hypothesis>&  vh);
        void inline expand(scored_phrases& wv,
                Alignment& a, unsigned int j);      
        void inline fill_words(std::pair<unsigned int, std::vector<Word> >
                & words, Alignment& a, unsigned int length);
};
