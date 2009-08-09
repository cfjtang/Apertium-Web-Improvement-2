#include "alignment.hh"
#include <algorithm>
#include <utility>
#include <cctype>

#define TRANSITIVITY_DEPTH 2 // >= 1

Alignment::Alignment(std::vector<wstring>& input_lines) 
{
    // fill the MT translations matrix, [translation][words_for_this_one]
    for (std::vector<wstring>::iterator it = input_lines.begin();
            it != input_lines.end(); ++it) {
        std::vector<wstring> temp;
        to_vec(*it, temp);
        _mt_translations.push_back(temp);
    }
    
    // build every Pairwise_Alignment(s) that we need 
    // and initialize _aligned
    for (unsigned int i = 0; i < _mt_translations.size(); ++i) {
        std::vector<std::list<std::pair<unsigned int, int> > > init_aligned;
        for (unsigned int j = 0; j < _mt_translations.size(); ++j) {
            if (j > i)
                _pw_alignments.push_back(Pairwise_Alignment(
                            _mt_translations[i], _mt_translations[j], i, j));
        }
        init_aligned.insert(init_aligned.begin(), _mt_translations[i].size(),
                std::list<std::pair<unsigned int, int> >());
        _aligned.push_back(init_aligned);
    }
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

    // use this results for fullfilling _aligned
    for (std::vector<Pairwise_Alignment>::iterator it = _pw_alignments.begin();
            it != _pw_alignments.end(); ++it) {
        // 2 loops for readability
        for (unsigned int iright = 0; 
                iright < it->_final_alignment.size(); 
                ++iright) {
            if (it->_final_alignment[iright] != -1) {
                _aligned[it->_index_mte_right][iright].push_back(
                        std::pair<unsigned int, int>
                        (it->_index_mte_left, 
                            it->_final_alignment[iright]));
            }
        }
        for (unsigned int ileft = 0; 
                ileft < it->_final_alignment_left.size(); 
                ++ileft) {
            if (it->_final_alignment_left[ileft] != -1) {
                _aligned[it->_index_mte_left][ileft].push_back(
                        std::pair<unsigned int, int>
                        (it->_index_mte_right, 
                            it->_final_alignment_left[ileft]));
            }
        }
    }

    // complete _aligned by transitivity
    unsigned int transitivity_depth = 
        TRANSITIVITY_DEPTH < (_mt_translations.size() - 1) ? 
        TRANSITIVITY_DEPTH : 
        (_mt_translations.size() - 2) ;
    for (unsigned int i = 1; i < transitivity_depth; ++i) {
        for (unsigned int mt = 0; mt < _aligned.size(); ++mt) {
            for (int nword = 0; nword < static_cast<int>(_aligned[mt].size());
                    ++nword) {
                std::list<std::pair<unsigned int, int> > temp_list(
                        _aligned[mt][nword]);
                for (std::list<std::pair<unsigned int, int> >::iterator 
                        it1 = _aligned[mt][nword].begin(); 
                        it1 != _aligned[mt][nword].end(); ++it1) { 
                    for (std::list<std::pair<unsigned int, int> >::iterator 
                            it2 = _aligned[it1->first][it1->second].begin(); 
                            it2 != _aligned[it1->first][it1->second].end(); 
                            ++it2) { 
                        if (it2->first != mt || it2->second != nword) {
                            temp_list.push_back(
                                    std::pair<unsigned int, int>
                                    (it2->first, it2->second));
                        }
                    }
                }
                _aligned[mt][nword].swap(temp_list);
            }
        }
    }
}

void Alignment::print()
{
    /* 
    for (std::vector<Pairwise_Alignment>::iterator it = _pw_alignments.begin();
            it != _pw_alignments.end(); ++it)
        it->print();
    */
    for (std::vector<Pairwise_Alignment>::iterator it = _pw_alignments.begin();
            it != _pw_alignments.end(); ++it) {
        wcout << "printing a _final_alignment: [ ";
        for (std::vector<int>::iterator i = it->_final_alignment.begin();
                i != it->_final_alignment.end(); ++i) {
            wcout << *i << " ";
        }
        wcout << "]" << endl;
    }
    print_mt_translations();
    print_aligned();
}

void Alignment::generate_graphviz()
{
    for (std::vector<Pairwise_Alignment>::iterator it = _pw_alignments.begin();
            it != _pw_alignments.end(); ++it)
        it->generate_graphviz();
    // TODO it generates only one, the last. 
    // should be one big or many as theses
}

void Alignment::print_aligned()
{
    wcout << "printing _aligned[#transl][#word]:" << endl;
    for (std::vector<std::vector<std::list<std::pair<unsigned int, int> > > >
        ::iterator it1 = _aligned.begin(); it1 != _aligned.end(); ++it1) {
        for (std::vector<std::list<std::pair<unsigned int, int> > >
        ::iterator it2 = it1->begin(); it2 != it1->end(); ++it2) {
            wcout << "{ ";
            for (std::list<std::pair<unsigned int, int> >::iterator 
                    c = it2->begin(); c != it2->end(); ++c) {
                wcout << "(" << c->first << ", " << c->second << ") ";
            }
            wcout << "} | ";
        }
        wcout << endl;
    }
}

void Alignment::print_mt_translations()
{
    wcout << "printing _mt_translations[#transl][#word]:" << endl;
    for (std::vector<std::vector<wstring> >::iterator 
            it1 = _mt_translations.begin(); 
            it1 != _mt_translations.end(); ++it1) {
        for (std::vector<wstring>::iterator 
                it2 = it1->begin(); it2 != it1->end(); ++it2) {
            wcout << *it2 << " | ";
        }
        wcout << endl;
    }
}
    
