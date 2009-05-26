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

#ifndef APERTIUMAUTHPLUGIN_H_
#define APERTIUMAUTHPLUGIN_H_

#include <libiqxmlrpc/libiqxmlrpc.h>
#include <libiqxmlrpc/auth_plugin.h>

class ApertiumAuthPlugin: public iqxmlrpc::Auth_Plugin_base {
public:
	ApertiumAuthPlugin();
	virtual ~ApertiumAuthPlugin();

	bool do_authenticate(const std::string& username, const std::string& pw) const;
	bool do_authenticate_anonymous() const;
};

#endif /* APERTIUMAUTHPLUGIN_H_ */
