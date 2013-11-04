AC_PREREQ(2.61)

m4_define([required_libxml_version], [2.6.17])
m4_define([required_apertium_version], [3.2.0])
m4_define([required_lttoolbox_version], [3.2.0])

AC_INIT([apertium-ranker], [0.1.0], [ftyers@prompsit.com])
AM_INIT_AUTOMAKE([apertium-ranker], [0.1.0], no-define)
AC_CONFIG_HEADER([config.h])

AC_PROG_CXX
AM_SANITY_CHECK
AC_LANG_CPLUSPLUS

AC_ARG_WITH(irstlm,
            [AC_HELP_STRING([--with-irstlm=PATH], [(optional) path to IRST's LM toolkit])],
            [with_irstlm=$withval],
            [with_irstlm=yes]
           )

AC_ARG_WITH(randlm,
            [AC_HELP_STRING([--with-randlm=PATH], [(optional) path to RandLM toolkit])],
            [with_randlm=$withval],
            [with_randlm=no]
           )

AM_CONDITIONAL([IRST_LM], true)
AM_CONDITIONAL([RAND_LM], false)


if test "x$with_irstlm" != 'xno'
then
  SAVE_CPPFLAGS="$CPPFLAGS"
  CPPFLAGS="$CPPFLAGS -I${with_irstlm}/include"

  AC_CHECK_HEADER(n_gram.h,
                 [AC_DEFINE([HAVE_IRSTLM], [], [flag for IRSTLM])],
                 [AC_MSG_ERROR([Cannot find IRST-LM!])])

  if test $MACHTYPE
  then
    MY_ARCH=$MACHTYPE
    echo $ECHO_N "THEN -> MY_ARCH=$MY_ARCH" >&6
  else
    MY_ARCH=`uname -m`
    echo $ECHO_N "ELSE -> MY_ARCH=$MY_ARCH\n" >&6
  fi

  echo ""
  #MY_ARCH=`uname -m`
  LIB_IRSTLM="-lirstlm"
  LDFLAGS="$LDFLAGS -L${with_irstlm}/lib/${MY_ARCH}"
  LIBS="$LIBS $LIB_IRSTLM"
  FMTLIBS="$FMTLIBS libirstlm.a"
  AM_CONDITIONAL([IRST_LM], true)
fi

if test "x$with_randlm" != 'xno'
then
  SAVE_CPPFLAGS="$CPPFLAGS"
  CPPFLAGS="$CPPFLAGS -I${with_randlm}/include"

  AC_CHECK_HEADER(RandLM.h,
                 [AC_DEFINE([HAVE_RANDLM], [], [flag for RandLM])],
                 [AC_MSG_ERROR([Cannot find RandLM!])])


  MY_ARCH=`uname -m`
  LIB_RANDLM="-lrandlm"
  LDFLAGS="$LDFLAGS -L${with_randlm}/lib"
  LIBS="$LIBS $LIB_RANDLM"
  FMTLIBS="$FMTLIBS librandlm.a"
  AM_CONDITIONAL([RAND_LM], true)
fi


PKG_CHECK_MODULES([APERTIUM], [apertium >= required_apertium_version])

AC_SUBST(APERTIUM_CFLAGS)
AC_SUBST(APERTIUM_LIBS)

PKG_CHECK_MODULES([LTTOOLBOX], [lttoolbox >= required_lttoolbox_version])

AC_SUBST(LTTOOLBOX_CFLAGS)
AC_SUBST(LTTOOLBOX_LIBS)

PKG_CHECK_MODULES([LIBXML], [libxml-2.0 >= required_libxml_version])

AC_SUBST(LIBXML_CFLAGS)
AC_SUBST(LIBXML_LIBS)

#
# Check for VISL constraint grammar
#

AC_PATH_PROG(CGCOMP, cg-comp, no)
if test x$ac_cv_path_CGCOMP = x
then
  AC_MSG_ERROR([You don't have cg-comp installed.])
fi
if test x$ac_cv_path_CGPROC = xno
then
  AC_MSG_ERROR([You don't have cg-comp installed.])
fi

AC_PATH_PROG(CGPROC, cg-proc, no)
if test x$ac_cv_path_CGPROC = x
then
  AC_MSG_ERROR([You don't have cg-proc installed.])
fi
if test x$ac_cv_path_CGPROC = xno
then
  AC_MSG_ERROR([You don't have cg-proc installed.])
fi

# Checks for libraries.
AC_CHECK_LIB(xml2, xmlReaderForFile)

AC_CHECK_FUNCS([setlocale strdup getopt_long])

CPPFLAGS="$CPPFLAGS $CFLAGS $APERTIUM_CFLAGS $LIBXML_CFLAGS"
LIBS="$LIBS $APERTIUM_LIBS $LIBXML_LIBS -lz"

AC_CONFIG_FILES([
                 Makefile
                 ])
AC_OUTPUT
