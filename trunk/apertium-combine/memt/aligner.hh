#ifndef ALIGNER_HH
#define ALIGNER_HH

#include "alignment.hh"

using namespace std;

class Aligner
{
    public: 
        virtual ~Aligner() {}
        virtual void align(Alignment& alignment)=0; 

};
#endif
