#include "TextClassifier.h"

extern "C" {
	#include <textcat.h>
}

TextClassifier *TextClassifier::instance = NULL;

TextClassifier *TextClassifier::Instance() {
	return(instance);
}

TextClassifier *TextClassifier::Instance(std::string path) {
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
	textcat_Done(h);
	instance = NULL;
}

std::string TextClassifier::classify(std::string str) {
	return std::string(textcat_Classify(h, str.data(), str.size()));
}
