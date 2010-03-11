/*
 * Copyright (C) 2009--2010 Francis M. Tyers
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

#include <cstdlib>
#include <getopt.h>
#include <libgen.h>
#include <iostream>

#include "ambiguate_corpus.h"

using namespace std;

void 
endProgram(char *name)
{
  cout << basename(name) << ": process a stream with a letter transducer" << endl;
  cout << "USAGE: " << basename(name) << " [-a bidix [input_file [output_file]]]" << endl;
  cout << "Options:" << endl;
#if HAVE_GETOPT_LONG
  cout << "  -a, --ambiguate:        ambiguate a tagged corpus" << endl;
  cout << "  -v, --version:          version" << endl;
  cout << "  -h, --help:             show this help" << endl;
#else
  cout << "  -a:   ambiguate a tagged corpus" << endl;
  cout << "  -v:   version" << endl;
  cout << "  -h:   show this help" << endl;
#endif
  exit(EXIT_FAILURE);
}

int
main(int argc, char **argv) 
{
#if HAVE_GETOPT_LONG
  static struct option long_options[]=
    {
      {"ambiguate",       0, 0, 'a'},
      {"version",	  0, 0, 'v'},
      {"help",            0, 0, 'h'}
    };
#endif    

	AmbiguateCorpus amb("/tmp/bar");

	return 0;
}

