TMPDIR=/tmp

lt-expand ../apertium-br-fr.br.dix | grep -v '<prn><enc>' | grep -e ':<:' -e '\w:\w' | sed 's/:<:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |   sed 's/\//\\\//g'  | sed 's/^/^/g' | sed 's/$/$ ^.\/.<sent>$/g' | sed 's/^\^/^foo\//g' | tee $TMPDIR/tmp_testvoc1.txt |
	cg-proc ../br-fr.rlx.bin |
        apertium-tagger -g ../br-fr.prob  |
        apertium-pretransfer|
        apertium-transfer ../apertium-br-fr.br-fr.t1x  ../br-fr.t1x.bin  ../br-fr.autobil.bin |
        apertium-interchunk ../apertium-br-fr.br-fr.t2x  ../br-fr.t2x.bin |
        apertium-postchunk ../apertium-br-fr.br-fr.t3x  ../br-fr.t3x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../br-fr.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'

