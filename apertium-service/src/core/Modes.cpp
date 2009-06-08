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

#include "Modes.h"

#include <fstream>
#include <sstream>

#include <map>
#include <list>
#include <vector>
#include <algorithm>

#include <cstring>

#include <boost/filesystem/fstream.hpp>
#include <boost/filesystem/operations.hpp>
#include <boost/tokenizer.hpp>

#include <boost/regex.hpp>

using namespace std;
namespace fs = boost::filesystem;

Program::Program() {

}

Program::Program(const string p) {
	programName = p;
}

Program::~Program() {

}

ostream& operator<<(ostream &output, Program &p) {
	output << p.getProgramName() << " { ";
	vector<string> files = p.getFileNames();
	for (vector<string>::iterator it = files.begin(); it != files.end(); ++it) {
		output << *it << " ";
	}
	output << "}";
    return output;
}

string Program::getProgramName() {
	return programName;
}

void Program::setProgramName(const string p) {
	programName = p;
}

vector<string> Program::getFileNames() {
	return fileNames;
}

void Program::setFileNames(const vector<string> f) {
	fileNames = f;
}

Mode::Mode() {

}

Mode::Mode(string m) {
	modeName = m;
}

Mode::~Mode() {

}

string Mode::getModeName() {
	return modeName;
}

void Mode::setModeName(const string m) {
	modeName = m;
}

vector<Program> Mode::getPrograms() {
	return programs;
}

void Mode::setPrograms(const vector<Program> p) {
	programs = p;
}

Modes *Modes::instance = NULL;

Modes *Modes::Instance() {
	if (!instance)
		instance = new Modes();
	return(instance);
}

Modes::Modes() { }

Modes::~Modes() {
	instance = NULL;
}

void Modes::parseXML(const fs::path path) {
	xmlpp::DomParser parser(path.string());

	if (parser) {

		const xmlpp::Node* rootNode = parser.get_document()->get_root_node();

		if (rootNode) {
			xmlpp::NodeSet modeNodeSet = rootNode->find("/modes/mode");

			for(xmlpp::NodeSet::iterator it = modeNodeSet.begin(); it != modeNodeSet.end(); ++it) {
				xmlpp::NodeSet modeNameAttributeSet = (*it)->find("@name");

				string modeName;

				for (xmlpp::NodeSet::iterator ita = modeNameAttributeSet.begin(); modeName.size() == 0 && ita != modeNameAttributeSet.end(); ++ita) {
					xmlpp::Attribute *modeNameAttribute = dynamic_cast<xmlpp::Attribute *>(*ita);
					modeName = modeNameAttribute->get_value();
				}

				Mode mode(modeName);

				vector<Program> programs;
				xmlpp::NodeSet programNodeSet = (*it)->find("pipeline/program");

				for(xmlpp::NodeSet::iterator ita = programNodeSet.begin(); ita != programNodeSet.end(); ++ita) {
					xmlpp::NodeSet programNameAttributeSet = (*ita)->find("@name");

					string programName;

					for (xmlpp::NodeSet::iterator itb = programNameAttributeSet.begin(); programName.size() == 0 && itb != programNameAttributeSet.end(); ++itb) {
						xmlpp::Attribute *programNameAttribute = dynamic_cast<xmlpp::Attribute *>(*itb);
						programName = programNameAttribute->get_value();
					}

					Program program(programName);

					vector<string> fileNames;

					xmlpp::NodeSet fileNodeSet = (*ita)->find("file");

					for(xmlpp::NodeSet::iterator itb = fileNodeSet.begin(); itb != fileNodeSet.end(); ++itb) {
						xmlpp::NodeSet fileNameAttributeSet = (*itb)->find("@name");

						for (xmlpp::NodeSet::iterator itc = fileNameAttributeSet.begin(); itc != fileNameAttributeSet.end(); ++itc) {
							xmlpp::Attribute *fileNameAttribute = dynamic_cast<xmlpp::Attribute *>(*itc);
							fileNames.push_back(fileNameAttribute->get_value());
						}
					}

					program.setFileNames(fileNames);
					programs.push_back(program);
				}

				mode.setPrograms(programs);
				modes[modeName] = mode;
			}
		}
	}
}

void Modes::initXML(const fs::path path) {
	boost::regex e("modes\\.xml$");
	list<fs::path> modeFiles = findFilesByRegex(path, e);
	for (list<fs::path>::iterator it = modeFiles.begin(); it != modeFiles.end(); ++it) {
		fs::path curPath = (*it);
		parseXML(curPath);
	}
}

void Modes::initPipe(const fs::path path) {
	string suffix = ".mode";

	//boost::regex e(".*\\.mode$")

	list<fs::path> modeFiles = findFilesBySuffix(path, suffix);

	for (list<fs::path>::iterator it = modeFiles.begin(); it != modeFiles.end(); ++it) {
		fs::path curPath = (*it);
		string fileName = curPath.filename();

		string modeName = fileName.substr(0, fileName.length()	- suffix.length());

		fs::ifstream inFile;
		inFile.open(curPath);

		string line, fileContent = "";

		while (getline(inFile, line)) {
			fileContent += line + " ";
		}

		vector<string> commands;

		typedef boost::tokenizer<boost::char_separator<char> > tokenizer;
		boost::char_separator<char> sepp("|");

		tokenizer tokensp(fileContent, sepp);

		for (tokenizer::iterator tit = tokensp.begin(); tit != tokensp.end(); ++tit) {
			commands.push_back(*tit);
		}

		boost::char_separator<char> seps(" \t\n\r");

		Mode a(modeName);
		vector<Program> programs;

		for (vector<string>::iterator vit = commands.begin(); vit
				!= commands.end(); vit++) {
			vector<string> files;

			tokenizer tokenss(*vit, seps);
			tokenizer::iterator tit2 = tokenss.begin();

			string command = *(tit2++);

			fs::path path(command);
			command = boost::filesystem::basename(path);

			while (tit2 != tokenss.end()) {
				string param = *tit2;
				if (param[0] == '-' || param[0] == '$') {
					command += (" " + param);
				} else {
					files.push_back(param);
				}
				++tit2;
			}

			Program b(command);
			b.setFileNames(files);

			programs.push_back(b);
		}

		a.setPrograms(programs);
		modes[modeName] = a;

		inFile.close();
	}
}

std::list<std::string> Modes::getModeNames() {
	std::list<std::string> ret;	string suffix = ".mode";
	ModeMapType::iterator it = modes.begin();
	while (it != modes.end()) {
		Mode mode = (*(it++)).second;
		ret.push_back(mode.getModeName());
	}
	return(ret);
}

std::list<fs::path> Modes::findFilesBySuffix(const fs::path p, const std::string suffix) {
	std::list<fs::path> ret;
	fs::directory_iterator endItr;
	for (fs::directory_iterator itr(p); itr != endItr; ++itr) {
		if (is_directory(itr->status())) {
			std::list<fs::path> a = findFilesBySuffix(itr->path(), suffix);
			a.sort();
			ret.sort();
			ret.merge(a);
			} else {
				string fileName = itr->leaf();
				if (fileName.length() > suffix.length()) {
					if (fileName.substr(fileName.length() - suffix.length(), suffix.length()) == suffix) {
						ret.push_back(itr->path());
					}
				}
			}
	}
	return(ret);
}

std::list<fs::path> Modes::findFilesByRegex(const fs::path p, const boost::regex e) {
	std::list<fs::path> ret;
	fs::directory_iterator endItr;
	for (fs::directory_iterator itr(p); itr != endItr; ++itr) {
		if (is_directory(itr->status())) {
			std::list<fs::path> a = findFilesByRegex(itr->path(), e);
			a.sort();
			ret.sort();
			ret.merge(a);
			} else {
				string fileName = itr->leaf();
				if (regex_match(fileName, e))
					ret.push_back(itr->path());
			}
	}
	return(ret);
}
