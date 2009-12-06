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

#ifndef ALIGNMENT_HH
#define ALIGNMENT_HH

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
#include "pairwise_alignment.hh"

using namespace std;

class Pairwise_Alignment;

class Alignment {
public:
	std::vector<Pairwise_Alignment> _pw_alignments;
	/// _mt_translations[#transl][#word] = word #word in transl #transl
	std::vector<std::vector<wstring> > _mt_translations;
	/// _aligned[#transl][#word] ==
	/// list of aligned words: (#transl2, #word2)
	std::vector<std::vector<std::list<std::pair<unsigned int, int> > > >
			_aligned;
	/// _ngrams_aligned[0] = bigrams, _ngrams_aligned[1] = trigrams, etc.
	/// _ngrams_aligned[0][0][0] = (1, 2) means that the first bigram
	/// consituted of word 0 and 1 of mt=0 is aligned with the first one
	/// of word 2 and 3 of mt=1. [Notations from the first word]
	std::vector<std::vector<std::vector<
			std::list<std::pair<unsigned int, int> > > > > _ngrams_aligned;
	Alignment(std::vector<wstring>& input_lines);
	~Alignment();
	void match(Matcher& m);
	void align(Aligner& aligner, unsigned int ngram_level);
	void print();

	void print_aligned(); // TODO templatize
	void print_mt_translations(); // TODO templatize
	void print_ngrams_aligned(); // TODO templatize
};

#endif

#endif
