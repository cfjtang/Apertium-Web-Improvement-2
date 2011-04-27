TMPDIR=/tmp

if [[ $1 = "it-es" ]]; then

lt-expand ../apertium-es-it.it.dix | grep -v REGEX | grep -v '<prn><enc>' | grep -v ':<:'  | sed 's/:>:/%/g' | sed 's/:<:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-es-it.it-es.t1x  ../it-es.t1x.bin  ../it-es.autobil.bin |
        apertium-interchunk ../apertium-es-it.it-es.t2x  ../it-es.t2x.bin |
        apertium-postchunk ../apertium-es-it.it-es.t3x  ../it-es.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../it-es.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'

elif [[ $1 = "es-it" ]]; then

lt-expand ../apertium-es-it.es.dix | grep -v REGEX | grep -v '<prn><enc>' | grep -v ':<:' | sed 's/:>:/%/g' | sed 's/:<:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-es-it.es-it.t1x  ../es-it.t1x.bin  ../es-it.autobil.bin |
        apertium-interchunk ../apertium-es-it.es-it.t2x  ../es-it.t2x.bin |
        apertium-postchunk ../apertium-es-it.es-it.t3x  ../es-it.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../es-it.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'


else
	echo "sh inconsistency.sh <direction>";
fi
