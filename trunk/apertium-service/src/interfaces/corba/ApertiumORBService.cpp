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

#include "config.h"

#include <iostream>
#include <string>

#include "ApertiumORBService.h"

#include "utils/Logger.h"

ApertiumORB_i::ApertiumORB_i() {

}

ApertiumORB_i::~ApertiumORB_i() {

}

ApertiumService::TranslationType* ApertiumORB_i::translate(const char* text, const char* srcLang, const char* destLang) {
	ApertiumService::TranslationType *ret = new ApertiumService::TranslationType();
	ret->translation("translation");
	return ret;
}

char* ApertiumORB_i::detect(const char* mesg) {
	char *ret = CORBA::string_dup("xx");
	return ret;
}

ApertiumService::LanguagePairsType* ApertiumORB_i::languagePairs() {
	ApertiumService::LanguagePairsType *ret = new ApertiumService::LanguagePairsType();

	ret->length(1);

	ApertiumService::LanguagePairType *pair = new ApertiumService::LanguagePairType();
	pair->srcLang = "xx";
	pair->destLang = "yy";

	(*ret)[0] = *pair;

	return ret;
}

#if defined(HAVE_LIBTEXTCAT)
ApertiumORBService::ApertiumORBService(int argc, char** argv, ConfigurationManager &cm,ModesManager &mm,
		ResourceBroker &ob, TextClassifier &tc, Statistics *s) {
#else
	ApertiumORBService::ApertiumORBService(int argc, char** argv, ConfigurationManager &cm,ModesManager &mm,
			ResourceBroker &ob, Statistics *s) {
#endif

	orb = CORBA::ORB_init(argc, argv);
	obj = orb->resolve_initial_references("RootPOA");

	poa = PortableServer::POA::_narrow(obj);

	ApertiumORB_i* myApertiumORB_i = new ApertiumORB_i();

	PortableServer::ObjectId_var myApertiumORB_iid = poa->activate_object(myApertiumORB_i);

	obj = myApertiumORB_i->_this();

	{
		CORBA::Object_var ref = myApertiumORB_i->_this();
		CORBA::String_var sior(orb->object_to_string(ref));

		stringstream ssmsg;
		ssmsg << "Starting the Apertium CORBA service: IDL object ApertiumService::Apertium IOR = '" << (char*) sior << "'";
		Logger::Instance()->trace(Logger::INFO, ssmsg.str());
	}

	//if (!ApertiumORBService::bindObjectToName(orb, obj))
	//	return 1;

	PortableServer::POAManager_var pman = poa->the_POAManager();
	pman->activate();
}

ApertiumORBService::~ApertiumORBService() {
	poa->destroy(true, true);

	orb->shutdown(true);
	orb->destroy();
}

void ApertiumORBService::start() {
	orb->run();
}

CORBA::Boolean ApertiumORBService::bindObjectToName(CORBA::ORB_ptr orb, CORBA::Object_ptr objref, char *context, char *name) {
	CosNaming::NamingContext_var rootContext;

	CORBA::Object_var obj;
	obj = orb->resolve_initial_references("NameService");

	rootContext = CosNaming::NamingContext::_narrow(obj);

	if (CORBA::is_nil(rootContext)) {
		cerr << "Failed to narrow the root naming context." << endl;
		return 0;
	}

	CosNaming::Name contextName;
	contextName.length(1);
	contextName[0].id = (const char*) "test";
	contextName[0].kind = (const char*) context;

	CosNaming::NamingContext_var testContext;

	try {
		testContext = rootContext->bind_new_context(contextName);
	} catch (CosNaming::NamingContext::AlreadyBound& ex) {

		CORBA::Object_var obj;
		obj = rootContext->resolve(contextName);
		testContext = CosNaming::NamingContext::_narrow(obj);
		if (CORBA::is_nil(testContext)) {
			cerr << "Failed to narrow naming context." << endl;
			return 0;
		}
	}

	CosNaming::Name objectName;
	objectName.length(1);
	objectName[0].id = (const char*) name;
	objectName[0].kind = (const char*) "Object";

	try {
		testContext->bind(objectName, objref);
	} catch (CosNaming::NamingContext::AlreadyBound& ex) {
		testContext->rebind(objectName, objref);
	}

	return 1;
}
