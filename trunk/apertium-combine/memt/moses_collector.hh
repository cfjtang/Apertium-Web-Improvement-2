#ifndef __MOSES_COLLECTOR_H__
#define __MOSES_COLLECTOR_H__
#include <iostream>
#include <cwchar>
#include <string>

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

class MosesCollector : public Collector {
	
public:

        MosesCollector();
        ~MosesCollector();

	wchar_t* translate(wchar_t *block);

};

#endif /* __MOSES_COLLECTOR_H__ */
