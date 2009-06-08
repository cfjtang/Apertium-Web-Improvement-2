#ifndef TEXTCLASSIFIER_H_
#define TEXTCLASSIFIER_H_

#include <iostream>
#include <string>

class TextClassifier {
public:
	static TextClassifier *Instance(std::string);
	static TextClassifier *Instance();

	std::string classify(std::string);

private:
	TextClassifier(void*);
	virtual ~TextClassifier();

	static TextClassifier *instance;

	void *h;
};

#endif /* TEXTCLASSIFIER_H_ */
