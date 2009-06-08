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

#include <lttoolbox/lt_locale.h>
#include <boost/program_options.hpp>

#include "ApertiumServer.h"
#include "ConfigurationManager.h"
#include "AuthenticationManager.h"

#include "core/TextClassifier.h"
#include "core/Modes.h"

using namespace std;
namespace po = boost::program_options;

ApertiumServer *server = NULL;

void apertiumServerSignalHandler(int) {
	cerr << "SIGINT: Cleaning things up.." << endl;
	if (server) {
		server->stop();
	}
}

int main(int ac, char *av[]) {

	LtLocale::tryToSetLocale();

	try {
		po::options_description desc("Allowed options");
		desc.add_options()("help", "produce this help message")
		("maxThreads", po::value<int>(), "(int) set maximum number of threads")
		("serverPort", po::value<int>(), "(int) set server port")
		("modesDir", po::value<string>(), "(string) set modes' directory")
		("useSSL", po::value<bool>(), "(bool) enable or disable SSL")
		("confTextClassifier", po::value<string>(), "(string) set text classifier's configuration file")
		("confUsers", po::value<string>(), "(string) set users list file");

		po::variables_map vm;
		po::store(po::parse_command_line(ac, av, desc), vm);
		po::notify(vm);

		ConfigurationManager *conf = ConfigurationManager::Instance();

	    if (vm.count("help")) {
	        cout << desc << endl;
	        return(1);
	    }

	    if (vm.count("maxThreads")) {
	        cout << "Maximum number of threads was " << conf->getMaxThreads() << ", setting it to " << vm["maxThreads"].as<int>() << endl;
	        conf->setMaxThreads(vm["maxThreads"].as<int>());
	    }

	    if (vm.count("serverPort")) {
	        cout << "Server port was " << conf->getServerPort() <<  ", setting it to " << vm["serverPort"].as<int>() << endl;
	        conf->setMaxThreads(vm["serverPort"].as<int>());
	    }

	    if (vm.count("modesDir")) {
	        cout << "Modes directory was " << conf->getApertiumBase() <<  ", setting it to " << vm["modesDir"].as<string>() << endl;
	        conf->setApertiumBase(vm["modesDir"].as<string>());
	    }

	    if (vm.count("useSSL")) {
	        cout << "UseSSL flag was " << conf->getUseSsl() <<  ", setting it to " << vm["useSSL"].as<bool>() << endl;
	        conf->setUseSsl(vm["useSSL"].as<bool>());
	    }

	    if (vm.count("confTextClassifier")) {
	        cout << "Text Classifier's configuration file was " << conf->getConfTextClassifier() <<  ", setting it to " << vm["confTextClassifier"].as<string>() << endl;
	        conf->setConfTextClassifier(vm["confTextClassifier"].as<string>());
	    }

	    if (vm.count("confUsers")) {
	        cout << "Users' list file was " << conf->getConfUsers() <<  ", setting it to " << vm["confUsers"].as<string>() << endl;
	        conf->setConfUsers(vm["confUsers"].as<string>());
	    }

		AuthenticationManager::Instance(conf->getConfUsers());
		TextClassifier::Instance(conf->getConfTextClassifier());

	    Modes::Instance()->initPipe(conf->getApertiumBase());
	    Modes::Instance()->initXML(conf->getApertiumBase());

	    ::signal(SIGINT, &apertiumServerSignalHandler);

	    ApertiumServer s;

		server = &s;
		server->init();

	} catch (exception& e) {
		cerr << "error: " << e.what() << endl;
		return 1;
	} catch (...) {
		cerr << "Exception." << endl;
	}

	return (0);
}
