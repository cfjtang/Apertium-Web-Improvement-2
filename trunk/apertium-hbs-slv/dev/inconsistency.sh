TMPDIR=/tmp
VAR=slv-hbs_HR

if [[ $1 = "slv-hbs_HR" ]]; then

lt-expand ../apertium-hbs-slv.slv.dix | grep -v '<prn><enc>' | grep -v 'REGEX' | grep -e ':>:' -e '\w:\w' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
	lt-proc -b ../slv-hbs.autobil.bin | 
        apertium-transfer -b ../apertium-hbs-slv.slv-hbs.t1x  ../slv-hbs.t1x.bin |
        apertium-interchunk ../apertium-hbs-slv.slv-hbs.t2x  ../slv-hbs.t2x.bin |
        apertium-postchunk ../apertium-hbs-slv.slv-hbs.t3x  ../slv-hbs.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../slv-hbs_HR.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'

elif [[ $1 = "slv-hbs_SR" ]]; then

lt-expand ../apertium-hbs-slv.slv.dix | grep -v '<prn><enc>' | grep -v 'REGEX' | grep -e ':>:' -e '\w:\w' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
	lt-proc -b ../slv-hbs.autobil.bin | 
        apertium-transfer -b ../apertium-hbs-slv.slv-hbs.t1x  ../slv-hbs.t1x.bin |
        apertium-interchunk ../apertium-hbs-slv.slv-hbs.t2x  ../slv-hbs.t2x.bin |
        apertium-postchunk ../apertium-hbs-slv.slv-hbs.t3x  ../slv-hbs.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../slv-hbs_SR.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'


elif [[ $1 = "hbs-slv" ]]; then

lt-expand ../apertium-hbs-slv.hbs.dix | grep -v '<prn><enc>' | grep -v 'REGEX' | grep -e ':>:' -e '\w:\w' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
	lt-proc -b ../hbs-slv.autobil.bin | 
        apertium-transfer -b ../apertium-hbs-slv.hbs-slv.t1x  ../hbs-slv.t1x.bin |
        apertium-interchunk ../apertium-hbs-slv.hbs-slv.t2x  ../hbs-slv.t2x.bin |
        apertium-postchunk ../apertium-hbs-slv.hbs-slv.t3x  ../hbs-slv.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../hbs-slv.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'


else
	echo "bash inconsistency.sh <direction>";
fi
