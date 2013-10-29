AC_INIT([apertium-dbus], [0.1], [wynand.winterbach@gmail.com])
AM_INIT_AUTOMAKE([apertium-dbus], [0.1], no-define)

AM_MAINTAINER_MODE
AM_PATH_PYTHON([3.0])

m4_define([required_apertium_version], [3.0.0])
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
PKG_CHECK_APERTIUM

PKG_CHECK_MODULES(DBUS,  dbus-1 >= 0.34, DBUS="yes", AC_MSG_ERROR([*** unable to locate dbus library! If on Debian/Ubuntu run: apt-get install dbus libdbus-1-dev  ***]))

PKG_CHECK_MODULES(PYDBUS, dbus-python >= 0.82, PYDBUS="yes", AC_MSG_ERROR([*** unable to locate python-dbus library! If on Debian/Ubuntu run: apt-get install python-dbus ***]))

AC_SUBST(PYDBUS_CFLAGS)
AC_SUBST(PYDBUS_LIBS)

AC_OUTPUT([
Makefile
src/Makefile
org.apertium.mode.service
org.apertium.info.service
])
