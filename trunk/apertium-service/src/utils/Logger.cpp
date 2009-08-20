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
	color[Err] = 31;
	color[Info] = 32;
	color[Notice] = 33;
	color[Warning] = 35;
	color[Debug] = 36;
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
	case Info:
		ss << "[\033[" << color[Info] << ";1mINFO\033[0m]";
		break;
	case Notice:
		ss << "[\033[" << color[Notice] << ";1mNOTICE\033[0m]";
		break;
	case Warning:
		ss << "[\033[" << color[Warning] << ";1mWARNING\033[0m]";
		break;
	case Err:
		ss << "[\033[" << color[Err] << ";1mERR\033[0m]";
		break;
	case Debug:
		ss << "[\033[" << color[Debug] << ";1mDEBUG\033[0m]";
		break;
	}

	ptime now = second_clock::local_time();
	ss << ": " << now << " - " << msg;

	if (verbosity > 1 || messageType != Debug) {
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
