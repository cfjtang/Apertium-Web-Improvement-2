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
	if (instance != NULL) {
		instance = NULL;
	}
}

void Logger::trace(MessageType messageType, const std::string msg) {
	std::stringstream ss;

	switch (messageType) {
	case NOTICE:
		ss << "[NOTICE] ";
	case WARNING:
		ss << "[WARNING] ";
	case ERR:
		ss << "[ERROR] ";
	case INFO:
		ss << "[INFO] ";
	}

	ss << msg << std::endl;

	{
		boost::mutex::scoped_lock Lock(instanceMutex);
		std::cout << ss;
	}
}
