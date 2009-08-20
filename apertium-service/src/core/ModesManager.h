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
 * The class ModesManager is used to parse Apertium Modes files. Modes files
 * (typically called modes.xml) are XML files which specify which programs
 * should be run and in what order. Normally each linguistic package has one
 * of these files which specifies various ways in which you can use the data
 * to perform translations.
 */

#ifndef MODESMANAGER_H_
#define MODESMANAGER_H_

#include <errno.h>
#include <dirent.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include <iostream>
#include <vector>
#include <list>
#include <map>

#include <boost/thread.hpp>
#include <boost/unordered/unordered_map.hpp>
#include <boost/filesystem.hpp>
#include <boost/regex.hpp>

#include <libxml++/libxml++.h>

using namespace std;
namespace fs = boost::filesystem;

/**
 * The class Program is used to handle informations regarding a specific program present in the Apertium pipeline.
 */
class Program {
	friend ostream& operator<<(ostream& output, Program& p);
public:
	Program();
	Program(const string);
	virtual ~Program();

	string getProgramName();
	void setProgramName(const string);

	vector<string> getFileNames();
	void setFileNames(const vector<string>);

	bool operator==(Program const& other) const {
        return programName == other.programName && fileNames == other.fileNames;
    }

	friend std::size_t hash_value(Program const& p) {
		std::size_t seed = 0;
		boost::hash_combine(seed, p.programName);
		boost::hash_combine(seed, p.fileNames);
		return seed;
	}

private:
	string programName;
	vector<string> fileNames;
};

/**
 * The class Mode is used to handle informations regarding a specific Apertium Mode.
 */
class Mode {
public:
	Mode();
	Mode(const string);
	virtual ~Mode();

	string getModeName();
	void setModeName(const string);

	vector<Program> getPrograms();
	void setPrograms(const vector<Program>);

private:
	string modeName;
	vector<Program> programs;
};

/**
 * The class ModesManager is used to parse Apertium Modes files. Modes files
 * (typically called modes.xml) are XML files which specify which programs
 * should be run and in what order. Normally each linguistic package has one
 * of these files which specifies various ways in which you can use the data
 * to perform translations.
 */
class ModesManager {
public:
	ModesManager();
	virtual ~ModesManager();

	typedef boost::unordered_map<string, Mode> ModeMapType;

	ModeMapType getModes();

	void initPipe(const fs::path);
	void initXML(const fs::path);

private:
	void parseXML(fs::path);

	list<fs::path> findFilesBySuffix(const fs::path, const string);
	list<fs::path> findFilesByRegex(const fs::path, const boost::regex);

	ModeMapType modes;
};

#endif /* MODESMANAGER_H_ */
