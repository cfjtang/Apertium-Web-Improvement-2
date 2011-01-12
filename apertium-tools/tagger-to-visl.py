#!/usr/bin/python2
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs;

sys.stdin  = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

# Process a lexical unit, examples:
#
#  1. ^zadoù/tad<n><m><pl>$
#  2. ^mont da get/mont<vblex><inf># da get$
#  3. ^mat-tre/mat<adj><mf><sp>+tre<adv>$
#

for line in sys.stdin: #{
	# Beginning of a lexical unit
	new = [];
	ci = 0;
	while ci < len(line):
		c = line[ci];
		
		if c == '\\': #{
			ci +=1;
			continue;
		#}
		if c == '^': #{
			ci += 1;
			superficial = '';
			lemma = '';
			analyses = {};
			tags = '';
			count = 0;
			
			last = '';
			c = line[ci];
			while not (c == '/' and last != '\\') : #{
				if c != '\\':
					superficial = superficial + c;
				last = c;
				ci += 1;
				c = line[ci];
			#}
			last = c;
			ci += 1;
			c = line[ci];
		
			analyses[count] = '';
			
			while not (c == '$' and last != '\\') : #{
				if c == '/' and last != '\\' : #{
					count = count + 1;
					ci += 1;
					c = line[ci];
					continue;
				#}
				if count not in analyses: #{
					analyses[count] = '';
				#}
				if c != '\\':
					analyses[count] = analyses[count] + c;
				last = c;
				ci += 1;
				c = line[ci];
			#}
			new.append('"<');
			new.append(superficial);
			new.append('>"');
			for i in analyses: #{
				#print '**' , analyses[i]
				analyses[i] = analyses[i].replace(' ', '_');
				if analyses[i].count('*') > 0: #{
					analyses[i] = analyses[i].replace('*', '')
					analyses[i] = analyses[i] + ' ukjent'
				#}
				if analyses[i].count('#') > 0: #{
					lemh = analyses[i].split('<')[0].replace(' ', '_');
					tags = ' '.join(analyses[i].split('<')[1:]).split('#')[0];
					lemq = analyses[i].split('#')[1].replace(' ', '_');
					analyses[i] = lemh + lemq + '<' + tags;
					analyses[i] = analyses[i].replace('>+', '><+').replace('><', ' ').replace('<', '" ').strip('><').replace('" ', ' ').replace('>', '');
				else: #{
					analyses[i] = analyses[i].replace('>+', '><+').replace('><', ' ').replace('<', '" ').strip('><').replace('" ', ' ');
				#}
				new.append('\n\t"');
				new.append(analyses[i].split(' ')[0].replace('_', ' '));
				new.append('" ');
				new.append(' '.join(analyses[i].split(' ')[1:])); 
			new.append('\n');
			#}
		#}
		# Newline is newline
		if c == '\n': #{
			new.append('\n');
		#}

		ci += 1;
	print ''.join(new);
#}
