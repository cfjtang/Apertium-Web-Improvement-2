AM_LDFLAGS=$(LIBS)
AM_CXXFLAGS=-Wall -g -DDEBUG 

#bin_PROGRAMS = irstlm-ranker apertium-lex-learner
bin_PROGRAMS = irstlm-ranker irstlm-ranker-frac

#if RAND_LM
#bin_PROGRAMS += randlm-ranker
#endif

irstlm_ranker_SOURCES = irstlm_ranker.cpp
irstlm_ranker_max_SOURCES = irstlm_ranker_max.cpp
irstlm_ranker_frac_SOURCES = irstlm_ranker_frac.cpp

randlm_ranker_SOURCES = randlm_ranker.cpp

