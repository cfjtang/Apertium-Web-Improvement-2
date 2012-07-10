#! /bin/sh

var="";

for i in $(xsltproc dixParadigms.xsl $1)
do 
	var="$var $i"
	var=$var" "$(grep "\"$i\"" $1 | wc -l)
	var=$var" "$(xsltproc --stringparam paradigm $i paradigmFlexion.xsl $1)"\n"
done

echo '<?xml version="1.0" encoding="UTF-8"?>'
echo '<!DOCTYPE splitter SYSTEM "config.dtd">'
echo ''
echo '<splitter>'
echo '    <paradigms>'

echo $var | sort -rn -k 2 |
while read -r line
do 
	aux1=$(echo $line | cut -d ' ' -f 1);
	
	[ -z "$aux1" ] && continue
	
	
	idForm=$(echo $aux1 | grep -o /[^_]*_ | cut -d / -f 2 | cut -d _ -f 1)
	type=$(echo $aux1 | grep -o _[^_]*$ | cut -d _ -f 2)
	case $type in
		n)
			type="noun";;
		vblex)
			type="verb";;
		adj)
			type="adjective";;
		adv)
			type="adverb";;
		num)
			type="numeral";;
		*)
			type="other";;
	esac
	
	echo "        <paradigm n = \"$aux1\" idForm=\"$idForm\" desc=\"$aux1\" kind=\"$type\">"
	
	acum=""
	for i in $(echo $line | cut -d ' ' -f 3-);
	do
		if [ $i == "|" ]
		then
			echo "            <flex form=\"$acum\"/>"
			acum=""
		else
			acum=$acum" "$i
		fi
		
	done
	echo "        </paradigm>"
done 

echo '    </paradigms>'
echo '</splitter>'
