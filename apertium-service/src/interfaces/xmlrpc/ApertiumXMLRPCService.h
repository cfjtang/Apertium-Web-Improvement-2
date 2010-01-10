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

#ifndef APERTIUMXMLRPCSERVICE_H_
#define APERTIUMXMLRPCSERVICE_H_

#include "config.h"

#include <iostream>
#include <string>

#include <exception>
#include <cstdlib>

#include <xmlrpc-c/base.hpp>
#include <xmlrpc-c/registry.hpp>
#include <xmlrpc-c/server_abyss.hpp>

#include <boost/thread.hpp>
#include <boost/unordered/unordered_map.hpp>
#include <boost/thread/shared_mutex.hpp>
#include <boost/thread/locks.hpp>

#include "core/ModesManager.h"
#include "core/Translator.h"

#if defined(HAVE_LIBTEXTCAT)
#include "core/TextClassifier.h"
#endif

#if defined(HAVE_COMBINE)
#include "core/Synthesiser.h"
#endif

#include "utils/ConfigurationManager.h"
#include "utils/Logger.h"
#include "utils/Statistics.h"

/**
 * The class ApertiumXMLRPCService implements a service using the XML-RPC protocol
 * and exposing Apertium's capabilities for translation and language detection.
 * XML-RPC is a remote procedure call protocol which uses XML to encode its calls
 * and HTTP as a transport mechanism.
 */
class ApertiumXMLRPCService {
public:

	enum FaultCodes { INVALID_PARAMS = 3 };

#if defined(HAVE_LIBTEXTCAT)
	ApertiumXMLRPCService(ConfigurationManager&, ModesManager&, ResourceBroker&, TextClassifier&, Statistics* = NULL);
#else
	ApertiumXMLRPCService(ConfigurationManager&, ModesManager&, ResourceBroker&, Statistics* = NULL);
#endif

	virtual ~ApertiumXMLRPCService();

	void start();
	void stop();

private:
    static const std::string TRANSLATE_NAME;

#if defined(HAVE_LIBTEXTCAT)
    static const std::string DETECT_NAME;
#endif

#if defined(HAVE_COMBINE)
    static const string SYNTHESISE_NAME;
#endif

    static const std::string LANGUAGEPAIRS_NAME;

    boost::shared_mutex serviceMutex;

	xmlrpc_c::registry* xmlrpcRegistry;
	xmlrpc_c::serverAbyss* abyssServer;

    ConfigurationManager *configurationManager;
};

#endif /* APERTIUMXMLRPCSERVICE_H_ */
