#!/usr/bin/python3
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys;

#+nature<n>	service<n> nature<n>	carácter<n>	3
#+nature<n>	The<def><def> imperialist<adj> nature<n>	carácter<n>	1
#+nature<n>	the<def><def> secular<adj> nature<n> of<pr> State<n>	carácter<n>	1
#+nature<n>	its<det><pos> nature<n> prevent<vblex>	carácter<n>	1
#+nature<n>	nature<n> be<vbser> in<pr>	carácter<n>	1
#

infile = '';

if len(sys.argv) < 3: #{
	print('ngrams-to-rules.py <ngrams> <threshold>');
	sys.exit(-1);
#}

infile = open(sys.argv[1]);
threshold = float(sys.argv[2]);

permitted_tags = ['n', 'vblex', 'adj', 'n.*', 'vblex.*', 'adj.*'];

print('<rules>');
lineno = 1;
ruleno = 0;
for line in infile.readlines(): #{
#	print('\n';
#	print(line
	if len(line) < 2: #{
		continue;
	#}
	line = line.strip();
	#line = line.decode('utf-8').strip();


	#+ 0.571428571429 14 8 8 	troiñ<vblex>		tourner<vblex>	8
	row = line.split('\t');

	if len(row) == 3:
		row.insert(0, '');

#	tipus = row[0].split(' ')[0];
	weight = row[0].split(' ')[1];
	sl = row[1].strip().lower();
	tl = row[3];
	tl_lema = tl.split('<')[0].lower();
	tl_tags = '<'.join(tl.split('<')[1:]).replace('><', '.').replace('>', '.*');

	freq = row[4];
	pattern = row[2].split(' ');

	if row[2].count('<guio>') > 0 or row[2].count('<sent>') > 0 or row[2].count('<cm>') > 0: #{
		print('PUNCTUATION_IN_PATTERN', line, file=sys.stderr);
		continue;
	#}


	# Hacks
	if len(pattern[0].strip()) == 0: #{
		print('ZERO_PATTERN' , line, file=sys.stderr);
		continue;
	#}

	inpattern = False;
	for w in pattern: #{
		if w.lower().count(sl) > 0: #{
			inpattern = True;
		#}
	#}
	if inpattern == False:  #{
		print('SL_NOT_IN_PATTERN' , line, file=sys.stderr);
		continue;
	#}

	if tl_tags.count('adj') > 0 and sl.count('adj')  < 1: #{
		print("TAG_MISMATCH" , line, file=sys.stderr);
		continue;
	#}
	if tl_tags.count('vbmod') > 0 and sl.count('vbmod')  < 1: #{
		print("TAG_MISMATCH" , line, file=sys.stderr);
		continue;
	#}

	if tl_tags not in permitted_tags: #{
		print("TAG_NOT_PERMITTED" , tl_tags , '||' , line, file=sys.stderr);
		continue;
	#}

	if float(freq) < float(threshold): #{
		print("UNDER_THRESHOLD", weight, "<", threshold, "||",  line, file=sys.stderr);
		continue;
	#}

	sel = False;
	ruleno = ruleno + 1;
	lineno = lineno + 1;
	print('  <rule c="' + str(ruleno) + ' ' + str(lineno) + ': ' + freq + '" weight="' + weight + '">');
	for word in pattern: #{
		sl_lema = word.split('<')[0].lower();
		if word.count('><') > 0: #{
			sl_tags = '<'.join(word.split('<')[1:]).replace('><', '.').replace('>', '');
		else: #{
			sl_tags = '<'.join(word.split('<')[1:]).strip('<>');
		#}

		# ======================================================================= #
		if sl_tags.count('.') > 0 or sl_tags == 'vblex':  #{
			sl_tags = sl_tags + '.*';
		#}

		if sl_tags == 'n': #{
			sl_tags = sl_tags.replace('n', 'n.*');
		#}
		sl_tags = sl_tags.replace('.inf', '').replace('.pp', '').replace('.ger','');
		tl_tags = tl_tags.replace('.inf', '').replace('.pp', '').replace('.ger','');
		if sl_tags == 'num': #{
			sl_lema = '';
		#}
		if sl_tags == 'det.pos.*' or sl_tags == 'det.ord.*': #{
			sl_lema = '';
		#}
		if sl_tags == 'np.top.*' or sl_tags == 'np.loc.*' or sl_tags == 'np.ant.*' or sl_tags == 'np.cog.*': #{
			sl_lema = '';
		#}
		if sl_tags == 'num.percent': #{
			sl_lema = '';
			sl_tags = 'num.percent';
		#}
		if sl_tags == 'det.ind.*' and sl_lema == 'a': #{
			sl_tags = 'det.ind.sg';
		#}
		if sl_tags == 'det.def.*' and sl_lema == 'the': #{
			sl_tags = 'det.def.sg';

		# ======================================================================= #

		sl_lema = sl_lema.replace('~', ' ');
		tl_lema = tl_lema.replace('~', ' ');
		sl_lema = sl_lema.replace('-', '\-');
		tl_lema = tl_lema.replace('-', '\-');
		sl_lema = sl_lema.replace('(', '\(');
		tl_lema = tl_lema.replace('(', '\(');
		sl_lema = sl_lema.replace(')', '\)');
		tl_lema = tl_lema.replace(')', '\)');

		if word.lower().count(sl) > 0: #{
			lineno = lineno + 1;
			if sl_lema == '': #{
				print('    <match tags="' + sl_tags + '"><select lemma="' + tl_lema + '" tags="' + tl_tags + '"/></match>');
			else: #{
				print('    <match lemma="' + sl_lema + '" tags="' + sl_tags + '"><select lemma="' + tl_lema + '" tags="' + tl_tags + '"/></match>');
			#}
			sel = True;
		else: #{
			lineno = lineno + 1;
			if sl_lema == '': #{	
				print('    <match tags="' + sl_tags + '"/>');
			else: #{
				print('    <match lemma="' + sl_lema + '" tags="' + sl_tags + '"/>');
			#}
		#}
	#}
	if sel == False: #{
		print('  </rule> <!-- Warning: No select operation -->');
	else: #{
		print('  </rule>');
	#}
	lineno = lineno + 1;
#}
print('</rules>');
