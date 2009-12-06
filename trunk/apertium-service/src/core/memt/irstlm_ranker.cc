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

#include <apertium/utf_converter.h>

#include "ranker.hh"
#include "irstlm_ranker.hh"

IRSTLMRanker::IRSTLMRanker(const string& file_path) {
	// open the LM file
	std::filebuf* fb = new std::filebuf();
	fb->open(file_path.c_str(), std::ios::in);
	std::streambuf* sb = fb;
	std::istream inputstream(sb);

	// create an lmtable object
	lmtb = (lmtable *) new lmtable;
	if (file_path.compare(file_path.size() - 3, 3, ".mm") == 0) {
		lmtb->load(inputstream, file_path.c_str(), NULL, 1);
	} else {
		lmtb->load(inputstream, file_path.c_str(), NULL, 0);
	}
}

IRSTLMRanker::IRSTLMRanker() {
}

IRSTLMRanker::~IRSTLMRanker() {
}

void inline IRSTLMRanker::score(Hypothesis& h) {
	int n = 0;
	ngram* lmtb_ngram = new ngram(lmtb->getDict());
	for (std::list<wstring>::iterator i = h.words.begin(); i != h.words.end(); ++i) {
		string s = UtfConverter::toUtf8(*i);
		n = lmtb->getDict()->encode(s.c_str());
		lmtb_ngram->pushc(n);
	}
	float lmscore = lmtb->clprob(*lmtb_ngram);
#ifdef DEBUG
	// cout << lmscore << endl;
#endif
	double sc = -1000 / (lmscore);
	h.score += sc; /// TODO
	// h.score -= (maximal_size - h.words.size()) * a_big_number; TODO
	delete lmtb_ngram;
	return;
}

#endif
