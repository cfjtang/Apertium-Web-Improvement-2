bin_PROGRAMS =

if MOSES
bin_PROGRAMS += collector

collector_SOURCES = collector.cc apertium_collector.cc moses_collector.cc
collector_LDADD= $(LDFLAGS_MOSES)
endif
