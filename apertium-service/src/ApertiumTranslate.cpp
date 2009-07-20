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

#include "ApertiumServer.h"

#include "core/Translator.h"
#include "utils/Logger.h"

using namespace std;

ObjectBroker *ApertiumTranslate::objectBroker = NULL;
ModesManager *ApertiumTranslate::modesManager = NULL;
TextClassifier *ApertiumTranslate::textClassifier = NULL;

void ApertiumTranslate::execute(const iqxmlrpc::Param_list &params, iqxmlrpc::Value &retval) {
	iqxmlrpc::Struct s;

	switch (params.size()) {
	case 0:
	case 1:
	case 2:
	default:
		throw ApertiumRuntimeException("Wrong number of arguments");
		break;
	case 3: {
		string text = params[0];

		string srcLang = params[1];
		string destLang = params[2];

		Logger::Instance()->trace(DEBUG, "Invoking translate(\"" + text + "\", \"" + srcLang + "\", \"" + destLang + "\");");

		if (srcLang.empty()) {
			srcLang = textClassifier->classify(text);
			s.insert("detectedSourceLanguage", srcLang);
		}

		s.insert("translation", Translator::translate(objectBroker, modesManager, text, srcLang, destLang));
	}
		break;
	}

	iqxmlrpc::Value ret = s;
	retval = ret;
}

