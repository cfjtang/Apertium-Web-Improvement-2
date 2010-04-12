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
 * The FunctionMapper class is used for requesting an arbitrary resource from
 * the Resource Pool, using it and releasing it depending by a given instance
 * of the Program class.
 */

#ifndef FUNCTIONMAPPER_H_
#define FUNCTIONMAPPER_H_

#include "ResourceBroker.h"
#include "ModesManager.h"

#include <boost/unordered/unordered_map.hpp>

/**
 * The FunctionMapper class is used for requesting an arbitrary resource from
 * the Resource Pool, using it and releasing it depending by a given instance
 * of the Program class.
 */
class FunctionMapper {
public:
	FunctionMapper(ResourceBroker&);
	virtual ~FunctionMapper();

	void load(Program&, unsigned int = 0);
	std::wstring execute(Program&, std::wstring&, bool);

private:
	ResourceBroker *resourceBroker;

	enum TaskType { /*DEFORMAT, REFORMAT,*/ APERTIUM_INTERCHUNK, APERTIUM_MULTIPLE_TRANSLATIONS, APERTIUM_POSTCHUNK,
		APERTIUM_PRETRANSFER, APERTIUM_TAGGER, APERTIUM_TRANSFER, LT_PROC, CG_PROC, OTHER };

	typedef boost::unordered_map<std::string, TaskType> TaskMapType;
	TaskMapType task;
};

#endif /* FUNCTIONMAPPER_H_ */
