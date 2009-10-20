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
#include "sentence_translator.h"
#include "zfstream.h"

using namespace std;

void help(char *name) {
  cerr<<"USAGE:\n";
  cerr<<name<<" --chunks|-c chunks_file --defscore|-s float [--pspts|-p] [--debug|-d] [--gzip|-z]\n";
  cerr<<"Note: chunks in chunks_file are supposed to have been filtered by filter-chunks.\n";
  cerr<<"      --gzip only affects chunks_file.\n";
  cerr<<"      --defscore refers to the score assigned to non-covered words\n";
  cerr<<"      --pspts forces the use of p(SL)*p(TL|SL) instead of p(SL)\n";
  cerr<<"Warning: Write translated chunks in the sentence between brackets .\n";
}

int main(int argc, char* argv[]) {
  int c;
  int option_index=0;

  string chunks_file="";
  bool use_zlib=false;
  double defscore=-1;
  bool use_pspts=false;

  while (true) {
    static struct option long_options[] =
      {
	{"defscore", required_argument,  0, 's'},
	{"chunks",   required_argument,  0, 'c'},
        {"pspts",          no_argument,  0, 'p'},
	{"gzip",           no_argument,  0, 'z'},
        {"debug",          no_argument,  0, 'd'},
	{"help",           no_argument,  0, 'h'},
	{"version",        no_argument,  0, 'v'},
	{0, 0, 0, 0}
      };

    c=getopt_long(argc, argv, "s:c:pzdhv",long_options, &option_index);
    if (c==-1)
      break;
      
    switch (c) {
    case 'c':
      chunks_file=optarg;
      break;
    case 's':
      defscore=atof(optarg);
      break;
    case 'p':
      use_pspts=true;
      break;
    case 'z':
      use_zlib=true;
      break;
    case 'd':
      SentenceTranslator::debug=true;
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

  cerr<<"LOCALE: "<<setlocale(LC_ALL,"")<<"\n";

  if ((chunks_file=="") || (defscore==-1)) {
    cerr<<"Error: Wrong number of parameters\n";
    help(argv[0]);
    exit(EXIT_FAILURE);
  }

  cerr<<"Chunks file:  '"<<chunks_file<<"'\n";
  cerr<<"Score assigned to non-covered words: "<<defscore<<"\n";
  cerr<<"Use p(SL)*p(TL|SL) instead of p(SL)? "<<use_pspts<<"\n";
  cerr<<"\n";



  istream *fchunks;

  if (use_zlib)
    fchunks = new gzifstream(chunks_file.c_str());
  else
    fchunks = new ifstream(chunks_file.c_str());

  if (fchunks->fail()) {
    cerr<<"Error: Cannot open input file '"<<chunks_file<<"'\n";
    exit(EXIT_FAILURE);
  }

  cerr<<"Loading chunks ... \n";
  SentenceTranslator translator(*fchunks);
  delete fchunks;

  translator.set_default_score(defscore);
  translator.set_use_pspts(use_pspts);

  cerr<<"Detecting chunks ... \n";
  translator.detect_chunks(cin);

  cerr<<"Statistics ...\n";
  translator.print_stats();

  cerr<<"\n";
}
