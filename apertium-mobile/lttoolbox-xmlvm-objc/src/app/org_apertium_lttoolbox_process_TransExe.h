#import "xmlvm.h"
#import "java_lang_Object.h"

// For circular include:
@class java_util_ArrayList;
@class java_io_InputStream;
@class org_apertium_lttoolbox_Alphabet_IntegerPair;
@class org_apertium_lttoolbox_process_TransExe;
@class java_lang_Object;
@class org_apertium_lttoolbox_Compression;
@class org_apertium_lttoolbox_Alphabet;
@class org_apertium_lttoolbox_process_Node;

// Automatically generated by xmlvm2obj. Do not edit!


	
@interface org_apertium_lttoolbox_process_TransExe : java_lang_Object
{
@private int org_apertium_lttoolbox_process_TransExe_initial_id;
@private XMLVMArray* org_apertium_lttoolbox_process_TransExe_node_list2;
@private java_util_ArrayList* org_apertium_lttoolbox_process_TransExe_finals2;

}
+ (void) initialize;
- (id) init;
- (int) _GET_initial_id;
- (void) _PUT_initial_id: (int) v;
- (XMLVMArray*) _GET_node_list2;
- (void) _PUT_node_list2: (XMLVMArray*) v;
- (java_util_ArrayList*) _GET_finals2;
- (void) _PUT_finals2: (java_util_ArrayList*) v;
- (void) __init_org_apertium_lttoolbox_process_TransExe__;
- (void) read___java_io_InputStream_org_apertium_lttoolbox_Alphabet :(java_io_InputStream*)n1 :(org_apertium_lttoolbox_Alphabet*)n2;
- (org_apertium_lttoolbox_process_Node*) getInitial__;
- (java_util_ArrayList*) getFinals__;

@end

