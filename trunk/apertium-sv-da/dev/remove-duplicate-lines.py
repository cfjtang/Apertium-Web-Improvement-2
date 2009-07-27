#!/usr/bin/python2.5
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy;

sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);
sys.stdin = codecs.getreader('utf-8')(sys.stdin);

o = 0;

lines = {};

for line in sys.stdin.read().split('\n'): #{

	if  line.count('<section') > 0: #{
		o = 1;
	elif line.count('</section') > 0: #{
		o = 0;
	#}
	
	if o == 0 or line.count('<e lm') < 1: #{
		print line;
		continue;
	#}	

	if line not in lines: #{
		lines[line] = 1;
		print line;
	else: #{
		lines[line] = lines[line] + 1;
	#}

#}
