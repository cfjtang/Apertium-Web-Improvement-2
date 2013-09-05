#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.52)

m4_define([required_apertium_version], [3.2.0])
m4_define([required_lttolbox_version], [3.2.0])

AC_INIT([apertium-is-sv], [0.1.0], [tihomir.rangelov@gmail.com])
AM_INIT_AUTOMAKE([apertium-is-sv], [0.1.0], no-define)
AC_PROG_LN_S
AC_PROG_AWK

AC_DEFUN([PKG_CHECK_APERTIUM], [
  APERTIUM_VER=0
  PKG_CHECK_MODULES(APERTIUM, apertium-3.0 >= 3.0.0,
  [
    APERTIUM_VER=30
  ],
  [
    PKG_CHECK_MODULES(APERTIUM, apertium-3.1 >= 3.1.0,
    [
      APERTIUM_VER=31
    ],
    [
      PKG_CHECK_MODULES(APERTIUM, apertium-3.2 >= 3.2.0)
      APERTIUM_VER=32
    ])
  ])
  AC_SUBST(APERTIUM_VER)
])

AC_DEFUN([PKG_CHECK_LTTOOLBOX], [
  LTTOOLBOX_VER=0
  PKG_CHECK_MODULES(LTTOOLBOX, lttoolbox-3.0 >= 3.0.0,
  [
    LTTOOLBOX_VER=30
  ],
  [
    PKG_CHECK_MODULES(LTTOOLBOX, lttoolbox-3.1 >= 3.1.0,
    [
      LTTOOLBOX_VER=31
    ],
    [
      PKG_CHECK_MODULES(LTTOOLBOX, lttoolbox-3.2 >= 3.2.0)
      LTTOOLBOX_VER=32
    ])
  ])
  AC_SUBST(LTTOOLBOX_VER)
])

PKG_CHECK_LTTOOLBOX
PKG_CHECK_APERTIUM


AC_OUTPUT([Makefile])
