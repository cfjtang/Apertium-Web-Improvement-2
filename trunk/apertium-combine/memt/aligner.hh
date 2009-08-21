#ifndef ALIGNER_HH
#define ALIGNER_HH

#include "pairwise_alignment.hh"

using namespace std;

class Pairwise_Alignment;

class Aligner
{
    public: 
        virtual ~Aligner() {}
        /** fills alignment._final_alignment & alignment._final_alignment_left
         *  and alignment._score, using alignement._matching 
         */
        virtual void align(Pairwise_Alignment& alignment)=0; 

};
#endif
