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

#include "AuthenticationManager.h"

#include <openssl/md5.h>

AuthenticationManager *AuthenticationManager::instance = NULL;

AuthenticationManager *AuthenticationManager::Instance() {
	return instance;
}

AuthenticationManager *AuthenticationManager::Instance(std::string confPath) {
	if (!instance) {
		instance = new AuthenticationManager(confPath);
	}
	return instance;
}

AuthenticationManager::AuthenticationManager(std::string confPath) {
	xmlpp::DomParser parser(confPath);

	if (parser) {
		const xmlpp::Node* rootNode = parser.get_document()->get_root_node();

		if (rootNode) {
			xmlpp::NodeSet userNodeSet = rootNode->find("/ApertiumUsers/User");
			for (xmlpp::NodeSet::iterator i = userNodeSet.begin(); i != userNodeSet.end(); ++i) {
				const xmlpp::Element* nodeElement =	dynamic_cast<const xmlpp::Element*> (*i);

				const xmlpp::Element::AttributeList& attributes = nodeElement->get_attributes();

				std::string username, password;

				for (xmlpp::Element::AttributeList::const_iterator iter = attributes.begin(); iter != attributes.end(); ++iter) {
					const xmlpp::Attribute* attribute = *iter;

					Glib::ustring attributeName = attribute->get_name();

					if (attributeName.compare("username") == 0)
						username = attribute->get_value();
					else if (attributeName.compare("password") == 0)
						password = attribute->get_value();
				}

				users[username] = password;
			}
		}
	}
}

AuthenticationManager::~AuthenticationManager() {
	instance = NULL;
}

bool AuthenticationManager::authenticateUser(std::string username,
		std::string password) {
	bool ret = false;

	MD5_CTX ctx;

	unsigned char digest[MD5_DIGEST_LENGTH];
	MD5_Init(&ctx);

	MD5_Update(&ctx, password.c_str(), password.length());
	MD5_Final(digest, &ctx);

	if (users[username].compare((char *) digest))
		ret = true;

	return (ret);
}
