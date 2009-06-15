#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.61])
AC_INIT([apertium-service], [0.1.0], [p.minervini@gmail.com])

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

# Checks for libraries.

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

PKG_CHECK_MODULES(XMLPP, libxml++-2.6 >= 2.8.1)

AC_SUBST(XMLPP_CFLAGS)
AC_SUBST(XMLPP_LIBS)


PKG_CHECK_MODULES(LTTOOLBOX, lttoolbox-3.1 >= 3.1.0)

AC_SUBST(LTTOOLBOX_CFLAGS)
AC_SUBST(LTTOOLBOX_LIBS)

PKG_CHECK_MODULES(APERTIUM, apertium-3.1 >= 3.1.0)

AC_SUBST(APERTIUM_CFLAGS)
AC_SUBST(APERTIUM_LIBS)

ICU_LIBS=`icu-config --ldflags`
ICUIO_LIBS=`icu-config --ldflags-icuio`

LIBS="$ICU_LIBS $ICUIO_LIBS $LIBS"

AC_FUNC_MMAP
AC_CHECK_FUNCS([open_memstream open_wmemstream])

AC_OUTPUT([Makefile src/Makefile])
