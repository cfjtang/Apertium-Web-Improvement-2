#import "xmlvm.h"
#import "java_lang_Object.h"

// For circular include:
@class java_io_InputStream;
@class java_util_Map;
@class org_apertium_lttoolbox_Pair;
@class javax_xml_stream_XMLStreamException;
@class org_apertium_lttoolbox_compile_XMLPrint;
@class java_lang_StringBuilder;
@class java_io_PrintStream;
@class java_lang_System;
@class java_util_HashMap;
@class org_apertium_lttoolbox_Expander;
@class javax_xml_stream_XMLStreamReader;
@class org_apertium_lttoolbox_Expander_SPair;
@class java_lang_Character;
@class javax_xml_stream_Location;
@class javax_xml_stream_XMLInputFactory;
@class java_util_Collection;
@class java_io_FileInputStream;
@class java_lang_Object;
@class java_io_Writer;
@class java_lang_String;
@class org_apertium_lttoolbox_Expander_EntList;
@class org_apertium_lttoolbox_compile_Compile;
@class java_util_Iterator;
@class java_lang_RuntimeException;
@class java_io_FileNotFoundException;

// Automatically generated by xmlvm2obj. Do not edit!


	
@interface org_apertium_lttoolbox_Expander : java_lang_Object
{
@private java_lang_String* org_apertium_lttoolbox_Expander_current_paradigm;
@private java_util_Map* org_apertium_lttoolbox_Expander_paradigm;
@private java_util_Map* org_apertium_lttoolbox_Expander_paradigm_lr;
@private java_util_Map* org_apertium_lttoolbox_Expander_paradigm_rl;
@private java_io_Writer* org_apertium_lttoolbox_Expander_output;
@private javax_xml_stream_XMLStreamReader* org_apertium_lttoolbox_Expander_reader;

}
+ (void) initialize;
- (id) init;
- (java_lang_String*) _GET_current_paradigm;
- (void) _PUT_current_paradigm: (java_lang_String*) v;
- (java_util_Map*) _GET_paradigm;
- (void) _PUT_paradigm: (java_util_Map*) v;
- (java_util_Map*) _GET_paradigm_lr;
- (void) _PUT_paradigm_lr: (java_util_Map*) v;
- (java_util_Map*) _GET_paradigm_rl;
- (void) _PUT_paradigm_rl: (java_util_Map*) v;
- (java_io_Writer*) _GET_output;
- (void) _PUT_output: (java_io_Writer*) v;
- (javax_xml_stream_XMLStreamReader*) _GET_reader;
- (void) _PUT_reader: (javax_xml_stream_XMLStreamReader*) v;
- (void) __init_org_apertium_lttoolbox_Expander__;
- (void) expand___java_lang_String_java_io_Writer :(java_lang_String*)n1 :(java_io_Writer*)n2;
- (int) allBlanks__;
- (org_apertium_lttoolbox_Expander_EntList*) append___org_apertium_lttoolbox_Expander_EntList_org_apertium_lttoolbox_Expander_EntList :(org_apertium_lttoolbox_Expander_EntList*)n1 :(org_apertium_lttoolbox_Expander_EntList*)n2;
- (org_apertium_lttoolbox_Expander_EntList*) append___org_apertium_lttoolbox_Expander_EntList_java_lang_String :(org_apertium_lttoolbox_Expander_EntList*)n1 :(java_lang_String*)n2;
- (org_apertium_lttoolbox_Expander_EntList*) append___org_apertium_lttoolbox_Expander_EntList_org_apertium_lttoolbox_Pair :(org_apertium_lttoolbox_Expander_EntList*)n1 :(org_apertium_lttoolbox_Pair*)n2;
- (java_lang_String*) attrib___java_lang_String :(java_lang_String*)n1;
- (int) isEmpty___javax_xml_stream_XMLStreamReader :(javax_xml_stream_XMLStreamReader*)n1;
- (void) procEntry__;
- (java_lang_String*) procIdentity__;
- (void) procNode__;
- (java_lang_String*) procPar__;
- (void) procParDef__;
- (java_lang_String*) procRegexp__;
- (org_apertium_lttoolbox_Expander_SPair*) procTransduction__;
- (void) readString___java_lang_StringBuilder_java_lang_String :(java_lang_StringBuilder*)n1 :(java_lang_String*)n2;
- (void) requireEmptyError___java_lang_String :(java_lang_String*)n1;
- (java_lang_String*) skip___java_lang_String_java_lang_String :(java_lang_String*)n1 :(java_lang_String*)n2;
- (java_lang_String*) skipBlanks___java_lang_String :(java_lang_String*)n1;

@end

