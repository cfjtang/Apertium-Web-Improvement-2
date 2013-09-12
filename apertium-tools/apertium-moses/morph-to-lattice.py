#!/usr/bin/python3 

import sys;

# Input:
# ^Siihen/Siihen$ ^kuuluu/kuuluu$ ^joukko/joukko$ ^toimia/*toimia$^,/,$ ^joiden/joiden/joidenka/joidenkas/joitten/joittenka/joittenkas$ ^avulla/avu>lla$ ^parannetaan/*parannetaan$ ^arvioinnin/arvioinni>n$ ^merkityksellisyyttä/*merkityksellisyyttä$^,/,$ ^laatua/laatu>a$ ^ja/ja$ ^käyttöä/käyttö>ä$^,/,$ ^kuten/kuten$ ^toimenpiteet/toimen#pitee>t$^,/,$ ^jotka/jotka$ ^esitettiin/esitettiin$ ^arviointia/arviointi>a$ ^koskevassa/*koskevassa$ ^tiedonannossa/*tiedonannossa$ ^vuonna/vuonna/vuote>na$ ^2000/2000$ ^./.$

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
#	print(c, file=sys.stderr);
	if c == '^': #{
		inword = True;
		c = sys.stdin.read(1);
	#}

	if c == '$': #{
		inword = False;
		#print(buf, lattice, file=sys.stderr);
		surface = buf.split('/')[0];
		decomps = list(set(buf.split('/')));
		max_split = 1;
		for decomp in decomps: #{
			splits = len(decomp.replace('#','>').split('>'));
			if splits > max_split: #{
				max_split = splits;
			#}
		#}
		outgoing = outgoing+max_split;
		print(pos, outgoing, max_split, buf, file=sys.stderr);
		weight = 1.0;
		for decomp in decomps: #{
			if decomp[0] == '*': #{
				decomp = decomp.lower();
			#}
			decomp = decomp.replace('*', '');
			if decomp == surface: #{
				weight = 0.5;
			elif len(decomps) > 1.0: #{
				weight = 0.5 / (float(len(decomps)-1.0));
			else: #{
				weight = 0.5;
			#}
			if decomp.count('>') or decomp.count('#') > 0: #{
				localpos = 0;
				for part in decomp.replace('#', ' ').replace('>', ' >').split(' '): #{	
					print('\t' , pos+localpos, pos+localpos+1, part, file=sys.stderr);
					if localpos == 0: #{
						lattice[pos+localpos].add((part, weight, pos+localpos, pos+localpos+1));
					else: #{
						lattice[pos+localpos].add((part, 1.0, pos+localpos, pos+localpos+1));
					#}
					localpos = localpos + 1;
					lattice[pos+localpos] = set();
				#}
			else: #{
				print('\t', pos, outgoing-1, decomp, file=sys.stderr);
				lattice[pos].add((decomp, weight, pos, outgoing-1));
			#}
		#}
		pos = pos + max_split;
		if pos not in lattice: #{
			lattice[pos] = set();
		#}
		buf = '';
		c = sys.stdin.read(1);
		continue;
	#}

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


