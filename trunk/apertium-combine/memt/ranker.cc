#include "ranker.hh"

Ranker::Ranker(const string& file_path)
{
    // open the LM file
    std::filebuf* fb = new std::filebuf();
    fb->open(file_path.c_str(), std::ios::in);
    std::streambuf* sb = fb;
    std::istream inputstream(sb);

    // create an lmtable object
    /* lmtb = (lmtable *)new lmtable;
    if (file_path.compare(file_path.size()-3,3,".mm")==0) {
        lmtb->load(inputstream, file_path.c_str(), NULL, 1);
    } else {
        lmtb->load(inputstream, file_path.c_str(), NULL, 0);
    }*/
}

Ranker::~Ranker() 
{
}

void Ranker::rank(std::list<Hypothesis>& hypotheses)
{
    return;
}
