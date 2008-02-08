SUBDIRS = po

distcleancheck_listfiles = find . -type -f print | grep -v 'omf\.out'

desktopdir		= $(datadir)/applications
desktop_in_files	= apertium-tolk.desktop.in
desktop_DATA		= $(desktop_in_files:.desktop.in=.desktop)
@INTLTOOL_DESKTOP_RULE@

apertiumtolkdir	= $(pythondir)/apertium-tolk
apertiumtolk_PYTHON = glade.py i18n.py simplegeneric.py widget_state.py

gladedir	= $(datadir)/apertium-tolk/glade
glade_DATA	= tolk.glade

