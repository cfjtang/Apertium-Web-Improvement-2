#import "java_lang_Object.h"
#import "java_util_Collection.h"

// Automatically generated by xmlvm2obj. Do not edit!


#import "org_apertium_lttoolbox_Expander_EntList.h"


@implementation org_apertium_lttoolbox_Expander_EntList;

+ (void) initialize
{
    if (strcmp(class_getName(self), "org_apertium_lttoolbox_Expander_EntList") == 0) {
    }
}

- (id) init
{
    if (self = [super init]) {
    }
    return self;
}

- (void) dealloc
{
    [super dealloc];
}

- (void) __init_org_apertium_lttoolbox_Expander_EntList__
{
    XMLVMElem _stack[1];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    [((super)) __init_java_util_ArrayList__];
    label1:;
    [_pool release];
    return;
}


- (void) __init_org_apertium_lttoolbox_Expander_EntList___java_util_Collection :(java_util_Collection*)n1
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
    _sp -= 2;
    [((super)) __init_java_util_ArrayList___java_util_Collection:_stack[_sp + 1].o];
    label1:;
    [_pool release];
    return;
}



@end

