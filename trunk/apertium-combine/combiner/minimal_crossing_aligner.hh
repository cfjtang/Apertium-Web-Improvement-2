#ifndef MINIMAL_CROSSING_ALIGNER_HH
#define MINIMAL_CROSSING_ALIGNER_HH

#include "aligner.hh"

using namespace std;

class Minimal_Crossing_Aligner: public Aligner
{
    public: 
        ~Minimal_Crossing_Aligner() { }
        void align(Pairwise_Alignment& alignment); 
        void inline basic_alignment(Pairwise_Alignment& alignment,
                std::vector<std::pair<int, int> >& tmp);
        void inline fill_finals(Pairwise_Alignment& alignment, 
                std::vector<std::pair<int, int> >& tmp);
};
#endif
