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

#include "config.h"

#include "Logger.h"

#include <iostream>
#include <string>

#if defined(HAVE_SYSLOG)
#include <syslog.h>
#endif

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

Logger::Logger() : verbosity(1), useConsole(true) {
	color[Err] = 31;
	color[Info] = 32;
	color[Notice] = 33;
	color[Warning] = 35;
	color[Debug] = 36;

#if defined(HAVE_SYSLOG)

	useSyslog = false;

	::setlogmask(LOG_UPTO(LOG_DEBUG));
	::openlog(PACKAGE_NAME, LOG_PID|LOG_CONS|LOG_ODELAY, LOG_USER);
#endif
}

Logger::~Logger() {
	boost::upgrade_lock<boost::shared_mutex> lock(instanceMutex);
	if (instance) {
		boost::upgrade_to_unique_lock<boost::shared_mutex> uniqueLock(lock);
#if defined(HAVE_SYSLOG)
		::closelog();
#endif
		instance = NULL;
	}
}

/**
 * Writes the diagnostic message.
 */
void Logger::trace(MessageType messageType ///< Type of message
		, const std::string msg ///< Message
		) {

	bool print = false;

	switch (messageType) {
	case Debug:
		if (verbosity > 2)
			print = true;
		break;

	case Info:
	case Notice:
		if (verbosity > 1)
			print = true;
		break;

	case Warning:
	case Err:
		if (verbosity > 0)
			print = true;
		break;
	}

	if (print) {
		if (useConsole) {
			traceConsole(messageType, msg);
		}
#if defined(HAVE_SYSLOG)
		if (useSyslog) {
			traceSyslog(messageType, msg);
		}
#endif
	}

}

void Logger::traceConsole(MessageType messageType, const std::string msg) {
		std::stringstream ss;

		switch (messageType) {
		case Info:
			ss << "[\033[" << color[Info] << ";1mInfo\033[0m]";
			break;
		case Notice:
			ss << "[\033[" << color[Notice] << ";1mNotice\033[0m]";
			break;
		case Warning:
			ss << "[\033[" << color[Warning] << ";1mWarning\033[0m]";
			break;
		case Err:
			ss << "[\033[" << color[Err] << ";1mErr\033[0m]";
			break;
		case Debug:
			ss << "[\033[" << color[Debug] << ";1mDebug\033[0m]";
			break;
		}

		ptime now = second_clock::local_time();
		ss << ": " << now << " - " << msg;

		boost::unique_lock<boost::shared_mutex> lock(consoleMutex);
		std::cout << ss.str() << std::endl;
}

#if defined(HAVE_SYSLOG)
void Logger::traceSyslog(MessageType messageType, const std::string msg) {
	int severity;

	switch (messageType) {
	case Logger::Debug:
		severity = LOG_DEBUG;
		break;
	case Logger::Info:
		severity = LOG_INFO;
		break;
	case Logger::Notice:
		severity = LOG_INFO;
		break;
	case Logger::Warning:
		severity = LOG_WARNING;
		break;
	case Logger::Err:
		severity = LOG_ERR;
		break;
	default:
		severity = LOG_INFO;
		break;
	}

	boost::unique_lock<boost::shared_mutex> lock(syslogMutex);
	::syslog(LOG_USER | severity, "[%s] %s", PACKAGE_NAME, msg.data());
}
#endif

void Logger::setVerbosity(int v) {
	verbosity = v;
}

int Logger::getVerbosity() {
	return verbosity;
}

void Logger::setConsoleUsage(bool u) {
	useConsole = u;
}

#if defined(HAVE_SYSLOG)
void Logger::setSyslogUsage(bool u) {
	useSyslog = u;
}
#endif
