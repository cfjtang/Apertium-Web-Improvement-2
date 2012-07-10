#! /bin/sh

var="";

for i in $(xsltproc dixParadigms.xsl $1)
do 
	var="$var $i"
	var=$var" "$(grep "\"$i\"" $1 | wc -l)"\n"
	echo grep "\"$i\"" $1
done
echo $var | sort -rn -k 2
