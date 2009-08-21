// including art
#include <vector>
#include <fstream>
#include <iostream>
#include "alignment.hh"
#include "hypotheses.hh"
#include "irstlm_ranker.hh"
#include "max_conseq_aligner.hh"
#include "naive_beam_generator.hh"
#include "case_insensitive_matcher.hh"

using namespace std;

void usage() 
{
    wcout << 
        "./combiner first_input second_input [third_input [fourth input [...]]]" 
        << endl;
}

int main(int argc, char** argv) 
{ 
    std::vector<char*> input_files;
    unsigned int k = 1;
    while (argv[k] != NULL) {
        input_files.push_back(argv[k]);
        ++k;
    }
    if (input_files[1] == NULL) {
        usage();
        return -1;
    }
    std::vector<wfstream*> files_in;
    cout << "Reading from";
    for (std::vector<char*>::iterator it = input_files.begin(); 
            it != input_files.end(); ++it) {
        wfstream* file_in = new wfstream(*it);
        files_in.push_back(file_in);
        cout << " " << *it;
    }
    cout << endl;
    std::vector<wstring> input_lines;
    for (std::vector<wfstream*>::iterator it = files_in.begin();
            it != files_in.end(); ++it) {
        wstring tmp;
        getline(**it, tmp);
        input_lines.push_back(tmp);
    }

    bool condition = true;
    for (std::vector<wfstream*>::iterator it = files_in.begin();
            it != files_in.end(); ++it) {
        if (!**it) { 
            condition = false;
            break;
        }
    }
    
    /// You can instantiate another derivation of Ranker here, 
    /// that should be the only change for switching the ranker
    IRSTLMRanker* r = new IRSTLMRanker("/Users/snippy/apertium/EN-LM");

    while(condition) {
        wcout << endl;
#ifdef DEBUG
        wcout << "### Align: " << input_lines[0];
        for (unsigned int i = 1; i < input_lines.size(); ++i)
            wcout << " <with> " << input_lines[i];
        wcout << endl;
#endif
        Alignment alignment = Alignment(input_lines);
        /// This is where you can change the Matcher
        Case_Insensitive_Matcher matcher;
        alignment.match(matcher);
        /// This is where you can change the Aligner
        Max_Conseq_Aligner aligner;
        alignment.align(aligner, 5);
#ifdef DEBUG
        alignment.print();
        alignment.generate_graphviz();
#endif
        wcout << "### Making hypotheses" << endl;
        /// This is where you can change the (hyp) Generator
        Naive_Beam_Generator generator;
        Hypotheses hypotheses = Hypotheses(alignment, generator);
        wcout << "### Ranking hypotheses" << endl;
        hypotheses.rank(r);
#ifdef DEBUG
        hypotheses.print();
#else
        hypotheses.best();
#endif
        // Example of how to print the hypotheses in a file:
        // wfstream hypotheses_file("input_ranker.txt", ios::out);
        // h.print(&hypotheses_file);
        for (unsigned int i = 0; i < files_in.size(); ++i) {
            getline(*files_in[i], input_lines[i]);
        }
        for (std::vector<wfstream*>::iterator it = files_in.begin();
                it != files_in.end(); ++it) {
            if (!**it) { 
                condition = false;
                break;
            }
        }
    }
    for (std::vector<wfstream*>::iterator it = files_in.begin();
            it != files_in.end(); ++it) 
        delete *it;
    return 0;
}
