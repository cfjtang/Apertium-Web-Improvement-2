#! /bin/bash

param=$1
i=0
cat $2 | while read line
do
	aux=$(echo $line | grep -o "^\.")
	
	if [ -z $aux ] 
	then
		i=0
		head=$line
		first=""
	else
		let i=$i+1
		form=$head" "$(echo $line | cut -d . -f 2)
		match=$(printf "%s\n%s\n" "$param" "$form" | sed -e 'N;s/^\(.*\).*\n\1.*$/\1/' )
		[ -z "$match" ] && continue;
		
		if [ "$param" = "$match" ]
		then
			echo $i
			exit
		fi
		
		first="not"
	fi
done
