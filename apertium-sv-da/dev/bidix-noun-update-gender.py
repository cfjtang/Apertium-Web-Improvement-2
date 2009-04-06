#!/usr/bin/python2.5
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy;

sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);
sys.stdin = codecs.getreader('utf-8')(sys.stdin);

lgenf = file('/tmp/sv-gen').read().split('\n');
rgenf = file('/tmp/da-gen').read().split('\n');

lgen = {};
for line in lgenf: #{
	if line.count(':') < 1: #{
		continue;
	#}
	row = line.split(':');
	lgen[row[0].decode('utf8')] = row[1];
#}

rgen = {};
for line in rgenf: #{
	if line.count(':') < 1: #{
		continue;
	#}
	row = line.split(':');
	rgen[row[0].decode('utf8')] = row[1];
#}

for line in sys.stdin.read().split('\n'): #{

	mid = '';
	if line.count('<s n="n"/><s n="GD"/>') > 0: #{
		llemma = line.split('<l>')[1].split('<s')[0];
		rlemma = line.split('<r>')[1].split('<s')[0];
		left = line.split('</l><r>')[0];
		right = line.split('</l><r>')[1];
		if llemma in lgen: #{
			left = '    <e><p><l>' + llemma + '<s n="n"/><s n="' + lgen[llemma] + '"/>';
		#}
		if rlemma in rgen: #{
			right = '<r>' + rlemma + '<s n="n"/><s n="' + rgen[rlemma] + '"/></r></p></e>';
		#}
			
		print left + '</l><r>' + right;

	else: #{
		print line;
	#}
#}
