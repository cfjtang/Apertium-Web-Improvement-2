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

	UNCASEDA=`echo $TR | python2.4 -c "import sys, codecs; sys.stdout = codecs.getwriter('utf-8')(sys.stdout); sys.stdin = codecs.getreader('utf-8')(sys.stdin); print sys.stdin.read().lower();"`;
	UNCASEDB=`echo $TL | python2.4 -c "import sys, codecs; sys.stdout = codecs.getwriter('utf-8')(sys.stdout); sys.stdin = codecs.getreader('utf-8')(sys.stdin); print sys.stdin.read().lower();"`;

	if [[ `echo $UNCASEDA` != `echo $UNCASEDB` ]]; then 
#		echo -e $mode"\t "$SL"\n☔\t-$TL\n\t+ "$TR"\n";
		echo -e $mode"\t "$SL"\n \t-$TL\n\t+ "$TR"\n";
	else
#		echo -e $mode"\t "$SL"\n☺\t  "$TR"\n";
		echo -e $mode"\t "$SL"\nWORKS\t  "$TR"\n";
	fi

done
