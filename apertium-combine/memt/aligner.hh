#ifndef ALIGNER_HH
#define ALIGNER_HH

#include "pairwise_alignment.hh"

using namespace std;

class Pairwise_Alignment;

class Aligner
{
    public: 
        virtual ~Aligner() {}
        virtual void align(Pairwise_Alignment& alignment)=0; 

};
#endif
