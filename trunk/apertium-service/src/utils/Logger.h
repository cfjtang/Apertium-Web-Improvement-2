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

#ifndef LOGGER_H_
#define LOGGER_H_

#include <iostream>
#include <boost/thread.hpp>
#include <boost/unordered/unordered_map.hpp>
#include <boost/thread/shared_mutex.hpp>
#include <boost/thread/locks.hpp>


enum MessageType { DEBUG, INFO, NOTICE, WARNING, ERR };

class Logger {
public:
	static Logger *Instance();
	virtual ~Logger();

    void trace(MessageType, const std::string);

    void setVerbosity(int);
    int getVerbosity();

private:
	Logger();
	static Logger *instance;

	static boost::shared_mutex instanceMutex;

	int verbosity;

	typedef boost::unordered_map<MessageType, int32_t> ColorMapType;
	ColorMapType color;
};

#endif /* LOGGER_H_ */
