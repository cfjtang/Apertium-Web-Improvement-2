#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.


AC_PREREQ(2.52)

AC_INIT([apertium-mk-bg], [0.2.0], [tihomir.rangelov@gmail.com])
AM_INIT_AUTOMAKE([apertium-mk-bg], [0.2.0], no-define)
AC_PROG_LN_S
AC_PROG_AWK
#AC_CONFIG_HEADER([config])

AC_DEFUN([PKG_CHECK_APERTIUM], [
  APERTIUM_VER=0
  PKG_CHECK_MODULES(APERTIUM, apertium-3.2 >= 3.2.0,
  [
      APERTIUM_VER=32
  ],
  [
      PKG_CHECK_MODULES(APERTIUM, apertium-3.1 >= 3.1.0,
      [
          APERTIUM_VER=31
      ],
      [
          PKG_CHECK_MODULES(APERTIUM, apertium-3.0 >= 3.0.0,
          [
              APERTIUM_VER=30
          ])
      ])
  ])
  AC_SUBST(APERTIUM_VER)
])
                          
PKG_CHECK_APERTIUM

AC_PATH_PROG(CGCOMP, cg-comp, no)
if test x$ac_cv_path_CGCOMP = x
then
  AC_MSG_ERROR([You don't have cg-comp installed.])
fi
if test x$ac_cv_path_CGCOMP = xno
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

if test $APERTIUM_VER = 10;
then MODES="";
else MODES="modes";
fi

AC_SUBST(MODES)
  
AC_OUTPUT([Makefile])
