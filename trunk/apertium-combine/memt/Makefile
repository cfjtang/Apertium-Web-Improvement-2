CXX=g++
INCLUDE=-I/Users/snippy/apertium/include
LDADD=-lmoses -lz -lirstlm
LDFLAGS=-L/Users/snippy/apertium/lib
CFLAGS=-Wall -DDEBUG -g $(INCLUDE)

all: main apertium-collector

apertium-collector: collector.o apertium_collector.o moses_collector.o
	$(CXX) -Wall -o apertium-collector $^ $(LDFLAGS) $(LDADD)

main: main.o alignment.o hypotheses.o ranker.o
	$(CXX) -o aligner $^

%.o: %.cc
	$(CXX) -c $< $(CFLAGS) -o $@

.PHONY: main apertium-collector clean graph

clean:
	rm -rf aligner apertium-collector *.dSYM *~ *.o

graph: 
	dot -Tpdf graph.viz > graph.pdf
