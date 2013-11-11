COUNT=0;
for i in `cat urls.txt  | cut -f3 -d'=' | cut -f1 -d'"' | sort -u | sort -g`; do 
	x=`python3 -c 'import sys; print(str(sys.argv[1]).zfill(5))' $i`; 
	if [[ $COUNT -eq 10 ]]; then
		COUNT=0;
		echo "";
	fi
	echo -n $x" ";
	cat html/$x.html | iconv -f windows-1251 | python3 process-html.py | strip_html.py > text/$x.txt; 
	COUNT=`expr $COUNT + 1`;
done

