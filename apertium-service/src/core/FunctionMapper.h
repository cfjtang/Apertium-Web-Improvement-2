#ifndef FUNCTIONMAPPER_H_
#define FUNCTIONMAPPER_H_

#include "ObjectBroker.h"
#include "Modes.h"

#include <boost/unordered/unordered_map.hpp>

class FunctionMapper {
public:
	FunctionMapper(ObjectBroker*);
	virtual ~FunctionMapper();

	wstring execute(Program, wstring);

private:
	ObjectBroker *objectBroker;

	enum TaskType { APERTIUM_INTERCHUNK, APERTIUM_MULTIPLE_TRANSLATIONS, APERTIUM_POSTCHUNK, APERTIUM_PRETRANSFER,
		APERTIUM_TAGGER, APERTIUM_TRANSFER, LT_PROC };

	typedef boost::unordered_map<string, TaskType> TaskMapType;
	TaskMapType task;

	//boost::mutex mutexin;
	//boost::mutex mutexmt;
	//boost::mutex mutexpo;
	//boost::mutex mutexpr;
	//boost::mutex mutexta;
	//boost::mutex mutextr;
	//boost::mutex mutexlt;
};

#endif /* FUNCTIONMAPPER_H_ */
