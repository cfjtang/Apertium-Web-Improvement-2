#include <iostream>
#include <cwchar>
#include <string>

#include "Parameter.h"
#include "Manager.h"
#include "Hypothesis.h"
#include "StaticData.h"
#include "InputType.h"

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
	delete parameter;
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
		buf = buf + val;

		if(seen == 0 && val == L'[') {
			seen++;
		}

		if(seen == 1 && val == L']') {
			seen++;
		}

		//fputwc_unlocked(val, output);

		if(seen == 2) {
			fputwc_unlocked(L'@', output);
			translate(buf.c_str());
			seen = 0;
			buf = L"";
		}

	}
	
	return;
}

wchar_t*
MosesCollector::translate(const wchar_t* block)
{
	wcout << L"MosesCollector: " << block << endl;

        InputType *source=0;
        size_t lineCount = 0;

	const vector<string> &inputFactorVector = parameter->GetParam("input-factors");
        std::vector<Moses::FactorType>    m_inputFactorOrder;

        for(size_t i=0; i<inputFactorVector.size(); i++)
        {
                m_inputFactorOrder.push_back(Scan<FactorType>(inputFactorVector[i]));
        }

        if(m_inputFactorOrder.empty())
        {
                wcout << L"no input factor specified in config file";
        }


        std::istream            *m_inputStream;
        const StaticData &staticData = StaticData::Instance();
        bool m_surpressSingleBestOutput = false;
        std::ostream *m_nBestStream = &std::cout;

	wstring wsblock(block);
	string *in = new string(wsblock.begin(), wsblock.end());
	in->assign(wsblock.begin(), wsblock.end());

        Sentence *s = new Sentence(Input);
	s->CreateFromString(m_inputFactorOrder , *in, string("|"));

        Manager manager(*s, staticData.GetSearchAlgorithm());
        manager.ProcessSentence();
	const Hypothesis *hypo  = manager.GetBestHypothesis();
	cout << *hypo << endl;

	return (wchar_t *)block; // Dummy function returns original
}

