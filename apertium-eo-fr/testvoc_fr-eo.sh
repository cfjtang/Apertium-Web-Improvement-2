#!/bin/bash

PREF=/tmp/testvoc_fr-eo

#lt-expand apertium-eo-en.en.dix | grep -e ':<:' -e '\w:\w' | sed 's/:<:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^;<sent>$/g' | tee tmp_testvoc1.txt |
 lt-expand apertium-eo-fr.fr.dix |grep -v REGEXP | grep -e ':>:' -e '\w:\w' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^;<sent>$/g' | tee ${PREF}1.txt |
        apertium-pretransfer|
        apertium-transfer-j fr-eo.t1x.class fr-eo.t1x.bin fr-eo.autobil.bin |
        apertium-interchunk apertium-eo-fr.fr-eo.t2x fr-eo.t2x.bin |
	apertium-postchunk apertium-eo-fr.fr-eo.t3x fr-eo.t3x.bin | tee  ${PREF}2.txt |
        lt-proc -d fr-eo.autogen.bin >  ${PREF}3.txt

paste -d _  ${PREF}1.txt  ${PREF}2.txt  ${PREF}3.txt | sed 's/\^;<sent>\$//g' | sed 's/ \.//g' | sed 's/_/   ---------> /g' > $0.txt

grep '@' ${PREF}2.txt | head
grep '#' ${PREF}3.txt | head
echo "Rezulto estas en: $0.txt"
