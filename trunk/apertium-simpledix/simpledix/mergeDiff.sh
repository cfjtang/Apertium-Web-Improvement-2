#! /bin/bash

init=$(grep -n "<\s*section[^>]*id[^>]*=[^>]*\"main\"[^>]*>" $1 | cut -d ':' -f 1)
next=false

for i in $(grep -n "</\?section[^>]*>" $1 | cut -d ':' -f 1)
do
	if $next
	then
		let toCut=$i-1
		break
	fi
	
	if (($i==$init))
	then
		next=true
	fi
done

let left=$(wc -l $1 | cut -f 1 -d ' ')-$toCut+1

head -n $toCut $1
sed "s/<\\/\\? *$3 *\\/\\?>//" $2
tail -n $left $1


#~ toInsert=$(cat $2 | sed "s/</\\</g" | sed "s/>/\\>/g" | sed "s/$/\\\\/" | \
#~ sed "s/<\\/\\?$3>//")

#~ sed "/[\s\S]*<\s*section[^>]*id[^>]*=[^>]*\"main\"[^>]*>/a$toInsert" $1 | \
#~ sed "s/\\\\$//"
