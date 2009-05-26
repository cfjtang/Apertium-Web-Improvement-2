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
#include <libiqxmlrpc/http.h>
#include <libiqxmlrpc/http_server.h>
#include <libiqxmlrpc/http_errors.h>

void ApertiumLogInterceptor::process(iqxmlrpc::Method* m, const iqxmlrpc::Param_list& p, iqxmlrpc::Value& r) {
	std::cout << "LogInterceptor: " << m->name() << " invoked:" << std::endl;
	yield(m, p, r);
}
