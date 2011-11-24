# Expands the dictionary and greps out the lemmas taken from the top of one of the three files

# $1 - the lemma file
# $2 - number of words (100,200,400)

head -$2 $1 > rieci.tmp

lt-expand ../apertium-sh-mk.sh.dix | grep -v -e '<vbser>' -e '<vbmod>' -e '<vbaux>' | grep -f rieci.tmp

rm rieci.tmp
