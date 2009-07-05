#include "hypotheses.hh"

using namespace std;

Hypotheses::Hypotheses(Alignment& a) 
{
    
    std::wstring s;
    for (vector<wstring>::iterator it = a._words_left.begin();
            it != a._words_left.end();
            ++it) {
        s.append(*it);
    }
    _hypotheses.push_back(s);
}

Hypotheses::~Hypotheses() 
{
}

void Hypotheses::rank() 
{
    //TODO
    return;
}

void Hypotheses::print()
{
    for (std::vector<wstring>::iterator it = _hypotheses.begin();
            it != _hypotheses.end();
            ++it)
        wcout << *it << endl;
}
