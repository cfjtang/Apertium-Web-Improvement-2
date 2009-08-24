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
        // generate_pairwise(a._pw_alignments[0], h);
        generate_all(a, h); 
    else {
        generate_all(a, h);
    }
}

void inline Naive_Beam_Generator::generate_all(Alignment& a,
        std::list<Hypothesis>& h)
// TODO beamize
{
    unsigned int length = 0;
    for (unsigned int i = 0; i < a._aligned.size(); ++i) {
        if (a._aligned[i].size() > length) {
            length = a._aligned[i].size();
        }
    }

    Chained_Word_Sentinel* sentinel = new Chained_Word_Sentinel(
            a._mt_translations.size());
    for (unsigned int j = 0; j < length; ++j) {
        sentinel->lasts.swap(sentinel->clean_roots);
        sentinel->lasts.clear();
        for (unsigned int i = 0; i < a._mt_translations.size(); ++i) {
            if (j < a._mt_translations[i].size()) {
                if (a._aligned[i][j].empty()) { // not aligned
                    ////unsigned int minimum_score = j/PARAM_BEAM;
                    /// extend half of the hypotheses with and half w/o
                    for (unsigned int k = 0; 
                            k < sentinel->clean_roots.size(); ++k) {
#ifdef DEBUG
                        ////wcout << "j: " << j << " score: " << sentinel->
                        ////    clean_roots[k].score << " minimum_score: " 
                        ////    << minimum_score << endl;
#endif
                        ////if (sentinel->clean_roots[k].score < minimum_score)
                        ////    continue;
                        // w/o
                        sentinel->lasts.push_back(CW_S_VUsed(sentinel->
                                    clean_roots[k].cw, sentinel->clean_roots
                                    [k].score, sentinel->clean_roots[k].used));
                        // with, if the word hasn't already been used
                        if (!sentinel->clean_roots[k].used[i].count(j)) {
                            Chained_Word* tmp = new Chained_Word(false, 
                                    &a._mt_translations[i][j], i, j);
                            sentinel->lasts.push_back(CW_S_VUsed(tmp,
                                        sentinel->clean_roots[k].score,
                                        sentinel->clean_roots[k].used));
                            sentinel->lasts[sentinel->lasts.size() - 1]
                                .used[i].insert(j);
                            sentinel->clean_roots[k].cw->nexts
                                .push_back(tmp);
                        }
                    }
                } else { // aligned
                    /// extend the hypotheses with and w/o != but aligned
                    for (unsigned int k = 0; 
                            k < sentinel->clean_roots.size(); ++k) {
                        if (sentinel->clean_roots[k].cw->nmt != i
                                && sentinel->clean_roots[k].cw->word != NULL)
                            continue; // TODO unhack that
                        bool place_current = true;
                        for (std::list<std::pair<unsigned int, int> >::iterator 
                                al = a._aligned[i][j].begin(); 
                                al != a._aligned[i][j].end(); ++al) {
                            /// determine if we have to add the [i][j]
                            /// and/or its aligned word to the [k] root
                            bool place_aligned = true;
                            if (!a._mt_translations[i][j].compare(
                                        a._mt_translations[al->first]
                                        [al->second])) {
                                place_aligned = false;
                            }
                            for (std::vector<Chained_Word*>::iterator it = 
                                    sentinel->clean_roots[k].cw->nexts
                                    .begin(); it != sentinel->clean_roots[k]
                                    .cw->nexts.end(); ++it) {
                                if (place_current && (*it)->nmt == i &&
                                        (*it)->nword == j) {
                                    place_current = false;
                                } // redundant "if" with the loop
                                if (place_aligned && !(*it)->nmt == al->first
                                        && (*it)->nword == static_cast
                                        <unsigned int>(al->second)) {
                                    place_aligned = false;
                                }
                            }
                            if (place_aligned && 
                                    !sentinel->clean_roots[k]
                                    .used[al->first].count(al->second)) { 
                                Chained_Word* tmp = new Chained_Word(
                                        true, &a._mt_translations
                                        [al->first][al->second], 
                                        al->first, al->second);
                                sentinel->lasts.push_back(CW_S_VUsed(tmp,
                                            sentinel->clean_roots[k].score + 1,
                                            sentinel->clean_roots[k].used));
                                for (std::list<std::pair<unsigned int, int> >::
                                        iterator x = a._aligned[al->first]
                                        [al->second].begin(); 
                                        x != a._aligned[al->first]
                                        [al->second].end(); ++x) {
                                    sentinel->lasts[sentinel->lasts.size() - 1]
                                        .used[x->first].insert(x->second);
                                }
                                sentinel->lasts[sentinel->lasts.size() - 1]
                                    .used[al->first].insert(al->second);
                                sentinel->clean_roots[k].cw->nexts
                                    .push_back(tmp);
                            }
                        }
                        if (place_current && 
                                !sentinel->clean_roots[k].used[i].count(j)) {
                            Chained_Word* tmp = new Chained_Word(
                                    true, &a._mt_translations[i][j], i, j);
                            sentinel->lasts.push_back(CW_S_VUsed(tmp,
                                        sentinel->clean_roots[k].score + 1,
                                        sentinel->clean_roots[k].used));
                            for (std::list<std::pair<unsigned int, int> >::
                                    iterator x = a._aligned[i][j].begin(); 
                                    x != a._aligned[i][j].end(); ++x) {
                                sentinel->lasts[sentinel->lasts.size() - 1]
                                    .used[x->first].insert(x->second);
                            }
                            sentinel->lasts[sentinel->lasts.size() - 1]
                                .used[i].insert(j);
                            sentinel->clean_roots[k].cw->nexts
                                .push_back(tmp);
                        } 
                    }
                }
            }
        }
    }
    sentinel->fill_hypotheses(h);
#ifdef DEBUG
    wcout << "number of hypotheses: " << h.size() << endl;
#endif
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

