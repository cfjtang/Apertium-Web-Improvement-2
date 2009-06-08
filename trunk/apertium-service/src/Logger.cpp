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

#include "Logger.h"

#include <iostream>
#include <string>
#include <fstream>
#include <iomanip>

#include <stdio.h>

#include <sys/time.h>
#include <time.h>

#include <syslog.h>

Logger *Logger::instance = NULL;
boost::mutex Logger::instanceMutex;

Logger *Logger::Instance() {
	boost::mutex::scoped_lock Lock(instanceMutex);
	if (!instance)
		instance = new Logger();
	return (instance);
}

Logger::Logger() {
}

Logger::~Logger() {
	boost::mutex::scoped_lock Lock(instanceMutex);
	instance = NULL;
}

using namespace std;

void Logger::trace(MessageType messageType, const std::string msg) {
	boost::mutex::scoped_lock Lock(instanceMutex);
	if (destType == CONSOLE || destType == FILE) {
		struct tm* pTime;

#ifdef _WIN32
		__int64 now;
		_time64(&now);
		pTime = _localtime64(&now);
		unsigned millis = unsigned((now / 1000) % 1000);
#else
		struct timeval time;
		gettimeofday(&time, NULL);
		pTime = localtime(&time.tv_sec);
		unsigned millis = time.tv_usec / 1000;
#endif

		char timeString[256];
		strftime(timeString, sizeof(timeString), "%y-%m-%d %H:%M:%S", pTime);
		(*destStream) << "[" << timeString << "." << setw(3) << setfill('0') << millis << setw(0) << "]: ";
		(*destStream) << msg << endl;
		destStream->flush();
	} else {
		int prio;

		switch (messageType) {
		case NOTICE:
			prio = LOG_NOTICE;
		case WARNING:
			prio = LOG_WARNING;
		case ERR:
			prio = LOG_ERR;
		case INFO:
			prio = LOG_INFO;
		}

		syslog(prio, "%s", msg.c_str());
	}
}
