if [ -z $TMPDIR ]; then
	TMPDIR="/tmp"
fi

GREP="";
if [[ -n $2 ]]; then
	GREP="grep \"<$2>\"" ;
else
	GREP="grep \"<.*>\"";
fi

echo $GREP

if [[ $1 = "tat-kaz" ]]; then

hfst-fst2strings ../.deps/tat.LR-debug.hfst | sort -u |  sed 's/:/%/g' | cut -f1 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-kaz-tat.tat-kaz.t1x  ../tat-kaz.t1x.bin  ../tat-kaz.autobil.bin |
        apertium-transfer -n ../apertium-kaz-tat.tat-kaz.t2x  ../tat-kaz.t2x.bin  | tee $TMPDIR/tmp_testvoc2.txt |
        hfst-proc -d ../tat-kaz.autogen.hfst > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'

elif [[ $1 = "kaz-tat" ]]; then

hfst-fst2strings ../.deps/kaz.LR-debug.hfst | sort -u | sed 's/:/%/g' | cut -f1 -d'%' |  sed 's/^/^/g' | sed 's/$/$ ^.<sent>$/g' | tee $TMPDIR/tmp_testvoc1.txt |
        apertium-pretransfer|
        apertium-transfer ../apertium-kaz-tat.kaz-tat.t1x  ../kaz-tat.t1x.bin  ../kaz-tat.autobil.bin | 
        apertium-transfer -n ../apertium-kaz-tat.kaz-tat.t2x  ../kaz-tat.t2x.bin | tee $TMPDIR/tmp_testvoc2.txt |
        hfst-proc -d ../kaz-tat.autogen.hfst > $TMPDIR/tmp_testvoc3.txt
paste -d _ $TMPDIR/tmp_testvoc1.txt $TMPDIR/tmp_testvoc2.txt $TMPDIR/tmp_testvoc3.txt | sed 's/\^.<sent>\$//g' | sed 's/_/   --------->  /g'


else
	echo "sh inconsistency.sh <direction>";
fi
