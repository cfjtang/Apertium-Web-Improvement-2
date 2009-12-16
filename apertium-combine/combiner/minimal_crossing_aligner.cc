#include "minimal_crossing_aligner.hh"
#include <assert.h>
#include <set>

using namespace std;

bool crossing(unsigned int i, unsigned int j, 
        unsigned int ii, unsigned int jj) 
{
    return true;
}

void Minimal_Crossing_Aligner::basic_alignment(Pairwise_Alignment& alignment,
        std::vector<std::pair<int, int> >& tmp)
{
    std::set<int> used_left;
    std::set<int> used_right;
    unsigned int j = 0;
    tmp.insert(tmp.begin(), alignment._words_right.size(), 
            std::pair<int, int>(0, -1));
    while (j < alignment._words_right.size()) {
        unsigned int i = 0;
        while (i < alignment._words_left.size()) {
            if (alignment._matching[j][i] && !used_left.count(i) 
                    && !used_right.count(j)) {
                tmp[j].second = i;
                used_left.insert(i);
                used_right.insert(j);
                break;
            }
            ++i;
        }
        ++j;
    }

}

void Minimal_Crossing_Aligner::fill_finals(Pairwise_Alignment& alignment,
        std::vector<std::pair<int, int> >& tmp)
{
#ifdef DEBUG
    assert(tmp.size() == alignment._final_alignment.size());
#endif
    for (unsigned int j = 0; j < tmp.size(); ++j) {
        alignment._final_alignment[j] = tmp[j].second;
    }
    for (int i = 0; i < int(alignment._words_left.size()); ++i) {
        alignment._final_alignment_left.push_back(-1);
        for (unsigned int j = 0; j < alignment._words_right.size(); ++j) {
            if (alignment._final_alignment[j] == i) {
                alignment._final_alignment_left[i] = j;
                break;
            }
        }
    }
}

void Minimal_Crossing_Aligner::align(Pairwise_Alignment& alignment) 
{
    //unsigned int size_end_left = alignment._words_left.size() - 1;
    //unsigned int size_end_right = alignment._words_right.size() - 1;
    std::vector<std::pair<int, int> > tmp; 
    // pair<crossings, aligned[right] = left>
    basic_alignment(alignment, tmp);
    fill_finals(alignment, tmp);
}
