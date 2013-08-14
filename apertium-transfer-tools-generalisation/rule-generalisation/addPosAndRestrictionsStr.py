#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys,re,argparse, ruleLearningLib

parser = argparse.ArgumentParser(description='')
parser.add_argument('--tag_groups_file_name',required=True)
parser.add_argument('--tag_sequences_file_name',required=True)
parser.add_argument('--rich_ats',action='store_true')
args = parser.parse_args(sys.argv[1:])

include_restrictions= not args.rich_ats


ruleLearningLib.AT_LexicalTagsProcessor.initialize(args.tag_groups_file_name,args.tag_sequences_file_name)

for line in sys.stdin:
	line=line.decode('utf-8').strip()
	
	pieces=line.split(u' | ')
	freqstr=pieces[0]
	atstr=u'|'.join(pieces[1:5])
	
	at = ruleLearningLib.AlignmentTemplate()
	at.parse(atstr)
	at.add_explicit_empty_tags()
	
	print at.get_pos_list_str(include_restrictions).encode('utf-8')+" | "+line.encode('utf-8')
