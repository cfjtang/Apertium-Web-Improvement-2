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
 * The class Translator is used to execute the sequence of step required by
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
std::string Translator::translate(ResourceBroker &rb, ModesManager &mm, std::string &text, ContentType type, std::string srcLang, std::string destLang, bool markUnknownWords, Statistics *s) {
	std::string pair = srcLang + "-" + destLang;

	std::wstring wtext = Encoding::utf8ToWstring(text);

	ModesManager::ModeMapType modes = mm.getModes();
	ModesManager::ModeMapType::iterator modeit = modes.find(pair);

	if (modeit == modes.end()) {
		throw ApertiumRuntimeException("Mode not found: " + pair);
	}

	Mode mode = (*modeit).second;

	FunctionMapper fm(rb);

	std::vector<Program> programs = mode.getPrograms();

	std::wstring ret = deformat(rb, wtext, type);

	for (std::vector<Program>::iterator it = programs.begin(); it != programs.end(); ++it) {
		Program program = *it;

		{
			std::stringstream ss;
			ss << "Translator::translate(): Executing " << program;
			Logger::Instance()->trace(Logger::Debug, ss.str());
		}

		std::wstring tmp = fm.execute(program, ret, markUnknownWords);

		{
			std::stringstream ss;
			ss << "Translator::translate(): Result: " << Encoding::wstringToUtf8(tmp);
			Logger::Instance()->trace(Logger::Debug, ss.str());
		}

		ret = tmp;
	}

	return Encoding::wstringToUtf8(reformat(rb, ret, type));
}

void Translator::eagerlyLoad(ResourceBroker &rb, ModesManager &mm, std::string srcLang, std::string destLang, unsigned int qty) {
	std::string pair = srcLang + "-" + destLang;

	{
		std::stringstream ss;
		ss << "Translator::translate(): Eagerly loading " << qty << " instances of the pair " << pair;
		Logger::Instance()->trace(Logger::Info, ss.str());
	}

	ModesManager::ModeMapType modes = mm.getModes();
	ModesManager::ModeMapType::iterator modeit = modes.find(pair);

	if (modeit == modes.end()) {
		throw ApertiumRuntimeException("Mode not found: " + pair);
	}

	Mode mode = (*modeit).second;

	FunctionMapper fm(rb);

	std::vector<Program> programs = mode.getPrograms();

	for (std::vector<Program>::iterator it = programs.begin(); it != programs.end(); ++it) {
		Program program = *it;

		{
			std::stringstream ss;
			ss << "Translator::translate(): Loading " << program;
			Logger::Instance()->trace(Logger::Debug, ss.str());
		}

		fm.load(program, qty);
	}

	std::stack<Format*> memd;
	std::stack<Format*> memr;

	Program pd("apertium-destxt");
	Program pr("apertium-retxt");

	for (unsigned int i = 0; i < qty; ++i) {
		Format *fd = rb.FormatPool.acquire(pd);
		Format *fr = rb.FormatPool.acquire(pr);

		memd.push(fd);
		memr.push(fr);
	}

	while (!memd.empty()) {
		Format *fd = memd.top();
		rb.FormatPool.release(fd, pd);
		memd.pop();
	}

	while (!memr.empty()) {
		Format *fr = memr.top();
		rb.FormatPool.release(fr, pr);
		memr.pop();
	}
}

std::wstring Translator::deformat(ResourceBroker &rb, std::wstring &in, ContentType type) {
	Program *p = NULL;

	switch (type) {
	case TEXT:
		p = new Program("apertium-destxt");
		break;
	//case HTML:
		//p = new Program("apertium-deshtml");
		//break;
	}

	{
		std::stringstream ss;
		ss << "Translator::translate(): Executing " << *p;
		Logger::Instance()->trace(Logger::Debug, ss.str());
	}

	Format *d = rb.FormatPool.acquire(*p);
	std::wstring ret = d->process(in);
	rb.FormatPool.release(d, *p);

	{
		std::stringstream ss;
		ss << "Translator::translate(): Result: " << Encoding::wstringToUtf8(ret);
		Logger::Instance()->trace(Logger::Debug, ss.str());
	}

	delete p;

	return(ret);
}

std::wstring Translator::reformat(ResourceBroker &rb, std::wstring &in, ContentType type) {

	Program *p = NULL;

	switch (type) {
	case TEXT:
		p = new Program("apertium-retxt");
		break;
	//case HTML:
	//	p = new Program("apertium-rehtml");
	//	break;
	}

	{
		std::stringstream ss;
		ss << "Translator::translate(): Executing " << *p;
		Logger::Instance()->trace(Logger::Debug, ss.str());
	}

	Format *r = rb.FormatPool.acquire(*p);
	std::wstring ret = r->process(in);
	rb.FormatPool.release(r, *p);

	{
		std::stringstream ss;
		ss << "Translator::translate(): Result: " << Encoding::wstringToUtf8(ret);
		Logger::Instance()->trace(Logger::Debug, ss.str());
	}

	delete p;

	return(ret);
}
