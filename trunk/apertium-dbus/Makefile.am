SUBDIRS = src

EXTRA_DIST = org.apertium.info.service org.apertium.mode.service

SERVICEDIR=$(prefix)/share/dbus-1/services/

install-data-local:
	install -dm755 $(DESTDIR)$(SERVICEDIR) || return 1
	$(INSTALL_DATA) org.apertium.info.service $(DESTDIR)$(SERVICEDIR)
	$(INSTALL_DATA) org.apertium.mode.service $(DESTDIR)$(SERVICEDIR)
