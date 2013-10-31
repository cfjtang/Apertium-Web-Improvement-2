#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.


AC_PREREQ(2.52)

m4_define([required_apertium_version], [0.9.2])

AC_INIT([apertium-pt-gl], [0.9.2], [oscar@imaxin.com])
AM_INIT_AUTOMAKE([apertium-pt-gl], [0.9.2], no-define)
AC_PROG_LN_S
AC_PROG_AWK
AC_CONFIG_HEADER([config])

PKG_CHECK_MODULES(APERTIUM,   apertium >= required_apertium_version, [], [PKG_CHECK_MODULES(APERTIUM,   apertium-3.2 >= required_apertium_version)])



if test $APERTIUM_VER = 10;
then MODES="";
else MODES="modes";
fi

AC_SUBST(MODES)

  
AC_OUTPUT([Makefile])
