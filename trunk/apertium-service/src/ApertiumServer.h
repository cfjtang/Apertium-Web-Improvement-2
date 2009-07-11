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

#ifndef APERTIUMLSERVER_H_
#define APERTIUMLSERVER_H_

#include <libiqxmlrpc/libiqxmlrpc.h>
#include <libiqxmlrpc/method.h>
#include <libiqxmlrpc/server.h>
#include <libiqxmlrpc/dispatcher_manager.h>

#include <libiqxmlrpc/http.h>
#include <libiqxmlrpc/http_server.h>
#include <libiqxmlrpc/http_errors.h>

#include <libiqxmlrpc/https_server.h>

#include "utils/ConfigurationManager.h"
#include "utils/AuthenticationManager.h"

#include "ApertiumLogInterceptor.h"
#include "ApertiumAuthPlugin.h"

#include "core/ObjectBroker.h"
#include "core/ModesManager.h"
#include "core/TextClassifier.h"

class ApertiumServer {
public:
	ApertiumServer(ConfigurationManager*, ModesManager*, ObjectBroker*, TextClassifier*, AuthenticationManager*);
	virtual ~ApertiumServer();

	//static void test(iqxmlrpc::Method *method, const iqxmlrpc::Param_list& params, iqxmlrpc::Value& retval) {
	//	if (params.empty()) {
	//		retval = 0;
	//	} else {
	//		retval = params[0];
	//	}
	//};

private:
	iqxmlrpc::Executor_factory_base* buildExecutorFactory(unsigned int);
	iqxmlrpc::Executor_factory_base *executorFactory;

	iqxmlrpc::Server *server;

	ApertiumLogInterceptor *logInterceptor;
	ApertiumAuthPlugin *authPlugin;
};

#endif /* APERTIUMSERVER_H_ */
