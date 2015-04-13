
#for i in 2012 2013 2014 2015; do 
#
#	for j in `seq 1 120`; do 
#		echo -e "$i $j" >/dev/stderr; 
#		wget -q -O - "http://www.shyn.ru/?q=archive/$i&page=$j" | grep article\/ | sed 's/<a/@<a/g' | cut -f2 -d'@' | cut -f2 -d'"' | sort -u | grep -v 'comments' >> urls.txt
#	done
#done
for i in `cat urls.txt | sort -u`; do
	
	name=`echo $i | cut -f2 -d'/'`; 
	md5s=`echo $name | md5sum | cut -f1 -d'-' | sed 's/ *$//g'`
	echo $md5s" "$name;
	wget -q -O - "http://www.shyn.ru/$i" > html/$md5s.html;
done

