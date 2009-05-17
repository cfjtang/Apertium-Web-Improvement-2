#!/usr/bin/python2.5

import sys, re
from BeautifulSoup import BeautifulStoneSoup

if len(sys.argv) != 3:
    print 'Usage:', sys.argv[0], '<dix> <RE to match end of paradigm name>'
    print '   Ex:', sys.argv[0], ' apertium-sv-da.da.dix vblex'
    sys.exit(1)

def stem(pardef):
    if pardef.count("/") > 0:
	return pardef.split("/")[0]
    else:
	return pardef.split("__")[0]

def printPart(pardefs):
    for pardef in pardefs:
	print pardef + ": ~" + " ~".join(pardefs[pardef])

def printFull(pardefs):
    for pardef in pardefs:
	print pardef + ":\t\t ",
	for inflection in pardefs[pardef]:
	    print stem(pardef) + inflection, "\t",
	print;

def printInfl(pardefs):
    for pardef in pardefs:
	print;
	for inflection in pardefs[pardef]:
	    print '~' + inflection, 

pardefs = {}

soup = BeautifulStoneSoup(file(sys.argv[1]).read())
for pardef in soup.findAll("pardef", n=re.compile(sys.argv[2] + '$')):
    inflections = []
    for l in pardef.findAll("l"):
	inflections.append(l.renderContents());
    pardefs[pardef['n']] = inflections

printPart(pardefs)
