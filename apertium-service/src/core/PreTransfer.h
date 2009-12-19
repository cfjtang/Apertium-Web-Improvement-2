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

#ifndef PRETRANSFER_H_
#define PRETRANSFER_H_

#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <libgen.h>
#include <string>

#include <lttoolbox/lt_locale.h>
#ifdef WIN32
#if defined(__MINGW32__)
#define __MSVCRT_VERSION__  0x0800
#endif
#include <io.h>
#include <fcntl.h>
#endif
#include <apertium/string_utils.h>

class PreTransfer {
public:
	static void processStream(FILE*, FILE*);

private:
	static void readAndWriteUntil(FILE*, FILE*, int const);
	static void procWord(FILE*, FILE*);
};

#endif /* PRETRANSFER_H_ */
