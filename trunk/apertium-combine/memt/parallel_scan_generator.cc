#include "parallel_scan_generator.hh"
#include <assert.h>

using namespace std;

bool inline find_aligned(std::list<std::pair<unsigned int, int> >& l, 
        const unsigned int mt, const int w)
{
    for (std::list<std::pair<unsigned int, int> >::iterator it =
            l.begin(); it != l.end(); ++it) {
        if (it->second == w && it->first == mt)
            return true;
    }
    return false;
}

/**
 * Parallel scan, uses two cases on words (we ALWAYS put only !used words): 
 *  aligned word: we put the following and the following of all the aligned
 *      words that are not aligned with the first following
 *  not aligned: we put the following
 */
void Parallel_Scan_Generator::generate(Alignment& a, std::list<Hypothesis>& h)
    // TODO: better gen., discriminate same words following each other
{
    for (unsigned int mt = 0; mt < a._mt_translations.size(); ++mt) {
        Chained_Word_Sentinel* sentinel = new Chained_Word_Sentinel(
                a._mt_translations.size());
        sentinel->swap_clear_lasts();
        // we put the first word of this mt-translation
        bool aligned = a._aligned[mt][0].empty() ? false : true;
        unsigned int score = aligned ? 1 : 0;
        Chained_Word* tmp = new Chained_Word(aligned, 
                &a._mt_translations[mt][0], mt, 0);
        sentinel->lasts.push_back(CW_S_VUsed(tmp,
                    sentinel->clean_roots[0].score + score,
                    sentinel->clean_roots[0].used));
        sentinel->lasts[sentinel->lasts.size() - 1]
            .used[mt].insert(0);
        sentinel->clean_roots[0].cw->nexts.push_back(tmp);
        sentinel->swap_clear_lasts();

        unsigned int size_end = a._mt_translations[mt].size() - 1;
        for (unsigned int nword = 0; nword < size_end ; ++nword) {

            for (unsigned int k = 0; k < sentinel->clean_roots.size(); ++k) {

                if (!sentinel->clean_roots[k].used[mt].count(nword + 1)) {
                    aligned = a._aligned[mt][nword + 1].empty() ? false : true;
                    score = aligned ? 1 : 0;
                    Chained_Word* tmp = new Chained_Word(aligned, 
                            &a._mt_translations[mt][nword + 1], mt, nword + 1);
                    sentinel->lasts.push_back(CW_S_VUsed(tmp,
                                sentinel->clean_roots[k].score + score,
                                sentinel->clean_roots[k].used));
                    sentinel->lasts[sentinel->lasts.size() - 1]
                        .used[mt].insert(nword + 1);
                    sentinel->clean_roots[k].cw->nexts.push_back(tmp);
                }

                for (std::list<std::pair<unsigned int, int> >::iterator al =
                        a._aligned[mt][nword].begin(); 
                        al != a._aligned[mt][nword].end(); ++al) {
                    if (!find_aligned(a._aligned[al->first][al->second +1], 
                                mt, nword + 1) && !sentinel->clean_roots[k]
                            .used[al->first].count(al->second + 1)) {
                        aligned = a._aligned[al->first][al->second + 1]
                            .empty() ? false : true;
                        score = aligned ? 1 : 0;
                        Chained_Word* tmp = new Chained_Word(aligned, 
                                &a._mt_translations[al->first][al->second + 1],
                                al->first, al->second + 1);
                        sentinel->lasts.push_back(CW_S_VUsed(tmp,
                                    sentinel->clean_roots[k].score + score,
                                    sentinel->clean_roots[k].used));
                        sentinel->lasts[sentinel->lasts.size() - 1]
                            .used[al->first].insert(al->second + 1);
                        sentinel->clean_roots[k].cw->nexts.push_back(tmp);
                    }
                }
            }
            sentinel->swap_clear_lasts();
        }
        sentinel->fill_hypotheses(h);
        delete sentinel;
    }
#ifdef DEBUG
    wcout << "number of hypotheses: " << h.size() << endl;
#endif
}
