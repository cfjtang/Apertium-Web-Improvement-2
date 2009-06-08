#ifndef TEXTCLASSIFIER_H_
#define TEXTCLASSIFIER_H_

#include <iostream>
#include <string>

#include <boost/thread.hpp>

class TextClassifier {
public:
	static TextClassifier *Instance(std::string);
	static TextClassifier *Instance();

	virtual ~TextClassifier();

	std::string classify(std::string);

private:
	TextClassifier(void *);

	static TextClassifier *instance;

	static boost::mutex instanceMutex;

	void *h;
};

#endif /* TEXTCLASSIFIER_H_ */
