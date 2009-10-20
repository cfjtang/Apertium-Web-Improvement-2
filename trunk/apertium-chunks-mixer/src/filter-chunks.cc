/*
 * Copyright (C) 2009 Universitat d'Alacant / Universidad de Alicante
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
 
#include <iostream>
#include <fstream>
#include <string>
#include <getopt.h>
#include <ctime>
#include <clocale>

#include "configure.h"
#include "alignment.h"
#include "zfstream.h"

using namespace std;

void help(char *name) {
  cerr<<"USAGE:\n";
  cerr<<name<<" --chunks|-c chunks_file --slmarker|-s sl_marker_words --tlmarker|-t tl_marker_words  [--count|-n num] [--check-open-words|-o]  [--ratio|-r ratio] [--fstrings|-f forbidden_string] [--check-words|-w] [--gzip|-z]\n";
  cerr<<"Note: chunks_file is supposed to be the output of score-chunks.\n";
  cerr<<"      --gzip only affects chunks_file.\n";
  cerr<<"Warning: Write filtered chunks to the standard output.\n";
}

int main(int argc, char* argv[]) {
  int c;
  int option_index=0;

  string chunks_file="";
  string slmarker_file="";
  string tlmarker_file="";
  string forbiddenstrings_file="";

  double ratio=100;
  int count=0;

  bool use_zlib=false;
  bool check_open_words=false;
  bool check_words=false;

  while (true) {
    static struct option long_options[] =
      {
	{"chunks",           required_argument,  0, 'c'},
	{"slmarker",         required_argument,  0, 's'},
	{"tlmarker",         required_argument,  0, 't'},
	{"ratio",            required_argument,  0, 'r'},
	{"count",            required_argument,  0, 'n'},
	{"fstrings",         required_argument,  0, 'f'},
        {"check-open-words",       no_argument,  0, 'o'},
        {"check-words",            no_argument,  0, 'w'},
	{"gzip",                   no_argument,  0, 'z'},
	{"help",                   no_argument,  0, 'h'},
	{"version",                no_argument,  0, 'v'},
	{0, 0, 0, 0}
      };

    c=getopt_long(argc, argv, "c:s:t:r:n:f:owzhv",long_options, &option_index);
    if (c==-1)
      break;
      
    switch (c) {
    case 'c':
      chunks_file=optarg;
      break;
    case 's':
      slmarker_file=optarg;
      break;
    case 't':
      tlmarker_file=optarg;
      break;
    case 'r':
      ratio=atof(optarg);
      break;
    case 'n':
      count=atoi(optarg);
      break;
    case 'f':
      forbiddenstrings_file=optarg;
      break;
    case 'o':
      check_open_words=true;
      break;
    case 'w':
      check_words=true;
      break;
    case 'z':
      use_zlib=true;
      break;
    case 'h': 
      help(argv[0]);
      exit(EXIT_SUCCESS);
      break;
    case 'v':
      cerr<<PACKAGE_STRING<<"\n";
      exit(EXIT_SUCCESS);
      break;    
    default:
      help(argv[0]);
      exit(EXIT_FAILURE);
      break;
    }
  }

  if ((chunks_file=="") || (slmarker_file=="") || (tlmarker_file=="")) {
    cerr<<"Error: Wrong number of parameters\n";
    help(argv[0]);
    exit(EXIT_FAILURE);
  }

  cerr<<"LOCALE: "<<setlocale(LC_ALL,"")<<"\n";

  cerr<<"Chunks file:                     '"<<chunks_file<<"'\n";
  cerr<<"SL marker words file:            '"<<slmarker_file<<"'\n";
  cerr<<"TL marker words file:            '"<<tlmarker_file<<"'\n";
  cerr<<"Check open words?:                "<<check_open_words<<"\n";
  cerr<<"Check that all tokens are words?: "<<check_words<<"\n";
  cerr<<"Max ratio:                        "<<ratio<<"\n";
  cerr<<"Min count:                        "<<count<<"\n";
  cerr<<"Forbidden strings file: '         "<<forbiddenstrings_file<<"'\n";
  cerr<<"\n";

  Alignment::init_stats();

  istream *fchunks;

  if (use_zlib)
    fchunks = new gzifstream(chunks_file.c_str());
  else
    fchunks = new ifstream(chunks_file.c_str());

  if (fchunks->fail()) {
    cerr<<"Error: Cannot open input file '"<<chunks_file<<"'\n";
    exit(EXIT_FAILURE);
  }
  
  ifstream fslmarker(slmarker_file.c_str());
  if (fslmarker.fail()) {
    cerr<<"Error: Cannot open input file '"<<slmarker_file<<"'\n";
    delete fchunks;
    exit(EXIT_FAILURE);
  }

  ifstream ftlmarker(tlmarker_file.c_str());
  if (ftlmarker.fail()) {
    cerr<<"Error: Cannot open input file '"<<tlmarker_file<<"'\n";
    delete fchunks;
    exit(EXIT_FAILURE);
  }

  if (forbiddenstrings_file.length()>0) {
    ifstream ffstrings(forbiddenstrings_file.c_str());
    if (ffstrings.fail()) {
      cerr<<"Error: Cannot open input file '"<<forbiddenstrings_file<<"'\n";
      delete fchunks;
      exit(EXIT_FAILURE);
    }

    Alignment::read_forbidden_strings(ffstrings);
    ffstrings.close();
  }

  Alignment::read_source_marker_words(fslmarker);
  Alignment::read_target_marker_words(ftlmarker);

  fslmarker.close();
  ftlmarker.close();

  int nchunks=0; int ndiscard=0;
  while (!fchunks->eof()) {
    string str_chunk;
    getline(*fchunks, str_chunk);

    if (str_chunk.length()>0) {
      Alignment chunk(str_chunk);

      nchunks++;
      if (chunk.pass_filter_test(check_open_words, check_words, count, ratio))
        cout<<chunk<<"\n";
      else
        ndiscard++;
    }
  }
  delete fchunks;

  cerr<<"# chunks read:      "<<nchunks<<"\n";
  cerr<<"# chunks discarded: "<<ndiscard<<"\n";
  cerr<<"% chunks discarded: "<<(((double)ndiscard)/((double)nchunks))*100<<"%\n";
  cerr<<"\n";

  Alignment::print_stats();
}
