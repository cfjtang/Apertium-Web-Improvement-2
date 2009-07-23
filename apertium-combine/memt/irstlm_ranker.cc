#include "irstlm_ranker.hh"
#include <utf_converter.h>

IRSTLM_Ranker::IRSTLM_Ranker(const string& file_path)
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

IRSTLM_Ranker::~IRSTLM_Ranker() 
{
}

void inline IRSTLM_Ranker::score(Hypothesis& h)
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
    cout << lmscore << endl;
#endif
    float sc = -100 / (lmscore);
    h.score += sc; /// TODO
    delete lmtb_ngram;
    return;
}
