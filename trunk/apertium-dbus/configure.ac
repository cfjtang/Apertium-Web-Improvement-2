AC_INIT([apertium-dbus], [0.1], [wynand.winterbach@gmail.com])
AM_INIT_AUTOMAKE([apertium-dbus], [0.1], no-define)

AM_MAINTAINER_MODE
AM_PATH_PYTHON([3.0])

m4_define([required_apertium_version], [3.0.0])
PKG_CHECK_MODULES(APERTIUM, apertium >= required_apertium_version, [],
[
  PKG_CHECK_MODULES(APERTIUM, apertium-3.0 >= required_apertium_version, [],
  [
    PKG_CHECK_MODULES(APERTIUM, apertium-3.1 >= required_apertium_version, [],
    [
      PKG_CHECK_MODULES(APERTIUM, apertium-3.2 >= required_apertium_version)
    ])
  ])
])

PKG_CHECK_MODULES(DBUS,  dbus-1 >= 0.34, DBUS="yes", AC_MSG_ERROR([*** unable to locate dbus library! If on Debian/Ubuntu run: apt-get install dbus libdbus-1-dev  ***]))

PKG_CHECK_MODULES(PYDBUS, dbus-python >= 0.82, PYDBUS="yes", AC_MSG_ERROR([*** unable to locate dbus-python library! If on Debian/Ubuntu run: apt-get install python3-dbus ***]))

AC_SUBST(PYDBUS_CFLAGS)
AC_SUBST(PYDBUS_LIBS)

AC_OUTPUT([
Makefile
src/Makefile
org.apertium.mode.service
org.apertium.info.service
])
