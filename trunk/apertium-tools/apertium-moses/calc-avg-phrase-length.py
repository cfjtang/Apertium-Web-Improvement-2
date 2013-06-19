#!/usr/bin/python
# -*- coding: utf-8 -*-
import sys;

# The input is the output from ./moses -translation-details FILENAME
#
#TRANSLATION HYPOTHESIS DETAILS:
#         SOURCE: [0..2] calls on the
#  TRANSLATED AS: felkéri a
#  WORD ALIGNED: 
#         SOURCE: [3..3] Commission
#  TRANSLATED AS: Commission
#  WORD ALIGNED: 0-0 
#         SOURCE: [4..4] to
#  TRANSLATED AS: a
#  WORD ALIGNED: 
#         SOURCE: [5..6] put forward
#  TRANSLATED AS: leghamarabb adandó alkalommal előterjeszti
#  WORD ALIGNED: 
#         SOURCE: [10..10] the
#  TRANSLATED AS: a
#  WORD ALIGNED: 
#         SOURCE: [11..11] GCC
#  TRANSLATED AS: GCC
#

span = '';
sseg = '';
tseg = '';
trans = {};
inDeets = False;
num_sent = 0.0;
avg_ng_len_sl = 0.0;
avg_ng_len_tl = 0.0;
trim_seg = 0.0;
if len(sys.argv) > 1: #{
	trim_seg = float(sys.argv[1]);
#}
for line in sys.stdin.readlines(): #{
	if line.strip() == '': #{
		continue;
	#}
	#print(line);
	if line.count('SOURCE/TARGET SPANS:') > 0: #{
		inDeets = False;
	#}
	if line.count('TRANSLATION HYPOTHESIS DETAILS:') > 0: #{
		k = list(trans.keys());
		k.sort();
		#print(trans)
		out = '';
		ng_len_sl = 1.0;
		ng_len_tl = 1.0;
		num_ngrams = 1.0;
		for i in k: #{
			out = out + '{' + trans[i][0] + ' ||| ' + trans[i][1] + '} '
			num_ngrams = num_ngrams + 1.0;
			ng_len_sl = ng_len_sl + float(trans[i][0].count(' ')) + 1.0;
			ng_len_tl = ng_len_tl + float(trans[i][1].count(' ')) + 1.0;
		#}
		#print(ng_len_sl, ng_len_tl, num_ngrams);
		if trim_seg != 0.0 and ng_len_sl > trim_seg: #{
			num_sent = num_sent + 1;
			avg_ng_len_sl = avg_ng_len_sl + (ng_len_sl/num_ngrams);
			avg_ng_len_tl = avg_ng_len_tl + (ng_len_tl/num_ngrams);
			print('%2f %2f\t%s' % (ng_len_sl/num_ngrams, ng_len_tl/num_ngrams,  out));
		elif trim_seg == 0.0: #{
			num_sent = num_sent + 1;
			avg_ng_len_sl = avg_ng_len_sl + (ng_len_sl/num_ngrams);
			avg_ng_len_tl = avg_ng_len_tl + (ng_len_tl/num_ngrams);
			print('%2f %2f\t%s' % (ng_len_sl/num_ngrams, ng_len_tl/num_ngrams,  out));
		#}
		inDeets = True;
		trans = {};
	#}
	
	if line.count('SOURCE:') > 0 and inDeets: #{
		span = line.split(']')[0].split('[')[1];
		sseg = line.split(']')[1];
	#}

	if line.count('TRANSLATED AS:') > 0 and inDeets: #{
		tseg = line.split(':')[1];
	#}

	if line.count('WORD ALIGNED:') > 0 and inDeets: #{
		trans[span] = (sseg.strip(), tseg.strip());		

		sseg = '';
		tseg = '';
		span = '';	
	#}
#}
print('sl: %2f ; tl: %2f' % (avg_ng_len_sl/num_sent, avg_ng_len_tl/num_sent));
