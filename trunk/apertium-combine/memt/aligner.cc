#include <fstream>
#include <iostream>
#include <sstream>
#include <unicode/unistr.h> 

using namespace std;

int main(int argc, char** argv) 
{ 
    char* input_file1 = argv[1];
    char* input_file2 = argv[2];
    fstream file_in1(input_file1, ios::in);
    fstream file_in2(input_file2, ios::in);
    //UnicodeString input1_line;
    //UnicodeString input2_line;
    string input1_line;
    string input2_line;
    while(file_in1 && file_in2) {
        getline(file_in1, input1_line);
        getline(file_in2, input2_line);
        cout << input1_line << " " << input2_line << endl;
    }
}
