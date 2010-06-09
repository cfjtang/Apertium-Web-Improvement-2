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

#include "ambiguate_corpus.h"
#include "bidix_parser.h"

#include <iostream>

using namespace std;

AmbiguateCorpus::AmbiguateCorpus()
{
	return;	
}

void
AmbiguateCorpus::readDict(const string dict, const string dir)
{
	// a_table = {"orð<n><nt>", ["orð:1<n><nt>", "orð:2<n><nt>", ...]}
	BidixParser p;
	
	p.parse(dict, "LR");

	cout << " " << dict << endl;
	dic_exp_file = dict;	
}


AmbiguateCorpus::~AmbiguateCorpus()
{
	return;	
}

void
AmbiguateCorpus::processCorpus(FILE *input, FILE *output)
{
	return;
}
