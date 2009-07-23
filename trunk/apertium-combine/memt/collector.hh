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

#ifndef __COLLECTOR_H__
#define __COLLECTOR_H__

#include <iostream>
#include <cwchar>
#include <string>

using namespace std;

class Collector {

public:
	Collector();
	virtual ~Collector();

	void collect(FILE *input, FILE *output);
	virtual wstring* translate(const wstring *block) = 0 ;
};

#endif /* __COLLECTOR_H__ */
