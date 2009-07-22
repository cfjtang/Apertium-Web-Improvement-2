#include "hypotheses.hh"

#define PARAM_BEAM 1.6 // parameter for the minimum_score for the beam search

using namespace std;

unsigned int max_length(scored_phrases& wv) 
{
    unsigned int max = 0;
    for (scored_phrases::iterator it = wv.begin();
            it != wv.end(); ++it) {
        if (it->second.size() > max)
            max = it->second.size();
    } 
    return max;
}

/*** Very basic hypothesis generator.
 * TODO:
 *   Should take in account the fact that alignment won't alws be exactMatching
 */
Hypotheses_Naive_Beam::Hypotheses_Naive_Beam(Alignment& a) 
{
    unsigned int length = a._words_right.size() > a._words_left.size() ? 
        a._words_right.size() : a._words_left.size();
    scored_phrases wv;
    std::pair<unsigned int, std::vector<Word> > words;
    fill_words(words, a, length);
    wv.push_back(words);
#ifdef DEBUG
    wcout << "WORDS: ";
    for (std::vector<Word>::iterator w = wv[0].second.begin();
            w != wv[0].second.end(); ++w) {
        wcout << w->word << " ";
    }
    wcout << endl;
#endif
    for (unsigned int j = 0; j < length; ++j) { 
        expand(wv, a, j);
        length = max_length(wv);
    }
    /// filling of the "_hypotheses"
    for (scored_phrases::iterator it = wv.begin();
            it != wv.end(); ++it) {
        std::list<wstring> temp;
        for (std::vector<Word>::iterator w = it->second.begin();
                w != it->second.end(); ++w) {
            if (w->used) 
                temp.push_back(w->word);
        }
        _hypotheses.push_back(Hypothesis(it->first, temp));
    } 


    /* unsigned int begin = j;
    unsigned int end = j;
    while (a._final_alignment[end] == -1)
        ++end;
    generate(begin, end, a, vs);*/
}

Hypotheses_Naive_Beam::~Hypotheses_Naive_Beam() 
{
}

void Hypotheses_Naive_Beam::rank() 
{
    //TODO
    return;
}

void Hypotheses_Naive_Beam::print()
{
    wcout << ">>> Hypotheses_Naive_Beam: " << endl;
    const wchar_t wc = L' ';
    for (std::list<Hypothesis>::iterator it = _hypotheses.begin();
            it != _hypotheses.end();
            ++it) {
        wstring s;
        for (std::list<wstring>::iterator w = it->words.begin();
                w != it->words.end(); ) {
            s.append(*w);
            if (++w != it->words.end())
                s.append(1, wc);
        }
        wcout << s << endl;
    }
}

/* void Hypotheses_Naive_Beam::generate(unsigned int begin, unsigned int end, Alignment& a, 
        std::vector<Hypothesis>& vh)
{
    std::wstring current;
    for (unsigned int i = begin; i <= end; ++i) {
        if (i >= a._words_left.size()) {
            if (i < a._words_right.size()) {
                current.append(a._words_right[i]);
            }
        } else if (i >= a._words_right.size()) {
            if (i < a._words_left.size()) {
                current.append(a._words_left[i]);
            }
        } else {
        }
    }
}*/

unsigned int inline find_ind(enum side side, unsigned int ind, std::vector<Word>& w) 
{
    for (unsigned int k = 0; k < w.size(); ++k) {
        if (w[k].ind == ind && w[k].side == side)
            return k;
    }
    return 0;
}

void inline Hypotheses_Naive_Beam::expand(scored_phrases& wv,Alignment& a, unsigned int j)
{
    unsigned int wvsize = wv.size();
    for (unsigned int i = 0; i < wvsize; ++i)
    {
        if (j >= wv[i].second.size()) 
            continue; // finished to expand this "words"
        if (wv[i].second[j].side == LEFT) { 
            if (a._final_alignment_left[wv[i].second[j].ind] != -1) { //aligned
                ++wv[i].first;
                if (!a._words_right[a._final_alignment_left[
                        wv[i].second[j].ind]]
                        .compare(wv[i].second[j].word)) { //aligned, same words
                    wv[i].second[j].used = true;
                } else { //aligned, not the same words: 1 hyp. with and 1 w/o
                    wv.push_back(wv[i]);
                    wv[i].second[j].used = true;
                    wv[wv.size() - 1].second[
                        find_ind(RIGHT, a._final_alignment_left[
                                wv[i].second[j].ind], wv[i].second)];
                }
            } else { // not aligned
                unsigned int minimum_score = j/PARAM_BEAM;
#ifdef DEBUG
                // wcout << "j: " << j << " score: " << wv[i].first 
                //    << " minimum_score: " << minimum_score << endl;
#endif
                if (wv[i].first >= minimum_score) { 
                    // construct one hyp. with and one without
                    wv.push_back(wv[i]);
                    wv[i].second[j].used = true;
                }
                // no need for else, word in not used by default
            }
        } else if (wv[i].second[j].side == RIGHT) { 
            if (a._final_alignment[wv[i].second[j].ind] != -1) { //aligned
                ++wv[i].first;
                if (!a._words_left[a._final_alignment[wv[i].second[j].ind]]
                        .compare(wv[i].second[j].word)) { //aligned, same words
                    wv[i].second[j].used = true;
                } else { //aligned, not the same words: 1 hyp. with and 1 w/o
                    wv.push_back(wv[i]);
                    wv[i].second[j].used = true;
                    wv[wv.size() - 1].second[
                        find_ind(LEFT, a._final_alignment[wv[i].second[j].ind], 
                                wv[i].second)] ;
                }
            } else { // not aligned: 
                unsigned int minimum_score = j/PARAM_BEAM;
#ifdef DEBUG
                // wcout << "j: " << j << " score: " << wv[i].first 
                //    << " minimum_score: " << minimum_score << endl;
#endif
                if (wv[i].first >= minimum_score) { 
                    // construct one hyp. with and one without
                    wv.push_back(wv[i]);
                    wv[i].second[j].used = true;
                }
                // no need for else, word in not used by default
            }
        }
    }
#ifdef DEBUG
    wcout << "expanded the: " << j << " (word: " 
        << wv[0].second[j].word<< ") with wv size of: " << wvsize << endl;
#endif
}

void inline Hypotheses_Naive_Beam::fill_words(std::pair<unsigned int, std::vector<Word> >
        & words, Alignment& a, unsigned int length)
{
    for (unsigned int i = 0; i < length; ++i) {
        if (i < a._words_right.size())
            if (a._final_alignment[i] != -1)
                words.second.push_back(
                        Word(false, true, RIGHT, i, a._words_right[i]));
            else
                words.second.push_back(
                        Word(false, false, RIGHT, i, a._words_right[i]));
        if (i < a._words_left.size()) {
            if (a._final_alignment_left[i] == -1) // add if it is not aligned
                words.second.push_back(
                        Word(false, false, LEFT, i, a._words_left[i]));
        }
    }
}

