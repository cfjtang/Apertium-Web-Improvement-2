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
