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

#ifndef THREADSAFEOBJECTPOOL_H_
#define THREADSAFEOBJECTPOOL_H_

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
#include <apertium/transfer.h>
#include <apertium/interchunk.h>
#include <apertium/postchunk.h>

#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/condition.hpp>

#include <boost/pool/pool_alloc.hpp>
#include <boost/pool/object_pool.hpp>

#include "ThreadSafeQueue.h"

using namespace std;

template <class T> class ThreadSafeObjectPool {
public:
	ThreadSafeObjectPool<T>();
	virtual ~ThreadSafeObjectPool<T>();

	T *construct();
	HMM* construct(TaggerData *);

private:
	ThreadSafeQueue<T*> queue;
};

template <class T> ThreadSafeObjectPool<T>::ThreadSafeObjectPool() { }

template <class T> ThreadSafeObjectPool<T>::~ThreadSafeObjectPool() {
	while (!queue.empty()) {
		T* ptr = queue.pop();
		delete ptr;
		//queue.pop();
	}
}

template <class T> T* ThreadSafeObjectPool<T>::construct() {
	T* ret = NULL;
    ret = new T();
    queue.push(ret);
    return ret;
}

template <class T> HMM* ThreadSafeObjectPool<T>::construct(TaggerData *ptr) {
	T* ret = NULL;
    ret = new T(ptr);
    queue.push(ret);
    return ret;
}

#endif /* THREADSAFEOBJECTPOOL_H_ */
