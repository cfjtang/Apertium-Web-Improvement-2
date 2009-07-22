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

	
public:
	Parameter *parameter;

        MosesCollector(const string &filePath);
        ~MosesCollector();

	void collect(FILE *input, FILE *output);
	wstring* translate(wstring *block);

};

#endif /* __MOSES_COLLECTOR_H__ */
