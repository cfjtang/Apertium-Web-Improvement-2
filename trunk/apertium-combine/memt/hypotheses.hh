/*
 * A bag of hypotheses that provides the following:
 * - hypothesis generation
 * - hypothesis ranking
 */

#include <iostream>
#include <vector>
#include "alignment.hh"

using namespace std;

class Hypotheses
{
    public: 
        Hypotheses(Alignment& a);
        ~Hypotheses();
        void rank();
        void print();
    private:
        std::vector<std::wstring> _hypotheses;
};
