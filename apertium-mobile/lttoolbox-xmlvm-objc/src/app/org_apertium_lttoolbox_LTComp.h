#import "xmlvm.h"
#import "java_lang_Object.h"

// For circular include:
@class java_io_BufferedOutputStream;
@class java_io_FileOutputStream;
@class java_lang_Object;
@class org_apertium_lttoolbox_LTComp;
@class java_lang_String;
@class java_lang_StringBuilder;
@class java_io_PrintStream;
@class java_lang_System;
@class org_apertium_lttoolbox_compile_Compile;
@class java_io_OutputStream;

// Automatically generated by xmlvm2obj. Do not edit!


	
@interface org_apertium_lttoolbox_LTComp : java_lang_Object
{

}
+ (void) initialize;
- (id) init;
+ (java_lang_String*) _GET_PACKAGE_VERSION;
+ (void) _PUT_PACKAGE_VERSION: (java_lang_String*) v;
- (void) __init_org_apertium_lttoolbox_LTComp__;
+ (void) endProgram___java_lang_String :(java_lang_String*)n1;
+ (void) main___java_lang_String_ARRAYTYPE :(XMLVMArray*)n1;

@end

