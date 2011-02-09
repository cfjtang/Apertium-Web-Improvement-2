#!/bin/bash

if [ $# -lt 1 ]; then
	echo 'Usage: sh multigen_oj__loc.sh <file>';
	exit;
fi

if [ ! -f $1 ]; then
	exit;	
fi

echo '  <section id="multiwords" type="standard">';
for word in `cat $1 | sed 's/ /<b\/>/g'`; do 
	lemma=`echo $word | sed 's/<b\/>/ /g'`;
	echo '<e lm="'$lemma'"><p><l>'$word'</l><r>'$word'<s n="np"/><s n="loc"/><s n="pl"/><s n="nom"/></r></p></e>';
	acc=`echo $word | sed 's/<b\/>/n<b\/>/g' | sed 's/$/n/g'`;
	echo '<e lm="'$lemma'"><p><l>'$acc'</l><r>'$word'<s n="np"/><s n="loc"/><s n="pl"/><s n="acc"/></r></p></e>';
done
echo '  </section>';
