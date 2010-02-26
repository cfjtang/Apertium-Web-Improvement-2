# /bin/bash

cat $1 | cut -f 5,10 -d " "| sort  >/tmp/tmpplot$$
echo "aaaaaaaaaaaaaa 0" >> /tmp/tmpplot$$

rm /tmp/plotb
USER=
TOTALCOST=0
cat /tmp/tmpplot$$ | while myLine=`line`
do
	OLDUSER=$USER
	USER=`echo "$myLine" | cut -f 1 -d " "`
	COST=`echo "$myLine" | cut -f 2 -d " "`
	 if [ ! -z $OLDUSER ]; then
	 	if [ x$OLDUSER != x$USER ]; then
	 		echo "$OLDUSER $TOTALCOST" >>/tmp/plotb
	 		TOTALCOST=0
	 	fi
	 fi
	 let TOTALCOST=$TOTALCOST+$COST
	echo "user: $USER cost: $COST"
done;

gnuplot< plotCostPerUser.gnuplot
