#import "xmlvm.h"
#import "java_lang_Object.h"

// For circular include:
@class java_lang_Object;
@class java_lang_String;
@class java_lang_Integer;
@class org_apertium_lttoolbox_compile_Transducer;
@class org_apertium_lttoolbox_Alphabet;
@class org_apertium_lttoolbox_compile_RegexpCompiler;
@class java_util_Iterator;
@class java_util_TreeSet;
@class java_lang_RuntimeException;
@class java_lang_Throwable;
@class java_util_Set;

// Automatically generated by xmlvm2obj. Do not edit!


	
@interface org_apertium_lttoolbox_compile_RegexpCompiler : java_lang_Object
{
@private int org_apertium_lttoolbox_compile_RegexpCompiler_token;
@private java_lang_String* org_apertium_lttoolbox_compile_RegexpCompiler_input;
@private org_apertium_lttoolbox_Alphabet* org_apertium_lttoolbox_compile_RegexpCompiler_alphabet;
@private org_apertium_lttoolbox_compile_Transducer* org_apertium_lttoolbox_compile_RegexpCompiler_transducer;
@private java_lang_Integer* org_apertium_lttoolbox_compile_RegexpCompiler_state;
@private int org_apertium_lttoolbox_compile_RegexpCompiler_letter;
@private java_lang_String* org_apertium_lttoolbox_compile_RegexpCompiler_postop;
@private java_util_Set* org_apertium_lttoolbox_compile_RegexpCompiler_brackets;

}
+ (void) initialize;
- (id) init;
+ (int) _GET_FIN_FICHERO;
+ (void) _PUT_FIN_FICHERO: (int) v;
- (int) _GET_token;
- (void) _PUT_token: (int) v;
- (java_lang_String*) _GET_input;
- (void) _PUT_input: (java_lang_String*) v;
- (org_apertium_lttoolbox_Alphabet*) _GET_alphabet;
- (void) _PUT_alphabet: (org_apertium_lttoolbox_Alphabet*) v;
- (org_apertium_lttoolbox_compile_Transducer*) _GET_transducer;
- (void) _PUT_transducer: (org_apertium_lttoolbox_compile_Transducer*) v;
- (java_lang_Integer*) _GET_state;
- (void) _PUT_state: (java_lang_Integer*) v;
- (int) _GET_letter;
- (void) _PUT_letter: (int) v;
- (java_lang_String*) _GET_postop;
- (void) _PUT_postop: (java_lang_String*) v;
- (java_util_Set*) _GET_brackets;
- (void) _PUT_brackets: (java_util_Set*) v;
- (void) __init_org_apertium_lttoolbox_compile_RegexpCompiler__;
- (int) isReserved___int :(int)n1;
- (void) error__;
- (void) errorConsuming___int :(int)n1;
- (void) consume___int :(int)n1;
- (void) compile___java_lang_String :(java_lang_String*)n1;
- (void) S__;
- (void) RExpr__;
- (void) Cola__;
- (void) Term__;
- (void) RExprp__;
- (void) Letra__;
- (void) Postop__;
- (void) Esp__;
- (void) Lista__;
- (void) Reservado__;
- (void) Elem__;
- (void) ColaLetra__;
- (void) setAlphabet___org_apertium_lttoolbox_Alphabet :(org_apertium_lttoolbox_Alphabet*)n1;
- (org_apertium_lttoolbox_compile_Transducer*) getTransducer__;
- (void) initialize___org_apertium_lttoolbox_Alphabet :(org_apertium_lttoolbox_Alphabet*)n1;

@end

