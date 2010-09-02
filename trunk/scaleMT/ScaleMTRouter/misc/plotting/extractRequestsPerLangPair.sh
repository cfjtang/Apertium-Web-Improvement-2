# /bin/bash

NUM_LANG=10

TOTAL=`wc -l $1 | cut -f 1 -d " "`

#cat $1 |cut -f 3,8 -d " " | grep -e ' 200' | cut -f 1 -d " " | sort | uniq -c | sed 's/^ *//g' | sort -g -r > data-requestsPerPair
cat $1 |cut -f 2 -d " " | sort | uniq -c | sed 's/^ *//g' | sort -g -r > data-requestsPerPair

NUM_LINES=`wc -l data-requestsPerPair | cut -f 1 -d " "`

#cat /tmp/other-file-$$ | sed 's/ /#/g' > /tmp/aux-file-$$
#for i in `cat /tmp/aux-file-$$`
#do
#VAL=`echo $i | cut -f 1 -d "#"`
#TOTAL=$(( $TOTAL + $VAL))	
#done

cat data-requestsPerPair | head -n $NUM_LANG > /tmp/head-file-$$
cat data-requestsPerPair | tail -n $(($NUM_LINES - $NUM_LANG)) > /tmp/other-file-$$

SUM=0
cat /tmp/other-file-$$ | sed 's/ /#/g' > /tmp/other-file-2-$$
for i in `cat /tmp/other-file-2-$$`
do
VAR=`echo "$i" | cut -f 1 -d "#"`
SUM=$(( $SUM + $VAR))
#   echo "$SUM"
done

cat /tmp/head-file-$$ | sed 's/ /#/g' > /tmp/head-file-2-$$

echo -n "chd=t:"
for i in `cat /tmp/head-file-2-$$`
do
VAL=`echo $i | cut -f 1 -d "#"`
FIN=`echo "$VAL/$TOTAL" | bc -l`
#FIN=`echo "$VAL/$TOTAL"`
	echo -n "$FIN,"
done
FIN=`echo "$SUM/$TOTAL" | bc -l`
echo  "$FIN"

echo -n "chl="
for i in `cat /tmp/head-file-2-$$`
do
	echo -n "`echo $i | cut -f 2 -d "#"`|"
done
echo "other"


#lineNumber=0
#rm /tmp/plotb
#cat /tmp/plota | while myLine=`line`
#do
#	lineNumber=`expr $lineNumber + 1`;
#	echo "$lineNumber $myLine" >>/tmp/plotb
#done;

#gnuplot< plotRequestsPerLangPair.gnuplot
