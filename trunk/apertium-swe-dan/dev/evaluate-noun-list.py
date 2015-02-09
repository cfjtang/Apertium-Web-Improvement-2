#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, commands;

sys.stdin  = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

list = file(sys.argv[1]);

invalidTags = ['<sent>'];

def rollDice(a, b): #{
        # bigram overlap * 2 / bigrams in a + bigrams in b
 
        overlap = 0;
        a_bigrams = set([a[i:i+2] for i in range(len(a) - 1)]);
        b_bigrams = set([b[i:i+2] for i in range(len(b) - 1)]);
 
        # Could this be replaced with:
        #  overlap = len(a_bigrams.intersection(b_bigrams));
        #
        for abi in a_bigrams: #{
                for bbi in b_bigrams: #{
                        if(bbi == abi): #{
                                overlap = overlap + 1;
                        #}
                #}
        #}               
 
        total = ((len(a_bigrams)) + (len(b_bigrams)));
        x = float(overlap * 2);
        dice  = x / float(total);
 
        return dice;
#}

def returnLemGen(s): #{
	x = s.split('/')[1];
	y = x.replace('<sp>', '@');	
	y = y.replace('<sg>', '@');	
	y = y.replace('<pl>', '@');	
	return y.split('@')[0];
#}

def returnLemma(s): #{
	return s.replace('<n>', '@').split('@')[0];
#}

def cleanLine(s): #{
	x = s.replace('^,/,<cm>$', '');
	x = x.replace('^:/:<sent>$', '');
	
	return x;
#}


# 
# 1.0000000	 ^zon/zon<n><ut><sg><ind><nom>$	 ^zone/zone<n><ut><sg><ind><nom>$
# 1.0000000	 ^yrkets/yrke<n><nt><sg><def><gen>$	 ^sygeplejens/sygepleje<n><ut><sg><def><gen>$
# 1.0000000	 ^yoghurt/yoghurt<n><ut><sg><ind><nom>$	 ^yoghurt/yoghurt<n><ut><sg><ind><nom>$
# 1.0000000	 ^vykort/vykort<n><nt><sg><ind><nom>$	 ^postkort/postkort<n><nt><sg><ind><nom>$
# 1.0000000	 ^vrak/vrak<n><nt><sg><ind><nom>$	 ^vrag/vrag<n><nt><sg><ind><nom>$
# 1.0000000	 ^vis/vis<n><nt><sp><ind><nom>$	 ^vis/vis<n><ut><sg><ind><nom>$
# 1.0000000	 ^visitkort/visitkort<n><nt><sg><ind><nom>$	 ^visitkort/visitkort<n><nt><sg><ind><nom>$
# 1.0000000	 ^visa/visum<n><nt><pl><ind><nom>$	 ^borgere/borger<n><ut><pl><ind><nom>$

dicc = {};

for line in list.read().split('\n'): #{
	line = cleanLine(line);

	if line.count('^') != 2 or line.count('$') != 2: #{
		continue;
	#}
	if line.count('$^') > 0 or line.count('<sent>') > 0: #{
		continue;
	#}

	row = line.split('\t');

	#print row[0] , returnLemGen(row[1]) , row[1] , returnLemGen(row[2]) , row[2];

	left = returnLemGen(row[1]);
	right = returnLemGen(row[2]);

	if left.count('<n>') > 0 and right.count('<n>') > 0: #{
		key = (left , right);
	else: #{
		continue;
	#}

	dicc[key] = (row[1], row[2]);
#}

def lemSymtoXML(s): #{

	return s.replace('<n><', '<s n="n"/><s n="').replace('>', '"/>');
#}

final = {};

for i in dicc.keys(): #{
	leml = dicc[i][0];
	lemr = dicc[i][1];

	print '    <e><p><l>' + lemSymtoXML(i[0]) + '</l><r>' + lemSymtoXML(i[1])  + '</r></p></e>';

#}
