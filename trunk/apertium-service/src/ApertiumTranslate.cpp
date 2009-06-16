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

#include "format/Encoding.h"
#include "core/Translator.h"
#include "core/Modes.h"

#include "ApertiumRuntimeException.h"

using namespace std;

void ApertiumTranslate::execute(const iqxmlrpc::Param_list &params, iqxmlrpc::Value &retval) {

	string modeName;

	switch (params.size()) {
	case 0:
	case 1:
		throw ApertiumRuntimeException("Too few arguments");
		break;
	case 2:
		modeName = string(params[1]);
		break;
	case 3:
		modeName = string(params[1] + "-" + params[2]);
		break;
	default:
		throw ApertiumRuntimeException("Wrong number of arguments");
		break;
	}

	Mode *mode = Modes::Instance()->getMode(modeName);

	if (!mode) {
		throw ApertiumRuntimeException("Mode not found: " + modeName);
	}

	string in = params[0];
	wstring toTranslate = Encoding::utf8ToWstring(in);

	wstring translated = Translator::translate(toTranslate, mode);
	string out = Encoding::wstringToUtf8(translated);
	retval = out;
}

