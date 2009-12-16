#include "ranker.hh"
#include "irstlm_ranker.hh"
#include "apertium/utf_converter.h"

IRSTLMRanker::IRSTLMRanker(const string& file_path)
{
    // open the LM file
    std::filebuf* fb = new std::filebuf();
    fb->open(file_path.c_str(), std::ios::in);
    std::streambuf* sb = fb;
    std::istream inputstream(sb);

    // create an lmtable object
    lmtb = (lmtable *)new lmtable;
    if (file_path.compare(file_path.size()-3,3,".mm")==0) {
        lmtb->load(inputstream, file_path.c_str(), NULL, 1);
    } else {
        lmtb->load(inputstream, file_path.c_str(), NULL, 0);
    } 
}

IRSTLMRanker::IRSTLMRanker()
{

}

IRSTLMRanker::~IRSTLMRanker() 
{
	if(lmtb) {
		delete lmtb;
	}
}

void inline IRSTLMRanker::score(Hypothesis& h)
{
    int n = 0;
    ngram* lmtb_ngram = new ngram(lmtb->getDict());
    for (std::list<wstring>::iterator i = h.words.begin();
            i != h.words.end(); ++i) {
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
