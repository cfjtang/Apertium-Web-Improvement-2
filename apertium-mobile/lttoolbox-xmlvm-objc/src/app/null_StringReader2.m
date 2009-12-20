#import "java_lang_IllegalArgumentException.h"
#import "java_lang_IndexOutOfBoundsException.h"
#import "java_lang_Object.h"
#import "java_lang_String.h"
#import "java_io_IOException.h"
#import "StringReader2.h"
#import "java_lang_Math.h"

// Automatically generated by xmlvm2obj. Do not edit!


#import "null_StringReader2.h"


@implementation _StringReader2;

+ (void) initialize
{
    if (strcmp(class_getName(self), "_StringReader2") == 0) {
    }
}

- (id) init
{
    if (self = [super init]) {
        _StringReader2_str = (id) [NSNull null];
    }
    return self;
}

- (void) dealloc
{
    [_StringReader2_str release];
    [super dealloc];
}

- (java_lang_String*) _GET_str
{
    return [[_StringReader2_str retain] autorelease];
}

- (void) _PUT_str: (java_lang_String*) v
{
    [v retain];
    [_StringReader2_str release];
    _StringReader2_str = v;
}

- (int) _GET_length
{
    return _StringReader2_length;
}

- (void) _PUT_length: (int) v
{
    _StringReader2_length = v;
}

- (int) _GET_next
{
    return _StringReader2_next;
}

- (void) _PUT_next: (int) v
{
    _StringReader2_next = v;
}

- (int) _GET_mark
{
    return _StringReader2_mark;
}

- (void) _PUT_mark: (int) v
{
    _StringReader2_mark = v;
}

- (void) __init__StringReader2___java_lang_String :(java_lang_String*)n1
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
    _sp -= 1;
    [((super)) __init_java_io_Reader__];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 0;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    [((StringReader2*) _op2.o) _PUT_next: _op1.i];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 0;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    [((StringReader2*) _op2.o) _PUT_mark: _op1.i];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[1].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = _stack[--_sp].o;
    [((StringReader2*) _op2.o) _PUT_str: _op1.o];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[1].o;
    _sp -= 1;
    _op1.i = [((java_lang_String*) _stack[_sp].o) length__];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    [((StringReader2*) _op2.o) _PUT_length: _op1.i];
    label1:;
    [_pool release];
    return;
}


- (void) ensureOpen__
{
    XMLVMElem _stack[3];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    label1:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((StringReader2*) _op1.o)  _GET_str];
    _stack[_sp++].o = _op2.o;
    _op1.o = _stack[--_sp].o;
    if (_op1.o != [NSNull null]) goto label0;
    _stack[_sp++].o = [[[java_io_IOException alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _stack[_sp++].o = @"Stream closed";
    _sp -= 2;
    [((java_io_IOException*) _stack[_sp].o) __init_java_io_IOException___java_lang_String:_stack[_sp + 1].o];
    _op1.o = _stack[--_sp].o;
    @throw _op1.o;
    label0:;
    [_pool release];
    return;
}


- (int) read__
{
    XMLVMElem _stack[5];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    label1:;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    [((StringReader2*) _stack[_sp].o) ensureOpen__];
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_length];
    _stack[_sp++].i = _op2.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i < _op2.i) goto label0;
    _stack[_sp++].i = -1;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((StringReader2*) _op1.o)  _GET_str];
    _stack[_sp++].o = _op2.o;
    _stack[_sp++].o = _locals[0].o;
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _op1 = _stack[--_sp];
    _op2 = _stack[--_sp];
    _stack[_sp++] = _op1;
    _stack[_sp++] = _op2;
    _stack[_sp++] = _op1;
    _stack[_sp++].i = 1;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    _stack[_sp++].i = _op1.i + _op2.i;    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    [((StringReader2*) _op2.o) _PUT_next: _op1.i];
    _sp -= 2;
    _op1.i = [((java_lang_String*) _stack[_sp].o) charAt___int:_stack[_sp + 1].i];
    _stack[_sp++].i = _op1.i;
    label2:;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
}


- (int) read___char_ARRAYTYPE_int_int :(XMLVMArray*)n1 :(int)n2 :(int)n3
{
    XMLVMElem _stack[5];
    XMLVMElem _locals[5];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    _locals[1].o = n1;
    _locals[2].i = n2;
    _locals[3].i = n3;
    label7:;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    [((StringReader2*) _stack[_sp].o) ensureOpen__];
    _op1.i = _locals[2].i;
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i < 0) goto label0;
    _op1.i = _locals[2].i;
    _stack[_sp++].i = _op1.i;
    _stack[_sp++].o = _locals[1].o;
    _op1.i = [_stack[--_sp].o count];
    _stack[_sp++].i = _op1.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i > _op2.i) goto label0;
    _op1.i = _locals[3].i;
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i < 0) goto label0;
    _op1.i = _locals[2].i;
    _stack[_sp++].i = _op1.i;
    _op1.i = _locals[3].i;
    _stack[_sp++].i = _op1.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    _stack[_sp++].i = _op1.i + _op2.i;    _stack[_sp++].o = _locals[1].o;
    _op1.i = [_stack[--_sp].o count];
    _stack[_sp++].i = _op1.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i > _op2.i) goto label0;
    _op1.i = _locals[2].i;
    _stack[_sp++].i = _op1.i;
    _op1.i = _locals[3].i;
    _stack[_sp++].i = _op1.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    _stack[_sp++].i = _op1.i + _op2.i;    _op1.i = _stack[--_sp].i;
    if (_op1.i >= 0) goto label4;
    label0:;
    _stack[_sp++].o = [[[java_lang_IndexOutOfBoundsException alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _sp -= 1;
    [((java_lang_IndexOutOfBoundsException*) _stack[_sp].o) __init_java_lang_IndexOutOfBoundsException__];
    _op1.o = _stack[--_sp].o;
    @throw _op1.o;
    label4:;
    _op1.i = _locals[3].i;
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i != 0) goto label5;
    _stack[_sp++].i = 0;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
    label5:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_length];
    _stack[_sp++].i = _op2.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i < _op2.i) goto label6;
    _stack[_sp++].i = -1;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
    label6:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_length];
    _stack[_sp++].i = _op2.i;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    _stack[_sp++].i = _op1.i - _op2.i;    _op1.i = _locals[3].i;
    _stack[_sp++].i = _op1.i;
    _sp -= 2;
    _op1.i = [java_lang_Math min___int_int:_stack[_sp + 0].i:_stack[_sp + 1].i];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    _locals[4].i = _op1.i;
    label15:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((StringReader2*) _op1.o)  _GET_str];
    _stack[_sp++].o = _op2.o;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _op1.i = _locals[4].i;
    _stack[_sp++].i = _op1.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    _stack[_sp++].i = _op1.i + _op2.i;    _stack[_sp++].o = _locals[1].o;
    _op1.i = _locals[2].i;
    _stack[_sp++].i = _op1.i;
    _sp -= 5;
    [((java_lang_String*) _stack[_sp].o) getChars___int_int_char_ARRAYTYPE_int:_stack[_sp + 1].i:_stack[_sp + 2].i:_stack[_sp + 3].o:_stack[_sp + 4].i];
    _stack[_sp++].o = _locals[0].o;
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _op1.i = _locals[4].i;
    _stack[_sp++].i = _op1.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    _stack[_sp++].i = _op1.i + _op2.i;    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    [((StringReader2*) _op2.o) _PUT_next: _op1.i];
    _op1.i = _locals[4].i;
    _stack[_sp++].i = _op1.i;
    label8:;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
}


- (long) skip___long :(long)n1
{
    XMLVMElem _stack[5];
    XMLVMElem _locals[5];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    _locals[1].l = n1;
    label1:;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    [((StringReader2*) _stack[_sp].o) ensureOpen__];
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_length];
    _stack[_sp++].i = _op2.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i < _op2.i) goto label0;
    _stack[_sp++].l = 0;
    _op1.l = _stack[--_sp].l;
    [_pool release];
    return _op1.l;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_length];
    _stack[_sp++].i = _op2.i;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    _stack[_sp++].i = _op1.i - _op2.i;    _op1.i = _stack[--_sp].i;
    _stack[_sp++].l = (long) _op1.i;    _op1.l = _locals[1].l;
  _stack[_sp++].l = _op1.l;
    _sp -= 2;
    _op1.l = [java_lang_Math min___long_long:_stack[_sp + 0].l:_stack[_sp + 1].l];
    _stack[_sp++].l = _op1.l;
    _op1.l = _stack[--_sp].l;
    _locals[3].l = _op1.l;
    label5:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _op1.i = _stack[--_sp].i;
    _stack[_sp++].i = -_op1.i;
    _op1.i = _stack[--_sp].i;
    _stack[_sp++].l = (long) _op1.i;    _op1.l = _locals[3].l;
  _stack[_sp++].l = _op1.l;
    _sp -= 2;
    _op1.l = [java_lang_Math max___long_long:_stack[_sp + 0].l:_stack[_sp + 1].l];
    _stack[_sp++].l = _op1.l;
    _op1.l = _stack[--_sp].l;
    _locals[3].l = _op1.l;
    _stack[_sp++].o = _locals[0].o;
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _op1.i = _stack[--_sp].i;
    _stack[_sp++].l = (long) _op1.i;    _op1.l = _locals[3].l;
  _stack[_sp++].l = _op1.l;
    _op2.l = _stack[--_sp].l;
    _op1.l = _stack[--_sp].l;
    _stack[_sp++].l = _op1.l + _op2.l;    _op1.l = _stack[--_sp].l;
  _stack[_sp++].i = (int) _op1.l;    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    [((StringReader2*) _op2.o) _PUT_next: _op1.i];
    _op1.l = _locals[3].l;
  _stack[_sp++].l = _op1.l;
    label2:;
    _op1.l = _stack[--_sp].l;
    [_pool release];
    return _op1.l;
}


- (int) ready__
{
    XMLVMElem _stack[2];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    label1:;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    [((StringReader2*) _stack[_sp].o) ensureOpen__];
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_length];
    _stack[_sp++].i = _op2.i;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i < _op2.i) goto label0;
    _stack[_sp++].i = 0;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
    label0:;
    _stack[_sp++].i = 1;
    label2:;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
}


- (int) markSupported__
{
    XMLVMElem _stack[1];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    label0:;
    _stack[_sp++].i = 1;
    label1:;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
}


- (void) mark___int :(int)n1
{
    XMLVMElem _stack[3];
    XMLVMElem _locals[2];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    _locals[1].i = n1;
    label1:;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i >= 0) goto label0;
    _stack[_sp++].o = [[[java_lang_IllegalArgumentException alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _stack[_sp++].o = @"Read-ahead limit < 0";
    _sp -= 2;
    [((java_lang_IllegalArgumentException*) _stack[_sp].o) __init_java_lang_IllegalArgumentException___java_lang_String:_stack[_sp + 1].o];
    _op1.o = _stack[--_sp].o;
    @throw _op1.o;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    [((StringReader2*) _stack[_sp].o) ensureOpen__];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_next];
    _stack[_sp++].i = _op2.i;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    [((StringReader2*) _op2.o) _PUT_mark: _op1.i];
    label2:;
    [_pool release];
    return;
}


- (void) reset__
{
    XMLVMElem _stack[2];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    [((StringReader2*) _stack[_sp].o) ensureOpen__];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((StringReader2*) _op1.o)  _GET_mark];
    _stack[_sp++].i = _op2.i;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    [((StringReader2*) _op2.o) _PUT_next: _op1.i];
    label1:;
    [_pool release];
    return;
}


- (void) close__
{
    XMLVMElem _stack[2];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = [NSNull null];
    _op1.o = _stack[--_sp].o;
    _op2.o = _stack[--_sp].o;
    [((StringReader2*) _op2.o) _PUT_str: _op1.o];
    label1:;
    [_pool release];
    return;
}



@end

