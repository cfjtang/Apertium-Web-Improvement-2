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

#ifndef APERTIUMTRANSLATE_H_
#define APERTIUMTRANSLATE_H_

#include <iostream>
#include <wchar.h>

#include <libiqxmlrpc/libiqxmlrpc.h>

#include "core/ObjectBroker.h"
#include "core/ModesManager.h"
#include "core/TextClassifier.h"

class ApertiumTranslate: public iqxmlrpc::Method {
public:
	void execute(const iqxmlrpc::Param_list &params, iqxmlrpc::Value &retval);
	static ObjectBroker *objectBroker;
	static ModesManager *modesManager;
	static TextClassifier *textClassifier;
};

#endif /* APERTIUMTRANSLATE_H_ */
