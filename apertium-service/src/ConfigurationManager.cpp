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

#include "ConfigurationManager.h"
#include <sstream>

ConfigurationManager *ConfigurationManager::instance = NULL;

ConfigurationManager *ConfigurationManager::Instance() {
	return Instance("configuration.xml");
}

ConfigurationManager *ConfigurationManager::Instance(std::string confPath) {
	ConfigurationManager *ret = NULL;
	if (!instance) {
		instance = new ConfigurationManager(confPath);
	}
	ret = instance;
	return (ret);
}

ConfigurationManager::ConfigurationManager(std::string confPath) {
	xmlpp::DomParser parser(confPath);

	if (parser) {
		const xmlpp::Node* rootNode = parser.get_document()->get_root_node();

		serverPort = 8080;
		apertiumBase = "/usr/local/share/apertium";
		maxThreads = 100;
		useSsl = false;
		confTextClassifier = "tc.conf";
		confUsers = "users.xml";

		if (rootNode) {
			xmlpp::NodeSet serverPortNodeSet = rootNode->find(
					"/ApertiumServerConfiguration/ServerPort/text()");
			if (serverPortNodeSet.begin() != serverPortNodeSet.end()) {
				xmlpp::TextNode
						*node =
								dynamic_cast<xmlpp::TextNode*> (*serverPortNodeSet.begin());
				std::stringstream strStream(node->get_content());
				strStream >> serverPort;
			}

			xmlpp::NodeSet apertiumBaseNodeSet = rootNode->find(
					"/ApertiumServerConfiguration/ApertiumBase/text()");
			if (apertiumBaseNodeSet.begin() != apertiumBaseNodeSet.end()) {
				xmlpp::TextNode
						*node =
								dynamic_cast<xmlpp::TextNode*> (*apertiumBaseNodeSet.begin());
				apertiumBase = node->get_content();
			}

			xmlpp::NodeSet maxThreadsNodeSet = rootNode->find(
					"/ApertiumServerConfiguration/MaxThreads/text()");
			if (maxThreadsNodeSet.begin() != maxThreadsNodeSet.end()) {
				xmlpp::TextNode
						*node =
								dynamic_cast<xmlpp::TextNode*> (*maxThreadsNodeSet.begin());
				std::stringstream strStream(node->get_content());
				strStream >> maxThreads;
			}

			xmlpp::NodeSet useSslNodeSet = rootNode->find(
					"/ApertiumServerConfiguration/UseSsl/text()");
			if (useSslNodeSet.begin() != useSslNodeSet.end()) {
				xmlpp::TextNode *node =
						dynamic_cast<xmlpp::TextNode*> (*useSslNodeSet.begin());
				if (node->get_content() == "true") {
					useSsl = true;
				}
			}

			xmlpp::NodeSet confTextClassifierNodeSet = rootNode->find(
					"/ApertiumServerConfiguration/ConfTextClassifier/text()");
			if (confTextClassifierNodeSet.begin()
					!= confTextClassifierNodeSet.end()) {
				xmlpp::TextNode
						*node =
								dynamic_cast<xmlpp::TextNode*> (*confTextClassifierNodeSet.begin());
				confTextClassifier = node->get_content();
			}

			xmlpp::NodeSet confUsersNodeSet = rootNode->find(
					"/ApertiumServerConfiguration/ConfUsers/text()");
			if (confUsersNodeSet.begin() != confUsersNodeSet.end()) {
				xmlpp::TextNode
						*node =
								dynamic_cast<xmlpp::TextNode*> (*confUsersNodeSet.begin());
				confUsers = node->get_content();
			}

		}
	}
}

ConfigurationManager::~ConfigurationManager() {

}

int ConfigurationManager::getServerPort() {
	return serverPort;
}

void ConfigurationManager::setServerPort(int s) {
	serverPort = s;
}

std::string ConfigurationManager::getApertiumBase() {
	return apertiumBase;
}

void ConfigurationManager::setApertiumBase(std::string a) {
	apertiumBase = a;
}

int ConfigurationManager::getMaxThreads() {
	return maxThreads;
}

void ConfigurationManager::setMaxThreads(int m) {
	maxThreads = m;
}

bool ConfigurationManager::getUseSsl() {
	return useSsl;
}

void ConfigurationManager::setUseSsl(bool u) {
	useSsl = u;
}

std::string ConfigurationManager::getConfTextClassifier() {
	return confTextClassifier;
}

void ConfigurationManager::setConfTextClassifier(std::string c) {
	confTextClassifier = c;
}

std::string ConfigurationManager::getConfUsers() {
	return confUsers;
}

void ConfigurationManager::setConfUsers(std::string c) {
	confUsers = c;
}
