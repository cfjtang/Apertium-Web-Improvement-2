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

//bool ranker_bin_pred(const Hypothesis& h1, const Hypothesis& h2) 
//{
//    return h1.score > h2.score;
//}


    // Sorting using a binary predicate:
    // // An alternative (using the container container pointers instead
    // // of values) that doesn't need a binary predicate is to define
    // // your own operator<() which sort() uses by default.
    // bool BinPred( const Class& o1, const Class& o2 )
    // {
    //     return o1.val < o2.val;
    //     }
    //     vector vec;
    //     sort( vec.begin(), vec.end(), BinPred );
    //
    //

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
