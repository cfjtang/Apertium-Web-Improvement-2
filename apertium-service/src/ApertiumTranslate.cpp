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
		wstring xin(params[0]);
		wstringstream xout;

		Deformat *d = new Deformat(&xin, &xout);

		int x = 1;
		while (x != 0) {
			x = tx->lex();
		}

		delete tx;

		wstring ret = string2wstring(xout.str());

		FunctionMapper *fm = new FunctionMapper(ObjectBroker::Instance());

		string modeName = params[1];
		Mode *mode = Modes::Instance()->getMode(modeName);

		if (!mode) {
			throw ApertiumRuntimeException("Mode not found: " + modeName);
		}

		vector<Program> programs = mode->getPrograms();

		for (vector<Program>::iterator it = programs.begin(); it
				!= programs.end(); ++it) {
			Program program = *it;
			wstring tmp = fm->execute(program, ret);
			ret = tmp;
		}

		delete fm;

		string yret = wstring2string(ret);

		stringstream yin(yret);
		stringstream yout;

		TXTReformat *ty = new TXTReformat(&yin, &yout);

		int y = 1;
		while (y != 0) {
			y = ty->lex();
		}

		delete ty;

		retval = yout.str();
	}
}
