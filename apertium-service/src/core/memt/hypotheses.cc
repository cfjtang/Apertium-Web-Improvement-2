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

#include "hypotheses.hh"

#ifdef DEBUG
#define HYP_PRINT_NUMBER_LIMIT 15
#endif

using namespace std;

unsigned int max_length(scored_phrases& wv) {
    unsigned int max = 0;
    for (scored_phrases::iterator it = wv.begin(); it != wv.end(); ++it) {
        if (it->second.size() > max) {
            max = it->second.size();
        }
    } 
    return max;
}

void fill_words(std::pair<unsigned int, std::vector<Word> > &words, Pairwise_Alignment& a, unsigned int length) {
    for (unsigned int i = 0; i < length; ++i) {
        if (i < a._words_right.size()) {
            if (a._final_alignment[i] != -1) {
                words.second.push_back(Word(false, true, 1, i, a._words_right[i]));
            } else {
                words.second.push_back(Word(false, false, 1, i, a._words_right[i]));
            }
        }

        if (i < a._words_left.size()) {
            if (a._final_alignment_left[i] == -1) { // add if not aligned
                words.second.push_back(Word(false, false, 0, i, a._words_left[i]));
            }
        }
    }
}

void fill_words(std::vector<std::vector<Word> > &words_vector, Alignment &a) {
    for (unsigned int k = 0; k < a._mt_translations.size(); ++k) {
        std::vector<Word> temp_words;
        for (unsigned int i = 0; i < a._mt_translations[k].size(); ++i) {
            if (a._aligned[k][i].empty()) {
                temp_words.push_back(Word(false, false, k, i, a._mt_translations[k][i]));
            } else {
                temp_words.push_back(Word(false, true, k, i, a._mt_translations[k][i]));
            }
        }
        words_vector.push_back(temp_words);
    }
}

bool Hypothesis::operator<(const Hypothesis& h) 
{
    return (score > h.score);
}

/*** Very basic hypothesis generator.
 * TODO:
 *   Should take in account the fact that alignment won't alws be exactMatching
 */
Hypotheses::Hypotheses(Alignment& a, Generator& g) 
{
    g.generate(a, _hypotheses);
}

Hypotheses::~Hypotheses() 
{
}

void Hypotheses::rank(Ranker* r) 
{
    r->rank(_hypotheses);
    return;
}

/// for drawkcab compatibility, with scores.
void Hypotheses::print()
{
    wcout << ">>> Hypotheses: " << endl;
    unsigned int count = 0;
    for (std::list<Hypothesis>::iterator it = _hypotheses.begin();
            it != _hypotheses.end();
            ++it) {
        wstring s;
        concatenate(it->words, s);
#ifdef DEBUG
        //if (count < HYP_PRINT_NUMBER_LIMIT) {
            wcout << "score: " << it->score << " == " << s << endl;
        //}
#endif
        ++count;
    }
    //wcout << "truncated because #hyp >= " << HYP_PRINT_NUMBER_LIMIT << endl;
}

void Hypotheses::print(wfstream* where)
{
    std::list<Hypothesis>::iterator it = _hypotheses.begin();
    wstring s;
    concatenate(it->words, s);
    (*where) << "{" << s;
    for(; it != _hypotheses.end(); ++it) {
        s.clear();
        concatenate(it->words, s);
        (*where) << "|" << s ;
    }
    (*where) << "}" << endl;
}

wstring Hypotheses::best() {
    wstring s = L"";
    concatenate(_hypotheses.front().words, s);
    //wcout << s << endl;
    return s;
}

void inline Hypotheses::concatenate(std::list<wstring>& ls, wstring& s) 
{
    const wchar_t wc = L' ';
    for (std::list<wstring>::iterator w = ls.begin();
            w != ls.end(); ) {
        s.append(*w);
        if (++w != ls.end())
            s.append(1, wc);
    }
}

#endif
