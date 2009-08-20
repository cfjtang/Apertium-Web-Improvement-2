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

#ifndef THREADSAFEQUEUE_H_
#define THREADSAFEQUEUE_H_

#include <queue>

#include <boost/thread.hpp>
#include <boost/unordered/unordered_map.hpp>
#include <boost/thread/shared_mutex.hpp>
#include <boost/thread/locks.hpp>

template <typename T> class ThreadSafeQueue {
public:
	ThreadSafeQueue<T>();
	virtual ~ThreadSafeQueue<T>();

    void push(T);
    T pop();

    T front();

    bool empty();

private:
    boost::shared_mutex mutex;
    std::queue<T> q;
};

template <class T> ThreadSafeQueue<T>::ThreadSafeQueue() { }

template <class T> ThreadSafeQueue<T>::~ThreadSafeQueue() { }

template <class T> void ThreadSafeQueue<T>::push(T e) {
	boost::unique_lock<boost::shared_mutex> uniqueLock(mutex);
	q.push(e);
}

template <class T> T ThreadSafeQueue<T>::pop() {
	boost::unique_lock<boost::shared_mutex> uniqueLock(mutex);
	T ret = q.front();
	q.pop();
	return(ret);
}

template <class T> T ThreadSafeQueue<T>::front() {
	boost::shared_lock<boost::shared_mutex> lock(mutex);
	T ret = q.front();
	return(ret);
}

template <class T> bool ThreadSafeQueue<T>::empty() {
	boost::shared_lock<boost::shared_mutex> lock(mutex);
	bool ret = q.empty();
	return(ret);
}

#endif /* THREADSAFEQUEUE_H_ */
