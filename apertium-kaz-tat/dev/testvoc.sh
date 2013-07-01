if [ -z $TMPDIR ]; then
	TMPDIR="/tmp"
fi

bash inconsistency.sh kaz-tat > $TMPDIR/testvoc.kaz-tat; bash inconsistency-summary.sh $TMPDIR/testvoc.kaz-tat kaz-tat
bash inconsistency.sh tat-kaz > $TMPDIR/testvoc.tat-kaz; bash inconsistency-summary.sh $TMPDIR/testvoc.tat-kaz tat-kaz
