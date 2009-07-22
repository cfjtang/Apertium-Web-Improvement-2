#include "lmtable.h"
#include "lmmacro.h"
#include "hypotheses.hh"

#include "ranker.hh"
#include "UtfConverter.h"

Ranker::Ranker(const string& file_path)
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

Ranker::~Ranker() 
{
}

void Ranker::rank(std::list<Hypothesis>& hypotheses)
{
    for (std::list<Hypothesis>::iterator it = hypotheses.begin();
            it != hypotheses.end(); ++it) {
        int n = 0;
        ngram* lmtb_ngram = new ngram(lmtb->getDict());
        for (std::list<wstring>::iterator i = it->words.begin();
                i != it->words.end(); ++i) {
            string s = ToUtf8(*i);
            n = lmtb->getDict()->encode(s.c_str());
            lmtb_ngram->pushc(n);
        } 
        float lmscore = lmtb->clprob(*lmtb_ngram);
        cout << lmscore << endl;
        float sc = 100000 * lmscore;
        it->score = sc; // WRONG := (TODO)
        delete lmtb_ngram;
    }
        
    hypotheses.sort();
    return;
}
