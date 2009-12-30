AM_LDFLAGS=$(LIBS)
AM_CXXFLAGS=-Wall -g -DDEBUG $(CFLAGS)

bin_PROGRAMS = irstlm-ranker

if RAND_LM
bin_PROGRAMS += randlm-ranker
endif

irstlm_ranker_SOURCES = irstlm-ranker.cpp

randlm_ranker_SOURCES = randlm-ranker.cpp

