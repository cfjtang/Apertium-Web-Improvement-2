#!/bin/bash 

REF=`mktemp`
TST=`mktemp`

cat $1 | grep '^r1' | cut -f2- > $REF
cat $1 | grep '^t1' | cut -f2- > $TST

apertium-eval-translator -r $REF -t $TST
