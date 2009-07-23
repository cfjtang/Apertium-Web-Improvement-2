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

class IRSTLM_Ranker: public Ranker
{
    public:
        IRSTLM_Ranker(const string& file_path);
        ~IRSTLM_Ranker();
        void inline score(Hypothesis& h);
        // void rank(std::list<Hypothesis>& hypotheses);
    private:
        lmtable *lmtb;
};

#endif
