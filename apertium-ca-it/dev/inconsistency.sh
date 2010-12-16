TMPDIR=/tmp

if [[ $1 = "it-ca" ]]; then

lt-expand ../apertium-ca-it.it.dix | grep -v REGEX | grep -v '<prn><enc>' | sed 's/:>:/%/g' | sed 's/:<:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-ca-it.it-ca.t1x  ../it-ca.t1x.bin  ../it-ca.autobil.bin |
        apertium-interchunk ../apertium-ca-it.it-ca.t2x  ../it-ca.t2x.bin |
        apertium-postchunk ../apertium-ca-it.it-ca.t3x  ../it-ca.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../it-ca.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'

elif [[ $1 = "ca-it" ]]; then

lt-expand ../apertium-ca-it.ca.dix | grep -v REGEX | grep -v '<prn><enc>' | sed 's/:>:/%/g' | sed 's/:<:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-ca-it.ca-it.t1x  ../ca-it.t1x.bin  ../ca-it.autobil.bin |
        apertium-interchunk ../apertium-ca-it.ca-it.t2x  ../ca-it.t2x.bin |
        apertium-postchunk ../apertium-ca-it.ca-it.t3x  ../ca-it.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../ca-it.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'


else
	echo "sh inconsistency.sh <direction>";
fi
