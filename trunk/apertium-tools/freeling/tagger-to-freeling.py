#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, commands;

sys.stdin  = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

tffile = sys.argv[1];

transform = {};

# Read in the parole lookup table.

for line in file(tffile).read().split('\n'): #{
	row = line.split('\t');

	if len(row) < 2: #{
		continue;
	#}

	parole = row[0].strip();
	apertium = row[1].strip();

	transform[apertium] = parole;
#}

c = sys.stdin.read(1);

# Process a lexical unit, examples:
#
#  1. ^zadoù/tad<n><m><sg>$
#  2. ^mont da get/mont<vblex><inf># da get$
#  3. ^mat-tre/mat<adj><mf><sp>+tre<adv>$
#
def processWord(c): #{
	superficial = '';
	lemma = '';
	analysis = '';
	tags = '';
	parole = '';
	unknown = False;

	c = sys.stdin.read(1);
	while c != '/': #{
		superficial = superficial + c;
		c = sys.stdin.read(1);
	#}

	c = sys.stdin.read(1);
	while c != '<': #{
		if c == '*': #{
			unknown = True;
		#}
		lemma = lemma + c;
		c = sys.stdin.read(1);
	#}

	while c != '$': #{
		analysis = analysis + c;
		c = sys.stdin.read(1);
	#}

	if unknown == True: #{
	        print superficial + ' ' + superficial + ' UNK';
#		c = sys.stdin.read(1);
		return;
	#}

	if '+' in analysis: #{
		gde = 0;
		for j in analysis: #{
			if j == '<' and gde == 0: #{
				gde = 1;
				tags = tags + '+<';
				continue;
			elif j == '+': #{
				gde = 0;
				lemma = lemma + '+';
				continue;
			#}

			if gde == 0: #{
				lemma = lemma + j;
			elif gde == 1: #{
				tags = tags + j;
			#}
		#}

		tags = tags.strip('+');
		for tag in tags.split('+'): #{
			parole = parole + '+' + transform[tag];
		#}
		parole = parole.strip('+');
	else: #{
		tags = analysis;
		parole = transform[tags];
	#}

	print superficial.replace(' ', '_') + ' ' + lemma.replace(' ', '_') + ' ' + parole;
#}

while c: #{
	# Beginning of a lexical unit
	if c == '^': #{
		processWord(c);
	#}

	# In some analysers, the comma is not analysed, it should be
	if c == ',': #{
		print ', , Fc';
	#}

	# Newline is newline
	if c == '\n': #{
		print '\n';
	#}

	c = sys.stdin.read(1);
#}
