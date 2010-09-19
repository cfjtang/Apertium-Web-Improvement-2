 cat /home/fran/corpora/makedonski/lex.f2e| grep -e '<vblex>.*<vblex>' -e '<vblex>.* \*' | grep " $1<" | head -15 | cut -f3 -d' ' | sort -f | uniq -c | sort -gr 
