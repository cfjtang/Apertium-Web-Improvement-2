SUBDIRS = po

EXTRA_DIST = intltool-extract.in intltool-merge.in intltool-update.in \
             apertium-verbconj apertium-verbconj.desktop.in.in verbconj.glade config.py.in\
	     $(man_MANS) apertium-logo.png

desktopdir		= $(datadir)/applications
desktop_in_files	= apertium-verbconj.desktop.in.in
desktop_DATA		= $(desktop_in_files:.desktop.in.in=.desktop)

@INTLTOOL_DESKTOP_RULE@

man_MANS	= apertium-verbconj.1

bin_SCRIPTS	= apertium-verbconj
verbconjdir     = $(pythondir)/verbconj
verbconj_PYTHON = verbconj.py config.py

gladedir	= $(datadir)/verbconj/
glade_DATA	= verbconj.glade apertium-logo.png

config.py: config.py.in Makefile
	sed \
	-e 's|@pkgpythondir[@]|$(pkgpythondir)|g' \
	-e 's|@pkgdatadir[@]|$(pkgdatadir)|g' \
	-e 's|@localedir[@]|$(localedir)|g' \
	-e 's|@datadir[@]|$(datadir)|g' \
	-e 's|@gladedir[@]|$(gladedir)|g' \
	-e 's|@prefix[@]|$(prefix)|g' \
	config.py.in > $@

apertium-verbconj.desktop.in: apertium-verbconj.desktop.in.in
	./intltool-merge -d po/ apertium-verbconj.desktop.in.in apertium-verbconj.desktop.in	

apertium-verbconj.desktop: apertium-verbconj.desktop.in
	@cat apertium-verbconj.desktop.in \
	| sed -e s/$$\(datadir\)/`echo $(datadir) | sed -e 's/\//\\\\\\//g'`/g \
	> apertium-verbconj.desktop

#This is temporary, honest!
apertium-verbconj: verbconj.py config.py
	@echo "#!$(BASH)" > $@
	@echo $(PYTHON) $(verbconjdir)/verbconj.py >> $@
	@chmod a+x $@

CLEANFILES = config.py apertium-verbconj.desktop apertium-verbconj.desktop.in apertium-verbconj

DISTCLEANFILES = intltool-extract intltool-merge intltool-update
