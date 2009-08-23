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

#ifndef LOGGER_H_
#define LOGGER_H_

#include "config.h"

#include <iostream>
#include <boost/thread.hpp>
#include <boost/unordered/unordered_map.hpp>
#include <boost/thread/shared_mutex.hpp>
#include <boost/thread/locks.hpp>

/**
 * The Logger class is used for logging purposes.
 */
class Logger {
public:
	static Logger *Instance();
	virtual ~Logger();

	enum MessageType { Debug, Info, Notice, Warning, Err };

    void trace(MessageType, const std::string);

    void setVerbosity(int);
    int getVerbosity();

    void setConsoleUsage(bool);

#if defined(HAVE_SYSLOG)
    void setSyslogUsage(bool);
#endif

private:
	Logger();
	static Logger *instance;
	static boost::shared_mutex instanceMutex;

	void traceConsole(MessageType, const std::string);

#if defined(HAVE_SYSLOG)
	void traceSyslog(MessageType, const std::string);
#endif

	int verbosity;

#if defined(HAVE_SYSLOG)
	bool useSyslog;
	boost::shared_mutex syslogMutex;
#endif
	bool useConsole;
	boost::shared_mutex consoleMutex;

	typedef boost::unordered_map<MessageType, int32_t> ColorMapType;
	ColorMapType color;
};

#endif /* LOGGER_H_ */
