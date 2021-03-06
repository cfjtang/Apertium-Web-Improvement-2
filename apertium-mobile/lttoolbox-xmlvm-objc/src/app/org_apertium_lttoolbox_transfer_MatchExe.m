#import "java_util_Map.h"
#import "java_util_List.h"
#import "java_lang_Integer.h"
#import "org_apertium_lttoolbox_compile_Transducer.h"
#import "java_util_Iterator.h"
#import "java_util_Vector.h"
#import "org_apertium_lttoolbox_transfer_MatchNode.h"
#import "java_util_Set.h"
#import "java_util_Collection.h"

// Automatically generated by xmlvm2obj. Do not edit!


#import "org_apertium_lttoolbox_transfer_MatchExe.h"


@implementation org_apertium_lttoolbox_transfer_MatchExe;

+ (void) initialize
{
    if (strcmp(class_getName(self), "org_apertium_lttoolbox_transfer_MatchExe") == 0) {
    }
}

- (id) init
{
    if (self = [super init]) {
        org_apertium_lttoolbox_transfer_MatchExe_node_list = (id) [NSNull null];
        org_apertium_lttoolbox_transfer_MatchExe_finals = (id) [NSNull null];
    }
    return self;
}

- (void) dealloc
{
    [org_apertium_lttoolbox_transfer_MatchExe_node_list release];
    [org_apertium_lttoolbox_transfer_MatchExe_finals release];
    [super dealloc];
}

- (int) _GET_initial_id
{
    return org_apertium_lttoolbox_transfer_MatchExe_initial_id;
}

- (void) _PUT_initial_id: (int) v
{
    org_apertium_lttoolbox_transfer_MatchExe_initial_id = v;
}

- (java_util_List*) _GET_node_list
{
    return [[org_apertium_lttoolbox_transfer_MatchExe_node_list retain] autorelease];
}

- (void) _PUT_node_list: (java_util_List*) v
{
    [v retain];
    [org_apertium_lttoolbox_transfer_MatchExe_node_list release];
    org_apertium_lttoolbox_transfer_MatchExe_node_list = v;
}

- (java_util_Map*) _GET_finals
{
    return [[org_apertium_lttoolbox_transfer_MatchExe_finals retain] autorelease];
}

- (void) _PUT_finals: (java_util_Map*) v
{
    [v retain];
    [org_apertium_lttoolbox_transfer_MatchExe_finals release];
    org_apertium_lttoolbox_transfer_MatchExe_finals = v;
}

- (void) __init_org_apertium_lttoolbox_transfer_MatchExe__
{
    XMLVMElem _stack[3];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    [((super)) __init_java_lang_Object__];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = [[[java_util_Vector alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _sp -= 1;
    [((java_util_Vector*) _stack[_sp].o) __init_java_util_Vector__];
    _op1.o = _stack[--_sp].o;
    _op2.o = _stack[--_sp].o;
    [((org_apertium_lttoolbox_transfer_MatchExe*) _op2.o) _PUT_node_list: _op1.o];
    label1:;
    [_pool release];
    return;
}


- (void) __init_org_apertium_lttoolbox_transfer_MatchExe___org_apertium_lttoolbox_transfer_MatchExe :(org_apertium_lttoolbox_transfer_MatchExe*)n1
{
    XMLVMElem _stack[3];
    XMLVMElem _locals[2];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    _locals[1].o = n1;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    [((super)) __init_java_lang_Object__];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = [[[java_util_Vector alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _sp -= 1;
    [((java_util_Vector*) _stack[_sp].o) __init_java_util_Vector__];
    _op1.o = _stack[--_sp].o;
    _op2.o = _stack[--_sp].o;
    [((org_apertium_lttoolbox_transfer_MatchExe*) _op2.o) _PUT_node_list: _op1.o];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[1].o;
    _sp -= 2;
    [((org_apertium_lttoolbox_transfer_MatchExe*) _stack[_sp].o) copy___org_apertium_lttoolbox_transfer_MatchExe:_stack[_sp + 1].o];
    label1:;
    [_pool release];
    return;
}


- (void) __init_org_apertium_lttoolbox_transfer_MatchExe___org_apertium_lttoolbox_compile_Transducer_java_util_Map :(org_apertium_lttoolbox_compile_Transducer*)n1 :(java_util_Map*)n2
{
    XMLVMElem _stack[4];
    XMLVMElem _locals[13];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    _locals[1].o = n1;
    _locals[2].o = n2;
    label10:;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    [((super)) __init_java_lang_Object__];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = [[[java_util_Vector alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _sp -= 1;
    [((java_util_Vector*) _stack[_sp].o) __init_java_util_Vector__];
    _op1.o = _stack[--_sp].o;
    _op2.o = _stack[--_sp].o;
    [((org_apertium_lttoolbox_transfer_MatchExe*) _op2.o) _PUT_node_list: _op1.o];
    _stack[_sp++].o = _locals[1].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_compile_Transducer*) _op1.o)  _GET_transitions];
    _stack[_sp++].o = _op2.o;
    _sp -= 1;
    _op1.o = [((java_util_Map*) _stack[_sp].o) keySet__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp -= 1;
    _op1.o = [((java_util_Set*) _stack[_sp].o) iterator__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[3].o = _op1.o;
    label1:;
    _stack[_sp++].o = _locals[3].o;
    _sp -= 1;
    _op1.i = [((java_util_Iterator*) _stack[_sp].o) hasNext__];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i == 0) goto label0;
    _stack[_sp++].o = _locals[3].o;
    _sp -= 1;
    _op1.o = [((java_util_Iterator*) _stack[_sp].o) next__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[4].o = _op1.o;
    label22:;
    _stack[_sp++].o = _locals[1].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_compile_Transducer*) _op1.o)  _GET_transitions];
    _stack[_sp++].o = _op2.o;
    _stack[_sp++].o = _locals[4].o;
    _sp -= 2;
    _op1.o = [((java_util_Map*) _stack[_sp].o) get___java_lang_Object:_stack[_sp + 1].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[5].o = _op1.o;
    label30:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_transfer_MatchExe*) _op1.o)  _GET_node_list];
    _stack[_sp++].o = _op2.o;
    _stack[_sp++].o = [[[org_apertium_lttoolbox_transfer_MatchNode alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _stack[_sp++].o = _locals[5].o;
    _sp -= 1;
    _op1.i = [((java_util_Map*) _stack[_sp].o) size__];
    _stack[_sp++].i = _op1.i;
    _sp -= 2;
    [((org_apertium_lttoolbox_transfer_MatchNode*) _stack[_sp].o) __init_org_apertium_lttoolbox_transfer_MatchNode___int:_stack[_sp + 1].i];
    _sp -= 2;
    _op1.i = [((java_util_List*) _stack[_sp].o) add___java_lang_Object:_stack[_sp + 1].o];
    _stack[_sp++].i = _op1.i;
    _sp--;
    label23:;
    goto label1;
    label0:;
    _stack[_sp++].o = _locals[2].o;
    _sp -= 1;
    _op1.o = [((java_util_Map*) _stack[_sp].o) keySet__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp -= 1;
    _op1.o = [((java_util_Set*) _stack[_sp].o) iterator__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[3].o = _op1.o;
    label3:;
    _stack[_sp++].o = _locals[3].o;
    _sp -= 1;
    _op1.i = [((java_util_Iterator*) _stack[_sp].o) hasNext__];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i == 0) goto label2;
    _stack[_sp++].o = _locals[3].o;
    _sp -= 1;
    _op1.o = [((java_util_Iterator*) _stack[_sp].o) next__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[4].o = _op1.o;
    label24:;
    _stack[_sp++].o = _locals[2].o;
    _stack[_sp++].o = _locals[4].o;
    _sp -= 2;
    _op1.o = [((java_util_Map*) _stack[_sp].o) get___java_lang_Object:_stack[_sp + 1].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[5].o = _op1.o;
    label32:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_transfer_MatchExe*) _op1.o)  _GET_finals];
    _stack[_sp++].o = _op2.o;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_transfer_MatchExe*) _op1.o)  _GET_node_list];
    _stack[_sp++].o = _op2.o;
    _stack[_sp++].o = _locals[4].o;
    _sp -= 1;
    _op1.i = [((java_lang_Integer*) _stack[_sp].o) intValue__];
    _stack[_sp++].i = _op1.i;
    _sp -= 2;
    _op1.o = [((java_util_List*) _stack[_sp].o) get___int:_stack[_sp + 1].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _stack[_sp++].o = _locals[5].o;
    _sp -= 3;
    _op1.o = [((java_util_Map*) _stack[_sp].o) put___java_lang_Object_java_lang_Object:_stack[_sp + 1].o:_stack[_sp + 2].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp--;
    label25:;
    goto label3;
    label2:;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[1].o;
    _sp -= 1;
    _op1.o = [((org_apertium_lttoolbox_compile_Transducer*) _stack[_sp].o) getInitial__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp -= 1;
    _op1.i = [((java_lang_Integer*) _stack[_sp].o) intValue__];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    [((org_apertium_lttoolbox_transfer_MatchExe*) _op2.o) _PUT_initial_id: _op1.i];
    _stack[_sp++].o = _locals[1].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_compile_Transducer*) _op1.o)  _GET_transitions];
    _stack[_sp++].o = _op2.o;
    _sp -= 1;
    _op1.o = [((java_util_Map*) _stack[_sp].o) keySet__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp -= 1;
    _op1.o = [((java_util_Set*) _stack[_sp].o) iterator__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[3].o = _op1.o;
    label9:;
    _stack[_sp++].o = _locals[3].o;
    _sp -= 1;
    _op1.i = [((java_util_Iterator*) _stack[_sp].o) hasNext__];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i == 0) goto label4;
    _stack[_sp++].o = _locals[3].o;
    _sp -= 1;
    _op1.o = [((java_util_Iterator*) _stack[_sp].o) next__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[4].o = _op1.o;
    label26:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_transfer_MatchExe*) _op1.o)  _GET_node_list];
    _stack[_sp++].o = _op2.o;
    _stack[_sp++].o = _locals[4].o;
    _sp -= 1;
    _op1.i = [((java_lang_Integer*) _stack[_sp].o) intValue__];
    _stack[_sp++].i = _op1.i;
    _sp -= 2;
    _op1.o = [((java_util_List*) _stack[_sp].o) get___int:_stack[_sp + 1].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[5].o = _op1.o;
    label28:;
    _stack[_sp++].i = 0;
    _op1.i = _stack[--_sp].i;
    _locals[6].i = _op1.i;
    label34:;
    _stack[_sp++].o = _locals[1].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_compile_Transducer*) _op1.o)  _GET_transitions];
    _stack[_sp++].o = _op2.o;
    _stack[_sp++].o = _locals[4].o;
    _sp -= 2;
    _op1.o = [((java_util_Map*) _stack[_sp].o) get___java_lang_Object:_stack[_sp + 1].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[7].o = _op1.o;
    label36:;
    _stack[_sp++].o = _locals[7].o;
    _sp -= 1;
    _op1.o = [((java_util_Map*) _stack[_sp].o) keySet__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp -= 1;
    _op1.o = [((java_util_Set*) _stack[_sp].o) iterator__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[8].o = _op1.o;
    label8:;
    _stack[_sp++].o = _locals[8].o;
    _sp -= 1;
    _op1.i = [((java_util_Iterator*) _stack[_sp].o) hasNext__];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i == 0) goto label5;
    _stack[_sp++].o = _locals[8].o;
    _sp -= 1;
    _op1.o = [((java_util_Iterator*) _stack[_sp].o) next__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[9].o = _op1.o;
    label40:;
    _stack[_sp++].o = _locals[7].o;
    _stack[_sp++].o = _locals[9].o;
    _sp -= 2;
    _op1.o = [((java_util_Map*) _stack[_sp].o) get___java_lang_Object:_stack[_sp + 1].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[10].o = _op1.o;
    label42:;
    _stack[_sp++].o = _locals[10].o;
    _sp -= 1;
    _op1.o = [((java_util_Collection*) _stack[_sp].o) iterator__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[11].o = _op1.o;
    label7:;
    _stack[_sp++].o = _locals[11].o;
    _sp -= 1;
    _op1.i = [((java_util_Iterator*) _stack[_sp].o) hasNext__];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i == 0) goto label6;
    _stack[_sp++].o = _locals[11].o;
    _sp -= 1;
    _op1.o = [((java_util_Iterator*) _stack[_sp].o) next__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[12].o = _op1.o;
    label46:;
    _stack[_sp++].o = _locals[5].o;
    _stack[_sp++].o = _locals[9].o;
    _sp -= 1;
    _op1.i = [((java_lang_Integer*) _stack[_sp].o) intValue__];
    _stack[_sp++].i = _op1.i;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_transfer_MatchExe*) _op1.o)  _GET_node_list];
    _stack[_sp++].o = _op2.o;
    _stack[_sp++].o = _locals[12].o;
    _sp -= 1;
    _op1.i = [((java_lang_Integer*) _stack[_sp].o) intValue__];
    _stack[_sp++].i = _op1.i;
    _sp -= 2;
    _op1.o = [((java_util_List*) _stack[_sp].o) get___int:_stack[_sp + 1].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.i = _locals[6].i;
    _stack[_sp++].i = _op1.i;
    _locals[6].i += 1;
    _sp -= 4;
    [((org_apertium_lttoolbox_transfer_MatchNode*) _stack[_sp].o) addTransition___int_org_apertium_lttoolbox_transfer_MatchNode_int:_stack[_sp + 1].i:_stack[_sp + 2].o:_stack[_sp + 3].i];
    label47:;
    goto label7;
    label6:;
    goto label8;
    label5:;
    goto label9;
    label4:;
    [_pool release];
    return;
}


- (void) copy___org_apertium_lttoolbox_transfer_MatchExe :(org_apertium_lttoolbox_transfer_MatchExe*)n1
{
    XMLVMElem _stack[2];
    XMLVMElem _locals[2];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    _locals[1].o = n1;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[1].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((org_apertium_lttoolbox_transfer_MatchExe*) _op1.o)  _GET_initial_id];
    _stack[_sp++].i = _op2.i;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    [((org_apertium_lttoolbox_transfer_MatchExe*) _op2.o) _PUT_initial_id: _op1.i];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[1].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_transfer_MatchExe*) _op1.o)  _GET_node_list];
    _stack[_sp++].o = _op2.o;
    _op1.o = _stack[--_sp].o;
    _op2.o = _stack[--_sp].o;
    [((org_apertium_lttoolbox_transfer_MatchExe*) _op2.o) _PUT_node_list: _op1.o];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[1].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_transfer_MatchExe*) _op1.o)  _GET_finals];
    _stack[_sp++].o = _op2.o;
    _op1.o = _stack[--_sp].o;
    _op2.o = _stack[--_sp].o;
    [((org_apertium_lttoolbox_transfer_MatchExe*) _op2.o) _PUT_finals: _op1.o];
    label1:;
    [_pool release];
    return;
}


- (org_apertium_lttoolbox_transfer_MatchNode*) getInitial__
{
    XMLVMElem _stack[2];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_transfer_MatchExe*) _op1.o)  _GET_node_list];
    _stack[_sp++].o = _op2.o;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((org_apertium_lttoolbox_transfer_MatchExe*) _op1.o)  _GET_initial_id];
    _stack[_sp++].i = _op2.i;
    _sp -= 2;
    _op1.o = [((java_util_List*) _stack[_sp].o) get___int:_stack[_sp + 1].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    label1:;
    _op1.o = _stack[--_sp].o;
    [_op1.o retain];
    [_pool release];
    return _op1.o;
}


- (java_util_Map*) getFinals__
{
    XMLVMElem _stack[1];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_transfer_MatchExe*) _op1.o)  _GET_finals];
    _stack[_sp++].o = _op2.o;
    label1:;
    _op1.o = _stack[--_sp].o;
    [_op1.o retain];
    [_pool release];
    return _op1.o;
}



@end

