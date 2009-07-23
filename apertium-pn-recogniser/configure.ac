AC_INIT(src/apertium-pn-recogniser.cc)

PACKAGE=apertium-pn-recogniser
VERSION=0.1.0

AM_INIT_AUTOMAKE($PACKAGE, $VERSION)

AC_PROG_CXX

AC_PROG_SED

AC_PATH_PROG(BASH, bash, no)
if test x$ac_cv_path_BASH = x
then
  AC_MSG_ERROR([You don't have bash installed.])
fi
if test x$ac_cv_path_BASH = xno
then
  AC_MSG_ERROR([You don't have bash installed.])
fi    

AC_DEFUN([AC_PROG_APERTIUM_TAGGER],[
  echo "Checking for apertium-tagger with -p option..."
  AC_CHECK_PROG(TAGGER,apertium-tagger,yes)
  if test x$TAGGER = xyes
  then
    TAGGERP=`apertium-tagger -p 2>&1|grep -c "invalid option"`
    if test $TAGGERP -gt 0
    then  
      AC_MSG_ERROR([You need apertium-tagger with option -p (version >= 3.1.0).])
    fi
  else
    AC_MSG_ERROR([You don't have apertium installed.])
  fi
])


AC_PROG_APERTIUM_TAGGER


# Check for wide strings
AC_DEFUN([AC_CXX_WSTRING],[
  AC_CACHE_CHECK(whether the compiler supports wide strings,
  ac_cv_cxx_wstring,
  [AC_LANG_SAVE
   AC_LANG_CPLUSPLUS
   AC_COMPILE_IFELSE([AC_LANG_PROGRAM([[#include <string>]],[[
std::wstring test = L"test";
   ]])],
   [ac_cv_cxx_wstring=yes], [ac_cv_cxx_wstring=no])
   AC_LANG_RESTORE
  ])
])

AC_CXX_WSTRING
AC_C_BIGENDIAN

if test "$ac_cv_cxx_wstring" = no 
then
  AC_MSG_ERROR([Missing wide string support])
fi


AC_OUTPUT(Makefile src/Makefile scripts/Makefile conf-samples/Makefile)

