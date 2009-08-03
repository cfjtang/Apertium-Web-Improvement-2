#ifndef NAIVE_BEAM_GENERATOR_HH
#define NAIVE_BEAM_GENERATOR_HH
#include "generator.hh"

using namespace std;

class Naive_Beam_Generator: public Generator
{
    public: 
        ~Naive_Beam_Generator() {}
        virtual void generate(Alignment& a,
                std::list<Hypothesis>& h);
    private:
        void inline expand(scored_phrases& wv, Alignment& a,
                unsigned int j);
};
#endif
