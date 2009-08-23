#ifndef NAIVE_BEAM_GENERATOR_HH
#define NAIVE_BEAM_GENERATOR_HH
#include "generator.hh"
#include <set>

using namespace std;

struct Chained_Word
{
    bool aligned;
    wstring* word;
    unsigned int nmt;
    unsigned int nword;
    std::vector<Chained_Word*> nexts;
    Chained_Word(bool a, wstring* w, unsigned int mt, unsigned int word): 
        aligned(a), word(w), nmt(mt), nword(word) { } 
    ~Chained_Word() { 
        while (!nexts.empty()) {
            delete nexts.back();
            nexts.pop_back();
        }
    }
    void read_all(std::pair<unsigned int, std::list<wstring*> >& 
            chain_of_words, std::list<Hypothesis>& h) {
        if (!nexts.empty()) {
            for (unsigned int k = 0; k < nexts.size(); ++k) {
                std::pair<unsigned int, std::list<wstring*> > tmp = 
                    std::pair<unsigned int, std::list<wstring*> >
                    (chain_of_words);
                tmp.first += aligned ? 1 : 0;
                tmp.second.push_back(word);
                nexts[k]->read_all(tmp, h);
            }
        } else {
            std::pair<unsigned int, std::list<wstring*> > tmp = 
                std::pair<unsigned int, std::list<wstring*> >
                (chain_of_words);
            tmp.first += aligned ? 1 : 0;
            tmp.second.push_back(word);
            h.push_back(Hypothesis(tmp.first, 
                        tmp.second));
            /* std::list<wstring> tmp;
            for (std::list<wstring*>::iterator it = 
                    chain_of_words.second.begin();
                    it != chain_of_words.second.end(); ++it)
                tmp.push_back(**it);
            h.push_back(Hypothesis(chain_of_words.first, tmp)); */
        }
    }
};

struct Chained_Word_Sentinel
{
    std::vector<Chained_Word*> firsts;
    // std::vector<std::pair<bool, Chained_Word*> > lasts; // expanded or not
    std::vector<std::pair<Chained_Word*, std::vector<std::set<int> > > >
        lasts; // expanded, [#mt] = used_set = DUMMY>
    std::vector<std::pair<Chained_Word*, std::vector<std::set<int> > > >
        clean_roots; // <not expanded, [#mt] = used_set>

    Chained_Word_Sentinel(unsigned int mt_size) { 
        Chained_Word* tmp = new Chained_Word(false, NULL, 0, 0);
        std::vector<std::set<int> > tmp_vect_used;
        tmp_vect_used.resize(mt_size);
        lasts.push_back(std::pair<Chained_Word*, std::vector<std::set<int >
                > >(tmp, tmp_vect_used));
        firsts.push_back(tmp);
    }
    ~Chained_Word_Sentinel() 
    { 
        while (!firsts.empty()) {
            delete firsts.back();
            firsts.pop_back();
        }
    }
    void inline fill_hypotheses(std::list<Hypothesis>& h)
    {
        for (std::vector<Chained_Word*>::iterator it = firsts.begin();
                it != firsts.end(); ++it) {
            std::pair<unsigned int, std::list<wstring*> > starter = 
                std::pair<unsigned int, std::list<wstring*> >(0, 
                        std::list<wstring*>());
            wcout << "read_all" << endl;
            (*it)->read_all(starter, h);
        }
    }
};

class Naive_Beam_Generator: public Generator
{
    public: 
        ~Naive_Beam_Generator() {}
        virtual void generate(Alignment& a,
                std::list<Hypothesis>& h);
        // for drawkcab compatibility TODO clean
        virtual void inline generate_pairwise(Pairwise_Alignment& a,
                std::list<Hypothesis>& h);
        virtual void inline generate_all(Alignment& a,
                std::list<Hypothesis>& h);
    private:
        void inline expand(scored_phrases& wv, Pairwise_Alignment& a,
                unsigned int j);
};
#endif
