#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.52)

#m4_define([required_libxml_version], [2.6.17])

AC_INIT([apertium-chunks-mixer], [0.8.0], [fsanchez@dlsi.ua.es])

AC_CONFIG_HEADERS([src/configure.h])

# Release versioning
GENERIC_MAJOR_VERSION=0
GENERIC_MINOR_VERSION=8
GENERIC_MICRO_VERSION=0

GENERIC_API_VERSION=$GENERIC_MAJOR_VERSION.$GENERIC_MINOR_VERSION
AC_SUBST(GENERIC_API_VERSION)

PACKAGE=apertium-chunks-mixer

GENERIC_VERSION=$GENERIC_MAJOR_VERSION.$GENERIC_MINOR_VERSION.$GENERIC_MICRO_VERSION
GENERIC_RELEASE=$GENERIC_MAJOR_VERSION.$GENERIC_MINOR_VERSION
AC_SUBST(GENERIC_RELEASE)
AC_SUBST(GENERIC_VERSION)

VERSION=$GENERIC_VERSION

AM_INIT_AUTOMAKE($PACKAGE, $VERSION, no-define)

# Checks for programs.

AC_MSG_CHECKING([Compilation architecture: PPC, i686, Other])
ARCH=$(/bin/uname -m)
if test x$ARCH = xppc
then
  AC_MSG_RESULT([PowerPC])
  CFLAGS="$CFLAGS -ansi -mpowerpc -maltivec -fno-pic -fomit-frame-pointer -Wall"
  CXXFLAGS="$CXXFLAGS -ansi -mpowerpc -maltivec -fno-pic -fomit-frame-pointer -Wall"
else
  if test x$ARCH = xi686
  then
    AC_MSG_RESULT([i686])
    CFLAGS="$CFLAGS -ansi -march=i686 -O3 -fno-pic -fomit-frame-pointer -Wall"
    CXXFLAGS="$CXXFLAGS -ansi -march=i686 -O3 -fno-pic -fomit-frame-pointer -Wall"
  else
    AC_MSG_RESULT([Other])
    CFLAGS="$CFLAGS -ansi -O3 -Wall"
    CXXFLAGS="$CXXFLAGS -ansi -O3 -Wall"
  fi
fi

AC_PROG_CXX
AM_SANITY_CHECK
AC_LANG_CPLUSPLUS

AC_ARG_ENABLE(debug,
              [  --enable-debug    Enable "-g" compiler options], 
              [CXXFLAGS="-g"; CFLAGS="-g"])

AC_ARG_ENABLE(profile,
              [  --enable-profile  Enable "-pg -g" compiler options],
              [CXXFLAGS="-pg -g"; CFLAGS="-pg -g"; LDFLAGS="-pg"])

echo Checking SRILM
AM_CONDITIONAL([SRI_LM], false)

AC_ARG_WITH(srilm,
            [AC_HELP_STRING([--with-srilm=PATH], [(required) path to the SRI LM toolkit])],
            [with_srilm=$withval],
            [with_srilm=no]
           )

if test "x$with_srilm" != 'xno'
then
  CXXFLAGS="$CXXFLAGS -I${with_srilm}/include"

  AC_CHECK_HEADER(Ngram.h,
                 [AC_DEFINE([HAVE_SRILM], [], [flag for SRILM])],
                 [AC_MSG_ERROR([Cannot find SRILM!])])

  LIB_SRILM="-loolm -ldstruct -lmisc"
  MY_ARCH=`${with_srilm}/sbin/machine-type`
  LDFLAGS="$LDFLAGS -L${with_srilm}/lib/${MY_ARCH}"
  LIBS="$LIBS $LIB_SRILM"
  FMTLIBS="$FMTLIBS liboolm.a libdstruct.a libmisc.a"
  AM_CONDITIONAL([SRI_LM], true)
else
  AC_MSG_ERROR([SRILM not found.])
fi
              
AC_PATH_PROG(BASH, bash, no)
if test x$ac_cv_path_BASH = x
then
  AC_MSG_ERROR([You don't have bash installed.])
fi
if test x$ac_cv_path_BASH = xno
then
  AC_MSG_ERROR([You don't have bash installed.])
fi

AC_PATH_PROG(AWK, gawk, no)
if test x$ac_cv_path_AWK = x
then
  AC_MSG_ERROR([You don't have gawk installed.])
fi
if test x$ac_cv_path_AWK = xno
then
  AC_MSG_ERROR([You don't have gawk installed.])
fi

AC_PATH_PROG(PERL, perl, no)
if test x$ac_cv_path_PERL = x
then
  AC_MSG_ERROR([You don't have perl installed.])
fi
if test x$ac_cv_path_PERL = xno
then
  AC_MSG_ERROR([You don't have perl installed.])
fi

# Checking zlib
echo Checking zlib
AC_CHECK_HEADERS(zlib.h,
  AC_CHECK_LIB(z, gzread,[
    AC_DEFINE([HAVE_LIBZ], [], [Have compression library])
      if test "x${Z_DIR}" != "x"
      then
        Z_CFLAGS="-I${Z_DIR}/include"
        Z_LIBS="-L${Z_DIR}/lib -lz"
      else
        Z_LIBS="-lz"
      fi]))

AC_SUBST(Z_CFLAGS)
AC_SUBST(Z_LIBS)

# Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADERS([locale.h getopt.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_HEADER_STDBOOL
AC_C_CONST
AC_TYPE_SIZE_T

# Checks for library functions.
AC_FUNC_ERROR_AT_LINE
AC_FUNC_MALLOC
AC_FUNC_REALLOC

AC_CHECK_DECLS([getopt_long])

AC_OUTPUT([Makefile src/Makefile])
