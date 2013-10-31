#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.52)

m4_define([required_apertium_version], [1.9.0])

AC_INIT([apertium-oc-ca], [1.0.6], [sortiz@users.sourceforge.net])
AM_INIT_AUTOMAKE([apertium-oc-ca], [1.0.6], no-define)
AC_PROG_LN_S
AC_PROG_AWK
AC_CONFIG_HEADER([config])

PKG_CHECK_MODULES(APERTIUM,   apertium >= required_apertium_version, [], [PKG_CHECK_MODULES(APERTIUM,   apertium-3.2 >= required_apertium_version)])



if test $APERTIUM_VER = 10;
then MODES="";
else MODES="modes";
fi

AC_SUBST(MODES)

if test -d .deps
then echo "";
else mkdir .deps
fi
 
AC_OUTPUT([Makefile])
