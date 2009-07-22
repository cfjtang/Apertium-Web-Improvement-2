CXX=g++
INCLUDE=-I/Users/snippy/apertium/include
LDADD=-lmoses -lz -lirstlm
LDFLAGS=-L/Users/snippy/apertium/lib
CFLAGS=-Wall -DDEBUG -g $(INCLUDE)

all: apertium-combiner apertium-collector

apertium-collector: collector.o apertium_collector.o moses_collector.o
	$(CXX) -Wall -o apertium-collector $^ $(LDFLAGS) $(LDADD)

apertium-combiner: apertium_combiner.o alignment.o hypotheses.o ranker.o
	$(CXX) -o combiner $^

%.o: %.cc
	$(CXX) -c $< $(CFLAGS) -o $@

.PHONY: apertium-combiner apertium-collector clean graph

clean:
	rm -rf combiner apertium-collector *.dSYM *~ *.o

graph: 
	dot -Tpdf graph.viz > graph.pdf
