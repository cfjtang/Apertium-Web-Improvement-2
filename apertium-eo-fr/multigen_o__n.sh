#!/bin/bash

if [ $# -lt 1 ]; then
	echo 'Usage: sh multigen_o__n.sh <file>';
	exit;
fi

if [ ! -f $1 ]; then
	exit;	
fi

echo '  <section id="multiwords" type="standard">';
for word in `cat $1 | sed 's/ /<b\/>/g'`; do 
	lemma=`echo $word | sed 's/<b\/>/ /g'`;
	echo '<e lm="'$lemma'"><p><l>'$word'</l><r>'$word'<s n="n"/><s n="sg"/><s n="nom"/></r></p></e>';
	msa=`echo $word | sed 's/<b\/>/n<b\/>/g' | sed 's/$/n/g'`;
	echo '<e lm="'$lemma'"><p><l>'$msa'</l><r>'$word'<s n="n"/><s n="sg"/><s n="acc"/></r></p></e>';
	mpn=`echo $word | sed 's/<b\/>/j<b\/>/g' | sed 's/$/j/g'`;
	echo '<e lm="'$lemma'"><p><l>'$mpn'</l><r>'$word'<s n="n"/><s n="pl"/><s n="nom"/></r></p></e>';
	mpa=`echo $word | sed 's/<b\/>/jn<b\/>/g' | sed 's/$/jn/g'`;
	echo '<e lm="'$lemma'"><p><l>'$mpa'</l><r>'$word'<s n="n"/><s n="pl"/><s n="acc"/></r></p></e>';
done
echo '  </section>';
