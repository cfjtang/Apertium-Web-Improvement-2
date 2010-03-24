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

const std::string ApertiumXMLRPCService::TRANSLATE_NAME = "translate";

#if defined(HAVE_LIBTEXTCAT)
const std::string ApertiumXMLRPCService::DETECT_NAME = "detect";
#endif

#if defined(HAVE_COMBINE)
const std::string ApertiumXMLRPCService::SYNTHESISE_NAME = "synthesise";
#endif

const std::string ApertiumXMLRPCService::LANGUAGEPAIRS_NAME = "languagePairs";

class TranslateMethod : public xmlrpc_c::method {
public:

	TranslateMethod(boost::shared_mutex &m, ResourceBroker &rb, ModesManager &mm,
#if defined(HAVE_LIBTEXTCAT)
			TextClassifier &tc,
#endif
			Statistics *s) : mux(&m), resourceBroker(&rb), modesManager(&mm),
#if defined(HAVE_LIBTEXTCAT)
			textClassifier(&tc),
#endif
			statistics(s) {

		this->_signature = "S:S,S:sss,S:ssss,S:ssssb";
		this->_help = "Translate method";
	}

	void execute(xmlrpc_c::paramList const &paramList, xmlrpc_c::value* const retvalP) {
		boost::shared_lock<boost::shared_mutex> lock(*mux);

		std::string text;
		std::string srcLang;
		std::string destLang;

		std::string type = "text";
		bool markUnknownWords = true;

		if (paramList.size() < 2) {
			typedef std::map<std::string, xmlrpc_c::value> params_t;

			const params_t params = paramList.getStruct(0);

	        params_t::const_iterator ti = params.find("text");
	        if (ti == params.end()) {
	            throw xmlrpc_c::fault("Missing source text", xmlrpc_c::fault::CODE_PARSE);
	        }
	        text = xmlrpc_c::value_string(ti->second);

	        params_t::const_iterator si = params.find("srcLang");
	        if (si == params.end()) {
	            throw xmlrpc_c::fault("Missing source language", xmlrpc_c::fault::CODE_PARSE);
	        }
	        srcLang = xmlrpc_c::value_string(si->second);

	        params_t::const_iterator di = params.find("destLang");
	        if (di == params.end()) {
	            throw xmlrpc_c::fault("Missing destination language", xmlrpc_c::fault::CODE_PARSE);
	        }
	        destLang = xmlrpc_c::value_string(di->second);

	        params_t::const_iterator pi = params.find("type");
	        if (pi != params.end()) {
	        	type = xmlrpc_c::value_string(pi->second);
	        }

	        params_t::const_iterator mi = params.find("markUnknownWords");
	        if (mi != params.end()) {
	        	markUnknownWords = xmlrpc_c::value_boolean(mi->second);
	        }

		} else {
			text = paramList.getString(0);
			srcLang = paramList.getString(1);
			destLang = paramList.getString(2);

			if (paramList.size() > 3) {
				type = paramList.getString(3);

				if (paramList.size() > 4) {
					markUnknownWords = paramList.getBoolean(4);
				}
			}
		}

		Translator::ContentType contentType = Translator::TEXT;

		if (type == "text") {
			contentType = Translator::TEXT;
		} else {
			throw xmlrpc_c::fault("Invalid parameter: Content Type unknown or not supported", xmlrpc_c::fault::CODE_PARSE);
		}

		std::map<std::string, xmlrpc_c::value> ret;

#if defined(HAVE_LIBTEXTCAT)
        if (srcLang.empty()) {
        	srcLang = textClassifier->classify(text);
        	std::pair<std::string, xmlrpc_c::value> detectedSourceLanguage("detectedSourceLanguage", xmlrpc_c::value_string(srcLang));
        	ret.insert(detectedSourceLanguage);
        }
#endif

	    std::pair<std::string, xmlrpc_c::value> translation("translation", xmlrpc_c::value_string(Translator::translate(*resourceBroker, *modesManager, text, contentType, srcLang, destLang, markUnknownWords, statistics)));
	    ret.insert(translation);

		*retvalP = xmlrpc_c::value_struct(ret);
	}

private:
	boost::shared_mutex *mux;
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
	DetectMethod(boost::shared_mutex &m, TextClassifier &tc) : mux(&m), textClassifier(&tc) {
		this->_signature = "s:s";
		this->_help = "Detect method";
	}

	void execute(xmlrpc_c::paramList const &paramList, xmlrpc_c::value* const retvalP) {
		boost::shared_lock<boost::shared_mutex> lock(*mux);

		std::string const text(paramList.getString(0));

		*retvalP = xmlrpc_c::value_string(textClassifier->classify(text));
	}

private:
	boost::shared_mutex *mux;
	TextClassifier *textClassifier;
};
#endif

#if defined(HAVE_COMBINE)
class SynthesiseMethod : public xmlrpc_c::method {
public:
	SynthesiseMethod(boost::shared_mutex &m, ResourceBroker &rb, ConfigurationManager &cm) : mux(&m), resourceBroker(&rb), configurationManager(&cm) {
		this->_signature = "S:Ass";
		this->_help = "Synthesise method";
	}

	void execute(xmlrpc_c::paramList const &paramList, xmlrpc_c::value* const retvalP) {
		boost::shared_lock<boost::shared_mutex> lock(*mux);

		std::vector<xmlrpc_c::value> tv = paramList.getArray(0);

		if (tv.size() == 0) {
			throw xmlrpc_c::fault("Invalid parameter: the list of translations is empty");
		}

		std::vector<std::string> translations(tv.size());

		for (unsigned int i = 0; i < tv.size(); ++i) {
			xmlrpc_c::value value = tv[i];
			std::string translation = xmlrpc_c::value_string(value);
			translations[i] = translation;
		}

		std::string const srcLang = paramList.getString(1);
		std::string const destLang = paramList.getString(2);

		std::map<std::string, xmlrpc_c::value> ret;

	    std::pair<std::string, xmlrpc_c::value> translation("synthesis", xmlrpc_c::value_string(Synthesiser::synthesise(*resourceBroker, translations, srcLang, destLang)));
	    ret.insert(translation);

		*retvalP = xmlrpc_c::value_struct(ret);
	}

private:
	boost::shared_mutex *mux;
	ResourceBroker *resourceBroker;
	ConfigurationManager *configurationManager;
};
#endif

class LanguagePairsMethod : public xmlrpc_c::method {
public:
	LanguagePairsMethod(boost::shared_mutex &m, ModesManager &mm) : mux(&m), modesManager(&mm) {
		this->_signature = "A:";
		this->_help = "LanguagePairs method";
	}

	void execute(xmlrpc_c::paramList const &paramList, xmlrpc_c::value* const retvalP) {
		boost::shared_lock<boost::shared_mutex> lock(*mux);

		std::vector<xmlrpc_c::value> ret;
		ModesManager::ModeMapType modes = modesManager->getModes();

		for (ModesManager::ModeMapType::iterator it = modes.begin(); it != modes.end(); ++it) {
			std::map<std::string, xmlrpc_c::value> spair;
            std::string mode = (*it).first;
            size_t sep = mode.find("-", 0);

            std::string srcLang = mode.substr(0, sep);
            std::string destLang = mode.substr(sep + 1, mode.size());

            std::pair<std::string, xmlrpc_c::value> src("srcLang", xmlrpc_c::value_string(srcLang));
            std::pair<std::string, xmlrpc_c::value> dest("destLang", xmlrpc_c::value_string(destLang));

			spair.insert(src);
			spair.insert(dest);

			ret.push_back(xmlrpc_c::value_struct(spair));
		}

		*retvalP = xmlrpc_c::value_array(ret);
	}

private:
	boost::shared_mutex *mux;
	ModesManager *modesManager;
};

ApertiumXMLRPCService::ApertiumXMLRPCService(ConfigurationManager &cm, ModesManager &mm, ResourceBroker &rb,
#if defined(HAVE_LIBTEXTCAT)
		TextClassifier &tc,
#endif
		Statistics *s) : configurationManager(&cm) {
	xmlrpcRegistry = new xmlrpc_c::registry;


	xmlrpc_c::methodPtr const TranslateMethodP(new TranslateMethod(serviceMutex, rb, mm,
#if defined(HAVE_LIBTEXTCAT)
			tc,
#endif
			s));
	xmlrpcRegistry->addMethod(TRANSLATE_NAME, TranslateMethodP);

#if defined(HAVE_LIBTEXTCAT)
	xmlrpc_c::methodPtr const DetectMethodP(new DetectMethod(serviceMutex, tc));
	xmlrpcRegistry->addMethod(DETECT_NAME, DetectMethodP);
#endif

#if defined(HAVE_COMBINE)
	xmlrpc_c::methodPtr const SynthesiseMethodP(new SynthesiseMethod(serviceMutex, rb, cm));
	xmlrpcRegistry->addMethod(SYNTHESISE_NAME, SynthesiseMethodP);
#endif

	xmlrpc_c::methodPtr const LanguagePairsMethodP(new LanguagePairsMethod(serviceMutex, mm));
	xmlrpcRegistry->addMethod(LANGUAGEPAIRS_NAME, LanguagePairsMethodP);

	abyssServer = new xmlrpc_c::serverAbyss(xmlrpc_c::serverAbyss::constrOpt()
		.registryP(xmlrpcRegistry)
		.portNumber(configurationManager->getServerPort())
		.keepaliveTimeout(configurationManager->getKeepaliveTimeout())
		.keepaliveMaxConn(configurationManager->getKeepaliveMaxConn())
		.timeout(configurationManager->getTimeout()));
}

ApertiumXMLRPCService::~ApertiumXMLRPCService() {
	boost::unique_lock<boost::shared_mutex> lock(serviceMutex);

	delete abyssServer;
	delete xmlrpcRegistry;
}

void ApertiumXMLRPCService::start() {
	{
		std::stringstream ssmsg;
		ssmsg << "Starting Apertium XML-RPC service on port "
				<< (configurationManager->getServerPort());
		Logger::Instance()->trace(Logger::Info, ssmsg.str());
	}
	abyssServer->run();
}

void ApertiumXMLRPCService::stop() {
	Logger::Instance()->trace(Logger::Info, "Terminating the Apertium XML-RPC service..");

	//abyssServer->terminate();
}
