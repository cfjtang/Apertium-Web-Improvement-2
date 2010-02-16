# /bin/bash

cat $1 | cut -f 5 -d " "| sort | uniq -c | sed 's/^ *//g' | sort > /tmp/plota

#lineNumber=0
#rm /tmp/plotb
#cat /tmp/plota | while myLine=`line`
#do
#	lineNumber=`expr $lineNumber + 1`;
#	echo "$lineNumber $myLine" >>/tmp/plotb
#done;

gnuplot< plotRequestsPerUser.gnuplot
