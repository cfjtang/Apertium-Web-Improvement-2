/*
 * Copyright (C) 2009 Apertium project
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
#include <cwchar>
#include <string>
#include <getopt.h>
#include <libgen.h>

#include "moses_collector.hh"

using namespace std;

void
usage(char *name) 
{
	cout << basename(name) << ": process an apertium stream with another MT system" << endl;
	cout << "USAGE: " << basename(name) << " " << endl;

	exit(EXIT_FAILURE);
}

int 
main(int argc, char **argv) 
{
	FILE *input = stdin;
	FILE *output = stdout;		
	FILE *buffer = 0;

	if(argc < 1) {
		usage(argv[0]);
	}

	MosesCollector c(argc, argv, buffer);
        buffer = fopen("/tmp/moses.out", "w+");

	if(setlocale(LC_CTYPE, "") == NULL) {
		wcerr << L"Warning: unsupported locale, fallback to \"C\"" << endl;
		setlocale(LC_ALL, "C");
	}

	c.collect(input, output, buffer);

	return 0;
}
