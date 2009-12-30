#!/bin/bash

DUMP=$1
DIR=en-ca
ROOT=/home/fran/source/apertium/trunk/apertium-en-ca/
ANALYSER=$ROOT"/"$DIR.automorf.bin
TAGGER=$ROOT"/"$DIR.prob

if [ $# -lt 1 ]; then
	echo 'process-wikipedia.sh <dump file>'
	exit;
fi

bzcat $DUMP |\
grep '^[A-ZÁÉÍÓÚ]' |\
grep -v 'Fitxer' |\
sed 's/&quot;/"/g' |\
sed 's/&gt;/>/g' |\
sed 's/&amp;gt;/>/g' |\
sed 's/&lt;/</g' |\
sed 's/&amp;lt;/</g' |\
sed 's/&amp;/\&/g' |\
sed 's/&nbsp;/ /g' |\
sed 's/&ndash;/–/g' |\
sed 's/{{mdash}}/—/g' |\
sed 's/<nowiki>//g' | sed 's/<\/nowiki>//g' | sed 's/<br \?\/>//g' |\
sed 's/<\/\?\(tt\|small\|u\|p\|s\|SUB\|Sub\|big\|br\|cite\|code\|nowiki\|sub\|sup\|SUP\|Sup\|var\|BR\|em\|blockquote\)>//g' |\
sed "s/'''//g" |\
sed "s/''//g" |\
grep -v -e '</ref>' -e '<ref' |\
grep -v -e '</math>' -e '<math' |\
grep -v -e '<!--' -e '-->' |\
grep '\[\[.*|' |\
sed "s/\[\[[ \'0-9A-ZÀÈÌÒÙÁÉÍÓÚa-záéíóúàèìòù\-\(\)#·]\+|//g" |\
sed 's/\]\]//g' |\
sed 's/\[\[//g' |\
grep -v -e '|' -e '=' |\
sed 's/$/./g' |\
apertium-destxt |\
lt-proc $ANALYSER |\
apertium-tagger -p -g $TAGGER |\
sed 's/\^\.\/\.<sent>\$ \^[A-ZÀÈÌÒÙÁÉÍÓÚ]/^.\/.<sent>$\n&/g' |\
sed 's/^\^\.\/\.<sent>\$ //g'
