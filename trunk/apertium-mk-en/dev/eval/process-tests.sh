#!/bin/bash 

REF=`mktemp`
TST=`mktemp`

cat $1 | grep '^r1' | cut -f2- > $REF
cat $1 | grep '^t1' | cut -f2- > $TST

apertium-eval-translator.pl -r $REF -t $TST
