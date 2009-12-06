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

#include <iostream>
#include <string>

#include "max_conseq_aligner.hh"

using namespace std;

void inline Max_Conseq_Aligner::complete(Pairwise_Alignment& alignment) {
	for (unsigned int j = 0; j < alignment._words_right.size(); ++j) {
		if (alignment._final_alignment[j] == -1) {
			unsigned int i = j;
			unsigned int count = 0;
			while (count < alignment._words_left.size()) {
				if (alignment._matching[j][i]) {
					// unmatch j and i as we "used" it
					for (unsigned int ki = 0; ki < alignment._words_left.size(); ++ki)
						alignment._matching[j][ki] = false;
					for (unsigned int kj = 0; kj
							< alignment._words_right.size(); ++kj)
						alignment._matching[kj][i] = false;
					alignment._final_alignment[j] = i;
					alignment._score++;
					break;
				}
				i = (i + 1) % alignment._words_left.size();
				++count;
			}
		}
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
#ifdef DEBUG
	wcout << ">>> number of aligned words: " << alignment._score << endl;
#endif
}

void Max_Conseq_Aligner::align(Pairwise_Alignment& alignment) {
	unsigned int size_end_left = alignment._words_left.size() - 1;
	unsigned int size_end_right = alignment._words_right.size() - 1;
	std::vector<int> temp_alignment;
	std::vector<int> best_alignment;
	best_alignment.resize(alignment._words_right.size());
	int best_score = 0;
	int tmp_score = 0;
	// align longuest sequence of matching words
	unsigned int i = 0;
	while (i <= size_end_left) {
		unsigned int j = 0;
		while (j <= size_end_right) {
			if (alignment._matching[j][i]) {
				tmp_score = 1;
				temp_alignment.clear();
				temp_alignment.insert(temp_alignment.begin(),
						alignment._words_right.size(), -1);
				temp_alignment[j] = i;
				if (i < size_end_left || j < size_end_right) {
					unsigned int t_i = i + 1;
					unsigned int t_j = j + 1;
					while (t_i <= size_end_left && t_j <= size_end_right
							&& alignment._matching[t_j][t_i]) {
						temp_alignment[t_j] = t_i;
						++t_i;
						++t_j;
						++tmp_score;
					}
				}
				if (tmp_score > best_score) {
					best_score = tmp_score;
					best_alignment.clear();
					temp_alignment.swap(best_alignment);
				}
			}
			++j;
		}
		++i;
	}
#ifdef DEBUG
	wcout << ">>> best score: " << best_score << endl;
#endif
	copy(best_alignment.begin(), best_alignment.end(),
			alignment._final_alignment.begin());
	alignment._score = best_score;
	alignment.unmatch();
	complete(alignment);
}

#endif
