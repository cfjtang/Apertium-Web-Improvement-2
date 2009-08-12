#include "hypotheses.hh"

using namespace std;

unsigned int max_length(scored_phrases& wv) 
{
    unsigned int max = 0;
    for (scored_phrases::iterator it = wv.begin();
            it != wv.end(); ++it) {
        if (it->second.size() > max)
            max = it->second.size();
    } 
    return max;
}

void fill_words(std::pair<unsigned int, std::vector<Word> >
        & words, Pairwise_Alignment& a, unsigned int length)
{
    for (unsigned int i = 0; i < length; ++i) {
        if (i < a._words_right.size())
            if (a._final_alignment[i] != -1)
                words.second.push_back(
                        Word(false, true, 1, i, a._words_right[i]));
            else
                words.second.push_back(
                        Word(false, false, 1, i, a._words_right[i]));
        if (i < a._words_left.size()) {
            if (a._final_alignment_left[i] != -1) {
                if (!a._words_left[i].compare(a._words_right[
                            a._final_alignment_left[i]])) {
                    ; // do not add if aligned and exactmatching
                } else { 
                    words.second.push_back(
                            Word(false, true, 0, i, a._words_left[i]));
                }
            } else {
                words.second.push_back(
                        Word(false, false, 0, i, a._words_left[i]));
            }
        }
    }
}

void fill_word(std::pair<unsigned int, std::vector<Word> >
        & words, Alignment& a, unsigned int length)
{
    for (unsigned int k = 0; k < a._aligned.size(); ++k) {
        for (unsigned int i = 0; i < length; ++i) {
            if (i < a._aligned[k].size()) {
                if (a._aligned[k][i].empty())
                    words.second.push_back(
                            Word(false, false, k, i,
                                a._mt_translations[k][i]));
                else {
                }
            }
        }
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
    for (std::list<Hypothesis>::iterator it = _hypotheses.begin();
            it != _hypotheses.end();
            ++it) {
        wstring s;
        concatenate(it->words, s);
#ifdef DEBUG
        wcout << "score: " << it->score << " == " << s << endl;
#endif
    }
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

void Hypotheses::best()
{
    wstring s = L"";
    concatenate(_hypotheses.front().words, s);
    wcout << s << endl;
    return;
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
