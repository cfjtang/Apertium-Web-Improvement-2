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
  cerr<<name<<" --chunks|-c chunks_file -totalcount|-t num [--gzip|-z]\n";
  cerr<<"Note: chunks_file is supposed to be the output of sort-uniq-chunks.\n";
  cerr<<"      totalcount cam be obtain with total-count-chunks.\n";
  cerr<<"Warning: Write scored chunks to the standard output.\n";
}

int main(int argc, char* argv[]) {
  int c;
  int option_index=0;

  string chunks_file="";
  bool use_zlib=false;
  int total_count=0;

  while (true) {
    static struct option long_options[] =
      {
	{"chunks",           required_argument,  0, 'c'},
	{"totalcount",       required_argument,  0, 't'},
	{"gzip",                   no_argument,  0, 'z'},
	{"help",                   no_argument,  0, 'h'},
	{"version",                no_argument,  0, 'v'},
	{0, 0, 0, 0}
      };

    c=getopt_long(argc, argv, "c:t:zhv",long_options, &option_index);
    if (c==-1)
      break;
      
    switch (c) {
    case 'c':
      chunks_file=optarg;
      break;
    case 't':
      total_count=atoi(optarg);
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

  if ((chunks_file=="")||(total_count<=0)) {
    cerr<<"Error: Wrong number of parameters\n";
    help(argv[0]);
    exit(EXIT_FAILURE);
  }

  cerr<<"Chunks file: '"<<chunks_file<<"'\n";
  cerr<<"Total count: "<<total_count<<"\n";

  istream *fchunks;

  if (use_zlib)
    fchunks = new gzifstream(chunks_file.c_str());
  else
    fchunks = new ifstream(chunks_file.c_str());

  if (fchunks->fail()) {
    cerr<<"Error: Cannot open input file '"<<chunks_file<<"'\n";
    exit(EXIT_FAILURE);
  }
  
  int nchunks=0;
  vector<Alignment> auxset;
  string source_str="";
  while (!fchunks->eof()) {
    string str_chunk;
    getline(*fchunks, str_chunk);

    if (str_chunk.length()>0) {
      Alignment chunk(str_chunk);
      nchunks++;
      if (source_str != chunk.get_source_str()) {
        if (auxset.size() > 0) {
          Alignment::score_alignments(auxset, total_count);

          for(unsigned i=0; i<auxset.size(); i++)
            cout<<auxset[i]<<"\n";

          auxset.clear();
        }

        source_str=chunk.get_source_str();
        auxset.push_back(chunk);
      } else
        auxset.push_back(chunk);
    }
  }

  if (auxset.size() > 0) {
    Alignment::score_alignments(auxset, total_count);

    for(unsigned i=0; i<auxset.size(); i++)
      cout<<auxset[i]<<"\n";

    auxset.clear();
  }

  cerr<<"# chunks read: "<<nchunks<<"\n";

  delete fchunks;
}
