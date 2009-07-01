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

#include "Statistics.h"

#include <iostream>
#include <string>

Statistics *Statistics::instance = NULL;
boost::shared_mutex Statistics::instanceMutex;

Statistics *Statistics::Instance() {
	instanceMutex.lock_shared();
	if (!instance) {
		instanceMutex.unlock_and_lock_upgrade();
		instance = new Statistics();
		instanceMutex.unlock_and_lock_shared();
	}
	Statistics *ret = instance;
	instanceMutex.unlock_shared();
	return ret;
}

Statistics::Statistics() { }

Statistics::~Statistics() {
	instanceMutex.lock_shared();
	if (instance != NULL) {
		instanceMutex.unlock_and_lock_upgrade();
		instance = NULL;
		instanceMutex.unlock();
	}
}
