#!/usr/bin/python3

import sys, os;

# The general idea of the file is to take a bilingual dictionary and two lexc files and spit out 
# trimmed versions of the two. Because we cannot be sure just by the stem lexicon what tags it will
# have, we look through all tags. One future possibility would be to try and parse the lexc file
# so we can know for each stem exactly what tags it gets given.

bidix_contlex = {
	'<n>':'Nouns',
	'<n><attr>':'Nouns',
	'<n><gen>':'Nouns',
	'<n><px3sg><loc>':'Nouns',
	'<n><PXD><loc>':'Nouns',
	'<post>':'Postpositions',
	'<post><Rnom>':'Postpositions',
	'<post><Rabl>':'Postpositions',
	'<post><Rdat>':'Postpositions',
	'<post><Rins>':'Postpositions',
	'<postadv>':'Postadvervbs',
	'<np><top>':'ProperNouns',
	'<np><org>':'ProperNouns',
	'<np><al>':'ProperNouns',
	'<np><ant><m>':'ProperNouns',
	'<np><ant><f>':'ProperNouns',
	'<np><pat>':'ProperNouns',
	'<np><cog><f>':'ProperNouns',
	'<np><cog><m>':'ProperNouns',
	'<np><cog><mf>':'ProperNouns',
	'<adj>':'Adjectives',
	'<adj><advl>':'Adjectives',
	'<adj><subst>':'Adjectives',
	'<adv>':'Adverbs',
	'<adv><itg>':'Adverbs',
	'<cnjcoo>':'Conjunctions',
	'<cnjadv>':'Conjunctions',
	'<cnjsub>':'Conjunctions',
	'<v><tv>':'Verbs',
	'<v><tv><pass>':'Verbs',
	'<v><iv>':'Verbs',
	'<v><TD>':'Verbs',
	'<v>':'Verbs',
	'<vaux>':'Verbs',
	'<ij>':'Interjections'
};

contlex_bidix = {
	'Nouns':['<n>', '<n><attr>', '<n><gen>', '<n><px3sg><loc>', '<n><PXD><loc>'],
	'Postpositions':['<post>', '<post><Rnom>', '<post><Rabl>', '<post><Rdat>', '<post><Rins>'],
	'ProperNouns':['<np><top>', '<np><cog>', '<np><ant><m>', '<np><ant><f>', '<np><pat>', '<np><cog><m>', '<np><cog><f>', '<np><cog><mf>', '<np><org>', '<np><al>'],
	'Adjectives':['<adj>', '<adj><advl>', '<adj><subst>'],
	'Adverbs':['<adv>', '<adv><itg>'],
	'Conjunctions':['<cnjcoo>','<cnjadv>','<cnjsub>'],
	'Verbs':['<v>', '<v><tv>', '<v><iv>', '<v><TD>', '<v><tv><pass>', '<vaux>'],
	'Postadverbs':['<postadv>'],
	'Interjections':['<ij>']
};

def sym(s): #{
	return s.replace('<b/>', '% ').replace('<g>', '#').replace('</g>','').replace('<s n="', '<').replace('"/>', '>').replace('-', '%-');
#}

def lem(s): #{
	return s.split('<')[0];
#}

def tags(s): #{
	return '<' + '<'.join(s.split('<')[1:]);
#}

def trim(ifile, stems, ofile): #{
	total = 0;
	output = 0;
	stemlex = [];
	curstemlex = '';
	of = open(ofile, 'w+');
	state = 0; # 0 = initial, 1 = in root lexicon, 2 = in a stem lexicon
	for line in open(ifile).readlines(): #{
		total += 1;
		line = line.strip('\n');
	
		if line.count('LEXICON Root') > 0: #{
			print(line, file=of);
			state = 1;
			continue;
		#}	
	
		if state == 1 and line.count('LEXICON') > 0: #{
			#print(stemlex, file=of);
			state = 0;
		#}
	
		if state == 2 and line.count('LEXICON') > 0: #{
			print('', file=of);
			curstemlex = '';
			state = 0;
		#}
	
		if state == 0 and line.count('LEXICON') > 0: #{
			lex = line.strip().split(' ')[1];
			if lex in stemlex and lex in contlex_bidix: #{
				print(line, file=of);
				print('', file=of);
				state = 2;
			#}
			curstemlex = lex;
		#}
		
		if state == 0: #{
			print(line, file=of);
		elif state == 1: #{
			print(line, file=of);
			if line.count(';') > 0: #{
				lex = line.strip('; ');
				stemlex.append(lex);
			#}
	
		elif state == 2: #{
			lem = line.split(':')[0];
			for tag in contlex_bidix[curstemlex]: #{
				if tag in stems: #{
					if lem in stems[tag]: #{
						print(line, file=of);	
						output += 1;
					#}
			#		print(curstemlex , lstems[tag]);
				#}
			#}
		#}
	#}
	# Maybe put a 'blame' here. E.g. username who performed the trimming, date etc.
	print('!', output, '/', total, file=sys.stderr);
	of.close();
#}	

### Main

if len(sys.argv) < 4: #{
	print('trim-lexc.py <bidix> <lexc left> <lexc right>');
	print('EXAMPLE:');
	print('    trim-lexc.py apertium-tr-tt.tr-tt.dix apertium-tur.tur.lexc apertium-tat.tat.lexc');
	sys.exit(-1);
#}

bidix = sys.argv[1];
lexcl = sys.argv[2];
lexcr = sys.argv[3];

lstems = {};
rstems = {};

for line in open(bidix).readlines(): #{
	if line.count('<e') < 1 or line.count('<!--<e') > 0: #{
		continue;
	#}
	row = line.replace('<l>', '|').replace('</l>', '|').replace('<r>', '|').replace('</r>', '|').split('|');

	left = sym(row[1]);
	right = sym(row[3]);
	ltags = tags(left);
	rtags = tags(right);
	llem = lem(left);
	rlem = lem(right);

	if ltags not in lstems: #{
		lstems[ltags] = {};
	#}

	if rtags not in rstems: #{
		rstems[rtags] = {};
	#}

	lstems[ltags][llem] = line;
	rstems[rtags][rlem] = line;
#}

if 'TMPDIR' not in os.environ: #{
	tmp = '/tmp/';
else: #{
	tmp = os.environ['TMPDIR'];
#}

trim(lexcl, lstems, tmp + os.path.basename(lexcl) + '.trimmed');
trim(lexcr, rstems, tmp + os.path.basename(lexcr) + '.trimmed');


