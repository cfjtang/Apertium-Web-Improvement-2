#import "xmlvm.h"
#import "java_lang_Object.h"

// For circular include:
@class java_io_InputStream;
@class java_lang_Object;
@class java_lang_StringBuilder;
@class java_lang_String;
@class org_apertium_lttoolbox_Compression;
@class java_lang_RuntimeException;
@class java_io_OutputStream;

// Automatically generated by xmlvm2obj. Do not edit!


	
@interface org_apertium_lttoolbox_Compression : java_lang_Object
{

}
+ (void) initialize;
- (id) init;
- (void) __init_org_apertium_lttoolbox_Compression__;
+ (void) multibyte_write___long_java_io_OutputStream :(long)n1 :(java_io_OutputStream*)n2;
+ (int) multibyte_read___java_io_InputStream :(java_io_InputStream*)n1;
+ (void) String_write___java_lang_String_java_io_OutputStream :(java_lang_String*)n1 :(java_io_OutputStream*)n2;
+ (java_lang_String*) String_read___java_io_InputStream :(java_io_InputStream*)n1;

@end

