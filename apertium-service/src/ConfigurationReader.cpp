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

#include "ConfigurationReader.h"
#include <sstream>

ConfigurationReader *ConfigurationReader::instance = NULL;

ConfigurationReader *ConfigurationReader::Instance() {
	return Instance("configuration.xml");
}

ConfigurationReader *ConfigurationReader::Instance(std::string confPath) {
	ConfigurationReader *ret = NULL;
	if (!instance) {
		instance = new ConfigurationReader(confPath);
	}
	ret = instance;
	return(ret);
}

ConfigurationReader::ConfigurationReader(std::string confPath) {
	xmlpp::DomParser parser(confPath);

	if (parser) {
		const xmlpp::Node* rootNode = parser.get_document()->get_root_node();

		serverPort = 8080;
		apertiumBase = "/usr/local/share/apertium";
		maxThreads = 100;
		useSsl = false;

		if (rootNode) {
			xmlpp::NodeSet serverPortNodeSet = rootNode->find("/ApertiumServerConfiguration/ServerPort/text()");
			if (serverPortNodeSet.begin() != serverPortNodeSet.end()) {
				xmlpp::TextNode	*node = dynamic_cast<xmlpp::TextNode*> (*serverPortNodeSet.begin());
				std::stringstream strStream(node->get_content());
				strStream >> serverPort;
			}

			xmlpp::NodeSet apertiumBaseNodeSet = rootNode->find("/ApertiumServerConfiguration/ApertiumBase/text()");
			if (apertiumBaseNodeSet.begin() != apertiumBaseNodeSet.end()) {
				xmlpp::TextNode	*node =	dynamic_cast<xmlpp::TextNode*> (*apertiumBaseNodeSet.begin());
				apertiumBase = node->get_content();
			}

			xmlpp::NodeSet maxThreadsNodeSet = rootNode->find("/ApertiumServerConfiguration/MaxThreads/text()");
			if (maxThreadsNodeSet.begin() != maxThreadsNodeSet.end()) {
				xmlpp::TextNode	*node = dynamic_cast<xmlpp::TextNode*> (*maxThreadsNodeSet.begin());
				std::stringstream strStream(node->get_content());
				strStream >> maxThreads;
			}

			xmlpp::NodeSet useSslNodeSet = rootNode->find("/ApertiumServerConfiguration/UseSsl/text()");
			if (useSslNodeSet.begin() != useSslNodeSet.end()) {
				xmlpp::TextNode	*node = dynamic_cast<xmlpp::TextNode*> (*useSslNodeSet.begin());
				if (node->get_content() == "true") {
					useSsl = true;
				}
			}
		}
	}
}

ConfigurationReader::~ConfigurationReader() {

}

int ConfigurationReader::getServerPort() {
	return serverPort;
}

void ConfigurationReader::setServerPort(int s) {
	serverPort = s;
}

std::string ConfigurationReader::getApertiumBase() {
	return apertiumBase;
}

void ConfigurationReader::setApertiumBase(std::string a) {
	apertiumBase = a;
}

int ConfigurationReader::getMaxThreads() {
	return maxThreads;
}

void ConfigurationReader::setMaxThreads(int m) {
	maxThreads = m;
}

bool ConfigurationReader::getUseSsl() {
	return useSsl;
}

void ConfigurationReader::setUseSsl(bool u) {
	useSsl = u;
}
