#include "alignment.hh"
#include <unicode/unistr.h>

Alignment::Alignment(UnicodeString& left, UnicodeString& right) 
{
    toVec(left, _words_left);
    toVec(right, _words_right);
    initialize();
}

Alignment::Alignment(const char* left, const char* right) 
{
    toVec(UnicodeString(left).toLower(), _words_left);
    toVec(UnicodeString(right).toLower(), _words_right);
    initialize();
}

Alignment::~Alignment() 
{
}

void Alignment::initialize() 
{ 
    return;   
}

bool Alignment::exactMatch(const UnicodeString& word_left, 
        const UnicodeString& word_right) 
{ 
    if (!word_left.compare(word_right))
        return true;
    else
        return false;
}

void Alignment::toVec(UnicodeString& line, 
        std::vector<UnicodeString>& words) 
{
    UChar space = ' ';
    int index = -1;
    int offset = 0;
    int length = 0;
    while ((index = line.indexOf(space, offset)) != -1) { 
        length = index - offset;
        if (length > 0) { 
            words.push_back(UnicodeString(line, offset, length));
        }
        offset = index + 1;
    }
    UnicodeString last(line, offset);
    if (last.length() > 0) 
        words.push_back(last);
}
