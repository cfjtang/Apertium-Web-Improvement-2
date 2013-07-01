#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, commands, os;

sys.stdin = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

c = sys.stdin.read(1);

while c != '': #{
	if c == '^': #{
		#sys.stdout.write('');
		escaped = False;
		c = sys.stdin.read(1);
		while c != '/': #{	
			if c == '\\': #{
				c = sys.stdin.read(1);
			#}
			sys.stdout.write(c);
			c = sys.stdin.read(1);
		#}
		while True: #{	
			escaped = False;
			if c == '\\': #{
				escaped = True;
				c = sys.stdin.read(1);
			#}
			if c == '$' and escaped == False: #{
				#sys.stdout.write(' ');
				break;
			#}
			c = sys.stdin.read(1);
		#}	
		c = sys.stdin.read(1);
		continue;
	#}	
	sys.stdout.write(c);
	c = sys.stdin.read(1);
#}
