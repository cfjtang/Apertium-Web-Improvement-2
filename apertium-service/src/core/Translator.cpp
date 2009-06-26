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

#include "Translator.h"

#include "ObjectBroker.h"
#include "FunctionMapper.h"

#include "format/Deformat.h"
#include "format/Reformat.h"

#include "format/Encoding.h"

#include "ModesManager.h"
#include "TextClassifier.h"

#include "ApertiumRuntimeException.h"

std::string Translator::translate(std::string text, std::string srcLang, std::string destLang) {

	string pair = srcLang + "-" + destLang;

	wstring wtext = Encoding::utf8ToWstring(text);

	Mode *mode = ModesManager::Instance()->getMode(pair);

	if (!mode) {
		throw ApertiumRuntimeException("Mode not found: " + pair);
	}

	FunctionMapper *fm = new FunctionMapper(ObjectBroker::Instance());

	vector<Program> programs = mode->getPrograms();

	wstring ret = deformat(wtext);

	for (vector<Program>::iterator it = programs.begin(); it != programs.end(); ++it) {
		Program program = *it;

		wstring tmp = fm->execute(program, ret);
		ret = tmp;
	}

	delete fm;

	return Encoding::wstringToUtf8(reformat(ret));
}

std::wstring Translator::deformat(std::wstring in) {
	wstringstream wss;
	Deformat *d = ObjectBroker::Instance()->DeformatPool.request();
	d->reset();
	d->setYyin(in);
	d->setYyout(&wss);
	d->lex();
	ObjectBroker::Instance()->DeformatPool.release(d);
	return(wss.str());
}

std::wstring Translator::reformat(std::wstring in) {
	wstringstream wss;
	Reformat *r = ObjectBroker::Instance()->ReformatPool.request();
	r->setYyin(in);
	r->setYyout(&wss);
	r->lex();
	ObjectBroker::Instance()->ReformatPool.release(r);
	return(wss.str());
}
