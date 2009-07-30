#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.52)

m4_define([required_lttoolbox_version], [3.1])
m4_define([required_apertium_version], [3.1])

AC_INIT([apertium-tagger-training-tools], [0.5.0], [ftyers@prompsit.com])

AC_CONFIG_HEADERS([config.h])

# Release versioning
GENERIC_MAJOR_VERSION=0
GENERIC_MINOR_VERSION=5
GENERIC_MICRO_VERSION=0

GENERIC_API_VERSION=$GENERIC_MAJOR_VERSION.$GENERIC_MINOR_VERSION
AC_SUBST(GENERIC_API_VERSION)

PACKAGE=apertium-combine

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
  CFLAGS="$CFLAGS -Wall -ansi -mpowerpc -maltivec -fno-pic "
  CXXFLAGS="$CXXFLAGS -Wall -ansi -mpowerpc -maltivec -fno-pic "
else
  if test x$ARCH = xi686
  then
    AC_MSG_RESULT([i686])
    CFLAGS="$CFLAGS -Wall -ansi -march=i686 -O3 -fno-pic "
    CXXFLAGS="$CXXFLAGS -Wall -ansi -march=i686 -O3 -fno-pic "
  else
    AC_MSG_RESULT([Other])
    CFLAGS="$CFLAGS -Wall -ansi -O3"
    CXXFLAGS="$CXXFLAGS -Wall -ansi -O3"
  fi
fi

AC_PROG_CXX
#AC_PROG_LIBTOOL
AM_SANITY_CHECK
AC_LANG_CPLUSPLUS

AC_ARG_ENABLE(debug,
              [  --enable-debug    Enable "-g -Wall" compiler options], 
              [CXXFLAGS="-g -Wall"; CFLAGS="-g -Wall"])

AC_ARG_ENABLE(profile,
              [  --enable-profile  Enable "-pg -g -Wall" compiler options],
              [CXXFLAGS="-pg -g -Wall"; CFLAGS="-pg -g -Wall"; LDFLAGS="-pg"])

AC_PATH_PROG(BASH, bash, no)
if test x$ac_cv_path_BASH = x
then
  AC_MSG_ERROR([You don't have bash installed.])
fi
if test x$ac_cv_path_BASH = xno
then
  AC_MSG_ERROR([You don't have bash installed.])
fi

AC_PATH_PROG(APERTIUM_DESTXT, apertium-destxt, no)

AC_PATH_PROG(PKG_CONFIG, pkg-config, no)
if test x$ac_cv_path_PKG_CONFIG = x
then
  AC_MSG_ERROR([You don't have pkg-config installed.])
fi
if test x$ac_cv_path_PKG_CONFIG = xno
then
  AC_MSG_ERROR([You don't have pkg-config installed.])
fi

PKG_CHECK_MODULES(LTTOOLBOX, [lttoolbox-3.1 >= required_lttoolbox_version], CPPFLAGS="$CPPFLAGS $LTTOOLBOX_CFLAGS"; LIBS="$LIBS $LTTOOLBOX_LIBS")
PKG_CHECK_MODULES(APERTIUM, [apertium-3.1 >= required_apertium_version], CPPFLAGS="$CPPFLAGS $APERTIUM_CFLAGS"; LIBS="$LIBS $APERTIUM_LIBS")

if test "x$with_irstlm" != 'xno'
then
  SAVE_CPPFLAGS="$CPPFLAGS"
  CPPFLAGS="$CPPFLAGS -I${with_irstlm}/include"

  AC_CHECK_HEADER(n_gram.h,
                 [AC_DEFINE([HAVE_IRSTLM], [], [flag for IRSTLM])],
                 [AC_MSG_ERROR([Cannot find IRSTLM!])])

  if test $MACHTYPE
  then
    MY_ARCH=$MACHTYPE
    echo $ECHO_N "THEN -> MY_ARCH=$MY_ARCH" >&6
  else
    MY_ARCH=`uname -m`
    echo $ECHO_N "ELSE -> MY_ARCH=$MY_ARCH\n" >&6
  fi

  #MY_ARCH=`uname -m`
  LIB_IRSTLM="-lirstlm"
  LDFLAGS="$LDFLAGS -L${with_irstlm}/lib/${MY_ARCH}"
  LIBS="$LIBS $LIB_IRSTLM"
  FMTLIBS="$FMTLIBS libirstlm.a"
  AM_CONDITIONAL([IRST_LM], true)
fi

if test "x$with_moses" != 'xno'
then
  SAVE_CPPFLAGS="$CPPFLAGS"
  CPPFLAGS="$CPPFLAGS -I${with_moses}/moses/src"

  AC_CHECK_HEADER(Hypothesis.h,
                 [AC_DEFINE([HAVE_MOSES], [], [flag for MOSES])],
                 [AC_MSG_ERROR([Cannot find Moses!])])

  if test $MACHTYPE
  then
    MY_ARCH=$MACHTYPE
    echo $ECHO_N "THEN -> MY_ARCH=$MY_ARCH" >&6
  else
    MY_ARCH=`uname -m`
    echo $ECHO_N "ELSE -> MY_ARCH=$MY_ARCH\n" >&6
  fi

  #MY_ARCH=`uname -m`
  LIB_MOSES="-lmoses"
  LDFLAGS="$LDFLAGS -L${with_moses}/moses/src"
  LIBS="$LIBS $LIB_MOSES"
  FMTLIBS="$FMTLIBS libmoses.a"
  AM_CONDITIONAL([MOSES], true)
fi


# Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADERS([regex.h sys/types.h sys/times.h sys/wait.h sys/resource.h locale.h getopt.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_HEADER_STDBOOL
AC_C_CONST
AC_TYPE_SIZE_T

# Checks for library functions.
AC_FUNC_ERROR_AT_LINE
AC_FUNC_MALLOC
AC_FUNC_REALLOC

#AC_CHECK_DECLS([fread_unlocked, fwrite_unlocked, fgetc_unlocked, fputc_unlocked, fputs_unlocked, getopt_long])
AC_CHECK_DECLS([getopt_long])

#AC_CHECK_FUNCS([setlocale strdup])
AC_CHECK_FUNCS([setlocale])

AC_OUTPUT([Makefile memt/Makefile])
