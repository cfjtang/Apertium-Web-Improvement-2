#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

#-2.45835	||	[2782:0 ||	].[] The climate is tropical with humid station and dry station (savannah).
#-2.45219	||	[2782:1 ||	].[] The climate is tropical with humid station and dry season (savannah).
#-2.45219	||	[2782:2 ||	].[] The climate is tropical with humid season and dry station (savannah).
#-2.44603	||	[2782:3 ||	].[] The climate is tropical with humid season and dry season (savannah).

#[2782:0 ||	].[] ^El/El<det><def><m><sg>$ ^clima/clima<n><m><sg>$ ^és/ser<vbser><pri><p3><sg>$ ^tropical/tropical<adj><mf><sg>$ ^amb/amb<pr>$ ^estació/estació<n><:0><f><sg>$ ^humida/humit<adj><f><sg>$ ^i/i<cnjcoo>$ ^estació/estació<n><:0><f><sg>$ ^seca/sec<adj><f><sg>$ ^(/(<lpar>$^sabana/sabana<n><f><sg>$^)/)<rpar>$^./.<sent>$
#[2782:1 ||	].[] ^El/El<det><def><m><sg>$ ^clima/clima<n><m><sg>$ ^és/ser<vbser><pri><p3><sg>$ ^tropical/tropical<adj><mf><sg>$ ^amb/amb<pr>$ ^estació/estació<n><:0><f><sg>$ ^humida/humit<adj><f><sg>$ ^i/i<cnjcoo>$ ^estació/estació<n><:1><f><sg>$ ^seca/sec<adj><f><sg>$ ^(/(<lpar>$^sabana/sabana<n><f><sg>$^)/)<rpar>$^./.<sent>$
#[2782:2 ||	].[] ^El/El<det><def><m><sg>$ ^clima/clima<n><m><sg>$ ^és/ser<vbser><pri><p3><sg>$ ^tropical/tropical<adj><mf><sg>$ ^amb/amb<pr>$ ^estació/estació<n><:1><f><sg>$ ^humida/humit<adj><f><sg>$ ^i/i<cnjcoo>$ ^estació/estació<n><:0><f><sg>$ ^seca/sec<adj><f><sg>$ ^(/(<lpar>$^sabana/sabana<n><f><sg>$^)/)<rpar>$^./.<sent>$
#[2782:3 ||	].[] ^El/El<det><def><m><sg>$ ^clima/clima<n><m><sg>$ ^és/ser<vbser><pri><p3><sg>$ ^tropical/tropical<adj><mf><sg>$ ^amb/amb<pr>$ ^estació/estació<n><:1><f><sg>$ ^humida/humit<adj><f><sg>$ ^i/i<cnjcoo>$ ^estació/estació<n><:1><f><sg>$ ^seca/sec<adj><f><sg>$ ^(/(<lpar>$^sabana/sabana<n><f><sg>$^)/)<rpar>$^./.<sent>$

import sys, codecs, copy, commands, os;

sys.stdin = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

class Phrase: #{
	index = 0; 		# 0
	prob = 0.0;		# -2.45835
	s_tagged = '';		# ^El/El<det><def><m><sg>$ ^clima/clima<n><m><sg>$ ^és/ser<vbser><pri><p3><sg>$ ...
	m_surface = '';		# The climate is tropical with humid station and dry station

	def __init__(self, _index, _prob, m_phr, s_phr): #{
		self.index = _index;
		self.prob = _prob;
		self.s_tagged = s_phr;
		self.m_surface = m_phr;
	#}

	def ngrams_surface_with_pos(self): #{
		# only interested in ngrams with ambiguous words inside
		# returns [{0: estació, 1: seca}, {0: estació, 1: humida}, {-1: amb, 0: estació}, ...]
		
		pos = 0;
		unigrams = [];
		ngrams = [];
		for unigram in self.s_tagged.replace('$^', '$ ^').split('$ ^'): #{
			pos = pos + 1;
			unigrams.append(unigram.replace('^','').strip());	
			if unigram.count(':') > 0: #{
				ngrams.append({0: unigram.replace('^','').split('/')[0]});
			#}
		#}
		pos = 0;

		for i in range(0, len(unigrams)-1): #{
			pos = pos + 1;
			bigram = unigrams[i-1] + ' ' + unigrams[i];
			if unigrams[i-1].count(':') > 0 and unigrams[i].count(':') < 1: #{
				ngrams.append({0: unigrams[i-1].split('/')[0], 1: unigrams[i].split('/')[0]});
			#}
			if unigrams[i-1].count(':') < 1 and unigrams[i].count(':') > 0: #{
				ngrams.append({-1: unigrams[i-1].split('/')[0], 0: unigrams[i].split('/')[0]});
			#}
		#}
		pos = 0;

		for i in range(0, len(unigrams)-2): #{
			pos = pos + 1;
			trigram = unigrams[i-2] + ' ' + unigrams[i-1] + ' ' + unigrams[i];
			if unigrams[i-2].count(':') > 0 and unigrams[i-1].count(':') < 1 and unigrams[i].count(':') < 1: #{
				ngrams.append({0: unigrams[i-2].split('/')[0], 1: unigrams[i-1].split('/')[0], 2: unigrams[i].split('/')[0]});
			#}
			if unigrams[i-2].count(':') < 1 and unigrams[i-1].count(':') > 0 and unigrams[i].count(':') < 1: #{
				ngrams.append({-1: unigrams[i-2].split('/')[0], 0: unigrams[i-1].split('/')[0], 1: unigrams[i].split('/')[0]});
			#}
			if unigrams[i-2].count(':') < 1 and unigrams[i-1].count(':') < 1 and unigrams[i].count(':') > 1: #{
				ngrams.append({-2: unigrams[i-2].split('/')[0], -1: unigrams[i-1].split('/')[0], 0: unigrams[i].split('/')[0]});
			#}
		#}
		print >> sys.stderr, ngrams;

		return ngrams;
	#}

	def ngrams_lemma_with_pos(self): #{
		# only interested in ngrams with ambiguous words inside
		# returns [{0: estació, 1: seca}, {0: estació, 1: humit}, {-1: amb, 0: estació}, ...]
		
		pos = 0;
		unigrams = [];
		ngrams = [];
		for unigram in self.s_tagged.replace('$^', '$ ^').split('$ ^'): #{
			pos = pos + 1;
			unigrams.append(unigram.replace('^','').strip());	
			if unigram.count(':') > 0: #{
				ngrams.append({0: unigram.replace('^','').split('/')[1].split('<')[0]});
			#}
		#}
		pos = 0;

		for i in range(0, len(unigrams)-1): #{
			pos = pos + 1;
			bigram = unigrams[i-1] + ' ' + unigrams[i];
			if unigrams[i-1].count(':') > 0 and unigrams[i].count(':') < 1: #{
				ngrams.append({0: unigrams[i-1].split('/')[1].split('<')[0], 1: unigrams[i].split('/')[1].split('<')[0]});
			#}
			if unigrams[i-1].count(':') < 1 and unigrams[i].count(':') > 0: #{
				ngrams.append({-1: unigrams[i-1].split('/')[1].split('<')[0], 0: unigrams[i].split('/')[1].split('<')[0]});
			#}
		#}
		pos = 0;

		for i in range(0, len(unigrams)-2): #{
			pos = pos + 1;
			trigram = unigrams[i-2] + ' ' + unigrams[i-1] + ' ' + unigrams[i];
			if unigrams[i-2].count(':') > 0 and unigrams[i-1].count(':') < 1 and unigrams[i].count(':') < 1: #{
				ngrams.append({0: unigrams[i-2].split('/')[1].split('<')[0], 1: unigrams[i-1].split('/')[1].split('<')[0], 2: unigrams[i].split('/')[1].split('<')[0]});
			#}
			if unigrams[i-2].count(':') < 1 and unigrams[i-1].count(':') > 0 and unigrams[i].count(':') < 1: #{
				ngrams.append({-1: unigrams[i-2].split('/')[1].split('<')[0], 0: unigrams[i-1].split('/')[1].split('<')[0], 1: unigrams[i].split('/')[1].split('<')[0]});
			#}
			if unigrams[i-2].count(':') < 1 and unigrams[i-1].count(':') < 1 and unigrams[i].count(':') > 1: #{
				ngrams.append({-2: unigrams[i-2].split('/')[1].split('<')[0], -1: unigrams[i-1].split('/')[1].split('<')[0], 0: unigrams[i].split('/')[1].split('<')[0]});
			#}
		#}
		print >> sys.stderr, ngrams;

		return ngrams;
	#}
#}

s_phrases = {};
phrases = {}; # ['2782'] = {'0': Phrase(0, -2.45835, 'The climate is ...', '^El/El<det><def><m><sg>$ ...', '1': ...)}

f_s_ambig = sys.argv[1];
f_m_probs = sys.argv[2];

fd_s_ambig = file(f_s_ambig); # File with ambig source lang phrases
fd_m_probs = file(f_m_probs); # File with target lang phrases + probs

for s_line in fd_s_ambig.read().split('\n'): #{
	if len(s_line) < 2: #{
		continue;
	#}
	s_id = s_line.split('||')[0].split(':')[0].strip('[').strip();
	s_idx = s_line.split('||')[0].split(':')[1].strip();
	s_phr = s_line.split('.[]')[1];

	if s_id not in s_phrases: #{
		s_phrases[s_id] = {};
	#}

	s_phrases[s_id][s_idx] = s_phr;
#}


for m_line in fd_m_probs.read().split('\n'): #{
	if len(m_line) < 2: #{
		continue;
	#}
#-2.45835	||	[2782:0 ||	].[] The climate is tropical with humid station and dry station (savannah).
	m_id = m_line.split('||')[1].split(':')[0].split('[')[1].strip();
	m_idx = m_line.split('||')[1].split(':')[1].strip();
	m_prob = float(m_line.split('||')[0].strip());
	m_phr = m_line.split('.[]')[1];

	if m_id not in s_phrases: #{
		print >> sys.stderr, '>>>' , m_line;
		print >> sys.stderr, 'Error: Target phrase' , m_id , 'not found in', f_m_probs;
		sys.exit(-1);
	else: #{
		if m_idx not in s_phrases[m_id]: #{
			print >> sys.stderr , '>>>' , m_line;
			print >> sys.stderr , 'Error: Target sub-phrase' , m_id + ':' + m_idx , 'not found in', f_m_probs;
			print >> sys.stderr , s_phrases[m_id];
			sys.exit(-1);
		#}
	#}
	
	if m_id not in phrases: #{
		phrases[m_id] = [];
	#}
	
	phrase = Phrase(m_idx, m_prob, m_phr, s_phrases[m_id][m_idx])
	phrases[m_id].append(phrase);
#}

def trim_phrases(phrase_list): #{
	base = phrase_list[0].prob;
	top = base;
	new_list = [];
		
	for phrase in phrase_list: #{
		if phrase.prob > top: #{
			top = phrase.prob;
		#}
	#}

	new_list.append(phrase_list[0]);
	for i in range(1, len(phrase_list)): #{
		if phrase_list[i].prob < top: #{
			continue;
		#}
		new_list.append(phrase_list[i]);
	#}

	if len(new_list) > 2: #{
		print >> sys.stderr, 'Error: More than two candidate max phrases... skipping.';	
		for p in new_list: #{
			print >> sys.stderr , p.index , p.prob , p.m_surface;
		#}
		return [];
	#}
	
	return new_list;
#}

def pos_and_index_diff(base, new): #{
	# 
	pos_index = ();	

	base_unigrams = [];
	for unigram in base.s_tagged.replace('$^', '$ ^').split('$ ^'): #{
		base_unigrams.append(unigram.replace('^','').strip());
	#}

	new_unigrams = [];
	for unigram in new.s_tagged.replace('$^', '$ ^').split('$ ^'): #{
		new_unigrams.append(unigram.replace('^','').strip());
	#}
	pos = 0;
	for u in base_unigrams: #{
		if u.count(':') > 0: #{
			lem = base_unigrams[pos].split('/')[1].split('<')[0];
			xtags = base_unigrams[pos].split('><')[0:];
			ytags = new_unigrams[pos].split('><')[0:];
			xtags[0] = xtags[0].split('<')[1];
			ytags[0] = ytags[0].split('<')[1];

			print >> sys.stderr , 'idx+diff: ' , pos , xtags, ytags;
			pos_index = (lem, '(' + xtags[0] + ' ' + xtags[1] + ')', '(' + ytags[0] + ' ' + ytags[1] + ')');
			print >> sys.stderr , 'idx+diff: ' , pos_index;
			break;
		#}
		pos = pos + 1;
	#}
	
	return pos_index;
#}

# For each of the ids we need to find: the one[s] with the greatest difference from the baseline.
#                                      the words in src that need to be changed in trg [estació:0 --> estació:1]
#                                      the ngrams around the candidate words [estació seca] [estació humida] [amb estació] [i estació]
#                                      find relative positions [0 estació 1 seca] [-1 amb 0 estació] ...
#				       generate rules SUBSTITUTE (n :0) (n :1) (0 ("estació"ri)) (1 ("seca"ri));
#                                                     SUBSTITUTE (n :0) (n :1) (-1 ("amb"ri)) (0 ("estació"ri));

rindex = 1;
rule_final = {};
for p_id in phrases: #{

	phrases[p_id] = trim_phrases(phrases[p_id]);
	
	if len(phrases[p_id]) != 2: #{
		continue;
	#}

	print >> sys.stderr , p_id , phrases[p_id][0].index , phrases[p_id][0].prob , phrases[p_id][0].m_surface;
	print >> sys.stderr , phrases[p_id][0].s_tagged;
	print >> sys.stderr , p_id , phrases[p_id][1].index , phrases[p_id][1].prob , phrases[p_id][1].m_surface;
	print >> sys.stderr , phrases[p_id][1].s_tagged;

	# First we generate surface form rules
	
	b_ngrams = phrases[p_id][0].ngrams_surface_with_pos();
	n_ngrams = phrases[p_id][1].ngrams_surface_with_pos();

	pos_index = pos_and_index_diff(phrases[p_id][0],  phrases[p_id][1]);

	for ngram in n_ngrams: #{
		nout = 'SUBSTITUTE ' + pos_index[1] + ' ' + pos_index[2] + ' ("' + pos_index[0] + '"ri) ';
		keys = ngram.keys();
		keys.sort();
		for y in keys:
			nout = nout + '(' + str(y) + ' ("<' + ngram[y] + '>"ri)) ';
		#}
		#print nout;
		nout = nout.strip();
		if nout not in rule_final: #{
			rule_final[nout] = 1;
		else: #{
			rule_final[nout] = rule_final[nout] + 1;
		#}
	#}

	# Then we generate lemma rules
	
	b_ngrams = phrases[p_id][0].ngrams_lemma_with_pos();
	n_ngrams = phrases[p_id][1].ngrams_lemma_with_pos();

	for ngram in n_ngrams: #{
		nout = 'SUBSTITUTE ' + pos_index[1] + ' ' + pos_index[2] + ' ("' + pos_index[0] + '"ri) ';
		keys = ngram.keys();
		keys.sort();
		for y in keys:
			nout = nout + '(' + str(y) + ' ("' + ngram[y] + '"ri)) ';
		#}
		#print nout;
		nout = nout.strip();
		if nout not in rule_final: #{
			rule_final[nout] = 1;
		else: #{
			rule_final[nout] = rule_final[nout] + 1;
		#}
	#}

	# Y después de todo, lo que esté definido
#}

items = rule_final.items()
items = [(v, k) for (k, v) in items]
items.sort()
items.reverse()		# so largest is first
items = [(k, v) for (v, k) in items]

for rule in items: #{
	print rule[0].replace('SUBSTITUTE', 'SUBSTITUTE:r' + str(rindex) + ' ') , ' ; # c: ', rule[1];
	rindex = rindex + 1;
#}
