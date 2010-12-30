#!/bin/bash

PREF=/tmp/kompletigu_ca-eo

 lt-expand apertium-eo-ca.ca.dix |grep -v REGEXP | grep -e ':>:' -e '\w:\w' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' | uniq -u | sed 's/^/^/g' | sed 's/$/$^;<sent>$/g' | 
#	head -1000 |
        apertium-pretransfer|
#        apertium-transfer-j ca-eo.t1x.class ca-eo.t1x.bin ca-eo.autobil.bin |
        apertium-transfer apertium-eo-ca.ca-eo.t1x ca-eo.t1x.bin ca-eo.autobil.bin |
        apertium-interchunk apertium-eo-ca.ca-eo.t2x ca-eo.t2x.bin |
	apertium-postchunk apertium-eo-ca.ca-eo.t3x ca-eo.t3x.bin | tee ${PREF}2.txt | 
	grep -v '@' | grep -v '/' | 
        lt-proc -d ca-eo.autogen.bin >  ${PREF}3.txt

#paste -d _  ${PREF}1.txt  ${PREF}2.txt  ${PREF}3.txt | sed 's/\^;<sent>\$//g' | sed 's/ \.//g' | sed 's/_/   ---------> /g' > $0.txt
#grep '@' ${PREF}2.txt | grep -v ' ' | sed 's/\^;<sent>\$//g' | sed 's/ \.//g' > ${PREF}2x.txt
#grep '#' ${PREF}3.txt | grep -v ' ' | sed 's/\^;<sent>\$//g' | sed 's/ \.//g' > ${PREF}3x.txt

grep '@' ${PREF}2.txt | grep -v ' ' | sed 's/\^;<sent>\$//g' | sed 's/@//g;s/\\//g;s/\^//g;s/\$//g' > mankas-eo-ca.txt
grep '#' ${PREF}3.txt | grep -v ' ' | sed 's/#//g;s/;//g;s/\\//g' > mankas-eo.txt

cat mankas-eo-ca.txt | sed 's/<np></<np_'/ | sed 's/<n></<n_'/ | sed 's/>/>\t/g' | cut -f1 | sed 's/<np_/<np></' | sed 's/<n_/<n><'/ | sort -u > mankas-eo-ca.txt2
cat mankas-eo.txt | sed 's/<np></<np_'/ | sed 's/<n><acr>/<n_acr>'/ | sed 's/>/>\t/g' | cut -f1 | sed 's/<n_acr>/<n><acr>/' | sed 's/<np_/<np></' | sort -u > mankas-eo.txt2
./krei_monodix.pl mankas-eo.txt2 >krei_monodix.txt 2>krei_monodix.err
