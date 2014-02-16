# Slovene to BCMS
echo "jame Slovene to BCMS"
cat jame.slv-hbs.src        |   apertium -d ../../../ slv-hbs_HR > slv-hbs/jame.slv-hbs
echo "maraton Slovene to BCMS"
cat maraton.slv-hbs.src     |   apertium -d ../../../ slv-hbs_HR > slv-hbs/maraton.slv-hbs
echo "mercator Slovene to BCMS"
cat mercator.slv-hbs.src    |   apertium -d ../../../ slv-hbs_HR > slv-hbs/mercator.slv-hbs
echo "sonce Slovene to BCMS"
cat sonce.slv-hbs.src       |   apertium -d ../../../ slv-hbs_HR > slv-hbs/sonce.slv-hbs
echo "volitve Slovene to BCMS"
cat volitve.slv-hbs.src     |   apertium -d ../../../ slv-hbs_HR > slv-hbs/volitve.slv-hbs
# BCMS to Slovene
echo "jame BCMS to Slovene"
cat jame.hbs-slv.src        |   apertium -d ../../../ hbs-slv > hbs-slv/jame.hbs-slv
echo "maraton BCMS to Slovene"
cat maraton.hbs-slv.src     |   apertium -d ../../../ hbs-slv > hbs-slv/maraton.hbs-slv
echo "mercator BCMS to Slovene"
cat mercator.hbs-slv.src    |   apertium -d ../../../ hbs-slv > hbs-slv/mercator.hbs-slv
echo "sunce BCMS to Slovene"
cat sunce.hbs-slv.src       |   apertium -d ../../../ hbs-slv > hbs-slv/sonce.hbs-slv
echo "volitve BCMS to Slovene"
cat volitve.hbs-slv.src     |   apertium -d ../../../ hbs-slv > hbs-slv/volitve.hbs-slv
