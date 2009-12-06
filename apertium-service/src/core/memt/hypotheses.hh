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
 * A bag of hypotheses that provides the following:
 * - hypothesis creation by invoking a generator
 * - basic services on hypotheses
 * - hypothesis ranking by invoking a ranker
 */

#ifndef HYPOTHESES_HH
#define HYPOTHESES_HH

#include "config.h"

#if defined(HAVE_IRSTLM)

#include <iostream>
#include <list>
#include <utility>
#include <vector>

#include "alignment.hh"
#include "ranker.hh"
#include "generator.hh"

class Ranker;
class Generator;

using namespace std;

struct Hypothesis {
    unsigned int score;
    std::list<wstring> words;

    Hypothesis(unsigned int s, std::list<wstring>& l) {
        score = s;
        words = l;
    }

    Hypothesis(unsigned int s, std::list<wstring*>& l) {
        score = s;
        for (std::list<wstring*>::iterator it = l.begin(); it != l.end(); ++it) {
            if (*it != NULL) {
                words.push_back(**it);
            }
        }
    }

    ~Hypothesis() { }

    bool operator<(const Hypothesis& h);
};

struct Word {
    bool used;
    bool aligned;
    unsigned int side;
    unsigned int ind;
    wstring word;

    Word(bool u, bool a, unsigned int s, unsigned int i, wstring w) {
        used = u;
        aligned = a;
        side = s;
        ind = i;
        word = w;
    }

    ~Word() { }
};

typedef std::vector<std::pair<unsigned int, std::vector<Word> > > 
    scored_phrases;

unsigned int max_length(scored_phrases& wv);

void fill_words(std::pair<unsigned int, std::vector<Word> > &words, Pairwise_Alignment& a, unsigned int length);

void fill_words(std::vector<std::vector<Word> > &words_vector, Alignment &a);

class Hypotheses {
    public: 
        Hypotheses(Alignment& a, Generator& g);
        ~Hypotheses();
        void rank(Ranker* r);
        void print();
        void print(wfstream* where);
        wstring best();
    private:
        std::list<Hypothesis> _hypotheses;
        void inline concatenate(std::list<wstring>& ls, wstring& s);
};

#endif

#endif
