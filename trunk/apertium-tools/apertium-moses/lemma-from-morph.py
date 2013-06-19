#!/usr/bin/python3
# -*- coding: utf-8 -*-


import sys;

# The Apertium stream format is more is less as follows:
#
# ^surface form/lemma1<some tags>/lemma2<more><tags>$
#
# ^, $, '[', ']' and '/' are escaped in input by the apertium-destxt program.
#
# Unescaped text:
#   ^ This is a test $50 [Nr. 1782/2003]
# Escaped text:
#   \^ This is a test \$50 \[Nr. 1782\/2003\]
#
# The idea of this script is to maintain the original formatting as much as possible while lemmatising the input.

escaped = False;
inside = False;
lbuffer = ''; # The buffer for the lexical unit, which is composed of a series of analyses separated by '/'

c = sys.stdin.read(1);
while c != '': #{
	if c == '\\' and not escaped and inside: #{
		lbuffer = lbuffer + c;	
		c = sys.stdin.read(1);	
		lbuffer = lbuffer + c;	
		c = sys.stdin.read(1);	
		continue;
	#}

	if c == '\\' and not escaped and not inside: #{

		sys.stdout.write(c);
		c = sys.stdin.read(1);	
		sys.stdout.write(c);
		c = sys.stdin.read(1);	
		continue;
	#}

	if c == '^' and not escaped: #{
		inside = True;
		c = sys.stdin.read(1);	
		continue;
	#}

	# We just skip over all tags.
	if c == '<' and not escaped: #{
		while c != '>': #{
			c = sys.stdin.read(1);
		#}
		c = sys.stdin.read(1);
		continue;
	#}

	if c == '$' and not escaped: #{
		inside = False;
		# We're going to split on '/', but we don't want to split if it is escaped
		lbuffer = lbuffer.replace('\/', '¶');
		# This contains surface form and list of analyses
		row = lbuffer.split('/');
		# The first item is always the surface form
		surface = row[0].replace('¶', '\/');
		# Collapse the lemmas
		analyses = list(set(row[1:]));

		# If the word is unknown in the dictionary (= marked with a '*') then output the surface form
		if len(analyses) == 1 and analyses[0][0] == '*': #{
			sys.stdout.write(surface);
		# If the word is known, and there is a single possible lemma, output the lemma.
		elif len(analyses) == 1 and analyses[0][0] != '*': #{
			stem = row[1].replace('¶', '\/');
			if stem.count('+') > 0: #{
				subreadings = stem.split('+');
				for subreading in subreadings: #{
					for c in subreading: #{
						sys.stdout.write(c);
					#}
					if subreading != subreadings[len(subreadings)-1]: #{
						sys.stdout.write(' ');
					#}
				#}	
			else: #{
				sys.stdout.write(stem);
			#}
		# In any other case -- e.g. ambiguity with regard to which lemma, then output the surface form
		else: #{
			sys.stdout.write(surface);
		#}
		#sys.stderr.write(lbuffer);
		lbuffer = '';
		c = sys.stdin.read(1);	
		continue;
	#}


	# If we're inside a lexical unit, add it to the buffer, if not write it to the output.
	if inside: #{
		lbuffer = lbuffer + c;
	else: 
		sys.stdout.write(c);
	#}

	c = sys.stdin.read(1);	
#}


