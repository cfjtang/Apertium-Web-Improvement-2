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
#include <string>
#include <signal.h>

#include "ApertiumServer.h"
#include "utils/Logger.h"

#include "ApertiumTranslate.h"
#include "ApertiumLanguagePairs.h"

#include "ApertiumDetect.h"

#include <boost/bind.hpp>

using namespace std;

ApertiumServer::ApertiumServer(ConfigurationManager *cm, ModesManager *mm, ObjectBroker *ob,
		TextClassifier *tc, AuthenticationManager *am, Statistics *s) {

	this->executorFactory = buildExecutorFactory(cm->getMaxThreads());

	stringstream ssmsg;
	ssmsg << "Starting server on port " << (cm->getServerPort());
	Logger::Instance()->trace(INFO, ssmsg.str());

	if (cm->getUseSsl()) {
		iqnet::ssl::ctx = iqnet::ssl::Ctx::server_only("data/cert.pem",	"data/pk.pem");
		this->server = new iqxmlrpc::Https_server(cm->getServerPort(), this->executorFactory);
	} else {
		this->server = new iqxmlrpc::Http_server(cm->getServerPort(), this->executorFactory);
	}

	ApertiumTranslate::objectBroker = ob;
	ApertiumTranslate::modesManager = mm;
	iqxmlrpc::register_method<ApertiumTranslate>(*(this->server), "translate");

	ApertiumLanguagePairs::modesManager = mm;
	iqxmlrpc::register_method<ApertiumLanguagePairs>(*(this->server), "languagePairs");

	ApertiumDetect::textClassifier = tc;
	iqxmlrpc::register_method<ApertiumDetect>(*(this->server), "detect");

	authPlugin = new ApertiumAuthPlugin(am);

	this->logInterceptor = new ApertiumLogInterceptor();

	this->server->push_interceptor(logInterceptor);
	this->server->set_auth_plugin(*authPlugin);

	this->server->log_errors(&std::cerr);
	this->server->enable_introspection();

	this->server->set_verification_level(iqxmlrpc::http::HTTP_CHECK_WEAK);

	this->server->work();
}

ApertiumServer::~ApertiumServer() {
	this->server->set_exit_flag();

	delete this->server;

	delete this->logInterceptor;
	delete this->authPlugin;
	delete this->executorFactory;
}

iqxmlrpc::Executor_factory_base* ApertiumServer::buildExecutorFactory(unsigned int nThreads) {
	iqxmlrpc::Executor_factory_base *ret = NULL;
    if (nThreads > 1) {
        ret = new iqxmlrpc::Pool_executor_factory(nThreads);
    } else {
    	ret = new iqxmlrpc::Serial_executor_factory();
    }
    return(ret);
}
