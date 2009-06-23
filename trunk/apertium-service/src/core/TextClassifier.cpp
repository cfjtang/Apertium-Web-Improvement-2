/*
 * Copyright (C) 2009  Pasquale Minervini <p.minervini@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	if (instance != NULL) {
		instance = NULL;
	}
}

std::string TextClassifier::classify(std::string str) {
	std::string ret = textcat_Classify(h, str.data(), str.size());
	return(ret == "SHORT" ? "" : ret.substr(1, ret.size() - 2));
}
