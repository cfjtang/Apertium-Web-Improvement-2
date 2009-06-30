#include "FunctionMapper.h"

#include <iostream>
#include <vector>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <wchar.h>

#include <boost/algorithm/string.hpp>

#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/condition.hpp>

#include "format/Encoding.h"

#include "cg/stdafx.h"
#include "cg/icu_uoptions.h"
#include "cg/Grammar.h"
#include "cg/BinaryGrammar.h"
#include "cg/ApertiumApplicator.h"

using namespace std;

FunctionMapper::FunctionMapper(ObjectBroker *o) {
	objectBroker = o;

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

wstring FunctionMapper::execute(Program p, wstring d) {

	vector<string> params;
	const string commandLine = p.getProgramName();
	boost::split(params, commandLine, boost::is_any_of("\t "));

	string program = params[0];
	vector<string> files = p.getFileNames();

	TaskType taskType = task[program];

	FILE *in = tmpfile();

	bool useUtf8 = false;

	if (taskType == CG_PROC)
		useUtf8 = true;

	if (useUtf8) {
		string sd = Encoding::wstringToUtf8(d);
		for (size_t i = 0; i < sd.size(); ++i) {
			fputc(sd[i], in);
		}
	} else {
		for (size_t i = 0; i < d.size(); ++i) {
			fputwc(d[i], in);
		}
	}

	rewind(in);

	void *outptr;
	size_t outsize;

	FILE *out = NULL;

	if (useUtf8) {
		out = open_memstream(reinterpret_cast<char **>(&outptr), &outsize);
	} else {
		out = open_wmemstream(reinterpret_cast<wchar_t **>(&outptr), &outsize);
	}

	switch (task[program]) {
	case APERTIUM_INTERCHUNK: {
		InterchunkIndexType index = make_pair(files[0], files[1]);
		Interchunk *i = objectBroker->InterchunkPool.request(index);
		i->interchunk(in, out);
		objectBroker->InterchunkPool.release(i, index);
	}
		break;

	case APERTIUM_MULTIPLE_TRANSLATIONS: {
		TransferMultIndexType index = make_pair(files[0], files[1]);
		TransferMult *i = objectBroker->TransferMultPool.request(index);
		i->transfer(in, out);
		objectBroker->TransferMultPool.release(i, index);
	}
		break;
	case APERTIUM_POSTCHUNK: {
		PostchunkIndexType index = make_pair(files[0], files[1]);
		Postchunk *i = objectBroker->PostchunkPool.request(index);
		i->postchunk(in, out);
		objectBroker->PostchunkPool.release(i, index);
	}
		break;

	case APERTIUM_PRETRANSFER: {
		PreTransfer *i = objectBroker->PreTransferPool.request();
		i->processStream(in, out);
		objectBroker->PreTransferPool.release(i);
	}
		break;

	case APERTIUM_TAGGER: {
		HMMIndexType index = files[0];
		HMMWrapper *i = objectBroker->HMMPool.request(index);
		i->getHmm()->tagger(in, out, false);
		objectBroker->HMMPool.release(i, index);
	}
		break;

	case APERTIUM_TRANSFER: {
		TransferIndexType index = files;
		Transfer *i = objectBroker->TransferPool.request(index);

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

		objectBroker->TransferPool.release(i, index);
	}
		break;

	case LT_PROC: {
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

		FSTProcessorIndexType index = make_pair(task, files[0]);

		FSTProcessor *i = objectBroker->FSTProcessorPool.request(index);

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

		objectBroker->FSTProcessorPool.release(i, index);
	}
		break;

	case CG_PROC: {
		const char *codepage_default = ucnv_getDefaultName();
		const char *locale_default = "en_US_POSIX"; //uloc_getDefault();

		UFILE *ux_in = u_finit(in, locale_default, codepage_default);
		UFILE *ux_out = u_finit(out, locale_default, codepage_default);
		UFILE *ux_err = u_finit(stderr, locale_default, codepage_default);

		GrammarIndexType index = files[0];

		CG3::Grammar *grammar = objectBroker->GrammarPool.request(index);

		CG3::GrammarApplicator *applicator = new CG3::ApertiumApplicator(ux_in, ux_out, ux_err);

		{
		boost::mutex::scoped_lock Lock(ObjectBroker::cgMutex);
		applicator->setGrammar(grammar);
		applicator->runGrammarOnText(ux_in, ux_out);
		}

		delete applicator;

		objectBroker->GrammarPool.release(grammar, index);
	}
		break;
	}

	fclose(out);

	wstring ret;

	if (useUtf8) {
		string sret(reinterpret_cast<char *>(outptr), outsize);
		ret = Encoding::utf8ToWstring(sret);
	} else {
		wstring wret(reinterpret_cast<wchar_t *>(outptr), outsize);
		ret = wret;
	}

	fclose(in);

	return (ret);
}
