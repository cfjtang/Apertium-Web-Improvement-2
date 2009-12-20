#import "xmlvm.h"
#import "java_lang_Object.h"

// For circular include:
@class java_lang_Object;
@class java_util_HashSet;
@class java_lang_Character;
@class java_util_Arrays;
@class org_apertium_lttoolbox_process_SetOfCharacters;
@class java_lang_Math;

// Automatically generated by xmlvm2obj. Do not edit!


	
@interface org_apertium_lttoolbox_process_SetOfCharacters : java_lang_Object
{
@private XMLVMArray* org_apertium_lttoolbox_process_SetOfCharacters_set;
@private java_util_HashSet* org_apertium_lttoolbox_process_SetOfCharacters_otherChars;

}
+ (void) initialize;
- (id) init;
+ (int) _GET_LIMIT;
+ (void) _PUT_LIMIT: (int) v;
- (XMLVMArray*) _GET_set;
- (void) _PUT_set: (XMLVMArray*) v;
- (java_util_HashSet*) _GET_otherChars;
- (void) _PUT_otherChars: (java_util_HashSet*) v;
+ (int) _GET_max_encountered;
+ (void) _PUT_max_encountered: (int) v;
- (void) __init_org_apertium_lttoolbox_process_SetOfCharacters__;
- (void) add___char :(int)n1;
- (void) clear__;
- (int) contains___char :(int)n1;
+ (void) __clinit_org_apertium_lttoolbox_process_SetOfCharacters;

@end

