/**
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
 * The class ApertiumXMLRPCService implements a service using the XML-RPC protocol
 * and exposing Apertium's capabilities for translation and language detection.
 * XML-RPC is a remote procedure call protocol which uses XML to encode its calls
 * and HTTP as a transport mechanism.
 */

#ifndef RANKER_HH
#define RANKER_HH

#include "config.h"

#if defined(HAVE_IRSTLM)

#include <iostream>
#include <sstream>

#include "lmtable.h"
#include "lmmacro.h"
#include "hypotheses.hh"

class Ranker;
struct Hypothesis;

using namespace std;

class Ranker {
public:
	// Ranker(const string& file_path) {}
	Ranker() { }
	virtual ~Ranker() { }

	virtual void score(Hypothesis& h) = 0;
	virtual void rank(std::list<Hypothesis>& hypotheses) {
		for (std::list<Hypothesis>::iterator it = hypotheses.begin(); it
				!= hypotheses.end(); ++it) {
			score(*it);
		}

		hypotheses.sort();
		return;
	}
};

#endif

#endif
