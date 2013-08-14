#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys

for line in sys.stdin:
	partsspace=line.split()
	partsslash=line.split('|')
	
	numRules=int(partsspace[0])
	ruleLength=1
	if len(partsspace) > 1:
		ruleLength=len(partsslash[0].strip().split())-2
	
	print str(numRules*ruleLength)+" "+line.strip()
	
