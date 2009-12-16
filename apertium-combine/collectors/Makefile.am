if MOSES
	bin_PROGRAMS = collector
else
	bin_PROGRAMS =
endif

collector_SOURCES = collector.cc apertium_collector.cc moses_collector.cc
collector_LDADD = $(LDFLAGS_MOSES) -L. -lapertiumcombine$(GENERIC_MAJOR_VERSION)
