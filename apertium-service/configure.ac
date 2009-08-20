#
# @file
# @author  Pasquale Minervini <p.minervini@gmail.com>
# @version 1.0
#
# @section LICENSE
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

AC_PREREQ([2.61])

AC_INIT([apertium-service], [0.1.0+svn], [p.minervini@gmail.com])
AM_INIT_AUTOMAKE([foreign])

PACKAGE=apertium-service
VERSION=0.1.0

AM_INIT_AUTOMAKE($PACKAGE, $VERSION, no-define)

ICU_VERSION=3.6.0

AC_CONFIG_HEADERS([src/config.h])

### args

AC_ARG_WITH(xmlrpc-prefix,
  [  --with-xmlrpc-prefix=PREFIX],
  with_xmlrpc_prefix="$withval")

ICUCPPFLAGS=`icu-config --cppflags`
CXXFLAGS="-Wno-deprecated $ICUCPPFLAGS $CXXFLAGS"

#ac_omniorbbase="$OMNIORBBASE"

# Checks for programs.
AC_PROG_CXX
AC_PROG_CC
AC_PROG_CPP
AC_PROG_INSTALL
AC_PROG_MAKE_SET
AC_LANG_CPLUSPLUS

AC_LANG([C++])

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
AX_BOOST_DATE_TIME
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
  AC_DEFINE(HAVE_LIBTEXTCAT, [1], [define if the libtextcat library is available])
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

# PKG_CHECK_MODULES(LIBIQXMLRPC, libiqxmlrpc-server >= 0.8.9)

# AC_SUBST(LIBIQXMLRPC_CFLAGS)
# AC_SUBST(LIBIQXMLRPC_CXXFLAGS)
# AC_SUBST(LIBIQXMLRPC_INCLUDES)
# AC_SUBST(LIBIQXMLRPC_LDFLAGS)
# AC_SUBST(LIBIQXMLRPC_LIBS)

### xmlrpc-c

AC_PATH_PROG(XMLRPC_CONFIG, xmlrpc-c-config, no, ${with_xmlrpc_prefix:+$with_xmlrpc_prefix/bin:}${PATH})

if test x$XMLRPC_CONFIG = xno; then
  AC_MSG_ERROR(Couldn't find xmlrpc-c-config -- please install the xmlrpc-c library)
fi

XMLRPC_CFLAGS=`$XMLRPC_CONFIG c++ c++2 abyss-server --cflags`
XMLRPC_LIBS=`$XMLRPC_CONFIG c++ c++2 abyss-server --libs`

XMLRPC_VERSION="$(xmlrpc-c-config --version)"

AC_SUBST(XMLRPC_CFLAGS)
AC_SUBST(XMLRPC_LIBS)

AC_SUBST(XMLRPC_VERSION)

AC_LANG_SAVE
AC_LANG_CPLUSPLUS
CXXFLAGS_cache=$CXXFLAGS
CXXFLAGS="$XMLRPC_CFLAGS"
LDFLAGS_cache=$LDFLAGS
LDFLAGS="$XMLRPC_LIBS"

### omniORB4

#AC_CORBA_ORB
#AC_CORBA_IDLCOS

# Checks for header files.
AC_CHECK_HEADERS([wchar.h])

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

AC_CHECK_FUNCS([open_memstream open_wmemstream])

AC_SUBST(PACKAGE)
AC_SUBST(VERSION)
AC_SUBST(CXXFLAGS)
AC_SUBST(INCLUDES)
AC_SUBST(LDFLAGS)
AC_SUBST(BOOST_THREAD_LIB)
AC_SUBST(MAKEDOCS)

ASCONFDIR="${sysconfdir}/${PACKAGE_NAME}"
AC_SUBST(ASCONFDIR)

asconfdir_e=`eval "echo ${ASCONFDIR}"`
AC_DEFINE_UNQUOTED(ASCONFDIR, "${asconfdir_e}", "Apertium Service's configuration directory")

# write a summary
echo             "+-----------------------------------------------------------------------------+"
echo             "| Package:                 ${PACKAGE_NAME}"
echo             "| CXX :                    $CXX"
echo             "| CXXFLAGS :               $CXXFLAGS"
echo             "| XMLRPC-C Version :       $XMLRPC_VERSION"
echo             "| Source directory:        ${srcdir}"
echo             "| Installation directory:  ${prefix}"
echo             "| Configuration directory: ${asconfdir_e}"

#case "$CORBA_ORB" in
#  omniORB3) echo "| CORBA ORB : $ac_cv_corba_omniorb3 (omniORB3)" ;;
#  omniORB4) echo "| CORBA ORB : $ac_cv_corba_omniorb4 (omniORB4)" ;;
#         *) echo "| CORBA ORB : NONE FOUND" ;;
#esac
#echo             "| omniidl   : $IDL"
#if test "x$OMNIORBBASE" != x; then
#echo             "+-----------------------------------------------------------------------------+"
#  if test "x$OMNIORBBASE" = "x$ac_omniorbbase"; then
#    echo         "| NOTE: omniORB search restricted by OMNIORBBASE=$ac_omniorbbase"
#    echo         "| To override, try 'unset OMNIORBBASE' or option '--with-omniorb'"
#  else
#    echo         "| NOTE: omniORB search restricted by --with-omniorb=$OMNIORBBASE"
#  fi
#fi
echo             "+-----------------------------------------------------------------------------+"


AC_OUTPUT([Makefile src/Makefile])
