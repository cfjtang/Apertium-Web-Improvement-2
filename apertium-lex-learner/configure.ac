AC_PREREQ(2.61)

AC_INIT([apertium-lex-learner], [0.1], [ftyers@prompsit.com])
AM_INIT_AUTOMAKE([apertium-lex-learner], [0.1], no-define)

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

#
# Check for VISL constraint grammar
#

echo ""

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


LIBS="$LIBS -lz"

AC_CONFIG_FILES([
                 Makefile
                 ])
AC_OUTPUT
