# Slovene to BCMS
echo "jame Slovene to BCMS"
cat jame.slv.original.txt        |   apertium -d ../../../ slv-hbs > slv-hbs/jame.slv-hbs
echo "maraton Slovene to BCMS"
cat maraton.slv.original.txt     |   apertium -d ../../../ slv-hbs > slv-hbs/maraton.slv-hbs
echo "mercator Slovene to BCMS"
cat mercator.slv.original.txt    |   apertium -d ../../../ slv-hbs > slv-hbs/mercator.slv-hbs
echo "sonce Slovene to BCMS"
cat sonce.slv.original.txt       |   apertium -d ../../../ slv-hbs > slv-hbs/sonce.slv-hbs
echo "volitve Slovene to BCMS"
cat volitve.slv.original.txt     |   apertium -d ../../../ slv-hbs > slv-hbs/volitve.slv-hbs
# BCMS to Slovene
echo "jame BCMS to Slovene"
cat jame.hbs.original.txt        |   apertium -d ../../../ sh-hbs > hbs-slv/jame.hbs-slv
echo "maraton BCMS to Slovene"
cat maraton.hbs.original.txt     |   apertium -d ../../../ sh-hbs > hbs-slv/maraton.hbs-slv
echo "mercator BCMS to Slovene"
cat mercator.hbs.original.txt    |   apertium -d ../../../ sh-hbs > hbs-slv/mercator.hbs-slv
echo "sunce BCMS to Slovene"
cat sunce.hbs.original.txt       |   apertium -d ../../../ sh-hbs > hbs-slv/sonce.hbs-slv
echo "volitve BCMS to Slovene"
cat volitve.hbs.original.txt     |   apertium -d ../../../ sh-hbs > hbs-slv/volitve.hbs-slv
