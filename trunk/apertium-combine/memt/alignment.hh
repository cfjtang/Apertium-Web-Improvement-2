/*
 * A word-to-word aligner between two hypotheses:
 * - one that comes from stdin, from lt-proc -g (Apertium)
 * - one that comes from tmp buffer, from Moses
 */

#include <iostream>
#include <streambuf>
#include <vector>
// TODO #include <unicode/unistr.h>

using namespace std;
class Alignment 
{
    public: 
        std::vector<wstring> _words_left;
        std::vector<wstring> _words_right;
        std::vector<int> _final_alignment; // result of align()
        int _consecutive_alignments;
        int _total_matches;
        Alignment(wstring& left, wstring& right);
        Alignment(const wchar_t* left, const wchar_t* right);
        ~Alignment();
        void initialize();
        void align();
        void print();
    private:
        void toVec(wstring& line, std::vector<wstring>& words);
        void toLower(wstring& to_lower);
        int exactMatch(const wstring& left, 
                const wstring& right);
        int caseInsensitiveMatch(const wstring& left, 
                const wstring& right);
};
