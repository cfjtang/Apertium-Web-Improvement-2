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
 * The class Translate is used to execute the sequence of step required by
 * a translation task by using the informations contained in Modes files.
 */

#include <assert.h>

#include "Process.h"

Process::Process(std::string cmd, std::vector<std::string> params) : p(NULL) {
	boost::process::context ctx;

	ctx.stdin_behavior = boost::process::capture_stream();
	ctx.stdout_behavior = boost::process::capture_stream();
	ctx.stderr_behavior = boost::process::close_stream();

	p = new boost::process::child(boost::process::launch(cmd, params, ctx));
}

Process::~Process() {
	if (p) {
		p->get_stdin().close();
		delete p;
	}
}

std::ostream& Process::in() {
	assert(p != NULL);
	return p->get_stdin();
}

std::istream& Process::out() {
	assert(p != NULL);
	return p->get_stdout();
}
