#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.


AC_PREREQ(2.52)

AC_INIT([apertium-spa-ita], [0.1], [ftyers@users.sourceforge.net])
AM_INIT_AUTOMAKE([apertium-spa-ita], [0.1], no-define)
AC_PROG_LN_S
AC_PROG_AWK
AC_PATH_PROG(AWKPATH, $AWK,[/usr/bin/gawk])
AC_CONFIG_HEADER([config])

m4_define([required_apertium_version], [3.4.0])

PKG_CHECK_MODULES(APERTIUM,   apertium >= required_apertium_version, [], [PKG_CHECK_MODULES(APERTIUM,   apertium-3.2 >= required_apertium_version)])

m4_ifdef([AP_CHECK_LING],[],[AC_MSG_ERROR([AP_CHECK_LING not defined, is apertium.m4 in ACLOCAL_PATH? See: http://wiki.apertium.org/wiki/Installation_troubleshooting])])


AP_CHECK_LING([1], [apertium-spa])
AP_CHECK_LING([2], [apertium-ita])


AC_OUTPUT([Makefile])
AC_OUTPUT([i_to_p.awk])
AC_OUTPUT([sustituye_caracteres.awk])
AC_OUTPUT([inline_e.awk])
