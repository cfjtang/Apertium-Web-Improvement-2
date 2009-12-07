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

#include <string>
#include <vector>
#include <map>

#include "utils/Logger.h"

using namespace std;

const string ApertiumXMLRPCService::TRANSLATE_NAME = "translate";

#if defined(HAVE_LIBTEXTCAT)
const string ApertiumXMLRPCService::DETECT_NAME = "detect";
#endif

#if defined(HAVE_IRSTLM)
const string ApertiumXMLRPCService::SYNTHESISE_NAME = "synthesise";
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

		this->_signature = "S:sss,S:ssss";
		this->_help = "Translate method";
	}

	void execute(xmlrpc_c::paramList const &paramList, xmlrpc_c::value* const retvalP) {
		string text(paramList.getString(0));
		string srcLang(paramList.getString(1));

		string const destLang(paramList.getString(2));

		Translator::ContentType contentType = Translator::TEXT;

		if (paramList.size() > 3) {
			string const type(paramList.getString(3));

			if (type == "text") {
				contentType = Translator::TEXT;
			} else if (type == "html") {
				contentType = Translator::HTML;
			} else {
				throw xmlrpc_c::fault("Invalid parameter: Content Type unknown or not supported");
			}
		}

	    map<string, xmlrpc_c::value> ret;

#if defined(HAVE_LIBTEXTCAT)
        if (srcLang.empty()) {
        	srcLang = textClassifier->classify(text);
        	pair<string, xmlrpc_c::value> detectedSourceLanguage("detectedSourceLanguage", xmlrpc_c::value_string(srcLang));
        	ret.insert(detectedSourceLanguage);
        }
#endif

	    pair<string, xmlrpc_c::value> translation("translation",
	    		xmlrpc_c::value_string(Translator::translate(*resourceBroker, *modesManager, text, contentType,
	    				srcLang, destLang, statistics)));
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

#if defined(HAVE_IRSTLM)
class SynthesiseMethod : public xmlrpc_c::method {
public:
	SynthesiseMethod(ResourceBroker &rb, ConfigurationManager &cm) : resourceBroker(&rb), configurationManager(&cm) {
		this->_signature = "s:Ass";
		this->_help = "Synthesise method";
	}

	void execute(xmlrpc_c::paramList const &paramList, xmlrpc_c::value* const retvalP) {
		std::vector<xmlrpc_c::value> tv(paramList.getArray(0));
		std::vector<std::string> translations(tv.size());

		for (unsigned int i = 0; i < tv.size(); ++i) {
			xmlrpc_c::value value = tv[i];
			std::string translation = xmlrpc_c::value_string(value);
			translations[i] = translation;
		}

		std::string const srcLang(paramList.getString(1));
		std::string const destLang(paramList.getString(2));

		std::string lm, mm;

		ConfigurationManager::LanguageModelsType::iterator itlm = configurationManager->getLanguageModels().find(srcLang);
		if (itlm != configurationManager->getLanguageModels().end()) {
			lm = itlm->second;
		} else {
			throw xmlrpc_c::fault("Invalid parameter: no language models for the language \"" + lm + "\"");
		}

		ConfigurationManager::MonolingualDictionariesType::iterator itmm = configurationManager->getMonolingualDictionaries().find(std::pair<std::string, std::string>(srcLang, destLang));
		if (itmm != configurationManager->getMonolingualDictionaries().end()) {
			mm = itmm->second;
		} else {
			throw xmlrpc_c::fault("Invalid parameter: no monolingual dictionaries for the language pair \"" + srcLang + "-" + destLang + "\"");
		}

		*retvalP = xmlrpc_c::value_string(Synthesiser::synthesise(*resourceBroker, lm, mm, translations, srcLang, destLang));
	}

private:
	ResourceBroker *resourceBroker;
	ConfigurationManager *configurationManager;
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
		TextClassifier &tc, Statistics *s) : configurationManager(&cm) {
#else
ApertiumXMLRPCService::ApertiumXMLRPCService(ConfigurationManager &cm, ModesManager &mm, ResourceBroker &rb, Statistics *s) {
#endif

	xmlrpcRegistry = new xmlrpc_c::registry;

#if defined(HAVE_LIBTEXTCAT)
	xmlrpc_c::methodPtr const TranslateMethodP(new TranslateMethod(rb, mm, tc, s));
#else
	xmlrpc_c::methodPtr const TranslateMethodP(new TranslateMethod(rb, mm, s));
#endif
	xmlrpcRegistry->addMethod(TRANSLATE_NAME, TranslateMethodP);

#if defined(HAVE_LIBTEXTCAT)
	xmlrpc_c::methodPtr const DetectMethodP(new DetectMethod(tc));
	xmlrpcRegistry->addMethod(DETECT_NAME, DetectMethodP);
#endif

#if defined(HAVE_IRSTLM)
	xmlrpc_c::methodPtr const SynthesiseMethodP(new SynthesiseMethod(rb, cm));
	xmlrpcRegistry->addMethod(SYNTHESISE_NAME, SynthesiseMethodP);
#endif

	xmlrpc_c::methodPtr const LanguagePairsMethodP(new LanguagePairsMethod(mm));
	xmlrpcRegistry->addMethod(LANGUAGEPAIRS_NAME, LanguagePairsMethodP);

	abyssServer = new xmlrpc_c::serverAbyss(xmlrpc_c::serverAbyss::constrOpt()
		.registryP(xmlrpcRegistry)
		.portNumber(configurationManager->getServerPort())
		.keepaliveTimeout(configurationManager->getKeepaliveTimeout())
		.keepaliveMaxConn(configurationManager->getKeepaliveMaxConn())
		.timeout(configurationManager->getTimeout())
		);

}

ApertiumXMLRPCService::~ApertiumXMLRPCService() {
	delete abyssServer;
	delete xmlrpcRegistry;
}

void ApertiumXMLRPCService::start() {
	{
		stringstream ssmsg;
		ssmsg << "Starting Apertium XML-RPC service on port " << (configurationManager->getServerPort());
		Logger::Instance()->trace(Logger::Info, ssmsg.str());
	}

	abyssServer->run();
}

void ApertiumXMLRPCService::stop() {
	stringstream ssmsg;
	ssmsg << "Terminating the Apertium XML-RPC service..";
	Logger::Instance()->trace(Logger::Info, ssmsg.str());

	//abyssServer->terminate();
}
