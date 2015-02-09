read word
word=`echo $word|cut -d. -f1`
#echo $word
lt-expand $1 | grep "$word<" | sed 's/:<:/%/g' | sed 's/:/ ^/g' | sed 's/$/$ ^.<sent>$/g'
