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

#ifndef APERTIUMLISTLANGUAGEPAIRS_H_
#define APERTIUMLISTLANGUAGEPAIRS_H_

#include <boost/thread/mutex.hpp>
#include <boost/thread/thread.hpp>
#include <libiqxmlrpc/libiqxmlrpc.h>
#include <time.h>

#include <iostream>
#include <string>
#include <list>

class ApertiumListLanguagePairs: public iqxmlrpc::Method {
public:
	void execute(const iqxmlrpc::Param_list &params, iqxmlrpc::Value &retval);
private:
	//static time_t lastCacheTime = 0;
};

#endif /* APERTIUMLISTLANGUAGEPAIRS_H_ */
