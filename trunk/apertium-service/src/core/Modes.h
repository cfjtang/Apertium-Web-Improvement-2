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

#ifndef MODES_H_
#define MODES_H_

#include <errno.h>
#include <dirent.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include <iostream>
#include <vector>
#include <list>
#include <map>

#include <boost/unordered/unordered_map.hpp>
#include <boost/filesystem.hpp>

#include <boost/regex.hpp>

#include <libxml++/libxml++.h>

using namespace std;
namespace fs = boost::filesystem;

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

private:
	string programName;
	vector<string> fileNames;
};

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

class Modes {
public:
	static Modes *Instance();
	virtual ~Modes();

	list<string> getModeNames();

	Mode *getMode(const string mode) {
		Mode *ret = NULL;
		ModeMapType::iterator it = modes.find(mode);
		if (it != modes.end())
			ret = &(it->second);
		return(ret);
	}

	void initPipe(const fs::path);
	void initXML(const fs::path);

private:
	Modes();

	void parseXML(fs::path);

	static Modes *instance;

	typedef boost::unordered_map<string, Mode> ModeMapType;
	//typedef map<string, Mode> ModeMapType;

	list<fs::path> findFilesBySuffix(const fs::path, const string);
	list<fs::path> findFilesByRegex(const fs::path, const boost::regex);

	ModeMapType modes;
};

#endif /* MODES_H_ */
