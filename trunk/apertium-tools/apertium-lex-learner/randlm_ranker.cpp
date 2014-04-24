#include <stdlib.h>
#include <sstream>
#include <iostream>
#include <getopt.h>
#include <clocale>
#include <vector>

#include "RandLMParams.h"
#include "RandLMQuery.h"

using namespace std;

#define VERSION "0.0.1"

randlm::RandLM* randlm_;
randlm::Vocab* vocab_;

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
        string m_filePath = strdup(filePath.c_str());
	randlm::RandLMFile fin(m_filePath, ios::in);
	randlm::RandLMInfo* info = new randlm::RandLMInfo(&fin);

	randlm_ = randlm::RandLM::initRandLM(info, &fin, 0);
	vocab_ = randlm_->getVocab();
	
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
	unsigned int num = 0;
	float prob = 0.0;
	float sprob = 0.0;
	int ret = 0;

        while (ss >> buf) {
                s_unigrams.push_back(trim(buf));
        }

        for(unsigned int i = 2; i < (s_unigrams.size()); i++) {
		buf = s_unigrams.at(i - 2) + " " + s_unigrams.at(i - 1) + " " + s_unigrams.at(i);

		randlm::WordID w = vocab_->getWordID(buf);
		prob = randlm_->getProb(&w, 3, &ret);
		sprob += prob;
		cerr << "_3(" << ret << "): " << buf << " " << prob << endl;
		num++;
        }
        return prob  / num;
        
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

	randlm::RandLMParams::init();

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

