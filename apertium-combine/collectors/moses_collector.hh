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

#ifndef __MOSES_COLLECTOR_H__
#define __MOSES_COLLECTOR_H__

#include <iostream>
#include <cwchar>
#include <string>

#include "Parameter.h"
#include "TypeDef.h"
#include "Sentence.h"
#include "FactorTypeSet.h"
#include "FactorCollection.h"
#include "Hypothesis.h"
#include "TrellisPathList.h"
#include "InputFileStream.h"
#include "InputType.h"

#include "collector.hh"

using namespace std;
using namespace Moses;

class MosesCollector : public Collector {
private:
	string filePath;
	void tokenise(string &sentence);
	
public:
	Parameter *parameter;

        MosesCollector(int argc, char **argv, FILE *buffer);
	~MosesCollector();

	wstring* translate(const wstring *block);
	void setPath(string file);
};

#endif /* __MOSES_COLLECTOR_H__ */
