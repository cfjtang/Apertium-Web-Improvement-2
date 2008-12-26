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

	apertium = row[1].strip();
	parole = row[0].strip();

	transform[parole] = apertium;
#}


# Process a lexical unit, examples:
#
#  1. gydradd cydradd AQ0000
#  2. â'i â+ei SPS000+DP0CN0
#
def processWord(c): #{
	superficial = '';
	lemma = '';
	analysis = '';
	tags = '';
	parole = '';
	probabilitat = '';

	while c != ' ' and c != '\n': #{
		superficial = superficial + c;
		c = sys.stdin.read(1);
	#}

	if c != '\n': #{
		c = sys.stdin.read(1);
	#}

	while c != ' ' and c != '\n': #{
		lemma = lemma + c;
		c = sys.stdin.read(1);
	#}

	while c != '\n': #{
		analysis = analysis + c;
		c = sys.stdin.read(1);
	#}

#	print superficial , lemma , analysis;

	apertium = '';

	if ' ' in analysis.strip(): #{
		analysis = analysis.strip().split(' ')[0];
	#}

	if '+' in analysis: #{
		lemma_row = lemma.split('+');
		tag_row = analysis.strip().split('+');

		analysis_temp = '';

		if len(lemma_row) != len(tag_row): #{
			print 'Fatal error';
			sys.exit(-1);
		#}

		for j in range(0, len(lemma_row)): #{
			analysis_temp = analysis_temp + '+' + lemma_row[j] + transform[tag_row[j]];
		#}

		print '^' + superficial.replace('_', ' ') + '/' + analysis_temp.strip('+').replace('_', ' ') + '$';

	elif superficial != '' and lemma == '': #{
		print '^' + superficial.replace('_', ' ') + '/*' + superficial.replace('_', ' ') + '$';
			
	elif superficial != '' and lemma != '': #{
		tags = analysis.strip();
		apertium = transform[tags];
		print '^' + superficial.replace('_', ' ') + '/' + lemma.replace('_', ' ') +  apertium + '$';
	else: #{
		return;
	#}

	return;
#}

c = sys.stdin.read(1);

while c: #{
	processWord(c);
	c = sys.stdin.read(1);
#}
