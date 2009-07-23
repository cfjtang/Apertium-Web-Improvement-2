/*
 * Copyright (C) 2009 Apertium project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

#include <iostream>
#include <cwchar>
#include <string>

#include "collector.hh"

using namespace std;

Collector::Collector()
{

}

Collector::~Collector()
{

}

/*
 *	Read in a deformatted file stream, send the non-format blocks to be translated
 *	and copy the format blocks into the stream.
 */

void
Collector::collect(FILE *input, FILE *output)
{
	wstring buf = L"";
	int seen = 0;

	while(wchar_t val = static_cast<wchar_t>(fgetwc(input))) {

		if(feof(input)) {
			return;
		}	
		buf = buf + val;

		if(seen == 0 && val == L'[') {
			seen++;
		}

		if(seen == 1 && val == L']') {
			seen++;
		}

                if(seen == 2) {
                        fputws_unlocked(L".[]", output); // This is a hack because I can't work out how to get .[] output.
                        unsigned int len = buf.length() - 1;
                        if(buf[0] == L'[' && buf[len] == ']') {
                                fputws_unlocked(buf.c_str(), output);
                        } else {
                                wstring *translation = translate(&buf);
                                fputws_unlocked(translation->c_str(), output);
                        }
                        seen = 0;
                        buf = L"";
                }
	}
	
	return;
}
