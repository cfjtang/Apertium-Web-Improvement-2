TMPDIR=/tmp
lt-expand ../apertium-sv-da.sv.dix | grep -v '<prn><enc>' | grep -e ':<:' -e '\w:\w' | sed 's/:<:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-sv-da.sv-da.t1x  ../sv-da.t1x.bin  ../sv-da.autobil.bin | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../sv-da.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'
