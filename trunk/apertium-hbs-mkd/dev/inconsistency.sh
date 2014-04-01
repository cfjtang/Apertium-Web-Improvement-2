TMPDIR=/tmp

if [[ $1 = "mkd-hbs" ]]; then

lt-expand ../apertium-hbs-mkd.mkd.dix | grep -v '<prn><enc>' | grep -v 'REGEX' | grep -e ':>:' -e '\w:\w' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-hbs-mkd.mkd-hbs.t1x  ../mkd-hbs.t1x.bin  ../mkd-hbs.autobil.bin |
        apertium-interchunk ../apertium-hbs-mkd.mkd-hbs.t2x  ../mkd-hbs.t2x.bin |
        apertium-postchunk ../apertium-hbs-mkd.mkd-hbs.t3x  ../mkd-hbs.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../mkd-hbs.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'

elif [[ $1 = "hbs-mkd" ]]; then

lt-expand ../apertium-hbs-mkd.hbs.dix | grep -v '<prn><enc>' | grep -v 'REGEX' | grep -e ':>:' -e '\w:\w' | sed 's/:>:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-hbs-mkd.hbs-mkd.t1x  ../hbs-mkd.t1x.bin  ../hbs-mkd.autobil.bin |
        apertium-interchunk ../apertium-hbs-mkd.hbs-mkd.t2x  ../hbs-mkd.t2x.bin |
        apertium-postchunk ../apertium-hbs-mkd.hbs-mkd.t3x  ../hbs-mkd.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../hbs-mkd.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'


else
	echo "bash inconsistency.sh <direction>";
fi
