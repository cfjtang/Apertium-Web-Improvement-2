#include "alignment.hh"
#include <algorithm>
#include <utility>
#include <cctype>


/** Indice of the maximum in a vector of positive integers
 */
int ind_max(std::vector<int>& v) 
{
    int ind = 0;
    for (unsigned int i = 0; i < v.size(); ++i) {
        if (v[i] > v[ind])
            ind = i;
    }
    return ind;
}

/** Erase all the blank/spaces before the first word 
 * and after the last in a (w)string
 */ 
void strip(wstring& s) 
{
    wchar_t space = L' ';
    std::wstring::iterator it = s.begin();
    if ((*it) == space) {
        while((*it) == space)
            it++;
        s.erase(s.begin(), it);
    }
    it = s.end();
    if ((*it) == space) {
        while((*it) == space)
            it--;
        s.erase(it, s.end());
    }
}

Alignment::Alignment(wstring& left, wstring& right) 
{
    to_vec(left, _words_left);
    to_vec(right, _words_right);
    initialize();
}

Alignment::Alignment(const wchar_t* left, const wchar_t* right) 
{
    wstring l = wstring(left);
    wstring r = wstring(right);
    to_vec(l, _words_left);
    to_vec(r, _words_right);
    initialize();
}

Alignment::~Alignment() 
{
}

void Alignment::initialize() 
{ 
    _final_alignment.insert(_final_alignment.begin(), _words_right.size(), -1);
    matching.resize(_words_right.size());
    total_matches = 0;
    return;   
}

/*** search for matching words
 */
void inline Alignment::match()
{
    for (unsigned int j = 0; j < _words_right.size(); ++j) {
        matching[j].resize(_words_left.size());
        for (unsigned int i = 0; i < _words_left.size(); ++i) {
            // here we can use another matching
            /// if (exact_match(_words_left[i], _words_right[j])) {
            if (case_insensitive_match(_words_left[i], _words_right[j])) {
                matching[j][i] = true;
            } else { 
                matching[j][i] = false;
            }
        }
    }
    wfstream oute("out", ios::out);
    for (unsigned int j = 0; j < _words_right.size(); ++j) 
        for (unsigned int i = 0; i < _words_left.size(); ++i) 
            oute << matching[j][i] << endl;
    oute.close();
}

/*** unmatch already aligned words
 */
void inline Alignment::unmatch()
{
    for (unsigned int j = 0; j < _words_right.size(); ++j) {
        if (_final_alignment[j] != -1) {
            for (unsigned int i = 0; i < _words_left.size(); ++i) {
                matching[j][i] = false;
            }
            for (unsigned int k = 0; k < _words_right.size(); ++k){
                matching[k][_final_alignment[j]] = false;
            }
        }
    }
}

void inline Alignment::complete()
{
    for (unsigned int j = 0; j < _words_right.size(); ++j) {
        if (_final_alignment[j] == -1) {
            unsigned int i = j;
            unsigned int count = 0;
            while (count < _words_left.size()) {
                if (matching[j][i]) {
                    // unmatch j and i as we "used" it
                    for (unsigned int ki = 0; ki < _words_left.size(); ++ki)
                        matching[j][ki] = false;
                    for (unsigned int kj = 0; kj < _words_right.size(); ++kj)
                        matching[kj][i] = false;
                    _final_alignment[j] = i;
                    total_matches++;
                    break;
                }
                i = (i + 1) % _words_left.size();
                ++count;
            }
        }
    }
    for (int i = 0; i < int(_words_left.size()); ++i) {
    // for (unsigned int i = 0; i < _words_left.size(); ++i) {
        _final_alignment_left.push_back(-1);
        for (unsigned int j = 0; j < _words_right.size(); ++j) {
            if (_final_alignment[j] == i) {
                _final_alignment_left[i] = j;
                break;
            }
        }
    }
#ifdef DEBUG
    wcout << ">>> number of aligned words: " << total_matches << endl;
#endif
}

/** maximal consecutive alignments
 */
void Alignment::align()
{
    match();
    unsigned int size_end_left = _words_left.size() - 1;
    unsigned int size_end_right = _words_right.size() - 1;
    std::vector<int> temp_alignment;
    std::vector<int> best_alignment;
    best_alignment.resize(_words_right.size());
    int best_score = 0;
    int tmp_score = 0;
    // align longuest sequence of matching words
    unsigned int i = 0;
    while (i <= size_end_left) {
        unsigned int j = 0;
        while (j <= size_end_right) {
            if (matching[j][i]) {
                tmp_score = 1;
                temp_alignment.clear();
                temp_alignment.insert(temp_alignment.begin(), 
                        _words_right.size(), -1);
                temp_alignment[j] = i;
                if (i < size_end_left || j < size_end_right) {
                    unsigned int t_i = i + 1;
                    unsigned int t_j = j + 1;
                    while (t_i <= size_end_left 
                            && t_j <= size_end_right
                            && matching[t_j][t_i]) {
                        temp_alignment[t_j] = t_i;
                        ++t_i;
                        ++t_j;
                        ++tmp_score;
                    }
                }
                if (tmp_score > best_score) {
                    best_score = tmp_score;
                    best_alignment.clear();
                    temp_alignment.swap(best_alignment);
                }
            }
            ++j;
        }
        ++i;
    }
#ifdef DEBUG
    wcout << ">>> best score: " << best_score << endl;
#endif
    copy(best_alignment.begin(), best_alignment.end(),
            _final_alignment.begin());
    total_matches = best_score;
    unmatch();
    complete();
    return;
}

/*void Alignment::align2() 
{
    match();
    std::vector<pair<int, std::vector<int> > > alignments;
    for (unsigned int j = 0; j < _words_right.size(); ++j) {
        for (unsigned int i = 0; i < _words_left.size(); ++i) {
            if (matching[j][i]) {

            }
        }
    }
}*/

void Alignment::align(std::list<pair<int, int> >& leftright) 
{
    for (std::list<pair<int, int> >::iterator it = leftright.begin();
            it != leftright.end();
            ++it) {
        _final_alignment[it->second] = it->first;
    }
}

void Alignment::print() 
{
    wcout << "<<<<<<<<<<<<<< RIGHT >>>>>>>>>>>>>>>" << endl;
    for (unsigned int i = 0; i < _final_alignment.size(); ++i) {
        if (_final_alignment[i] != -1) {
            wcout << _words_left[_final_alignment[i]] << " --> " 
                << _words_right[i] << endl;
        } else {
            wcout << "unaligned" << endl;
        }
    }
    wcout << "<<<<<<<<<<<<<< LEFT >>>>>>>>>>>>>>>" << endl;
    for (unsigned int i = 0; i < _final_alignment_left.size(); ++i) {
        if (_final_alignment_left[i] != -1) {
            wcout << _words_right[_final_alignment_left[i]] << " --> " 
                << _words_left[i] << endl;
        } else {
            wcout << "unaligned" << endl;
        }
    }
}

void Alignment::generate_graphviz()
{
    wfstream graph_out("graph.viz", ios::out);
    // graph_out << "digraph Alignment {" << endl;
    graph_out << "graph Alignment {" << endl;
    graph_out << "    rankdir=LR;" << endl;
    graph_out << "    ranksep=.5;" << endl;
    graph_out << "    compound=true;" << endl;
    graph_out << "    subgraph cluster_0 {" << endl;
    graph_out << "        node[style=filled];" << endl;
    graph_out << "        ";
    for (unsigned int i = 0; i < (_words_left.size() - 1); ++i)
        graph_out << "\"L" << i <<  "_" << _words_left[i] << "\" -- ";
    graph_out << "\"L" << (_words_left.size() - 1) << "_" 
        << _words_left[_words_left.size() - 1] << "\" ;" << endl;
    graph_out << "        " << endl;
    graph_out << "        label = \"Left\";" << endl;
    graph_out << "    }" << endl;
    graph_out << "    subgraph cluster_1 {" << endl;
    graph_out << "        node[style=filled];" << endl;
    graph_out << "        ";
    for (unsigned int i = 0; i < (_words_right.size() - 1); ++i)
        graph_out << "\"R" << i <<  "_" << _words_right[i] << "\" -- ";
    graph_out << "\"R" << (_words_right.size() - 1) << "_" 
        << _words_right[_words_right.size() - 1] << "\" ;" << endl;
    graph_out << "        " << endl;
    graph_out << "        label = \"Right\";" << endl;
    graph_out << "    }" << endl;
    for (unsigned int i = 0; i < _final_alignment.size(); ++i) {
        if (_final_alignment[i] != -1) {
            graph_out << "    \"R" << i << "_" << _words_right[i]
                << "\" -- \"L" << _final_alignment[i] << "_" 
                << _words_left[_final_alignment[i]] << "\" ;" << endl;
        }
    }
    graph_out << "}" << endl;
    graph_out.close();
}

void Alignment::to_vec(wstring& line, 
        std::vector<wstring>& words) 
{
    wchar_t space = L' ';
    int index = -1;
    int offset = 0;
    int length = 0;
    while ((index = line.find(space, offset)) != -1) { 
        length = index - offset;
        if (length > 0) { 
            // words.push_back(wstring(line, offset, length));
            // shouldn't this stripping be done beforehand? TODO
            wstring raw_word = wstring(line, offset, length);
            strip(raw_word);
            words.push_back(raw_word);
        }
        offset = index + 1;
    }
    wstring last(line, offset);
    if (last.length() > 0) 
        words.push_back(last);
}

void Alignment::to_lower(wstring& to_lower)
{
    transform(to_lower.begin(), to_lower.end(),
      to_lower.begin(), towlower);
    return;
}

int Alignment::exact_match(const wstring& left, 
        const wstring& right) 
{ 
    return !left.compare(right);
}

int Alignment::case_insensitive_match(const wstring& left,
        const wstring& right)
{
    wstring l = wstring(left);
    wstring r = wstring(right);
    to_lower(l);
    to_lower(r);
    return !l.compare(r);
}

