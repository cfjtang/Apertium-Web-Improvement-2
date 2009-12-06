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

#include <algorithm>
#include <utility>
#include <cctype>

#include "pairwise_alignment.hh"

/** Indice of the maximum in a vector of positive integers
 */
int ind_max(std::vector<int>& v) {
	int ind = 0;
	for (unsigned int i = 0; i < v.size(); ++i) {
		if (v[i] > v[ind])
			ind = i;
	}
	return ind;
}

/** Erase all the blank/spaces before the first word 
 * and after the last in a (w)string
 */
void strip(wstring& s) {
	wchar_t space = L' ';
	std::wstring::iterator it = s.begin();
	if ((*it) == space) {
		while ((*it) == space)
			it++;
		s.erase(s.begin(), it);
	}
	it = s.end();
	if ((*it) == space) {
		while ((*it) == space)
			it--;
		s.erase(it, s.end());
	}
}

Pairwise_Alignment::Pairwise_Alignment(wstring& left, wstring& right) {
	to_vec(left, _words_left);
	to_vec(right, _words_right);
	initialize();
}

Pairwise_Alignment::Pairwise_Alignment(const wchar_t* left,
		const wchar_t* right) {
	wstring l = wstring(left);
	wstring r = wstring(right);
	to_vec(l, _words_left);
	to_vec(r, _words_right);
	initialize();
}

Pairwise_Alignment::Pairwise_Alignment(std::vector<wstring>& left, std::vector<
		wstring>& right, unsigned int ileft, unsigned int iright) {
	_index_mte_left = ileft;
	_index_mte_right = iright;
	// copy(left.begin(), left.end(), _words_left.begin()); TODO find bug
	for (std::vector<wstring>::iterator it = left.begin(); it != left.end(); ++it)
		_words_left.push_back(*it);
	// copy(right.begin(), right.end(), _words_right.begin());
	for (std::vector<wstring>::iterator it = right.begin(); it != right.end(); ++it)
		_words_right.push_back(*it);
	initialize();
}

Pairwise_Alignment::~Pairwise_Alignment() {
}

void Pairwise_Alignment::initialize() {
	_final_alignment.insert(_final_alignment.begin(), _words_right.size(), -1);
	_matching.resize(_words_right.size());
	_score = 0;
	return;
}

/*** search for matching words
 */
void Pairwise_Alignment::match(Matcher& m) {
	for (unsigned int j = 0; j < _words_right.size(); ++j) {
		_matching[j].resize(_words_left.size());
		for (unsigned int i = 0; i < _words_left.size(); ++i) {
			/// here we can use another matching
			if (m.match(_words_left[i], _words_right[j])) {
				_matching[j][i] = true;
			} else {
				_matching[j][i] = false;
			}
		}
	}
}

/*** unmatch already aligned words
 */
void Pairwise_Alignment::unmatch() {
	for (unsigned int j = 0; j < _words_right.size(); ++j) {
		if (_final_alignment[j] != -1) {
			for (unsigned int i = 0; i < _words_left.size(); ++i) {
				_matching[j][i] = false;
			}
			for (unsigned int k = 0; k < _words_right.size(); ++k) {
				_matching[k][_final_alignment[j]] = false;
			}
		}
	}
}

void Pairwise_Alignment::align(std::list<pair<int, int> >& leftright) {
	for (std::list<pair<int, int> >::iterator it = leftright.begin(); it
			!= leftright.end(); ++it) {
		_final_alignment[it->second] = it->first;
	}
}

void Pairwise_Alignment::print() {
	wcout << "<<<<<<<<<<<<<< RIGHT >>>>>>>>>>>>>>>" << endl;
	for (unsigned int i = 0; i < _final_alignment.size(); ++i) {
		if (_final_alignment[i] != -1) {
			wcout << _words_left[_final_alignment[i]] << " --> "
					<< _words_right[i] << endl;
		} else {
			wcout << "unaligned" << endl;
		}
	}
	wcout << "<<<<<<<<<<<<<< LEFT >>>>>>>>>>>>>>>" << endl;
	for (unsigned int i = 0; i < _final_alignment_left.size(); ++i) {
		if (_final_alignment_left[i] != -1) {
			wcout << _words_right[_final_alignment_left[i]] << " --> "
					<< _words_left[i] << endl;
		} else {
			wcout << "unaligned" << endl;
		}
	}
}

void to_vec(wstring& line, std::vector<wstring>& words) {
	wchar_t space = L' ';
	int index = -1;
	int offset = 0;
	int length = 0;
	while ((index = line.find(space, offset)) != -1) {
		length = index - offset;
		if (length > 0) {
			// words.push_back(wstring(line, offset, length));
			// shouldn't this stripping be done beforehand? TODO
			wstring raw_word = wstring(line, offset, length);
			strip(raw_word);
			words.push_back(raw_word);
		}
		offset = index + 1;
	}
	wstring last(line, offset);
	if (last.length() > 0)
		words.push_back(last);
}

#endif
