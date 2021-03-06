#import "java_util_HashSet.h"
#import "java_lang_Character.h"
#import "java_util_Arrays.h"
#import "java_lang_Math.h"

// Automatically generated by xmlvm2obj. Do not edit!


#import "org_apertium_lttoolbox_process_SetOfCharacters.h"

int _STATIC_org_apertium_lttoolbox_process_SetOfCharacters_LIMIT = 255;
int _STATIC_org_apertium_lttoolbox_process_SetOfCharacters_max_encountered;

@implementation org_apertium_lttoolbox_process_SetOfCharacters;

+ (void) initialize
{
    if (strcmp(class_getName(self), "org_apertium_lttoolbox_process_SetOfCharacters") == 0) {
        [org_apertium_lttoolbox_process_SetOfCharacters __clinit_org_apertium_lttoolbox_process_SetOfCharacters];
    }
}

- (id) init
{
    if (self = [super init]) {
        org_apertium_lttoolbox_process_SetOfCharacters_set = (id) [NSNull null];
        org_apertium_lttoolbox_process_SetOfCharacters_otherChars = (id) [NSNull null];
    }
    return self;
}

- (void) dealloc
{
    [org_apertium_lttoolbox_process_SetOfCharacters_set release];
    [org_apertium_lttoolbox_process_SetOfCharacters_otherChars release];
    [super dealloc];
}

+ (int) _GET_LIMIT
{
    return _STATIC_org_apertium_lttoolbox_process_SetOfCharacters_LIMIT;
}

+ (void) _PUT_LIMIT: (int) v
{
    _STATIC_org_apertium_lttoolbox_process_SetOfCharacters_LIMIT = v;
}

- (XMLVMArray*) _GET_set
{
    return [[org_apertium_lttoolbox_process_SetOfCharacters_set retain] autorelease];
}

- (void) _PUT_set: (XMLVMArray*) v
{
    [v retain];
    [org_apertium_lttoolbox_process_SetOfCharacters_set release];
    org_apertium_lttoolbox_process_SetOfCharacters_set = v;
}

- (java_util_HashSet*) _GET_otherChars
{
    return [[org_apertium_lttoolbox_process_SetOfCharacters_otherChars retain] autorelease];
}

- (void) _PUT_otherChars: (java_util_HashSet*) v
{
    [v retain];
    [org_apertium_lttoolbox_process_SetOfCharacters_otherChars release];
    org_apertium_lttoolbox_process_SetOfCharacters_otherChars = v;
}

+ (int) _GET_max_encountered
{
    return _STATIC_org_apertium_lttoolbox_process_SetOfCharacters_max_encountered;
}

+ (void) _PUT_max_encountered: (int) v
{
    _STATIC_org_apertium_lttoolbox_process_SetOfCharacters_max_encountered = v;
}

- (void) __init_org_apertium_lttoolbox_process_SetOfCharacters__
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
    _stack[_sp++].i = 255;
    _op1.i = _stack[--_sp].i;
    _stack[_sp++].o = [XMLVMArray createSingleDimensionWithType:1 andSize:_op1.i];
    _op1.o = _stack[--_sp].o;
    _op2.o = _stack[--_sp].o;
    [((org_apertium_lttoolbox_process_SetOfCharacters*) _op2.o) _PUT_set: _op1.o];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = [[[java_util_HashSet alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _sp -= 1;
    [((java_util_HashSet*) _stack[_sp].o) __init_java_util_HashSet__];
    _op1.o = _stack[--_sp].o;
    _op2.o = _stack[--_sp].o;
    [((org_apertium_lttoolbox_process_SetOfCharacters*) _op2.o) _PUT_otherChars: _op1.o];
    label1:;
    [_pool release];
    return;
}


- (void) add___char :(int)n1
{
    XMLVMElem _stack[3];
    XMLVMElem _locals[2];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    _locals[1].i = n1;
    label2:;
    _op1.i = [org_apertium_lttoolbox_process_SetOfCharacters _GET_max_encountered];
    _stack[_sp++].i = _op1.i;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _sp -= 2;
    _op1.i = [java_lang_Math max___int_int:_stack[_sp + 0].i:_stack[_sp + 1].i];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    [org_apertium_lttoolbox_process_SetOfCharacters _PUT_max_encountered: _op1.i];
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _stack[_sp++].i = 255;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i >= _op2.i) goto label0;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_process_SetOfCharacters*) _op1.o)  _GET_set];
    _stack[_sp++].o = _op2.o;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _stack[_sp++].i = 1;
    _op1.i = _stack[--_sp].i;
    _op2.i = _stack[--_sp].i;
    _op3.o = _stack[--_sp].o;
    ((XMLVMArray*) _op3.o)->array.i[_op2.i] = _op1.i;
    goto label1;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_process_SetOfCharacters*) _op1.o)  _GET_otherChars];
    _stack[_sp++].o = _op2.o;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _sp -= 1;
    _op1.o = [java_lang_Character valueOf___char:_stack[_sp + 0].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp -= 2;
    _op1.i = [((java_util_HashSet*) _stack[_sp].o) add___java_lang_Object:_stack[_sp + 1].o];
    _stack[_sp++].i = _op1.i;
    _sp--;
    label1:;
    [_pool release];
    return;
}


- (void) clear__
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
    _op2.o = [((org_apertium_lttoolbox_process_SetOfCharacters*) _op1.o)  _GET_set];
    _stack[_sp++].o = _op2.o;
    _stack[_sp++].i = 0;
    _sp -= 2;
    [java_util_Arrays fill___boolean_ARRAYTYPE_boolean:_stack[_sp + 0].o:_stack[_sp + 1].i];
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_process_SetOfCharacters*) _op1.o)  _GET_otherChars];
    _stack[_sp++].o = _op2.o;
    _sp -= 1;
    [((java_util_HashSet*) _stack[_sp].o) clear__];
    label1:;
    [_pool release];
    return;
}


- (int) contains___char :(int)n1
{
    XMLVMElem _stack[2];
    XMLVMElem _locals[2];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    _locals[1].i = n1;
    label3:;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _stack[_sp++].i = 65535;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i != _op2.i) goto label0;
    _stack[_sp++].i = 0;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
    label0:;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _stack[_sp++].i = 255;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i >= _op2.i) goto label1;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_process_SetOfCharacters*) _op1.o)  _GET_set];
    _stack[_sp++].o = _op2.o;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    _stack[_sp++].i = ((XMLVMArray*) _op2.o)->array.i[_op1.i];
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
    label1:;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _op1.i = [org_apertium_lttoolbox_process_SetOfCharacters _GET_max_encountered];
    _stack[_sp++].i = _op1.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i <= _op2.i) goto label2;
    _stack[_sp++].i = 0;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
    label2:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_apertium_lttoolbox_process_SetOfCharacters*) _op1.o)  _GET_otherChars];
    _stack[_sp++].o = _op2.o;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _sp -= 1;
    _op1.o = [java_lang_Character valueOf___char:_stack[_sp + 0].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp -= 2;
    _op1.i = [((java_util_HashSet*) _stack[_sp].o) contains___java_lang_Object:_stack[_sp + 1].o];
    _stack[_sp++].i = _op1.i;
    label4:;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
}


+ (void) __clinit_org_apertium_lttoolbox_process_SetOfCharacters
{
    XMLVMElem _stack[1];
    XMLVMElem _locals[0];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _stack[_sp++].i = 0;
    _op1.i = _stack[--_sp].i;
    [org_apertium_lttoolbox_process_SetOfCharacters _PUT_max_encountered: _op1.i];
    [_pool release];
    return;
}



@end

