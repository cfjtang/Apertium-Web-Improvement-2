#include "naive_beam_generator.hh"

#define PARAM_BEAM 1.6 // parameter for the minimum_score for the beam search

unsigned int inline find_ind(unsigned int side, unsigned int ind, 
        std::vector<Word>& w) 
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
    if (a._pw_alignments.size() == 1) 
        generate_pairwise(a._pw_alignments[0], h);
    else {
        generate_all(a, h);
    }
}

void inline Naive_Beam_Generator::generate_all(Alignment& a,
        std::list<Hypothesis>& h)
{
    unsigned int length = 0;
    for (unsigned int i = 0; i < a._aligned.size(); ++i) {
        if (a._aligned[i].size() > length) {
            length = a._aligned[i].size();
        }
    }

    Chained_Word_Sentinel* sentinel = new Chained_Word_Sentinel();
    for (unsigned int j = 0; j < length; ++j) {
        sentinel->lasts.swap(sentinel->clean_roots);
        sentinel->lasts.clear();
        for (unsigned int i = 0; i < a._mt_translations.size(); ++i) {
            if (j < a._mt_translations[i].size()) {
                if (a._aligned[i][j].empty()) {
                /// copy and extend half of the hypotheses with and half w/o
                    for (unsigned int k = 0; 
                            k < sentinel->clean_roots.size(); ++k) {
                        // w/o
                        sentinel->lasts.push_back(sentinel->clean_roots[k]); 
                        Chained_Word* tmp = new Chained_Word(false, 
                                &a._mt_translations[i][j]);
                        // with
                        sentinel->lasts.push_back(tmp);                       
                        sentinel->clean_roots[k]->nexts.push_back(tmp);
                    }
                } else { // aligned
                /// copy and extend the hypotheses with and w/o != but aligned
                    for (std::list<std::pair<unsigned int, int> >::iterator 
                            al = a._aligned[i][j].begin(); 
                            al != a._aligned[i][j].end(); ++al) {
                        for (unsigned int k = 0; 
                                k < sentinel->clean_roots.size(); ++k) {
                            /// determine if we have to add the [i][j]
                            /// and/or its aligned word 
                            bool place_current = true;
                            bool place_aligned = true;
                            for (std::vector<Chained_Word*>::iterator it = 
                                    sentinel->clean_roots[k]->nexts.begin();
                                    it != sentinel->clean_roots[k]
                                    ->nexts.end(); ++it) {
                                if ((*it)->word == 
                                        &a._mt_translations[i][j]) 
                                    place_current = false;
                                if ((*it)->word == 
                                        &a._mt_translations[al->first]
                                        [al->second]) 
                                    place_aligned = false;
                            }
                            if (a._mt_translations[i][j].compare(
                                        a._mt_translations[al->first]
                                        [al->second]) == 0)
                                place_aligned = false;

                            if (place_current) { 
                                Chained_Word* tmp = new Chained_Word(
                                        true, &a._mt_translations[i][j]);
                                sentinel->lasts.push_back(tmp);
                                sentinel->clean_roots[k]->nexts.push_back(
                                        tmp);
                            }
                            if (place_aligned) { 
                                Chained_Word* tmp = new Chained_Word(
                                        true, &a._mt_translations
                                        [al->first][al->second]);
                                sentinel->lasts.push_back(tmp);
                                sentinel->clean_roots[k]->nexts.push_back(
                                        tmp);
                            }
                        }
                    }
                }
            }
        }
    }
    sentinel->fill_hypotheses(h);
    delete sentinel;
}

void inline Naive_Beam_Generator::generate_pairwise(Pairwise_Alignment& a,
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
        Pairwise_Alignment& a, unsigned int j)
{
    unsigned int wvsize = wv.size();
    for (unsigned int i = 0; i < wvsize; ++i)
    {
        if (j >= wv[i].second.size()) 
            continue; // finished to expand this "words"
        if (wv[i].second[j].side == 0) { 
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
                        find_ind(1, a._final_alignment_left[
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
        } else if (wv[i].second[j].side == 1) { 
            if (a._final_alignment[wv[i].second[j].ind] != -1) { //aligned
                ++wv[i].first;
                if (!a._words_left[a._final_alignment[wv[i].second[j].ind]]
                        .compare(wv[i].second[j].word)) { //aligned, same words
                    wv[i].second[j].used = true;
                } else { //aligned, not the same words: 1 hyp. with and 1 w/o
                    wv.push_back(wv[i]);
                    wv[i].second[j].used = true;
                    wv[wv.size() - 1].second[
                        find_ind(0, a._final_alignment[wv[i].second[j].ind], 
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

