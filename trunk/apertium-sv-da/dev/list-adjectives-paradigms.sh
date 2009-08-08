

SVLIST=`cat apertium-sv-da.sv.dix | grep '<e lm.*__adj' | sed 's/"> *<i>.*<\/i><par n="/; /g' | sed 's/^<e lm="//g' | sed 's/"\/><\/e>//g' | sed 's/ //g'`;

for line in $SVLIST; do
	PARDEF=`echo $line | cut -f2 -d';'`;
	LEMMA=`echo $line | cut -f1 -d';'`;

	COUNT=`cat apertium-sv-da.sv-da.dix | grep '>'$LEMMA'<s n="adj"/>' | grep -v '<e r' | wc -l`;


	if [ $COUNT -eq 1 ]; then 

		DALEMMA=`cat apertium-sv-da.sv-da.dix | grep '>'$LEMMA'<s n="adj"/>' | grep -v '<e r' | sed 's/<r>/@/g'  | cut -f2 -d'@' | cut -f1 -d'<'`; 
		DAPARDEF=`cat apertium-sv-da.da.dix | grep '<e lm="'$DALEMMA'".*__adj' | sed 's/"> *<i>.*<\/i><par n="/; /g' | sed 's/^<e lm="//g' | sed 's/"\/><\/e>//g' | sed 's/ //g' | cut -f2 -d';'`;



		echo $LEMMA"; "$PARDEF"; "$DALEMMA"; "$DAPARDEF;
	fi
done
