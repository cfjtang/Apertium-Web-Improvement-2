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

#include <iostream>
#include <string>

#include "Translator.h"

#include "ObjectBroker.h"
#include "FunctionMapper.h"

#include "format/Deformat.h"
#include "format/Reformat.h"

#include "format/Encoding.h"

#include "utils/Logger.h"

#include "ModesManager.h"
#include "TextClassifier.h"

#include "ApertiumRuntimeException.h"

std::string Translator::translate(ObjectBroker *ob, ModesManager *mm, std::string text, std::string srcLang, std::string destLang) {

	string pair = srcLang + "-" + destLang;

	wstring wtext = Encoding::utf8ToWstring(text);

	Mode *mode = mm->getMode(pair);

	if (!mode) {
		throw ApertiumRuntimeException("Mode not found: " + pair);
	}

	FunctionMapper *fm = new FunctionMapper(ob);

	vector<Program> programs = mode->getPrograms();

	wstring ret = deformat(ob, wtext);

	for (vector<Program>::iterator it = programs.begin(); it != programs.end(); ++it) {
		Program program = *it;
		std::stringstream ss;
		ss << "Translator::translate(): Executing " << program;
		Logger::Instance()->trace(DEBUG, ss.str());
		wstring tmp = fm->execute(program, ret);
		ret = tmp;
	}

	delete fm;

	return Encoding::wstringToUtf8(reformat(ob, ret));
}

std::wstring Translator::deformat(ObjectBroker *ob, std::wstring in) {
	wstringstream wss;
	Deformat *d = ob->DeformatPool.request();
	d->reset();
	d->setYyin(in);
	d->setYyout(&wss);
	d->lex();
	ob->DeformatPool.release(d);
	return(wss.str());
}

std::wstring Translator::reformat(ObjectBroker *ob, std::wstring in) {
	wstringstream wss;
	Reformat *r = ob->ReformatPool.request();
	r->setYyin(in);
	r->setYyout(&wss);
	r->lex();
	ob->ReformatPool.release(r);
	return(wss.str());
}
