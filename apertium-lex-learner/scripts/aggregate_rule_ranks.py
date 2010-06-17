#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, commands, os;

sys.stdin = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

baseline_score = 0.0;
rule_centre = {};
rule_scores = {}; 
rule_content = {}; 

if len(sys.argv) < 2: #{
	print 'rank_candidate_rules.py <rules.rlx> <temporary dir>';
	sys.exit(-1);
#}

rules_rlx_file = sys.argv[1];
temporary_dir = sys.argv[2];
rule_aggregate_file = sys.argv[2] + '/rule.aggregate.txt';
rule_ranks_file = sys.argv[2] + '/rules.ranked.txt';

###############################################################################

for line in file(rules_rlx_file).read().split('\n'): #{
	if line.count('SUBSTITUTE') > 0: #{
		rule_id = line.split(':')[1].split(' ')[0];
		rule_scores[rule_id] = -10.0;
		rule_centre[rule_id] = line.split('"')[1];
		rule_content[rule_id] = line;
	#}
#}

print >>sys.stderr, '+ Read' , len(rule_scores) , 'rules.';

###############################################################################
# Calculate the total score of the baseline.

baseline_lines = [];
baseline_lines_hash = {};

if not os.path.isfile(temporary_dir + '/baseline.out'): #{
	# If the file does not exist, then they need to run generate_rule_diffs.py
	print 'Please run generate_rule_diffs.py before rank_candidate_rules.py';
	sys.exit(-1);
#}

baseline_lines = file(temporary_dir + '/baseline.out').read().split('\n');

for line in baseline_lines: #{
	if len(line) < 2: #{
		continue;
	#}
	line_score = float(line.split('||')[0].strip());
	line_id = line.split('||')[1].split('[')[1].split(':')[0];
	baseline_lines_hash[line_id] = line_score;
	baseline_score = baseline_score + line_score;
#}

print >>sys.stderr, '+ Total score for ' + str(len(baseline_lines)) + ' sentences is ' + str(baseline_score);

###############################################################################

centres = set();
for centre in rule_centre: #{
	centres.add(rule_centre[centre]);
#}
print >>sys.stderr, '+ There are ' + str(len(centres)) + ' rule centres.';

###############################################################################

print >>sys.stderr, '+ Processing ' + str(len(rule_scores)) + ' rules.'

def rule_id_compare(x, y): #{
	x = int(x.replace('r', ''));	
	y = int(y.replace('r', ''));	
	return x - y;
#}

sorted_rule_ids = list(rule_scores);
sorted_rule_ids.sort(cmp=rule_id_compare);

#baseline_tagged_set = set(file(baseline_tagged_file).read().split('\n'));
count = 0;

rules_phrases = {};

for ag_line in file(temporary_dir + '/rules.ranked.txt').read().split('\n'): #{
	if len(ag_line) < 2: #{
		continue;
	#}

	# -3.78711	||	[5970:0:3:26 || r1	].[]  ...
	#
	# rules_phrases['r1'][5970] = -3.78711;
	#
	prob = float(ag_line.split('||')[0].strip());
	phid = int(ag_line.split('[')[1].split(':')[0].strip());
	rule_id = ag_line.split('||')[2].split(']')[0].strip();

	#print rule_id , phid , prob;

	if rule_id not in rules_phrases: #{
		rules_phrases[rule_id] = {};
	#}
	
	rules_phrases[rule_id][phid] = prob;
#}

baseline_phrases = {};

for baseline_line in file(temporary_dir + '/baseline.out').read().split('\n'): #{
	if len(baseline_line) < 2: #{
		continue;
	#}

	# -3.41661	||	[10:0:4:12 ||	].[] Finnar Defended months...
	#
	# baseline_phrases[5970] = -3.41661;
	#
	prob = float(baseline_line.split('||')[0].strip());
	phid = int(baseline_line.split('[')[1].split(':')[0].strip());

	#print 'baseline' , phid , prob;

	baseline_phrases[phid] = prob;
#}

def rule_id_compare(x, y): #{
	x = int(x.replace('r', ''));	
	y = int(y.replace('r', ''));	
	return x - y;
#}

sorted_rule_ids = list(rules_phrases);
sorted_rule_ids.sort(cmp=rule_id_compare);

all_rule_probs = {};

for rule_id in sorted_rule_ids: #{
	rule_score = 0.0;
	for phid in baseline_phrases: #{
		if phid in rules_phrases[rule_id]: #{
			rule_score = rule_score + rules_phrases[rule_id][phid];
		else: #{
			rule_score = rule_score + baseline_phrases[phid];
		#}
	#}
	#print >> sys.stderr , (rule_score - baseline_score) , rule_id , baseline_score , rule_score;

	all_rule_probs[rule_id] = (rule_score - baseline_score);
#}

alist = sorted(all_rule_probs.iteritems(), key=lambda (k,v): (v,k), reverse=True);

for rule in alist: #{

	# ('r1942', -38.076839999979711)
	#print '%.5f\t%s\t%s' % (rule[1], rule[0], rule_content[rule[0]]);
	print '%.4f\t%s' % (rule[1], rule_content[rule[0]]);
#}

#       current_rule_score = 0.0;
#       for line in file(temporary_dir + '/rule.ranked.' + rule_id).read().split('\n'): #{
#               if len(line) > 2: #{
#                       current_rule_score = current_rule_score + float(line.split('||')[0].strip());
#               #}      
#       #}      
#
#       # baseline_lines_hash
#       
#       bl_intersect_score = 0.0;
#       for line in list(intersection_baseline_rules): #{
#                       
#               if len(line) > 2: #{
#                       bl_line_id = line.split('||')[0].strip().split(':')[0].strip('[');
#                       bl_intersect_score = bl_intersect_score + baseline_lines_hash[bl_line_id];
#               #}      
#       #}                      
#
#       bl_diff = (current_rule_score + bl_intersect_score) - baseline_score;
#       print '++ rule_score: ' + str(current_rule_score) + '; bl_intersect: ' + str(bl_intersect_score);
#       print '++ bl_score: ' + str(baseline_score) + '; bl_with_rule: ' + str(current_rule_score + bl_intersect_score) + '; bl_diff: ' + str(bl_diff);
#
#       rule_scores[rule_id] = current_rule_score + bl_intersect_score;
#
#       print intersection_baseline_rules;      
#       print diff_baseline_rules;      
#}


# WHY ARE WE RANKING TWICE ? 
# WE ALREADY KNOW THE SCORES
# FROM THE AMBIG CORPUS. WE JUST HAVE TO LOOK UP THE SENTENCE ID  ??
