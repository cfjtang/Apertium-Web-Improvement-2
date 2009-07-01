#include "alignment.hh"
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
    std::vector<int> scores;
    std::vector<std::vector<int> > alignments;
    unsigned int size_end = _words_right.size() - 1;

    //for (indices) {
        std::vector<int> temp_alignment;
        int score = 0;
        int tmp_score = 0;
        bool consecutive = false;
        std::set<int> to_align_left;
        std::set<int> to_align_right;
        for (unsigned int i = 0; i < _words_left.size(); ++i) 
            to_align_left.insert(i);
        for (unsigned int i = 0; i < _words_right.size(); ++i) 
            to_align_right.insert(i);
        //temp_alignment.reserve(_words_right.size());
        temp_alignment.insert(temp_alignment.begin(), size_end + 1, -1);
        // align matching words
        for (unsigned int i = 0; i < _words_left.size(); ++i) {
            unsigned int j_begin = 0;
            unsigned int j_end = size_end;
            for (unsigned int j = j_begin; j <= j_end; ++j) {
                // here we can use another matching
                if (exactMatch(_words_left[i], _words_right[j])) {
                    temp_alignment[j] = i;
                    to_align_left.erase(i);
                    to_align_right.erase(j);
                    if (consecutive)
                        tmp_score++;
                    else {
                        if (tmp_score > score) 
                            score = tmp_score;
                        tmp_score = 1;
                    }
                    consecutive = true;
                    j_begin = j + 1;
                    j_end = j + 1;
                    ++i;
                    break;
                } else { 
                    if (consecutive)
                        --i;
                    consecutive = false;
                }
            }
        }
        // align non matching words that are left (still not aligned)
        if (!to_align_left.empty() || !to_align_right.empty()) {
        }
#ifdef DEBUG
        // TODO should be done with exception
        for (std::vector<int>::iterator it = temp_alignment.begin(); 
                it != temp_alignment.end();
                it++) {
            if (*it == -1) 
                wcout << "Error: Alignments are unfinished" << endl;
        }
#endif
        alignments.push_back(temp_alignment);
        scores.push_back(score);
    //}
    int indmax = ind_max(scores);
    copy(alignments[indmax].begin(), alignments[indmax].end(),
            _final_alignment.begin());
    return;
}

void Alignment::print() 
{
    for (unsigned int i = 0; i < _final_alignment.size(); ++i) {
        wcout << _words_left[_final_alignment[i]] << " --> " 
            << _words_right[i] << endl;
    }
}

void Alignment::toVec(wstring& line, 
        std::vector<wstring>& words) 
{
    // UChar space = ' ';
    wchar_t space = L' ';
    int index = -1;
    int offset = 0;
    int length = 0;
    while ((index = line.find(space, offset)) != -1) { 
        length = index - offset;
        if (length > 0) { 
            words.push_back(wstring(line, offset, length));
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
   return left.compare(right);
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

