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

#include "format/Encoding.h"

#include "utils/Logger.h"

/**
 * Translate a given text from a source language to a destination language, by using the resources present inside a specific
 * Resource Pool and the informations present inside a given mode.
 */
std::string Translator::translate(ResourceBroker &rb, ModesManager &mm, std::string &text, ContentType type, std::string srcLang, std::string destLang, Statistics *s) {
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

	wstring ret = deformat(rb, wtext, type);

	for (vector<Program>::iterator it = programs.begin(); it != programs.end(); ++it) {
		Program program = *it;

		std::stringstream ss;
		ss << "Translator::translate(): Executing " << program;
		Logger::Instance()->trace(Logger::Debug, ss.str());

		wstring tmp = fm.execute(program, ret);
		ret = tmp;
	}

	return Encoding::wstringToUtf8(reformat(rb, ret, type));
}

std::wstring Translator::deformat(ResourceBroker &rb, std::wstring &in, ContentType type) {
	Program *p = NULL;

	switch (type) {
	case TEXT:
		p = new Program("apertium-destxt");
		break;
	case HTML:
		p = new Program("apertium-deshtml");
		break;
	}

	FormatWrapper *d = rb.FormatPool.acquire(*p);
	std::wstring ret = d->getFormatter()->process(in);
	rb.FormatPool.release(d, *p);

	delete p;

	return(ret);
}

std::wstring Translator::reformat(ResourceBroker &rb, std::wstring &in, ContentType type) {

	Program *p = NULL;

	switch (type) {
	case TEXT:
		p = new Program("apertium-retxt");
		break;
	case HTML:
		p = new Program("apertium-rehtml");
		break;
	}

	FormatWrapper *r = rb.FormatPool.acquire(*p);
	std::wstring ret = r->getFormatter()->process(in);
	rb.FormatPool.release(r, *p);

	delete p;

	return(ret);
}
