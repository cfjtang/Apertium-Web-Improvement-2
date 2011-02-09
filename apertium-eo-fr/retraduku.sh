#make && cat tekstaro/fr.crp.txt | time apertium -d . fr-eo > nova_traduko.txt
make && cat tekstaro/fr.crp.txt | time apertium -d . fr-eo-bytecode > nova_traduko.txt
#make && cat tekstaro/fr.crp.txt | time apertium -d . fr-eo-pn > nova_traduko.txt (tiu modo ne donas bonajn rezultojn)
./komparu.sh

