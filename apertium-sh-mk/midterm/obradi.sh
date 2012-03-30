cat article.sh.txt | apertium -d .. sh-mk-morph > article.sh.morph.txt
cat article.sh.txt | apertium -d .. sh-mk-morph | python ../tagger-to-visl.py | vislcg3 --trace --grammar ../apertium-sh-mk.sh-mk.rlx > article.sh.cg.txt
#cat article.sh.txt | apertium -d .. sh-mk-morph | python ../tagger-to-visl.py | vislcg3 --trace --grammar ~/CGPOS/dev/script/ConstraintGrammar-Croatian.rlx > article.sh.cg.txt
cat article.sh.txt | apertium -d .. sh-mk-chunker > article.sh.chunker.txt
cat article.sh.txt | apertium -d .. sh-mk-debug > article.sh.debug.txt
cat article.sh.txt | apertium -d .. sh-mk-postchunk > article.sh.postchunk.txt
cat article.sh.txt | apertium -d .. sh-mk > article.sh.translated.txt
# | python ../tagger-to-visl.py
# grep "ukjent" article.sh.analysed.txt | sed 's_ukjent__' |sort -u > unknown.txt
