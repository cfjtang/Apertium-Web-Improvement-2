#include "alignment.hh"
#include <algorithm>
#include <set>
// TODO #include <unicode/unistr.h>

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
    toVec(left, _words_left);
    toVec(right, _words_right);
    initialize();
}

Alignment::Alignment(const wchar_t* left, const wchar_t* right) 
{
    wstring l = wstring(left);
    wstring r = wstring(right);
    toVec(l, _words_left);
    toVec(r, _words_right);
    initialize();
}

Alignment::~Alignment() 
{
}

void Alignment::initialize() 
{ 
    _final_alignment.insert(_final_alignment.begin(), _words_right.size(), -1);
    _consecutive_alignments = 0;
    _total_matches = 0;
    return;   
}

/*** maximal consecutive alignments
 */
void Alignment::align()
{
    unsigned int size_end_left = _words_left.size() - 1;
    unsigned int size_end_right = _words_right.size() - 1;
    std::vector<int> temp_alignment;
    std::vector<int> best_alignment;
    best_alignment.resize(_words_right.size());
    int best_score = 0;
    int tmp_score = 0;
    // search for matching words
    std::vector<std::vector<bool> > matching;
    matching.resize(_words_right.size());
    for (unsigned int j = 0; j <= size_end_right; ++j) {
        matching[j].resize(_words_left.size());
        for (unsigned int i = 0; i <= size_end_left; ++i) {
            // here we can use another matching
            if (exactMatch(_words_left[i], _words_right[j])) {
                matching[j][i] = true;
            } else { 
                matching[j][i] = false;
            }
        }
    }
    // align longuest sequence of matching words
    unsigned int i = 0;
    while (i <= size_end_left) {
        unsigned int j = 0;
        while (j <= size_end_right) {
            // here we can use another matching
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
    wcout << best_alignment.size() << endl;
    wcout << _final_alignment.size() << endl;
#endif
    copy(best_alignment.begin(), best_alignment.end(),
            _final_alignment.begin());
    return;
}

void Alignment::print() 
{
    for (unsigned int i = 0; i < _final_alignment.size(); ++i) {
        if (_final_alignment[i] != -1) {
            wcout << _words_left[_final_alignment[i]] << " --> " 
                << _words_right[i] << endl;
        } else {
            wcout << "unaligned" << endl;
        }
    }
}

void Alignment::toVec(wstring& line, 
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
            // shouldn't this stripping be done beforehand?
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

void Alignment::toLower(wstring& to_lower)
{
    // TODO
    std::wstring::iterator i = to_lower.begin();
    return;
}

int Alignment::exactMatch(const wstring& left, 
        const wstring& right) 
{ 
    return !left.compare(right);
}

int Alignment::caseInsensitiveMatch(const wstring& left,
        const wstring& right)
{
    wstring l = wstring(left);
    wstring r = wstring(right);
    toLower(l);
    toLower(r);
    return l.compare(r);
}

