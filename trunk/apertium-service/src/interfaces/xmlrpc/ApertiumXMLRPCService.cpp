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
 * The class ApertiumXMLRPCService implements a service using the XML-RPC protocol
 * and exposing Apertium's capabilities for translation and language detection.
 * XML-RPC is a remote procedure call protocol which uses XML to encode its calls
 * and HTTP as a transport mechanism.
 */

#include "config.h"

#include "ApertiumXMLRPCService.h"

#include <vector>
#include <map>

#include "utils/Logger.h"

using namespace std;

const string ApertiumXMLRPCService::TRANSLATE_NAME = "translate";

#if defined(HAVE_LIBTEXTCAT)
const string ApertiumXMLRPCService::DETECT_NAME = "detect";
#endif

const string ApertiumXMLRPCService::LANGUAGEPAIRS_NAME = "languagePairs";

class TranslateMethod : public xmlrpc_c::method {
public:

#if defined(HAVE_LIBTEXTCAT)
	TranslateMethod(ResourceBroker &rb, ModesManager &mm, TextClassifier &tc, Statistics *s) : resourceBroker(&rb), modesManager(&mm),
		textClassifier(&tc), statistics(s) {
#else
	TranslateMethod(ResourceBroker &rb, ModesManager &mm, Statistics *s) : resourceBroker(&rb), modesManager(&mm), statistics(s) {
#endif

		this->_signature = "S:sss";
		this->_help = "Translate method";
	}

	void execute(xmlrpc_c::paramList const &paramList, xmlrpc_c::value* const retvalP) {
		string text(paramList.getString(0));
		string srcLang(paramList.getString(1));

		string const destLang(paramList.getString(2));

	    map<string, xmlrpc_c::value> ret;

#if defined(HAVE_LIBTEXTCAT)
        if (srcLang.empty()) {
        	srcLang = textClassifier->classify(text);
        	pair<string, xmlrpc_c::value> detectedSourceLanguage("detectedSourceLanguage", xmlrpc_c::value_string(srcLang));
        	ret.insert(detectedSourceLanguage);
        }
#endif

	    pair<string, xmlrpc_c::value> translation("translation",
	    		xmlrpc_c::value_string(Translator::translate(*resourceBroker, *modesManager, text, srcLang, destLang, statistics)));
	    ret.insert(translation);

		*retvalP = xmlrpc_c::value_struct(ret);
	}

private:
	ResourceBroker *resourceBroker;
	ModesManager *modesManager;

#if defined(HAVE_LIBTEXTCAT)
	TextClassifier *textClassifier;
#endif

	Statistics *statistics;
};

#if defined(HAVE_LIBTEXTCAT)
class DetectMethod : public xmlrpc_c::method {
public:
	DetectMethod(TextClassifier &tc) : textClassifier(&tc) {
		this->_signature = "s:s";
		this->_help = "Detect method";
	}

	void execute(xmlrpc_c::paramList const &paramList, xmlrpc_c::value* const retvalP) {
		string const text(paramList.getString(0));

		*retvalP = xmlrpc_c::value_string(textClassifier->classify(text));
	}

private:
	TextClassifier *textClassifier;
};
#endif

class LanguagePairsMethod : public xmlrpc_c::method {
public:
	LanguagePairsMethod(ModesManager &mm) : modesManager(&mm) {
		this->_signature = "A:";
		this->_help = "LanguagePairs method";
	}

	void execute(xmlrpc_c::paramList const &paramList, xmlrpc_c::value* const retvalP) {
		vector<xmlrpc_c::value> ret;
		ModesManager::ModeMapType modes = modesManager->getModes();

		for (ModesManager::ModeMapType::iterator it = modes.begin(); it != modes.end(); it++) {
			map<string, xmlrpc_c::value> spair;
            string mode = (*it).first;
            size_t sep = mode.find("-", 0);

            string srcLang = mode.substr(0, sep);
            string destLang = mode.substr(sep + 1, mode.size());

			pair<string, xmlrpc_c::value> src("srcLang", xmlrpc_c::value_string(srcLang));
			pair<string, xmlrpc_c::value> dest("destLang", xmlrpc_c::value_string(destLang));

			spair.insert(src);
			spair.insert(dest);

			ret.push_back(xmlrpc_c::value_struct(spair));
		}

		*retvalP = xmlrpc_c::value_array(ret);
	}

private:
	ModesManager *modesManager;
};

#if defined(HAVE_LIBTEXTCAT)
ApertiumXMLRPCService::ApertiumXMLRPCService(ConfigurationManager &cm, ModesManager &mm, ResourceBroker &rb,
		TextClassifier &tc, Statistics *s) {
#else
ApertiumXMLRPCService::ApertiumXMLRPCService(ConfigurationManager &cm, ModesManager &mm, ResourceBroker &rb, Statistics *s) {
#endif

	xmlrpcRegistry = new xmlrpc_c::registry();

	xmlrpc_c::methodPtr const TranslateMethodP(new TranslateMethod(rb, mm, tc, s));
	xmlrpcRegistry->addMethod(TRANSLATE_NAME, TranslateMethodP);

#if defined(HAVE_LIBTEXTCAT)
	xmlrpc_c::methodPtr const DetectMethodP(new DetectMethod(tc));
	xmlrpcRegistry->addMethod(DETECT_NAME, DetectMethodP);
#endif

	xmlrpc_c::methodPtr const LanguagePairsMethodP(new LanguagePairsMethod(mm));
	xmlrpcRegistry->addMethod(LANGUAGEPAIRS_NAME, LanguagePairsMethodP);

	abyssServer = new xmlrpc_c::serverAbyss(xmlrpc_c::serverAbyss::constrOpt()
		.registryP(xmlrpcRegistry)
		.portNumber(cm.getServerPort())
		.keepaliveTimeout(cm.getKeepaliveTimeout())
		.keepaliveMaxConn(cm.getKeepaliveMaxConn())
		.timeout(cm.getTimeout())
		);

    //abyssServer = new xmlrpc_c::serverAbyss(*xmlrpcRegistry, cm.getServerPort(), "/dev/null");

	stringstream ssmsg;
	ssmsg << "Apertium XML-RPC service set up to run on port " << (cm.getServerPort());
	Logger::Instance()->trace(Logger::Info, ssmsg.str());
}

ApertiumXMLRPCService::~ApertiumXMLRPCService() {
	stringstream ssmsg;
	ssmsg << "Terminating the Apertium XML-RPC service..";
	Logger::Instance()->trace(Logger::Info, ssmsg.str());

	abyssServer->terminate();

	delete abyssServer;
	delete xmlrpcRegistry;
}

void ApertiumXMLRPCService::start() {
	stringstream ssmsg;
	ssmsg << "Starting the Apertium XML-RPC service..";
	Logger::Instance()->trace(Logger::Info, ssmsg.str());

	abyssServer->run();
}
