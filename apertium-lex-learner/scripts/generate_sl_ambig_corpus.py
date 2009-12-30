#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, commands, os;

sys.stdin = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

atable = {};

lu = '';
in_word = False;
lineno = 1;

if len(sys.argv) < 2: #{
	print 'generate_sl_ambig_corpus.py <ambig word list>';
	sys.exit(-1);
#}

for line in file(sys.argv[1]).read().decode('utf-8').split('\n'): #{
	if len(line) < 2: #{
		continue;
	#}
	row = line.split(':<:');
	lf = row[1].split('><')[0] + '>';
	
	if lf not in atable: #{
		atable[lf] = [];
	#}
	
	atable[lf].append(row[1]);
#}

#for key in atable: #{
#	for alt in atable[key]: #{
#		print >>sys.stderr , key , alt;
#	#}
##}

# time<n><ND>:<:temps<n><:0><m><sp>
# weather<n><ND>:<:temps<n><:1><m><sp>
# languge<n>:<:llengua<n><:0><f>
# tongue<n>:<:llengua<n><:1><f>
# history<n>:<:història<n><:0><f>
# story<n>:<:història<n><:1><f>
# station<n>:<:estació<n><:0><f>
# season<n>:<:estació<n><:1><f>
# 

def process_lexical_unit(lu, ambigs): #{
	super = '';
	lema = '';
	cat = '';

	seen_bar = False;
	first_tag = False;
	for c in lu.decode('utf-8'): #{
		if c == '^': #{
			continue;
		#}
		if c == '/': #{
			seen_bar = True;
			continue;
		#}
		if c == '<': #{
			first_tag = True;
			continue;
		#}
		if c == '>': #{
			break;
		#}
		if seen_bar == False: #{
			super = super + c;
		elif seen_bar == True and first_tag == False: #{
			lema = lema + c;
		elif seen_bar == True and first_tag == True: #{
			cat = cat + c;
		#}
	#}	

	busca = lema + '<' + cat + '>';

	res = {};
	curr = 0;
	if busca in atable: #{
#		print '>>' , busca
		for alt in atable[busca]: #{
#			print '>>>' , alt; 
			if curr not in res: #{
				res[curr] = '';
			#}
			res[curr] = lu.replace(busca, alt);
			curr = curr + 1;
		#}
	else: #{
		res = {0: lu};
	#}

	if len(res) > 1: #{
		new_paths = {};
		for lu in res: #{
			for path in ambigs: #{
				new_paths[path + res[lu]] = ambigs[path] + res[lu];
			#}
		#}
		ambigs = new_paths;
	else: #{
		for path in ambigs: #{
			ambigs[path] = ambigs[path] + res[0];
		#}
	#}
	return ambigs;
#}

ambigs = {};
ambigs[''] = '';

c = sys.stdin.read(1);

xx = 0;
while c != '': #{

	if c == '^': #{
		in_word = True;
	#}
	if c == '$': #{
		lu = lu + c;
		ambigs = process_lexical_unit(lu, ambigs);
		in_word = False;
		lu = '';
		c = sys.stdin.read(1);
		if c == '^': #{
			in_word = True;
		#}
	#}
	if c.isspace(): #{
		if c == '\n': #{
			idx = 0;
			if len(ambigs) > 1: #{
				keys = ambigs.keys();
				keys.sort();
				for sentence in keys: #{
					if  ambigs[sentence] != '': #{
						sys.stdout.write('[' + str(lineno) + ':' + str(idx) + ' ||\t].[] ' + ambigs[sentence] + c);
						idx = idx + 1;
					#}
				#}
				lineno = lineno + 1;
			#}
			ambigs = {};
			ambigs[''] = '';

#			b = len(str(xx)) * "\b";
#			sys.stderr.write(b + str(xx));
#			xx = xx + 1;
		else: #{
			for sentence in ambigs: #{
				ambigs[sentence] = ambigs[sentence] + c;
			#}
		#}
	#}

	if in_word == True: #{
		lu = lu + c;
	#}
	
	c = sys.stdin.read(1);
#}
