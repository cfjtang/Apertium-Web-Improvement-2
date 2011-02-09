TMPDIR=/tmp
lt-expand  apertium-eo-fr.fr.dix | grep -v '<prn><enc>' | grep -e ':<:' -e '\w:\w' | sed 's/:<:/%/g' | sed 's/:/%/g' | cut -f2 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee "$TMPDIR/tmp_testvoc1.txt" |
        apertium-pretransfer|
        apertium-transfer apertium-eo-fr.fr-eo.t1x  fr-eo.t1x.bin  fr-eo.autobil.bin |
        apertium-interchunk apertium-eo-fr.fr-eo.t2x  fr-eo.t2x.bin |
        apertium-interchunk apertium-eo-fr.fr-eo.post_t2x  fr-eo.post_t2x.bin |
        apertium-postchunk apertium-eo-fr.fr-eo.t3x  fr-eo.t3x.bin  | tee "$TMPDIR/tmp_testvoc2.txt" |
        lt-proc -d fr-eo.autogen.bin > "$TMPDIR/tmp_testvoc3.txt"
paste -d _ "$TMPDIR/tmp_testvoc1.txt" "$TMPDIR/tmp_testvoc2.txt" "$TMPDIR/tmp_testvoc3.txt" | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'
