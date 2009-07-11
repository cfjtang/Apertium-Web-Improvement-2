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

#ifndef CONFIGURATIONMANAGER_H_
#define CONFIGURATIONMANAGER_H_

#include <iostream>

#include <libxml++/libxml++.h>
#include <boost/filesystem.hpp>

namespace fs = boost::filesystem;

class ConfigurationManager {
public:
	ConfigurationManager(fs::path, fs::path);
	virtual ~ConfigurationManager();

	int getServerPort();
	void setServerPort(int);

	fs::path getApertiumBase();
	void setApertiumBase(fs::path);

	int getMaxThreads();
	void setMaxThreads(int);

	bool getUseSsl();
	void setUseSsl(bool);

	fs::path getConfTextClassifier();
	void setConfTextClassifier(fs::path);

	fs::path getConfUsers();
	void setConfUsers(fs::path);

private:
	int serverPort;
	fs::path apertiumBase;
	int maxThreads;
	bool useSsl;
	fs::path confTextClassifier;
	fs::path confUsers;
};

#endif /* CONFIGURATIONMANAGER_H_ */
