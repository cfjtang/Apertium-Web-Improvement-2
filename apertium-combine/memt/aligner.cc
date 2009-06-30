#include <fstream>
#include <iostream>
#include <sstream>
// TODO #include <unicode/unistr.h> 
#include "alignment.hh"

using namespace std;

int main(int argc, char** argv) 
{ 
    char* input_file1 = argv[1];
    char* input_file2 = argv[2];
    fstream file_in1(input_file1, ios::in);
    fstream file_in2(input_file2, ios::in);
    string input_line1;
    string input_line2;
    cout << "Reading from " << input_file1 << " and " << input_file2 << endl;
    while(file_in1 && file_in2) {
        getline(file_in1, input_line1);
        getline(file_in2, input_line2);
        Alignment a = Alignment(input_line1, input_line2);
        a.align();
        //cout << a.print_result() << endl;
    }
}
