#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, commands, os;

sys.stdin = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

sl = file(sys.argv[1]); 
tl = file(sys.argv[2]);

sentence = {};
curr = 0;

# 2 2 [2:2 ||	].[] ^El/El<det><def><m><sg>$ ^territori/territori<n><m><sg>$ ^era/ser<vbser><past><p3><sg>$ ^habitat/habitar<vblex><pp><m><sg>$   ^des de/des de<pr>$ ^temps/temps<n><:0><m><sp>$ ^per/per<pr>$ ^tribus/tribu<n><f><pl>$ ^la/el<det><def><f><sg>$ ^llengua/llengua<n><:1><f><sg>$   ^dels quals/de<pr>+el qual<rel><an><m><pl>$ ^no/no<adv>$ ^entenien/entendre<vblex><pii><p3><pl>$ 
#-2.05685	||	[2:2 ||	].[] The territory was inhabited   since time for tribes the tongue   of which did not understand

while True: #{
	sll = sl.readline().strip();
	tll = tl.readline().strip();

	if sll != '' and tll != '': #{
		slid = sll.split('||')[0].split('[')[1].strip();
		tlid = tll.split('||')[1].split('[')[1].strip();
		if slid != tlid: #{
			print >> sys.stderr , "SL and TL corpora are inconsistent";
			break; # In future make this just skip until the next ID
		#}
		sent = slid.split(':')[0];
		index = slid.split(':')[1];

#		print sent , index , sll , tll;
		if sent != curr: #{
			total_prob = 0.0;
			lus = [];
			probs = {};
			for i in sentence: #{
				total_prob = total_prob + sentence[i][0];
			#}
			for i in sentence: #{
				probs[i] = sentence[i][0] / total_prob;
			#}
			print probs;

			j = 0;
			for i in sentence: #{
				for lu in sentence[i][1].split('$ ^'): #{
					lus.append((j, lu));
					j = j + 1;
				#}	
				j = 0;
			#}
			lus = set(lus);
			lus = list(lus);
			print str(total_prob) , sentence;
			print lus;
			sentence = {};
			curr = sent;
		#}
		tlprob = float(tll.split('||')[0].strip());
		slsent = sll.split('.[]')[1].strip();
		sentence[index] = (tlprob, slsent);
	else: #{
		break;
	#}
#}

sl.close();
tl.close();
