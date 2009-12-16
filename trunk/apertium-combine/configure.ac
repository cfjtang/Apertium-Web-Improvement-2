#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.61)

m4_define([required_lttoolbox_version], [3.2])
m4_define([required_apertium_version], [3.2])

AC_INIT([apertium-combine], [1.0], [ftyers@prompsit.com])

AC_CONFIG_HEADERS([config.h])

AC_CANONICAL_SYSTEM

GENERIC_LIBRARY_NAME=apertium-combine

# Release versioning
GENERIC_MAJOR_VERSION=1
GENERIC_MINOR_VERSION=0
GENERIC_MICRO_VERSION=0

GENERIC_API_VERSION=$GENERIC_MAJOR_VERSION.$GENERIC_MINOR_VERSION
AC_SUBST(GENERIC_API_VERSION)
AC_SUBST(GENERIC_MAJOR_VERSION)

# Shared library versioning
GENERIC_LIBRARY_VERSION=0:0:0
#                       | | |
#                +------+ | +---+
#                |        |     |
#             current:revision:age
#                |        |     |
#                |        |     +- increment if interfaces have been added
#                |        |        set to zero if interfaces have been removed
#                                  or changed
#                |        +- increment if source code has changed
#                |           set to zero if current is incremented
#                +- increment if interfaces have been added, removed or changed

AC_SUBST(GENERIC_LIBRARY_VERSION)
PACKAGE=$GENERIC_LIBRARY_NAME
AC_SUBST(GENERIC_LIBRARY_NAME)

PACKAGE=apertium-combine

GENERIC_VERSION=$GENERIC_MAJOR_VERSION.$GENERIC_MINOR_VERSION.$GENERIC_MICRO_VERSION
GENERIC_RELEASE=$GENERIC_MAJOR_VERSION.$GENERIC_MINOR_VERSION
AC_SUBST(GENERIC_RELEASE)
AC_SUBST(GENERIC_VERSION)

VERSION=$GENERIC_VERSION

AM_INIT_AUTOMAKE($PACKAGE, $VERSION, no-define)

# Checks for programs.

ARCH=$(/bin/uname -m)
AC_MSG_CHECKING([Compilation architecture: PPC, i686, Other])
if test x$ARCH = xppc
then
  AC_MSG_RESULT([PowerPC])
  CFLAGS="$CFLAGS -Wall -ansi -mpowerpc -maltivec -fno-pic "
  CXXFLAGS="$CXXFLAGS -Wall -ansi -mpowerpc -mal1tivec -fno-pic "
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
AC_PROG_LIBTOOL
AM_SANITY_CHECK
AC_LANG_CPLUSPLUS

AC_ARG_ENABLE(debug,
              [  --enable-debug  Enable "-g -Wall" compiler options],
              [CXXFLAGS="-g -Wall";CFLAGS="-g -Wall"])

AC_ARG_ENABLE(profile,
              [  --enable-profile  Enable "-pg -g -Wall" compiler options],
              [CXXFLAGS="-pg -g -Wall"; CFLAGS="-pg -g -Wall"; LDFLAGS="-pg"])

AM_CONDITIONAL([IRSTLM], true)
# AM_CONDITIONAL([MOSES], true)

AC_ARG_WITH(irstlm,
            [AC_HELP_STRING([--with-irstlm=PATH], [(mandatory) path to IRST's LM toolkit])],
            [with_irstlm=$withval],
            [with_irstlm=no]
           )

# AC_ARG_WITH(moses,
#            [AC_HELP_STRING([--with-moses=PATH], [path to Moses SMT toolkit])],
#            [with_moses=$withval],
#            [with_moses=no]
#           )

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

AC_PATH_PROG(PKG_CONFIG, pkg-config, no)
if test x$ac_cv_path_PKG_CONFIG = x
then
  AC_MSG_ERROR([You don't have pkg-config installed.])
fi
if test x$ac_cv_path_PKG_CONFIG = xno
then
  AC_MSG_ERROR([You don't have pkg-config installed.])
fi

PKG_CHECK_MODULES(LTTOOLBOX, [lttoolbox-3.2 >= required_lttoolbox_version], CPPFLAGS="$CPPFLAGS $LTTOOLBOX_CFLAGS"; LIBS="$LIBS $LTTOOLBOX_LIBS")
PKG_CHECK_MODULES(APERTIUM, [apertium-3.2 >= required_apertium_version], CPPFLAGS="$CPPFLAGS $APERTIUM_CFLAGS"; LIBS="$LIBS $APERTIUM_LIBS")

AC_PATH_PROG(APERTIUM_DESTXT, apertium-destxt, no)

if test "x$with_irstlm" != 'xno'
then
  SAVE_CPPFLAGS="$CPPFLAGS"
  CPPFLAGS="$CPPFLAGS -I${with_irstlm}/include"

  AC_CHECK_HEADER(n_gram.h,
                 [AC_DEFINE([HAVE_IRSTLM], [], [flag for IRSTLM])],
                 [AC_MSG_ERROR([Cannot find IRSTLM!])])

  AC_CHECK_HEADER(lmtable.h,
                 [AC_DEFINE([HAVE_IRSTLM], [], [flag for IRSTLM])],
                 [AC_MSG_ERROR([Cannot find IRSTLM!])])

  if test $MACHTYPE
  then
    MY_ARCH=$MACHTYPE
    #echo $ECHO_N "THEN -> MY_ARCH=$MY_ARCH" >&6
  else
    MY_ARCH=`uname -m`
    #echo $ECHO_N "ELSE -> MY_ARCH=$MY_ARCH\n" >&6
  fi

  #MY_ARCH=`uname -m`
  LIB_IRSTLM="-lirstlm"
  LDFLAGS="$LDFLAGS -L${with_irstlm}/lib/${MY_ARCH}"
  LIBS="$LIBS $LIB_IRSTLM"
  FMTLIBS="$FMTLIBS libirstlm.a"
  AM_CONDITIONAL([IRSTLM], true)
fi

# if test "x$with_moses" != 'xno'
# then
#  SAVE_CPPFLAGS="$CPPFLAGS"
#  CPPFLAGS="$CPPFLAGS -I${with_moses}/moses/src"

#  AC_CHECK_HEADER(Hypothesis.h,
#                 [AC_DEFINE([HAVE_MOSES], [], [flag for MOSES])],
#                 [AC_MSG_ERROR([Cannot find Moses!])])

#  if test $MACHTYPE
#  then
#    MY_ARCH=$MACHTYPE
#  else
#    MY_ARCH=`uname -m`
#  fi

#  LDFLAGS_MOSES="-L${with_moses}/moses/src -lmoses"
#  AC_SUBST(LDFLAGS_MOSES)
#  AM_CONDITIONAL([MOSES], true)
# fi


# Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADERS([regex.h sys/types.h sys/times.h sys/wait.h sys/resource.h locale.h getopt.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_HEADER_STDBOOL
AC_C_CONST
AC_C_INLINE 
AC_TYPE_SIZE_T

# Checks for library functions.
AC_FUNC_ERROR_AT_LINE
AC_FUNC_MALLOC
AC_FUNC_REALLOC

#AC_CHECK_DECLS([fread_unlocked, fwrite_unlocked, fgetc_unlocked, fputc_unlocked, fputs_unlocked, getopt_long])
AC_CHECK_DECLS([getopt_long])

#AC_CHECK_FUNCS([setlocale strdup])
AC_CHECK_FUNCS([setlocale])

AC_CONFIG_FILES([
                 Makefile
                 apertium-combine-1.0.pc
                 combiner/Makefile
                 ])

#AC_OUTPUT([Makefile apertium-combine-1.0.pc memt/Makefile])
AC_OUTPUT
