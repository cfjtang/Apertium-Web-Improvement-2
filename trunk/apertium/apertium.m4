# apertium.m4 - Macros to locate and utilise apertium libraries -*- Autoconf -*-
# serial 1 (apertium-3.4.0)
#
# Copyright (C) 2013--2015 Universitat d'Alacant / Universidad de Alicante
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License as
# published by the Free Software Foundation; either version 2 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
# 02111-1307, USA.


# AP_CHECK_LING([ID], [MONOLINGUAL_PACKAGE])
#
# Check to see whether MONOLINGUAL_PACKAGE exists, and if so sets
# AP_LIB[ID] and AP_SRC[ID].
#
# As an example, AP_CHECK_LING([1], [apertium-fie]) would check that
# apertium-fie exists, and set AP_LIB1 and AP_SRC1 to the paths
# containing the binaries and sources respectively of that monolingual
# language package.
#
# Also sets up options --with-lang[ID] (e.g. --with-lang1) if the user
# wants to use the source code checkout instead of installed files.
# ------------------------------------------
AC_DEFUN([AP_CHECK_LING],
[
  AC_ARG_VAR([AP_SRC][$1], [Path to $2 sources, same as AP_LIB$1 if --with-lang$1 set])
  AC_ARG_VAR([AP_LIB][$1], [Path to $2 binaries, same as AP_SRC$1 if --with-lang$1 set])
  AC_ARG_VAR([AP_SUBDIRS], [List of all --with-lang dirs; add it to SUBDIRS to make configure-specified dependencies recursively])
  AC_ARG_WITH([lang][$1],
    [dnl
AS_HELP_STRING([--with-lang][$1],dnl
[Uninstalled source directory for $2, defines AP_SRC$1 and AP_LIB$1 for Makefile, otherwise these are set to paths of installed files.])
    ],
    [
      AP_LIB$1=$withval
      AP_SRC$1=$withval
      echo "Using $2 from $withval"
      AP_SUBDIRS="$AP_SUBDIRS $withval"
    ],
    [
      # TODO: PKG_CHECK_MODULES sets useless variables, while _EXISTS
      # doesn't error if not found, should make a PKG_CHECK macro that
      # errors but does not set _CFLAGS/_LIBS
      PKG_CHECK_MODULES(m4_toupper(m4_bpatsubst($2, [-], [_])), [$2])
      AP_LIB$1=`pkg-config --variable=dir $2`
      AP_SRC$1=`pkg-config --variable=srcdir $2`
    ])
  if test -z "$AP_SRC$1" || ! test -d "$AP_SRC$1"; then
    AC_MSG_ERROR([Could not find sources dir for $2 (AP_SRC$1="$AP_SRC$1")])
  fi
])


# AP_MKINCLUDE()
#
# Creates the file ap_include.am and sets the variable ap_include to
# point to this path. Now in your Makefile.am you can include
# ap_include.am by writing @ap_include@ on a line by itself.
#
# The file defines a pattern rule for making modes files, and a goal
# for installing the ones that have install="yes" in modes.xml. To
# generate modes, include a line like
#
#     noinst_DATA=modes/$(PREFIX1).mode
#
# in your Makefile.am with _at most one mode_ (the others will be
# created even if you list only one, listing several will lead to
# trouble with parallell make).
# 
# Install the modes by making install-data-local dependent on
# install-modes, ie.
#
#     install-data-local: install-modes
#
# Also defined is a goal for making the .deps folder. If you want some
# file to be built in a folder named .deps, just make that goal
# dependent on .deps/.d, e.g.
#
#     .deps/intermediate.dix: original.dix .deps/.d
# 
# ------------------------------------------
AC_DEFUN([AP_MKINCLUDE],
[
  AC_SUBST_FILE(ap_include)
  ap_include=$srcdir/ap_include.am

  cat >$srcdir/ap_include.am <<EOF

modes/%.mode: modes.xml
	apertium-validate-modes modes.xml
	apertium-gen-modes modes.xml
	modes=\`xmllint --xpath '//mode@<:@@install="yes"@:>@/@name' modes.xml | sed 's/ *name="\(@<:@^"@:>@*\)"/\1.mode /g'\`; \\
		if test -n "\$\$modes"; then mv \$\$modes modes/; fi

apertium_modesdir=\$(prefix)/share/apertium/modes/
install-modes:
	mv modes modes.bak
	apertium-gen-modes modes.xml \$(BASENAME)
	rm -rf modes
	mv modes.bak modes
	test -d \$(DESTDIR)\$(apertium_modesdir) || mkdir \$(DESTDIR)\$(apertium_modesdir)
	modes=\`xmllint --xpath '//mode@<:@@install="yes"@:>@/@name' modes.xml | sed 's/ *name="\(@<:@^"@:>@*\)"/\1.mode /g'\`; \\
		if test -n "\$\$modes"; then \\
			\$(INSTALL_DATA) \$\$modes \$(DESTDIR)\$(apertium_modesdir); \\
			rm \$\$modes; \\
		fi

.deps/.d:
	test -d .deps || mkdir .deps
	touch \$[]@

.PRECIOUS: .deps/.d

langs:
	@fail=; \
	if \$(am__make_keepgoing); then \
	  failcom='fail=yes'; \
	else \
	  failcom='exit 1'; \
	fi; \
	dot_seen=no; \
	list='\$(AP_SUBDIRS)'; \
	for subdir in \$\$list; do \
	  echo "Making \$\$subdir"; \
	  (\$(am__cd) \$\$subdir && \$(MAKE) \$(AM_MAKEFLAGS) all-am) \
	  || eval \$\$failcom; \
	done; \
	\$(MAKE) \$(AM_MAKEFLAGS) all-am || exit 1; \
	test -z "\$\$fail"
.PHONY: langs

EOF

])
