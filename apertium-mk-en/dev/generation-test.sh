#!/bin/bash

if [[ $# -lt 1 ]]; then
	echo "Not enough arguments to generation-test.sh";
	echo "bash generation-test.sh <corpus>";
	exit;
fi

if [[ $1 == "-r" ]]; then
	if [[ $# -lt 2 ]]; then 
		echo $#;
		echo "Not enough arguments to generation-test.sh -r";
		echo "bash generation-test.sh -r <corpus>";
		exit;
	fi
	args=("$@")
	echo "Corpus in: "`dirname $2`;
	echo -n "Processing corpus for generation test... ";
	rm -f /tmp/mk-en.corpus.txt
	for i in `seq 1 $#`; do 
		if [[ ${args[$i]} != "" && -f ${args[$i]} ]]; then 
			cat ${args[$i]} >> /tmp/mk-en.corpus.txt
		fi
	done
	echo "done.";
	echo -n "Translating corpus for generation test (this could take some time)... ";
	cat /tmp/mk-en.corpus.txt | apertium -d ../ mk-en-postchunk | sed 's/\$\W*\^/$\n^/g' > /tmp/mk-en.gentest.postchunk
	echo "done.";
fi

if [[ ! -f /tmp/mk-en.gentest.postchunk ]]; then
	echo "Something went wrong in processing the corpus, you have no output file.";
	echo "Try running:"
	echo "   sh generation-test.sh -r <corpus>";
	exit;
fi

cat /tmp/mk-en.gentest.postchunk  | sed 's/^ //g' | grep -v -e '@' -e '*' -e '[0-9]<num>' -e '#}' -e '#{' | sed 's/\$>/$/g' | sed 's/^\W*\^/^/g' | sort -f | uniq -c | sort -gr > /tmp/mk-en.gentest.stripped
cat /tmp/mk-en.gentest.stripped | grep -v '\^\W<' | lt-proc -d ../mk-en.autogen.bin > /tmp/mk-en.gentest.surface
cat /tmp/mk-en.gentest.stripped | grep -v '\^\W<'  | sed 's/^ *[0-9]* \^/^/g' > /tmp/mk-en.gentest.nofreq
paste /tmp/mk-en.gentest.surface /tmp/mk-en.gentest.nofreq  > /tmp/mk-en.generation.errors.txt
cat /tmp/mk-en.generation.errors.txt  | grep -v '#' | grep '\/' > /tmp/mk-en.multiform
cat /tmp/mk-en.generation.errors.txt  | grep '[0-9] #.*\/' > /tmp/mk-en.multibidix 
cat /tmp/mk-en.generation.errors.txt  | grep '[0-9] #' | grep -v '\/' > /tmp/mk-en.tagmismatch 

echo "";
echo "===============================================================================";
echo "Multiple surface forms for a single lexical form";
echo "===============================================================================";
cat /tmp/mk-en.multiform

echo "";
echo "===============================================================================";
echo "Multiple bidix entries for a single source language lexical form";
echo "===============================================================================";
cat /tmp/mk-en.multibidix

echo "";
echo "===============================================================================";
echo "Tag mismatch between transfer and generation";
echo "===============================================================================";
cat /tmp/mk-en.tagmismatch

echo "";
echo "===============================================================================";
echo "Summary";
echo "===============================================================================";
wc -l /tmp/mk-en.multiform /tmp/mk-en.multibidix /tmp/mk-en.tagmismatch
