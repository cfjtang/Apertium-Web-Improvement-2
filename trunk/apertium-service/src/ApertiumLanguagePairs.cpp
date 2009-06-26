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

#include "ApertiumLanguagePairs.h"

#include "ApertiumServer.h"

#include "ApertiumRuntimeException.h"
#include "core/ModesManager.h"

#include <iostream>
#include <string>

using namespace std;

void ApertiumLanguagePairs::execute(const iqxmlrpc::Param_list &params, iqxmlrpc::Value &retval) {
	ModesManager *m = ModesManager::Instance();

	list<string> modes = m->getModeNames();
	modes.sort();

	retval = iqxmlrpc::Array();

	for (list<string>::iterator it = modes.begin(); it != modes.end(); it++) {
		//iqxmlrpc::Value v = iqxmlrpc::Value(*it);
		string mode = *it;
		size_t sep = mode.find("-", 0);

		string srcLang = mode.substr(0, sep);
		string destLang = mode.substr(sep + 1, mode.size());

		iqxmlrpc::Struct pair;

		pair.insert("destLang", destLang);
		pair.insert("srcLang", srcLang);

		iqxmlrpc::Value v = pair;

		retval.push_back(v);
	}

}
