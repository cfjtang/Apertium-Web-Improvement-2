#import "xmlvm.h"
#import "java_lang_Object.h"

// For circular include:
@class java_util_ArrayList;
@class org_apertium_lttoolbox_process_Pool;
@class java_util_Iterator;
@class java_lang_Object;
@class java_util_List;

// Automatically generated by xmlvm2obj. Do not edit!


	
@interface org_apertium_lttoolbox_process_Pool : java_lang_Object
{
@private java_util_List* org_apertium_lttoolbox_process_Pool_free;
@private java_util_List* org_apertium_lttoolbox_process_Pool_created;

}
+ (void) initialize;
- (id) init;
- (java_util_List*) _GET_free;
- (void) _PUT_free: (java_util_List*) v;
- (java_util_List*) _GET_created;
- (void) _PUT_created: (java_util_List*) v;
- (void) copy___org_apertium_lttoolbox_process_Pool :(org_apertium_lttoolbox_process_Pool*)n1;
- (void) destroy__;
- (void) init___int :(int)n1;
- (void) init___int_java_lang_Object :(int)n1 :(java_lang_Object*)n2;
- (void) __init_org_apertium_lttoolbox_process_Pool__;
- (void) __init_org_apertium_lttoolbox_process_Pool___int_java_lang_Object :(int)n1 :(java_lang_Object*)n2;
- (void) __init_org_apertium_lttoolbox_process_Pool___int :(int)n1;
- (void) __init_org_apertium_lttoolbox_process_Pool___org_apertium_lttoolbox_process_Pool :(org_apertium_lttoolbox_process_Pool*)n1;
- (java_lang_Object*) get__;
- (void) release___java_lang_Object :(java_lang_Object*)n1;

@end

