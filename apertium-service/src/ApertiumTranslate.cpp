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

#include "ApertiumTranslate.h"

#include <iostream>
#include <string>

#include "core/Translator.h"
#include "core/TextClassifier.h"

#include "ApertiumRuntimeException.h"

using namespace std;

void ApertiumTranslate::execute(const iqxmlrpc::Param_list &params, iqxmlrpc::Value &retval) {

	iqxmlrpc::Struct s;

	switch (params.size()) {
	case 0:
	case 1:
	case 2:
		throw ApertiumRuntimeException("Too few arguments");
		break;
	case 3: {
		string text = params[0];

		string srcLang = params[1];
		string destLang = params[2];

		bool detected = false;

		if (srcLang.empty()) {
			detected = true;
			srcLang = TextClassifier::Instance()->classify(text);
		}

		s.insert("translation", Translator::translate(text, srcLang, destLang));

		if (detected) {
			s.insert("detectedSourceLanguage", srcLang);
		}
	}
		break;
	default:
		throw ApertiumRuntimeException("Wrong number of arguments");
		break;
	}

	iqxmlrpc::Value ret = s;
	retval = ret;
}

