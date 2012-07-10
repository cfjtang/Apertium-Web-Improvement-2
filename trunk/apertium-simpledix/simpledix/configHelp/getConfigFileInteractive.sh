#! /bin/sh

echo '<?xml version="1.0" encoding="UTF-8"?>' > $2
echo '<!DOCTYPE splitter SYSTEM "config.dtd">' > $2
echo '' > $2
echo '<splitter>' > $2
echo '    <paradigms>' > $2

for i in $(xsltproc dixParadigms.xsl $1)
do 
	line=$i
	line=$line" "$(xsltproc --stringparam paradigm $i paradigmFlexion.xsl $1)"\n"
	
	aux1=$(echo $line | cut -d ' ' -f 1);

	[ -z "$aux1" ] && continue
	
	echo "Is $aux1 a word paradigm? [y|n]"
	read isParad
	
	[ -z $isParad ] || [ "y" != $isParad ] && continue

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
	
	echo "        <paradigm n = \"$aux1\" idForm=\"$idForm\" desc=\"$aux1\" kind=\"$type\">" > $2
	
	acum=""
	for flex in $(echo $line | cut -d ' ' -f 2-);
	do
		if [ $flex = "|" ]
		then
			echo "            <flex form=\"$acum\"/>" > $2
			acum=""
		else
			acum=$acum" "$flex
		fi
		
	done
	echo "        </paradigm>" > $2
	
done



echo '    </paradigms>' > $2
echo '</splitter>' > $2
