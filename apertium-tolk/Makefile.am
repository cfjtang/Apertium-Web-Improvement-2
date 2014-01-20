SUBDIRS = po

EXTRA_DIST = intltool-extract.in intltool-merge.in intltool-update.in \
	     apertium-tolk apertium-tolk.desktop.in.in tolk.glade tolk/prefix.py.in \
	     $(man_MANS) py-compile emblem-web.svg

desktopdir		= $(datadir)/applications
desktop_in_files	= apertium-tolk.desktop.in.in
desktop_DATA		= $(desktop_in_files:.desktop.in.in=.desktop)
@INTLTOOL_DESKTOP_RULE@

man_MANS	= apertium-tolk.1

bin_SCRIPTS	= apertium-tolk 
apertiumtolkdir	= $(pythondir)/tolk
apertiumtolk_PYTHON = tolk/glade.py tolk/i18n.py tolk/simplegeneric.py tolk/widget_state.py tolk/__init__.py tolk/lang_names.py
nodist_apertiumtolk_PYTHON = tolk/prefix.py
BUILT_SOURCES = tolk/prefix.py

gladedir	= $(datadir)/apertium-tolk/glade
glade_DATA	= tolk.ui emblem-web.svg

tolk/prefix.py: tolk/prefix.py.in Makefile
	sed \
	-e 's|@pkgpythondir[@]|$(pkgpythondir)|g' \
	-e 's|@pkgdatadir[@]|$(pkgdatadir)|g' \
	-e 's|@localedir[@]|$(localedir)|g' \
	-e 's|@datadir[@]|$(datadir)|g' \
	-e 's|@prefix[@]|$(prefix)|g' \
	$< > $@

apertium-tolk.desktop.in: apertium-tolk.desktop.in.in
	intltool-merge -d po/ apertium-tolk.desktop.in.in apertium-tolk.desktop.in	

apertium-tolk.desktop: apertium-tolk.desktop.in
	cat apertium-tolk.desktop.in \
	| sed "s|PYTHONDIR|$(pythondir)|" \
	| sed -e s/$$\(datadir\)/`echo $(datadir) | sed -e 's/\//\\\\\\//g'`/g \
	> apertium-tolk.desktop

CLEANFILES = prefix.py apertium-tolk.desktop apertium-tolk.desktop.in
