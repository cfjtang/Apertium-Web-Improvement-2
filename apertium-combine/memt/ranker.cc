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
    /// TODO
    const wstring ws = L"this is é à ï test";
    string s = ToUtf8(ws);

    for(std::list<Hypothesis>::iterator it = hypotheses.begin();
            it != hypotheses.end(); ++it) {
    }
        
    // std::list<wstring> words;
    // Hypothesis(unsigned int s, std::list<wstring>& l)
    // {
    //     score = s;
    //     words = l;
    // }

    hypotheses.sort();
    return;
}
