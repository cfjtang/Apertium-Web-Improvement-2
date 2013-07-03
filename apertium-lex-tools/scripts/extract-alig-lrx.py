#!/usr/bin/python3
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys;

d = open(sys.argv[1]);

print('<rules>');
for line in d.readlines(): #{
	try:
		sys.stdout.flush();
		if line[-2] == '@': #{
			row = line.split(' ');
	
			fq = row[0];
			sl = row[1];
			tl = row[2];

			if line.count('>') < 2: #{
				continue;
			#}

			sl_lem = sl.split('<')[0];
			tl_lem = tl.split('<')[0];
			sl_lem = sl_lem.replace('-', '\\-').replace('~', ' ').replace('&', '&amp;');
			tl_lem = tl_lem.replace('-', '\\-').replace('~', ' ').replace('&', '&amp;');

			sl_tag = sl.replace('><', '.').split('<')[1].strip('>');
			tl_tag = tl.replace('><', '.').split('<')[1].strip('>');

			cmb = '';
			cma = '';
			if sl_tag not in ['adj', 'vblex', 'n']: #{
				cmb = '<!--';
				cma = '-->';	
			else: #{
				cma = '';
				cmb = '';
			#}

			rule = cmb + '<rule comment="' + fq + '">';
			#rule = rule + '<match lemma="' + sl_lem + '" tags="' + sl_tag + '"><select lemma="' + tl_lem + '" tags="' + tl_tag + '"/>';	
			rule = rule + '<match lemma="' + sl_lem + '"><select lemma="' + tl_lem + '"/>';	
			rule = rule + '</match>';
			rule = rule + '</rule>' + cma;
		
			print(rule);
		#}
	except:
		pass

#}
print('</rules>');
