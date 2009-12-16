#ifndef DUMMY_RANKER_HH
#define DUMMY_RANKER_HH

#include "ranker.hh"

class Dummy_Ranker: public Ranker
{
    public: 
        Dummy_Ranker() { }
        ~Dummy_Ranker() { }
        void score(Hypothesis& h);
};

#endif
