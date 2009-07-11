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

#include "ApertiumLogInterceptor.h"

#include <iostream>
#include <string>

#include "utils/Logger.h"

void ApertiumLogInterceptor::process(iqxmlrpc::Method* m, const iqxmlrpc::Param_list& p, iqxmlrpc::Value& r) {
	std::string user;
	if (m->authenticated())
		user = m->authname();
	else
		user = "anonymous";

	std::stringstream ss;
	ss << m->name() << "(\"";
	for (size_t i = 0; i < p.size(); ++i) {
		if (i != 0)
			ss << "\", \"";
		std::string str = p[i];
		ss << str;
	}
	ss << "\");";

	Logger::Instance()->trace(INFO, "User \"" + user + "\" invoked " + ss.str());
	yield(m, p, r);
}
