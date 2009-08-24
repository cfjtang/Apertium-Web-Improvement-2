#ifndef PARALLEL_SCAN_GENERATOR_HH
#define PARALLEL_SCAN_GENERATOR_HH
#include "chained_words.hh"
#include "generator.hh"

using namespace std;

class Parallel_Scan_Generator: public Generator
{
    public: 
        ~Parallel_Scan_Generator() {}
        virtual void generate(Alignment& a, std::list<Hypothesis>& h);
};
#endif
