#ifndef FUNCTIONMAPPER_H_
#define FUNCTIONMAPPER_H_

#include "ObjectBroker.h"
#include "ModesManager.h"

#include <boost/unordered/unordered_map.hpp>

class FunctionMapper {
public:
	FunctionMapper(ObjectBroker*);
	virtual ~FunctionMapper();

	wstring execute(Program, wstring);

private:
	ObjectBroker *objectBroker;

	enum TaskType { /*DEFORMAT, REFORMAT,*/ APERTIUM_INTERCHUNK, APERTIUM_MULTIPLE_TRANSLATIONS, APERTIUM_POSTCHUNK,
		APERTIUM_PRETRANSFER, APERTIUM_TAGGER, APERTIUM_TRANSFER, LT_PROC, CG_PROC };

	typedef boost::unordered_map<string, TaskType> TaskMapType;
	TaskMapType task;
};

#endif /* FUNCTIONMAPPER_H_ */
