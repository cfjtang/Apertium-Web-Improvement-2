import sys, re;

#
# The general algorithm is:
#   
# For each set of contiguous lines in the input where the lemma is the same:
#     Make a new paradigm, and then linearise that paradigm 
#     If the linearised paradigm does not exist in the hash:
#       Add the linearised paradigm to the hash
#     Add the lemma to the paradigm in the hash
#         
# For each of the paradigms in the hash: 
#     Print out the paradigm
# 
# For each of the paradigms in the hash:
#     For each of the entries attached to that paradigm:
#         Print out the entry with its corresponding paradigm
#


def find_longest_common_substring(lemma, flexion): #{
# IN: 'wolf', 'wolves'

	candidate = '';
	length = len(lemma);
	for char in lemma: #{
		candidate = candidate + char;
		#print >> sys.stderr , 'cand: ' , candidate , '; flexion:', flexion;
		if candidate not in flexion: #{
			return candidate[:-1];
		#}
	#}
	#print >> sys.stderr , 'cand: ' , candidate
	return candidate;

# OUT: 'wol'
#}

def linearise_paradigm(p): #{
# IN: [('pl.dat': 'ам'), ('pl.ins': 'ами'), ('pl.acc', 'а'), ('pl.gen', 'ов'), ('pl.nom', 'а'), ('pl.prp', 'ах')]

	out = '';
	for e in p: #{

		out = out + e[0] + '=' + e[1] + ':'
	#}
	return out.strip(':');

# OUT:  pl.acc=а:pl.dat=ам:pl.gen=ов:pl.ins=ами:pl.nom=а:pl.prp=ах
#}

def delinearise_paradigm(p): #{
# IN:  pl.acc=а:pl.dat=ам:pl.gen=ов:pl.ins=ами:pl.nom=а:pl.prp=ах

	out = {};
	for e in p.split(':'): #{
		row = e.split('=');
	
		if row[0] not in out: #{
			out[row[0]] = [];
		#}
		out[row[0]].append(row[1]);
	#}	
	return out;

# OUT: {'pl.dat': ['ам'], 'pl.ins': ['ами'], 'pl.acc': ['а'], 'pl.gen': ['ов'], 'pl.nom': ['а'], 'pl.prp': ['ах']}
#}

firstLine = True;
paradigms = {}; # keyed on contents
forms = {}; # temporary

lema = '';
count = 0;
for line in sys.stdin.readlines(): #{
	row = line.strip().split(';')

	if row[0] != lema and firstLine == False: #{
		#print(row[0], lema);
		#process;

		if 'sg.nom' in forms: #{
			lema = forms['sg.nom'][0];
		elif 'sg.nom' not in forms and 'pl.nom' in forms: #{
			lema = forms['pl.nom'][0];
		#}

		stem = lema;			

		pardef = [];
		for msd in forms: #{
			for form in forms[msd]: #{
				newstem = find_longest_common_substring(lema, form);
				if len(newstem) < len(stem): #{
					stem = newstem;
				#}
			#}
		#}

		for msd in forms: #{

			for form in forms[msd]: #{
				patro = '^' + stem;
				suffix = re.sub(patro, '', form);
				pardef.append((msd, suffix));
			#}
		#}
		pardef = list(set(pardef));
		pardef.sort();

		sig = linearise_paradigm(pardef);
		if sig not in paradigms: #{
			paradigms[sig] = [];
		#}
		paradigms[sig].append((lema, tag, stem));

		#print(lema, stem, sig);

		forms = {};
		lema = row[0];
	#}

	if firstLine: #{
		lema = row[0];
		firstLine = False;
	#}


	tag = row[3].strip();
	form = row[1].strip();
	msd = row[2].strip(); 

	if msd not in forms: #{
		forms[msd] = [] ;
	#}

	forms[msd].append(form);
	count = count + 1;
	if count % 10000 == 0: #{
		sys.stderr.write('.');
	#}
	sys.stderr.flush();
#}

# This defines the order that you want the entries to be printed in. It will
# be dependent on the part-of-speech class you are making the paradigms + entries for. 
# It should be exhaustive, if a form is not found it is not printed out. 

order = ['sg.nom', 'sg.gen', 'sg.dat', 'sg.acc', 'sg.ins', 'sg.ins.fac', 'sg.prp', 'sg.loc', 'sg.loc2', 'sg.par', 
	'pl.nom', 'pl.gen', 'pl.dat', 'pl.acc', 'pl.ins', 'pl.prp', 'pl.loc', 'pl.loc2', 'pl.par'];

names = {}; # Maps paradigm signatures to the names we have chosen

count_paradigms = 0; # Counter for number of paradigms 
count_entries = 0; # Counter for number of entries

print('<dictionary>');
print('<alphabet></alphabet>');

print('<pardefs>');

for pardef in paradigms: #{

	n = paradigms[pardef][0];

	names[pardef] = n[0] + '__' + n[1].replace('.','_')
	print('  <pardef n="%s__%s" c="%d">' % (n[0], n[1].replace('.','_'), len(paradigms[pardef])));
	xpardef = delinearise_paradigm(pardef);

	for msd in order: #{
		if msd in xpardef: #{
			for var in xpardef[msd]: #{
				suffix = var;
				tags = '<s n="' + msd.replace('.', '"/><s n="') + '"/>';
				entry = '<e>';
				if tags.count('<s n="fac"/>') > 0: #{
					entry = '<e r="LR">';
					tags = tags.replace('<s n="fac"/>', '');
				#}
				print('    %s<p><l>%s</l><r>%s</r></p></e>' % (entry, suffix, tags));
			#}
		#}
	#}
	print('  </pardef>');
	count_paradigms = count_paradigms + 1;	
#}

print('  <section id="main" type="standard">');

for pardef in paradigms: #{

	for w in paradigms[pardef]: #{
		print('    <e lm="%s"><p><l>%s</l><r>%s</r></p><par n="%s"/></e>' % (w[0], w[2], w[0], names[pardef]));
#		print(w, names[pardef]);
		count_entries = count_entries + 1;
	#}
#}

print('  </section>');
print('</dictionary>');

print('Paradigms: %d' % (count_paradigms), file=sys.stderr);
print('Entries: %d' % (count_entries), file=sys.stderr);
