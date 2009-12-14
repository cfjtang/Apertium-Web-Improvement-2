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
 *
 */

#ifndef THREADSAFEOBJECTPOOL_H_
#define THREADSAFEOBJECTPOOL_H_

//#include "ThreadSafeQueue.h"
#include <queue>

#include <boost/thread.hpp>
#include <boost/thread/shared_mutex.hpp>
#include <boost/thread/locks.hpp>

#include <boost/date_time/posix_time/posix_time.hpp>

using namespace std;

template <class T> class Container {
public:
	Container<T>(T *p) : ptr(p) { }
	virtual ~Container<T>() { }

	class ObjectUsageStats {
	public:
		ObjectUsageStats() : accessesCount(0) {
			timeCreation = boost::posix_time::second_clock::universal_time();
			timeLastAccess = timeCreation;
		}

		virtual ~ObjectUsageStats() { }

		ObjectUsageStats(ObjectUsageStats &o) {
			boost::shared_lock<boost::shared_mutex> lock(o.mutex);
			timeCreation = o.timeCreation;
			timeLastAccess = o.timeLastAccess;
			accessesCount = o.accessesCount;
		}

		void notifyAccess() {
			boost::unique_lock<boost::shared_mutex> lock(mutex);
			timeLastAccess = boost::posix_time::second_clock::universal_time();
			accessesCount += 1;
		}

		boost::posix_time::ptime getTimeCreation() {
			boost::shared_lock<boost::shared_mutex> lock(mutex);
			return timeCreation;
		}

		boost::posix_time::ptime getTimeLastAccess() {
			boost::shared_lock<boost::shared_mutex> lock(mutex);
			return timeLastAccess;
		}

		unsigned int getAccessesCount() {
			boost::shared_lock<boost::shared_mutex> lock(mutex);
			return accessesCount;
		}

	private:
		boost::posix_time::ptime timeCreation;
		boost::posix_time::ptime timeLastAccess;
		unsigned int accessesCount;

		boost::shared_mutex mutex;
	};

	ObjectUsageStats getObjectUsageStats() {
		typename Container<T>::ObjectUsageStats ret(stats);
		return ret;
	}

	T *getPtr() {
		stats.notifyAccess();
		return ptr;
	}

private:
	T *ptr;

	ObjectUsageStats stats;
};


template <class T> class ThreadSafeObjectPool {
public:
	ThreadSafeObjectPool<T>();
	virtual ~ThreadSafeObjectPool<T>();

	T *construct();
	void add(T*);

private:
	list<Container<T>*> pool;
	boost::shared_mutex mutex;
};

template <class T> ThreadSafeObjectPool<T>::ThreadSafeObjectPool() { }

template <class T> ThreadSafeObjectPool<T>::~ThreadSafeObjectPool() {
	boost::unique_lock<boost::shared_mutex> lock(mutex);
	while (!pool.empty()) {
		Container<T> *c = pool.front();
		pool.pop_front();
		T *ptr = c->getPtr();
		delete c;
		delete ptr;
	}
}

template <class T> T* ThreadSafeObjectPool<T>::construct() {
	T* ret = new T();
	add(ret);
    return ret;
}

template <class T> void ThreadSafeObjectPool<T>::add(T *o) {
	Container<T> *c = new Container<T>(o);
	{
		boost::unique_lock<boost::shared_mutex> lock(mutex);
		pool.push_back(c);
	}
}

#endif /* THREADSAFEOBJECTPOOL_H_ */
