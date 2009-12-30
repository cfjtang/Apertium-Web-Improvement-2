/*
 *
 *
 *  This is mostly mangled from the Moses LanguageModelIRST code, the lt_proc code
 *
 */

#include <stdlib.h>
#include <sstream>
#include <iostream>
#include <getopt.h>
#include <clocale>
#include <vector>

#include "lmtable.h"
#include "lmmacro.h"

#define VERSION "0.0.1"

class lmtable;  // irst lm table
class lmmacro;  // irst lm for macro tags

using namespace std;

lmtable        *m_lmtb;

int            m_unknownId;
int            m_lmtb_size;          // max ngram stored in the table
int            m_lmtb_dub;           // dictionary upperbound

float          m_weight; //! scoring weight.
string         m_filePath; //! for debugging purposes.
size_t         m_nGramOrder; //! max n-gram length contained in this LM.

// Prototypes

bool           load(const string &filePath, float weight, size_t nGramOrder);
float          rank(const string &frame);
void           usage(const char *name);
void           process(FILE *input, FILE *output);

void usage(const char *name) 
{
        cout << basename(name) << ": rank a stream with a language model" << endl;
        cout << "USAGE: " << basename(name) << " [-r] lm_file [input_file [output_file]]" << endl;
        cout << "Options:" << endl;
#if HAVE_GETOPT_LONG
        cout << "  -r, --rank:             ranking (default behaviour)" << endl;
        cout << "  -v, --version:          version" << endl;
        cout << "  -h, --help:             show this help" << endl;
#else
        cout << "  -r:   ranking (default behaviour)" << endl;
        cout << "  -v:   version" << endl;
        cout << "  -h:   show this help" << endl;
#endif
        exit(EXIT_FAILURE);
}

bool load(const string &filePath, float weight, size_t nGramOrder)
{
        m_weight       = weight;
        m_nGramOrder   = nGramOrder;

        // get name of LM file and, if any, of the micro-macro map file
        m_filePath = strdup(filePath.c_str());

        // Open the input file (possibly gzipped)
        std::streambuf *m_streambuf;
        std::filebuf* fb = new std::filebuf();
        fb->open(filePath.c_str(), std::ios::in);
        m_streambuf = fb;
        std::istream inp(m_streambuf);

        // case (standard) LMfile only: create an object of lmtable

        m_lmtb  = (lmtable *)new lmtable;
        if (m_filePath.compare(m_filePath.size()-3,3,".mm")==0) {
                m_lmtb->load(inp, m_filePath.c_str(), NULL, 1);
        } else {
                m_lmtb->load(inp, m_filePath.c_str(), NULL, 0);
        }

        m_lmtb_size = m_lmtb->maxlevel();       

        // LM can be ok, just outputs warnings

        // Mauro: in the original, the following two instructions are wrongly switched:
        m_unknownId = m_lmtb->getDict()->oovcode(); // at the level of micro tags

        cerr << "IRST: m_unknownId=" << m_unknownId << std::endl;

        //install caches
        m_lmtb->init_probcache();
        m_lmtb->init_statecache();
        m_lmtb->init_lmtcaches(m_lmtb->maxlevel() > 2 ? (m_lmtb->maxlevel() - 1) : 2);

        if (m_lmtb_dub > 0) {
                m_lmtb->setlogOOVpenalty(m_lmtb_dub);
        }

        return true;
}
inline string trim(const string& o) 
{
	string ret = o;
	const char* chars = "\n\t\v\f\r ";
	ret.erase(ret.find_last_not_of(chars)+1);
	ret.erase(0, ret.find_first_not_of(chars));
	return ret;
}


float rank(const string &frame)
{
        string buf;

	vector<string> s_unigrams;

        stringstream ss(frame); 
        ngram* m_lmtb_ng;
        int lmId = 0;                   
	float num = 0.0f;
	float prob = 0.0f;
	float sprob = 0.0f;

        m_lmtb_ng = new ngram(m_lmtb->getDict()); // ngram of words/micro tags
	m_lmtb_ng->size = 0;

        while (ss >> buf) {
                s_unigrams.push_back(trim(buf));

/*
        	m_lmtb_ng = new ngram(m_lmtb->getDict()); // ngram of words/micro tags
                lmId = m_lmtb->getDict()->encode(buf.c_str()); 
                m_lmtb_ng->pushc(lmId);
		prob = m_lmtb->clprob(*m_lmtb_ng);
		sprob += prob;
		cerr << "_1: " << buf << " " << prob << endl;
		delete m_lmtb_ng;
		num++;
*/
        }

        for(unsigned int i = 1; i < (s_unigrams.size()); i++) {
		buf = s_unigrams.at(i - 1) + " " + s_unigrams.at(i);

/*
        	m_lmtb_ng = new ngram(m_lmtb->getDict()); // ngram of words/micro tags
                lmId = m_lmtb->getDict()->encode(s_unigrams.at(i - 1).c_str()); 
                m_lmtb_ng->pushc(lmId);
                lmId = m_lmtb->getDict()->encode(s_unigrams.at(i).c_str()); 
                m_lmtb_ng->pushc(lmId);
		prob = m_lmtb->clprob(*m_lmtb_ng);
		sprob += prob;
		cerr << "_2: " << buf << " " << prob << endl;
		delete m_lmtb_ng;
		num++;
*/
        }

        for(unsigned int i = 2; i < (s_unigrams.size()); i++) {
		buf = s_unigrams.at(i - 2) + " " + s_unigrams.at(i - 1) + " " + s_unigrams.at(i);

        	m_lmtb_ng = new ngram(m_lmtb->getDict()); // ngram of words/micro tags
                lmId = m_lmtb->getDict()->encode(s_unigrams.at(i - 2).c_str()); 
                m_lmtb_ng->pushc(lmId);
                lmId = m_lmtb->getDict()->encode(s_unigrams.at(i - 1).c_str()); 
                m_lmtb_ng->pushc(lmId);
                lmId = m_lmtb->getDict()->encode(s_unigrams.at(i).c_str()); 
                m_lmtb_ng->pushc(lmId);
		prob = m_lmtb->clprob(*m_lmtb_ng);
		sprob += prob;
		cerr << "_3: " << buf << " " << prob << endl;
		delete m_lmtb_ng;
		num++;
        }
        cerr << num << endl; 
        return sprob  / num;
        
}

void process(FILE *input, FILE *output)
{
        char c = 0;
        int frame = 0;
        int count = 1;
        string unambig = "";
        string out = "";
        vector<string> ambiguous;
        
        c = fgetc(input);
        while(!feof(input)) {
                if(c == '\n') {
                        //Compute propability using LM 
                        
                        float max_value = 0.0;
                        int max_ind = 0;
                        float log_prob = 0.0;
                        
                        log_prob = rank(unambig);
			// same fiddle used by Moses 
                        //cout << exp(log_prob * 2.30258509299405f)  << "\t||\t" << unambig << endl;                               
                        cout << log_prob << "\t||\t" << unambig << endl;                               
                        unambig = "";

                } else {
                        if(c != 0xff) {
                                unambig = unambig + c;
                        }                       
                } 
                
                c = fgetc(input);               
        }
}

int main(int argc, char **argv) 
{
        FILE *input = stdin, *output = stdout;
        int cmd = 0;

        if(setlocale(LC_CTYPE, "") == NULL) {
                wcerr << L"Warning: unsupported locale, fallback to \"C\"" << endl;
                setlocale(LC_ALL, "C");
        }

#if HAVE_GETOPT_LONG
        static struct option long_options[]= {
                {"rank", 0, 0, 'r'},
                {"version",  0, 0, 'v'},
                {"help",     0, 0, 'h'}
        };
#endif    

        while(true) {

#if HAVE_GETOPT_LONG
                int option_index;
                int c = getopt_long(argc, argv, "rvh", long_options, &option_index);
#else
                int c = getopt(argc, argv, "rvh");
#endif    
                if(c == -1) {
                        break;
                }

                switch(c) {
                        case 'r':
                
                        case 'v':
                                cout << basename(argv[0]) << " version " << VERSION << endl;
                                exit(EXIT_SUCCESS);
                                break;
                        
                        case 'h':
                        default:
                                usage(argv[0]);
                                break;
                }
        }

        if(optind == (argc - 3)) {
                bool val = load(argv[optind], 1.0, 3);

                if(val == false) {
                        usage(argv[0]);
                }
    
                input = fopen(argv[optind+1], "rb");
                if(input == NULL || ferror(input)) {
                        usage(argv[0]);
                }
    
                output = fopen(argv[optind+2], "wb");

                if(output == NULL || ferror(output)) {
                        usage(argv[0]);
                }

        } else if(optind == (argc -2)) { 
                bool val = load(argv[optind], 1.0, 3);

                if(val == false) {
                        usage(argv[0]);
                }
    
                input = fopen(argv[optind+1], "rb");
                if(input == NULL || ferror(input)) {
                        usage(argv[0]);
                }
        } else if(optind == (argc - 1)) {
                bool val = load(argv[optind], 1.0, 3);

                if(val == false) {
                        usage(argv[0]);
                }
        } else {
                usage(argv[0]);
        }

        try {

                switch(cmd) {
                        case 'r':
                        default:
                                break;
                }

        } catch(exception& e) {
                cerr << e.what();
                exit(EXIT_FAILURE);
        }

        process(input, output);

        fclose(input);
        fclose(output); 
        return EXIT_SUCCESS;

        return 0;
}

