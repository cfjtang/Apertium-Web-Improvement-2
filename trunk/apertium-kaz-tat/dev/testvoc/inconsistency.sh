TMPDIR=/tmp

DIR=$1

if [[ $DIR = "tat-kaz" ]]; then

hfst-fst2strings ../../tat-kaz.automorf.hfst | grep -v 'REGEX' | grep -v ':<:' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/$DIR.tmp_testvoc1.txt |
        apertium-pretransfer|
	lt-proc -b ../../tat-kaz.autobil.bin |  tee $TMPDIR/$DIR.tmp_testvoc2.txt |
        apertium-transfer -b ../../apertium-kaz-tat.tat-kaz.t1x  ../../tat-kaz.t1x.bin |
        apertium-transfer -n ../../apertium-kaz-tat.tat-kaz.t2x  ../../tat-kaz.t2x.bin | tee $TMPDIR/$DIR.tmp_testvoc3.txt |
        hfst-proc -d ../../tat-kaz.autogen.hfst > $TMPDIR/$DIR.tmp_testvoc4.txt
paste  $TMPDIR/$DIR.tmp_testvoc1.txt $TMPDIR/$DIR.tmp_testvoc2.txt $TMPDIR/$DIR.tmp_testvoc3.txt $TMPDIR/$DIR.tmp_testvoc4.txt | sed 's/\^.<sent>\$//g' | sed 's/\t/   --------->  /g'

elif [[ $DIR = "kaz-tat" ]]; then

hfst-fst2strings ../../kaz-tat.automorf.hfst | grep -v 'REGEX' | grep -v '\.' | grep -v ':<:' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/$DIR.tmp_testvoc1.txt |
        apertium-pretransfer|
	lt-proc -b ../../kaz-tat.autobil.bin | tee $TMPDIR/$DIR.tmp_testvoc2.txt |
        apertium-transfer -b ../../apertium-kaz-tat.kaz-tat.t1x  ../../kaz-tat.t1x.bin |
        apertium-transfer -n ../../apertium-kaz-tat.kaz-tat.t2x  ../../kaz-tat.t2x.bin | tee $TMPDIR/$DIR.tmp_testvoc3.txt |
        hfst-proc -d ../../kaz-tat.autogen.hfst > $TMPDIR/$DIR.tmp_testvoc4.txt
paste $TMPDIR/$DIR.tmp_testvoc1.txt $TMPDIR/$DIR.tmp_testvoc2.txt $TMPDIR/$DIR.tmp_testvoc3.txt $TMPDIR/$DIR.tmp_testvoc4.txt| sed 's/\^.<sent>\$//g' | sed 's/\t/   --------->  /g'


else
	echo "bash inconsistency.sh <direction>";
fi
