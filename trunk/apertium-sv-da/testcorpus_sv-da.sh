#!/bin/bash


TMP=/tmp/sv-da
CRP=corpa/sv.crp.txt
mkdir -p testcache
NOVA=testcache/corpus_sv-da_nova_traduko.txt
ORIGINA=testcache/corpus_sv-da_origina_traduko.txt

if [ ! -e $CRP ]
then
	echo Elpakas $CRP
	bunzip2 -c corpa/svwiki_180000.crp.txt.bz2 > $CRP
fi

echo "diff -w $ORIGINA $NOVA | grep -r '[<>]' > $TMP-crpdiff.txt && for i in \`cut -c3-8 $TMP-crpdiff.txt | sort -un\`; do echo  --- \$i ---; grep -r \"^ *\$i\\.\" $CRP; grep -r \"^. *\$i\\.\" $TMP-crpdiff.txt; done | less"

echo "cat $CRP | apertium -d . sv-da"
make -s -j 3 && cat $CRP | apertium -d . sv-da > $NOVA || exit
echo
grep '#' $NOVA && echo -e "^ Estis mankoj sv la cellingva dix, montrata supre ^\n"
grep '@' $NOVA && echo -e "^ Estis mankoj sv la dulingva dix, montrata supre ^\n"

if [ -e $ORIGINA ]
then
	diff -w $ORIGINA $NOVA | grep -r '[<>]' > $TMP-crpdiff.txt && for i in `cut -c3-8 $TMP-crpdiff.txt | sort -un`; do echo  --- $i ---; grep -r "^ *$i\." $CRP; grep -r "^. *$i\." $TMP-crpdiff.txt; done > testcorpus_sv-da.txt
	echo "Estis `cut -c3-8 $TMP-crpdiff.txt | sort -un | wc -l` diferenco(j) - rigardu en: testcorpus_sv-da.txt"
	echo
fi

echo "Se vi volas uzi la nunan version por kompari venontajn versiojn, voku nun:"
echo "mv $NOVA $ORIGINA"
echo

