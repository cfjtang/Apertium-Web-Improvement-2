
for i in 201210 201211 201212 201301 201302 201303 201304 201305 201306 201307 201308 201309 201310; do 

	for j in `seq 1 20`; do 
		echo -e "$i $j" >/dev/stderr; 
		wget -q -O - "http://www.shyn.ru/?q=archive/$i&page=$j" | grep article\/ | sed 's/<a/@<a/g' | cut -f2 -d'@' | cut -f2 -d'"' | sort -u | grep -v 'comments' >> urls.txt
	done
done
for i in `cat urls.txt | sort -u`; do
	
	name=`echo $i | cut -f3 -d'/'`; 
	echo $name;
	wget -q -O - "http://www.shyn.ru/$i" > html/$name.html;
done

