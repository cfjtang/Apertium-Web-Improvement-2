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
 * The TextClassifier class implements the classification technique described
 * in [1].
 * [1] William B. Cavnar & John M. Trenkle (1994), N-Gram-Based Text Categorization.
 */

#ifndef TEXTCLASSIFIER_H_
#define TEXTCLASSIFIER_H_

#include "config.h"

#if defined(HAVE_LIBTEXTCAT)

#include <iostream>
#include <string>

#include <boost/filesystem.hpp>

namespace fs = boost::filesystem;

/**
 * The TextClassifier class implements the classification technique described
 * in [1].
 * [1] William B. Cavnar & John M. Trenkle (1994), N-Gram-Based Text Categorization.
 */
class TextClassifier {
public:
	TextClassifier(fs::path);
	virtual ~TextClassifier();

	std::string classify(std::string);

private:
	void *h;
};

#endif

#endif /* TEXTCLASSIFIER_H_ */
