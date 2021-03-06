#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys

numMatch=0
numPattern=0

numWordsPattern=0

BEGIN_SECTION=u"<section-rules>"
END_SECTION=u"</section-rules>"

RULECOUNTERTOFIND=u"<rule>"

TAGTOFIND=u"</when>"
TAGTOFIND2=u"</otherwise>"
TAGTOFIND3=u"</action><!--isolated word-->"

insideSection=False

#for origline in sys.stdin:
origline=sys.stdin.readline()
while origline:
	line=origline.strip().decode('utf-8')
	
	if insideSection:
		pos= line.find(END_SECTION)
		if pos >=0:
			insideSection=False
	else:
		pos= line.find(BEGIN_SECTION)
		if pos >=0:
			insideSection=True

	modifiedLine=False
	if insideSection:
		pos=line.find(RULECOUNTERTOFIND)
		if pos >=0 :
			modifiedLine=True
			print origline[:-1]
			numPattern+=1			
			endPatternFound=False
			numPatternSeen=0
			while not endPatternFound:
				myorigline=sys.stdin.readline()
				myline=myorigline.strip().decode('utf-8')
				if myline.find(u"</pattern>") >= 0:
					endPatternFound=True
					if myline.find(u"<pattern>") >= 0:
						#continue processing
						origline=myorigline
						line=myline
						modifiedLine=False
					else:
						print myorigline[:-1]
				else:
					numPatternSeen+=1
					print myorigline[:-1]
			numWordsPattern=numPatternSeen-1
		
		pos= line.find(TAGTOFIND)
		if pos >=0:
			numMatch+=1
			modifiedLine=True
			strSourceWords=""
			for num in range(numWordsPattern):
				strSourceWords=strSourceWords+"<lu><clip pos=\""+str(num+1)+"\" side=\"sl\" part=\"whole\"/></lu>"
			print line[:pos].encode('utf-8')+ "<out><lu><lit v=\"*executedtule"+str(numMatch)+"\"/></lu>"+strSourceWords+"<lu><lit v=\"*endexecutedtule\"/></lu></out>" +line[pos:].encode('utf-8')
		else:
			pos= line.find(TAGTOFIND2)
			if pos >=0:
				modifiedLine=True
				print line[:pos].encode('utf-8')+ "<out><lu><lit v=\"*wordforword"+str(numPattern)+"\"/></lu></out>" +line[pos:].encode('utf-8')
			else:
				pos= line.find(TAGTOFIND3)
				if pos >=0:
					modifiedLine=True
					print line[:pos].encode('utf-8')+ "<out><lu><lit v=\"*isolatedword\"/></lu><lu><clip pos=\"1\" side=\"sl\" part=\"whole\"/></lu><lu><lit v=\"*endisolatedword\"/></lu></out>" +line[pos:].encode('utf-8')	
	if not modifiedLine:
		print origline[:-1]
	origline=sys.stdin.readline()
