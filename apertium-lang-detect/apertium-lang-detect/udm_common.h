/* Copyright (C) 2000-2009 Lavtech.com corp. All rights reserved.

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
*/
/************** Language and charset guesser *************/

#include <apertium-lang-detect/apertium-lang-detect-config.h>

#define UDM_LM_MAXGRAM		3
#define UDM_LM_HASHMASK		0x0FFF

typedef struct {
	int count;
	char str[UDM_LM_MAXGRAM+3];
} UDM_LANGITEM;

typedef struct {
	float  expectation;			/* Average value   */
	float  dispersion;			/* Math dispersion */
	char * lang;				/* Map Language    */
	char * charset;				/* Map charset     */
	UDM_LANGITEM  memb[UDM_LM_HASHMASK+1];	/* Items list      */
} UDM_LANGMAP;

typedef struct {
	size_t nmaps;
	UDM_LANGMAP * maps;
} UDM_LANGMAPLIST;

typedef struct{
	UDM_LANGMAPLIST LangMapList;
	int errcode;
	char errstr[1024];
} UDM_ENV;

#define UDM_FREE(x)		{if(x){free(x);x=NULL;}}
#define UdmGetToken(a,b,c)	strtok(a,b)

/*********************************************************/

