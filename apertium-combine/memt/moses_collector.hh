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

using namespace std;

class MosesCollector :: Collector {
	
public:

        Collector();
        ~Collector();

	wchar_t* translate(wchar_t *block);

}
