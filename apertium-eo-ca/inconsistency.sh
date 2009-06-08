TMPDIR=/tmp
lt-expand apertium-eo-ca.ca.dix | grep -v '<prn><enc>' | grep -e ':<:' -e '\w:\w' | sed 's/:<:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer apertium-eo-ca.ca-eo.t1x  ca-eo.t1x.bin  ca-eo.autobil.bin |
        apertium-interchunk apertium-eo-ca.ca-eo.t2x  ca-eo.t2x.bin |
        apertium-postchunk apertium-eo-ca.ca-eo.t3x  ca-eo.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ca-eo.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'
