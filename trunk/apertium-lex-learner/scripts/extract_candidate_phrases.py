#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, commands, os;

sys.stdin = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

thresh = float(0.01) / -1.0;

if len(sys.argv) > 1: #{
	thresh = float(sys.argv[1]) / -1.0;
#}

table = {};
s = {};

#-2.36044	||	[1298:0 ||	].[] In 1985 the Soviet probes ''*Vega 1'' and ''2'' left to go probes  in shape of globe *aerostàtic to study the atmosphere to the same weather that the rest of the probe studied the surface.
#-2.34662	||	[1298:1 ||	].[] In 1985 the Soviet probes ''*Vega 1'' and ''2'' left to go probes  in shape of globe *aerostàtic to study the atmosphere to the same time that the rest of the probe studied the surface.
cur_id = 0;
bprob = 0.0;

for line in sys.stdin.read().split('\n'): #{
	row = line.split('||');
	if len(row) < 3: #{
		continue;
	#}
	prob = float(row[0].strip(' \t'));
	id = int(row[1].split(':')[0].strip('[| \t'));
	idx = int(row[1].split(':')[1].strip('[| \t'));

	if id == cur_id: #{
		table[idx] = prob;
		s[idx] = line;
	else: #{

		for i in s: #{
			if i == 0: #{
				continue;
			#}
			if table[i] > bprob: #{
				#print id , table[i] , '>' , bprob , '(' , (table[i] - bprob) / -1 , ')' , thresh;
				if (table[i] - bprob) / -1 >= thresh: #{
					break;
				#}
				for j in s: #{
					print s[j];	
				#}
				break;
			#}
		#}
		idx = 0;
		cur_id = id;
		table = {};
		s = {};
		bprob = 0.0;
		table[idx] = prob;
		s[idx] = line;
	#}	
	#print table , cur_id , id , idx , bprob , prob , line;
	if idx == 0: #{
		bprob = prob;
	#}

#}
