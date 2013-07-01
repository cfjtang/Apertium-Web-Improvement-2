#!/bin/bash

# See http://wiki.apertium.org/wiki/Corpus_test
#
# To create initial "original" translation, run this:
#make && cat corpa/corpus.kaz.txt | apertium -d . kaz-tat > corpa/origina_traduko.txt

cat corpa/corpus.kaz.txt | apertium -d . kaz-tat > corpa/nova_traduko.txt &&

diff -U0 corpa/origina_traduko.txt corpa/nova_traduko.txt | dwdiff -c --diff-input | more
