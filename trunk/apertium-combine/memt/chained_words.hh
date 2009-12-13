#ifndef CHAINED_WORDS_HH
#define CHAINED_WORDS_HH
#include "hypotheses.hh"
#include <set>

struct Chained_Word
{
    bool aligned;
    wstring* word; // easy transition to comparing pointers instead of strings
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
        }
    }
};

struct CW_S_VUsed {                                                         
    Chained_Word* cw;                                                       
    unsigned int score;                                                     
    std::vector<std::set<int> > used; 
    CW_S_VUsed(Chained_Word* c, unsigned int s, std::vector<
            std::set<int> > u): cw(c), score(s), used(u) { }
    ~CW_S_VUsed() { }
};  

struct Chained_Word_Sentinel
{
    std::vector<Chained_Word*> firsts;
    std::vector<CW_S_VUsed> lasts;
    std::vector<CW_S_VUsed> clean_roots;

    Chained_Word_Sentinel(unsigned int mt_size) { 
        Chained_Word* tmp = new Chained_Word(false, NULL, 0, 0);
        std::vector<std::set<int> > tmp_vect_used;
        tmp_vect_used.resize(mt_size);
        lasts.push_back(CW_S_VUsed(tmp, 0, tmp_vect_used));

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
            //wcout << "read_all" << endl;
            (*it)->read_all(starter, h);
        }
    }
    void inline swap_clear_lasts() 
    {
        lasts.swap(clean_roots);
        lasts.clear();
    }
};

#endif
