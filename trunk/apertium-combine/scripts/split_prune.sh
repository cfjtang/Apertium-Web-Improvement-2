#!/bin/bash

# Usage "sh split_prune.sh phrase-table" with the good div and exe
div=30000
exe=/Users/snippy/apertium/svn/apertium/trunk/apertium-combine/pruning/a.out

if [ "$1" ]
then
    echo ">>> Spliting phrase table in chunks"
else 
    echo "Usage: sh split_prune.sh phrase-table"
    exit
fi

#amount=`wc -l $1`
#amount=`echo $amount | sed 's/[^0-9]*//g'`
split -l $div $1 chunk
#chunks=$(($(($amount / $div)) + 1))
#echo ">>>" $amount "lines has been divided in" $chunks "chunks"
count=`ls | grep chunk | wc -w`
echo ">>>" $count "chunks created"
table=`ls | grep chunk`
echo $table
echo `ls | grep chunk`

for f in `ls | grep chunk`
do
    echo ">>> launched $exe on $f"
    $exe $f _$f
done
cat _chunk* > pruned_phrase-table

