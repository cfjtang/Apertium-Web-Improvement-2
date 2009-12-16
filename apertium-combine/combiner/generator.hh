#ifndef GENERATOR_HH
#define GENERATOR_HH
#include "hypotheses.hh"

using namespace std;

struct Hypothesis;

class Generator
{
    public: 
        virtual ~Generator() {}
        virtual void generate(Alignment& a,
                std::list<Hypothesis>& h) = 0;
};
#endif
