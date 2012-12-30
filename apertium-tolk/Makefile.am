SUBDIRS = po

EXTRA_DIST = intltool-extract.in intltool-merge.in intltool-update.in \
	     apertium-tolk apertium-tolk.desktop.in.in tolk.glade prefix.py.in \
	     $(man_MANS) py-compile emblem-web.svg

desktopdir		= $(datadir)/applications
desktop_in_files	= apertium-tolk.desktop.in.in
desktop_DATA		= $(desktop_in_files:.desktop.in.in=.desktop)
@INTLTOOL_DESKTOP_RULE@

man_MANS	= apertium-tolk.1

bin_SCRIPTS	= apertium-tolk 
apertiumtolkdir	= $(pythondir)/tolk
apertiumtolk_PYTHON = glade.py i18n.py simplegeneric.py widget_state.py\
	__init__.py prefix.py lang_names.py Blocks.txt
	
guess_languagedir	= $(apertiumtolkdir)/guess_language
guess_language_DATA	= guess_language/console_mode.py guess_language/__init__.py\
	guess_language/__main__.py

guess_language_datadir	= $(guess_languagedir)/data
guess_language_data_DATA	= guess_language/data/__init__.py

guess_language_modelsdir	= $(guess_language_datadir)/models
guess_language_models_DATA	=	guess_language/data/models/__init__.py\
	guess_language/data/models/af.py\
	guess_language/data/models/ar.py\
	guess_language/data/models/az.py\
	guess_language/data/models/bg.py\
	guess_language/data/models/ca.py\
	guess_language/data/models/ceb.py\
	guess_language/data/models/cs.py\
	guess_language/data/models/cy.py\
	guess_language/data/models/da.py\
	guess_language/data/models/de.py\
	guess_language/data/models/en.py\
	guess_language/data/models/es.py\
	guess_language/data/models/et.py\
	guess_language/data/models/eu.py\
	guess_language/data/models/fa.py\
	guess_language/data/models/fi.py\
	guess_language/data/models/fr.py\
	guess_language/data/models/ha.py\
	guess_language/data/models/haw.py\
	guess_language/data/models/hi.py\
	guess_language/data/models/hr.py\
	guess_language/data/models/hu.py\
	guess_language/data/models/id.py\
	guess_language/data/models/is.py\
	guess_language/data/models/it.py\
	guess_language/data/models/kk.py\
	guess_language/data/models/ky.py\
	guess_language/data/models/la.py\
	guess_language/data/models/lt.py\
	guess_language/data/models/lv.py\
	guess_language/data/models/mk.py\
	guess_language/data/models/mn.py\
	guess_language/data/models/nb.py\
	guess_language/data/models/ne.py\
	guess_language/data/models/nl.py\
	guess_language/data/models/nr.py\
	guess_language/data/models/nso.py\
	guess_language/data/models/pl.py\
	guess_language/data/models/ps.py\
	guess_language/data/models/pt.py\
	guess_language/data/models/pt_br.py\
	guess_language/data/models/pt_pt.py\
	guess_language/data/models/ro.py\
	guess_language/data/models/ru.py\
	guess_language/data/models/sk.py\
	guess_language/data/models/sl.py\
	guess_language/data/models/so.py\
	guess_language/data/models/sq.py\
	guess_language/data/models/sr.py\
	guess_language/data/models/ss.py\
	guess_language/data/models/st.py\
	guess_language/data/models/sv.py\
	guess_language/data/models/sw.py\
	guess_language/data/models/tl.py\
	guess_language/data/models/tlh.py\
	guess_language/data/models/tn.py\
	guess_language/data/models/tr.py\
	guess_language/data/models/ts.py\
	guess_language/data/models/uk.py\
	guess_language/data/models/ur.py\
	guess_language/data/models/uz.py\
	guess_language/data/models/ve.py\
	guess_language/data/models/vi.py\
	guess_language/data/models/xh.py\
	guess_language/data/models/zu.py

gladedir	= $(datadir)/apertium-tolk/glade
glade_DATA	= tolk.ui emblem-web.svg

prefix.py: prefix.py.in Makefile
	sed \
	-e 's|@pkgpythondir[@]|$(pkgpythondir)|g' \
	-e 's|@pkgdatadir[@]|$(pkgdatadir)|g' \
	-e 's|@localedir[@]|$(localedir)|g' \
	-e 's|@datadir[@]|$(datadir)|g' \
	-e 's|@prefix[@]|$(prefix)|g' \
	prefix.py.in > $@

apertium-tolk.desktop.in: apertium-tolk.desktop.in.in
	intltool-merge -d po/ apertium-tolk.desktop.in.in apertium-tolk.desktop.in	

apertium-tolk.desktop: apertium-tolk.desktop.in
	cat apertium-tolk.desktop.in \
	| sed "s|PYTHONDIR|$(pythondir)|" \
	| sed -e s/$$\(datadir\)/`echo $(datadir) | sed -e 's/\//\\\\\\//g'`/g \
	> apertium-tolk.desktop

CLEANFILES = prefix.py apertium-tolk.desktop apertium-tolk.desktop.in
