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
#include "language_model.h"
#include "zfstream.h"


using namespace std;

void help(char *name) {
  cerr<<"USAGE:\n";
  cerr<<name<<" --chunks|-c chunks_file --langmodel|-l langmode_file [--debug|-d] [--gzip|-z]\n";
  cerr<<"Note: chunks_file is supposed to be the same provided to translate-detect-chunks.\n";
  cerr<<"      --gzip only affects chunks_file.\n";
}

int main(int argc, char* argv[]) {
  cerr<<"LOCALE: "<<setlocale(LC_ALL,"")<<"\n";

  int c;
  int option_index=0;

  string chunks_file="";
  string langmodel_file="";
  bool use_zlib=false;

  while (true) {
    static struct option long_options[] =
      {
	{"chunks",   required_argument,  0, 'c'},
	{"langmodel",required_argument,  0, 'l'},
	{"gzip",           no_argument,  0, 'z'},
        {"debug",          no_argument,  0, 'd'},
	{"help",           no_argument,  0, 'h'},
	{"version",        no_argument,  0, 'v'},
	{0, 0, 0, 0}
      };

    c=getopt_long(argc, argv, "c:l:zdhv",long_options, &option_index);
    if (c==-1)
      break;
      
    switch (c) {
    case 'c':
      chunks_file=optarg;
      break;
    case 'l':
      langmodel_file=optarg;
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

  if ((chunks_file=="") || (langmodel_file=="")) {
    cerr<<"Error: Wrong number of parameters\n";
    help(argv[0]);
    exit(EXIT_FAILURE);
  }

  cerr<<"Chunks file:  '"<<chunks_file<<"'\n";
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

  cerr<<"Loading language model ... \n";
  LanguageModel langmodel;
  langmodel.load(langmodel_file);

  cerr<<"Scoring and replacing chunks ... \n";
  translator.score_replace_chunks(langmodel, cin);

  cerr<<"Statistics ...\n";
  translator.print_stats();

  cerr<<"\n";
}
