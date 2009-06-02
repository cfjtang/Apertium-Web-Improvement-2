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

#ifndef OBJECTBROKER_H_
#define OBJECTBROKER_H_

#include <lttoolbox/compiler.h>
#include <lttoolbox/fst_processor.h>
#include <lttoolbox/my_stdio.h>
#include <lttoolbox/lt_locale.h>
#include <lttoolbox/match_exe.h>
#include <lttoolbox/match_state.h>

#include <apertium/tagger.h>
#include <apertium/tsx_reader.h>
#include <apertium/string_utils.h>
#include <apertium/hmm.h>

#include <apertium/tagger_utils.h>
#include <apertium/tagger_word.h>
#include <apertium/tagger_data.h>
#include <apertium/transfer.h>
#include <apertium/transfer_mult.h>
#include <apertium/interchunk.h>
#include <apertium/postchunk.h>

#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/condition.hpp>

#include <boost/unordered/unordered_map.hpp>

#include <iostream>

#include "PreTransfer.h"
#include "ThreadSafeObjectPool.h"

using namespace std;

enum FSTProcessorTask { ANALYSIS, GENERATION, POSTGENERATION, TRANSLITERATION };

// enum TaggerMode { UNKNOWN_MODE, TRAIN_MODE, TAGGER_MODE, RETRAIN_MODE, TAGGER_SUPERVISED_MODE,
//	TRAIN_SUPERVISED_MODE, RETRAIN_SUPERVISED_MODE, TAGGER_EVAL_MODE, TAGGER_FIRST_MODE };

typedef pair<FSTProcessorTask, string> FSTProcessorIndexType;
typedef string HMMIndexType;
typedef std::vector<string> TaggerIndexType;
typedef std::vector<string> TransferIndexType;
typedef pair<string, string> InterchunkIndexType;
typedef pair<string, string> PostchunkIndexType;
typedef pair<string, string> TransferMultIndexType;

template <class T> class NonIndexedObjectPool {
public:
	NonIndexedObjectPool() { }

	virtual ~NonIndexedObjectPool() { }

	T *request() {
		T *ret = NULL;
		bool isNew = false;
		boost::mutex::scoped_lock lock(mutex);
		if (poolQueue.empty()) {
			isNew = true;
		} else {
			ret = poolQueue.front();
			poolQueue.pop();
		}
		lock.unlock();
		if (isNew) {
			ret = pool.construct();
		}
		return(ret);
	}

	void release(T *i) {
		boost::mutex::scoped_lock lock(mutex);
		poolQueue.push(i);
	}

private:
	T *getNewInstance();

	ThreadSafeObjectPool<T> pool;
	queue<T*> poolQueue;
	boost::mutex mutex;
};

template <class T, class I> class ObjectPool {
public:
	ObjectPool() { }

	virtual ~ObjectPool() { }

	typedef boost::unordered_map<I, queue<T*> > PoolMapType;

	T *request(I index) {
		T *ret = NULL;
		bool isNew = false;
		boost::mutex::scoped_lock lock(mutex);

		typename PoolMapType::iterator it = poolMap.find(index);
		if (it == poolMap.end()) {
			isNew = true;
		} else {
			queue<T*> *q = &(it->second);
			if (q->empty()) {
				isNew = true;
			} else {
				ret = q->front();
				q->pop();
			}
		}
		lock.unlock();
		if (isNew) {
			ret = getNewInstance(index);
		}
		return(ret);
	}

	void release(T *i, I index) {
		boost::mutex::scoped_lock lock(mutex);
		typename PoolMapType::iterator it = poolMap.find(index);;
		if (it == poolMap.end()) {
			queue<T*> q;
			q.push(i);
			poolMap[index] = q;
		} else {
			queue<T*> *q = &(it->second);
			q->push(i);
		}
	}

private:
	T *getNewInstance(I);

	ThreadSafeObjectPool<T> pool;
	PoolMapType poolMap;
	boost::mutex mutex;
};

class ObjectBroker {
public:
	static ObjectBroker *Instance();

	NonIndexedObjectPool<PreTransfer> PreTransferPool;

	ObjectPool<FSTProcessor, FSTProcessorIndexType> FSTProcessorPool;
	ObjectPool<HMM, HMMIndexType> HMMPool;
	ObjectPool<Transfer, TransferIndexType> TransferPool;
	ObjectPool<Interchunk, InterchunkIndexType> InterchunkPool;
	ObjectPool<Postchunk, PostchunkIndexType> PostchunkPool;
	ObjectPool<TransferMult, TransferMultIndexType> TransferMultPool;

private:
	ObjectBroker();
	virtual ~ObjectBroker();

	static ObjectBroker *instance;
};

#endif /* OBJECTBROKER_H_ */
