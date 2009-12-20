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

#include "core/cg/stdafx.h"
#include "core/cg/icu_uoptions.h"
#include "core/cg/Grammar.h"
#include "core/cg/BinaryGrammar.h"
#include "core/cg/ApertiumApplicator.h"

using namespace std;

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

/**
 * Request an arbitrary resource from the Resource Pool, use it and release it.
 */
wstring FunctionMapper::execute(Program &p, wstring &d) {
	vector<string> params;
	const string commandLine = p.getProgramName();
	boost::split(params, commandLine, boost::is_any_of("\t "));

	string program = params[0];
	vector<string> files = p.getFileNames();

	TaskType taskType = task[program];

	int infd[2];
	int outfd[2];

	pipe(infd);
	pipe(outfd);

	FILE *tin = fdopen(infd[1], "w");
	FILE *in = fdopen(infd[0], "r");

	FILE *out = fdopen(outfd[1], "w");
	FILE *tout = fdopen(outfd[0], "r");

	bool useUtf8 = false;

	if (taskType == CG_PROC)
		useUtf8 = true;

	if (useUtf8) {
		string sd = Encoding::wstringToUtf8(d);

		for (size_t i = 0; i < sd.size(); ++i) {
			fputc(sd[i], tin);
		}
	} else {
		for (size_t i = 0; i < d.size(); ++i) {
			fputwc(d[i], tin);
		}
	}

	fclose(tin);

	switch (task[program]) {
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
		//PreTransfer *i = resourceBroker->PreTransferPool.acquire(p);
		//i->processStream(in, out);
		//resourceBroker->PreTransferPool.release(i, p);
		PreTransfer::processStream(in, out);
	}
		break;

	case APERTIUM_TAGGER: {
		HMMWrapper *i = resourceBroker->HMMPool.acquire(p);
		i->getHmm()->tagger(in, out, false);
		resourceBroker->HMMPool.release(i, p);
	}
		break;

	case APERTIUM_TRANSFER: {
		Transfer *i = resourceBroker->TransferPool.acquire(p);

		bool useBilingual = true;

		for (vector<string>::iterator it = params.begin(); it != params.end(); ++it) {
			string param = *it;
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

		for (vector<string>::iterator it = params.begin(); it != params.end(); ++it) {
			string param = *it;
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
			i->generation(in, out);
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
		UFILE *ux_err = u_finit(stderr, locale_default, codepage_default);

		CG3::Grammar *grammar = resourceBroker->GrammarPool.acquire(p);

		//CG3::GrammarApplicator *applicator = new CG3::ApertiumApplicator(ux_in, ux_out, ux_err);
		CG3::GrammarApplicator *applicator = new CG3::ApertiumApplicator(ux_err);

		{
		boost::mutex::scoped_lock Lock(ResourceBroker::cgMutex);
		applicator->setGrammar(grammar);
		applicator->runGrammarOnText(ux_in, ux_out);
		}

		delete applicator;

		resourceBroker->GrammarPool.release(grammar, p);
	}
		break;
	}

	fclose(in);
	fclose(out);

	wstring ret;

	if (useUtf8) {
		stringstream ss;
		while (true) {
			int c = fgetc(tout);
			if (feof(tout))
				break;
			ss << (char)c;
		}
		ret = Encoding::utf8ToWstring(ss.str());
	} else {
		wstringstream wss;
		while (true) {
			wint_t c = fgetwc(tout);
			if (feof(tout))
				break;
			wss << (wchar_t)c;
		}
		ret = wss.str();
	}

	fclose(tout);

	return (ret);
}
