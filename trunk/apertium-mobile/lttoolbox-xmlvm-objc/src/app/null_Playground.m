#import "java_io_InputStream.h"
#import "java_io_Writer.h"
#import "java_lang_StringBuilder.h"
#import "java_lang_String.h"
#import "java_io_PrintStream.h"
#import "java_lang_System.h"
#import "org_apertium_lttoolbox_process_FSTProcessor.h"
#import "java_io_StringWriter.h"
#import "java_io_BufferedInputStream.h"
#import "Playground.h"
#import "StringReader2.h"
#import "java_io_Reader.h"
#import "java_io_FileInputStream.h"

// Automatically generated by xmlvm2obj. Do not edit!


#import "null_Playground.h"


@implementation _Playground;

+ (void) initialize
{
    if (strcmp(class_getName(self), "_Playground") == 0) {
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

- (void) __init__Playground__
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


+ (void) main___java_lang_String_ARRAYTYPE :(XMLVMArray*)n1
{
    XMLVMElem _stack[6];
    XMLVMElem _locals[4];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = n1;
    label0:;
    _stack[_sp++].o = @"c b[\012]";
    _op1.o = _stack[--_sp].o;
    _locals[1].o = _op1.o;
    label2:;
    _stack[_sp++].o = [[[org_apertium_lttoolbox_process_FSTProcessor alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _sp -= 1;
    [((org_apertium_lttoolbox_process_FSTProcessor*) _stack[_sp].o) __init_org_apertium_lttoolbox_process_FSTProcessor__];
    _op1.o = _stack[--_sp].o;
    _locals[2].o = _op1.o;
    label4:;
    _stack[_sp++].o = _locals[2].o;
    _stack[_sp++].o = [[[java_io_BufferedInputStream alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _stack[_sp++].o = [[[java_io_FileInputStream alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _stack[_sp++].o = @"testdata/cb.bin";
    _sp -= 2;
    [((java_io_FileInputStream*) _stack[_sp].o) __init_java_io_FileInputStream___java_lang_String:_stack[_sp + 1].o];
    _sp -= 2;
    [((java_io_BufferedInputStream*) _stack[_sp].o) __init_java_io_BufferedInputStream___java_io_InputStream:_stack[_sp + 1].o];
    _sp -= 2;
    [((org_apertium_lttoolbox_process_FSTProcessor*) _stack[_sp].o) load___java_io_InputStream:_stack[_sp + 1].o];
    _stack[_sp++].o = _locals[2].o;
    _sp -= 1;
    [((org_apertium_lttoolbox_process_FSTProcessor*) _stack[_sp].o) initAnalysis__];
    _stack[_sp++].o = [[[java_io_StringWriter alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _sp -= 1;
    [((java_io_StringWriter*) _stack[_sp].o) __init_java_io_StringWriter__];
    _op1.o = _stack[--_sp].o;
    _locals[3].o = _op1.o;
    label6:;
    _stack[_sp++].o = _locals[2].o;
    _stack[_sp++].o = [[[short alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _stack[_sp++].o = _locals[1].o;
    _sp -= 2;
    [((StringReader2*) _stack[_sp].o) __init_StringReader2___java_lang_String:_stack[_sp + 1].o];
    _stack[_sp++].o = _locals[3].o;
    _sp -= 3;
    [((org_apertium_lttoolbox_process_FSTProcessor*) _stack[_sp].o) analysis___java_io_Reader_java_io_Writer:_stack[_sp + 1].o:_stack[_sp + 2].o];
    _op1.o = [java_lang_System _GET_err];
    _stack[_sp++].o = _op1.o;
    _stack[_sp++].o = [[[java_lang_StringBuilder alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _sp -= 1;
    [((java_lang_StringBuilder*) _stack[_sp].o) __init_java_lang_StringBuilder__];
    _stack[_sp++].o = @"analysis output = ";
    _sp -= 2;
    _op1.o = [((java_lang_StringBuilder*) _stack[_sp].o) append___java_lang_String:_stack[_sp + 1].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _stack[_sp++].o = _locals[3].o;
    _sp -= 2;
    _op1.o = [((java_lang_StringBuilder*) _stack[_sp].o) append___java_lang_Object:_stack[_sp + 1].o];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp -= 1;
    _op1.o = [((java_lang_StringBuilder*) _stack[_sp].o) toString__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp -= 2;
    [((java_io_PrintStream*) _stack[_sp].o) println___java_lang_String:_stack[_sp + 1].o];
    label1:;
    [_pool release];
    return;
}



@end


  
      
int main(int argc, char* argv[])
{
    [_Playground main___java_lang_String_ARRAYTYPE: nil];
	return 0;						 
}
  
  
