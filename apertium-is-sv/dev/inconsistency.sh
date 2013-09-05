TMPDIR=/tmp

if [[ $1 = "sv-is" ]]; then

lt-expand ../apertium-is-sv.sv.dix | grep -v '<prn><enc>' | grep -e ':>:' -e '\w:\w' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-is-sv.sv-is.t1x  ../sv-is.t1x.bin  ../sv-is.autobil.bin |
        apertium-interchunk ../apertium-is-sv.sv-is.t2x  ../sv-is.t2x.bin |
        apertium-postchunk ../apertium-is-sv.sv-is.t3x  ../sv-is.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../sv-is.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'

elif [[ $1 = "is-sv" ]]; then

lt-expand ../apertium-is-sv.is.dix | grep -v '<prn><enc>' | grep -e ':>:' -e '\w:\w' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-is-sv.is-sv.t1x  ../is-sv.t1x.bin  ../is-sv.autobil.bin |
        apertium-interchunk ../apertium-is-sv.is-sv.t2x  ../is-sv.t2x.bin |
        apertium-postchunk ../apertium-is-sv.is-sv.t3x  ../is-sv.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../is-sv.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'


else
	echo "sh inconsistency.sh <direction>";
fi
