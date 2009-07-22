/*
 * The ranker using IRST language model
 */

#ifndef RANKER_HH
#define RANKER_HH
#include <iostream>
#include <sstream>
#include "lmtable.h"
#include "lmmacro.h"
#include "hypotheses.hh"

class lmtable;
class lmmacro;
class Ranker;
struct Hypothesis;

using namespace std;

class Ranker
{
    public:
        Ranker(const string& file_path);
        ~Ranker();
        void rank(std::list<Hypothesis>& hypotheses);
    private:
        lmtable *lmtb;
};

#endif
