CXX=g++
MOSES_SRC=/Users/snippy/apertium/moses/moses/src
AM_INCLUDE=-I/Users/snippy/apertium/include -I/Users/snippy/apertium/svn/apertium/trunk/apertium/apertium -I$(MOSES_SRC)
LDADD=-lz -lirstlm -lapertium3 -lmoses -lpcre
AM_LDFLAGS=$(MEMT_LIBS)
AM_CXXFLAGS=-Wall -g -DDEBUG $(MEMT_CFLAGS)

.o: .cc
	$(CXX) -c $< $(MEMT_CFLAGS) -o $@

bin_PROGRAMS = combiner collector

combiner_SOURCES = apertium_combiner.cc pairwise_alignment.cc alignment.cc hypotheses.cc irstlm_ranker.cc case_insensitive_matcher.cc exact_matcher.cc max_conseq_aligner.cc naive_beam_generator.cc dummy_ranker.cc minimal_crossing_aligner.cc parallel_scan_generator.cc case_insensitive_stemmer_matcher.cc
collector_SOURCES = collector.cc apertium_collector.cc moses_collector.cc 

CLEANFILES = *~

