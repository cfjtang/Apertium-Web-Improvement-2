#import "xmlvm.h"
#import "java_lang_Object.h"

// For circular include:
@class java_io_InputStream;
@class java_util_Map;
@class java_lang_StringBuilder;
@class org_apertium_lttoolbox_compile_Transducer;
@class java_util_Vector;
@class org_apertium_lttoolbox_transfer_MatchExe;
@class java_util_Set;
@class org_apertium_lttoolbox_transfer_PatternStore;
@class java_util_Collection;
@class java_util_Map_Entry;
@class java_lang_Object;
@class java_util_List;
@class org_apertium_lttoolbox_transfer_PatternList;
@class java_lang_String;
@class java_lang_Integer;
@class java_lang_IllegalStateException;
@class org_apertium_lttoolbox_Alphabet;
@class java_util_Iterator;
@class java_lang_RuntimeException;
@class java_io_OutputStream;

// Automatically generated by xmlvm2obj. Do not edit!


	
@interface org_apertium_lttoolbox_transfer_PatternList : java_lang_Object
{
@private org_apertium_lttoolbox_Alphabet* org_apertium_lttoolbox_transfer_PatternList_alphabet;
@private org_apertium_lttoolbox_transfer_PatternStore* org_apertium_lttoolbox_transfer_PatternList_patterns;
@private int org_apertium_lttoolbox_transfer_PatternList_sequence;
@private java_util_List* org_apertium_lttoolbox_transfer_PatternList_sequence_data;
@private org_apertium_lttoolbox_compile_Transducer* org_apertium_lttoolbox_transfer_PatternList_transducer;
@private java_util_Map* org_apertium_lttoolbox_transfer_PatternList_final_type;
@private int org_apertium_lttoolbox_transfer_PatternList_sequence_id;

}
+ (void) initialize;
- (id) init;
- (org_apertium_lttoolbox_Alphabet*) _GET_alphabet;
- (void) _PUT_alphabet: (org_apertium_lttoolbox_Alphabet*) v;
- (org_apertium_lttoolbox_transfer_PatternStore*) _GET_patterns;
- (void) _PUT_patterns: (org_apertium_lttoolbox_transfer_PatternStore*) v;
- (int) _GET_sequence;
- (void) _PUT_sequence: (int) v;
- (java_util_List*) _GET_sequence_data;
- (void) _PUT_sequence_data: (java_util_List*) v;
- (org_apertium_lttoolbox_compile_Transducer*) _GET_transducer;
- (void) _PUT_transducer: (org_apertium_lttoolbox_compile_Transducer*) v;
- (java_util_Map*) _GET_final_type;
- (void) _PUT_final_type: (java_util_Map*) v;
- (int) _GET_sequence_id;
- (void) _PUT_sequence_id: (int) v;
+ (java_lang_String*) _GET_ANY_CHAR;
+ (void) _PUT_ANY_CHAR: (java_lang_String*) v;
+ (java_lang_String*) _GET_ANY_TAG;
+ (void) _PUT_ANY_TAG: (java_lang_String*) v;
+ (java_lang_String*) _GET_QUEUE;
+ (void) _PUT_QUEUE: (java_lang_String*) v;
- (void) __init_org_apertium_lttoolbox_transfer_PatternList__;
- (void) beginSequence__;
- (void) endSequence__;
- (void) insertOutOfSequence___java_lang_String_java_lang_String_java_util_List :(java_lang_String*)n1 :(java_lang_String*)n2 :(java_util_List*)n3;
- (void) insertIntoSequence___int_java_lang_String_java_lang_String :(int)n1 :(java_lang_String*)n2 :(java_lang_String*)n3;
- (void) insert___int_java_lang_String_java_lang_String :(int)n1 :(java_lang_String*)n2 :(java_lang_String*)n3;
- (void) insert___int_int :(int)n1 :(int)n2;
- (int) tagCount___java_lang_String :(java_lang_String*)n1;
- (java_lang_String*) tagAt___java_lang_String_int :(java_lang_String*)n1 :(int)n2;
- (org_apertium_lttoolbox_transfer_PatternStore*) getPatterns__;
- (void) buildTransducer__;
- (void) write___java_io_OutputStream :(java_io_OutputStream*)n1;
- (void) read___java_io_InputStream :(java_io_InputStream*)n1;
- (org_apertium_lttoolbox_transfer_MatchExe*) newMatchExe__;
- (org_apertium_lttoolbox_Alphabet*) getAlphabet__;

@end

