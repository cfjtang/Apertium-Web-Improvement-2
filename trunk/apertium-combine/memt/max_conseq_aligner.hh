#ifndef MAX_CONSEQ_ALIGNER_HH
#define MAX_CONSEQ_ALIGNER_HH

#include "aligner.hh"

using namespace std;

class Max_Conseq_Aligner: public Aligner
{
    public: 
        ~Max_Conseq_Aligner() {}
        void inline complete(Pairwise_Alignment& alignment);
        void align(Pairwise_Alignment& alignment);
};
#endif
