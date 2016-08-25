#import "java_io_BufferedOutputStream.h"
#import "java_io_FileOutputStream.h"
#import "java_lang_String.h"
#import "java_lang_StringBuilder.h"
#import "java_io_PrintStream.h"
#import "java_lang_System.h"
#import "org_apertium_lttoolbox_compile_Compile.h"
#import "java_io_OutputStream.h"

// Automatically generated by xmlvm2obj. Do not edit!


#import "org_apertium_lttoolbox_LTComp.h"

java_lang_String* _STATIC_org_apertium_lttoolbox_LTComp_PACKAGE_VERSION = @"0.1j";

@implementation org_apertium_lttoolbox_LTComp;

+ (void) initialize
{
    if (strcmp(class_getName(self), "org_apertium_lttoolbox_LTComp") == 0) {
        _STATIC_org_apertium_lttoolbox_LTComp_PACKAGE_VERSION = (id) [NSNull null];
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

+ (java_lang_String*) _GET_PACKAGE_VERSION
{
    return [[_STATIC_org_apertium_lttoolbox_LTComp_PACKAGE_VERSION retain] autorelease];
}

+ (void) _PUT_PACKAGE_VERSION: (java_lang_String*) v
{
    [v retain];
    [_STATIC_org_apertium_lttoolbox_LTComp_PACKAGE_VERSION release];
    _STATIC_org_apertium_lttoolbox_LTComp_PACKAGE_VERSION = v;
}

- (void) __init_org_apertium_lttoolbox_LTComp__
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
    [((super)) __init_java_lang_Object__];
    label1:;
    [_pool release];
    return;
}


+ (void) endProgram___java_lang_String :(java_lang_String*)n1
{
    XMLVMElem _stack[3];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = n1;
    label1:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    if (_op1.o == [NSNull null]) goto label0;
    _op1.o = [java_lang_System _GET_out];
    _stack[_sp++].o = _op1.o;
    _stack[_sp++].o = [[[java_lang_StringBuilder alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _sp -= 1;
    [((java_lang_StringBuilder*) _stack[_sp].o) __init_java_lang_StringBuilder__];
    _stack[_sp++].o = @" v0.1j: build a letter transducer from a dictionary\012USAGE: ";
    _sp -= 2;
    _op1.o = [((java_lang_StringBuilder*) _stack[_sp].o) append___java_lang_String:_stack[_sp + 1].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 2;
    _op1.o = [((java_lang_StringBuilder*) _stack[_sp].o) append___java_lang_String:_stack[_sp + 1].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _stack[_sp++].o = @" lr \174 rl dictionary_file output_file [acx_file]\012";
    _sp -= 2;
    _op1.o = [((java_lang_StringBuilder*) _stack[_sp].o) append___java_lang_String:_stack[_sp + 1].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _stack[_sp++].o = @"Modes:\012";
    _sp -= 2;
    _op1.o = [((java_lang_StringBuilder*) _stack[_sp].o) append___java_lang_String:_stack[_sp + 1].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _stack[_sp++].o = @"  lr:     left-to-right compilation\012";
    _sp -= 2;
    _op1.o = [((java_lang_StringBuilder*) _stack[_sp].o) append___java_lang_String:_stack[_sp + 1].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _stack[_sp++].o = @"  rl:     right-to-left compilation\012";
    _sp -= 2;
    _op1.o = [((java_lang_StringBuilder*) _stack[_sp].o) append___java_lang_String:_stack[_sp + 1].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp -= 1;
    _op1.o = [((java_lang_StringBuilder*) _stack[_sp].o) toString__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp -= 2;
    [((java_io_PrintStream*) _stack[_sp].o) println___java_lang_String:_stack[_sp + 1].o];
    label0:;
    [_pool release];
    return;
}


+ (void) main___java_lang_String_ARRAYTYPE :(XMLVMArray*)n1
{
    XMLVMElem _stack[6];
    XMLVMElem _locals[5];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = n1;
    label7:;
    _stack[_sp++].o = _locals[0].o;
    _op1.i = [_stack[--_sp].o count];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    _locals[1].i = _op1.i;
    label9:;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _stack[_sp++].i = 3;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i == _op2.i) goto label0;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _stack[_sp++].i = 4;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i == _op2.i) goto label0;
    _stack[_sp++].o = @"LTComp";
    _sp -= 1;
    [org_apertium_lttoolbox_LTComp endProgram___java_lang_String:_stack[_sp + 0].o];
    [_pool release];
    return;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 0;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    _stack[_sp++].o = [_op2.o objectAtIndex: _op1.i];
    _op1.o = _stack[--_sp].o;
    _locals[2].o = _op1.o;
    label11:;
    _stack[_sp++].o = [[[org_apertium_lttoolbox_compile_Compile alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _sp -= 1;
    [((org_apertium_lttoolbox_compile_Compile*) _stack[_sp].o) __init_org_apertium_lttoolbox_compile_Compile__];
    _op1.o = _stack[--_sp].o;
    _locals[3].o = _op1.o;
    label13:;
    _stack[_sp++].o = _locals[2].o;
    _stack[_sp++].o = @"lr";
    _sp -= 2;
    _op1.i = [((java_lang_String*) _stack[_sp].o) equals___java_lang_Object:_stack[_sp + 1].o];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i == 0) goto label2;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _stack[_sp++].i = 4;
    _op2.i = _stack[--_sp].i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i != _op2.i) goto label3;
    _stack[_sp++].o = _locals[3].o;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 3;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    _stack[_sp++].o = [_op2.o objectAtIndex: _op1.i];
    _op1.o = [org_apertium_lttoolbox_compile_Compile _GET_COMPILER_RESTRICTION_LR_VAL];
    _stack[_sp++].o = _op1.o;
    _sp -= 3;
    [((org_apertium_lttoolbox_compile_Compile*) _stack[_sp].o) parseACX___java_lang_String_java_lang_String:_stack[_sp + 1].o:_stack[_sp + 2].o];
    label3:;
    _stack[_sp++].o = _locals[3].o;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 1;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    _stack[_sp++].o = [_op2.o objectAtIndex: _op1.i];
    _op1.o = [org_apertium_lttoolbox_compile_Compile _GET_COMPILER_RESTRICTION_LR_VAL];
    _stack[_sp++].o = _op1.o;
    _sp -= 3;
    [((org_apertium_lttoolbox_compile_Compile*) _stack[_sp].o) parse___java_lang_String_java_lang_String:_stack[_sp + 1].o:_stack[_sp + 2].o];
    goto label4;
    label2:;
    _stack[_sp++].o = _locals[2].o;
    _stack[_sp++].o = @"rl";
    _sp -= 2;
    _op1.i = [((java_lang_String*) _stack[_sp].o) equals___java_lang_Object:_stack[_sp + 1].o];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    if (_op1.i == 0) goto label5;
    _stack[_sp++].o = _locals[3].o;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 1;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    _stack[_sp++].o = [_op2.o objectAtIndex: _op1.i];
    _op1.o = [org_apertium_lttoolbox_compile_Compile _GET_COMPILER_RESTRICTION_RL_VAL];
    _stack[_sp++].o = _op1.o;
    _sp -= 3;
    [((org_apertium_lttoolbox_compile_Compile*) _stack[_sp].o) parse___java_lang_String_java_lang_String:_stack[_sp + 1].o:_stack[_sp + 2].o];
    goto label4;
    label5:;
    _stack[_sp++].o = @"LTComp";
    _sp -= 1;
    [org_apertium_lttoolbox_LTComp endProgram___java_lang_String:_stack[_sp + 0].o];
    [_pool release];
    return;
    label4:;
    _stack[_sp++].o = [[[java_io_BufferedOutputStream alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _stack[_sp++].o = [[[java_io_FileOutputStream alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 2;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    _stack[_sp++].o = [_op2.o objectAtIndex: _op1.i];
    _sp -= 2;
    [((java_io_FileOutputStream*) _stack[_sp].o) __init_java_io_FileOutputStream___java_lang_String:_stack[_sp + 1].o];
    _sp -= 2;
    [((java_io_BufferedOutputStream*) _stack[_sp].o) __init_java_io_BufferedOutputStream___java_io_OutputStream:_stack[_sp + 1].o];
    _op1.o = _stack[--_sp].o;
    _locals[4].o = _op1.o;
    label15:;
    _stack[_sp++].o = _locals[3].o;
    _stack[_sp++].o = _locals[4].o;
    _sp -= 2;
    [((org_apertium_lttoolbox_compile_Compile*) _stack[_sp].o) write___java_io_OutputStream:_stack[_sp + 1].o];
    _stack[_sp++].o = _locals[4].o;
    _sp -= 1;
    [((java_io_OutputStream*) _stack[_sp].o) close__];
    label8:;
    [_pool release];
    return;
}



@end


  
      
int main(int argc, char* argv[])
{
    [org_apertium_lttoolbox_LTComp main___java_lang_String_ARRAYTYPE: nil];
	return 0;						 
}
  
  