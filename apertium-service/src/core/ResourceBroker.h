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

#ifndef RESOURCEBROKER_H_
#define RESOURCEBROKER_H_

#include "config.h"

#include <iostream>
#include <list>

#include <stdlib.h>

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

#include <boost/bind.hpp>
#include <boost/thread.hpp>
#include <boost/utility.hpp>

#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/condition.hpp>
#include <boost/thread/shared_mutex.hpp>
#include <boost/thread/locks.hpp>

#include <boost/unordered/unordered_map.hpp>
#include <boost/filesystem.hpp>

#include "PreTransfer.h"
#include "ThreadSafeObjectPool.h"

#include "core/ModesManager.h"

#include "Process.h"

#include "core/vislcg3/stdafx.h"
#include "core/vislcg3/icu_uoptions.h"
#include "core/vislcg3/Grammar.h"
#include "core/vislcg3/BinaryGrammar.h"
#include "core/vislcg3/ApertiumApplicator.h"

#if defined(HAVE_COMBINE)
#include <apertium-combine/alignment.hh>
#include <apertium-combine/hypotheses.hh>
#include <apertium-combine/irstlm_ranker.hh>
#include <apertium-combine/max_conseq_aligner.hh>
#include <apertium-combine/parallel_scan_generator.hh>
#include <apertium-combine/case_insensitive_morph_matcher.hh>
#endif

#include "format/Format.h"

#include "format/TXTDeformat.h"
#include "format/TXTReformat.h"

//#include "format/HTMLDeformat.h"
//#include "format/HTMLReformat.h"

#include "utils/Logger.h"

#include "ApertiumRuntimeException.h"

namespace fs = boost::filesystem;

template <class T> class ObjectPool : private boost::noncopyable {
public:
	ObjectPool(unsigned int ub = 0) : upperBound(ub) { };
	virtual ~ObjectPool() { };

	virtual unsigned int getObjectsCount() = 0;

	unsigned int getUpperBound() {
		return (upperBound);
	}

	virtual T* acquire(Program&) = 0;
	virtual void release(T*, Program&) = 0;

	enum ObjectPoolType { INDEXED, NON_INDEXED };

	virtual ObjectPoolType getType() = 0;

protected:
	unsigned int upperBound;

	static void checkFile(fs::path p) {
		if (!fs::exists(p)) {
			std::string msg = "File " + p.string() + " requested but doesn't exist.";
			Logger::Instance()->trace(Logger::Err, msg);
			throw ApertiumRuntimeException(msg);
		}
	}
};

template <class T> class NonIndexedObjectPool : public ObjectPool<T> {
public:
	NonIndexedObjectPool(unsigned int u = 0) : ObjectPool<T>(u), objectsCount(0) { }

	virtual ~NonIndexedObjectPool() { }

	unsigned int getObjectsCount() {
		boost::shared_lock<boost::shared_mutex> lock(countMutex);
		return (objectsCount);
	}

	T *acquire(Program &p) {
		T *ret = NULL;

		boost::upgrade_lock<boost::shared_mutex> queueLock(queueMutex);
		boost::upgrade_lock<boost::shared_mutex> countLock(countMutex);

		bool empty = poolQueue.empty();

		if (empty && !(this->upperBound > 0 && objectsCount >= this->upperBound)) {

			{
				boost::upgrade_to_unique_lock<boost::shared_mutex> uniqueLock(countLock);
				objectsCount += 1;
			}
			queueLock.unlock();
			countLock.unlock();
			ret = getNewInstance(p);
		} else {

			while (empty && this->upperBound > 0 && objectsCount >= this->upperBound) {

				queueLock.unlock();
				countLock.unlock();
				{
					boost::mutex::scoped_lock monitorLock(monitor);
					queueNotEmpty.wait(monitorLock);
				}
				queueLock.lock();
				countLock.lock();

				empty = poolQueue.empty();
			}

			boost::upgrade_to_unique_lock<boost::shared_mutex> uniqueLock(queueLock);
			ret = poolQueue.front();
			poolQueue.pop_front();
		}

		return(ret);
	}

	void release(T *i, Program &p) {
		boost::unique_lock<boost::shared_mutex> queueLock(queueMutex);
		poolQueue.push_back(i);
		queueNotEmpty.notify_one();
	}

	typename ObjectPool<T>::ObjectPoolType getType() {
		return ObjectPool<T>::NON_INDEXED;
	}

private:
	T *getNewInstance(Program &p);

	ThreadSafeObjectPool<T> pool;

	std::list<T*> poolQueue;
	boost::shared_mutex queueMutex;

	unsigned int objectsCount;
	boost::shared_mutex countMutex;

	boost::condition queueNotEmpty;
	boost::mutex monitor;
};

template <class T> class IndexedObjectPool : public ObjectPool<T> {
public:
	typedef boost::unordered_map<Program, NonIndexedObjectPool<T>* > PoolMapType;

	IndexedObjectPool(unsigned int u = 0) : ObjectPool<T>(u) { }

	virtual ~IndexedObjectPool() {
		boost::unique_lock<boost::shared_mutex> mapLock(mapMutex);
		for (typename PoolMapType::iterator it = poolMap.begin(); it != poolMap.end(); ++it) {
			delete it->second;
		}
	}

	unsigned int getObjectsCount() {
		unsigned int ret = 0;
		boost::shared_lock<boost::shared_mutex> mapLock(mapMutex);
		for (typename PoolMapType::iterator it = poolMap.begin(); it != poolMap.end(); ++it) {
			ret += it->second->getObjectsCount();
		}
		return (ret);
	}

	T *acquire(Program &p) {
		T *ret = NULL;
		NonIndexedObjectPool<T> *ptr = NULL;
		{
			boost::upgrade_lock<boost::shared_mutex> mapLock(mapMutex);
			typename PoolMapType::iterator it = poolMap.find(p);
			if (it != poolMap.end()) {
				ptr = it->second;
			} else {
				ptr = new NonIndexedObjectPool<T> (this->upperBound);
				boost::upgrade_to_unique_lock<boost::shared_mutex> uniqueLock(mapLock);
				poolMap[p] = ptr;
			}
		}
		ret = ptr->acquire(p);
		return (ret);
	}

	void release(T *i, Program &p) {
		NonIndexedObjectPool<T> *ptr = NULL;
		{
			boost::upgrade_lock<boost::shared_mutex> mapLock(mapMutex);
			typename PoolMapType::iterator it = poolMap.find(p);
			if (it != poolMap.end()) {
				ptr = it->second;
			} else {
				// should not happen
				ptr = new NonIndexedObjectPool<T> (this->upperBound);
				boost::upgrade_to_unique_lock<boost::shared_mutex> uniqueLock(mapLock);
				poolMap[p] = ptr;
			}
		}
		ptr->release(i, p);
	}

	typename ObjectPool<T>::ObjectPoolType getType() {
		return ObjectPool<T>::INDEXED;
	}

private:

	PoolMapType poolMap;
	boost::shared_mutex mapMutex;
};

class HMMWrapper {
public:
	HMMWrapper();
	~HMMWrapper();

	void read(std::string);
	HMM *get();
private:
	HMM *hmm;
	TaggerData *td;
};

class CGApplicator {
public:
	CGApplicator();
	~CGApplicator();

	void read(std::string);
	CG3::ApertiumApplicator *get();
private:
	CG3::ApertiumApplicator *applicator;
	CG3::Grammar *grammar;
};

/**
 * The ResourceBroker class is an implementation of the Object Pool design pattern.
 * An object pool is a set of initialised objects that are kept ready to use,
 * rather than allocated and destroyed on demand. A client of the pool will
 * request an object from the pool and perform operations on the returned object.
 * When the client has finished with an object, it returns it to the pool, rather
 * than destroying it. It is a specific type of factory object.
 */
class ResourceBroker : boost::noncopyable {
public:
	ResourceBroker(unsigned int = 0);
	virtual ~ResourceBroker();

	//NonIndexedObjectPool<PreTransfer> PreTransferPool;

	IndexedObjectPool<Format> FormatPool;

	IndexedObjectPool<FSTProcessor> FSTProcessorPool;
	IndexedObjectPool<HMMWrapper> HMMPool;
	IndexedObjectPool<Transfer> TransferPool;

	IndexedObjectPool<Interchunk> InterchunkPool;
	IndexedObjectPool<Postchunk> PostchunkPool;
	IndexedObjectPool<TransferMult> TransferMultPool;

#if defined(HAVE_COMBINE)
	IndexedObjectPool<IRSTLMRanker> IRSTLMRankerPool;
	IndexedObjectPool<Case_Insensitive_Morph_Matcher> Case_Insensitive_Morph_MatcherPool;
#endif

	IndexedObjectPool<CGApplicator> GrammarPool;
	IndexedObjectPool<Process> ProcessPool;

	static boost::mutex cgMutex;

private:
	unsigned int upperBound;
};

#endif /* RESOURCEBROKER_H_ */
