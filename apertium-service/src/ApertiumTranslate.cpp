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

#include <glibmm/convert.h>
#include <glibmm/ustring.h>

#include "format/Deformat.h"
#include "format/Reformat.h"

#include "core/ObjectBroker.h"
#include "core/FunctionMapper.h"

#include "Modes.h"

#include "ApertiumRuntimeException.h"

using namespace std;

void ApertiumTranslate::execute(const iqxmlrpc::Param_list &params,
		iqxmlrpc::Value &retval) {
	//cout << "ApertiumTranslate::execute() invoked;" << endl;

	if (params.size() < 2) {
		throw ApertiumRuntimeException("Too few arguments");
		//retval = 0;
	} else {
		string uin = params[0];

		wstring win = utf8ToWstring(uin);

		wstring ret = deformat(win);

		FunctionMapper *fm = new FunctionMapper(ObjectBroker::Instance());

		string modeName = params[1];
		Mode *mode = Modes::Instance()->getMode(modeName);

		if (!mode) {
			throw ApertiumRuntimeException("Mode not found: " + modeName);
		}

		vector<Program> programs = mode->getPrograms();

		for (vector<Program>::iterator it = programs.begin(); it != programs.end(); ++it) {
			Program program = *it;
			wstring tmp = fm->execute(program, ret);
			ret = tmp;
		}

		delete fm;

		wstring wout = reformat(ret);

		string rval = wstringToUtf8(wout);

		retval = rval;
	}
}

std::wstring ApertiumTranslate::deformat(std::wstring in) {
	wstringstream wss;
	Deformat *d = new Deformat(in, &wss);
	d->lex();
	delete d;
	return(wss.str());
}

std::wstring ApertiumTranslate::reformat(std::wstring in) {
	wstringstream wss;
	Reformat *r = new Reformat(in, &wss);
	r->lex();
	delete r;
	return(wss.str());
}

std::string ApertiumTranslate::wstringToUtf8(std::wstring in) {
	string ret = Glib::convert(std::string(reinterpret_cast<const char *>(in.data()), in.size() * sizeof(wchar_t)), "UTF-8", "WCHAR_T");
	return ret;
}

std::wstring ApertiumTranslate::utf8ToWstring(std::string in) {
	string str = Glib::convert(in, "WCHAR_T", "UTF-8");
	wstring ret(reinterpret_cast<const wchar_t *>(str.data()), str.size() / sizeof(wchar_t));
	return ret;
}
