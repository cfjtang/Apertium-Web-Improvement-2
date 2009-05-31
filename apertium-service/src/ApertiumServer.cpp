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
#include <signal.h>

#include <libiqxmlrpc/libiqxmlrpc.h>

#include "ApertiumServer.h"

#include "ConfigurationReader.h"

#include "ApertiumTranslate.h"
#include "ApertiumListLanguagePairs.h"

#include "ApertiumTest.h"

#include "ApertiumAuthPlugin.h"
#include "ApertiumLogInterceptor.h"

using namespace std;

ApertiumServer::ApertiumServer() {
	executorFactory = NULL;
	server = NULL;
}

ApertiumServer::~ApertiumServer() {
	stop();
}

iqxmlrpc::Executor_factory_base* ApertiumServer::buildExecutorFactory(unsigned int nThreads) {
	iqxmlrpc::Executor_factory_base *ret = NULL;
    if (nThreads > 1) {
    	// I will probably have to implement my own iqxmlrpc::Executor_factory_base
    	// to manage in my own way scheduling, priorities etc.
        ret = new iqxmlrpc::Pool_executor_factory(nThreads);
    } else {
    	ret = new iqxmlrpc::Serial_executor_factory();
    }
    return(ret);
}

void ApertiumServer::init() {
	ConfigurationReader *conf = ConfigurationReader::Instance();

	if (!executorFactory) {
		executorFactory = buildExecutorFactory(conf->getMaxThreads());
	}

	if (!server) {
		cout << "Starting server on port: " << conf->getServerPort() << endl;

		if (conf->getUseSsl()) {
			iqnet::ssl::ctx = iqnet::ssl::Ctx::server_only("data/cert.pem",	"data/pk.pem");
			server = new iqxmlrpc::Https_server(conf->getServerPort(), executorFactory);
		} else {
			server = new iqxmlrpc::Http_server(conf->getServerPort(), executorFactory);
		}

		iqxmlrpc::register_method<ApertiumTranslate>(*server, "translate");
		iqxmlrpc::register_method<ApertiumListLanguagePairs>(*server, "listLanguagePairs");

		//
		iqxmlrpc::register_method<ApertiumTest>(*server, "test");
		//

		ApertiumAuthPlugin apertiumAuthPlugin;

		server->push_interceptor(new ApertiumLogInterceptor());
		server->set_auth_plugin(apertiumAuthPlugin);

		server->log_errors(&std::cerr);
		server->enable_introspection();

		server->set_verification_level(iqxmlrpc::http::HTTP_CHECK_WEAK);

		server->work();
	}
}

void ApertiumServer::stop() {
	if (server) {
		server->set_exit_flag();
		//delete server;
		server = NULL;
	}

	if (executorFactory) {
		//delete executorFactory;
		executorFactory = NULL;
	}
}
