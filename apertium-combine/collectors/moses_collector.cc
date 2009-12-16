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
#include <stack>
#include <pcre.h> // For tokenisation

#include "Parameter.h"
#include "Manager.h"
#include "Hypothesis.h"
#include "StaticData.h"
#include "InputType.h"

#include "apertium/apertium_re.h" // Also tokenisation
#include "apertium/utf_converter.h"
#include "moses_collector.hh"

using namespace std;
using namespace Moses;

MosesCollector::MosesCollector(int argc, char **argv, FILE *buffer)
{
	parameter = new Moses::Parameter();

        if (!parameter->LoadParam(filePath))
        {
                parameter->Explain();
                delete parameter;
		wcerr << "Failed to load parameters" << endl;
        }

        const StaticData &staticData = StaticData::Instance();
        if (!StaticData::LoadDataStatic(parameter)) {
		wcerr << "Failed to load static data" << endl;
	}

        vector<float> weights = staticData.GetAllWeights();
}

MosesCollector::~MosesCollector()
{
	if (parameter) {
		delete parameter;
	}
}

/*
 *	This function replaces tokenizer.perl in the Moses scripts
 */

void
MosesCollector::tokenise(string &sentence)
{
	ApertiumRE my_re;
	
	// This doesn't work yet 
	my_re.compile("[']");
	my_re.replace(sentence, " '");
/*
	my_re.compile("([^[[:alnum:]]\\s\\.\\'\\`\\,\\-])");
	my_re.replace(sentence, " $1 ");

	my_re.compile("([^\\p{N}])[,]([^\\p{N}])");
	my_re.replace(sentence, "$1 , $2");
	my_re.compile("([\\p{N}])[,]([^\\p{N}])");
	my_re.replace(sentence, "$1 , $2");
	my_re.compile("([\\p{N}])[,]([^\\p{N}])");
	my_re.replace(sentence, "$1 , $2");

	//my_re.compile("([^\\p{IsAlpha}])[']([^\\p{IsAlpha}])");
	my_re.compile("([^[[:alpha:]]])['](^[[:alpha:]]])");
	my_re.replace(sentence, "$1 ' $2");
	my_re.compile("([^\\p{IsAlpha}\\p{IsN}])[']([\\p{IsAlpha}])");
	my_re.replace(sentence, "$1 ' $2");
	my_re.compile("([\\p{IsAlpha}])[']([^\\p{IsAlpha}])");
	my_re.replace(sentence, "$1 ' $2");
	//my_re.compile("([\\p{IsAlpha}])[']([\\p{IsAlpha}])");
	my_re.compile("([[:alpha:]])[']([[:alpha:]])");
	my_re.replace(sentence, "$1 '$2");
*/

	return;
}

void
MosesCollector::setPath(string file)
{
	filePath = file;
}

wstring*
MosesCollector::translate(const wstring *wsblock) /* Best code ever */
{
	wstring *final_translation = new wstring();

	const vector<string> &inputFactorVector = parameter->GetParam("input-factors");
        std::vector<Moses::FactorType>    m_inputFactorOrder;

        for(size_t i=0; i<inputFactorVector.size(); i++) {
                m_inputFactorOrder.push_back(Scan<FactorType>(inputFactorVector[i]));
        }

        if(m_inputFactorOrder.empty()) {
                wcout << L"no input factor specified in config file";
        }

        const StaticData &staticData = StaticData::Instance();

	wstring twsblock(*wsblock);
	transform(twsblock.begin(), twsblock.end(), twsblock.begin(),towlower); 
	string in = UtfConverter::toUtf8(twsblock);
	tokenise(in);

        Sentence *s = new Sentence(Input);
	s->CreateFromString(m_inputFactorOrder , in, string(" "));

        Manager manager(*s, staticData.GetSearchAlgorithm());
        manager.ProcessSentence();

	const Hypothesis *hypo  = manager.GetBestHypothesis();
	std::stack<wstring> bounce;
	hypo = hypo->GetPrevHypo();
	if(hypo!=NULL){
		while(hypo != NULL) {
			string sout = hypo->GetTargetPhraseStringRep();
		
			wstring wsout = UtfConverter::fromUtf8(sout);
	
			bounce.push(wsout);
			hypo = hypo->GetPrevHypo();
		} 
	} else {
		wstring *val = new wstring(wsblock->c_str());
		return val; // if there is no decent translation return the original.
	}

	while(!bounce.empty()) {
		
		final_translation->append(bounce.top());
		bounce.pop();
	}

	return final_translation;
}
