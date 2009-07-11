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

#ifndef APERTIUMLANGUAGEPAIRS_H_
#define APERTIUMLANGUAGEPAIRS_H_

#include <boost/thread/mutex.hpp>
#include <boost/thread/thread.hpp>
#include <libiqxmlrpc/libiqxmlrpc.h>
#include <time.h>

#include "core/ModesManager.h"

#include <iostream>
#include <string>
#include <list>

class ApertiumLanguagePairs: public iqxmlrpc::Method {
public:
	void execute(const iqxmlrpc::Param_list &params, iqxmlrpc::Value &retval);
	static ModesManager *modesManager;
};

#endif /* APERTIUMLANGUAGEPAIRS_H_ */
