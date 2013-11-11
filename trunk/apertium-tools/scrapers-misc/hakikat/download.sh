#for i in `seq 49 10000`; do 
#	x=`seq -w $i 10000 | head -1`; 
#	echo $x;
#	wget -q "http://hakikat.etnosmi.ru/one_stat.php?id=$i" -O html/$x.html; 
#done
for i in `seq 10001 28722`; do 
	x=$i; 
	echo $x;
	wget -q "http://hakikat.etnosmi.ru/one_stat.php?id=$i" -O html/$x.html; 
done
