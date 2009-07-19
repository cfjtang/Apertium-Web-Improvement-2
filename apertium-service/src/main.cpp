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

#include <iostream>
#include <exception>

#include <stdlib.h>

#include <lttoolbox/lt_locale.h>
#include <boost/thread.hpp>
#include <boost/program_options.hpp>
#include <boost/filesystem.hpp>

#include "ApertiumServer.h"

#include "core/ObjectBroker.h"
#include "core/TextClassifier.h"
#include "core/ModesManager.h"

#include "utils/Logger.h"
#include "utils/Statistics.h"
#include "utils/AuthenticationManager.h"

#include "cg/stdafx.h"
#include "cg/icu_uoptions.h"
#include "cg/Recycler.h"
#include "cg/Grammar.h"
#include "cg/BinaryGrammar.h"
#include "cg/ApertiumApplicator.h"

using namespace std;
namespace po = boost::program_options;
namespace fs = boost::filesystem;

Logger *logger = NULL;

ApertiumServer *as = NULL;
ConfigurationManager *cm = NULL;

AuthenticationManager *am = NULL;
ObjectBroker *ob = NULL;
TextClassifier *tc = NULL;
ModesManager *mm = NULL;

Statistics *s = NULL;

boost::mutex cleanupMutex;

void cleanup(void) {
	boost::mutex::scoped_lock Lock(cleanupMutex);

	cerr << "Cleaning things up.." << endl;

	delete as;
	delete cm;
	delete am;
	delete ob;
	delete tc;
	delete mm;
	delete s;

	delete logger;

	free_strings();
	free_keywords();
	free_gbuffers();
	free_flags();

	CG3::Recycler::cleanup();

	u_cleanup();
}

void apertiumServerSignalHandler(int) {
	cerr << "SIGINT received." << endl;
	exit(EXIT_SUCCESS);
}

int main(int ac, char *av[]) {

	LtLocale::tryToSetLocale();

	ucnv_setDefaultName("UTF-8");

	try {
		po::options_description desc("Allowed options");

		desc.add_options()
		("help", "produce this help message")
		("confDir",	po::value<string>(), "(string) set configuration directory")
		("confFile", po::value<string>(), "(string) set configuration file")
		("maxThreads", po::value<int>(), "(int) set maximum number of threads")
		("serverPort", po::value<int>(), "(int) set server port")
		("modesDir", po::value<string>(), "(string) set modes' directory")
		("useSSL", po::value<bool>(), "(bool) enable or disable SSL")
		("confTextClassifier", po::value<string>(), "(string) set text classifier's configuration file")
		("confUsers", po::value<string>(), "(string) set users list file")
		("verbosity", po::value<int>(),	"(int) set verbosity");

		po::variables_map vm;
		po::store(po::parse_command_line(ac, av, desc), vm);
		po::notify(vm);

		if (vm.count("help")) {
			cout << desc << endl;
			return (1);
		}

		CG3::Recycler::instance();

		init_gbuffers();
		init_strings();
		init_keywords();
		init_flags();

		::atexit(cleanup);
		::signal(SIGINT, &apertiumServerSignalHandler);

		fs::path cd = "conf";

	    if (vm.count("confDir")) {
	        cout << "Configuration directory was " << cd <<  ", setting it to " << vm["confDir"].as<string>() << endl;
	        cd = vm["confDir"].as<string>();
	    }

	    fs::path cf = cd / "configuration.xml";

	    if (vm.count("confFile")) {
	        cout << "Configuration file was " << cf <<  ", setting it to " << vm["confFile"].as<string>() << endl;
	        cf = vm["confFile"].as<string>();
	    }

	    cm = new ConfigurationManager(cf, cd);

	    logger = Logger::Instance();
	    logger->setVerbosity(2);

	    if (vm.count("maxThreads")) {
	        cout << "Maximum number of threads was " << cm->getMaxThreads() << ", setting it to " << vm["maxThreads"].as<int>() << endl;
	        cm->setMaxThreads(vm["maxThreads"].as<int>());
	    }

	    if (vm.count("serverPort")) {
	        cout << "Server port was " << cm->getServerPort() <<  ", setting it to " << vm["serverPort"].as<int>() << endl;
	        cm->setMaxThreads(vm["serverPort"].as<int>());
	    }

	    if (vm.count("modesDir")) {
	        cout << "Modes directory was " << cm->getApertiumBase() <<  ", setting it to " << vm["modesDir"].as<string>() << endl;
	        cm->setApertiumBase(vm["modesDir"].as<string>());
	    }

	    if (vm.count("useSSL")) {
	        cout << "UseSSL flag was " << cm->getUseSsl() <<  ", setting it to " << vm["useSSL"].as<bool>() << endl;
	        cm->setUseSsl(vm["useSSL"].as<bool>());
	    }

	    if (vm.count("confTextClassifier")) {
	        cout << "Text Classifier's configuration file was " << cm->getConfTextClassifier() <<  ", setting it to " << vm["confTextClassifier"].as<string>() << endl;
	        cm->setConfTextClassifier(vm["confTextClassifier"].as<string>());
	    }

	    if (vm.count("confUsers")) {
	        cout << "Users' list file was " << cm->getConfUsers() <<  ", setting it to " << vm["confUsers"].as<string>() << endl;
	        cm->setConfUsers(vm["confUsers"].as<string>());
	    }

	    if (vm.count("verbosity")) {
	        cout << "Verbosity was " << logger->getVerbosity() <<  ", setting it to " << vm["verbosity"].as<int>() << endl;
	        logger->setVerbosity(vm["confUsers"].as<int>());
	    }

		am = new AuthenticationManager(cm->getConfUsers());

		tc = new TextClassifier(cm->getConfTextClassifier());

		ob = new ObjectBroker();
		mm = new ModesManager();

	    mm->initPipe(cm->getApertiumBase());

	    as = new ApertiumServer(cm, mm,	ob, tc, am, s);

	} catch (const std::exception& e) {
		cerr << "Exception: " << e.what() << endl;
		return 1;
	} catch (...) {
		cerr << "Exception." << endl;
	}

	return (0);
}
