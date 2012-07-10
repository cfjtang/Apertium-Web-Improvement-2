#! /bin/sh

echo '<?xml version="1.0" encoding="UTF-8"?>'
echo '<!DOCTYPE splitter SYSTEM "config.dtd">'
echo ''
echo '<splitter>'
echo '    <paradigms>'

for i in $(xsltproc dixParadigms.xsl $1)
do 
	line=""
	phase="first"
	line=$line" "$(xsltproc --stringparam paradigm $i paradigmFlexionAndType.xsl $1)"\n"
	
	aux1=$i;

	[ -z "$aux1" ] && continue
	
	type=$(echo $aux1 | grep -o _[^_]*$ | cut -d _ -f 2)

	acum=""
	common=""
	buffer=""
	safe=""
	maxMatches=""

	for flex in $(echo $line);
	do
		if [ $flex = "|" ]
		then
			phase="first"
			acum=$(echo $acum | tr -s " ")
			if [ -z "$common" ]
			then
				common=$acum
			else
				common=$(printf "%s\n%s\n" "$common" "$acum" | sed -e 'N;s/^\(.*\).*\n\1.*$/\1/')
			fi
			#~ echo sh getFormMatch.sh "$acum" $2
			matches=$(sh getFormMatch.sh "$acum" $2)
			
			if [ -z "$matches" ]
			then
				echo ""
			else
				if [ -z "$maxMatches" ]
				then
					idForm=$form
					idAcum=$acum
					type=$(echo $acum | cut -d " " -f 1)
					maxMatches=$matches
				else
					if [ "$matches" -lt "$maxMatches" ]
					then
						buffer=$buffer"                <flex form=\"$idForm\" attrs=\"$idAcum\"/>\n"
						idForm=$form
						idAcum=$acum
						type=$(echo $acum | cut -d " " -f 1)
						maxMatches=$matches
					else
						buffer=$buffer"                <flex form=\"$form\" attrs=\"$acum\" />\n"
					fi
				fi
			fi
					
			acum=""
		else
			if [ $phase = "first" ]
			then
				phase="rest"
				form=$flex
				
			else
				acum=$acum" "$flex
			fi
		fi
	done
	

	[ -z "$maxMatches" ] && continue
	common=$(echo $common | tr -s " ")
	echo -e "        <paradigm n = \"$aux1\" idForm=\"$idForm\" attrs=\"$idAcum\" desc=\"$aux1\" kind=\"$type\" common=\"$common\">"
	echo -e $buffer
	echo -e "        </paradigm>"
	
done



echo '    </paradigms>'
echo '</splitter>'
