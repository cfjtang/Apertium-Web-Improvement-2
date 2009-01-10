# Convert a file tagged with the apertium-tagger -p -g to the factored
# format required by Moses

# Copyright (c) 2009 Francis Tyers, released under the GNU GPL.


#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, commands;

sys.stdin  = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

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
			lemma = superficial;
			break;
		#}
		lemma = lemma + c;
		c = sys.stdin.read(1);
	#}

	if unknown == True: #{
		sys.stdout.write(superficial + '|' + lemma + '|?|? ' );
	#}

	while c != '$': #{
		analysis = analysis + c;
		c = sys.stdin.read(1);
	#}

	if unknown == True: #{
		c = sys.stdin.read(1);
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

	else: #{
		tags = analysis;
	#}

	tags = tags.replace('>+<', '+');
	if tags.count('><') > 0: #{
		tag = tags.replace('><','.').strip('+><').split('.')[0];
	else: #{
		tag = tags.replace('><','.').strip('+><');
	#}

	sys.stdout.write(superficial + '|' + lemma + '|' + tag + '|' + tags.replace('><','.').strip('+><') + ' ' );
#}

while c: #{
	# Beginning of a lexical unit
	if c == '^': #{
		processWord(c);
	#}

	# In some analysers, the comma is not analysed, it should be
	if c == ',': #{
		sys.stdout.write(',|,|cm|cm');
	#}

	# Newline is newline
	if c == '\n': #{
		sys.stdout.write(c);
	#}

	c = sys.stdin.read(1);
#}
