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
        std::vector<string> _words_left;
        std::vector<string> _words_right;
        std::vector<int> _final_alignment; // result of align()
        int _consecutive_alignments;
        int _total_matches;
        Alignment(string& left, string& right);
        Alignment(const char* left, const char* right);
        ~Alignment();
        void initialize();
        void align();
    private:
        void toVec(string& line, std::vector<string>& words);
        void toLower(string& to_lower);
        int exactMatch(const string& left, 
                const string& right);
        int caseInsensitiveMatch(const string& left, 
                const string& right);
};
