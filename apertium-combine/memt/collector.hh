#ifndef __COLLECTOR_H__
#define __COLLECTOR_H__

#include <iostream>
#include <cwchar>
#include <string>

using namespace std;

class Collector {

public:
	Collector();
	~Collector();

	void collect(FILE *input, FILE *output);
	wchar_t* translate(const wchar_t* block);
};

#endif /* __COLLECTOR_H__ */
