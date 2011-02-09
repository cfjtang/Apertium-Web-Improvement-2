#!/bin/bash

PREF=/tmp/kompletigu_fr-eo

make

lt-expand apertium-eo-fr.fr.dix |grep -v REGEXP | grep -e ':>:' -e '\w:\w' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' | uniq -u | sed 's/^/^/g' | sed 's/$/$^;<sent>$/g' | 
        apertium-pretransfer|
        apertium-transfer apertium-eo-fr.fr-eo.t1x fr-eo.t1x.bin fr-eo.autobil.bin |
        apertium-interchunk apertium-eo-fr.fr-eo.t2x fr-eo.t2x.bin |
        apertium-interchunk apertium-eo-fr.fr-eo.post_t2x fr-eo.post_t2x.bin |
	apertium-postchunk apertium-eo-fr.fr-eo.t3x fr-eo.t3x.bin | tee ${PREF}2.txt | 
	grep -v '@' | grep -v '/' | 
        lt-proc -d fr-eo.autogen.bin >  ${PREF}3.txt

grep '@' ${PREF}2.txt | grep -v ' ' | sed 's/\^;<sent>\$//g' | sed 's/@//g;s/\\//g;s/\^//g;s/\$//g' > mankas-eo-fr.txt
grep '#' ${PREF}3.txt | grep -v ' ' | sed 's/#//g;s/;//g;s/\\//g' > mankas-eo.txt

cat mankas-eo-fr.txt | sed 's/<np></<np_'/ | sed 's/<n></<n_'/ | sed 's/>/>\t/g' | cut -f1 | sed 's/<np_/<np></' | sed 's/<n_/<n><'/ | sort -u > mankas-eo-fr.txt2
cat mankas-eo.txt | sed 's/<np></<np_'/ | sed 's/<n><acr>/<n_acr>'/ | sed 's/>/>\t/g' | cut -f1 | sed 's/<n_acr>/<n><acr>/' | sed 's/<np_/<np></' | sort -u > mankas-eo.txt2
./krei_monodix.pl mankas-eo.txt2 >krei_monodix.txt 2>krei_monodix.err
