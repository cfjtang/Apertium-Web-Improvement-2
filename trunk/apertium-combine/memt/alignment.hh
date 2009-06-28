/*
 * A word-to-word aligner between two hypotheses:
 * - one that comes from stdin, from lt-proc -g (Apertium)
 * - one that comes from tmp buffer, from Moses
 */

#include <iostream>
#include <streambuf>
#include <vector>
#include <unicode/unistr.h>


using namespace std;
class Alignment 
{
    public: 
        std::vector<UnicodeString> _words_left;
        std::vector<UnicodeString> _words_right;
        std::vector<int> _finalAlignment;
        std::vector<int> _workingAlignment;
        Alignment(UnicodeString& left, UnicodeString& right);
        Alignment(const char* left, const char* right);
        ~Alignment();
        void initialize();
    private:
        void toVec(UnicodeString& line, 
                std::vector<UnicodeString>& words);
        bool exactMatch(const UnicodeString& word_left, 
                const UnicodeString& word_right);
};
