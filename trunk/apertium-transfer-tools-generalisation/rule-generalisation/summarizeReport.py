 #!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys

numWordsTranslatedByRules=0
numWordsTranslaredWordforWordInsideRules=0
numWordsTranslaredWordforWordOutsideRules=0

for line in sys.stdin:
	parts=line.split()
	numWords=int(parts[0])
	if len(parts)==2:
		numWordsTranslaredWordforWordOutsideRules+=numWords
	elif '|' in line:
		numWordsTranslatedByRules+=numWords
	else:
		numWordsTranslaredWordforWordInsideRules+=numWords

totalWords=numWordsTranslatedByRules+numWordsTranslaredWordforWordInsideRules+numWordsTranslaredWordforWordOutsideRules

print "Num. words translated by rules: "+str(numWordsTranslatedByRules)+" ("+str( numWordsTranslatedByRules*100.0/ totalWords)+"%)"
print "Num. words translated word-for-word inside rules: "+str(numWordsTranslaredWordforWordInsideRules)+" ("+str( numWordsTranslaredWordforWordInsideRules*100.0/ totalWords)+"%)"
print "Num. words translated word-for-word outside rules: "+str(numWordsTranslaredWordforWordOutsideRules)+" ("+str( numWordsTranslaredWordforWordOutsideRules*100.0/ totalWords)+"%)"

