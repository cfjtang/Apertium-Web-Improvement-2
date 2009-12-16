if MOSES
	MOSES_PROGRAMS = collector
else
	MOSES_PROGRAMS =
endif

bin_PROGRAMS = $(MOSES_PROGRAMS)

collector_SOURCES = collector.cc apertium_collector.cc moses_collector.cc
collector_LDADD = $(LDFLAGS_MOSES) -L. -lapertiumcombine$(GENERIC_MAJOR_VERSION)
