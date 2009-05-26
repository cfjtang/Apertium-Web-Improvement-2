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

#include "ApertiumAuthPlugin.h"

#include <iostream>

#include "AuthenticationManager.h"

using namespace std;

ApertiumAuthPlugin::ApertiumAuthPlugin() { }

ApertiumAuthPlugin::~ApertiumAuthPlugin() { }

bool ApertiumAuthPlugin::do_authenticate(const std::string& username, const std::string& pw) const {
	bool ret = false;

	AuthenticationManager *am = AuthenticationManager::Instance();
	ret = am->authenticateUser(username, pw);

	if (ret)
		cout << "Authentication granted for user " << username << endl;
	else
		cout << "Authentication not granted for user " << username << endl;

	return ret;
}

bool ApertiumAuthPlugin::do_authenticate_anonymous() const {
	bool ret = true;
	cout << "Authentication granted for anonymous user" << endl;
	return ret;
}
