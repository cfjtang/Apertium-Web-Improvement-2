#include "alignment.hh"
#include <algorithm>
#include <utility>
#include <cctype>

Alignment::Alignment(std::vector<wstring>& input_lines) 
{
    Pairwise_Alignment pw_a(input_lines[0], input_lines[1]);
    _pw_alignments.push_back(pw_a);
}   

Alignment::~Alignment() 
{
}

void Alignment::match(Matcher& m) 
{
    for (std::vector<Pairwise_Alignment>::iterator it = _pw_alignments.begin();
            it != _pw_alignments.end(); ++it)
        it->match(m);
}

void Alignment::align(Aligner& aligner)
{
    for (std::vector<Pairwise_Alignment>::iterator it = _pw_alignments.begin();
            it != _pw_alignments.end(); ++it)
        aligner.align(*it);
    // TODO some more transitivity stuff
}

void Alignment::print()
{
    for (std::vector<Pairwise_Alignment>::iterator it = _pw_alignments.begin();
            it != _pw_alignments.end(); ++it)
        it->print();
    // TODO
}

void Alignment::generate_graphviz()
{
    for (std::vector<Pairwise_Alignment>::iterator it = _pw_alignments.begin();
            it != _pw_alignments.end(); ++it)
        it->generate_graphviz();
    // TODO it generates only one, the last. 
    // should be one big or many as theses
}
    
