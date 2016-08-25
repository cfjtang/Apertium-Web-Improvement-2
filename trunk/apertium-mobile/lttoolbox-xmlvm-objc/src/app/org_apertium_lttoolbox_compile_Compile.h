#import "xmlvm.h"
#import "java_lang_Object.h"

// For circular include:
@class java_io_InputStream;
@class java_util_Map;
@class java_lang_Boolean;
@class javax_xml_stream_XMLStreamException;
@class org_apertium_lttoolbox_compile_EntryToken;
@class org_apertium_lttoolbox_compile_XMLPrint;
@class java_lang_StringBuilder;
@class org_apertium_lttoolbox_compile_Transducer;
@class java_io_PrintStream;
@class java_lang_System;
@class java_util_HashMap;
@class javax_xml_stream_XMLStreamReader;
@class java_lang_Throwable;
@class java_lang_Character;
@class java_io_BufferedInputStream;
@class java_util_Set;
@class javax_xml_stream_Location;
@class javax_xml_stream_XMLInputFactory;
@class java_util_Collection;
@class java_io_FileInputStream;
@class java_util_ArrayList;
@class java_lang_Object;
@class java_util_List;
@class java_lang_Integer;
@class java_util_TreeMap;
@class java_lang_String;
@class org_apertium_lttoolbox_Compression;
@class java_lang_IllegalStateException;
@class org_apertium_lttoolbox_Alphabet;
@class org_apertium_lttoolbox_compile_Compile;
@class org_apertium_lttoolbox_compile_RegexpCompiler;
@class java_util_Iterator;
@class java_util_HashSet;
@class java_lang_RuntimeException;
@class java_io_OutputStream;
@class java_io_FileNotFoundException;

// Automatically generated by xmlvm2obj. Do not edit!


	
@interface org_apertium_lttoolbox_compile_Compile : java_lang_Object
{
@private java_lang_String* org_apertium_lttoolbox_compile_Compile_current_paradigm;
@private java_lang_String* org_apertium_lttoolbox_compile_Compile_current_section;
@private java_lang_String* org_apertium_lttoolbox_compile_Compile_direction;
@private java_lang_String* org_apertium_lttoolbox_compile_Compile_letters;
@private org_apertium_lttoolbox_Alphabet* org_apertium_lttoolbox_compile_Compile_alphabet;
@private java_util_Map* org_apertium_lttoolbox_compile_Compile_paradigms;
@private java_util_Map* org_apertium_lttoolbox_compile_Compile_sections;
@private java_util_HashMap* org_apertium_lttoolbox_compile_Compile_prefix_paradigms;
@private java_util_HashMap* org_apertium_lttoolbox_compile_Compile_suffix_paradigms;
@private java_util_HashMap* org_apertium_lttoolbox_compile_Compile_postsuffix_paradigms;
@private java_util_HashMap* org_apertium_lttoolbox_compile_Compile_acx_map;
@private int org_apertium_lttoolbox_compile_Compile_acx_current_char;
@private java_lang_String* org_apertium_lttoolbox_compile_Compile_version;
@private java_lang_String* org_apertium_lttoolbox_compile_Compile_encoding;
@private java_lang_Boolean* org_apertium_lttoolbox_compile_Compile_standalone;
@private javax_xml_stream_XMLStreamReader* org_apertium_lttoolbox_compile_Compile_reader;

}
+ (void) initialize;
- (id) init;
+ (java_lang_String*) _GET_COMPILER_DICTIONARY_ELEM;
+ (void) _PUT_COMPILER_DICTIONARY_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_ALPHABET_ELEM;
+ (void) _PUT_COMPILER_ALPHABET_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_SDEFS_ELEM;
+ (void) _PUT_COMPILER_SDEFS_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_SDEF_ELEM;
+ (void) _PUT_COMPILER_SDEF_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_N_ATTR;
+ (void) _PUT_COMPILER_N_ATTR: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_PARDEFS_ELEM;
+ (void) _PUT_COMPILER_PARDEFS_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_PARDEF_ELEM;
+ (void) _PUT_COMPILER_PARDEF_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_PAR_ELEM;
+ (void) _PUT_COMPILER_PAR_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_ENTRY_ELEM;
+ (void) _PUT_COMPILER_ENTRY_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_RESTRICTION_ATTR;
+ (void) _PUT_COMPILER_RESTRICTION_ATTR: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_RESTRICTION_LR_VAL;
+ (void) _PUT_COMPILER_RESTRICTION_LR_VAL: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_RESTRICTION_RL_VAL;
+ (void) _PUT_COMPILER_RESTRICTION_RL_VAL: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_PAIR_ELEM;
+ (void) _PUT_COMPILER_PAIR_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_LEFT_ELEM;
+ (void) _PUT_COMPILER_LEFT_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_RIGHT_ELEM;
+ (void) _PUT_COMPILER_RIGHT_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_S_ELEM;
+ (void) _PUT_COMPILER_S_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_REGEXP_ELEM;
+ (void) _PUT_COMPILER_REGEXP_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_SECTION_ELEM;
+ (void) _PUT_COMPILER_SECTION_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_ID_ATTR;
+ (void) _PUT_COMPILER_ID_ATTR: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_TYPE_ATTR;
+ (void) _PUT_COMPILER_TYPE_ATTR: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_IDENTITY_ELEM;
+ (void) _PUT_COMPILER_IDENTITY_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_FLAG_ELEM;
+ (void) _PUT_COMPILER_FLAG_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_VALUE_ATTR;
+ (void) _PUT_COMPILER_VALUE_ATTR: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_JOIN_ELEM;
+ (void) _PUT_COMPILER_JOIN_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_BLANK_ELEM;
+ (void) _PUT_COMPILER_BLANK_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_POSTGENERATOR_ELEM;
+ (void) _PUT_COMPILER_POSTGENERATOR_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_GROUP_ELEM;
+ (void) _PUT_COMPILER_GROUP_ELEM: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_LEMMA_ATTR;
+ (void) _PUT_COMPILER_LEMMA_ATTR: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_IGNORE_ATTR;
+ (void) _PUT_COMPILER_IGNORE_ATTR: (java_lang_String*) v;
+ (java_lang_String*) _GET_COMPILER_IGNORE_YES_VAL;
+ (void) _PUT_COMPILER_IGNORE_YES_VAL: (java_lang_String*) v;
- (java_lang_String*) _GET_current_paradigm;
- (void) _PUT_current_paradigm: (java_lang_String*) v;
- (java_lang_String*) _GET_current_section;
- (void) _PUT_current_section: (java_lang_String*) v;
- (java_lang_String*) _GET_direction;
- (void) _PUT_direction: (java_lang_String*) v;
- (java_lang_String*) _GET_letters;
- (void) _PUT_letters: (java_lang_String*) v;
- (org_apertium_lttoolbox_Alphabet*) _GET_alphabet;
- (void) _PUT_alphabet: (org_apertium_lttoolbox_Alphabet*) v;
- (java_util_Map*) _GET_paradigms;
- (void) _PUT_paradigms: (java_util_Map*) v;
- (java_util_Map*) _GET_sections;
- (void) _PUT_sections: (java_util_Map*) v;
- (java_util_HashMap*) _GET_prefix_paradigms;
- (void) _PUT_prefix_paradigms: (java_util_HashMap*) v;
- (java_util_HashMap*) _GET_suffix_paradigms;
- (void) _PUT_suffix_paradigms: (java_util_HashMap*) v;
- (java_util_HashMap*) _GET_postsuffix_paradigms;
- (void) _PUT_postsuffix_paradigms: (java_util_HashMap*) v;
- (java_util_HashMap*) _GET_acx_map;
- (void) _PUT_acx_map: (java_util_HashMap*) v;
- (int) _GET_acx_current_char;
- (void) _PUT_acx_current_char: (int) v;
- (java_lang_String*) _GET_version;
- (void) _PUT_version: (java_lang_String*) v;
- (java_lang_String*) _GET_encoding;
- (void) _PUT_encoding: (java_lang_String*) v;
- (java_lang_Boolean*) _GET_standalone;
- (void) _PUT_standalone: (java_lang_Boolean*) v;
- (javax_xml_stream_XMLStreamReader*) _GET_reader;
- (void) _PUT_reader: (javax_xml_stream_XMLStreamReader*) v;
+ (int) _GET_DEBUG;
+ (void) _PUT_DEBUG: (int) v;
- (void) __init_org_apertium_lttoolbox_compile_Compile__;
- (void) parse___java_lang_String_java_lang_String :(java_lang_String*)n1 :(java_lang_String*)n2;
- (void) parseACX___java_lang_String_java_lang_String :(java_lang_String*)n1 :(java_lang_String*)n2;
- (void) write___java_io_OutputStream :(java_io_OutputStream*)n1;
- (int) allBlanks__;
- (void) insertEntryTokens___java_util_ArrayList :(java_util_ArrayList*)n1;
- (int) matchTransduction___java_util_ArrayList_java_util_ArrayList_int_org_apertium_lttoolbox_compile_Transducer :(java_util_ArrayList*)n1 :(java_util_ArrayList*)n2 :(int)n3 :(org_apertium_lttoolbox_compile_Transducer*)n4;
- (org_apertium_lttoolbox_compile_EntryToken*) procRegexp__;
- (void) procAlphabet__;
- (void) procEntry__;
- (void) procNode__;
- (void) procNodeACX__;
- (void) procParDef__;
- (java_lang_String*) attrib___java_lang_String :(java_lang_String*)n1;
- (void) procSDef__;
- (void) procSection__;
- (org_apertium_lttoolbox_compile_EntryToken*) procIdentity__;
- (org_apertium_lttoolbox_compile_EntryToken*) procFlag__;
- (org_apertium_lttoolbox_compile_EntryToken*) procTransduction__;
- (org_apertium_lttoolbox_compile_EntryToken*) procPar__;
- (void) readString___java_util_List_java_lang_String :(java_util_List*)n1 :(java_lang_String*)n2;
- (void) requireAttribute___java_lang_String_java_lang_String_java_lang_String :(java_lang_String*)n1 :(java_lang_String*)n2 :(java_lang_String*)n3;
- (void) requireEmptyError___java_lang_String :(java_lang_String*)n1;
- (void) skipBlanks__;
- (void) skipUntil___java_lang_String_java_lang_String :(java_lang_String*)n1 :(java_lang_String*)n2;
+ (org_apertium_lttoolbox_compile_Compile*) DEBUG_read___java_io_InputStream :(java_io_InputStream*)n1;
- (int) DEBUG_compare___org_apertium_lttoolbox_compile_Compile :(org_apertium_lttoolbox_compile_Compile*)n1;
+ (void) main___java_lang_String_ARRAYTYPE :(XMLVMArray*)n1;
+ (void) __clinit_org_apertium_lttoolbox_compile_Compile;

@end
