#!/usr/bin/python2.5
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy;

sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);
sys.stdin = codecs.getreader('utf-8')(sys.stdin);


for line in sys.stdin.read().split('\n'): #{

	mid = '';
	if line.count('<i>') > 0: #{
		mid = line.replace('<i>', '@').replace('</i>', '@').split('@')[1];
		print '    <e><p><l>' + mid + '</l><r>' + mid + '</r></p></e>';
	else: #{
		print line;
	#}
#}
