#ifndef TEXTCATEGORIZER_H_
#define TEXTCATEGORIZER_H_

#include <iostream>
#include <string>

class TextCategorizer {
public:
	static TextCategorizer *Instance(std::string);
	static TextCategorizer *Instance();

	std::string classify(std::string);

private:
	TextCategorizer(void*);
	virtual ~TextCategorizer();

	static TextCategorizer *instance;

	void *h;
};

#endif /* TEXTCATEGORIZER_H_ */
