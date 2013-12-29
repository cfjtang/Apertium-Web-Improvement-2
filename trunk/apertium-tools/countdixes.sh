#!/bin/bash

# takes a directory with language modules as an argument

#echo "counting $1";

for i in $1/apertium-*; do
	dir=`echo $i | sed -r 's/.*(apertium.*)/\\1/'`;
	lg=`echo $dir | sed 's/apertium-//'`;
	dix=$i/$dir.$lg.dix;
	lexc=$i/$dir.$lg.lexc;
	if [[ -f $dix ]]; then
		echo == $lg ==
		python3 $APERTIUMPATH/trunk/apertium-tools/dixcounter.py $dix
	elif [[ -f $lexc ]]; then
		echo == $lg ==
		bash $APERTIUMPATH/trunk/apertium-tools/lexccounter.sh $lexc
	fi;
done;
