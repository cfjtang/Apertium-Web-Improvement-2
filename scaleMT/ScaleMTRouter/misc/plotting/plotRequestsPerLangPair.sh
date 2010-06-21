# /bin/bash

cat $1 |cut -f 3,8 -d " " | grep -e ' 200' | cut -f 1 -d " " | sort | uniq -c | sed 's/^ *//g' | sort -g -r > /tmp/plota

#lineNumber=0
#rm /tmp/plotb
#cat /tmp/plota | while myLine=`line`
#do
#	lineNumber=`expr $lineNumber + 1`;
#	echo "$lineNumber $myLine" >>/tmp/plotb
#done;

gnuplot< plotRequestsPerLangPair.gnuplot
