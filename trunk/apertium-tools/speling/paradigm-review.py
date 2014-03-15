#!/usr/bin/env python3
# coding=utf-8
# -*- coding: utf-8 -*-

import sys

if len(sys.argv) < 2:
	print('paradigm-review.py <dix file>')
	sys.exit(-1)


d = open(sys.argv[1], 'r')

pardefs = {}
e_count = 0
u_count = 0
inpar = False
inmain = False
parname = ''
pos = {}

for line in d:
#       print >> sys.stderr , line
	if line.strip()[0:4] == '<!--':
		continue


	if '<pardef n="' in line:
		inpar = True
		parname = line.split('"')[1]
		pardefs[parname] = {}
		pardefs[parname]['e'] = 0
		pardefs[parname]['u'] = 0
		if '__' not in line:
			continue

		ppos = line.split('__')[1].split('"')[0]
		if ppos in pos:
			pos[ppos] += 1
		else:
			pos[ppos] = 0

	if inpar and '<e' in line:
		pardefs[parname]['e'] += 1


	if '</pardef>' in line:
		inpar = False


	if '<section' in line:
		inmain = True


	if '</section>' in line:
		inmain = False


	if inmain and '<e' in line:
		if '<par n' not in line: #{
			continue;
		#}
		parname = line.replace('<par n="', '@').split('@')[1].split('"')[0]
		if parname not in pardefs:
			print('Paradigm "' + parname + '" not found.')
#		       sys.exit(-1)
			continue

		pardefs[parname]['u'] += 1
		parname = ''

unused_pars = {}

for c in pos:
	print()
	print('====== ' + c + ' ' + ('=' * (76 - len('=== ' + c))))
	print('Name\t\t\t\tEntries\t\tUsed by'.expandtabs())
	print('================================================================================')
	print()
	#sort by how frequently it's used.
	for p in sorted(pardefs, key=lambda x: pardefs[x]['u'], reverse=True):
		assert (type(p) == type('str'))

		if p.count('__') < 1:
			continue

		if p.split('__')[1] == c:
			print(p.ljust(32) +
			      str(pardefs[p]['e']).ljust(8) +
			      '\t'.expandtabs() + str(pardefs[p]['u']))

		if pardefs[p]['u'] == 0:
			unused_pars[p] = pardefs[p]

if unused_pars:
	print('================================================================================')
	print('================================================================================')
	print('Name\t\t\t\tEntries\t\tUsed by'.expandtabs())

	for c in pos:
		for p in unused_pars:

			if p.count('__') < 1:
				continue

			if p.split('__')[1] == c:
				print(p.ljust(32) +
				      str(pardefs[p]['e']).ljust(8) +
				      '\t'.expandtabs() + str(pardefs[p]['u']))


