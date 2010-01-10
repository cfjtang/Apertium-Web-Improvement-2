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

#include "Statistics.h"

#include <iostream>
#include <string>

Statistics::Statistics(ResourceBroker &rb) : resourceBroker(&rb) { }

Statistics::~Statistics() { }

void Statistics::notifyTranslationRequest(std::string srcLang, std::string destLang) {
	pair<std::string, std::string> p(srcLang, destLang);
	boost::unique_lock<boost::shared_mutex> uniqueLock(pairsMutex);
	if (pairs.find(p) == pairs.end()) {
		pairs[p] = 1;
	} else {
		pairs[p] += 1;
	}
}

void Statistics::notifyProgramExecutionRequest(Program &p) {
	boost::unique_lock<boost::shared_mutex> uniqueLock(programsMutex);
	if (programs.find(p) == programs.end()) {
		programs[p] = 1;
	} else {
		programs[p] += 1;
	}
}

Statistics::PairInvocationsMapType Statistics::getPairs() {
	boost::shared_lock<boost::shared_mutex> lock(pairsMutex);
	PairInvocationsMapType ret = pairs;
	return ret;
}

Statistics::ProgramInvocationsMapType Statistics::getPrograms() {
	boost::shared_lock<boost::shared_mutex> lock(programsMutex);
	ProgramInvocationsMapType ret = programs;
	return ret;
}
