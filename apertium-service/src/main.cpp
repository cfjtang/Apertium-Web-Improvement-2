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
#include "ConfigurationReader.h"
#include "AuthenticationManager.h"

#include "Modes.h"

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
		("useSSL", po::value<bool>(), "(bool) enable or disable SSL");

		po::variables_map vm;
		po::store(po::parse_command_line(ac, av, desc), vm);
		po::notify(vm);

		ConfigurationReader *conf = ConfigurationReader::Instance();
		AuthenticationManager::Instance("users.xml");

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

	    ::signal(SIGINT, &apertiumServerSignalHandler);

	    Modes::Instance()->initPipe(ConfigurationReader::Instance()->getApertiumBase());

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
