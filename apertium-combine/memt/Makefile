CXX=g++
INCLUDE=-I/home/fran/statmt/mosesdecoder/moses/src -I/home/fran/statmt/irstlm/include -I/build/local/default/include/apertium-3.1
LDFLAGS=-L/home/fran/statmt/mosesdecoder/moses/src -L/home/fran/statmt/irstlm/lib/x86_64-pc-linux-gnu  -L/build/local/default/lib
LDADD=-lmoses -lirstlm -lz -lapertium3
#INCLUDE=-I/Users/snippy/apertium/include 
#LDADD=-lz -lirstlm 
#LDFLAGS=-L/Users/snippy/apertium/lib
CFLAGS=-Wall -DDEBUG -g $(INCLUDE)

all: apertium-combiner apertium-collector

apertium-collector: collector.o apertium_collector.o moses_collector.o 
	$(CXX) -Wall -o apertium-collector $^ $(LDFLAGS) $(LDADD)

apertium-combiner: apertium_combiner.o alignment.o hypotheses.o irstlm_ranker.o 
	$(CXX) -o apertium-combiner $(LDFLAGS) $^ $(LDFLAGS) $(LDADD)

%.o: %.cc
	$(CXX) -c $< $(CFLAGS) -o $@

.PHONY: apertium-combiner apertium-collector clean graph

clean:
	rm -rf combiner apertium-collector *.dSYM *~ *.o

graph: 
	dot -Tpdf graph.viz > graph.pdf
