CXX=g++
INCLUDE=-I/home/fran/statmt/mosesdecoder/moses/src -I/home/fran/statmt/irstlm/include
LDADD=-lmoses -lz -lirstlm
LDFLAGS=-L/home/fran/statmt/mosesdecoder/moses/src -L/home/fran/statmt/irstlm/lib/x86_64-pc-linux-gnu 
CFLAGS=-Wall -DDEBUG -g $(INCLUDE)

all: aligner apertium-collector

apertium-collector: collector.o apertium_collector.o moses_collector.o
	$(CXX) -Wall -o apertium-collector $^ $(LDFLAGS) $(LDADD)

aligner: aligner.o alignment.o hypotheses.o
	$(CXX) -o aligner $^

%.o: %.cc
	$(CXX) -c $< $(CFLAGS) -o $@

.PHONY: aligner apertium-collector clean 

clean:
	rm -rf aligner apertium-collector *.dSYM *~ *.o
