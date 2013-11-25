#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy;

#sys.stdin  = codecs.getreader('utf-8')(sys.stdin);
#sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
#sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

if len(sys.argv) < 2: #{
	print('Usage: python concordance.py [<file>] "string"');
	print('');
	print('       If no input file specified, reads on stdin');
	print('');
	sys.exit(-1);
elif len(sys.argv) < 3: #{
	infile = sys.stdin;
	findstring = sys.argv[1];
elif len(sys.argv) < 4: #{
	infile = file(sys.argv[1]);
	findstring = sys.argv[2];
#}

WINDOW = 50;

for line in infile.read().split('\n'): #{
#	loc = line.find(' ' + findstring + ' ');
	loc = line.find(findstring);

	if loc <= 0: #{
		continue;
	#}

	lgt = len(line);

	mid = lgt / 2;

	text_len = len(findstring);	

	start = loc - WINDOW
	end = loc + WINDOW;

	if start < 0 and end > lgt: #{
		diff = WINDOW - loc;
		pad = '';
		for x in range(diff): #{
			pad = pad + ' ';
		#}

		print('%' , pad + line[0:loc] , line[loc:end]);
	elif start > 0 and end > lgt: #{
		print('+', line[start:loc] , line[loc:end]);

	elif start < 0 and end < lgt: #{
		diff = WINDOW - loc;
		pad = '';
		for x in range(diff): #{
			pad = pad + ' ';
		#}
		print('-', pad + line[0:loc] , line[loc:end]);

	else: #{
		diff = WINDOW - loc;
		pad = '';
		for x in range(diff): #{
			pad = pad + ' ';
		#}
		print(' ', pad + line[start:loc] , line[loc:end]);
	#}

	sys.stdout.flush()

#	print(loc , lgt, mid;
#}