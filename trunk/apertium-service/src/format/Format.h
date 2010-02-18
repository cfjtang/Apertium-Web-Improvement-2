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
 * Abstract class inherited by all formatters.
 */

#ifndef FORMAT_H_
#define FORMAT_H_

#include <iostream>
#include <sstream>

class Format {
public:
	std::wstring process(std::wstring in) {
		std::wstringstream wss;
		setYyin(in);
		setYyout(&wss);
		lex();
		return wss.str();
	}

	virtual void setYyin(std::wstring in) = 0;
	virtual void setYyout(std::wostream* out) = 0;
	virtual bool lex() = 0;
};

#endif /* FORMAT_H_ */
