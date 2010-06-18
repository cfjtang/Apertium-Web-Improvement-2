/*
 * Copyright (C) 2009--2010 Francis M. Tyers
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
#include <string.h>

#include <lttoolbox/lt_locale.h>
#include <lttoolbox/xml_parse_util.h>

#include "bidix_parser.h"

using namespace std;

typedef struct BidixSAXData { 
 
        string direction; 
        string in_tag; 
        string cur_side; 
        bool in_section; 
        wstring left_string; 
        wstring right_string; 
        int cur_slr; 
        int cur_srl; 
 
} BidixSAXData; 
	
void startElement(void *context, const xmlChar *fullname, const xmlChar **atts);
void endElement(void *context, const xmlChar *fullname);
void characters(void *context, const xmlChar *chars, int len);

void
startElement(void *user_data, const xmlChar *localname, const xmlChar **attributes)
{

	BidixSAXData *d = (BidixSAXData *)user_data;

	if(XMLParseUtil::towstring(localname) == L"section") {
		d->in_section = true;
	}

	if(XMLParseUtil::towstring(localname) == L"e") {
		d->in_tag = "e";

		unsigned int var = 0;
		unsigned int len = 0;

		if(attributes) {
			const xmlChar *p = attributes[len];
			while(p[len]) {
				len++; 
			}

			for(unsigned int i = 0; i < len; i++) {
				cout << len << ": " << endl;
				if(d->direction == "lr" && XMLParseUtil::towstring(attributes[i]) == L"slr") {
					var = atoi((char*)attributes[i+1]);
				} else if(d->direction == "rl" && XMLParseUtil::towstring(attributes[i]) == L"srl") {
					var = atoi((char*)attributes[i+1]);
				}	
			}
	
			if(d->direction == "lr") {
				d->cur_slr = var;
			} else if(d->direction == "rl") {
				d->cur_srl = var;
			}

			if(d->direction == "lr") {
				cout << "+ (" << d->in_tag << ":" << d->cur_slr << ") (" << d->in_section << ") (" <<  d->cur_side << ") startElement: " << localname << endl;
			} else if(d->direction == "rl") {
				cout << "+ (" << d->in_tag << ":" << d->cur_srl << ") (" << d->in_section << ") (" <<  d->cur_side << ") startElement: " << localname << endl;
			}
		}	

	} else if(XMLParseUtil::towstring(localname) == L"p") {
		d->in_tag = "p";
	
	} else if(XMLParseUtil::towstring(localname) == L"l") {
		d->in_tag = "l";
		d->cur_side = "l";

	} else if(XMLParseUtil::towstring(localname) == L"r") {
		d->in_tag = "r";
		d->cur_side = "r";

	} else if(XMLParseUtil::towstring(localname) == L"i") {
		d->in_tag = "i";

	} else if(XMLParseUtil::towstring(localname) == L"b") {
		d->in_tag = "b";

		if(d->cur_side == "l") {
			d->left_string += L" ";
		} else if(d->cur_side == "r") {
			d->right_string += L" ";
		}

	} else if(XMLParseUtil::towstring(localname) == L"s") {
		d->in_tag = "s";

		wstring tag = XMLParseUtil::towstring(attributes[1]);
		if(d->cur_side == "l") {
			d->left_string = d->left_string + L"<" + tag + L">";
		} else if(d->cur_side == "r") {
			d->right_string = d->right_string + L"<" + tag + L">" ;
		}

	} else if(XMLParseUtil::towstring(localname) == L"g") {
		d->in_tag = "g";

		if(d->cur_side == "l") {
			d->left_string += L"#";
		} else if(d->cur_side == "r") {
			d->right_string += L"#";
		}

	} else {
		d->in_tag = "";
	}	
}

void
endElement(void *user_data, const xmlChar *localname)
{
	BidixSAXData *d = (BidixSAXData *)user_data;

	if(xmlStrEqual(localname, xmlCharStrdup("section")) == 1) {
		d->in_section = false;
	}

	if(XMLParseUtil::towstring(localname) == L"e" && d->in_section) {
		wcout << L"e: " << d->left_string << L" " << d->right_string << endl;

		d->left_string = L"";
		d->right_string = L"";
		d->cur_side = "";
	}
}

void
characters(void *user_data, const xmlChar *chars, int len)
{
	BidixSAXData *d = (BidixSAXData *)user_data;

	if(d->in_section == false) {
		return;
	}

	if(d->cur_side == "l") {
		d->left_string += XMLParseUtil::towstring(chars).substr(0, len);
	} else if(d->cur_side == "r") {
		d->right_string += XMLParseUtil::towstring(chars).substr(0, len);
	}
}

BidixParser::BidixParser()
{
	LtLocale::tryToSetLocale();

	memset(&handler, 0, sizeof(handler));
	handler.initialized = XML_SAX2_MAGIC;
	handler.startElement = &startElement;
	handler.endElement = &endElement;
	handler.characters = &characters;
}

void
BidixParser::parse(const string fichero, const string dir)
{
/**
	sorti<n><m> (0, u'sorti:0<n><m>', u'darkness<n>')
	sorti<n><m> (1, u'sorti:1<n><m>', u'thick fog<n>')
	sorti<n><m> (2, u'sorti:2<n><m>', u'heavy snowstorm<n>')
*/
	BidixSAXData user_data;

	user_data.in_section = false;
	user_data.cur_slr = 0;
	user_data.left_string = L"";
	user_data.right_string = L"";
	user_data.cur_side = "";
	user_data.in_tag = "";
	user_data.cur_srl = 0;
	user_data.direction = dir;

	int res = xmlSAXUserParseFile(&handler, &user_data, fichero.c_str());

	if(res != 0) {
		cerr << "Error: Cannot open '" << fichero << "'." << endl;
		exit(EXIT_FAILURE);
	}
}

BidixParser::~BidixParser()
{
	return;	
}
