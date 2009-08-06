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
        // for drawkcab compatibility TODO clean
        virtual void generate_pairwise(Pairwise_Alignment& a,
                std::list<Hypothesis>& h);
    private:
        void inline expand(scored_phrases& wv, Pairwise_Alignment& a,
                unsigned int j);
};
#endif
