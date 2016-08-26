#import "xmlvm.h"
#import "java_io_Reader.h"

// For circular include:
@class java_lang_IllegalArgumentException;
@class java_lang_IndexOutOfBoundsException;
@class java_lang_Object;
@class java_lang_String;
@class java_io_IOException;
@class java_io_Reader;
@class StringReader2;
@class java_lang_Math;

// Automatically generated by xmlvm2obj. Do not edit!


	
@interface _StringReader2 : java_io_Reader
{
@private java_lang_String* _StringReader2_str;
@private int _StringReader2_length;
@private int _StringReader2_next;
@private int _StringReader2_mark;

}
+ (void) initialize;
- (id) init;
- (java_lang_String*) _GET_str;
- (void) _PUT_str: (java_lang_String*) v;
- (int) _GET_length;
- (void) _PUT_length: (int) v;
- (int) _GET_next;
- (void) _PUT_next: (int) v;
- (int) _GET_mark;
- (void) _PUT_mark: (int) v;
- (void) __init__StringReader2___java_lang_String :(java_lang_String*)n1;
- (void) ensureOpen__;
- (int) read__;
- (int) read___char_ARRAYTYPE_int_int :(XMLVMArray*)n1 :(int)n2 :(int)n3;
- (long) skip___long :(long)n1;
- (int) ready__;
- (int) markSupported__;
- (void) mark___int :(int)n1;
- (void) reset__;
- (void) close__;

@end
