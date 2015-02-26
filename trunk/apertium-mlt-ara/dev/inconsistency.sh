#!/bin/bash

TMPDIR=/tmp

# Use the first one for a quick random sample testvoc, second for the full thing
#maybe-sample () { N=10000; echo "($0: only running on a random sample of $N entries)" >&2; shuf | head "-$N"; }
maybe-sample () { cat; }

expansion-to-pretransfer () {
    grep -v ':<:' | sort -u | maybe-sample | sed 's/:>:/:/g' | cut -f2 -d':' | sed 's%\/%\\/%g' | sed 's/^/^/' | sed 's/$/$ ^.<sent><clb>$/'
}


if [[ $1 = "ar-mt" ]]; then

lt-expand ../apertium-mt-ar.ar.dix | expansion-to-pretransfer | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-mt-ar.ar-mt.t1x  ../ar-mt.t1x.bin  ../ar-mt.autobil.bin | tee $TMPDIR/tmp_testvoc2.txt | 
        lt-proc -d ../ar-mt.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'

elif [[ $1 = "mt-ar" ]]; then

lt-expand ../apertium-mt-ar.mt.dix | expansion-to-pretransfer | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-mt-ar.mt-ar.t1x  ../mt-ar.t1x.bin ../mt-ar.autobil.bin | apertium-interchunk ../apertium-mt-ar.mt-ar.t2x ../mt-ar.t2x.bin | apertium-postchunk ../apertium-mt-ar.mt-ar.t3x ../mt-ar.t3x.bin | tee $TMPDIR/tmp_testvoc2.txt |
        lt-proc -d ../mt-ar.autogen.bin > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'

else
	echo "./inconsistency.sh <direction>";
fi
