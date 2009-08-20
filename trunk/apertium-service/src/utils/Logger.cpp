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
 * The Logger class is used for logging purposes.
 */

#include "Logger.h"

#include <iostream>
#include <string>

#include <boost/date_time/posix_time/posix_time.hpp>

using namespace boost::posix_time;

Logger *Logger::instance = NULL;
boost::shared_mutex Logger::instanceMutex;

/**
 * This class implements the Singleton pattern
 */
Logger *Logger::Instance() {
	Logger *ret = NULL;
	{
		boost::upgrade_lock<boost::shared_mutex> lock(instanceMutex);
		if (!instance) {
			boost::upgrade_to_unique_lock<boost::shared_mutex> uniqueLock(lock);
			instance = new Logger();
		}
		ret = instance;
	}
	return (ret);
}

Logger::Logger() : verbosity(0) {
	color[ERR] = 31;
	color[INFO] = 32;
	color[NOTICE] = 33;
	color[WARNING] = 35;
	color[DEBUG] = 36;
}

Logger::~Logger() {
	boost::upgrade_lock<boost::shared_mutex> lock(instanceMutex);
	if (instance) {
		boost::upgrade_to_unique_lock<boost::shared_mutex> uniqueLock(lock);
		instance = NULL;
	}
}

/**
 * Writes the diagnostic message.
 */
void Logger::trace(MessageType messageType ///< Type of message
		, const std::string msg /// Message
		) {
	boost::upgrade_lock<boost::shared_mutex> lock(instanceMutex);
	std::stringstream ss;

	switch (messageType) {
	case INFO:
		ss << "[\033[" << color[INFO] << ";1mINFO\033[0m]";
		break;
	case NOTICE:
		ss << "[\033[" << color[NOTICE] << ";1mNOTICE\033[0m]";
		break;
	case WARNING:
		ss << "[\033[" << color[WARNING] << ";1mWARNING\033[0m]";
		break;
	case ERR:
		ss << "[\033[" << color[ERR] << ";1mERR\033[0m]";
		break;
	case DEBUG:
		ss << "[\033[" << color[DEBUG] << ";1mDEBUG\033[0m]";
		break;
	}

	ptime now = second_clock::local_time();
	ss << ": " << now << " - " << msg;

	if (verbosity > 1 || messageType != DEBUG) {
		boost::upgrade_to_unique_lock<boost::shared_mutex> uniqueLock(lock);
		std::cout << ss.str() << std::endl;
	}
}

void Logger::setVerbosity(int v) {
	verbosity = v;
}

int Logger::getVerbosity() {
	return verbosity;
}
