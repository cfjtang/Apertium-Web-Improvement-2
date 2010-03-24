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
 * The class Translator is used to execute the sequence of step required by
 * a translation task by using the informations contained in Modes files.
 */

#ifndef TRANSLATOR_H_
#define TRANSLATOR_H_

#include <iostream>
#include <wchar.h>

#include "core/ResourceBroker.h"
#include "core/ModesManager.h"

#include "utils/Statistics.h"

/**
 * The class Translate is used to execute the sequence of step required by
 * a translation task by using the informations contained in Modes files.
 */
class Translator {
public:
	enum ContentType { TEXT /*, HTML */ };

	static std::string translate(ResourceBroker&, ModesManager&, std::string&, ContentType, std::string, std::string, bool, Statistics* = NULL);
	static void eagerlyLoad(ResourceBroker&, ModesManager&, std::string, std::string, unsigned int);

private:
	static std::wstring deformat(ResourceBroker&, std::wstring&, ContentType);
	static std::wstring reformat(ResourceBroker&, std::wstring&, ContentType);
};

#endif /* TRANSLATOR_H_ */
