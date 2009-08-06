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

	#if line.count('<section') > 0: #{

	if line.count('<section id="unchecked" type="standard">') >0:#{
		o = 1;
	elif line.count('</section') > 0: #{
		o = 0;
	#}
	
	l = '';
	if line.count('<e lm') > 0: #{
		r = line.split('__')[1].split('"')[0];
		l = line.split('">')[0].replace(' ', '');

		l = l + r;
	#}

	if o == 0 or line.count('<e lm') < 1: #{
		print line;
		lines[l] = 1;
		continue;
	#}	

	#}
	if l not in lines: #{
	#	lines[l] = 1;
		print line;
	else: #{
		lines[l] + 1;
		#print '-', line;
	#}

#}
