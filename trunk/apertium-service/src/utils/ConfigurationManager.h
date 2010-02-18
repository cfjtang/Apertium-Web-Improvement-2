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
 * The ConfigurationManager class handles the service's configuration options.
 */

#ifndef CONFIGURATIONMANAGER_H_
#define CONFIGURATIONMANAGER_H_

#include "config.h"

#include <iostream>

#include <libxml++/libxml++.h>
#include <boost/unordered/unordered_map.hpp>
#include <boost/filesystem.hpp>

namespace fs = boost::filesystem;

/**
 * The ConfigurationManager class handles the service's configuration options.
 */
class ConfigurationManager {
public:
	ConfigurationManager(fs::path, fs::path);
	virtual ~ConfigurationManager();

	fs::path getApertiumBase();
	void setApertiumBase(fs::path);

	unsigned int getServerPort();
	void setServerPort(unsigned int);

	unsigned int getKeepaliveTimeout();
	void setKeepaliveTimeout(unsigned int);

	unsigned int getKeepaliveMaxConn();
	void setKeepaliveMaxConn(unsigned int);

	unsigned int getTimeout();
	void setTimeout(unsigned int);

	typedef boost::unordered_map<std::pair<std::string, std::string>, unsigned int> EagerlyLoadsType;

	EagerlyLoadsType getEagerlyLoads();

#if defined(HAVE_LIBTEXTCAT)
	fs::path getConfTextClassifier();
	void setConfTextClassifier(fs::path);
#endif

#if defined(HAVE_COMBINE)
	typedef boost::unordered_map<std::pair<std::string, std::string>, std::string> MonolingualDictionariesType;
	typedef boost::unordered_map<std::string, std::string> LanguageModelsType;

	MonolingualDictionariesType getMonolingualDictionaries();
	LanguageModelsType getLanguageModels();
#endif

	unsigned int getHighWaterMark();
	void setHighWaterMark(unsigned int);

private:
    static const fs::path APERTIUMBASE_DEF;

    static const unsigned int SERVERPORT_DEF;
    static const unsigned int KEEPALIVETIMEOUT_DEF;
    static const unsigned int KEEPALIVEMAXCONN_DEF;
    static const unsigned int TIMEOUT_DEF;

#if defined(HAVE_LIBTEXTCAT)
    static const fs::path CONFTEXTCLASSIFIER_DEF;
#endif

#if defined(HAVE_COMBINE)
    MonolingualDictionariesType monolingualDictionaries;
    LanguageModelsType languageModels;
#endif

    static const unsigned int HIGHWATERMARK_DEF;

	fs::path apertiumBase;

	unsigned int serverPort;
	unsigned int keepaliveTimeout;
	unsigned int keepaliveMaxConn;
	unsigned int timeout;

#if defined(HAVE_LIBTEXTCAT)
	fs::path confTextClassifier;
#endif

	unsigned int highWaterMark;

	EagerlyLoadsType eagerlyLoads;
};

#endif /* CONFIGURATIONMANAGER_H_ */
