TMPDIR=/tmp

if [[ $1 = "mk-sh" ]]; then

lt-expand ../apertium-sh-mk.mk.dix | grep -v '<prn><enc>' | grep -v 'REGEX' | grep -e ':>:' -e '\w:\w' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-sh-mk.mk-sh.t1x  ../mk-sh.t1x.bin  ../mk-sh.autobil.bin |
        apertium-interchunk ../apertium-sh-mk.mk-sh.t2x  ../mk-sh.t2x.bin |
        apertium-postchunk ../apertium-sh-mk.mk-sh.t3x  ../mk-sh.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../mk-sh.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'

elif [[ $1 = "sh-mk" ]]; then

lt-expand ../apertium-sh-mk.sh.dix | grep -v '<prn><enc>' | grep -v 'REGEX' | grep -e ':>:' -e '\w:\w' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-sh-mk.sh-mk.t1x  ../sh-mk.t1x.bin  ../sh-mk.autobil.bin |
        apertium-interchunk ../apertium-sh-mk.sh-mk.t2x  ../sh-mk.t2x.bin |
        apertium-postchunk ../apertium-sh-mk.sh-mk.t3x  ../sh-mk.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../sh-mk.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'


else
	echo "bash inconsistency.sh <direction>";
fi
