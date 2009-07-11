#ifndef THREADSAFEQUEUE_H_
#define THREADSAFEQUEUE_H_

#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/condition.hpp>
#include <boost/thread/shared_mutex.hpp>
#include <boost/thread/locks.hpp>

#include <queue>

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
        boost::upgrade_lock<boost::shared_mutex> lock(mutex);
        T ret = q.front();
        boost::upgrade_to_unique_lock<boost::shared_mutex> uniqueLock(lock);
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
