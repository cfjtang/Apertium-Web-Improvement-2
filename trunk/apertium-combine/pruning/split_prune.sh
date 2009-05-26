#!/bin/bash
div=30000
exe=pruning/pruning
#amount=`wc -l $1`
#amount=`echo $amount | sed 's/[^0-9]*//g'`
split -a 4 -l $div $1 chunk
#chunks=$(($(($amount / $div)) + 1))
#echo ">>>" $amount "lines has been divided in" $chunks "chunks"
count=`ls | grep chunk | wc -w`
echo ">>>" $count "chunks created"
for f in `ls | grep chunk`
do
    $exe $f _$f
done
cat _chunk* > pruned_phrase-table

