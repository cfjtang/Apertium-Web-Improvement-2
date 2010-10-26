/*
 * Copyright (C) 2009--2010 Francis M. Tyers
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

#include <stdlib.h>
#include <math.h>
#include <sstream>
#include <iostream>
#include <getopt.h>
#include <clocale>
#include <vector>
//#include <libgen.h>
#include <string.h>
#include<fstream>
#include <map>


#define VERSION "0.0.1"

using namespace std;

string         m_filePath; //! for debugging purposes.
string         m_stopWordPath;
vector<string> stopWords;
map<int,string> references;

// Prototypes

bool           load(const string &filePath);
float          rank(const string &frame);
void           usage(const char *name);
void           process(FILE *input, FILE *output);
unsigned int edit_distance(vector<string> s1, vector<string> s2);
inline string trim(const string& o); 

void usage(const char *name) 
{
        cout << basename(name) << ": rank a stream by position-independent word error rate" << endl;
        cout << "USAGE: " << basename(name) << " [-r] [-s stopword_file] ref_sentences [input_file [output_file]]" << endl;
        cout << "Options:" << endl;
#if HAVE_GETOPT_LONG
        cout << "  -r, --rank:             ranking (default behaviour)" << endl;
        cout << "  -s, --stop-words:       file containing stop words" << endl;
        cout << "  -v, --version:          version" << endl;
        cout << "  -h, --help:             show this help" << endl;
#else
        cout << "  -r:   ranking (default behaviour)" << endl;
        cout << "  -s:   file containing stop words" << endl;
        cout << "  -v:   version" << endl;
        cout << "  -h:   show this help" << endl;
#endif
        exit(EXIT_FAILURE);
}

pair<int, string> get_id_and_sentence(const string &line)
{
	pair<int, string> p;

	unsigned int id = 0;
	unsigned int plox = line.find(':');	
	string sid = trim(line.substr(1,plox));
	id = atoi(sid.c_str());
	plox = line.find(".[]");
	p = make_pair(id, trim(line.substr(plox+3)));

	return p;
}

bool load(const string &filePath) 
{
	ifstream ifs(filePath.c_str());
	string temp;

	// [173:0:3,5:8 ||	].[] He served as prime minister five times.
	while(getline(ifs, temp)) {
		pair<int, string> sent_pair = get_id_and_sentence(temp);
		references.insert(sent_pair);

		cerr << "ref[" << sent_pair.first << "] " << sent_pair.second << endl;
	}

	return true;
}

inline string trim(const string& o) 
{
	string ret = o;
	const char* chars = "\n\t\v\f\r *"; // whitespace and unknown word mark
	ret.erase(ret.find_last_not_of(chars)+1);
	ret.erase(0, ret.find_first_not_of(chars));
	return ret;
}

unsigned int edit_distance(vector<string> s1, vector<string> s2)
{
	const size_t len1 = s1.size(), len2 = s2.size();
	vector<vector<unsigned int> > d(len1 + 1, vector<unsigned int>(len2 + 1));
 
	d[0][0] = 0;
	for(unsigned int i = 1; i <= len1; ++i) {
		d[i][0] = i;
	}
	for(unsigned int i = 1; i <= len2; ++i) {
		d[0][i] = i;
	}
 
	for(unsigned int i = 1; i <= len1; ++i) {
		for(unsigned int j = 1; j <= len2; ++j) {
 
                      d[i][j] = std::min( std::min(d[i - 1][j] + 1, d[i][j - 1] + 1),
                                          d[i - 1][j - 1] + (s1[i - 1] == s2[j - 1] ? 0 : 1) );
		}
	}

	return d[len1][len2];
}

float rank(const string &frame)
{
        string buf;

	vector<string> s_unigrams;

        stringstream ss(frame); 
	float errors = 0.0;
	pair<int, string> cur_sentence = get_id_and_sentence(frame);

	int count = 0;
        while (ss >> buf) {
		if(count == 1) {
                	s_unigrams.push_back(trim(buf));
		}
		if(strstr(trim(buf).c_str(),"].[]")) {
			count = 1;
		}
        }

	vector<string> s_ref_unigrams;
	stringstream rr(references[cur_sentence.first]);
        while (rr >> buf) {
                s_ref_unigrams.push_back(trim(buf));
        }
	
	cerr << "N:REF " << s_unigrams.size() << " : " << s_ref_unigrams.size() << endl;

	if(0 == s_ref_unigrams.size()) {
		return 100.0;
	}

	errors = edit_distance(s_unigrams, s_ref_unigrams);	
	
	cerr << "PER: " << errors << endl;

        //return sprob  / num;
        return (errors / s_unigrams.size() * 100);
        
}

void load_stopwords(const string &path)
{
	ifstream ifs(path.c_str());
	string temp;

	while(getline(ifs, temp)) {
		
	}

	std::cerr << "+ " << stopWords.size() << " stop words loaded." << std::endl;
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
                        
                        float error_rate = 0.0;
               
			// eliminate stop words here 
			string orig_sentence = string(unambig);

			for (unsigned int i = 0; i < stopWords.size(); i++) {
				string::size_type pos = 0;
       				pos = unambig.find(stopWords[i] + " ", pos);
				if(pos != string::npos && pos == 0) {
					unambig.replace(pos, stopWords[i].size()+1, "");
				}
				pos = 0;
	                        while ((pos = unambig.find(" " + stopWords[i] + " ", pos)) != string::npos) {
        	                        unambig.replace(pos, stopWords[i].size()+1, "");
                	                pos++;
        	                }
			}	
         
                        error_rate = rank(unambig);
			if(error_rate != -1.0) {
				std::cerr << "unambig_nostop: " << unambig << std::endl;
				cout << error_rate << "\t||\t" << orig_sentence << endl;                               
			}
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
                {"stop-words", 0, 0, 's'},
                {"version",  0, 0, 'v'},
                {"help",     0, 0, 'h'}
        };
#endif    

        while(true) {

#if HAVE_GETOPT_LONG
                int option_index;
                int c = getopt_long(argc, argv, "rvhs:", long_options, &option_index);
#else
                int c = getopt(argc, argv, "rvhs:");
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
                        case 's':
				m_stopWordPath = string(argv[optind-1]);
				load_stopwords(m_stopWordPath);
                		break;        
                        case 'h':
                        default:
                                usage(argv[0]);
                                break;
                }
        }
	
	cerr << optind << " " << argc << endl;
        if(optind == (argc - 3)) {
                bool val = load(argv[optind]);

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
                bool val = load(argv[optind]);

                if(val == false) {
                        usage(argv[0]);
                }
    
                input = fopen(argv[optind+1], "rb");
                if(input == NULL || ferror(input)) {
                        usage(argv[0]);
                }
        } else if(optind == (argc - 1)) {
                bool val = load(argv[optind]);

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

