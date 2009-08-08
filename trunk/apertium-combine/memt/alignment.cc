#include "alignment.hh"
#include <algorithm>
#include <utility>
#include <cctype>

Alignment::Alignment(std::vector<wstring>& input_lines) 
{
    // fill the MT translations matrix, [translation][words_for_this_one]
    for (std::vector<wstring>::iterator it = input_lines.begin();
            it != input_lines.end(); ++it) {
        std::vector<wstring> temp;
        to_vec(*it, temp);
        _mt_translations.push_back(temp);
    }
    
    // align each MT translation with each other(s)
    for (unsigned int i = 0; i < _mt_translations.size(); ++i) {
        for (unsigned int j = 0; j < _mt_translations.size(); ++j) {
            if (j > i)
                _pw_alignments.push_back(Pairwise_Alignment(
                            _mt_translations[i], _mt_translations[j], i, j));
        }
    }

    // use this result for fullfilling _aligned


    // complete _aligned by transitivity
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
    
