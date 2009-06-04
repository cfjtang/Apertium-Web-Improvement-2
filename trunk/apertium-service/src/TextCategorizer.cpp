#include "TextCategorizer.h"

extern "C" {
	#include <textcat.h>
}

TextCategorizer *TextCategorizer::instance = NULL;

TextCategorizer *TextCategorizer::Instance() {
	if (!instance) {
		instance = TextCategorizer::Instance("tc.conf");
	}
	return(instance);
}

TextCategorizer *TextCategorizer::Instance(std::string path) {
	if (!instance) {
		void *h = textcat_Init(path.data());
		if (h != NULL)
			instance = new TextCategorizer(h);
		else
			instance = NULL;
	}
	return(instance);
}

TextCategorizer::TextCategorizer(void *p) {
	h = p;
}

TextCategorizer::~TextCategorizer() {
	textcat_Done(h);
}

std::string TextCategorizer::classify(std::string str) {
	return std::string(textcat_Classify(h, str.data(), str.size()));
}
