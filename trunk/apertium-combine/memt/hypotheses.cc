#include "hypotheses.hh"

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
Hypotheses::Hypotheses(Alignment& a) 
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
    wcout << ">>> Hypotheses: " << endl;
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

/* void Hypotheses::generate(unsigned int begin, unsigned int end, Alignment& a, 
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

unsigned int inline find_ind(int side, unsigned int ind, std::vector<Word>& w) 
{
    for (unsigned int k = 0; k < w.size(); ++k) {
        if (w[k].ind == ind && w[k].side == side)
            return k;
    }
    return 0;
}

void inline Hypotheses::expand(scored_phrases& wv,Alignment& a, unsigned int j)
{
    unsigned int wvsize = wv.size();
    for (unsigned int i = 0; i < wvsize; ++i)
    {
        if (j >= wv[i].second.size()) 
            continue; // finished to expand this "words"
        if (wv[i].second[j].side == 0) {
            if (a._final_alignment_left[wv[i].second[j].ind] != -1) {
                if (!a._words_right[a._final_alignment_left[wv[i].second[j].ind]]
                        .compare(wv[i].second[j].word)) {
                    wv[i].second[j].used = true;
                } else { 
                    wv.push_back(wv[i]);
                    wv[i].second[j].used = true;
                    wv[wv.size() - 1].second[
                        find_ind(1, a._final_alignment_left[
                                wv[i].second[j].ind], wv[i].second)] ;
                }
            } else {
                wv.push_back(wv[i]);
                wv[i].second[j].used = true;
            }
        } else if (wv[i].second[j].side == 1) {
            if (a._final_alignment[wv[i].second[j].ind] != -1) {
                if (!a._words_right[a._final_alignment[wv[i].second[j].ind]]
                        .compare(wv[i].second[j].word)) {
                    wv[i].second[j].used = true;
                } else { 
                    wv.push_back(wv[i]);
                    wv[i].second[j].used = true;
                    wv[wv.size() - 1].second[
                        find_ind(0, a._final_alignment[wv[i].second[j].ind], 
                                wv[i].second)] ;
                }
            } else {
                wv.push_back(wv[i]);
                wv[i].second[j].used = true;
            }
        }
    }
#ifdef DEBUG
    wcout << "expanded the: " << j << " with wv size of: " << wvsize << endl;
#endif
}

void inline Hypotheses::fill_words(std::pair<unsigned int, std::vector<Word> >
        & words, Alignment& a, unsigned int length)
{
    for (unsigned int i = 0; i < length; ++i) {
        if (i < a._words_right.size())
            if (a._final_alignment[i] != -1)
                words.second.push_back(
                        Word(false, true, 1, i, a._words_right[i]));
            else
                words.second.push_back(
                        Word(false, false, 1, i, a._words_right[i]));
        if (i < a._words_left.size()) {
            if (i >= a._words_right.size()               // put it only if
                    || a._final_alignment_left[i] == -1) // it is not aligned
                words.second.push_back(
                        Word(false, false, 0, i, a._words_left[i]));
        }
    }
}

