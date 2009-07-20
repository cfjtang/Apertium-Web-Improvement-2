CXX=g++
CFLAGS=-Wall -DDEBUG -g

all: aligner collector

collector: collector.o
	$(CXX) -o collector $^

aligner: aligner.o alignment.o hypotheses.o
	$(CXX) -o aligner $^

%.o: %.cc
	$(CXX) -c $< $(CFLAGS) -o $@

.PHONY: aligner clean

clean:
	rm -rf aligner *.dSYM *~ *.o
