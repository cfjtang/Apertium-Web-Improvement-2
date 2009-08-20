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

#ifndef STATISTICS_H_
#define STATISTICS_H_

#include <iostream>
#include <boost/thread.hpp>
#include <boost/unordered/unordered_map.hpp>
#include <boost/thread/shared_mutex.hpp>
#include <boost/thread/locks.hpp>

#include "core/ResourceBroker.h"

using namespace std;

class Statistics {
public:
	Statistics(ResourceBroker&);
	virtual ~Statistics();

	void notifyTranslationRequest(string, string);
	void notifyProgramExecutionRequest(Program&);

	typedef boost::unordered_map<pair<string, string>, unsigned int> PairInvocationsMapType;
	typedef boost::unordered_map<Program, unsigned int> ProgramInvocationsMapType;

	PairInvocationsMapType getPairs();
	ProgramInvocationsMapType getPrograms();

private:
	ResourceBroker *resourceBroker;

	PairInvocationsMapType pairs;
	boost::shared_mutex pairsMutex;

	ProgramInvocationsMapType programs;
	boost::shared_mutex programsMutex;
};

#endif /* STATISTICS_H_ */
