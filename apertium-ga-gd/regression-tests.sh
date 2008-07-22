LIST=`wget -O - -q http://wiki.apertium.org/wiki/Scottish_Gaelic_and_Irish/Regression_tests | grep '<li>' | sed 's/<.*li>//g' | sed 's/ /_/g'`;

cp *.mode modes/

for LINE in $LIST; do
	dir=`echo $LINE | cut -f2 -d'(' | cut -f1 -d')'`;

	if [ $dir = "ga" ]; then
		mode="ga-gd";
	elif [ $dir = "gd" ]; then
		mode="gd-ga";
	else 
		continue;
	fi

#	echo $LINE;
	SL=`echo $LINE | cut -f2 -d')' | sed 's/<i>//g' | sed 's/<\/i>//g' | cut -f2 -d'*' | sed 's/→/@/g' | cut -f1 -d'@' | sed 's/(note:/@/g' | sed 's/_/ /g'`;
	TL=`echo $LINE | sed 's/(\w\w)//g' | sed 's/<i>//g' | cut -f2 -d'*' | sed 's/<\/i>_→/@/g' | cut -f2 -d'@' | sed 's/_/ /g'`;

	echo -e $mode"\t "$SL"\n\t-$TL\n\t+ "`echo $SL | apertium -d . $mode`"\n";

done
