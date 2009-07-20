CXX=g++
INCLUDE=-I/home/fran/statmt/mosesdecoder/moses/src
LDFLAGS=-L/home/fran/statmt/mosesdecoder/moses/src 
LDADD=-lmoses
CFLAGS=-Wall -DDEBUG -g $(INCLUDE)

all: aligner apertium-collector

apertium-collector: collector.o apertium_collector.o moses_collector.o
	$(CXX) $(LDFLAGS) $(LDADD) -Wall -o apertium-collector $^

aligner: aligner.o alignment.o hypotheses.o
	$(CXX) -o aligner $^

%.o: %.cc
	$(CXX) $(LDFLAGS) -c $< $(CFLAGS) -o $@

.PHONY: aligner apertium-collector clean 

clean:
	rm -rf aligner apertium-collector *.dSYM *~ *.o
