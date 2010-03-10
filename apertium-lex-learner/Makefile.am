AM_LDFLAGS=$(LIBS)
AM_CXXFLAGS=-Wall -g -DDEBUG $(CFLAGS)

bin_PROGRAMS = irstlm-ranker apertium-lex-learner

if RAND_LM
bin_PROGRAMS += randlm-ranker
endif

irstlm_ranker_SOURCES = irstlm-ranker.cpp

apertium_lex_learner_SOURCES = apertium_lex_learner.cpp ambiguate_corpus.cpp 

randlm_ranker_SOURCES = randlm-ranker.cpp

