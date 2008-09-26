#!/bin/bash

LIST=`wget -O - -q http://wiki.apertium.org/wiki/Traductor_rumano-español/Pruebas_de_regresión | grep '<li>' | sed 's/<.*li>//g' | sed 's/ /_/g'`;

cp *.mode modes/

for LINE in $LIST; do
	dir=`echo $LINE | cut -f2 -d'(' | cut -f1 -d')'`;

	if [ $dir = "es" ]; then
		mode="es-ro";
	elif [ $dir = "ro" ]; then
		mode="ro-es";
	else 
		continue;
	fi

#	echo $LINE;
	SL=`echo $LINE | cut -f2 -d')' | sed 's/<i>//g' | sed 's/<\/i>//g' | cut -f2 -d'*' | sed 's/→/@/g' | cut -f1 -d'@' | sed 's/(note:/@/g' | sed 's/_/ /g'`;
	TL=`echo $LINE | sed 's/(\w\w)//g' | sed 's/<i>//g' | cut -f2 -d'*' | sed 's/<\/i>_→/@/g' | cut -f2 -d'@' | sed 's/_/ /g'`;

	TR=`echo $SL | apertium -d . $mode`;

	if [[ `echo $TR | tr '[A-Z]' '[a-z]'` != `echo $TL | tr '[A-Z]' '[a-z]'` ]]; then 
		echo -e $mode"\t "$SL"\n☔\t-$TL\n\t+ "$TR"\n";
	else
		echo -e $mode"\t "$SL"\n☺\t  "$TR"\n";
	fi

done
