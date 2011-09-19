date > nova_traduko-cg.err
cat tekstaro/es.crp.txt | time apertium es-eo-cg > nova_traduko.txt 2>> nova_traduko-cg.err
#cat tekstaro/es.crp.txt | time apertium es-eo-cg-bytecode > nova_traduko.txt 2>> nova_traduko-cg-bc.err
date >> nova_traduko-cg.err
diff aktuala_traduko.txt nova_traduko.txt | grep -r '[<>]' > /tmp/crpdiff.txt && for i in `cut -c3-8 /tmp/crpdiff.txt | uniq`; do echo  --- $i ---; grep -r "^ *$i\." tekstaro/es.crp.txt; grep -r " $i\." /tmp/crpdiff.txt; done | less

