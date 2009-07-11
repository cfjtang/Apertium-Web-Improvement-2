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

TextClassifier::TextClassifier(fs::path p) {
	h = textcat_Init(p.string().data());
}

TextClassifier::~TextClassifier() {
	textcat_Done(h);
}

std::string TextClassifier::classify(std::string str) {
	std::string ret = textcat_Classify(h, str.data(), str.size());
	return(ret == "SHORT" ? "" : ret.substr(1, ret.size() - 2));
}
