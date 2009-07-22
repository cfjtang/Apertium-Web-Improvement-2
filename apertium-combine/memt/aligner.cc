#include <fstream>
#include <iostream>
#include "alignment.hh"
#include "hypotheses.hh"

using namespace std;

int main(int argc, char** argv) 
{ 
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
        Alignment a = Alignment(input_line1, input_line2);
        a.align();
#ifdef DEBUG
        a.print();
        a.generate_graphviz();
#endif
        wcout << "### Making hypotheses" << endl;
        Hypotheses_Naive_Beam h = Hypotheses_Naive_Beam(a);
        h.rank();
        h.print();
        getline(file_in1, input_line1);
        getline(file_in2, input_line2);
    }
    return 0;
}
