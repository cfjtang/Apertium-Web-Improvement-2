#include "alignment.hh"
// TODO #include <unicode/unistr.h>

Alignment::Alignment(string& left, string& right) 
{
    toVec(left, _words_left);
    toVec(right, _words_right);
    initialize();
}

Alignment::Alignment(const char* left, const char* right) 
{
    string l = string(left);
    string r = string(right);
    toVec(l, _words_left);
    toVec(r, _words_right);
    initialize();
}

Alignment::~Alignment() 
{
}

void Alignment::initialize() 
{ 
    _final_alignment.insert(_final_alignment.begin(), _words_right.size(), -1);
    _consecutive_alignments = 0;
    _total_matches = 0;
    return;   
}

/*** maximal consecutive alignments
 */
void Alignment::align()
{
    // TODO work in progress
    int consecutives = 0;
    std::vector<int> current_best_alignment;
    std::vector<int> temp_alignment;
    current_best_alignment.reserve(_words_right.size());
    temp_alignment.reserve(_words_right.size());
    /*for (std::vector<string>::iterator i = _words_left.begin(); 
            i != _words_left.end();
            i++) {
        for (std::vector<string>::iterator j = _words_right.begin();
                j != _words_right.end();
                j++) {
            if (exactMatch(*i, *j)) {
                temp_alignment[j] = i;
                break;
            }
        }
    }*/
    
    return;
}

void Alignment::toVec(string& line, 
        std::vector<string>& words) 
{
    // UChar space = ' ';
    char space = ' ';
    int index = -1;
    int offset = 0;
    int length = 0;
    while ((index = line.find(space, offset)) != -1) { 
        length = index - offset;
        if (length > 0) { 
            words.push_back(string(line, offset, length));
        }
        offset = index + 1;
    }
    string last(line, offset);
    if (last.length() > 0) 
        words.push_back(last);
}

void Alignment::toLower(string& to_lower)
{
    // TODO
    std::string::iterator i = to_lower.begin();
    return;
}

int Alignment::exactMatch(const string& left, 
        const string& right) 
{ 
   return left.compare(right);
}

int Alignment::caseInsensitiveMatch(const string& left,
        const string& right)
{
    string l = string(left);
    string r = string(right);
    toLower(l);
    toLower(r);
    return l.compare(r);
}

