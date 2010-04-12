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
 * The ResourceBroker class is an implementation of the Object Pool design pattern.
 * An object pool is a set of initialised objects that are kept ready to use,
 * rather than allocated and destroyed on demand. A client of the pool will
 * request an object from the pool and perform operations on the returned object.
 * When the client has finished with an object, it returns it to the pool, rather
 * than destroying it. It is a specific type of factory object.
 */

#include <boost/algorithm/string.hpp>

#include "ResourceBroker.h"
#include "utils/Logger.h"

#include "vislcg3/stdafx.h"
#include "vislcg3/Grammar.h"
#include "vislcg3/BinaryGrammar.h"
#include "vislcg3/ApertiumApplicator.h"
#include "vislcg3/MatxinApplicator.h"
#include "vislcg3/GrammarApplicator.h"

using namespace std;
using CG3::CG3Quit;

ResourceBroker::ResourceBroker(unsigned int ub) : /* PreTransferPool(ub), */ FormatPool(ub),
	FSTProcessorPool(ub), HMMPool(ub), TransferPool(ub), InterchunkPool(ub),
	PostchunkPool(ub), TransferMultPool(ub)
#if defined(HAVE_COMBINE)
	, IRSTLMRankerPool(ub), Case_Insensitive_Morph_MatcherPool(ub)
#endif
	, GrammarPool(ub) {
	upperBound = ub;
}

ResourceBroker::~ResourceBroker() { }

HMMWrapper::HMMWrapper() {
	td = NULL;
	hmm = NULL;
}

HMMWrapper::~HMMWrapper() {
	delete hmm;
	delete td;
}

void HMMWrapper::read(std::string index) {
	td = new TaggerData();
	FILE *ftemp = fopen(index.c_str(), "rb");

	if (ftemp == NULL)
		throw ApertiumRuntimeException(::strerror(errno));

	td->read(ftemp);
	fclose(ftemp);

	hmm = new HMM(td);
}

HMM *HMMWrapper::get() {
	return hmm;
}

CGApplicator::CGApplicator() {
	applicator = NULL;
	grammar = NULL;
}

CGApplicator::~CGApplicator() {
	delete applicator;
	delete grammar;
}

void CGApplicator::read(std::string index) {
	const char *codepage_default = ucnv_getDefaultName();
	const char *locale_default = "en_US_POSIX"; //uloc_getDefault();

	int pret = 0;

	UFILE *ux_err = u_finit(stderr, locale_default, codepage_default);

	//{ // XXX
	//boost::mutex::scoped_lock Lock(ResourceBroker::cgMutex);

	grammar = new CG3::Grammar();
	grammar->ux_stderr = ux_err;

	CG3::IGrammarParser *parser = new CG3::BinaryGrammar(*grammar, ux_err);

	pret = parser->parse_grammar_from_file(index.data(), locale_default, codepage_default);
	//grammar->reindex();

	delete parser;

	if (pret) {
		throw ApertiumRuntimeException("Error: Grammar could not be parsed.");
	}

	applicator = new CG3::ApertiumApplicator(ux_err);
	applicator->setNullFlush(false);
	applicator->setGrammar(grammar);
	//} // XXX
}

CG3::ApertiumApplicator *CGApplicator::get() {
	return applicator;
}

/*
template <> PreTransfer *NonIndexedObjectPool<PreTransfer>::getNewInstance(Program &p) {
	Logger::Instance()->trace(Logger::Debug, "NonIndexedObjectPool<PreTransfer>::getNewInstance();");

	PreTransfer *ret = pool.construct();
	return(ret);
}
*/

template <> Format *NonIndexedObjectPool<Format>::getNewInstance(Program &p) {
	Logger::Instance()->trace(Logger::Debug, "NonIndexedObjectPool<Format>::getNewInstance();");

	Format *ret = NULL;

	if (p.getProgramName() == "apertium-destxt") {
		ret = new TXTDeformat();
	} else if (p.getProgramName() == "apertium-retxt") {
		ret = new TXTReformat();
	} //else if (p.getProgramName() == "apertium-deshtml") {
	//	ret = new HTMLDeformat();
	//} else if (p.getProgramName() == "apertium-rehtml") {
	//	ret = new HTMLReformat();
	//}

	pool.add(ret);

	return(ret);
}

template <> FSTProcessor *NonIndexedObjectPool<FSTProcessor>::getNewInstance(Program &p) {
	Logger::Instance()->trace(Logger::Debug, "ObjectPool<FSTProcessor>::getNewInstance();");

	enum FSTProcessorTask { ANALYSIS, GENERATION, POSTGENERATION, TRANSLITERATION };

	std::vector<std::string> params;
	const std::string cl = p.getProgramName();

	boost::split(params, cl, boost::is_any_of("\t "));

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

	std::vector<std::string> fileNames = p.getFileNames();

	checkFile(fileNames[0]);

	FSTProcessor *ret = pool.construct();

	FILE *fp = fopen((fileNames[0]).c_str(), "r");

	if (fp == NULL)
		throw ApertiumRuntimeException(::strerror(errno));

	ret->load(fp);
	fclose(fp);

	switch (task) {
	case ANALYSIS:
		ret->initAnalysis();
		break;
	case GENERATION:
		ret->initGeneration();
		break;
	case POSTGENERATION:
	case TRANSLITERATION:
		ret->initPostgeneration();
		break;
	}

	return(ret);
}

template <> HMMWrapper *NonIndexedObjectPool<HMMWrapper>::getNewInstance(Program &p) {
	Logger::Instance()->trace(Logger::Debug, "ObjectPool<HMMWrapper>::getNewInstance();");

	std::vector<std::string> fileNames = p.getFileNames();

	checkFile(fileNames[0]);

	HMMWrapper *ret = pool.construct();
	ret->read(fileNames[0]);

	return(ret);
}

template <> Transfer *NonIndexedObjectPool<Transfer>::getNewInstance(Program &p) {
	Logger::Instance()->trace(Logger::Debug, "ObjectPool<Transfer>::getNewInstance();");

	std::vector<std::string> fileNames = p.getFileNames();

	for (std::vector<std::string>::iterator it = fileNames.begin(); it != fileNames.end(); ++it)
		checkFile(*it);

	Transfer *ret = pool.construct();
	switch (fileNames.size()) {
	case 3:
		ret->read(fileNames[0], fileNames[1], fileNames[2]);
		break;
	case 2:
		ret->read(fileNames[0], fileNames[1]);
		break;
	}

	return(ret);
}

template <> Interchunk *NonIndexedObjectPool<Interchunk>::getNewInstance(Program &p) {
	Logger::Instance()->trace(Logger::Debug, "ObjectPool<Interchunk>::getNewInstance();");

	std::vector<std::string> fileNames = p.getFileNames();

	checkFile(fileNames[0]);
	checkFile(fileNames[1]);

	Interchunk *ret = pool.construct();
	ret->read(fileNames[0], fileNames[1]);
	return(ret);
}


template <> Postchunk *NonIndexedObjectPool<Postchunk>::getNewInstance(Program &p) {
	Logger::Instance()->trace(Logger::Debug, "ObjectPool<Postchunk>::getNewInstance();");

	std::vector<std::string> fileNames = p.getFileNames();

	checkFile(fileNames[0]);
	checkFile(fileNames[1]);

	Postchunk *ret = pool.construct();
	ret->read(fileNames[0], fileNames[1]);
	return(ret);
}


template <> TransferMult *NonIndexedObjectPool<TransferMult>::getNewInstance(Program &p) {
	Logger::Instance()->trace(Logger::Debug, "ObjectPool<TransferMult>::getNewInstance();");

	std::vector<std::string> fileNames = p.getFileNames();

	checkFile(fileNames[0]);
	checkFile(fileNames[1]);

	TransferMult *ret = pool.construct();
	ret->read(fileNames[0], fileNames[1]);
	return(ret);
}

#if defined(HAVE_COMBINE)
template <> IRSTLMRanker *NonIndexedObjectPool<IRSTLMRanker>::getNewInstance(Program &p) {
	Logger::Instance()->trace(Logger::Debug, "ObjectPool<IRSTLMRanker>::getNewInstance();");

	std::vector<std::string> fileNames = p.getFileNames();

	checkFile(fileNames[0]);

	IRSTLMRanker *ret = new IRSTLMRanker(fileNames[0]);
	pool.add(ret);

	return(ret);
}

template <> Case_Insensitive_Morph_Matcher *NonIndexedObjectPool<Case_Insensitive_Morph_Matcher>::getNewInstance(Program &p) {
	Logger::Instance()->trace(Logger::Debug, "ObjectPool<Case_Insensitive_Morph_Matcher>::getNewInstance();");

	std::vector<std::string> fileNames = p.getFileNames();

	checkFile(fileNames[0]);

	Case_Insensitive_Morph_Matcher *ret = pool.construct();
	ret->readBil(fileNames[0]);
	return(ret);
}
#endif

boost::mutex ResourceBroker::cgMutex;

template <> CGApplicator *NonIndexedObjectPool<CGApplicator>::getNewInstance(Program &p) {
	Logger::Instance()->trace(Logger::Debug, "ObjectPool<CG3::Grammar>::getNewInstance();");

	std::vector<std::string> fileNames = p.getFileNames();

	checkFile(fileNames[0]);

	CGApplicator *ret = pool.construct();
	ret->read(fileNames[0]);

	return(ret);
}

template <> Process *NonIndexedObjectPool<Process>::getNewInstance(Program &p) {
	Logger::Instance()->trace(Logger::Debug, "ObjectPool<Process>::getNewInstance();");

	Process *ret = new Process(p.getProgram(), p.getParameters());
	pool.add(ret);

	return(ret);
}
