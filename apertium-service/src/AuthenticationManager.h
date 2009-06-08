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

#ifndef AUTHENTICATIONMANAGER_H_
#define AUTHENTICATIONMANAGER_H_

#include <iostream>
#include <vector>
#include <list>
#include <map>

#include <libxml++/libxml++.h>
#include <boost/thread.hpp>

class AuthenticationManager {
public:
	static AuthenticationManager* Instance();
	static AuthenticationManager* Instance(std::string);
	virtual ~AuthenticationManager();

	bool authenticateUser(std::string, std::string);

private:
	AuthenticationManager(std::string);
	static AuthenticationManager *instance;

	static boost::mutex instanceMutex;

	std::map<std::string, std::string> users;
};

#endif /* AUTHENTICATIONMANAGER_H_ */
