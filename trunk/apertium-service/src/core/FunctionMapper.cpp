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

using namespace std;

FunctionMapper::FunctionMapper(ObjectBroker *o) {
	objectBroker = o;

	task["apertium-interchunk"] = APERTIUM_INTERCHUNK;
	task["apertium-multiple-translations"] = APERTIUM_MULTIPLE_TRANSLATIONS;
	task["apertium-postchunk"] = APERTIUM_POSTCHUNK;
	task["apertium-pretransfer"] = APERTIUM_PRETRANSFER;
	task["apertium-tagger"] = APERTIUM_TAGGER;
	task["apertium-transfer"] = APERTIUM_TRANSFER;
	task["lt-proc"] = LT_PROC;

}

FunctionMapper::~FunctionMapper() {

}

wstring FunctionMapper::execute(Program p, wstring d) {

	FILE *in = tmpfile();

	for (size_t i = 0; i < d.size(); i++)
		fputwc(d[i], in);

	rewind(in);

	wchar_t *outptr;
	size_t outsize;
	FILE *out = open_wmemstream(&outptr, &outsize);
	//FILE *out = tmpfile();

	vector<string> params;
	const string commandLine = p.getProgramName();
	boost::split(params, commandLine, boost::is_any_of("\t "));

	string program = params[0];
	vector<string> files = p.getFileNames();

	switch (task[program]) {
	case APERTIUM_INTERCHUNK: {
		InterchunkIndexType index = make_pair(files[0], files[1]);
		//boost::mutex::scoped_lock lock(mutexin);
		Interchunk *i = objectBroker->InterchunkPool.request(index);
		i->interchunk(in, out);
		objectBroker->InterchunkPool.release(i, index);
	}
		break;

	case APERTIUM_MULTIPLE_TRANSLATIONS: {
		TransferMultIndexType index = make_pair(files[0], files[1]);
		//boost::mutex::scoped_lock lock(mutexmt);
		TransferMult *i = objectBroker->TransferMultPool.request(index);
		i->transfer(in, out);
		objectBroker->TransferMultPool.release(i, index);
	}
		break;
	case APERTIUM_POSTCHUNK: {
		PostchunkIndexType index = make_pair(files[0], files[1]);
		//boost::mutex::scoped_lock lock(mutexpo);
		Postchunk *i = objectBroker->PostchunkPool.request(index);
		i->postchunk(in, out);
		objectBroker->PostchunkPool.release(i, index);
	}
		break;

	case APERTIUM_PRETRANSFER: {
		PreTransfer *i = objectBroker->PreTransferPool.request();
		//boost::mutex::scoped_lock lock(mutexpr);
		i->processStream(in, out);
		objectBroker->PreTransferPool.release(i);
	}
		break;

	case APERTIUM_TAGGER: {
		HMMIndexType index = files[0];
		//boost::mutex::scoped_lock lock(mutexta);
		HMM *i = objectBroker->HMMPool.request(index);
		i->tagger(in, out, false);
		objectBroker->HMMPool.release(i, index);
	}
		break;

	case APERTIUM_TRANSFER: {
		TransferIndexType index = files;
		Transfer *i = NULL;
		{
		//boost::mutex::scoped_lock lock(mutexlt);
		i = objectBroker->TransferPool.request(index);

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
		}
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

		FSTProcessor *i = NULL;
		{
		//boost::mutex::scoped_lock lock(mutexlt);
		i = objectBroker->FSTProcessorPool.request(index);

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
		}

		objectBroker->FSTProcessorPool.release(i, index);

	}
		break;

	default:
		cerr << "unknown code for " << program << endl;
	}

	fclose(out);
	wstring ret(outptr, outsize);

	fclose(in);

	return (ret);
}
