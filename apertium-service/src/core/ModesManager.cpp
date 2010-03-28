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

#include "ModesManager.h"

#include <fstream>
#include <sstream>

#include <map>
#include <list>
#include <vector>
#include <algorithm>

#include <cstring>

#include <boost/algorithm/string.hpp>

#include <boost/filesystem/fstream.hpp>
#include <boost/filesystem/operations.hpp>
#include <boost/tokenizer.hpp>

#include <boost/regex.hpp>

namespace fs = boost::filesystem;

Program::Program() { }

Program::Program(const std::string p) : program(p) { }

Program::~Program() { }

std::ostream& operator<<(std::ostream &output, Program &p) {
	output << p.getProgramName() << " { ";
	std::vector<std::string> files = p.getFileNames();
	for (std::vector<std::string>::iterator it = files.begin(); it != files.end(); ++it) {
		output << *it << " ";
	}
	output << "}";
    return output;
}

std::string Program::getProgram() {
	return program;
}

void Program::setProgram(const std::string p) {
	program = p;
}

std::string Program::getProgramName() {
	fs::path path(program);
	std::string ret = boost::filesystem::basename(path);
	return ret;
}

std::vector<std::string> Program::getParameters() {
	std::vector<std::string> params;
	const std::string commandLine = this->getProgramName();
	boost::split(params, commandLine, boost::is_any_of("\t "));
	return params;
}

std::vector<std::string> Program::getFileNames() {
	return fileNames;
}

void Program::setFileNames(const std::vector<std::string> f) {
	fileNames = f;
}

Mode::Mode() { }

Mode::Mode(std::string m) : modeName(m) { }

Mode::~Mode() { }

std::string Mode::getModeName() {
	return modeName;
}

void Mode::setModeName(const std::string m) {
	modeName = m;
}

std::vector<Program> Mode::getPrograms() {
	return programs;
}

void Mode::setPrograms(const std::vector<Program> p) {
	programs = p;
}

ModesManager::ModesManager() { }

ModesManager::~ModesManager() { }

void ModesManager::parseXML(const fs::path path) {
	xmlpp::DomParser parser(path.string());

	if (parser) {

		const xmlpp::Node* rootNode = parser.get_document()->get_root_node();

		if (rootNode) {
			xmlpp::NodeSet modeNodeSet = rootNode->find("/modes/mode");

			for(xmlpp::NodeSet::iterator it = modeNodeSet.begin(); it != modeNodeSet.end(); ++it) {
				xmlpp::NodeSet modeNameAttributeSet = (*it)->find("@name");

				std::string modeName;

				for (xmlpp::NodeSet::iterator ita = modeNameAttributeSet.begin(); modeName.size() == 0 && ita != modeNameAttributeSet.end(); ++ita) {
					xmlpp::Attribute *modeNameAttribute = dynamic_cast<xmlpp::Attribute *>(*ita);
					modeName = modeNameAttribute->get_value();
				}

				Mode mode(modeName);

				std::vector<Program> programs;
				xmlpp::NodeSet programNodeSet = (*it)->find("pipeline/program");

				for(xmlpp::NodeSet::iterator ita = programNodeSet.begin(); ita != programNodeSet.end(); ++ita) {
					xmlpp::NodeSet programNameAttributeSet = (*ita)->find("@name");

					std::string programName;

					for (xmlpp::NodeSet::iterator itb = programNameAttributeSet.begin(); programName.size() == 0 && itb != programNameAttributeSet.end(); ++itb) {
						xmlpp::Attribute *programNameAttribute = dynamic_cast<xmlpp::Attribute *>(*itb);
						programName = programNameAttribute->get_value();
					}

					Program program(programName);

					std::vector<std::string> fileNames;

					xmlpp::NodeSet fileNodeSet = (*ita)->find("file");

					for(xmlpp::NodeSet::iterator itb = fileNodeSet.begin(); itb != fileNodeSet.end(); ++itb) {
						xmlpp::NodeSet fileNameAttributeSet = (*itb)->find("@name");

						for (xmlpp::NodeSet::iterator itc = fileNameAttributeSet.begin(); itc != fileNameAttributeSet.end(); ++itc) {
							xmlpp::Attribute *fileNameAttribute = dynamic_cast<xmlpp::Attribute *>(*itc);
							std::string fileName = fileNameAttribute->get_value();

							//if (!fs::exists(fileName)){
							//	cout << "WARNING: " << fileName << " used by " << modeName << " but it doesn't exist." << endl;
							//}

							fileNames.push_back(fileName);
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

/**
 * Parse the modes.xml files present under an arbitrary path.
 */
void ModesManager::initXML(const fs::path path) {
	boost::regex e("modes\\.xml$");
	std::list<fs::path> modeFiles = findFilesByRegex(path, e);
	for (std::list<fs::path>::iterator it = modeFiles.begin(); it != modeFiles.end(); ++it) {
		fs::path curPath = (*it);
		parseXML(curPath);
	}
}

/**
 * Parse the mode files having the .mode suffix present under an arbitrary path.
 */
void ModesManager::initPipe(const fs::path path) {
	std::string suffix = ".mode";

	//boost::regex e(".*\\.mode$")

	std::list<fs::path> modeFiles = findFilesBySuffix(path, suffix);

	for (std::list<fs::path>::iterator it = modeFiles.begin(); it != modeFiles.end(); ++it) {
		fs::path curPath = (*it);
		std::string fileName = curPath.filename();

		std::string modeName = fileName.substr(0, fileName.length()	- suffix.length());

		fs::ifstream inFile;
		inFile.open(curPath);

		std::string line, fileContent = "";

		while (getline(inFile, line)) {
			fileContent += line + " ";
		}

		std::vector<std::string> commands;

		typedef boost::tokenizer<boost::char_separator<char> > tokenizer;
		boost::char_separator<char> sepp("|");

		tokenizer tokensp(fileContent, sepp);

		for (tokenizer::iterator tit = tokensp.begin(); tit != tokensp.end(); ++tit) {
			commands.push_back(*tit);
		}

		boost::char_separator<char> seps(" \t\n\r");

		Mode a(modeName);
		std::vector<Program> programs;

		for (std::vector<std::string>::iterator vit = commands.begin(); vit
				!= commands.end(); vit++) {
			std::vector<std::string> files;

			tokenizer tokenss(*vit, seps);
			tokenizer::iterator tit2 = tokenss.begin();

			std::string command = *(tit2++);

			while (tit2 != tokenss.end()) {
				std::string param = *tit2;
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

/**
 * Return a map containing the modes' name as keys, and modes as values
 */
ModesManager::ModeMapType ModesManager::getModes() {
	ModesManager::ModeMapType ret = modes;
	return ret;
}

std::list<fs::path> ModesManager::findFilesBySuffix(const fs::path p, const std::string suffix) {
	std::list<fs::path> ret;
	fs::directory_iterator endItr;
	for (fs::directory_iterator itr(p); itr != endItr; ++itr) {
		if (is_directory(itr->status())) {
			std::list<fs::path> a = findFilesBySuffix(itr->path(), suffix);
			a.sort();
			ret.sort();
			ret.merge(a);
			} else {
				std::string fileName = itr->leaf();
				if (fileName.length() > suffix.length()) {
					if (fileName.substr(fileName.length() - suffix.length(), suffix.length()) == suffix) {
						ret.push_back(itr->path());
					}
				}
			}
	}
	return(ret);
}

std::list<fs::path> ModesManager::findFilesByRegex(const fs::path p, const boost::regex e) {
	std::list<fs::path> ret;
	fs::directory_iterator endItr;
	for (fs::directory_iterator itr(p); itr != endItr; ++itr) {
		if (is_directory(itr->status())) {
			std::list<fs::path> a = findFilesByRegex(itr->path(), e);
			a.sort();
			ret.sort();
			ret.merge(a);
			} else {
				std::string fileName = itr->leaf();
				if (regex_match(fileName, e))
					ret.push_back(itr->path());
			}
	}
	return(ret);
}
