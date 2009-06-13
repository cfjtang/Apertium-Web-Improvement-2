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

#include "Encoding.h"

#include <iostream>
#include <string>

#include <glibmm/convert.h>
#include <glibmm/ustring.h>

Encoding::Encoding() { }

Encoding::~Encoding() { }

std::string Encoding::wstringToUtf8(std::wstring in) {
	std::string ret = Glib::convert(std::string(reinterpret_cast<const char *> (in.data()), in.size() * sizeof(wchar_t)), "UTF-8", "WCHAR_T");
	return ret;
}

std::wstring Encoding::utf8ToWstring(std::string in) {
	std::string str = Glib::convert(in, "WCHAR_T", "UTF-8");
	std::wstring ret(reinterpret_cast<const wchar_t *> (str.data()), str.size() / sizeof(wchar_t));
	return ret;
}
