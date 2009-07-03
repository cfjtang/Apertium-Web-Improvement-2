CXX=g++
CFLAGS=-Wall -DDEBUG -g

all: aligner

aligner: aligner.o alignment.o
	$(CXX) -o aligner $^

%.o: %.cc
	$(CXX) -c $< $(CFLAGS) -o $@

.PHONY: aligner clean

clean:
	rm -rf *.dSYM *~ *.o
