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

#include "config.h"

#if defined(HAVE_LIBTEXTCAT)

#include "TextClassifier.h"

extern "C" {
	#include <textcat.h>
}

/**
 * Initialize the language detector using the data contained in a configuration file.
 */
TextClassifier::TextClassifier(fs::path p) {
	h = textcat_Init(p.string().data());
}

TextClassifier::~TextClassifier() {
	textcat_Done(h);
}

/**
 * Recognize the language used in a text.
 */
std::string TextClassifier::classify(std::string str) {
	std::string ret = textcat_Classify(h, str.data(), str.size());
	return(ret == _TEXTCAT_RESULT_SHORT ? "" : ret.substr(1, ret.size() - 2));
}

#endif

