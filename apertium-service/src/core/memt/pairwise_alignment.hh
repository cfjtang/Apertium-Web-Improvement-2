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

/*
 * A word-to-word aligner between two hypotheses:
 * - one that comes from stdin, from lt-proc -g (Apertium)
 * - one that comes from tmp buffer, from Moses
 */

#ifndef PAIRWISE_ALIGNMENT_HH
#define PAIRWISE_ALIGNMENT_HH

#include "config.h"

#if defined(HAVE_IRSTLM)

#include <iostream>
#include <vector>
#include <list>
#include <utility>
#include <fstream>
#include <string>

#include "matcher.hh"
#include "aligner.hh"

using namespace std;

void to_vec(wstring& line, std::vector<wstring>& words);

class Pairwise_Alignment {
public:
	unsigned int _index_mte_left;
	unsigned int _index_mte_right;
	std::vector<wstring> _words_left;
	std::vector<wstring> _words_right;
	std::vector<int> _final_alignment; // result of align()
	std::vector<int> _final_alignment_left; // result of align()

	Pairwise_Alignment(wstring& left, wstring& right);
	Pairwise_Alignment(const wchar_t* left, const wchar_t* right);
	Pairwise_Alignment(std::vector<wstring>& left, std::vector<wstring>& right,	unsigned int ileft, unsigned int iright);
	~Pairwise_Alignment();

	int _score;
	std::vector<std::vector<bool> > _matching; // _matching[right][left]
	void align(std::list<pair<int, int> >& leftright);
	void print();

	void match(Matcher& m);
	void unmatch();
private:
	void initialize();
	void inline complete();
};

#endif

#endif
