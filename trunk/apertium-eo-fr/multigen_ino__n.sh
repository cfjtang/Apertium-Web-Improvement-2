#!/bin/bash

if [ $# -lt 1 ]; then
	echo 'Usage: sh multigen_ino__n.sh <file>';
	exit;
fi

if [ ! -f $1 ]; then
	exit;	
fi

echo '  <section id="multiwords" type="standard">';
for word in `cat $1 | sed 's/ /<b\/>/g'`; do 
	lemma=`echo $word | sed 's/<b\/>/ /g'`;
	echo '<e lm="'$lemma'"><p><l>'$word'</l><r>'$word'<s n="n"/><s n="m"/><s n="sg"/><s n="nom"/></r></p></e>';
	msa=`echo $word | sed 's/<b\/>/n<b\/>/g' | sed 's/$/n/g'`;
	echo '<e lm="'$lemma'"><p><l>'$msa'</l><r>'$word'<s n="n"/><s n="m"/><s n="sg"/><s n="acc"/></r></p></e>';
	mpn=`echo $word | sed 's/<b\/>/j<b\/>/g' | sed 's/$/j/g'`;
	echo '<e lm="'$lemma'"><p><l>'$mpn'</l><r>'$word'<s n="n"/><s n="m"/><s n="pl"/><s n="nom"/></r></p></e>';
	mpa=`echo $word | sed 's/<b\/>/jn<b\/>/g' | sed 's/$/jn/g'`;
	echo '<e lm="'$lemma'"><p><l>'$mpa'</l><r>'$word'<s n="n"/><s n="m"/><s n="pl"/><s n="acc"/></r></p></e>';
	fsn=`echo $word | sed 's/<b\/>/<b\/>/g' | sed 's/o$/ino/g'`;
	echo '<e lm="'$lemma'"><p><l>'$fsn'</l><r>'$word'<s n="n"/><s n="f"/><s n="sg"/><s n="nom"/></r></p></e>';
	fsa=`echo $word | sed 's/<b\/>/n<b\/>/g' | sed 's/o$/inon/g'`;
	echo '<e lm="'$lemma'"><p><l>'$fsa'</l><r>'$word'<s n="n"/><s n="f"/><s n="sg"/><s n="acc"/></r></p></e>';
	fpn=`echo $word | sed 's/<b\/>/j<b\/>/g' | sed 's/o$/inoj/g'`;
	echo '<e lm="'$lemma'"><p><l>'$fpn'</l><r>'$word'<s n="n"/><s n="f"/><s n="pl"/><s n="nom"/></r></p></e>';
	fpa=`echo $word | sed 's/<b\/>/jn<b\/>/g' | sed 's/o$/inojn/g'`;
	echo '<e lm="'$lemma'"><p><l>'$fpa'</l><r>'$word'<s n="n"/><s n="f"/><s n="pl"/><s n="acc"/></r></p></e>';
done
echo '  </section>';
