/*
 * Copyright (C) 2005 Universitat d'Alacant / Universidad de Alicante
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
#include <lttoolbox/compiler.h>
#include <lttoolbox/lttoolbox_config.h>

#include <cstdlib>
#include <iostream>
#include <libgen.h>
#include <string>
#include <getopt.h>

using namespace std;

void endProgram(char *name)
{
  if(name != NULL)
  {
    cout << basename(name) << " v" << PACKAGE_VERSION <<": build a letter transducer from a dictionary" << endl;
    cout << "USAGE: " << basename(name) << " [-avh] lr | rl dictionary_file output_file [acx_file]" << endl;
    cout << "  -v:     set language variant" << endl;
    cout << "  -a:     set alternative" << endl;
    cout << "Modes:" << endl;
    cout << "  lr:     left-to-right compilation" << endl;
    cout << "  rl:     right-to-left compilation" << endl;
  }
  exit(EXIT_FAILURE);
}


int main(int argc, char *argv[])
{
  Compiler c;
  
#if HAVE_GETOPT_LONG
  int option_index=0;
#endif

  while (true) {
#if HAVE_GETOPT_LONG
    static struct option long_options[] =
    {
      {"alt",      required_argument, 0, 'a'},
      {"var",      required_argument, 0, 'v'},
      {"help",     no_argument,       0, 'h'}, 
      {0, 0, 0, 0}
    };

    int cnt=getopt_long(argc, argv, "a:v:h", long_options, &option_index);
#else
    int cnt=getopt(argc, argv, "a:v:h");
#endif
    if (cnt==-1)
      break;

    switch (cnt)
    {
      case 'a':
        c.setAltValue(optarg);
        break;

      case 'v':
        c.setVariantValue(optarg);
        break;

      case 'h':
      default:
        endProgram(argv[0]);
        break;
    }
  }

  string opc;
  string infile;
  string outfile;
  string acxfile;

  switch(argc - optind + 1)
  {
    case 5:
      opc = argv[argc-4];
      infile = argv[argc-3];
      outfile = argv[argc-2];
      acxfile = argv[argc-1];
      break;

    case 4:
      opc = argv[argc-3];
      infile = argv[argc-2];
      outfile = argv[argc-1];
      break;

    default:
      endProgram(argv[0]);
      break;
  }

  if(opc == "lr")
  {
    if(acxfile != "")
    {
      c.parseACX(acxfile, Compiler::COMPILER_RESTRICTION_LR_VAL);
    }
    c.parse(infile, Compiler::COMPILER_RESTRICTION_LR_VAL);
  }
  else if(opc == "rl")
  {
    c.parse(infile, Compiler::COMPILER_RESTRICTION_RL_VAL);
  }
  else
  {
    endProgram(argv[0]);
  }

  FILE *output = fopen(outfile.c_str(), "wb");
  if(!output)
  {
    cerr << "Error: Cannot open file '" << outfile << "'." << endl;
    exit(EXIT_FAILURE);
  }
  c.write(output);
  fclose(output);
}
