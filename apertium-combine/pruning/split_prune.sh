#!/bin/bash
div=30000
exe=pruning/fpruning.py
#amount=`wc -l $1`
#amount=`echo $amount | sed 's/[^0-9]*//g'`
split -l $div $1 chunk
#chunks=$(($(($amount / $div)) + 1))
#echo ">>>" $amount "lines has been divided in" $chunks "chunks"
count=`ls | grep chunk | wc -w`
echo ">>>" $count "chunks created"
for f in `ls | grep chunk`
do
    $exe -i $f -o _$f
done
cat _chunk* > pruned_phrase-table

