#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy;

sys.stdin  = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

hitparade = '/home/fran/corpora/wiki/sv/sv.hitparade.txt';

coverage = 80.0;
if len(sys.argv) > 1: #{
	coverage = float(sys.argv[1]);
#}

total = 0.0;
counts = {};

for line in file(hitparade).read().split('\n'): #{
	if len(line) < 2: #{
		continue;
	#}
	row = line.strip().split(' ');
	if len(row) < 2: #{
		continue;
	#}

	counts[row[1]] = float(row[0]);
	total = total + counts[row[1]];

#	print total , row[0] , row[1];
#}

fraction = (total / 100.0) * coverage;

print total;
print fraction;

count = 0;

total = 0.0

for line in file(hitparade).read().split('\n'): #{
        if len(line) < 2: #{
                continue;
        #}
        row = line.strip().split(' ');
        if len(row) < 2: #{
                continue;
        #}

	count = count + 1;
	total = total + counts[row[1]];

	print line;

	if total >= fraction: #{
		print count;
		sys.exit(0);
	#}
#}
