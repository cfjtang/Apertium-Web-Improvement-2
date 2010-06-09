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

#include <lttoolbox/lt_locale.h>


#include "config.h"
#include "ambiguate_corpus.h"

using namespace std;

void 
endProgram(char *name)
{
  cout << basename(name) << ": process a stream with a letter transducer" << endl;
  cout << "USAGE: " << basename(name) << " [-a bidix [input_file [output_file]]]" << endl;
  cout << "       " << basename(name) << " [-r lm [input_file [output_file]]]" << endl;
  cout << "Options:" << endl;
#if HAVE_GETOPT_LONG
  cout << "  -a, --ambiguate:        ambiguate a tagged corpus" << endl;
  cout << "  -r, --rank:             rank a set of translations" << endl;
  cout << "  -v, --version:          version" << endl;
  cout << "  -h, --help:             show this help" << endl;
#else
  cout << "  -a:   ambiguate a tagged corpus" << endl;
  cout << "  -r:   rank a set of translations" << endl;
  cout << "  -v:   version" << endl;
  cout << "  -h:   show this help" << endl;
#endif
  exit(EXIT_FAILURE);
}

int
main(int argc, char **argv) 
{
	string dict = "";
	string dir = "lr";
	AmbiguateCorpus amb;


#if HAVE_GETOPT_LONG
	static struct option long_options[]=
		{
			{"ambiguate", 1, 0, 'a'},
			{"version",   0, 0, 'v'},
			{"help",      0, 0, 'h'},
			{0, 0, 0, 0}
		};
#endif    
	while(true) {
#if HAVE_GETOPT_LONG
		int option_index = 0;
		int c = getopt_long(argc, argv, "a:vh", long_options, &option_index);
#else
		int c = getopt(argc, argv, "a:vh");
#endif

		if(c == -1)
		{
			break;
		}

		switch(c)
		{
		case 'a':
			dict = string(optarg);
			amb.readDict(dict, dir);
			break;

		case 'v':
			cout << basename(argv[0]) << " version " << PACKAGE_VERSION << endl;
			exit(EXIT_SUCCESS);
			break;

		case 'h':
		default:
			endProgram(argv[0]);
			break;
		}
	}

	FILE *input = stdin, *output = stdout;

	LtLocale::tryToSetLocale();
	
	amb.processCorpus(input, output);

	return 0;
}

