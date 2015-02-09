#!/bin/bash


TMP=/tmp/da-sv
CRP=corpa/da.crp.txt
mkdir -p testcache
NOVA=testcache/corpus_da-sv_nova_traduko.txt
ORIGINA=testcache/corpus_da-sv_origina_traduko.txt

if [ ! -e $CRP ]
then
	echo Elpakas $CRP
	bunzip2 -c corpa/da_europarl.crp.txt.bz2 | nl -s ". " > $CRP
fi

echo "diff -w $ORIGINA $NOVA | grep -r '[<>]' > $TMP-crpdiff.txt && for i in \`cut -c3-8 $TMP-crpdiff.txt | sort -un\`; do echo  --- \$i ---; grep -r \"^ *\$i\\.\" $CRP; grep -r \"^. *\$i\\.\" $TMP-crpdiff.txt; done | less"

echo "cat $CRP | apertium -d . da-sv"
make -s -j 3 && cat $CRP | apertium -d . da-sv > $NOVA || exit
echo
#grep '#' $NOVA && echo -e "^ Estis mankoj en la cellingva dix, montrata supre ^\n"
#grep '@' $NOVA && echo -e "^ Estis mankoj en la dulingva dix, montrata supre ^\n"

grep '#' $NOVA | head -10 && echo -e "^ Estis mankoj en la cellingva dix, montrata supre ^\n"
grep '@' $NOVA | head -10 && echo -e "^ Estis mankoj en la dulingva dix, montrata supre ^\n"


if [ -e $ORIGINA ]
then
	diff -w $ORIGINA $NOVA | grep -r '[<>]' | head -5000 > $TMP-crpdiff.txt && for i in `cut -c3-8 $TMP-crpdiff.txt | sort -un`; do echo  --- $i ---; grep -r "^ *$i\." $CRP; grep -r "^. *$i\." $TMP-crpdiff.txt; done > testcorpus_da-sv.txt
	echo "Estis `cut -c3-8 $TMP-crpdiff.txt | sort -un | wc -l` diferenco(j) - rigardu en: testcorpus_da-sv.txt"
	echo
fi

echo "Se vi volas uzi la nunan version por kompari venontajn versiojn, voku nun:"
echo "mv $NOVA $ORIGINA"
echo

