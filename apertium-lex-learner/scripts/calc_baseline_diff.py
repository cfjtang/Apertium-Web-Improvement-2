#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, commands, os;

sys.stdin = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

b_prob = 0.0;
r_prob = 0.0;

b_file = sys.argv[1];
r_file = sys.argv[2];

prob = 0.0;
i = 0;
for line in file(b_file).read().split('\n'): #{
	if len(line) < 2: #{
		continue;
	#}
	prob = prob + float(line.split('||')[0].strip());
	i = i + 1;
#}

b_prob = prob / float(i);

prob = 0.0;
i = 0;
for line in file(r_file).read().split('\n'): #{
	if len(line) < 2: #{
		continue;
	#}
	prob = prob + float(line.split('||')[0].strip());
	i = i + 1;
#}

r_prob = prob / float(i);

print "%f" % (r_prob - b_prob);
