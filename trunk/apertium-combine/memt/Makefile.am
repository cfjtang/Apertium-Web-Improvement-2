bin_PROGRAMS = combiner collector

h_SOURCES = case_insensitive_stemmer_matcher.hh aligner.hh alignment.hh chained_words.hh naive_beam_generator.hh max_conseq_aligner.hh dummy_ranker.hh generator.hh exact_matcher.hh irstlm_ranker.hh ranker.hh hypotheses.hh parallel_scan_generator.hh case_insensitive_matcher.hh pairwise_alignment.hh case_insensitive_morph_matcher.hh minimal_crossing_aligner.hh matcher.hh
cc_SOURCES = pairwise_alignment.cc alignment.cc hypotheses.cc irstlm_ranker.cc case_insensitive_matcher.cc exact_matcher.cc max_conseq_aligner.cc naive_beam_generator.cc dummy_ranker.cc minimal_crossing_aligner.cc parallel_scan_generator.cc case_insensitive_stemmer_matcher.cc case_insensitive_morph_matcher.cc

combiner_SOURCES = apertium_combiner.cc
combiner_LDADD = -L. -lapertiumcombine$(GENERIC_MAJOR_VERSION)

collector_SOURCES = collector.cc apertium_collector.cc moses_collector.cc
collector_LDADD = $(LDFLAGS_MOSES) -L. -lapertiumcombine$(GENERIC_MAJOR_VERSION)

library_includedir = $(includedir)/$(GENERIC_LIBRARY_NAME)-$(GENERIC_API_VERSION)/$(GENERIC_LIBRARY_NAME)
library_include_HEADERS = $(h_SOURCES)

lib_LTLIBRARIES = libapertiumcombine1.la
libapertiumcombine1_la_SOURCES = $(h_SOURCES) $(cc_SOURCES)
libapertiumcombine1_la_LDFLAGS = -version-info $(GENERIC_LIBRARY_VERSION) -release $(GENERIC_RELEASE)
