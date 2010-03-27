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

#include <sys/types.h>

#include <iostream>
#include <exception>

#include <stdlib.h>
#include <unistd.h>

#include <lttoolbox/lt_locale.h>
#include <boost/thread.hpp>
#include <boost/program_options.hpp>
#include <boost/filesystem.hpp>

#include "interfaces/xmlrpc/ApertiumXMLRPCService.h"
//#include "interfaces/corba/ApertiumORBService.h"

#include "core/ResourceBroker.h"

#if defined(HAVE_LIBTEXTCAT)
#include "core/TextClassifier.h"
#endif

#include "core/ModesManager.h"

#include "utils/ConfigurationManager.h"
#include "utils/Logger.h"
#include "utils/Statistics.h"

#include "core/vislcg3/stdafx.h"
#include "core/vislcg3/Strings.h"
#include "core/vislcg3/icu_uoptions.h"
#include "core/vislcg3/Grammar.h"
#include "core/vislcg3/BinaryGrammar.h"
#include "core/vislcg3/ApertiumApplicator.h"

namespace po = boost::program_options;
namespace fs = boost::filesystem;

using CG3::CG3Quit;

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

	axs->stop();

	delete axs;
	delete mm;
	delete s;

#if defined(HAVE_LIBTEXTCAT)
	delete tc;
#endif

	delete rb;
	delete cm;

	delete logger;

	//free_strings();
	//free_keywords();
	//free_gbuffers();
	//free_flags();

	u_cleanup();
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


#if defined(HAVE_SYSLOG)
		("syslog,s", "enable sending messages to the system logger")
#endif

		("directory,d",	po::value<std::string>(), "(string) set configuration directory")
		("conf,c", po::value<std::string>(), "(string) set configuration file")

#if defined(HAVE_LIBTEXTCAT)
		("conftc,t", po::value<std::string>(), "(string) set text classifier's configuration file")
#endif

		("modes,m", po::value<std::string>(), "(string) set modes' directory")

		("port,p", po::value<unsigned int>(), "(uint) set XML-RPC service's port")
		("keepalivetimeout,k", po::value<unsigned int>(), "(uint) set maximum time in seconds that the server allows a connection to be open between RPCs.")
		("keepalivemaxconn,K", po::value<unsigned int>(), "(uint) set maximum number of RPCs that the server will execute on a single connection.")
		("timeout,T", po::value<unsigned int>(), "(uint) set maximum time in seconds the server will wait for the client to do anything while processing an RPC.")

		("highwatermark,w", po::value<unsigned int>(), "(uint) set high water mark")

#if defined(HAVE_FORK)
		("daemon,D", "run the service as a daemon")
#endif
		;

		po::variables_map vm;
		po::store(po::parse_command_line(ac, av, desc), vm);
		po::notify(vm);

		if (vm.count("help")) {
			cout << "Usage: " << PACKAGE_NAME << " [options]" << endl;
			cout << desc << endl;
			return (1);
		}

		if (vm.count("version")) {
			cout << PACKAGE_STRING << endl;
			return (1);
		}

		//CG3::init_gbuffers();
		//init_strings();
		//init_keywords();
		//init_flags();

		UErrorCode status = U_ZERO_ERROR;

        u_init(&status);
        if (U_FAILURE(status) && status != U_FILE_ACCESS_ERROR) {
                std::cerr << "Error: Cannot initialize ICU. Status = " << u_errorName(status) << std::endl;
                CG3Quit(1);
        }

        ucnv_setDefaultName("UTF-8");

		::atexit(cleanup);

		fs::path cd;

#if defined(ASCONFDIR)
		if (fs::is_directory(ASCONFDIR)) {
			cd = ASCONFDIR;
		} else {
			cd = "configuration";
		}
#else
		cd = "configuration";
#endif

		cd = fs::system_complete(cd);

	    if (vm.count("directory")) {
	        cout << "Configuration directory was " << cd <<  ", setting it to " << vm["directory"].as<std::string>() << endl;
	        cd = vm["directory"].as<std::string>();
	    }

	    if (!fs::is_directory(cd)) {
	    	cerr << "Error: " << cd << " doesn't exist or it isn't a directory." << endl;
	    	::exit(EXIT_FAILURE);
	    }

	    if (::chdir(cd.string().c_str()) < 0) {
	    	cerr << "Error: cannot change directory to " << cd << ": " << ::strerror(errno) << endl;
	    	::exit(EXIT_FAILURE);
	    }

	    fs::path cf = cd / "configuration.xml";


	    if (vm.count("conf")) {
	        cout << "Configuration file was " << cf <<  ", setting it to " << vm["conf"].as<std::string>() << endl;
	        cf = vm["conf"].as<std::string>();
	    }

	    cout << "Using the configuration file located in " << cf << endl;

	    cm = new ConfigurationManager(cf, cd);

#if defined(HAVE_LIBTEXTCAT)
	    if (vm.count("conftc")) {
	        cout << "Text Classifier's configuration file was " << cm->getConfTextClassifier() <<  ", setting it to " << vm["conftc"].as<std::string>() << endl;
	        cm->setConfTextClassifier(vm["conftc"].as<std::string>());
	    }
#endif

	    if (vm.count("modes")) {
	        cout << "Modes directory was " << cm->getApertiumBase() <<  ", setting it to " << vm["modes"].as<std::string>() << endl;
	        cm->setApertiumBase(vm["modes"].as<std::string>());
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

#if defined(HAVE_SYSLOG)
		if (vm.count("syslog")) {
			cout << "Sending messages to the system logger." << endl;
			logger->setSyslogUsage(true);
		}
#endif

#if defined(HAVE_FORK)
		if (vm.count("daemon")) {
			cout << "Running the service as a daemon.." << endl;

			switch (::fork()) {
			case -1:
				throw ApertiumRuntimeException(::strerror(errno));
				break;
			case 0:
				break;
			default:
				_exit(0);
				return 0;
				break;
			}
			logger->setConsoleUsage(false);
		}
#endif

	} catch (const std::exception& e) {
		cerr << "Exception: " << e.what() << endl;
		return(1);
	} catch (...) {
		cerr << "Exception." << endl;
	}

	try {

#if defined(HAVE_LIBTEXTCAT)
		tc = new TextClassifier(cm->getConfTextClassifier());
#endif

		rb = new ResourceBroker(cm->getHighWaterMark());

		s = new Statistics(*rb);

		mm = new ModesManager;
	    mm->initPipe(cm->getApertiumBase());

	    ConfigurationManager::EagerlyLoadsType els = cm->getEagerlyLoads();
	    for (ConfigurationManager::EagerlyLoadsType::iterator it = els.begin(); it != els.end(); ++it) {
	    	std::pair<std::string, std::string> p = (*it).first;
	    	unsigned int qty = (*it).second;

	    	Translator::eagerlyLoad(*rb, *mm, p.first, p.second, qty);
	    }

	    axs = new ApertiumXMLRPCService(*cm, *mm, *rb,
#if defined(HAVE_LIBTEXTCAT)
	   *tc,
#endif
	    s);

	    axs->start();

	    //boost::thread xmlrpcThread(boost::bind(&ApertiumXMLRPCService::start, axs));
	    //xmlrpcThread.join();

	} catch (const std::exception& e) {
		cerr << "Exception: " << e.what() << endl;
		return(1);
	} catch (...) {
		cerr << "Exception." << endl;
	}

	return (EXIT_SUCCESS);
}
