#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, commands;

#
# This is a conversion script for the format outputted by lt-expand to the
# format accepted by the freeling indexdict program.
#
# Input is an expanded Apertium dictionary:
#  
#   tadoù:tad<n><m><sg>
# 
# Output is a Freeling dictionary:
#
#   tadoù tag NCMPV0
# 
# To convert the Apertium tagset into a PAROLE-compatible tagset, a file
# with the parole tag and Apertium tag list is used. The two are separated
# by a tab:
#
#   NCMPV0	<n><m><sg>
#

sys.stdin  = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

if len(sys.argv) < 4: #{
	print 'Usage: ./dix-to-maco.py [-l|-n] <dix file> <parole lookup>';	
	print '';
	sys.exit(-1);
#}

if commands.getstatusoutput('lt-expand')[0] > 256: #{
	print 'Please install `lt-expand\' or add it to your $PATH';	
	sys.exit(-1);
#}

tipoxt = sys.argv[1];
infile = sys.argv[2];
tffile = sys.argv[3];

transform = {};

# Read in the parole lookup table.

for line in file(tffile).read().split('\n'): #{
	row = line.split('\t');

	if len(row) < 2: #{
		continue;
	#}

	parole = row[0].strip();
	apertium = row[1].strip();

	transform[apertium] = parole;
#}

# We use a hash to store the surface (superficial) forms, along with
# their analyses. The hash is used so that duplicate values are removed.

surface = {};

mode = 0; # "General entries"

if tipoxt == '-l': #{
	mode = 1;
#}

for line in commands.getoutput('lt-expand ' + infile).decode('utf-8').split('\n'): #{
	# We skip "generate only" entries and regexes
	if line.count(':<:') > 0 or line.count('REGEXP') > 0: #{
		continue;
	#}

	# Are we extracting locutions or main entries?
	if mode == 1: #{
		if line.count(' ') < 1 and line.count('#') < 1: #{
			continue;
		#}	
	else:
		if line.count(' ') > 0 or line.count('#') > 0: #{
			continue;
		#}
	#}	

	row = line.replace(':>:', ':').split(':');

	case_flag = 0;
	for c in row[0]: #{
		if c.isupper(): #{
			case_flag = 1;
			continue;
		#}
	#}

	# Skip proper names in the general entries
	if tipoxt == '-n': #{
		if case_flag == 0: #{
			continue;
		#}
	else: #{
		if case_flag == 1: #{
			continue;
		#}
	#}



	if line.count('#') > 0 and line.count(' ') < 0 and mode == 1: #{
		#	bapurau newydd:>:papur<n><m><pl># newydd
		#	cat bapurau newydd:>:cat papur<n><m><pl># newydd
		# 	 -> <cat bapurau>
		# we need to count the number of spaces backwards from the end
		# of the right side in order to calculate where to place the '<>'
		# as paradigms might throw out different number of spaces.
			
		# Skip these at the moment as we'll also need to work out how to 
		# only put in one entry for the non-flecting part.
		#

		continue;

	elif line.count('#') == 0 and line.count(' ') > 0 and mode == 1: #{
		lemma = '';
		analysis = '';
		tags = '';
		
		if line.count('+') > 0: #{
			superficial = row[0].strip().replace(' ', '_');
			subreadings = row[1].strip().replace(' ', '_').split('+');

			if superficial not in surface: #{
				surface[superficial] = {};
			#}

			lemma = '';
			analysis = '';
			tags = '';

			for reading in subreadings: #{
				tag = '';
				lemma_part = '';

				idx = reading.index('<');
				tag = reading[idx:];
				lemma_part = reading.split('<')[0];

				
				if tag in transform: #{
					tags = tags + transform[tag] + '+';
				#}
				lemma = lemma + lemma_part + '+';

			#}

			lemma = lemma.replace(' ', '_');
			analysis = lemma.strip('+') + ' ' + tags.strip('+');
			surface[superficial][analysis] = '';

		else: #{	
			superficial = row[0].strip().replace(' ', '_');
			analysis = row[1].strip().replace(' ', '_');

			if superficial not in surface: #{
				surface[superficial] = {};
			#}

			tags = '';
		
			if '<' in analysis: #{
				idx = analysis.index('<');
				tags = analysis[idx:];
				lemma = analysis.split('<')[0].replace(' ', '_');
			#}
		
			if tags in transform: #{
				analysis = lemma + ' ' + transform[tags];
				surface[superficial][analysis] = '';
			#}
		#}
	#}

	if line.count('+') > 0 and mode == 0: #{

		superficial = row[0].strip().replace(' ', '_');
		subreadings = row[1].strip().split('+');

		if superficial not in surface: #{
			surface[superficial] = {};
		#}

		lemma = '';
		analysis = '';
		tags = '';

		for reading in subreadings: #{
			tag = '';
			lemma_part = '';

			idx = reading.index('<');
			tag = reading[idx:];
			lemma_part = reading.split('<')[0];

			
			if tag in transform: #{
				tags = tags + transform[tag] + '+';
			#}
			lemma = lemma + lemma_part + '+';
		#}
		
		lemma = lemma.replace(' ', '_');
		analysis = lemma.strip('+') + ' ' + tags.strip('+');
		surface[superficial][analysis] = '';

	elif mode == 0: #{

		superficial = row[0].strip().replace(' ', '_');
		analysis = row[1].strip();

		if superficial not in surface: #{
			surface[superficial] = {};
		#}

		tags = '';
	
		if '<' in analysis: #{
			idx = analysis.index('<');
			tags = analysis[idx:];
			lemma = analysis.split('<')[0].replace(' ', '_');
		#}
	
		if tags in transform: #{
			analysis = lemma + ' ' + transform[tags];
			surface[superficial][analysis] = '';
		#}
	#}
#}

# Print out
for key in surface: #{
	analyses = '';
	for subkey in surface[key]: #{
		analyses = analyses + subkey + ' ';
	#}
	print key + ' ' +  analyses;
#}
