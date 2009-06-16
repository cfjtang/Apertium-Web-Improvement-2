#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.61])

AC_INIT([apertium-service], [0.1.0], [p.minervini@gmail.com])
AM_INIT_AUTOMAKE([foreign])

PACKAGE=apertium-service
VERSION=0.1.0

AM_INIT_AUTOMAKE($PACKAGE, $VERSION, no-define)

ICU_VERSION=3.6.0

AC_CONFIG_HEADERS([src/config.h])

ICUCPPFLAGS=`icu-config --cppflags`
CXXFLAGS="-Wno-deprecated $ICUCPPFLAGS $CXXFLAGS"

# Checks for programs.
AC_PROG_CXX
AC_PROG_CC
AC_PROG_CPP
AC_PROG_INSTALL
AC_PROG_MAKE_SET
AC_LANG_CPLUSPLUS

# Debug mode
AC_ARG_ENABLE([debug],AC_HELP_STRING([--enable-debug],[debug mode (default disabled)]))
if test "${enable_debug}" = "yes"; then
    CPPFLAGS+=" -DDEBUG"
    CFLAGS+=" -g"
    CXXFLAGS+=" -g"
fi

# Checks for libraries.
AX_BOOST_BASE([1.37.0])
AX_BOOST_THREAD
AX_BOOST_FILESYSTEM
AX_BOOST_PROGRAM_OPTIONS
AX_BOOST_REGEX
AX_BOOST_UNIT_TEST_FRAMEWORK

AC_CHECK_LIB(iconv, libiconv_open, LIBS="$LIBS -liconv")

# Test for libtextcat 2.1 or better

AC_LANG_C
LIBTEXTCAT_CFLAGS=""
LIBTEXTCAT_LIBS="-ltextcat"

CXXFLAGS_cache=$CXXFLAGS
CXXFLAGS=" $LIBTEXTCAT_CFLAGS "
CFLAGS_cache=$CFLAGS
CFLAGS=" $LIBTEXTCAT_CFLAGS "
LDFLAGS_cache=$LDFLAGS
LDFLAGS=" $LIBTEXTCAT_LIBS "

AC_TRY_COMPILE([
#include <textcat.h>
#include <string.h>
        ],
        [
    char* buf = "The createfp program allows you to easily create your own document"
        "fingerprints. Just feed it an example document on standard input, and"
        "store the standard output:";

    void* h = textcat_Init( "tc.conf" );
    printf( "Language: %s\n", textcat_Classify(h, buf, strlen(buf)) );
    textcat_Done(h);
        ],
        [have_libtextcat=yes],[have_libtextcat=no])
LDFLAGS=$LDFLAGS_cache
CXXFLAGS=$CXXFLAGS_cache
CFLAGS=$CFLAGS_cache

if test x"$have_libtextcat" = xno; then
  LIBTEXTCAT_CFLAGS=" "
  LIBTEXTCAT_LIBS=" "
  echo " libtextcat 2.1 or greater not found... no text language guessing for you."
else
  echo "libtextcat 2.1 or greater found, text language guessing support being built..."
  AC_DEFINE(HAVE_LIBTEXTCAT, [], [define if the libtextcat library is available])
fi;

AM_CONDITIONAL(HAVE_LIBTEXTCAT, test x"$have_libtextcat" = xyes)
AC_SUBST(LIBTEXTCAT_CFLAGS)
AC_SUBST(LIBTEXTCAT_LIBS)


PKG_CHECK_MODULES(XMLPP, libxml++-2.6 >= 2.8.1)

AC_SUBST(XMLPP_CFLAGS)
AC_SUBST(XMLPP_LIBS)

PKG_CHECK_MODULES(LTTOOLBOX, lttoolbox-3.1 >= 3.1.0)

AC_SUBST(LTTOOLBOX_CFLAGS)
AC_SUBST(LTTOOLBOX_LIBS)

PKG_CHECK_MODULES(APERTIUM, apertium-3.1 >= 3.1.0)

AC_SUBST(APERTIUM_CFLAGS)
AC_SUBST(APERTIUM_LIBS)

PKG_CHECK_MODULES(LIBIQXMLRPC, libiqxmlrpc-server >= 0.8.9)

AC_SUBST(LIBIQXMLRPC_CFLAGS)
AC_SUBST(LIBIQXMLRPC_CXXFLAGS)
AC_SUBST(LIBIQXMLRPC_INCLUDES)
AC_SUBST(LIBIQXMLRPC_LDFLAGS)
AC_SUBST(LIBIQXMLRPC_LIBS)

# Checks for header files.
AC_CHECK_HEADERS([syslog.h wchar.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_HEADER_STDBOOL
AC_C_INLINE
AC_TYPE_INT32_T
AC_TYPE_SIZE_T
AC_TYPE_UINT16_T
AC_TYPE_UINT32_T
AC_TYPE_UINT64_T
AC_TYPE_UINT8_T

# Checks for library functions.
ICU_LIBS=`icu-config --ldflags`
ICUIO_LIBS=`icu-config --ldflags-icuio`

LIBS="$ICU_LIBS $ICUIO_LIBS $LIBS"

AC_FUNC_MMAP
AC_CHECK_FUNCS([open_memstream open_wmemstream])

AC_SUBST(PACKAGE)
AC_SUBST(VERSION)
AC_SUBST(CXXFLAGS)
AC_SUBST(INCLUDES)
AC_SUBST(LDFLAGS)
AC_SUBST(BOOST_THREAD_LIB)
AC_SUBST(MAKEDOCS)

AC_OUTPUT([Makefile src/Makefile])
