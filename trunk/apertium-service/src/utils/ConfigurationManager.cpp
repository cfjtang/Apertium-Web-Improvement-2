/**
 * @file
 * @author  Pasquale Minervini <p.minervini@gmail.com>
 * @version 1.0
 *
 * @section LICENSE
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
 *
 * @section DESCRIPTION
 *
 * The ConfigurationManager class handles the service's configuration options.
 */

#include "ConfigurationManager.h"
#include <sstream>

using namespace std;
namespace fs = boost::filesystem;

const fs::path ConfigurationManager::APERTIUMBASE_DEF = "/usr/local/share/apertium";

const unsigned int ConfigurationManager::SERVERPORT_DEF = 6173;

const unsigned int ConfigurationManager::KEEPALIVETIMEOUT_DEF = 15;
const unsigned int ConfigurationManager::KEEPALIVEMAXCONN_DEF = 30;
const unsigned int ConfigurationManager::TIMEOUT_DEF = 15;

#if defined(HAVE_LIBTEXTCAT)
const fs::path ConfigurationManager::CONFTEXTCLASSIFIER_DEF = "tc.conf";
#endif

const unsigned int ConfigurationManager::HIGHWATERMARK_DEF = 0;

/**
 * Constructor that sets configuration's default values and parses the main configuration file
 */
ConfigurationManager::ConfigurationManager(fs::path confPath, fs::path confDirPath)
	: apertiumBase(APERTIUMBASE_DEF),
	serverPort(SERVERPORT_DEF),
	keepaliveTimeout(KEEPALIVETIMEOUT_DEF), keepaliveMaxConn(KEEPALIVEMAXCONN_DEF), timeout(TIMEOUT_DEF),
	highWaterMark(HIGHWATERMARK_DEF) {
	xmlpp::DomParser parser(confPath.string());

	if (parser) {
		const xmlpp::Node* rootNode = parser.get_document()->get_root_node();

#if defined(HAVE_LIBTEXTCAT)
		confTextClassifier = confDirPath / CONFTEXTCLASSIFIER_DEF;
#endif

		if (rootNode) {

			const string rootName = "/ApertiumServiceConfiguration";

			xmlpp::NodeSet apertiumBaseNodeSet = rootNode->find(rootName + "/ApertiumBase/text()");
			if (apertiumBaseNodeSet.begin() != apertiumBaseNodeSet.end()) {
				xmlpp::TextNode *node = dynamic_cast<xmlpp::TextNode*> (*apertiumBaseNodeSet.begin());
				apertiumBase = node->get_content();
			}

			xmlpp::NodeSet serverPortNodeSet = rootNode->find(rootName + "/ServerPort/text()");
			if (serverPortNodeSet.begin() != serverPortNodeSet.end()) {
				xmlpp::TextNode *node = dynamic_cast<xmlpp::TextNode*> (*serverPortNodeSet.begin());
				std::stringstream strStream(node->get_content());
				strStream >> serverPort;
			}


			xmlpp::NodeSet keepaliveTimeoutNodeSet = rootNode->find(rootName + "/KeepAliveTimeout/text()");
			if (keepaliveTimeoutNodeSet.begin() != keepaliveTimeoutNodeSet.end()) {
				xmlpp::TextNode	*node = dynamic_cast<xmlpp::TextNode*> (*keepaliveTimeoutNodeSet.begin());
				std::stringstream strStream(node->get_content());
				strStream >> keepaliveTimeout;
			}

			xmlpp::NodeSet keepaliveMaxConnNodeSet = rootNode->find(rootName + "/KeepAliveMaxConn/text()");
			if (keepaliveMaxConnNodeSet.begin() != keepaliveMaxConnNodeSet.end()) {
				xmlpp::TextNode	*node = dynamic_cast<xmlpp::TextNode*> (*keepaliveMaxConnNodeSet.begin());
				std::stringstream strStream(node->get_content());
				strStream >> keepaliveMaxConn;
			}

			xmlpp::NodeSet timeoutNodeSet = rootNode->find(rootName + "/Timeout/text()");
			if (timeoutNodeSet.begin() != timeoutNodeSet.end()) {
				xmlpp::TextNode	*node = dynamic_cast<xmlpp::TextNode*> (*timeoutNodeSet.begin());
				std::stringstream strStream(node->get_content());
				strStream >> timeout;
			}

			/*
			xmlpp::NodeSet useSslNodeSet = rootNode->find(rootName + "/UseSsl/text()");
			if (useSslNodeSet.begin() != useSslNodeSet.end()) {
				xmlpp::TextNode *node =	dynamic_cast<xmlpp::TextNode*> (*useSslNodeSet.begin());
				if (node->get_content() == "true") {
					useSsl = true;
				}
			}
			*/

#if defined(HAVE_LIBTEXTCAT)
			xmlpp::NodeSet confTextClassifierNodeSet = rootNode->find(rootName + "/ConfTextClassifier/text()");
			if (confTextClassifierNodeSet.begin() != confTextClassifierNodeSet.end()) {
				xmlpp::TextNode	*node = dynamic_cast<xmlpp::TextNode*> (*confTextClassifierNodeSet.begin());
				confTextClassifier = node->get_content();
			}
#endif

			/*
			xmlpp::NodeSet confUsersNodeSet = rootNode->find(rootName + "/ConfUsers/text()");
			if (confUsersNodeSet.begin() != confUsersNodeSet.end()) {
				xmlpp::TextNode *node = dynamic_cast<xmlpp::TextNode*> (*confUsersNodeSet.begin());
				confUsers = node->get_content();
			}
			*/

			xmlpp::NodeSet highWaterMarkNodeSet = rootNode->find(rootName + "/HighWaterMark/text()");
			if (highWaterMarkNodeSet.begin() != highWaterMarkNodeSet.end()) {
				xmlpp::TextNode	*node = dynamic_cast<xmlpp::TextNode*> (*highWaterMarkNodeSet.begin());
				std::stringstream strStream(node->get_content());
				strStream >> highWaterMark;
			}

#if defined(HAVE_COMBINE)
			xmlpp::NodeSet memtmonoNodeSet = rootNode->find(rootName + "/MultiEngineMachineTranslation/MonolingualDictionary");
			for (xmlpp::NodeSet::iterator it = memtmonoNodeSet.begin(); it != memtmonoNodeSet.end(); ++it) {
				xmlpp::Element *e = dynamic_cast<xmlpp::Element*> (*it);
				xmlpp::Attribute const *const src_attribute(e->get_attribute("srcLang"));
				xmlpp::Attribute const *const dest_attribute(e->get_attribute("destLang"));

				if (src_attribute != NULL && dest_attribute != NULL) {
					xmlpp::TextNode *tn = e->get_child_text();
					if (tn != NULL) {
						std::pair<std::string, std::string> lp(src_attribute->get_value(), dest_attribute->get_value());
						monolingualDictionaries[lp] = tn->get_content();
					}
				}
			}

			xmlpp::NodeSet memtlmNodeSet = rootNode->find(rootName + "/MultiEngineMachineTranslation/LanguageModel");
			for (xmlpp::NodeSet::iterator it = memtlmNodeSet.begin(); it != memtlmNodeSet.end(); ++it) {
				xmlpp::Element *e = dynamic_cast<xmlpp::Element*> (*it);
				xmlpp::Attribute const *const lang_attribute(e->get_attribute("lang"));

				if (lang_attribute != NULL) {
					xmlpp::TextNode *tn = e->get_child_text();
					if (tn != NULL) {
						languageModels[lang_attribute->get_value()] = tn->get_content();
					}
				}
			}
#endif

		}
	}
}

ConfigurationManager::~ConfigurationManager() { }

unsigned int ConfigurationManager::getServerPort() {
	return serverPort;
}

void ConfigurationManager::setServerPort(unsigned int s) {
	serverPort = s;
}

fs::path ConfigurationManager::getApertiumBase() {
	return apertiumBase;
}

void ConfigurationManager::setApertiumBase(fs::path a) {
	apertiumBase = a;
}

unsigned int ConfigurationManager::getKeepaliveTimeout() {
	return keepaliveTimeout;
}

void ConfigurationManager::setKeepaliveTimeout(unsigned int k) {
	keepaliveTimeout = k;
}

unsigned int ConfigurationManager::getKeepaliveMaxConn() {
	return keepaliveMaxConn;
}

void ConfigurationManager::setKeepaliveMaxConn(unsigned int k) {
	keepaliveMaxConn = k;
}

unsigned int ConfigurationManager::getTimeout() {
	return timeout;
}

void ConfigurationManager::setTimeout(unsigned int t) {
	timeout = t;
}

/*
bool ConfigurationManager::getUseSsl() {
	return useSsl;
}

void ConfigurationManager::setUseSsl(bool u) {
	useSsl = u;
}
*/

#if defined(HAVE_LIBTEXTCAT)
fs::path ConfigurationManager::getConfTextClassifier() {
	return confTextClassifier;
}

void ConfigurationManager::setConfTextClassifier(fs::path c) {
	confTextClassifier = c;
}
#endif

#if defined(HAVE_COMBINE)
ConfigurationManager::MonolingualDictionariesType ConfigurationManager::getMonolingualDictionaries() {
	return monolingualDictionaries;
}

ConfigurationManager::LanguageModelsType ConfigurationManager::getLanguageModels() {
	return languageModels;
}
#endif

/*
fs::path ConfigurationManager::getConfUsers() {
	return confUsers;
}

void ConfigurationManager::setConfUsers(fs::path c) {
	confUsers = c;
}
*/

unsigned int ConfigurationManager::getHighWaterMark() {
	return highWaterMark;
}

void ConfigurationManager::setHighWaterMark(unsigned int h) {
	highWaterMark = h;
}
