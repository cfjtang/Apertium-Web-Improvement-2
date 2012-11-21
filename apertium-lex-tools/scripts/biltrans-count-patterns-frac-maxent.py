#!/usr/bin/python3
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, math;

# Input:
#	a) Frequency lexicon

# 0.9983989491 kemennadur<n> précepte<n> @
# 0.0016010509 kemennadur<n> directive<n>
# 2.9999998997 a-led<adv> horizontal<adv> @
# 0.0000001004 a-led<adv> à~plat<adv>
# 20.4545497200 pazenn<n> étape<n> @
# 2.5454502800 pazenn<n> marche<n>
# 

#	b) Biltrans output

# 56011   ^un<det><ind><sp>/un<det><ind><GD><ND>$ ^digarez<n><m><sg>/excuse<n><f><sg>/occasion<n><f><sg>$ ^da<pr>/à<pr>$ ^distreiñ<vblex><inf>/revenir<vblex><inf>$ ^war<pr>/sur<pr>$ ^e<det><pos><m><sp>/son<det><pos><GD><ND>$ ^doare<n><m><sg>/manière<n><f><sg>$ ^ober<vblex><inf>/faire<vblex><inf>$ ^.<sent>/.<sent>$


#	c) Disambiguated biltrans output  

#.[][56011 0].[] ^un<det><ind><sp>/un<det><ind><GD><ND>$ ^digarez<n><m><sg>/excuse<n><f><sg>$ ^da<pr>/à<pr>$ ^distreiñ<vblex><inf>/revenir<vblex><inf>$ ^war<pr>/sur<pr>$ ^e<det><pos><m><sp>/son<det><pos><GD><ND>$ ^doare<n><m><sg>/manière<n><f><sg>$ ^ober<vblex><inf>/faire<vblex><inf>$ ^.<sent>/.<sent>$^.<sent>/.<sent>$ 0.9917274061    |@|
#.[][56011 1].[] ^un<det><ind><sp>/un<det><ind><GD><ND>$ ^digarez<n><m><sg>/occasion<n><f><sg>$ ^da<pr>/à<pr>$ ^distreiñ<vblex><inf>/revenir<vblex><inf>$ ^war<pr>/sur<pr>$ ^e<det><pos><m><sp>/son<det><pos><GD><ND>$ ^doare<n><m><sg>/manière<n><f><sg>$ ^ober<vblex><inf>/faire<vblex><inf>$ ^.<sent>/.<sent>$^.<sent>/.<sent>$       0.0082725939    ||

MAX_NGRAMS = 3; # Max = 5-grams

cur_line = 0;


sl_tl_defaults = {}; 
sl_tl = {};
ngrams = {};

meevents = {}; # events[slword][counter] = [feat, feat, feat];
meoutcomes = {}; # meoutcomes[slword][counter] = tlword;
event_counter = 0;

features = {}; # features[(slword, ['a', 'list'], tlword)] = 3
feature_counter = 0;

indexes = {};
trad_counter = {};

am_counter = 0;
dm_counter = 0;


# First read in the frequency defaults

for line in open(sys.argv[1]).readlines(): #{
	line = line.strip();
	if len(line) < 1: #{
		continue;
	#}
	row = line.split(' ');
	sl = row[1];
	tl = row[2];
	if sl not in trad_counter: #{
		trad_counter[sl] = 0;
	#}
	if sl not in sl_tl: #{
		sl_tl[sl] = [];
	#}
	if line.count('@') > 0: #{
		sl_tl_defaults[sl] = tl;
		sl_tl[sl].append(tl);
		indexes[(sl, tl)] = trad_counter[sl];
		trad_counter[sl] = trad_counter[sl] + 1;
	else: #{
		sl_tl[sl].append(tl);
		indexes[(sl, tl)] = trad_counter[sl];
		trad_counter[sl] = trad_counter[sl] + 1;
	#}
#}

for sl in sl_tl: #{
	sl_tl[sl] = set(sl_tl[sl]);
	for tl in sl_tl[sl]: #{
		if tl == sl_tl_defaults[sl]: #{
			print('-', trad_counter[sl], indexes[(sl, tl)], sl, tl + '*', file=sys.stderr);
		else: #{
			print('-', trad_counter[sl], indexes[(sl, tl)], sl, tl, file=sys.stderr);
		#}
	#}
#}

am_file = open(sys.argv[2]); # File with ambiguous biltrans output
dm_file = open(sys.argv[3]); # File with disambiguated biltrans output
reading = True;

current_am_line_id = -1;
current_dm_line_id = -1;

dm_line = dm_file.readline();
current_dm_line_id = int(dm_line.split('.[][')[1].split(' ')[0]);


while reading: #{
	am_line = am_file.readline();

	if am_line == '': #{
		reading = False;
		continue;
	#}

	current_am_line_id = int(am_line.split('\t')[0]);

	# to skip lines in the frac corpus if we have a sub-corpus
	if current_dm_line_id != current_am_line_id: #{
		while current_dm_line_id != current_am_line_id: #{
			dm_line = dm_file.readline();
			current_dm_line_id = int(dm_line.split('.[][')[1].split(' ')[0]);
		#}
	#}

	while current_dm_line_id == current_am_line_id: #{

		if am_line.count('$ ^') != dm_line.count('$ ^'): #{
			print('Mismatch in number of LUs between analysis and training', file=sys.stderr);
			print('\t' + am_line, file=sys.stderr);
			print('\t' + dm_line, file=sys.stderr);
			print('...skipping', file=sys.stderr);
			continue;
		#}
	
	
		am_row = am_line.split('\t')[1].replace('$^', '$ ^')[1:-1].split('$ ^');
		dm_row = dm_line.split('\t')[1].replace('$^', '$ ^')[1:-1].split('$ ^');
		#print(dm_row, file=sys.stderr)
		cur_sl_row = [];
		for lu in am_row: #{
			sl = lu.split('/')[0];
			if sl.count('><') > 0: #{
				sl = sl.split('><')[0] + '>';
			#}
			cur_sl_row.append(sl);
		#}

		#frac_count = float(dm_line.split('\t')[2]);

		frac_count = 0.0;
		s_fc = dm_line.split('\t')[2].strip();
		if s_fc == '' or len(s_fc) == 0: #{
			print('%d %d :: %d %d :: Frac count is not floatable' % (am_counter, dm_counter, current_am_line_id, current_dm_line_id), file=sys.stderr);
		#}
		frac_count = float(s_fc);
		if math.isnan(frac_count): #{ 
			print('%d %d :: %d %d :: Frac count is not a number' % (am_counter, dm_counter, current_am_line_id, current_dm_line_id), file=sys.stderr);
			frac_count = 0.0;
		#}

	
		limit = len(am_row);
		for i in range(0, limit): #{
			if am_row[i].count('/') > 1: #{
				#print(am_row[i] , dm_row[i]); 
				sl = am_row[i].split('/')[0].replace(' ', '~');
				tl = dm_row[i].split('/')[1].replace(' ', '~');
				if sl.count('><') > 0: #{
					sl = sl.split('><')[0] + '>';
				#}
				if tl.count('><') > 0: #{
					tl = tl.split('><')[0] + '>';
				#}
	
#				if tl !=  sl_tl_defaults[sl]: #{
#					print('+' , sl , sl_tl_defaults[sl] , tl, file=sys.stderr);
#				else: #{
#					print('-' , sl , sl_tl_defaults[sl] , tl, file=sys.stderr);
#				#}
	
				for j in range(1, MAX_NGRAMS): #{
					pregram = ' '.join(cur_sl_row[i-j:i+1]);
					postgram = ' '.join(cur_sl_row[i:i+j+1]);
					roundgram = ' '.join(cur_sl_row[i-j:i+j+1]);
	
					if sl not in ngrams: #{
						ngrams[sl] = {};
					#}
					if pregram not in ngrams[sl]: #{
						ngrams[sl][pregram] = {};
					#}
					if postgram not in ngrams[sl]: #{
						ngrams[sl][postgram] = {};
					#}
					if roundgram not in ngrams[sl]: #{
						ngrams[sl][roundgram] = {};
					#}
					if tl not in ngrams[sl][pregram]: #{
						ngrams[sl][pregram][tl] = 0.0;
					#}
					if tl not in ngrams[sl][postgram]: #{
						ngrams[sl][postgram][tl] = 0.0;
					#}
					if tl not in ngrams[sl][roundgram]: #{
						ngrams[sl][roundgram][tl] = 0.0;
					#}
	
					ngrams[sl][pregram][tl] = ngrams[sl][pregram][tl] + frac_count;
					ngrams[sl][postgram][tl] = ngrams[sl][postgram][tl] + frac_count;
					ngrams[sl][roundgram][tl] = ngrams[sl][roundgram][tl] + frac_count;
				#}
				if sl not in meevents: #{
					meevents[sl] = {};
				#}
				if sl not in meoutcomes: #{
					meoutcomes[sl] = {};
				#}
				if event_counter not in meevents: #{
					meevents[sl][event_counter] = [];
				#}
				if event_counter not in meoutcomes: #{
					meoutcomes[sl][event_counter] = '';
				#}
				for ni in ngrams[sl]: #{
					if ni not in features: #{
						feature_counter = feature_counter + 1;
						features[ni] = feature_counter;
					#}
					meevents[sl][event_counter].append(features[ni]);
					#meevents[sl][event_counter].append(feat);
					#meoutcomes[sl][event_counter] = (tl, frac_count);
					meoutcomes[sl][event_counter] = (tl, int(frac_count * 10000));

				#}
				del ngrams;
				ngrams = {};
				if sl not in sl_tl:  #{
					continue;
				#}
				if len(sl_tl[sl]) < 2: #{
					continue;
				#}
				for event in meevents[sl]: #{
					outline = str(indexes[(sl, meoutcomes[sl][event][0])]) + ' $ ' ;
					outline = outline + str(meoutcomes[sl][event][1]) + ' # ';
					for j in range(0,  len(sl_tl[sl])): #{
						for feature in meevents[sl][event]: #{
							outline = outline + str(feature) + ':' + str(j) + ' ';
						#}
						outline = outline + ' # '
					#}
					print(sl , '\t', len(sl_tl[sl]),'\t', outline);
				#}
				del meevents;
				del meoutcomes;
				meevents = {};
				meoutcomes = {};
			#}
		#}
		
		dm_line = dm_file.readline();
		if dm_line == '': #{
			reading = False;
			break;
		#}
		current_dm_line_id = int(dm_line.split('.[][')[1].split(' ')[0]);
		event_counter = event_counter + 1;

		dm_counter += 1;

	#}
	am_counter += 1;

#}

for feature in features: #{
	print(features[feature] , '\t' , feature, file=sys.stderr);
#}

