/*
 * Copyright (c) 2004-2009 XMLVM --- An XML-based Programming Language
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 675 Mass
 * Ave, Cambridge, MA 02139, USA.
 * 
 * For more information, visit the XMLVM Home Page at http://www.xmlvm.org
 */

#import "org_xmlvm_iphone_CGSize.h"

// CGSize
//----------------------------------------------------------------------------
@implementation org_xmlvm_iphone_CGSize;

- (void) __init_org_xmlvm_iphone_CGSize___float_float: (float) w : (float) h {
	width = w;
	height = h;
}

- (void) _PUT_width: (float) w
{
	self->width = w;
}

- (float) _GET_width
{
	return width;
}

- (void) _PUT_height: (float) h
{
	self->height = h;
}

- (float) _GET_height
{
	return height;
}

- (int) equals___java_lang_Object: (java_lang_Object*) o
{
	if (o == [NSNull null] || ![o isKindOfClass: [org_xmlvm_iphone_CGSize class]]) {
		return 0;
	}
		
	org_xmlvm_iphone_CGSize* s = (org_xmlvm_iphone_CGSize*) o;
	return self->width == s->width && self->height == s->height;
}

@end
