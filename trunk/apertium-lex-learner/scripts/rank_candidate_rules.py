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

if len(sys.argv) < 4: #{
	print 'rank_candidate_rules.py <tagged baseline> <rules.rlx> <translate.sh> <temp dir>';
	sys.exit(-1);
#}

baseline_tagged_file = sys.argv[1];
rules_rlx_file = sys.argv[2];
translate_and_rank_script = sys.argv[3];
temporary_dir = sys.argv[4];

###############################################################################

for line in file(rules_rlx_file).read().split('\n'): #{
	if line.count('SUBSTITUTE') > 0: #{
		rule_id = line.split(':')[1].split(' ')[0];
		rule_scores[rule_id] = -10.0;
		rule_centre[rule_id] = line.split('"')[1];
	#}
#}

print '+ Read' , len(rule_scores) , 'rules.';

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

print '+ Total score for ' + str(len(baseline_lines)) + ' sentences is ' + str(baseline_score);

###############################################################################

centres = set();
for centre in rule_centre: #{
	centres.add(rule_centre[centre]);
#}
print '+ There are ' + str(len(centres)) + ' rule centres.';

###############################################################################

print '+ Processing ' + str(len(rule_scores)) + ' rules.'

def rule_id_compare(x, y): #{
	x = int(x.replace('r', ''));	
	y = int(y.replace('r', ''));	
	return x - y;
#}

sorted_rule_ids = list(rule_scores);
sorted_rule_ids.sort(cmp=rule_id_compare);

baseline_tagged_set = set(file(baseline_tagged_file).read().split('\n'));
count = 0;
fd_rules = open(temporary_dir + '/rule.aggregate.txt', 'w');

for rule_id in sorted_rule_ids: #{
	print '++ ' + rule_id + ': ';
	process_rule_flag = True;

	if not os.path.isfile(temporary_dir + '/rule.diff.' + rule_id + '.gz'): #{
		print '++ ' + rule_id + ' generated no output. File ' + temporary_dir + '/rule.diff.' + rule_id + '.gz does not exist.';
		continue;
	#}
	cmd = 'zcat ' + temporary_dir + '/rule.diff.' + rule_id + '.gz';
	res = commands.getstatusoutput(cmd);
	for line in res[1].split('\n'): #{
		rule_line = line.replace('||\t]', '|| ' + rule_id + '\t]');
		fd_rules.writelines(rule_line + '\n');	
		count = count + 1;
	#}	
	print '++ Processing ' + rule_id + ', ' + str(count) + ' total lines processed.';
#}

fd_rules.close();

cmd = 'cat ' + temporary_dir + '/rule.aggregate.txt | bash ' + translate_and_rank_script + ' > ' + temporary_dir + '/rules.ranked.txt';
print '++ Translating and ranking ' + str(count) + ' sentences. Output in: ' + temporary_dir + '/rules.ranked.txt';
print '---> ' + cmd;
res = commands.getstatusoutput(cmd);

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
