#include "naive_beam_generator.hh"

#define PARAM_BEAM 1.6 // parameter for the minimum_score for the beam search

unsigned int inline find_ind(enum side side, unsigned int ind, std::vector<Word>& w) 
{
    for (unsigned int k = 0; k < w.size(); ++k) {
        if (w[k].ind == ind && w[k].side == side)
            return k;
    }
    return 0;
}

void Naive_Beam_Generator::generate(Alignment& a,
        std::list<Hypothesis>& h)
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
    /// filling of the "h"
    for (scored_phrases::iterator it = wv.begin();
            it != wv.end(); ++it) {
        std::list<wstring> temp;
        for (std::vector<Word>::iterator w = it->second.begin();
                w != it->second.end(); ++w) {
            if (w->used) 
                temp.push_back(w->word);
        }
        h.push_back(Hypothesis(it->first, temp));
    } 
}

void inline Naive_Beam_Generator::expand(scored_phrases& wv,
        Alignment& a, unsigned int j)
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

        
