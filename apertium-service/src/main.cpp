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
 * Service's main function.
 */

#include "config.h"

#include <iostream>
#include <exception>

#include <stdlib.h>

#include <lttoolbox/lt_locale.h>
#include <boost/thread.hpp>
#include <boost/program_options.hpp>
#include <boost/filesystem.hpp>

#include "core/ResourceBroker.h"

#if defined(HAVE_LIBTEXTCAT)
#include "core/TextClassifier.h"
#endif

#include "core/ModesManager.h"

#include "utils/ConfigurationManager.h"
#include "utils/Logger.h"
#include "utils/Statistics.h"

#include "core/cg/stdafx.h"
#include "core/cg/icu_uoptions.h"
#include "core/cg/Grammar.h"
#include "core/cg/BinaryGrammar.h"
#include "core/cg/ApertiumApplicator.h"

#include "interfaces/xmlrpc/ApertiumXMLRPCService.h"
//#include "interfaces/corba/ApertiumORBService.h"

using namespace std;
namespace po = boost::program_options;
namespace fs = boost::filesystem;

Logger *logger = NULL;

ConfigurationManager *cm = NULL;
ResourceBroker *rb = NULL;

#if defined(HAVE_LIBTEXTCAT)
TextClassifier *tc = NULL;
#endif

Statistics *s = NULL;
ModesManager *mm = NULL;
ApertiumXMLRPCService *axs = NULL;

boost::mutex cleanupMutex;

void cleanup(void) {
	boost::mutex::scoped_lock Lock(cleanupMutex);

	cerr << "Cleaning things up.." << endl;

	delete axs;
	delete mm;
	delete s;

#if defined(HAVE_LIBTEXTCAT)
	delete tc;
#endif

	delete rb;
	delete cm;
	delete logger;

	free_strings();
	free_keywords();
	free_gbuffers();
	free_flags();

	u_cleanup();
}

void signalHandler(int) {
	cerr << "SIGINT received." << endl;
	exit(EXIT_SUCCESS);
}

int main(int ac, char *av[]) {
	LtLocale::tryToSetLocale();

	ucnv_setDefaultName("UTF-8");

	try {
		po::options_description desc("Allowed options:");

		desc.add_options()

		("help,h", "produce this help message")
		("version,V", "show version")

		("verbosity,v", po::value<unsigned int>(),	"(uint) set verbosity")

		("directory,d",	po::value<string>(), "(string) set configuration directory")
		("conf,c", po::value<string>(), "(string) set configuration file")

#if defined(HAVE_LIBTEXTCAT)
		("conftc,t", po::value<string>(), "(string) set text classifier's configuration file")
#endif

		("modes,m", po::value<string>(), "(string) set modes' directory")

		("port,p", po::value<unsigned int>(), "(uint) set XML-RPC service's port")
		("keepalivetimeout,k", po::value<unsigned int>(), "(uint) set maximum time in seconds that the server allows a connection to be open between RPCs.")
		("keepalivemaxconn,K", po::value<unsigned int>(), "(uint) set maximum number of RPCs that the server will execute on a single connection.")
		("timeout,T", po::value<unsigned int>(), "(uint) set maximum time in seconds the server will wait for the client to do anything while processing an RPC.")

		("highwatermark,w", po::value<unsigned int>(), "(uint) set high water mark")

		("daemon,D", "run the service as a daemon");

		po::variables_map vm;
		po::store(po::parse_command_line(ac, av, desc), vm);
		po::notify(vm);

		if (vm.count("help")) {
			cout << desc << endl;
			return (1);
		}

		if (vm.count("version")) {
			cout << PACKAGE_STRING << endl;
			return (1);
		}

		init_gbuffers();
		init_strings();
		init_keywords();
		init_flags();

		::atexit(cleanup);
		::signal(SIGINT, &signalHandler);

#if defined(ASCONFDIR)
		fs::path cd = ASCONFDIR;
#else
		fs::path cd = "configuration";
#endif

	    if (vm.count("directory")) {
	        cout << "Configuration directory was " << cd <<  ", setting it to " << vm["directory"].as<string>() << endl;
	        cd = vm["directory"].as<string>();
	    }

	    fs::path cf = cd / "configuration.xml";

	    if (vm.count("conf")) {
	        cout << "Configuration file was " << cf <<  ", setting it to " << vm["conf"].as<string>() << endl;
	        cf = vm["conf"].as<string>();
	    }

	    cout << "Using the configuration file located in " << cf << endl;

	    cm = new ConfigurationManager(cf, cd);

#if defined(HAVE_LIBTEXTCAT)
	    if (vm.count("conftc")) {
	        cout << "Text Classifier's configuration file was " << cm->getConfTextClassifier() <<  ", setting it to " << vm["conftc"].as<string>() << endl;
	        cm->setConfTextClassifier(vm["conftc"].as<string>());
	    }
#endif

	    if (vm.count("modes")) {
	        cout << "Modes directory was " << cm->getApertiumBase() <<  ", setting it to " << vm["modes"].as<string>() << endl;
	        cm->setApertiumBase(vm["modes"].as<string>());
	    }

	    logger = Logger::Instance();

	    if (vm.count("port")) {
	        cout << "Server port was " << cm->getServerPort() <<  ", setting it to " << vm["port"].as<unsigned int>() << endl;
	        cm->setServerPort(vm["port"].as<unsigned int>());
	    }

	    if (vm.count("keepalivetimeout")) {
	        cout << "Maximum time in seconds that the server allows a connection to be open between RPCs was " << cm->getKeepaliveTimeout() << ", setting it to " << vm["keepalivetimeout"].as<unsigned int>() << endl;
	        cm->setKeepaliveTimeout(vm["keepalivetimeout"].as<unsigned int>());
	    }

	    if (vm.count("keepalivemaxconn")) {
	        cout << "Maximum number of RPCs that the server will execute on a single connection was " << cm->getKeepaliveMaxConn() << ", setting it to " << vm["keepalivemaxconn"].as<unsigned int>() << endl;
	        cm->setKeepaliveMaxConn(vm["keepalivemaxconn"].as<unsigned int>());
	    }

	    if (vm.count("timeout")) {
	        cout << "Maximum time in seconds the server will wait for the client to do anything while processing an RPC was " << cm->getTimeout() << ", setting it to " << vm["timeout"].as<unsigned int>() << endl;
	        cm->setTimeout(vm["timeout"].as<unsigned int>());
	    }

	    if (vm.count("highwatermark")) {
	        cout << "HighWater mark was " << cm->getHighWaterMark() <<  ", setting it to " << vm["highWaterMark"].as<unsigned int>() << endl;
	        cm->setHighWaterMark(vm["highWaterMark"].as<unsigned int>());
	    }

	    if (vm.count("verbosity")) {
	        cout << "Verbosity was " << logger->getVerbosity() <<  ", setting it to " << vm["verbosity"].as<unsigned int>() << endl;
	        logger->setVerbosity(vm["verbosity"].as<unsigned int>());
	    }

		if (vm.count("daemon")) {
			switch (::fork()) {

			case -1: {
				throw ApertiumRuntimeException(::strerror(errno));
			}
				break;

			default: {
				// parent
				_exit(0);
				return 0;
			}
				break;

			case 0: {
				// child
			}
				break;
			}
		}

#if defined(HAVE_LIBTEXTCAT)
		tc = new TextClassifier(cm->getConfTextClassifier());
#endif

		rb = new ResourceBroker(cm->getHighWaterMark());

		s = new Statistics(*rb);

		mm = new ModesManager();
	    mm->initPipe(cm->getApertiumBase());

#if defined(HAVE_LIBTEXTCAT)
	    axs = new ApertiumXMLRPCService(*cm, *mm, *rb, *tc, s);
#else
	    axs = new ApertiumXMLRPCService(*cm, *mm, *rb, s);
#endif

	    //ApertiumORBService aos(ac, av, cm, mm, rb, tc, &s);

	    //boost::thread xmlrpcThread(boost::bind(&ApertiumXMLRPCService::start, axs));
	    //boost::thread orbThread(boost::bind(&ApertiumORBService::start, &aos));

	    //xmlrpcThread.join();
	    //orbThread.join();

	    axs->start();

	    /*
	} catch (CORBA::NO_RESOURCES&) {
		cerr << "Caught NO_RESOURCES exception. You must configure omniORB with the location of the naming service." << endl;
		return 0;
	} catch (CORBA::ORB::InvalidName&) {
		cerr << "Service required is invalid [does not exist]." << endl;
		return 0;
	} catch (CORBA::TRANSIENT&) {
		cerr << "Caught system exception TRANSIENT -- unable to contact the server." << endl;
	} catch (CORBA::SystemException& ex) {
		cerr << "Caught a CORBA::" << ex._name() << endl;
	} catch (CORBA::Exception& ex) {
		cerr << "Caught CORBA::Exception: " << ex._name() << endl;
	} catch (omniORB::fatalException& fe) {
		cerr << "Caught omniORB::fatalException:" << endl;
		cerr << "  file: " << fe.file() << endl;
		cerr << "  line: " << fe.line() << endl;
		cerr << "  mesg: " << fe.errmsg() << endl;
		*/

	} catch (const std::exception& e) {
		cerr << "Exception: " << e.what() << endl;
		return(1);
	} catch (...) {
		cerr << "Exception." << endl;
	}

	return (0);
}
