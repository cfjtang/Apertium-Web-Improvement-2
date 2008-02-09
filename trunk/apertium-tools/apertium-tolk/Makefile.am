SUBDIRS = po

EXTRA_DIST = intltool-extract.in intltool-merge.in intltool-update.in \
	     apertium-tolk apertium-tolk.desktop.in tolk.glade prefix.py.in \
	     $(man_MANS) py-compile

desktopdir		= $(datadir)/applications
desktop_in_files	= apertium-tolk.desktop.in
desktop_DATA		= $(desktop_in_files:.desktop.in=.desktop)
@INTLTOOL_DESKTOP_RULE@

man_MANS	= apertium-tolk.1

bin_SCRIPTS	= apertium-tolk 
apertiumtolkdir	= $(pythondir)/tolk
apertiumtolk_PYTHON = glade.py i18n.py simplegeneric.py widget_state.py __init__.py prefix.py

gladedir	= $(datadir)/apertium-tolk/glade
glade_DATA	= tolk.glade

prefix.py: prefix.py.in Makefile
	sed \
	-e 's|@pkgpythondir[@]|$(pkgpythondir)|g' \
	-e 's|@pkgdatadir[@]|$(pkgdatadir)|g' \
	-e 's|@localedir[@]|$(localedir)|g' \
	-e 's|@datadir[@]|$(datadir)|g' \
	-e 's|@prefix[@]|$(prefix)|g' \
	prefix.py.in > $@

CLEANFILES = prefix.py
