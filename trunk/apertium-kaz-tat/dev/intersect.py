#!/usr/bin/python3
# coding=utf-8
# -*- encoding: utf-8 -*-

#
#  Read three frequency lists and find the intersection.
#    (Find Russian loanwords in Turkic languages.)
#
#    $ python3 intersect.py /tmp/ru /tmp/kk /tmp/tt 
#
#  Consider preprocessing lists before running the intersection, f.ex.
#
#    ия.* -> и (to get other morphological forms of words ending in -ия)
#    изм.* -> изм (for other morf. forms of words ending in -изм)
#
#  You can also use it on a list of f.ex. nouns, if you know that the 
#  list only contains nouns, then just run:
# 
#    $ python3 intersect.py /tmp/ru-nouns /tmp/kk /tmp/tt
#
#  And in this case it is pretty much safe to lowercase all the input.

# Sample input: 
#
#   ru:
#       603 выше
#       602 очередь
#       600 общества
#       598 широко
#       596 кроме
#        ...
#   tt: 
#        24 китап
#        24 әсәрләре
#        23 башлана
#        23 басылып
#        23 алар
#        ...
#
# Sample output:
#  
#  9 1 1 драматургия
#  4 5 1 комедия
#  4 2 1 гимназия
#  3 1 1 студия
#  3 1 1 премия
#  3 1 1 идеология
#  2 5 1 цивилизация
#  ...
#

import sys, codecs, copy;

ru = {};
tt = {};
kk = {};

ru_f = open(sys.argv[1]);
tt_f = open(sys.argv[2]);
kk_f = open(sys.argv[3]);

for line in ru_f.readlines(): #{
	ru_q = line.strip().split(' ')[0];
	ru_w = ' '.join(line.strip().split(' ')[1:]);

	if ru_w not in ru: #{
		ru[ru_w] = ru_q;	
	#}
#}

for line in tt_f.readlines(): #{
	tt_q = line.strip().split(' ')[0];
	tt_w = ' '.join(line.strip().split(' ')[1:]);

	if tt_w not in tt: #{
		tt[tt_w] = tt_q;	
		tt[tt_w.lower()] = tt_q;	
	#}
#}

for line in kk_f.readlines(): #{
	kk_q = line.strip().split(' ')[0];
	kk_w = ' '.join(line.strip().split(' ')[1:]);

	if kk_w not in kk: #{
		kk[kk_w] = kk_q;	
		kk[kk_w.lower()] = kk_q;	
	#}
#}

for w in ru: #{

	if w in kk and w in tt: #{
		print(kk[w], tt[w], ru[w], w);
	#}
#}
