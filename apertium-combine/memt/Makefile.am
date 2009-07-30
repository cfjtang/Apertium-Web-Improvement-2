CXX=g++
MOSES_SRC=/Users/snippy/apertium/moses/moses/src
AM_INCLUDE=-I/Users/snippy/apertium/include -I/Users/snippy/apertium/svn/apertium/trunk/apertium/apertium -I$(MOSES_SRC)
LDADD=-lz -lirstlm -lapertium3 -lmoses
AM_LDFLAGS=-L/Users/snippy/apertium/lib -L$(MOSES_SRC)
AM_CXXFLAGS=-Wall -g $(AM_INCLUDE)

.o: .cc
	$(CXX) -c $< $(AM_CXXFLAGS) -o $@

bin_PROGRAMS = combiner collector

combiner_SOURCES = apertium_combiner.cc alignment.cc hypotheses.cc irstlm_ranker.cc 
collector_SOURCES = collector.cc apertium_collector.cc moses_collector.cc 

