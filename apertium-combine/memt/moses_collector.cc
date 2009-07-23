#include <iostream>
#include <cwchar>
#include <string>
#include <stack>

#include "Parameter.h"
#include "Manager.h"
#include "Hypothesis.h"
#include "StaticData.h"
#include "InputType.h"

#include "utf_converter.h"
#include "moses_collector.hh"

using namespace std;
using namespace Moses;

MosesCollector::MosesCollector(const string &filePath)
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
//	delete parameter;
}

void
MosesCollector::collect(FILE *input, FILE *output)
{
	wstring buf = L"";
	int seen = 0;

	while(wchar_t val = static_cast<wchar_t>(fgetwc_unlocked(input))) {

		if(feof(input)) {
			return;
		}	

		if(seen == 0 && val == L'[') {
			seen++;
		}

		if(seen == 1 && val == L']') {
			seen++;
		}

		buf = buf + val;

		if(seen == 2) {
			fputws_unlocked(L".[]", output); // This is a hack because I can't work out how to get .[] output.
			unsigned int len = buf.length() - 1;
			if(buf[0] == L'[' && buf[len] == ']') {
				fputws_unlocked(buf.c_str(), output);
			} else {
				wstring *translation = translate(&buf);
				fputws_unlocked(translation->c_str(), output);
			}
			seen = 0;
			buf = L"";
		}
	}
	
	return;
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

	string in = UtfConverter::toUtf8(*wsblock);

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
