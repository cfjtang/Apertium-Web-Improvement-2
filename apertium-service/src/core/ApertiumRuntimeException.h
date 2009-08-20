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
 * The ApertiumRuntimeException class is a runtime exception call used internally
 * to the Apertium Service.
 */

#ifndef APERTIUMRUNTIMEEXCEPTION_H_
#define APERTIUMRUNTIMEEXCEPTION_H_

#include <iostream>
#include <stdexcept>

/**
 * The ApertiumRuntimeException class is a runtime exception call used internally
 * to the Apertium Service.
 */
class ApertiumRuntimeException: public std::runtime_error {
public:
	ApertiumRuntimeException(std::string);
};

#endif /* APERTIUMRUNTIMEEXCEPTION_H_ */
