// including art
#include <fstream>
#include <iostream>
#include "alignment.hh"
#include "hypotheses.hh"
#include "irstlm_ranker.hh"
#include "max_conseq_aligner.hh"
#include "naive_beam_generator.hh"
#include "case_insensitive_matcher.hh"

using namespace std;

int main(int argc, char** argv) 
{ 
    // You can instantiate another derivation of Ranker here, 
    // that should be the only change for switching the ranker
    IRSTLMRanker* r = new IRSTLMRanker("/Users/snippy/apertium/EN-LM");

    char* input_file1 = argv[1];
    char* input_file2 = argv[2];
    wfstream file_in1(input_file1); 
    wfstream file_in2(input_file2); 
    wstring input_line1;
    wstring input_line2;
    cout << "Reading from " << input_file1 << " and " << input_file2 << endl;
    // ugly hack to avoid eating the ending line
    getline(file_in1, input_line1);
    getline(file_in2, input_line2);
    while(file_in1 && file_in2) {
        wcout << endl;
        wcout << "### Align: " << input_line1 << " <with> " << input_line2 << endl;
        Alignment alignment = Alignment(input_line1, input_line2);
        Case_Insensitive_Matcher matcher;
        alignment.match(matcher);
        Max_Conseq_Aligner aligner;
        aligner.align(alignment);
#ifdef DEBUG
        alignment.print();
        alignment.generate_graphviz();
#endif
        wcout << "### Making hypotheses" << endl;
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
        /// wfstream hypotheses_file("input_ranker.txt", ios::out);
        /// h.print(&hypotheses_file);
        getline(file_in1, input_line1);
        getline(file_in2, input_line2);
    }
    return 0;
}
