TMPDIR=/tmp
lt-expand ../apertium-sl-mk.sl.dix | grep -v '<prn><enc>' | grep -e ':<:' -e '\w:\w' | sed 's/:<:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-sl-mk.sl-mk.t1x  ../sl-mk.t1x.bin  ../sl-mk.autobil.bin | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../sl-mk.autogen.bin  | sed 's/ \.//g' > $TMPDIR/tmp_testvoc3.txt
	lt-proc -d ../mk-sl.autogen.bin $TMPDIR/tmp_testvoc1.txt | sed 's/ \.//g'  > $TMPDIR/tmp_testvoc0.txt
paste -d _ $TMPDIR/tmp_testvoc0.txt $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/ ------>  /g'

