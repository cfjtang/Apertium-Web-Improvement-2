#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.


AC_PREREQ(2.52)

AC_INIT([apertium-eu-en], [0.3.1], [jimregan@users.sourceforge.net])
AM_INIT_AUTOMAKE([apertium-eu-en], [0.3.1], no-define)
AC_PROG_LN_S
AC_PROG_AWK
AC_CONFIG_HEADER([config])

m4_define([required_apertium_version], [3.2.0])
PKG_CHECK_MODULES(APERTIUM,   apertium >= required_apertium_version, [], [PKG_CHECK_MODULES(APERTIUM,   apertium-3.2 >= required_apertium_version)])
                          

if test $APERTIUM_VER = 10;
then MODES="";
else MODES="modes";
fi

ALTXSL="alt.xsl";

AC_SUBST(MODES)
AC_SUBST(ALTXSL)

  
AC_OUTPUT([Makefile])
