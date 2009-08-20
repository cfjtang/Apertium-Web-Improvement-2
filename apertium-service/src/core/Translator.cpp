/**
 * @file
 * @author  Pasquale Minervini <p.minervini@gmail.com>
 * @version 1.0
 *
 * @section LICENSE
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
 *
 * @section DESCRIPTION
 *
 * The class Translate is used to execute the sequence of step required by
 * a translation task by using the informations contained in Modes files.
 */

#include <iostream>
#include <string>

#include "Translator.h"

#include "ResourceBroker.h"
#include "FunctionMapper.h"
#include "ModesManager.h"
#include "TextClassifier.h"

#include "ApertiumRuntimeException.h"

#include "format/Deformat.h"
#include "format/Reformat.h"
#include "format/Encoding.h"

#include "utils/Logger.h"

/**
 * Translate a given text from a source language to a destination language, by using the resources present inside a specific
 * Resource Pool and the informations present inside a given mode.
 */
std::string Translator::translate(ResourceBroker &rb, ModesManager &mm, std::string &text, std::string srcLang, std::string destLang,
		Statistics *s) {
	string pair = srcLang + "-" + destLang;

	wstring wtext = Encoding::utf8ToWstring(text);

	ModesManager::ModeMapType modes = mm.getModes();
	ModesManager::ModeMapType::iterator modeit = modes.find(pair);

	if (modeit == modes.end()) {
		throw ApertiumRuntimeException("Mode not found: " + pair);
	}

	Mode mode = (*modeit).second;

	FunctionMapper fm(rb);

	vector<Program> programs = mode.getPrograms();

	wstring ret = deformat(rb, wtext);

	for (vector<Program>::iterator it = programs.begin(); it != programs.end(); ++it) {
		Program program = *it;

		std::stringstream ss;
		ss << "Translator::translate(): Executing " << program;
		Logger::Instance()->trace(Logger::DEBUG, ss.str());

		wstring tmp = fm.execute(program, ret);
		ret = tmp;
	}

	return Encoding::wstringToUtf8(reformat(rb, ret));
}

std::wstring Translator::deformat(ResourceBroker &rb, std::wstring &in) {
	wstringstream wss;
	Program p("apertium-destxt");
	Deformat *d = rb.DeformatPool.acquire(p);
	d->reset();
	d->setYyin(in);
	d->setYyout(&wss);
	d->lex();
	rb.DeformatPool.release(d, p);
	return(wss.str());
}

std::wstring Translator::reformat(ResourceBroker &rb, std::wstring &in) {
	wstringstream wss;
	Program p("apertium-retxt");
	Reformat *r = rb.ReformatPool.acquire(p);
	r->setYyin(in);
	r->setYyout(&wss);
	r->lex();
	rb.ReformatPool.release(r, p);
	return(wss.str());
}
