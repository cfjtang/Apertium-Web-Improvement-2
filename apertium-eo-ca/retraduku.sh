make && cat tekstaro/ca.crp.txt | time apertium ca-eo > nova_traduko.txt
diff aktuala_traduko.txt nova_traduko.txt | grep -r '[<>]' > /tmp/crpdiff.txt && for i in `cut -c3-8 /tmp/crpdiff.txt | uniq`; do echo  --- $i ---; grep -r "^ *$i\." tekstaro/ca.crp.txt; grep -r " $i\." /tmp/crpdiff.txt; done | less

