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
 * The FunctionMapper class is used for requesting an arbitrary resource from
 * the Resource Pool, using it and releasing it depending by a given instance
 * of the Program class.
 */

#include "FunctionMapper.h"

#include <iostream>
#include <sstream>

#include <vector>
#include <stack>

#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <wchar.h>

#include <boost/algorithm/string.hpp>

#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/condition.hpp>

#include "format/Encoding.h"

#include "core/vislcg3/stdafx.h"
#include "core/vislcg3/icu_uoptions.h"
#include "core/vislcg3/Grammar.h"
#include "core/vislcg3/BinaryGrammar.h"
#include "core/vislcg3/ApertiumApplicator.h"

#if defined(BOOST_WINDOWS_API) && defined(__CYGWIN__)
# undef BOOST_WINDOWS_API
# define BOOST_POSIX_API 1
#endif

#include <boost/process/detail/file_handle.hpp>
#include <boost/process/detail/pipe.hpp>

FunctionMapper::FunctionMapper(ResourceBroker &rb) : resourceBroker(&rb) {
	//task["deformat"] = DEFORMAT;
	//task["reformat"] = REFORMAT;

	task["apertium-interchunk"] = APERTIUM_INTERCHUNK;
	task["apertium-multiple-translations"] = APERTIUM_MULTIPLE_TRANSLATIONS;
	task["apertium-postchunk"] = APERTIUM_POSTCHUNK;
	task["apertium-pretransfer"] = APERTIUM_PRETRANSFER;
	task["apertium-tagger"] = APERTIUM_TAGGER;
	task["apertium-transfer"] = APERTIUM_TRANSFER;
	task["lt-proc"] = LT_PROC;
	task["cg-proc"] = CG_PROC;
}

FunctionMapper::~FunctionMapper() { }

void FunctionMapper::load(Program &p, unsigned int qty) {
	std::vector<std::string> params = p.getParameters();
	const std::string commandLine = p.getProgramName();

	std::string program = params[0];
	std::vector<std::string> files = p.getFileNames();

	TaskType taskType = OTHER;

	TaskMapType::iterator iter = task.find(program);
	if (iter != task.end()) {
		taskType = iter->second;
	}

	switch (taskType) {
	case APERTIUM_INTERCHUNK: {
		std::stack<Interchunk*> mem;

		for (unsigned int i = 0; i < qty; ++i) {
			Interchunk *i = resourceBroker->InterchunkPool.acquire(p);
			mem.push(i);
		}

		while (!mem.empty()) {
			Interchunk *i = mem.top();
			resourceBroker->InterchunkPool.release(i, p);
			mem.pop();
		}
	}
		break;

	case APERTIUM_MULTIPLE_TRANSLATIONS: {
		std::stack<TransferMult*> mem;

		for (unsigned int i = 0; i < qty; ++i) {
			TransferMult *i = resourceBroker->TransferMultPool.acquire(p);
			mem.push(i);
		}

		while (!mem.empty()) {
			TransferMult *i = mem.top();
			resourceBroker->TransferMultPool.release(i, p);
			mem.pop();
		}
	}
		break;

	case APERTIUM_POSTCHUNK: {
		std::stack<Postchunk*> mem;

		for (unsigned int i = 0; i < qty; ++i) {
			Postchunk *i = resourceBroker->PostchunkPool.acquire(p);
			mem.push(i);
		}

		while (!mem.empty()) {
			Postchunk *i = mem.top();
			resourceBroker->PostchunkPool.release(i, p);
			mem.pop();
		}
	}
		break;

	case APERTIUM_TAGGER: {
		std::stack<HMMWrapper*> mem;

		for (unsigned int i = 0; i < qty; ++i) {
			HMMWrapper *i = resourceBroker->HMMPool.acquire(p);
			mem.push(i);
		}

		while (!mem.empty()) {
			HMMWrapper *i = mem.top();
			resourceBroker->HMMPool.release(i, p);
			mem.pop();
		}
	}
		break;

	case APERTIUM_TRANSFER: {
		std::stack<Transfer*> mem;

		for (unsigned int i = 0; i < qty; ++i) {
			Transfer *i = resourceBroker->TransferPool.acquire(p);
			mem.push(i);
		}

		while (!mem.empty()) {
			Transfer *i = mem.top();
			resourceBroker->TransferPool.release(i, p);
			mem.pop();
		}
	}
		break;

	case LT_PROC: {
		std::stack<FSTProcessor*> mem;

		for (unsigned int i = 0; i < qty; ++i) {
			FSTProcessor *i = resourceBroker->FSTProcessorPool.acquire(p);
			mem.push(i);
		}

		while (!mem.empty()) {
			FSTProcessor *i = mem.top();
			resourceBroker->FSTProcessorPool.release(i, p);
			mem.pop();
		}
	}
		break;

	case CG_PROC: {
		std::stack<CGApplicator*> mem;

		for (unsigned int i = 0; i < qty; ++i) {
			CGApplicator *grammar = resourceBroker->GrammarPool.acquire(p);
			mem.push(grammar);
		}

		while (!mem.empty()) {
			CGApplicator *grammar = mem.top();
			resourceBroker->GrammarPool.release(grammar, p);
			mem.pop();
		}
	}
		break;

	case OTHER: {

	}
		break;
	}
}

/**
 * Request an arbitrary resource from the Resource Pool, use it and release it.
 */
std::wstring FunctionMapper::execute(Program &p, std::wstring &d, bool markUnknownWords) {
	std::vector<std::string> params;
	const std::string commandLine = p.getProgramName();
	boost::split(params, commandLine, boost::is_any_of("\t "));

	std::string program = params[0];
	std::vector<std::string> files = p.getFileNames();

	TaskType taskType = OTHER;

	TaskMapType::iterator iter = task.find(program);
	if (iter != task.end()) {
		taskType = iter->second;
	}

	boost::process::detail::pipe pin, pout;

	boost::process::detail::file_handle &pinr = pin.rend();
	boost::process::detail::file_handle &pinw = pin.wend();

	#if defined(BOOST_WINDOWS_API)
	# define fdopen(x, y) fdopen(_open_osfhandle(_get_osfhandle(x), 0), y)
	#endif

	FILE *tin = fdopen(pinw.get(), "w");
	FILE *in = fdopen(pinr.get(), "r");

	boost::process::detail::file_handle &poutr = pout.rend();
	boost::process::detail::file_handle &poutw = pout.wend();

	FILE *out = fdopen(poutw.get(), "w");
	FILE *tout = fdopen(poutr.get(), "r");

	bool useUtf8 = false;

	if (taskType == CG_PROC || taskType == OTHER)
		useUtf8 = true;

	if (useUtf8) {
		std::string sd = Encoding::wstringToUtf8(d);

		for (size_t i = 0; i < sd.size(); ++i) {
			fputc(sd[i], tin);
		}
	} else {
		for (size_t i = 0; i < d.size(); ++i) {
			fputwc(d[i], tin);
		}
	}

	fclose(tin);

	switch (taskType) {
	case APERTIUM_INTERCHUNK: {
		Interchunk *i = resourceBroker->InterchunkPool.acquire(p);
		i->interchunk(in, out);
		resourceBroker->InterchunkPool.release(i, p);
	}
		break;

	case APERTIUM_MULTIPLE_TRANSLATIONS: {
		TransferMult *i = resourceBroker->TransferMultPool.acquire(p);
		i->transfer(in, out);
		resourceBroker->TransferMultPool.release(i, p);
	}
		break;

	case APERTIUM_POSTCHUNK: {
		Postchunk *i = resourceBroker->PostchunkPool.acquire(p);
		i->postchunk(in, out);
		resourceBroker->PostchunkPool.release(i, p);
	}
		break;

	case APERTIUM_PRETRANSFER: {
		PreTransfer::processStream(in, out);
	}
		break;

	case APERTIUM_TAGGER: {
		HMMWrapper *i = resourceBroker->HMMPool.acquire(p);
		i->get()->tagger(in, out, false);
		resourceBroker->HMMPool.release(i, p);
	}
		break;

	case APERTIUM_TRANSFER: {
		Transfer *i = resourceBroker->TransferPool.acquire(p);

		bool useBilingual = true;

		for (std::vector<std::string>::iterator it = params.begin(); it != params.end(); ++it) {
			std::string param = *it;
			if (param == "-n") {
				useBilingual = false;
			}
		}

		if (useBilingual)
			i->setUseBilingual(true);
		else
			i->setUseBilingual(false);

		i->transfer(in, out);

		resourceBroker->TransferPool.release(i, p);
	}
		break;

	case LT_PROC: {
		enum FSTProcessorTask { ANALYSIS, GENERATION, POSTGENERATION, TRANSLITERATION };

		FSTProcessorTask task = ANALYSIS;

		for (std::vector<std::string>::iterator it = params.begin(); it != params.end(); ++it) {
			std::string param = *it;
			if (param[0] == '-') {
				switch (param[1]) {
				case 'g':
					task = GENERATION;
					break;
				case 'p':
					task = POSTGENERATION;
					break;
				case 'a':
					task = ANALYSIS;
					break;
				case 't':
					task = TRANSLITERATION;
					break;
				}
			} else if (param[0] == '$' && param[1] == '1') {
				task = GENERATION;
			}
		}

		FSTProcessor *i = resourceBroker->FSTProcessorPool.acquire(p);

		switch (task) {
		case ANALYSIS:
			i->analysis(in, out);
			break;
		case GENERATION:
			if (markUnknownWords) {
				i->generation(in, out);
			} else {
				i->generation(in, out, gm_clean);
			}
			break;
		case POSTGENERATION:
			i->postgeneration(in, out);
			break;
		case TRANSLITERATION:
			i->transliteration(in, out);
		}

		resourceBroker->FSTProcessorPool.release(i, p);
	}
		break;

	case CG_PROC: {
		const char *codepage_default = ucnv_getDefaultName();
		const char *locale_default = "en_US_POSIX"; //uloc_getDefault();

		UFILE *ux_in = u_finit(in, locale_default, codepage_default);
		UFILE *ux_out = u_finit(out, locale_default, codepage_default);
		//UFILE *ux_err = u_finit(stderr, locale_default, codepage_default);

		bool wordform_case = false;

		for (std::vector<std::string>::iterator it = params.begin(); it != params.end(); ++it) {
				std::string param = *it;
				if (param[0] == '-') {
					switch (param[1]) {
					case 'w':
						wordform_case = true;
						break;
					}
				}
		}

		{ // XXX
		boost::mutex::scoped_lock Lock(ResourceBroker::cgMutex);
		CGApplicator *grammar = resourceBroker->GrammarPool.acquire(p);

		(grammar->get())->wordform_case = wordform_case;
		(grammar->get())->runGrammarOnText(ux_in, ux_out);

		resourceBroker->GrammarPool.release(grammar, p);
		} // XXX

	}
		break;

	case OTHER: {
		Process *proc = resourceBroker->ProcessPool.acquire(p);

		while (true) {
			int c = fgetc(in);
			if (feof(in))
				break;
			(proc->in()) << (char)c;
		}

		(proc->in()) << (char)0;

		std::string os;
		(proc->out()) >> os;

		fwrite(os.c_str(), sizeof(char), os.size(), out);
		fflush(out);

		resourceBroker->ProcessPool.release(proc, p);
	}

		break;
	}

	fclose(in);
	fclose(out);

	std::wstring ret;

	if (useUtf8) {
		std::stringstream ss;
		while (true) {
			int c = fgetc(tout);
			if (feof(tout))
				break;
			ss << (char)c;
		}
		ret = Encoding::utf8ToWstring(ss.str());
	} else {
		std::wstringstream wss;
		while (true) {
			wint_t c = fgetwc(tout);
			if (feof(tout))
				break;
			wss << (wchar_t)c;
		}
		ret = wss.str();
	}

	fclose(tout);

	pinr.release();
	pinw.release();

	poutr.release();
	poutw.release();

	return (ret);
}
