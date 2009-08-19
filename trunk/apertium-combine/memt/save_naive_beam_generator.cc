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

    // typedef std::vector<std::pair<unsigned int, std::vector<Word> > > 
    //     scored_phrases;
    // scored_phrases wv;
    // std::vector<std::vector<Word> > words_vector;
    // fill_words(words_vector, a);

    // typedef std::vector<std::pair<bool, Chained_Word*> > hypotheses_vector;
    // hypotheses_vector hyp_vector;
    // std::vector<std::pair<bool, std::list<std::pair<bool, wstring> > > > hyp_vector;
    // std::list<std::pair<bool, wstring> > tmp = 
    //    std::list<std::pair<bool, wstring> >();
    // hyp_vector.push_back(std::pair<bool, std::list<std::pair<bool, wstring>
    //        > >(false, tmp));
    Chained_Word_Sentinel* sentinel = new Chained_Word_Sentinel();
    // hyp_vector.push_back(std::pair<bool, Chained_Word*>(false, sentinel));
    wcout << "before j loop" << endl;
    for (unsigned int j = 0; j < length; ++j) {
        //        for (unsigned int k = 0; k < hyp_vector.size(); ++k)
        //            hyp_vector[k].first = false;
        for (std::vector<std::pair<bool, Chained_Word*> >::iterator it = 
                sentinel->lasts.begin(); it != sentinel->lasts.end(); ++it)
            it->first = false;
        wcout << "before i loop" << endl;
        for (unsigned int i = 0; i < a._mt_translations.size(); ++i) {
            /*
               unsigned int hyp_size = hyp_vector.size();
               std::list<unsigned int> indices;
            // TODO optimize 
            for (unsigned int k = 0; k < hyp_size; ++k) {
            if (!hyp_vector[k].first) {
            hyp_vector.push_back(hyp_vector[k]);
            indices.push_back(hyp_vector.size() - 1);
            }
            }
            */
            if (j < a._mt_translations[i].size()) {
                /// copy and extend half of the hypotheses with and half w/o
                if (a._aligned[i][j].empty()) {
                    wcout << "not aligned" << endl;
                    unsigned int size = sentinel->lasts.size();
                    for (unsigned int k = 0; k < size; ++k) {
                        if (!sentinel->lasts[k].first) {
                            sentinel->lasts.push_back(
                                    std::pair<bool, Chained_Word*>
                                    (true, sentinel->lasts[k].second)); // w/o
                            Chained_Word* tmp = new Chained_Word(false, 
                                    &a._mt_translations[i][j]);
                            sentinel->lasts.push_back(
                                    std::pair<bool, Chained_Word*>
                                    (true, tmp));                       // with
                            sentinel->lasts[k].second->nexts.push_back(tmp);
                        }
                    }
                    // /* 
                    //                    unsigned int hyp_size = hyp_vector.size();
                    //                    std::list<unsigned int> indices;
                    //                    for (unsigned int k = 0; k < hyp_size; ++k) {
                    //                        if (!hyp_vector[k].first) {
                    //                            hyp_vector.push_back(hyp_vector[k]);
                    //                            hyp_vector[hyp_vector.size() - 1].first = true;
                    //                            wcout << "extends with nothing" << endl;
                    //                            hyp_vector.push_back(hyp_vector[k]);
                    //                            indices.push_back(hyp_vector.size() - 1);
                    //                            // hyp_vector[k].first = false;
                    //                        }
                    //                    }
                    //                    // */
                    //                    for (std::list<unsigned int>::iterator it = indices.begin();
                    //                            it != indices.end(); ++it) {
                    //                        hyp_vector[*it].second.push_back(std::pair<bool,
                    //                                wstring>(false, a._mt_translations[i][j]));
                    //                        hyp_vector[*it].first = true;
                    //                        wcout << "extends with: " << a._mt_translations[i][j] << endl;
                    //                    }
                    /// copy and extend the hypotheses with and w/o != but aligned
                } else { // aligned
                    wcout << "aligned" << endl;
                    for (std::list<std::pair<unsigned int, int> >::iterator 
                            al = a._aligned[i][j].begin(); 
                            al != a._aligned[i][j].end(); ++al) {
                        //                        if (a._mt_translations[i][j].compare(
                        //                                    a._mt_translations[al->first][al->second])
                        //                                != 0) { 
                        unsigned int size = sentinel->lasts.size();
                        for (unsigned int k = 0; k < size; ++k) {
                            if (!sentinel->lasts[k].first) {
                                /// determine if we have to add the [i][j]
                                /// and/or its aligned word 
                                bool place_current = true;
                                bool place_aligned = true;
                                for (std::vector<Chained_Word*>::iterator it = 
                                        sentinel->lasts[k].second
                                        ->nexts.begin();
                                        it != sentinel->lasts[k].second
                                        ->nexts.end();
                                        ++it) {
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
                                    sentinel->lasts.push_back(
                                            std::pair<bool, Chained_Word*>
                                            (true, tmp));
                                    sentinel->lasts[k].second->nexts.push_back(
                                            tmp);
                                }
                                if (place_aligned) { 
                                    Chained_Word* tmp = new Chained_Word(
                                            true, &a._mt_translations
                                            [al->first][al->second]);
                                    sentinel->lasts.push_back(
                                            std::pair<bool, Chained_Word*>
                                            (true, tmp));
                                    sentinel->lasts[k].second->nexts.push_back(
                                            tmp);
                                }
                            }
                        }
                    }
                    // /*
                    //                            unsigned int hyp_size = hyp_vector.size();
                    //                            std::list<unsigned int> indices;
                    //                            for (unsigned int k = 0; k < hyp_size; ++k) {
                    //                                if (!hyp_vector[k].first) {
                    //                                    hyp_vector.push_back(hyp_vector[k]);
                    //                                    indices.push_back(hyp_vector.size() - 1);
                    //                                    // hyp_vector[k].first = false;
                    //                                }
                    //                            }
                    // */
                    //                            for (std::list<unsigned int>::iterator it = 
                    //                                    indices.begin();
                    //                                    it != indices.end(); ++it) {
                    //                                wcout << "indice: " << *it << endl;
                    //                                bool test = false;
                    //                                for (unsigned int k = 0; 
                    //                                        k < hyp_vector.size(); ++k) {
                    //                                    if (hyp_vector[k].second[hyp_vector[k]
                    //                                            .second.size() - 1] 
                    //                                            == a._mt_translations[i][j])
                    //                                        test = true;
                    //                                }
                    //                                if (!test) { // TODO NO OTHER LIKE THIS ONE
                    //                                    hyp_vector[*it].second.push_back(std::pair<bool,
                    //                                            wstring>(true, 
                    //                                                a._mt_translations[al->first][
                    //                                                al->second]));
                    //                                    hyp_vector[*it].first = true;
                    //                                    wcout << "extends with: " << a._mt_translations[al->first][al->second] << endl;
                    //                                }
                    //                            }
                    //                        }
                    //                        {
                    //                            // /*
                    //                            unsigned int hyp_size = hyp_vector.size();
                    //                            std::list<unsigned int> indices;
                    //                            for (unsigned int k = 0; k < hyp_size; ++k) {
                    //                                if (!hyp_vector[k].first) {
                    //                                    hyp_vector.push_back(hyp_vector[k]);
                    //                                    indices.push_back(hyp_vector.size() - 1);
                    //                                    // hyp_vector[k].first = false;
                    //                                }
                    //                            }
                    //                            // */
                    //                            for (std::list<unsigned int>::iterator it = 
                    //                                    indices.begin();
                    //                                    it != indices.end(); ++it) {
                    //                                bool test = false;
                    //                                for (unsigned int k = 0; 
                    //                                        k < hyp_vector.size(); ++k) {
                    //                                    if (hyp_vector[k].second[hyp_vector[k]
                    //                                            .second.size() - 1] 
                    //                                            == a._mt_translations[i][j])
                    //                                        test = true;
                    //                                }
                    //
                    //                                if (!test) { // TODO NO OTHER LIKE THIS ONE
                    //                                    hyp_vector[*it].second.push_back(
                    //                                            std::pair<bool, wstring>(true, 
                    //                                                a._mt_translations[i][j]));
                    //                                    hyp_vector[*it].first = true;
                    //                                }
                    //                            }
                    //                        }
                }
            }
        }
        wcout << "before erasing" << endl;
        for (std::vector<std::pair<bool, Chained_Word*> >::iterator it = 
                sentinel->lasts.begin(); it != sentinel->lasts.end(); ++it) {
            if (!it->first) {
                sentinel->lasts.erase(it);
            }
        }
        wcout << "after erasing" << endl;
        //            for (std::vector<std::pair<bool, std::list
        //                    <std::pair<bool, wstring> > > >::iterator it = 
        //                    hyp_vector.begin(); it != hyp_vector.end(); ++it) {
        //                if (!it->first) {
        //                    // wcout << "erasing" << endl;
        //                    hyp_vector.erase(it);
        //                }
        //            }
    }
    /*
       for (std::vector<std::pair<bool, std::list
       <std::pair<bool, wstring> > > >::iterator it = 
       hyp_vector.begin(); it != hyp_vector.end(); ++it) {
       if (!it->first) {
       wcout << "erasing" << endl;
       hyp_vector.erase(it);
       }
       }
       */
    //    }

    /// filling of the "h"
    //std::vector<std::pair<bool, std::list<std::pair<bool, wstring> > > > 
    //    hyp_vector;
    //    for (std::vector<std::pair<bool, std::list<
    //            std::pair<bool, wstring> > > >::iterator it = 
    //            hyp_vector.begin(); it != hyp_vector.end(); ++it) {
    //        std::list<wstring> temp;
    //        unsigned int score = 0;
    //        for (std::list<std::pair<bool, wstring> >::iterator w = 
    //                it->second.begin();
    //                w != it->second.end(); ++w) {
    //            if (w->first) 
    //                ++score;
    //            temp.push_back(w->second);
    //        }
    //        h.push_back(Hypothesis(score, temp));
    //    } 
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

