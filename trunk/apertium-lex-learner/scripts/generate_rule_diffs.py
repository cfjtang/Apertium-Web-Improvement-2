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

if len(sys.argv) < 5: #{
	print 'rank_candidate_rules.py <tagged baseline> <rules.rlx> <rules.bin> <translate.sh> <temp dir>';
	sys.exit(-1);
#}

baseline_tagged_file = sys.argv[1];
rules_rlx_file = sys.argv[2];
rules_bin_file = sys.argv[3];
translate_and_rank_script = sys.argv[4];
temporary_dir = sys.argv[5];

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
	# If the file does not exist, then we translate, score and save it.
	print '+ Translated and scoring baseline to ' + temporary_dir + '/baseline.out';
	cmd = 'cat ' + baseline_tagged_file + ' | bash ' + translate_and_rank_script + ' > ' + temporary_dir + '/baseline.out';
	print cmd;
	res = commands.getstatusoutput(cmd);
	baseline_lines = file(temporary_dir + '/baseline.out').read().split('\n');
	print '+ Translated and scored ' + str(len(baseline_lines)) + ' sentences.';
else: #{
	# If the file does exist, then we check to make sure the file line lengths are the same.
	baseline_lines = file(temporary_dir + '/baseline.out').read().split('\n');
	baseline_tagged_lines = file(baseline_tagged_file).read().split('\n'); 
	if len(baseline_lines) != len(baseline_tagged_lines): #{
		print '+ ERROR: File length mismatch in tagged and translated/scored files. (' + str(len(baseline_lines)) + ' != ' + str(len(baseline_tagged_lines)) + ')';
		sys.exit(-1);
	else: #{
		print '+ Using existing translated and scored baseline (' + str(len(baseline_lines)) + ' sentences) in ' + temporary_dir + '/baseline.out';
	#}
#}

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

for centre in centres: #{
	cmd = 'cat ' + baseline_tagged_file + ' | grep "/' + centre + '<" > ' + temporary_dir + '/baseline.' + centre + '.txt';
	print '---> ' + cmd;
	res = commands.getstatusoutput(cmd);
#}

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

for rule_id in sorted_rule_ids: #{
	print '++ ' + rule_id + ': ';
	process_rule_flag = True;
	if os.path.isfile(temporary_dir + '/rule.out.' + rule_id): #{
		rule_out_len = len(file(temporary_dir + '/rule.out.' + rule_id).read().split('\n'));
		if rule_out_len == len(baseline_lines): #{
			process_rule_flag = False;
		#}
	#}
	if process_rule_flag == True: #{
		# We only need to process those lines which correspond with our rule centre.
		cmd = 'cat ' + temporary_dir + '/baseline.' + rule_centre[rule_id] + '.txt | cg-proc -r ' + rule_id + ' ' + rules_bin_file + ' > ' + temporary_dir + '/rule.out.' + rule_id; 
		print '---> ' + cmd;
		res = commands.getstatusoutput(cmd);
	else: #{
		print '+++ Baseline already translated and ranked.'
	#}

	rule_lines = file(temporary_dir + '/rule.out.' + rule_id).read().split('\n');

	rule_lines_set = set(rule_lines);

	intersection_baseline_rules = rule_lines_set.intersection(baseline_tagged_set);
	diff_baseline_rules = rule_lines_set.difference(baseline_tagged_set);

	print '++ Baseline (' + str(len(baseline_lines)) + ' sentences) With-rules (' + str(len(rule_lines)) + ' sentences)';	
	print '++ Intersection (' + str(len(intersection_baseline_rules)) + ' sentences) Difference (' + str(len(diff_baseline_rules)) + ' sentences)';	

	if len(diff_baseline_rules) == 0: #{
		rule_scores[rule_id] = baseline_score;
		os.unlink(temporary_dir + '/rule.out.' + rule_id); # This temp file is like 20M
		continue;
	#}

	fd_rule = open(temporary_dir + '/rule.diff.' + rule_id, 'w');
	fd_rule.writelines('\n'.join(list(diff_baseline_rules)));	
	fd_rule.close();
	cmd = 'gzip ' + temporary_dir + '/rule.diff.' + rule_id;
	print '---> ' + cmd;
	res = commands.getstatusoutput(cmd);
	
	os.unlink(temporary_dir + '/rule.out.' + rule_id); # This temp file is like 20M
#}

