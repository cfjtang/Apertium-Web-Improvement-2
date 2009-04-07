#!/usr/bin/python2.5
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy;

sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);
sys.stdin = codecs.getreader('utf-8')(sys.stdin);

seen = {};
bidix = '/home/fran/source/apertium/trunk/apertium-sv-da/apertium-sv-da.sv-da.dix';

for line in file(bidix).read().split('\n'): #{
	if line.count('<s n="n"/>') > 0 and line.count('<e') > 0: #{
		svlem = line.replace('<l>', '@').replace('<s', '@').split('@')[1];
		seen[svlem] = line;
	
#		print '~' , svlem , seen[svlem];
	#}
#}

#
# akutmottagning<n><ut>/akutmottagning<n><nt>:skadestue<n><ut>.
# alarm<n><nt>:alarm<n><ut>.
# album<n><nt>:album<n><nt>.
# alfabet<n><nt>:alfabet<n><nt>.
#
for line in sys.stdin.read().split('\n'): #{
	if len(line) < 2: #{
		continue;
	#}
	row = line.split(':');
	if row[0].count('/') > 0: #{
		sv = row[0].split('/')[0];
	else: #{
		sv = row[0];
	#}
	if row[1].count('/') > 0: #{
		da = row[1].split('/')[0];
	else: #{
		da = row[1];
	#}

	svlem = sv.replace('<n>', '').split('<')[0];
	dalem = da.replace('<n>', '').split('<')[0];

	svgen = ''.join(sv.replace('<n>', '').split('<')[1:]).replace('>', '').replace('.', '');
	dagen = ''.join(da.replace('<n>', '').split('<')[1:]).replace('>', '').replace('.', '');

	if svlem in seen: #{
#		print seen[svlem];
		continue;
	#}
#	print '+' , svlem + '(' + svgen + ') : ' , dalem + '(' + dagen + ')';
	print '    <e><p><l>' + svlem + '<s n="n"/><s n="' + svgen + '"/></l><r>' + dalem + '<s n="n"/><s n="' + dagen + '"/></r></p></e>'; 
#}
