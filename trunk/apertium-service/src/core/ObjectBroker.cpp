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

ObjectBroker *ObjectBroker::instance = NULL;

ObjectBroker *ObjectBroker::Instance() {
	if (!instance)
		instance = new ObjectBroker();
	return(instance);
}

ObjectBroker::ObjectBroker() {

}

ObjectBroker::~ObjectBroker() {

}

template <> PreTransfer *NonIndexedObjectPool<PreTransfer>::getNewInstance() {
	PreTransfer *ret = pool.construct();
	return(ret);
}

template <> FSTProcessor *ObjectPool<FSTProcessor, FSTProcessorIndexType>::getNewInstance(FSTProcessorIndexType index) {
	FSTProcessor *ret = pool.construct();

	FILE *fp = fopen((index.second).c_str(), "r");
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

template <> HMM *ObjectPool<HMM, HMMIndexType>::getNewInstance(HMMIndexType index) {
	HMM *ret = NULL;

	// XXX: MUST HANDLE THIS OBJECT TOO
	TaggerData *td = new TaggerData();
	FILE *ftemp = fopen(index.c_str(), "rb");
	td->read(ftemp);
	fclose(ftemp);

	ret = pool.construct(td);

	return(ret);
}

template <> Transfer *ObjectPool<Transfer, TransferIndexType>::getNewInstance(TransferIndexType index) {
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

template <> Interchunk *ObjectPool<Interchunk, InterchunkIndexType>::getNewInstance(InterchunkIndexType index) {
	Interchunk *ret = pool.construct();
	ret->read(index.first, index.second);
	return(ret);
}

template <> Postchunk *ObjectPool<Postchunk, PostchunkIndexType>::getNewInstance(PostchunkIndexType index) {
	Postchunk *ret = pool.construct();
	ret->read(index.first, index.second);
	return(ret);
}

template <> TransferMult *ObjectPool<TransferMult, TransferMultIndexType>::getNewInstance(TransferMultIndexType index) {
	TransferMult *ret = pool.construct();
	ret->read(index.first, index.second);
	return(ret);
}
