# Slovene to BCMS
echo "jame Slovene to BCMS"
cat jame.sl.original.txt        |   apertium -d ../../../ sl-sh > sl-bhs/jame.sl-sh
echo "maraton Slovene to BCMS"
cat maraton.sl.original.txt     |   apertium -d ../../../ sl-sh > sl-bhs/maraton.sl-sh
echo "mercator Slovene to BCMS"
cat mercator.sl.original.txt    |   apertium -d ../../../ sl-sh > sl-bhs/mercator.sl-sh
echo "sonce Slovene to BCMS"
cat sonce.sl.original.txt       |   apertium -d ../../../ sl-sh > sl-bhs/sonce.sl-sh
echo "volitve Slovene to BCMS"
cat volitve.sl.original.txt     |   apertium -d ../../../ sl-sh > sl-bhs/volitve.sl-sh
# BCMS to Slovene
echo "jame BCMS to Slovene"
cat jame.bhs.original.txt        |   apertium -d ../../../ sh-sl > bhs-sl/jame.sh-sl
echo "maraton BCMS to Slovene"
cat maraton.bhs.original.txt     |   apertium -d ../../../ sh-sl > bhs-sl/maraton.sh-sl
echo "mercator BCMS to Slovene"
cat mercator.bhs.original.txt    |   apertium -d ../../../ sh-sl > bhs-sl/mercator.sh-sl
echo "sunce BCMS to Slovene"
cat sunce.bhs.original.txt       |   apertium -d ../../../ sh-sl > bhs-sl/sonce.sh-sl
echo "volitve BCMS to Slovene"
cat volitve.bhs.original.txt     |   apertium -d ../../../ sh-sl > bhs-sl/volitve.sh-sl
