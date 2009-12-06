/**
 * @section LICENSE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @section DESCRIPTION
 *
 * The class ApertiumXMLRPCService implements a service using the XML-RPC protocol
 * and exposing Apertium's capabilities for translation and language detection.
 * XML-RPC is a remote procedure call protocol which uses XML to encode its calls
 * and HTTP as a transport mechanism.
 */

#include "config.h"

#if defined(HAVE_IRSTLM)

#include <assert.h>

#include "parallel_scan_generator.hh"

using namespace std;

/**
 * Find in the [mtarg][wordarg]._aligned list passed as l if a word is [mt][w]
 */
bool inline find_aligned(std::list<std::pair<unsigned int, int> >& l,
		const unsigned int mt, const int w) {
	if (l.empty())
		return false;
	for (std::list<std::pair<unsigned int, int> >::iterator it = l.begin(); it
			!= l.end(); ++it) {

		if (it->second == w && it->first == mt) // BUS ERROR TODO
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
void Parallel_Scan_Generator::generate(Alignment& a, std::list<Hypothesis>& h) {
	// TODO: better gen., discriminate same words following each other
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
		sentinel->lasts[sentinel->lasts.size() - 1] .used[mt].insert(0);
		sentinel->clean_roots[0].cw->nexts.push_back(tmp);
		sentinel->swap_clear_lasts();

		unsigned int size_end = a._mt_translations[mt].size() - 1;
		for (unsigned int nword = 0; nword < size_end; ++nword) {

			for (unsigned int k = 0; k < sentinel->clean_roots.size(); ++k) {

				if (!sentinel->clean_roots[k].used[mt].count(nword + 1)) {
					aligned = a._aligned[mt][nword + 1].empty() ? false : true;
					score = aligned ? 1 : 0;
					Chained_Word* tmp = new Chained_Word(aligned,
							&a._mt_translations[mt][nword + 1], mt, nword + 1);
					sentinel->lasts.push_back(CW_S_VUsed(tmp,
							sentinel->clean_roots[k].score + score,
							sentinel->clean_roots[k].used));
					sentinel->lasts[sentinel->lasts.size() - 1] .used[mt].insert(
							nword + 1);
					sentinel->clean_roots[k].cw->nexts.push_back(tmp);
				}

				for (std::list<std::pair<unsigned int, int> >::iterator al =
						a._aligned[mt][nword].begin(); al
						!= a._aligned[mt][nword].end(); ++al) {
					if (!find_aligned(a._aligned[al->first][al->second + 1],
							mt, nword + 1)
							&& !sentinel->clean_roots[k] .used[al->first].count(
									al->second + 1)) {
						aligned
								= a._aligned[al->first][al->second + 1] .empty() ? false
										: true;
						score = aligned ? 1 : 0;
						Chained_Word* tmp = new Chained_Word(aligned,
								&a._mt_translations[al->first][al->second + 1],
								al->first, al->second + 1);
						sentinel->lasts.push_back(CW_S_VUsed(tmp,
								sentinel->clean_roots[k].score + score,
								sentinel->clean_roots[k].used));
						sentinel->lasts[sentinel->lasts.size() - 1] .used[al->first].insert(
								al->second + 1);
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

#endif

