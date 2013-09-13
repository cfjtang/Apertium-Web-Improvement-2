#!/usr/bin/python3 

import sys;

# Input:
# ^Ústava/ústava<n><f><sg><nom>$ ^EU/EU<np><top><f><sg><indecl>$ ^učiní/učinit<vblex><perf><fut><p3><sg>/učinit<vblex><perf><fut><p3><pl>$ ^Evropu/Evropa<np><top><f><sg><acc>$ ^demokratičtější/demokratický<adj><comp><ma><sg><voc>/demokratický<adj><comp><ma><sg><nom>/demokratický<adj><comp><ma><pl><voc>/demokratický<adj><comp><ma><pl><nom>/demokratický<adj><comp><ma><pl><acc>/demokratický<adj><comp><mi><sg><voc>/demokratický<adj><comp><mi><sg><nom>/demokratický<adj><comp><mi><sg><acc>/demokratický<adj><comp><mi><pl><voc>/demokratický<adj><comp><mi><pl><nom>/demokratický<adj><comp><mi><pl><acc>/demokratický<adj><comp><f><sg><loc>/demokratický<adj><comp><f><sg><voc>/demokratický<adj><comp><f><sg><ins>/demokratický<adj><comp><f><sg><nom>/demokratický<adj><comp><f><sg><dat>/demokratický<adj><comp><f><sg><acc>/demokratický<adj><comp><f><sg><gen>/demokratický<adj><comp><f><pl><voc>/demokratický<adj><comp><f><pl><nom>/demokratický<adj><comp><f><pl><acc>/demokratický<adj><comp><nt><sg><voc>/demokratický<adj><comp><nt><sg><nom>/demokratický<adj><comp><nt><sg><acc>/demokratický<adj><comp><nt><pl><voc>/demokratický<adj><comp><nt><pl><nom>/demokratický<adj><comp><nt><pl><acc>$ ^a/a<cnjcoo>$ ^průhlednější/průhledný<adj><sint><comp><ma><sg><voc>/průhledný<adj><sint><comp><ma><sg><nom>/průhledný<adj><sint><comp><ma><pl><voc>/průhledný<adj><sint><comp><ma><pl><nom>/průhledný<adj><sint><comp><ma><pl><acc>/průhledný<adj><sint><comp><mi><sg><voc>/průhledný<adj><sint><comp><mi><sg><nom>/průhledný<adj><sint><comp><mi><sg><acc>/průhledný<adj><sint><comp><mi><pl><voc>/průhledný<adj><sint><comp><mi><pl><nom>/průhledný<adj><sint><comp><mi><pl><acc>/průhledný<adj><sint><comp><f><sg><loc>/průhledný<adj><sint><comp><f><sg><voc>/průhledný<adj><sint><comp><f><sg><ins>/průhledný<adj><sint><comp><f><sg><nom>/průhledný<adj><sint><comp><f><sg><dat>/průhledný<adj><sint><comp><f><sg><acc>/průhledný<adj><sint><comp><f><sg><gen>/průhledný<adj><sint><comp><f><pl><voc>/průhledný<adj><sint><comp><f><pl><nom>/průhledný<adj><sint><comp><f><pl><acc>/průhledný<adj><sint><comp><nt><sg><voc>/průhledný<adj><sint><comp><nt><sg><nom>/průhledný<adj><sint><comp><nt><sg><acc>/průhledný<adj><sint><comp><nt><pl><voc>/průhledný<adj><sint><comp><nt><pl><nom>/průhledný<adj><sint><comp><nt><pl><acc>$ ^./.<sent>$^./.<sent>$

# TODO:
#  - Deal properly with blanks and escaped characters.

# style is 0 for PLF (Moses), 1 for PLF multiline and 2 for graphviz
style = 2;

buf = '';              # the current lexical unit
lattice = {};          # data structure for the lattice lattice[position] = set((word, weight, nodein, nodeout), (word2, weight, nodein, nodeout))
pos = 0;               # current position in the sentence
outgoing = 1;          # current outgoing node
lattice[pos] = set(); 
inword = False;        # are we in a lexical unit ?

c = sys.stdin.read(1);
while c != '': #{
	# Starting a lexical unit
	if c == '^': #{
		inword = True;
		c = sys.stdin.read(1);
	#}

	# We get to the end of a lexical unit
	if c == '$': #{
		inword = False;
		surface = buf.split('/')[0]; # The surface form is before the first '/'
		analyses = list(set(buf.split('/'))); 
		step = 1; # By default we step 1 per input word
		for analysis in analyses: #{
			# If the analysis contains some tags, we step 2
			if analysis.count('<') > 0: #{
				step = 2;
			#}
		#}
		outgoing = outgoing+step;
		#print('[' + str(pos) + '] ANALYSES:', analyses, file=sys.stderr);
		#print('\t', pos, outgoing, 1, buf, file=sys.stderr);


		# How the weights are calculated should probably be done 
		#   differently
		weight = 1.0;
		for analysis in analyses: #{

			if analysis.count('>') > 0: #{
				# analysis = ústava<n><f><sg><nom>
				row = analysis.split('<', 1);
				lemma = row[0]; 
				tags = row[1].replace('><', '.').strip('>');
				weight = 0.5/float(len(analyses)-1.0);
				lattice[pos].add((lemma, weight, pos, pos+1));
				if pos+1 not in lattice: #{
					lattice[pos+1] = set();
				#}	
				lattice[pos+1].add((tags, 1.0, pos+1, pos+2));
			#}

		#}
		# Add the surface form
		lattice[pos].add((surface, 0.5, pos, pos+step));
		pos = pos + step;
		if pos not in lattice: #{
			lattice[pos] = set();
		#}
		buf = '';
		c = sys.stdin.read(1);
		continue;
	#}

	# We print out the sentence as a lattice.
	if c == '\n': #{
		if style == 0: #{
			sys.stdout.write('(');
			for i in lattice: #{
				if len(lattice[i]) == 0: #{
					continue;
				#}
				sys.stdout.write('(');
				for point in list(lattice[i]): #{
					sys.stdout.write("('%s', %.2f, %d)," % (point[0], point[1], point[3]-point[2]));
				#}	
				sys.stdout.write('),');
			#}
			sys.stdout.write(')\n');
		elif style == 1: #{
			sys.stdout.write(' (\n');
			for i in lattice: #{
				if len(lattice[i]) == 0: #{
					continue;
				#}
				sys.stdout.write('  (\n');
				for point in list(lattice[i]): #{
					sys.stdout.write("   ('%s', %.2f, %d),\n" % (point[0], point[1], point[3]-point[2]));
				#}	
				sys.stdout.write('  ),\n');
			#}
			sys.stdout.write(' )\n');

		elif style == 2: #{
			print('digraph lattice {');
			print('\trankdir=LR;');
			print('\tnode [shape = doublecircle ]; ' + str(len(lattice)-1) + ';');
			print('\tnode [shape = circle ];');
			for i in lattice: #{
				for point in list(lattice[i]): #{
					print('\t%d -> %d [ label = "(%s, %.2f)"];' % (point[2], point[3], point[0], point[1]));
#					print('\t' point[2], '->',point[3], '[ label = "(', point[0], ',' ,point[1], ')"];');
				#}	
			#}
			print('}');
		#}
		buf = '';
		lattice = {};
		pos = 0;
		lattice[pos] = set();
		outgoing = 1;
		inword = False;

		#c = sys.stdin.read(1);
	#}

	if inword: #{
		buf = buf + c;
	#}
	c = sys.stdin.read(1);
#}


