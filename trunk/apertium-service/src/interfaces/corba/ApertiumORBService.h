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
 * The class ApertiumORBService implements a service using the CORBA standard
 * and exposing Apertium's capabilities for translation and language detection.
 * The Common Object Request Broker Architecture (CORBA) is a standard  that
 * enables software components written in multiple computer languages and running
 * on multiple computers to work together.
 */

#ifndef APERTIUMORBSERVICE_H_
#define APERTIUMORBSERVICE_H_

#include "config.h"

#include <iostream>
#include "stub/apertium.hh"

#include "core/ResourceBroker.h"
#include "core/ModesManager.h"
#include "core/Translator.h"

#if defined(HAVE_LIBTEXTCAT)
#include "core/TextClassifier.h"
#endif

#include "utils/ConfigurationManager.h"
#include "utils/Logger.h"
#include "utils/Statistics.h"

using namespace std;

class ApertiumORB_i: public POA_ApertiumService::Apertium {
public:
	ApertiumORB_i();
	virtual ~ApertiumORB_i();

	ApertiumService::TranslationType* translate(const char*, const char*, const char*);
	char* detect(const char*);
	ApertiumService::LanguagePairsType* languagePairs();
};


class ApertiumORBService {
public:

#if defined(HAVE_LIBTEXTCAT)
	ApertiumORBService(int, char**, ConfigurationManager&, ModesManager&, ResourceBroker&, TextClassifier&, Statistics*);
#else
	ApertiumORBService(int, char**, ConfigurationManager&, ModesManager&, ResourceBroker&, Statistics*);
#endif

	virtual ~ApertiumORBService();

	void start();

private:
	static CORBA::Boolean bindObjectToName(CORBA::ORB_ptr, CORBA::Object_ptr, char*, char*);

	CORBA::ORB_var orb;
	CORBA::Object_var obj;
	PortableServer::POA_var poa;
};

#endif /* APERTIUMORBSERVICE_H_ */
