plugindir = $(LIBPURPLE_LIBDIR)/purple-2

apertium_la_LDFLAGS = \
	-module \
	-avoid-version

if PLUGINS

plugin_LTLIBRARIES = apertium.la

apertium_la_SOURCES = \
	apertium.c

endif

AM_CPPFLAGS = \
	-DLIBDIR=\"$(LIBPURPLE_LIBDIR)/purple-2/\" \
	-DDATADIR=\"$(LIBPURPLE_DATADIR)\" \
	-DLOCALEDIR=\"$(LIBPURPLE_DATADIR)/locale\" \
	$(DEBUG_CFLAGS) \
	$(GTK_CFLAGS) \
	$(GLIB_CFLAGS) \
	$(PIDGIN_CFLAGS) \
	$(LIBPURPLE_CFLAGS) \
	$(LIBDBUSGLIB_CFLAGS) \
	$(APERTIUM_CFLAGS)
