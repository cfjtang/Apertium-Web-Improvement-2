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

#ifndef __AMBIGUATE_CORPUS_H__
#define __AMBIGUATE_CORPUS_H__

#include <stdio.h>
#include <string>
#include <map>
#include <vector>

using namespace std;

class AmbiguateCorpus
{
private:
	string dic_exp_file;  // File with the ambiguous parts of the expanded dictionary
	map<string, vector<string> > a_table;

public:
	AmbiguateCorpus();
	AmbiguateCorpus(const string dict);
	virtual ~AmbiguateCorpus();
	
	int ambiguate(FILE *input, FILE *output);
};


#endif /* __AMBIGUATE_CORPUS_H__ */
