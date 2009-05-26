#ifndef THREADSAFEQUEUE_H_
#define THREADSAFEQUEUE_H_

#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/thread/condition.hpp>
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
    boost::mutex mutex;
    std::queue<T> q;
};

template <class T> ThreadSafeQueue<T>::ThreadSafeQueue() { }

template <class T> ThreadSafeQueue<T>::~ThreadSafeQueue() { }

template <class T> void ThreadSafeQueue<T>::push(T e) {
        boost::mutex::scoped_lock lock(mutex);
        q.push(e);
}

template <class T> T ThreadSafeQueue<T>::pop() {
        boost::mutex::scoped_lock lock(mutex);
        T ret = q.front();
        q.pop();
        return(ret);
}

template <class T> T ThreadSafeQueue<T>::front() {
        boost::mutex::scoped_lock lock(mutex);
        T ret = q.front();
        return(ret);
}

template <class T> bool ThreadSafeQueue<T>::empty() {
        boost::mutex::scoped_lock lock(mutex);
        bool ret = q.empty();
        return(ret);
}

#endif /* THREADSAFEQUEUE_H_ */
