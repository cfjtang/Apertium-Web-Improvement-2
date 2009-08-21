/*
 * The ranker using IRSTLM
 */

#ifndef IRSTLM_RANKER_HH
#define IRSTLM_RANKER_HH
#include "lmtable.h"
#include "lmmacro.h"
#include "ranker.hh"

class lmtable;
class lmmacro;

class IRSTLMRanker: public Ranker
{
    public:
        IRSTLMRanker();
        IRSTLMRanker(const string& file_path);
        ~IRSTLMRanker();
        inline void score(Hypothesis& h);
        // void rank(std::list<Hypothesis>& hypotheses);
    private:
        lmtable *lmtb;
};

#endif
