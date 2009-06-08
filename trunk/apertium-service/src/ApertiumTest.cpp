/*
 * Copyright (C) 2009  Pasquale Minervini <p.minervini@gmail.com>
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
 */

#include "ApertiumTest.h"

#include <iostream>

#include <glibmm/convert.h>

using namespace std;

void ApertiumTest::execute(const iqxmlrpc::Param_list &params, iqxmlrpc::Value &retval) {
	wstring ws = L"Cómo te es?";
	string in((const char *)ws.data(), ws.size() * sizeof(wchar_t));
	string out = Glib::convert(in, "UTF-8", "WCHAR_T");
	retval = out;
}
