#import "xmlvm.h"
#import "java_lang_Object.h"

// For circular include:
@class org_apertium_lttoolbox_Alphabet_IntegerPair;
@class java_lang_Object;
@class java_lang_Character;
@class java_lang_StringBuilder;
@class java_lang_String;
@class java_lang_Integer;

// Automatically generated by xmlvm2obj. Do not edit!


	
@interface org_apertium_lttoolbox_Alphabet_IntegerPair : java_lang_Object
{
@private int org_apertium_lttoolbox_Alphabet_IntegerPair_first;
@private int org_apertium_lttoolbox_Alphabet_IntegerPair_second;

}
+ (void) initialize;
- (id) init;
- (int) _GET_first;
- (void) _PUT_first: (int) v;
- (int) _GET_second;
- (void) _PUT_second: (int) v;
- (void) __init_org_apertium_lttoolbox_Alphabet_IntegerPair___java_lang_Integer_java_lang_Integer :(java_lang_Integer*)n1 :(java_lang_Integer*)n2;
- (int) hashCode__;
- (int) equals___java_lang_Object :(java_lang_Object*)n1;
- (java_lang_String*) toString__;

@end
