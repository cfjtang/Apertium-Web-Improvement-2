# A simple trick script for finding a correct verb paradigm
#
# Greps for a lemma, and then replaces part1 with part2 trying to mold a word into a paradigm
#
# Usage:
#	 ./paradigm-test.sh lemma part1 part2
#
# Example:
#	 ./paradigm-test.sh igrati igr svir


lt-expand apertium-sh-sl.sh.dix | grep ":$1<" | grep -v "+" | sed "s|$2|$3|g" | less
