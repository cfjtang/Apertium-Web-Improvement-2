AC_PREREQ([2.56])

AC_INIT([apertium-tolk], [0.2], [wynand.winterbach@gmail.com])
AM_INIT_AUTOMAKE([apertium-tolk], [0.2], no-define)

AM_MAINTAINER_MODE

AC_PROG_INTLTOOL([0.35.0])

AM_PATH_PYTHON([3.0])

GETTEXT_PACKAGE=apertium-tolk
AC_SUBST(GETTEXT_PACKAGE)
AC_DEFINE_UNQUOTED([GETTEXT_PACKAGE],["$GETTEXT_PACKAGE"],[Define the gettext package to be used])
AM_GLIB_GNU_GETTEXT


m4_define([required_apertium_version], [3.2.0])
PKG_CHECK_MODULES(APERTIUM,   apertium >= required_apertium_version, [], [PKG_CHECK_MODULES(APERTIUM,   apertium-3.2 >= required_apertium_version)])

PKG_CHECK_MODULES(DBUS,  dbus-1 >= 0.34, DBUS="yes", AC_MSG_ERROR([*** unable to locate dbus! Run: apt-get install dbus libdbus-1-dev  ***]))

PKG_CHECK_MODULES(PYDBUS, dbus-python >= 0.82, PYDBUS="yes", AC_MSG_ERROR([*** unable to locate python-dbus! Run: apt-get install python-dbus ***]))

ac_config_commands="$ac_config_commands po/stamp-it"

AC_SUBST(PYDBUS_CFLAGS)
AC_SUBST(PYDBUS_LIBS)

AC_OUTPUT([
Makefile
po/Makefile.in
])
