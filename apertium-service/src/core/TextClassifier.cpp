#include "TextClassifier.h"

extern "C" {
	#include <textcat.h>
}

TextClassifier *TextClassifier::instance = NULL;
boost::mutex TextClassifier::instanceMutex;

TextClassifier *TextClassifier::Instance() {
	boost::mutex::scoped_lock Lock(instanceMutex);
	TextClassifier *ret = instance;
	return(ret);
}

TextClassifier *TextClassifier::Instance(std::string path) {
	boost::mutex::scoped_lock Lock(instanceMutex);
	if (!instance) {
		void *h = textcat_Init(path.data());
		if (h != NULL)
			instance = new TextClassifier(h);
		else
			instance = NULL;
	}
	return(instance);
}

TextClassifier::TextClassifier(void *p) {
	h = p;
}

TextClassifier::~TextClassifier() {
	boost::mutex::scoped_lock Lock(instanceMutex);
	textcat_Done(h);
	instance = NULL;
}

std::string TextClassifier::classify(std::string str) {
	return std::string(textcat_Classify(h, str.data(), str.size()));
}
