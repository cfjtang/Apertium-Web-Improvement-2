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

#ifndef IRSTLM_RANKER_HH
#define IRSTLM_RANKER_HH

#include "config.h"

#if defined(HAVE_IRSTLM)

#include <lmtable.h>
#include <lmmacro.h>

#include "ranker.hh"

class lmtable;
class lmmacro;

class IRSTLMRanker: public Ranker {
public:
	IRSTLMRanker();
	IRSTLMRanker(const string& file_path);
	~IRSTLMRanker();
	inline void score(Hypothesis& h);
	// void rank(std::list<Hypothesis>& hypotheses);
private:
	lmtable *lmtb;
};

#endif

#endif
