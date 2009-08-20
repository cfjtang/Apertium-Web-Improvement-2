// This file is generated by omniidl (C++ backend)- omniORB_4_1. Do not edit.
#ifndef __apertium_hh__
#define __apertium_hh__

#ifndef __CORBA_H_EXTERNAL_GUARD__
#include <omniORB4/CORBA.h>
#endif

#ifndef  USE_stub_in_nt_dll
# define USE_stub_in_nt_dll_NOT_DEFINED_apertium
#endif
#ifndef  USE_core_stub_in_nt_dll
# define USE_core_stub_in_nt_dll_NOT_DEFINED_apertium
#endif
#ifndef  USE_dyn_stub_in_nt_dll
# define USE_dyn_stub_in_nt_dll_NOT_DEFINED_apertium
#endif






#ifdef USE_stub_in_nt_dll
# ifndef USE_core_stub_in_nt_dll
#  define USE_core_stub_in_nt_dll
# endif
# ifndef USE_dyn_stub_in_nt_dll
#  define USE_dyn_stub_in_nt_dll
# endif
#endif

#ifdef _core_attr
# error "A local CPP macro _core_attr has already been defined."
#else
# ifdef  USE_core_stub_in_nt_dll
#  define _core_attr _OMNIORB_NTDLL_IMPORT
# else
#  define _core_attr
# endif
#endif

#ifdef _dyn_attr
# error "A local CPP macro _dyn_attr has already been defined."
#else
# ifdef  USE_dyn_stub_in_nt_dll
#  define _dyn_attr _OMNIORB_NTDLL_IMPORT
# else
#  define _dyn_attr
# endif
#endif





_CORBA_MODULE ApertiumService

_CORBA_MODULE_BEG

  struct LanguagePairType {
    typedef _CORBA_ConstrType_Variable_Var<LanguagePairType> _var_type;

    
    ::CORBA::String_member srcLang;

    ::CORBA::String_member destLang;

  

    void operator>>= (cdrStream &) const;
    void operator<<= (cdrStream &);
  };

  typedef LanguagePairType::_var_type LanguagePairType_var;

  typedef _CORBA_ConstrType_Variable_OUT_arg< LanguagePairType,LanguagePairType_var > LanguagePairType_out;

  class LanguagePairsType_var;

  class LanguagePairsType : public _CORBA_Unbounded_Sequence< LanguagePairType >  {
  public:
    typedef LanguagePairsType_var _var_type;
    inline LanguagePairsType() {}
    inline LanguagePairsType(const LanguagePairsType& _s)
      : _CORBA_Unbounded_Sequence< LanguagePairType > (_s) {}

    inline LanguagePairsType(_CORBA_ULong _max)
      : _CORBA_Unbounded_Sequence< LanguagePairType > (_max) {}
    inline LanguagePairsType(_CORBA_ULong _max, _CORBA_ULong _len, LanguagePairType* _val, _CORBA_Boolean _rel=0)
      : _CORBA_Unbounded_Sequence< LanguagePairType > (_max, _len, _val, _rel) {}

  

    inline LanguagePairsType& operator = (const LanguagePairsType& _s) {
      _CORBA_Unbounded_Sequence< LanguagePairType > ::operator=(_s);
      return *this;
    }
  };

  class LanguagePairsType_out;

  class LanguagePairsType_var {
  public:
    inline LanguagePairsType_var() : _pd_seq(0) {}
    inline LanguagePairsType_var(LanguagePairsType* _s) : _pd_seq(_s) {}
    inline LanguagePairsType_var(const LanguagePairsType_var& _s) {
      if( _s._pd_seq )  _pd_seq = new LanguagePairsType(*_s._pd_seq);
      else              _pd_seq = 0;
    }
    inline ~LanguagePairsType_var() { if( _pd_seq )  delete _pd_seq; }
      
    inline LanguagePairsType_var& operator = (LanguagePairsType* _s) {
      if( _pd_seq )  delete _pd_seq;
      _pd_seq = _s;
      return *this;
    }
    inline LanguagePairsType_var& operator = (const LanguagePairsType_var& _s) {
      if( _s._pd_seq ) {
        if( !_pd_seq )  _pd_seq = new LanguagePairsType;
        *_pd_seq = *_s._pd_seq;
      } else if( _pd_seq ) {
        delete _pd_seq;
        _pd_seq = 0;
      }
      return *this;
    }
    inline LanguagePairType& operator [] (_CORBA_ULong _s) {
      return (*_pd_seq)[_s];
    }

  

    inline LanguagePairsType* operator -> () { return _pd_seq; }
    inline const LanguagePairsType* operator -> () const { return _pd_seq; }
#if defined(__GNUG__)
    inline operator LanguagePairsType& () const { return *_pd_seq; }
#else
    inline operator const LanguagePairsType& () const { return *_pd_seq; }
    inline operator LanguagePairsType& () { return *_pd_seq; }
#endif
      
    inline const LanguagePairsType& in() const { return *_pd_seq; }
    inline LanguagePairsType&       inout()    { return *_pd_seq; }
    inline LanguagePairsType*&      out() {
      if( _pd_seq ) { delete _pd_seq; _pd_seq = 0; }
      return _pd_seq;
    }
    inline LanguagePairsType* _retn() { LanguagePairsType* tmp = _pd_seq; _pd_seq = 0; return tmp; }
      
    friend class LanguagePairsType_out;
    
  private:
    LanguagePairsType* _pd_seq;
  };

  class LanguagePairsType_out {
  public:
    inline LanguagePairsType_out(LanguagePairsType*& _s) : _data(_s) { _data = 0; }
    inline LanguagePairsType_out(LanguagePairsType_var& _s)
      : _data(_s._pd_seq) { _s = (LanguagePairsType*) 0; }
    inline LanguagePairsType_out(const LanguagePairsType_out& _s) : _data(_s._data) {}
    inline LanguagePairsType_out& operator = (const LanguagePairsType_out& _s) {
      _data = _s._data;
      return *this;
    }
    inline LanguagePairsType_out& operator = (LanguagePairsType* _s) {
      _data = _s;
      return *this;
    }
    inline operator LanguagePairsType*&()  { return _data; }
    inline LanguagePairsType*& ptr()       { return _data; }
    inline LanguagePairsType* operator->() { return _data; }

    inline LanguagePairType& operator [] (_CORBA_ULong _i) {
      return (*_data)[_i];
    }

  

    LanguagePairsType*& _data;

  private:
    LanguagePairsType_out();
    LanguagePairsType_out& operator=(const LanguagePairsType_var&);
  };

  struct TranslationWithDetectionType {
    typedef _CORBA_ConstrType_Variable_Var<TranslationWithDetectionType> _var_type;

    
    ::CORBA::String_member detectedSourceLanguage;

    ::CORBA::String_member translation;

  

    void operator>>= (cdrStream &) const;
    void operator<<= (cdrStream &);
  };

  typedef TranslationWithDetectionType::_var_type TranslationWithDetectionType_var;

  typedef _CORBA_ConstrType_Variable_OUT_arg< TranslationWithDetectionType,TranslationWithDetectionType_var > TranslationWithDetectionType_out;

  class TranslationType {
  public:

    typedef _CORBA_ConstrType_Variable_Var<TranslationType> _var_type;

    

    TranslationType(): _pd__initialised(0) {
      
    }
    
    TranslationType(const TranslationType& _value) {
      _pd__initialised = _value._pd__initialised;
      switch(_value._pd__d) {
        case 1: translationWithDetection(_value._pd_translationWithDetection); break;

        case 0: translation(_value._pd_translation); break;

#ifndef HAS_Cplusplus_Bool
          default: break;
#endif

      
      }
      _pd__d = _value._pd__d;
  
    }

    ~TranslationType() {}

    TranslationType& operator=(const TranslationType& _value) {
      _pd__initialised = _value._pd__initialised;
      switch(_value._pd__d) {
        case 1: translationWithDetection(_value._pd_translationWithDetection); break;

        case 0: translation(_value._pd_translation); break;

#ifndef HAS_Cplusplus_Bool
          default: break;
#endif

      
      }
      _pd__d = _value._pd__d;
  
      return *this;
    }

    ::CORBA::Boolean _d() const { return _pd__d;}
    void _d(::CORBA::Boolean _value){
      // illegal to set discriminator before making a member active
      if (!_pd__initialised)
        OMNIORB_THROW(BAD_PARAM,_OMNI_NS(BAD_PARAM_InvalidUnionDiscValue),::CORBA::COMPLETED_NO);

      if (_value == _pd__d) return; // no change

      switch (_pd__d){
        case 1: goto fail;
        case 0: goto fail;
        default: goto fail;

      };
      

      fail:
      OMNIORB_THROW(BAD_PARAM,_OMNI_NS(BAD_PARAM_InvalidUnionDiscValue),::CORBA::COMPLETED_NO);


  
    }

    

    const TranslationWithDetectionType &translationWithDetection () const { return _pd_translationWithDetection; }
    TranslationWithDetectionType &translationWithDetection () { return _pd_translationWithDetection; }
    void translationWithDetection (const TranslationWithDetectionType& _value) {
      _pd__initialised = 1;
      _pd__d = 1;
      _pd__default = 0;
      _pd_translationWithDetection = _value;
    }

    const char * translation () const { return (const char*) _pd_translation; }
    void translation(char* _value) {
      _pd__initialised = 1;
      _pd__d = 0;
      _pd__default = 0;
      _pd_translation = _value;
    }
    void translation(const char*  _value) {
      _pd__initialised = 1;
      _pd__d = 0;
      _pd__default = 0;
      _pd_translation = _value;
    }
    void translation(const ::CORBA::String_var& _value) {
      _pd__initialised = 1;
      _pd__d = 0;
      _pd__default = 0;
      _pd_translation = _value;
    }
    void translation(const ::CORBA::String_member& _value) {
      _pd__initialised = 1;
      _pd__d = 0;
      _pd__default = 0;
      _pd_translation = _value;
    }

  
    
    void operator>>= (cdrStream&) const;
    void operator<<= (cdrStream&);

  private:
    ::CORBA::Boolean _pd__d;
    _CORBA_Boolean _pd__default;
    _CORBA_Boolean _pd__initialised;

    
    TranslationWithDetectionType _pd_translationWithDetection;

    ::CORBA::String_member _pd_translation;

  
  };

  typedef TranslationType::_var_type TranslationType_var;

  typedef _CORBA_ConstrType_Variable_OUT_arg< TranslationType,TranslationType_var > TranslationType_out;

#ifndef __ApertiumService_mApertium__
#define __ApertiumService_mApertium__

  class Apertium;
  class _objref_Apertium;
  class _impl_Apertium;
  class _sk_Apertium;
  typedef _objref_Apertium* Apertium_ptr;
  typedef Apertium_ptr ApertiumRef;

  class Apertium_Helper {
  public:
    typedef Apertium_ptr _ptr_type;

    static _ptr_type _nil();
    static _CORBA_Boolean is_nil(_ptr_type);
    static void release(_ptr_type);
    static void duplicate(_ptr_type);
    static void marshalObjRef(_ptr_type, cdrStream&);
    static _ptr_type unmarshalObjRef(cdrStream&);
  };

  typedef _CORBA_ObjRef_Var<_objref_Apertium, Apertium_Helper> Apertium_var;
  typedef _CORBA_ObjRef_OUT_arg<_objref_Apertium,Apertium_Helper > Apertium_out;

#endif

  // interface Apertium
  class Apertium {
  public:
    // Declarations for this interface type.
    typedef Apertium_ptr _ptr_type;
    typedef Apertium_var _var_type;

    static _ptr_type _duplicate(_ptr_type);
    static _ptr_type _narrow(::CORBA::Object_ptr);
    static _ptr_type _unchecked_narrow(::CORBA::Object_ptr);
    
    static _ptr_type _nil();

    static inline void _marshalObjRef(_ptr_type, cdrStream&);

    static inline _ptr_type _unmarshalObjRef(cdrStream& s) {
      omniObjRef* o = omniObjRef::_unMarshal(_PD_repoId,s);
      if (o)
        return (_ptr_type) o->_ptrToObjRef(_PD_repoId);
      else
        return _nil();
    }

    static _core_attr const char* _PD_repoId;

    // Other IDL defined within this scope.
    class ApertiumRuntimeException : public ::CORBA::UserException {
    public:
      
      ::CORBA::String_member reason;

    

      inline ApertiumRuntimeException() {
        pd_insertToAnyFn    = insertToAnyFn;
        pd_insertToAnyFnNCP = insertToAnyFnNCP;
      }
      ApertiumRuntimeException(const ApertiumRuntimeException&);
      ApertiumRuntimeException(const char* i_reason);
      ApertiumRuntimeException& operator=(const ApertiumRuntimeException&);
      virtual ~ApertiumRuntimeException();
      virtual void _raise() const;
      static ApertiumRuntimeException* _downcast(::CORBA::Exception*);
      static const ApertiumRuntimeException* _downcast(const ::CORBA::Exception*);
      static inline ApertiumRuntimeException* _narrow(::CORBA::Exception* _e) {
        return _downcast(_e);
      }
      
      void operator>>=(cdrStream&) const ;
      void operator<<=(cdrStream&) ;

      static _core_attr insertExceptionToAny    insertToAnyFn;
      static _core_attr insertExceptionToAnyNCP insertToAnyFnNCP;

      virtual ::CORBA::Exception* _NP_duplicate() const;

      static _core_attr const char* _PD_repoId;
      static _core_attr const char* _PD_typeId;

    private:
      virtual const char* _NP_typeId() const;
      virtual const char* _NP_repoId(int*) const;
      virtual void _NP_marshal(cdrStream&) const;
    };

  
  };

  class _objref_Apertium :
    public virtual ::CORBA::Object,
    public virtual omniObjRef
  {
  public:
    TranslationType* translate(const char* text, const char* srcLang, const char* destLang);
    char* detect(const char* mesg);
    LanguagePairsType* languagePairs();

    inline _objref_Apertium()  { _PR_setobj(0); }  // nil
    _objref_Apertium(omniIOR*, omniIdentity*);

  protected:
    virtual ~_objref_Apertium();

    
  private:
    virtual void* _ptrToObjRef(const char*);

    _objref_Apertium(const _objref_Apertium&);
    _objref_Apertium& operator = (const _objref_Apertium&);
    // not implemented

    friend class Apertium;
  };

  class _pof_Apertium : public _OMNI_NS(proxyObjectFactory) {
  public:
    inline _pof_Apertium() : _OMNI_NS(proxyObjectFactory)(Apertium::_PD_repoId) {}
    virtual ~_pof_Apertium();

    virtual omniObjRef* newObjRef(omniIOR*,omniIdentity*);
    virtual _CORBA_Boolean is_a(const char*) const;
  };

  class _impl_Apertium :
    public virtual omniServant
  {
  public:
    virtual ~_impl_Apertium();

    virtual TranslationType* translate(const char* text, const char* srcLang, const char* destLang) = 0;
    virtual char* detect(const char* mesg) = 0;
    virtual LanguagePairsType* languagePairs() = 0;
    
  public:  // Really protected, workaround for xlC
    virtual _CORBA_Boolean _dispatch(omniCallHandle&);

  private:
    virtual void* _ptrToInterface(const char*);
    virtual const char* _mostDerivedRepoId();
    
  };


  class _sk_Apertium :
    public virtual _impl_Apertium,
    public virtual omniOrbBoaServant
  {
  public:
    _sk_Apertium() {}
    _sk_Apertium(const omniOrbBoaKey&);
    virtual ~_sk_Apertium();
    inline Apertium::_ptr_type _this() {
      return (Apertium::_ptr_type) omniOrbBoaServant::_this(Apertium::_PD_repoId);
    }

  };

_CORBA_MODULE_END



_CORBA_MODULE POA_ApertiumService
_CORBA_MODULE_BEG

  class Apertium :
    public virtual ApertiumService::_impl_Apertium,
    public virtual ::PortableServer::ServantBase
  {
  public:
    virtual ~Apertium();

    inline ::ApertiumService::Apertium_ptr _this() {
      return (::ApertiumService::Apertium_ptr) _do_this(::ApertiumService::Apertium::_PD_repoId);
    }
  };

_CORBA_MODULE_END



_CORBA_MODULE OBV_ApertiumService
_CORBA_MODULE_BEG

_CORBA_MODULE_END





#undef _core_attr
#undef _dyn_attr



inline void
ApertiumService::Apertium::_marshalObjRef(::ApertiumService::Apertium_ptr obj, cdrStream& s) {
  omniObjRef::_marshal(obj->_PR_getobj(),s);
}




#ifdef   USE_stub_in_nt_dll_NOT_DEFINED_apertium
# undef  USE_stub_in_nt_dll
# undef  USE_stub_in_nt_dll_NOT_DEFINED_apertium
#endif
#ifdef   USE_core_stub_in_nt_dll_NOT_DEFINED_apertium
# undef  USE_core_stub_in_nt_dll
# undef  USE_core_stub_in_nt_dll_NOT_DEFINED_apertium
#endif
#ifdef   USE_dyn_stub_in_nt_dll_NOT_DEFINED_apertium
# undef  USE_dyn_stub_in_nt_dll
# undef  USE_dyn_stub_in_nt_dll_NOT_DEFINED_apertium
#endif

#endif  // __apertium_hh__

