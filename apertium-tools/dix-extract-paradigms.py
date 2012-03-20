#!/usr/bin/env python3
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, re;

pardefs = [];

for line in open(sys.argv[1]).readlines(): #{

	parname = line.strip(); 

	if parname == '': #{
		continue;
	#}

	pardefs.append(parname);	
#}

printing = False;

for line in open(sys.argv[2]).readlines(): #{
	line = line.strip('\n');
	if line == '': #{
		continue;
	#}

	if line.count('<pardef') > 0: #{
		for pardef in pardefs: #{
			if line.count('n="' + pardef + '"') > 0: #{
				printing = True;
				break;
			#}
		#}
	#}

	if printing == True: #{
		print(line);
	#}

	if printing == True and line.count('</pardef>') > 0: #{
		printing = False;
	#}
	
#}

