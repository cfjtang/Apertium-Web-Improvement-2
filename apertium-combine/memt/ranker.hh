/*
 * The ranker 
 */

#ifndef RANKER_HH
#define RANKER_HH
#include <iostream>
#include <sstream>
#include "lmtable.h"
#include "lmmacro.h"
#include "hypotheses.hh"

class Ranker;
struct Hypothesis;

using namespace std;

class Ranker
{
    public:
        // Ranker(const string& file_path) {} 
        Ranker() {}
        virtual ~Ranker() {}
        virtual void score(Hypothesis& h) = 0;
        virtual void rank(std::list<Hypothesis>& hypotheses)
        {
            for (std::list<Hypothesis>::iterator it = hypotheses.begin();
                    it != hypotheses.end(); ++it) {
                score(*it);
            }
                
            hypotheses.sort();
            return;
        }
};

#endif
