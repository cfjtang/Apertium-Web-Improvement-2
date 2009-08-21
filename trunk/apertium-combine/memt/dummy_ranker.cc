#include "dummy_ranker.hh"

void Dummy_Ranker::score(Hypothesis& h)
{
    h.score = 0;
}
