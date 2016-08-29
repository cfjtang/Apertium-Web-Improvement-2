#include <fstream>
#include <string>

#include <apertium/perceptron_tagger.h>
#include <apertium/shell_utils.h>
#include <apertium/stream_tagger.h>

using namespace Apertium;
using namespace Apertium::ShellUtils;

int main(int argc, char* argv[]) {
  if (argc == 3 && std::string(argv[1]) == "model") {
    // Dumps a tagger model
    basic_Tagger::Flags flags;
    PerceptronTagger pt(flags);
    std::ifstream tagger_model;
    try_open_fstream("MODEL", argv[2], tagger_model);
    pt.deserialise(tagger_model);
    std::wcout << pt;
  } else if (argc == 4 && std::string(argv[1]) == "path") {
    // Dumps features generated by every wordoid in every path using a correctly tagged path
    // Doesn't use a model

  } else if (argc >= 2 && std::string(argv[1]) == "tag") {
    // Runs tagging tracing every step of the process

  } else {
    // Print help
  }
}
