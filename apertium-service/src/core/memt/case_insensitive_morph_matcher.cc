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

#include "config.h"

#if defined(HAVE_IRSTLM)

#include <iostream>
#include <cctype>
#include <utility>
#include <algorithm>
#include <string>

#include "case_insensitive_morph_matcher.hh"

using namespace std;

void inline to_lower(wstring& to_lower) {
	transform(to_lower.begin(), to_lower.end(), to_lower.begin(), towlower);
	return;
}

Case_Insensitive_Morph_Matcher::Case_Insensitive_Morph_Matcher(
		string const &fstfile) {
	readBil(fstfile);
}

Case_Insensitive_Morph_Matcher::Case_Insensitive_Morph_Matcher() { }

Case_Insensitive_Morph_Matcher::~Case_Insensitive_Morph_Matcher() { }

int Case_Insensitive_Morph_Matcher::match(const wstring& left,
		const wstring& right) {
	wstring l = wstring(left);
	wstring r = wstring(right);
	to_lower(l);
	to_lower(r);
	if (!l.compare(r)) {
		return 100;
	}

	wstring tr_right, tr_left;
	tr_right = fstp.biltrans(r, false);
	tr_left = fstp.biltrans(l, false);

	/// Align verbs even if conjugation is different.
	wstring::size_type lasts = wstring::npos;
	lasts = tr_left.find(L"<vblex>");
	bool lleft = false;
	if (lasts != wstring::npos) {
		tr_left.erase(lasts, tr_left.size() - 1);
		lleft = true;
		lasts = tr_right.find(L"<vblex>");
		if (lasts != wstring::npos)
		tr_right.erase(lasts, tr_right.size() - 1);
		// wcout << L"r: " << tr_right << endl;
			// wcout << L"l: " << tr_left << endl;
			if (lleft && lasts != wstring::npos) {
				if (!tr_left.compare(tr_right)) {
					return 10;
				}
			}
		}

		return 0;
	}

void Case_Insensitive_Morph_Matcher::readBil(string const &fstfile) {
	FILE *in = fopen(fstfile.c_str(), "rb");

	if (!in) {
		cerr << "Error: Could not open file '" << fstfile << "'." << endl;
		exit(EXIT_FAILURE);
	}

	fstp.load(in);
	fstp.initBiltrans();
	fclose(in);

	//cerr << "Loaded transducer from " << fstfile << endl;
}

#endif
