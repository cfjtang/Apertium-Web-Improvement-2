CXX=g++
CFLAGS=-Wall -DDEBUG -g

all: aligner apertium-collector

apertium-collector: collector.o apertium_collector.o
	$(CXX) -Wall -o apertium-collector $^

aligner: aligner.o alignment.o hypotheses.o
	$(CXX) -o aligner $^

%.o: %.cc
	$(CXX) -c $< $(CFLAGS) -o $@

.PHONY: aligner clean

clean:
	rm -rf aligner *.dSYM *~ *.o
