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

#ifndef __BIDIX_PARSER_H__
#define __BIDIX_PARSER_H__

#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <map>
#include <vector>

#include <libxml/xmlmemory.h>
#include <libxml/parser.h>

#include <lttoolbox/lt_locale.h>

#include <apertium/apertium_re.h>

using namespace std;

class BidixParser
{
private:
	xmlSAXHandler handler;
	wstring direction;

/**
sorti<n><m> (0, u'sorti:0<n><m>', u'darkness<n>')
sorti<n><m> (1, u'sorti:1<n><m>', u'thick fog<n>')
sorti<n><m> (2, u'sorti:2<n><m>', u'heavy snowstorm<n>')
*/

	map<ApertiumRE, map<unsigned int, wstring, wstring> > a_table;

public:

	BidixParser();
	void parse(const string dict, const string dir);
	virtual ~BidixParser();
	
	map<ApertiumRE, vector<string> > get_ambiguity_table(void);
};


#endif /* __BIDIX_PARSER_H__ */
