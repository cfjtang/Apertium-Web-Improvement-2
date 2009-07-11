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
#include "utils/AuthenticationManager.h"

#include "core/ObjectBroker.h"
#include "core/TextClassifier.h"
#include "core/ModesManager.h"

#include "utils/Logger.h"
#include "utils/Statistics.h"

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
Statistics *statistics = NULL;

ApertiumServer *apertiumServer = NULL;
ConfigurationManager *configurationManager = NULL;

AuthenticationManager *authenticationManager = NULL;
ObjectBroker *objectBroker = NULL;
TextClassifier *textClassifier = NULL;
ModesManager *modesManager = NULL;

boost::mutex cleanupMutex;

void cleanup(void) {
	boost::mutex::scoped_lock Lock(cleanupMutex);

	cerr << "Cleaning things up.." << endl;

	delete apertiumServer;
	apertiumServer = NULL;

	delete configurationManager;
	configurationManager = NULL;

	delete authenticationManager;
	authenticationManager = NULL;

	delete objectBroker;
	objectBroker = NULL;

	delete textClassifier;
	textClassifier = NULL;

	delete modesManager;
	modesManager = NULL;

	delete statistics;
	statistics = NULL;

	delete logger;
	logger = NULL;

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

		fs::path confDir = "conf";

	    if (vm.count("confDir")) {
	        cout << "Configuration directory was " << confDir <<  ", setting it to " << vm["confDir"].as<string>() << endl;
	        confDir = vm["confDir"].as<string>();
	    }

	    fs::path confFile = confDir / "configuration.xml";

	    if (vm.count("confFile")) {
	        cout << "Configuration file was " << confFile <<  ", setting it to " << vm["confFile"].as<string>() << endl;
	        confFile = vm["confFile"].as<string>();
	    }

	    configurationManager = new ConfigurationManager(confFile, confDir);

	    logger = Logger::Instance();
	    logger->setVerbosity(2);

	    statistics = Statistics::Instance();

	    if (vm.count("maxThreads")) {
	        cout << "Maximum number of threads was " << configurationManager->getMaxThreads() << ", setting it to " << vm["maxThreads"].as<int>() << endl;
	        configurationManager->setMaxThreads(vm["maxThreads"].as<int>());
	    }

	    if (vm.count("serverPort")) {
	        cout << "Server port was " << configurationManager->getServerPort() <<  ", setting it to " << vm["serverPort"].as<int>() << endl;
	        configurationManager->setMaxThreads(vm["serverPort"].as<int>());
	    }

	    if (vm.count("modesDir")) {
	        cout << "Modes directory was " << configurationManager->getApertiumBase() <<  ", setting it to " << vm["modesDir"].as<string>() << endl;
	        configurationManager->setApertiumBase(vm["modesDir"].as<string>());
	    }

	    if (vm.count("useSSL")) {
	        cout << "UseSSL flag was " << configurationManager->getUseSsl() <<  ", setting it to " << vm["useSSL"].as<bool>() << endl;
	        configurationManager->setUseSsl(vm["useSSL"].as<bool>());
	    }

	    if (vm.count("confTextClassifier")) {
	        cout << "Text Classifier's configuration file was " << configurationManager->getConfTextClassifier() <<  ", setting it to " << vm["confTextClassifier"].as<string>() << endl;
	        configurationManager->setConfTextClassifier(vm["confTextClassifier"].as<string>());
	    }

	    if (vm.count("confUsers")) {
	        cout << "Users' list file was " << configurationManager->getConfUsers() <<  ", setting it to " << vm["confUsers"].as<string>() << endl;
	        configurationManager->setConfUsers(vm["confUsers"].as<string>());
	    }

	    if (vm.count("verbosity")) {
	        cout << "Verbosity was " << logger->getVerbosity() <<  ", setting it to " << vm["verbosity"].as<int>() << endl;
	        logger->setVerbosity(vm["confUsers"].as<int>());
	    }

		authenticationManager = new AuthenticationManager(configurationManager->getConfUsers());

		textClassifier = new TextClassifier(configurationManager->getConfTextClassifier());

		objectBroker = new ObjectBroker();
		modesManager = new ModesManager();

	    modesManager->initPipe(configurationManager->getApertiumBase());
	    //modesManager->initXML(configurationManager->getApertiumBase());

	    apertiumServer = new ApertiumServer(configurationManager, modesManager,
	    		objectBroker, textClassifier, authenticationManager);

	} catch (const std::exception& e) {
		cerr << "Exception: " << e.what() << endl;
		return 1;
	} catch (...) {
		cerr << "Exception." << endl;
	}

	return (0);
}
