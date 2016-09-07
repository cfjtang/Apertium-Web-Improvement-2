#import "org_me_apertium_MainActivity_1.h"
#import "org_me_apertium_MainActivity_2.h"
#import "java_lang_Object.h"
#import "android_widget_CheckBox.h"
#import "java_lang_String.h"
#import "android_view_View.h"
#import "android_widget_TextView.h"
#import "android_text_Editable.h"
#import "android_widget_CompoundButton_OnCheckedChangeListener.h"
#import "android_os_Bundle.h"
#import "android_widget_Button.h"
#import "java_lang_CharSequence.h"
#import "engine_utils_ApertiumWS.h"
#import "android_view_View_OnClickListener.h"
#import "android_widget_EditText.h"
#import "android_widget_Spinner.h"
#import "android_content_res_Resources.h"

// Automatically generated by xmlvm2obj. Do not edit!


#import "org_me_apertium_MainActivity.h"


@implementation org_me_apertium_MainActivity;

+ (void) initialize
{
    if (strcmp(class_getName(self), "org_me_apertium_MainActivity") == 0) {
    }
}

- (id) init
{
    if (self = [super init]) {
        org_me_apertium_MainActivity_language_codes = (id) [NSNull null];
    }
    return self;
}

- (void) dealloc
{
    [org_me_apertium_MainActivity_language_codes release];
    [super dealloc];
}

- (int) _GET_markUnk
{
    return org_me_apertium_MainActivity_markUnk;
}

- (void) _PUT_markUnk: (int) v
{
    org_me_apertium_MainActivity_markUnk = v;
}

- (XMLVMArray*) _GET_language_codes
{
    return [[org_me_apertium_MainActivity_language_codes retain] autorelease];
}

- (void) _PUT_language_codes: (XMLVMArray*) v
{
    [v retain];
    [org_me_apertium_MainActivity_language_codes release];
    org_me_apertium_MainActivity_language_codes = v;
}

- (void) __init_org_me_apertium_MainActivity__
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
    [((super)) __init_android_app_Activity__];
    label1:;
    [_pool release];
    return;
}


- (void) onCreate___android_os_Bundle :(android_os_Bundle*)n1
{
    XMLVMElem _stack[4];
    XMLVMElem _locals[4];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    _locals[1].o = n1;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[1].o;
    _sp -= 2;
    [((super)) onCreate___android_os_Bundle:_stack[_sp + 1].o];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 2130903040;
    _sp -= 2;
    [((org_me_apertium_MainActivity*) _stack[_sp].o) setContentView___int:_stack[_sp + 1].i];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 2131099651;
    _sp -= 2;
    _op1.o = [((org_me_apertium_MainActivity*) _stack[_sp].o) findViewById___int:_stack[_sp + 1].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[2].o = _op1.o;
    label4:;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    _op1.o = [((org_me_apertium_MainActivity*) _stack[_sp].o) getResources__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _stack[_sp++].i = 2131034113;
    _sp -= 2;
    _op1.o = [((android_content_res_Resources*) _stack[_sp].o) getStringArray___int:_stack[_sp + 1].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _op2.o = _stack[--_sp].o;
    [((org_me_apertium_MainActivity*) _op2.o) _PUT_language_codes: _op1.o];
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 2131099650;
    _sp -= 2;
    _op1.o = [((org_me_apertium_MainActivity*) _stack[_sp].o) findViewById___int:_stack[_sp + 1].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[3].o = _op1.o;
    label6:;
    _stack[_sp++].o = _locals[3].o;
    _stack[_sp++].o = [[[org_me_apertium_MainActivity_1 alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 2;
    [((org_me_apertium_MainActivity_1*) _stack[_sp].o) __init_org_me_apertium_MainActivity_1___org_me_apertium_MainActivity:_stack[_sp + 1].o];
    _sp -= 2;
    [((android_widget_CheckBox*) _stack[_sp].o) setOnCheckedChangeListener___android_widget_CompoundButton_OnCheckedChangeListener:_stack[_sp + 1].o];
    _stack[_sp++].o = _locals[2].o;
    _stack[_sp++].o = [[[org_me_apertium_MainActivity_2 alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 2;
    [((org_me_apertium_MainActivity_2*) _stack[_sp].o) __init_org_me_apertium_MainActivity_2___org_me_apertium_MainActivity:_stack[_sp + 1].o];
    _sp -= 2;
    [((android_widget_Button*) _stack[_sp].o) setOnClickListener___android_view_View_OnClickListener:_stack[_sp + 1].o];
    label1:;
    [_pool release];
    return;
}


- (java_lang_String*) getSourceText__
{
    XMLVMElem _stack[2];
    XMLVMElem _locals[3];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 2131099649;
    _sp -= 2;
    _op1.o = [((org_me_apertium_MainActivity*) _stack[_sp].o) findViewById___int:_stack[_sp + 1].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[1].o = _op1.o;
    label2:;
    _stack[_sp++].o = _locals[1].o;
    _sp -= 1;
    _op1.o = [((android_widget_EditText*) _stack[_sp].o) getText__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _sp -= 1;
    _op1.o = [((java_lang_Object*) _stack[_sp].o) toString__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[2].o = _op1.o;
    label4:;
    _stack[_sp++].o = _locals[2].o;
    label1:;
    _op1.o = _stack[--_sp].o;
    [_op1.o retain];
    [_pool release];
    return _op1.o;
}


- (java_lang_String*) getLanguageDir__
{
    XMLVMElem _stack[2];
    XMLVMElem _locals[4];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 2131099648;
    _sp -= 2;
    _op1.o = [((org_me_apertium_MainActivity*) _stack[_sp].o) findViewById___int:_stack[_sp + 1].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[1].o = _op1.o;
    label2:;
    _stack[_sp++].o = _locals[1].o;
    _sp -= 1;
    _op1.i = [((android_widget_Spinner*) _stack[_sp].o) getSelectedItemPosition__];
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    _locals[2].i = _op1.i;
    label4:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.o = [((org_me_apertium_MainActivity*) _op1.o)  _GET_language_codes];
    _stack[_sp++].o = _op2.o;
    _op1.i = _locals[2].i;
    _stack[_sp++].i = _op1.i;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    _stack[_sp++].o = [_op2.o objectAtIndex: _op1.i];
    _op1.o = _stack[--_sp].o;
    _locals[3].o = _op1.o;
    label6:;
    _stack[_sp++].o = _locals[3].o;
    label1:;
    _op1.o = _stack[--_sp].o;
    [_op1.o retain];
    [_pool release];
    return _op1.o;
}


- (java_lang_String*) translate_text___java_lang_String_java_lang_String_java_lang_String_boolean :(java_lang_String*)n1 :(java_lang_String*)n2 :(java_lang_String*)n3 :(int)n4
{
    XMLVMElem _stack[5];
    XMLVMElem _locals[7];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    _locals[1].o = n1;
    _locals[2].o = n2;
    _locals[3].o = n3;
    _locals[4].i = n4;
    label0:;
    _stack[_sp++].o = [NSNull null];
    _op1.o = _stack[--_sp].o;
    _locals[5].o = _op1.o;
    label10:;
    _stack[_sp++].o = [[[engine_utils_ApertiumWS alloc] init] autorelease];
    _op1 = _stack[_sp - 1];
    _stack[_sp++] = _op1;
    _stack[_sp++].o = _locals[1].o;
    _stack[_sp++].o = _locals[2].o;
    _stack[_sp++].o = _locals[3].o;
    _sp -= 4;
    [((engine_utils_ApertiumWS*) _stack[_sp].o) __init_engine_utils_ApertiumWS___java_lang_String_java_lang_String_java_lang_String:_stack[_sp + 1].o:_stack[_sp + 2].o:_stack[_sp + 3].o];
    _op1.o = _stack[--_sp].o;
    _locals[5].o = _op1.o;
    _stack[_sp++].o = _locals[5].o;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((org_me_apertium_MainActivity*) _op1.o)  _GET_markUnk];
    _stack[_sp++].i = _op2.i;
    _sp -= 2;
    [((engine_utils_ApertiumWS*) _stack[_sp].o) setMarkUnknown___boolean:_stack[_sp + 1].i];
    _stack[_sp++].o = _locals[5].o;
    _sp -= 1;
    _op1.o = [((engine_utils_ApertiumWS*) _stack[_sp].o) translate__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[6].o = _op1.o;
    label12:;
    _stack[_sp++].o = _locals[6].o;
    label1:;
    _op1.o = _stack[--_sp].o;
    [_op1.o retain];
    [_pool release];
    return _op1.o;
}


- (void) showTranslation___java_lang_String :(java_lang_String*)n1
{
    XMLVMElem _stack[2];
    XMLVMElem _locals[3];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = self;
    _locals[1].o = n1;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].i = 2131099652;
    _sp -= 2;
    _op1.o = [((org_me_apertium_MainActivity*) _stack[_sp].o) findViewById___int:_stack[_sp + 1].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    _op1.o = _stack[--_sp].o;
    _locals[2].o = _op1.o;
    label4:;
    _stack[_sp++].o = _locals[2].o;
    _stack[_sp++].o = _locals[1].o;
    _sp -= 2;
    [((android_widget_TextView*) _stack[_sp].o) setText___java_lang_CharSequence:_stack[_sp + 1].o];
    label1:;
    [_pool release];
    return;
}


+ (int) access$002___org_me_apertium_MainActivity_boolean :(org_me_apertium_MainActivity*)n1 :(int)n2
{
    XMLVMElem _stack[3];
    XMLVMElem _locals[2];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = n1;
    _locals[1].i = n2;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _op1.i = _locals[1].i;
    _stack[_sp++].i = _op1.i;
    _op1 = _stack[--_sp];
    _op2 = _stack[--_sp];
    _stack[_sp++] = _op1;
    _stack[_sp++] = _op2;
    _stack[_sp++] = _op1;
    _op1.i = _stack[--_sp].i;
    _op2.o = _stack[--_sp].o;
    [((org_me_apertium_MainActivity*) _op2.o) _PUT_markUnk: _op1.i];
    label1:;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
}


+ (java_lang_String*) access$100___org_me_apertium_MainActivity :(org_me_apertium_MainActivity*)n1
{
    XMLVMElem _stack[1];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = n1;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    _op1.o = [((org_me_apertium_MainActivity*) _stack[_sp].o) getSourceText__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    label1:;
    _op1.o = _stack[--_sp].o;
    [_op1.o retain];
    [_pool release];
    return _op1.o;
}


+ (java_lang_String*) access$200___org_me_apertium_MainActivity :(org_me_apertium_MainActivity*)n1
{
    XMLVMElem _stack[1];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = n1;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _sp -= 1;
    _op1.o = [((org_me_apertium_MainActivity*) _stack[_sp].o) getLanguageDir__];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    label1:;
    _op1.o = _stack[--_sp].o;
    [_op1.o retain];
    [_pool release];
    return _op1.o;
}


+ (int) access$000___org_me_apertium_MainActivity :(org_me_apertium_MainActivity*)n1
{
    XMLVMElem _stack[1];
    XMLVMElem _locals[1];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = n1;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _op1.o = _stack[--_sp].o;
    _op2.i = [((org_me_apertium_MainActivity*) _op1.o)  _GET_markUnk];
    _stack[_sp++].i = _op2.i;
    label1:;
    _op1.i = _stack[--_sp].i;
    [_pool release];
    return _op1.i;
}


+ (java_lang_String*) access$300___org_me_apertium_MainActivity_java_lang_String_java_lang_String_java_lang_String_boolean :(org_me_apertium_MainActivity*)n1 :(java_lang_String*)n2 :(java_lang_String*)n3 :(java_lang_String*)n4 :(int)n5
{
    XMLVMElem _stack[5];
    XMLVMElem _locals[5];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = n1;
    _locals[1].o = n2;
    _locals[2].o = n3;
    _locals[3].o = n4;
    _locals[4].i = n5;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[1].o;
    _stack[_sp++].o = _locals[2].o;
    _stack[_sp++].o = _locals[3].o;
    _op1.i = _locals[4].i;
    _stack[_sp++].i = _op1.i;
    _sp -= 5;
    _op1.o = [((org_me_apertium_MainActivity*) _stack[_sp].o) translate_text___java_lang_String_java_lang_String_java_lang_String_boolean:_stack[_sp + 1].o:_stack[_sp + 2].o:_stack[_sp + 3].o:_stack[_sp + 4].i];
    [_op1.o autorelease];
    _stack[_sp++].o = _op1.o;
    label1:;
    _op1.o = _stack[--_sp].o;
    [_op1.o retain];
    [_pool release];
    return _op1.o;
}


+ (void) access$400___org_me_apertium_MainActivity_java_lang_String :(org_me_apertium_MainActivity*)n1 :(java_lang_String*)n2
{
    XMLVMElem _stack[2];
    XMLVMElem _locals[2];
    int _sp = 0;
    XMLVMElem _op1, _op2, _op3;
    NSAutoreleasePool* _pool = [[NSAutoreleasePool alloc] init];
    _locals[0].o = n1;
    _locals[1].o = n2;
    label0:;
    _stack[_sp++].o = _locals[0].o;
    _stack[_sp++].o = _locals[1].o;
    _sp -= 2;
    [((org_me_apertium_MainActivity*) _stack[_sp].o) showTranslation___java_lang_String:_stack[_sp + 1].o];
    label1:;
    [_pool release];
    return;
}



@end
