# The script expands the dictionary and pulls out pairs Noun Noun + Genitive
# that represent partitive vs. possesive candidates

# Uses a pyhton script, the output goes to stdout

dict=../../apertium-sh-mk.sh.dix

lt-expand $dict | grep -e "<n>" | grep "<nom>" | grep -v "<adj>" | sed "s|:.*$||g" | sort -u > nominativi.tmp
lt-expand $dict | grep -e "<n>" -e "<np>" | grep "<gen>" | grep -v "<adj>" | sed "s|:.*$||g" | sort -u > genitivi.tmp

python cartesian.py nominativi.tmp genitivi.tmp

#rm nominativi.tmp genitivi.tmp
