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

#include "ObjectBroker.h"
#include "utils/Logger.h"

ObjectBroker::ObjectBroker() { }

ObjectBroker::~ObjectBroker() { }

ObjectPool::ObjectPool() {
	objectsCount = 0;
}

ObjectPool::~ObjectPool() { }

unsigned int ObjectPool::getObjectsCount() {
	boost::shared_lock<boost::shared_mutex> lock(countMutex);
	return (objectsCount);
}

void ObjectPool::incObjectsCount() {
	boost::unique_lock<boost::shared_mutex> lock(countMutex);
	++objectsCount;
}

void ObjectPool::decObjectsCount() {
	boost::unique_lock<boost::shared_mutex> lock(countMutex);
	--objectsCount;
}

void ObjectPool::checkFile(fs::path p) {
	if (!fs::exists(p)) {
		string msg = "File " + p.string() + " requested but doesn't exist.";
		Logger::Instance()->trace(ERR, msg);
		throw ApertiumRuntimeException(msg);
	}
}

HMMWrapper::HMMWrapper() {
	td = NULL;
	hmm = NULL;
}

HMMWrapper::~HMMWrapper() {
	delete hmm;
	delete td;
}

void HMMWrapper::read(string index) {
	td = new TaggerData();
	FILE *ftemp = fopen(index.c_str(), "rb");

	if (ftemp == NULL)
		throw ApertiumRuntimeException(::strerror(errno));

	td->read(ftemp);
	fclose(ftemp);

	hmm = new HMM(td);
}

HMM *HMMWrapper::getHmm() {
	return hmm;
}

template <> PreTransfer *NonIndexedObjectPool<PreTransfer>::getNewInstance() {

	Logger::Instance()->trace(DEBUG, "NonIndexedObjectPool<PreTransfer>::getNewInstance();");

	PreTransfer *ret = pool.construct();
	return(ret);
}

template <> Deformat *NonIndexedObjectPool<Deformat>::getNewInstance() {

	Logger::Instance()->trace(DEBUG, "NonIndexedObjectPool<Deformat>::getNewInstance();");

	Deformat *ret = pool.construct();
	return(ret);
}

template <> Reformat *NonIndexedObjectPool<Reformat>::getNewInstance() {

	Logger::Instance()->trace(DEBUG, "NonIndexedObjectPool<Reformat>::getNewInstance();");

	Reformat *ret = pool.construct();
	return(ret);
}

template <> FSTProcessor *IndexedObjectPool<FSTProcessor, FSTProcessorIndexType>::getNewInstance(FSTProcessorIndexType index) {

	Logger::Instance()->trace(DEBUG, "ObjectPool<FSTProcessor, FSTProcessorIndexType>::getNewInstance(FSTProcessorIndexType index);");

	checkFile(index.second);

	FSTProcessor *ret = pool.construct();

	FILE *fp = fopen((index.second).c_str(), "r");

	if (fp == NULL)
		throw ApertiumRuntimeException(::strerror(errno));

	ret->load(fp);
	fclose(fp);

	FSTProcessorTask task = index.first;
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

template <> HMMWrapper *IndexedObjectPool<HMMWrapper, HMMIndexType>::getNewInstance(HMMIndexType index) {

	Logger::Instance()->trace(DEBUG, "ObjectPool<HMMWrapper, HMMIndexType>::getNewInstance(HMMIndexType index);");

	checkFile(index);

	HMMWrapper *ret = pool.construct();
	ret->read(index);

	return(ret);
}

template <> Transfer *IndexedObjectPool<Transfer, TransferIndexType>::getNewInstance(TransferIndexType index) {

	Logger::Instance()->trace(DEBUG, "ObjectPool<Transfer, TransferIndexType>::getNewInstance(TransferIndexType index);");

	for (TransferIndexType::iterator it = index.begin(); it != index.end(); ++it)
		checkFile(*it);

	Transfer *ret = pool.construct();
	switch (index.size()) {
	case 3:
		ret->read(index[0], index[1], index[2]);
		break;
	case 2:
		ret->read(index[0], index[1]);
		break;
	}

	return(ret);
}

template <> Interchunk *IndexedObjectPool<Interchunk, InterchunkIndexType>::getNewInstance(InterchunkIndexType index) {

	Logger::Instance()->trace(DEBUG, "ObjectPool<Interchunk, InterchunkIndexType>::getNewInstance(InterchunkIndexType index);");

	checkFile(index.first);
	checkFile(index.second);

	Interchunk *ret = pool.construct();
	ret->read(index.first, index.second);
	return(ret);
}

template <> Postchunk *IndexedObjectPool<Postchunk, PostchunkIndexType>::getNewInstance(PostchunkIndexType index) {

	Logger::Instance()->trace(DEBUG, "ObjectPool<Postchunk, PostchunkIndexType>::getNewInstance(PostchunkIndexType index);");

	checkFile(index.first);
	checkFile(index.second);

	Postchunk *ret = pool.construct();
	ret->read(index.first, index.second);
	return(ret);
}

template <> TransferMult *IndexedObjectPool<TransferMult, TransferMultIndexType>::getNewInstance(TransferMultIndexType index) {

	Logger::Instance()->trace(DEBUG, "ObjectPool<TransferMult, TransferMultIndexType>::getNewInstance(TransferMultIndexType index);");

	checkFile(index.first);
	checkFile(index.second);

	TransferMult *ret = pool.construct();
	ret->read(index.first, index.second);
	return(ret);
}

boost::mutex ObjectBroker::cgMutex;

template <> CG3::Grammar *IndexedObjectPool<CG3::Grammar, GrammarIndexType>::getNewInstance(GrammarIndexType index) {

	Logger::Instance()->trace(DEBUG, "ObjectPool<CG3::Grammar, GrammarIndexType>::getNewInstance(GrammarIndexType index);");

	checkFile(index);

	const char *codepage_default = ucnv_getDefaultName();
	const char *locale_default = "en_US_POSIX"; //uloc_getDefault();

	UFILE *ux_err = u_finit(stderr, locale_default, codepage_default);

	CG3::Grammar *ret = pool.construct();

	CG3::IGrammarParser *parser = new CG3::BinaryGrammar(*ret, ux_err);

	(*ret).ux_stderr = ux_err;
	CG3::Tag *tag_any = ret->allocateTag(stringbits[S_ASTERIK]);
	(*ret).tag_any = tag_any->hash;

	int pret = 0;

	{ // XXX
	boost::mutex::scoped_lock Lock(ObjectBroker::cgMutex);
	pret = parser->parse_grammar_from_file(index.data(), locale_default, codepage_default);
	} // XXX

	if (pret) {
		throw ApertiumRuntimeException("Error: Grammar could not be parsed.");
	}

	ret->reindex();

	delete parser;

	return(ret);
}

