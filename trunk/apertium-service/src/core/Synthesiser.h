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
 * The class Translate is used to execute the sequence of step required by
 * a translation task by using the informations contained in Modes files.
 */

#ifndef SYNTHESISER_H_
#define SYNTHESISER_H_

#include "config.h"

#if defined(HAVE_IRSTLM)

#include <iostream>
#include <wchar.h>

#include "core/ResourceBroker.h"
#include "utils/ConfigurationManager.h"

class Synthesiser {
public:
	static std::string synthesise(ResourceBroker&, ConfigurationManager&, std::vector<std::string>&, std::string, std::string);
};

#endif

#endif
