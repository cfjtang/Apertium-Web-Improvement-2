#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, os, commands, gc;
from xml.sax import make_parser
from xml.sax.handler import ContentHandler

sys.stdin = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

if len(sys.argv) < 3: #{
	print 'generate_sl_ambig_corpus.py <bilingual dictionary> [lr|rl] [input_file]';
	sys.exit(-1);
#}

bidix = sys.argv[1]
direction = sys.argv[2];
kategoriak = ['n', 'vblex', 'adj']; # The categories we are searching for
atable = {}; # Ambiguity table

lu = '';
in_word = False;
lineno = 1;

class BidixHandler(ContentHandler): #{

	def __init__ (self): #{
		self.inTag = '';
		self.inSection = False;
		self.curSide = '';
		self.left_string = '';
		self.right_string = '';
		self.curSLR = 0;
		self.curSRL = 0;
		self.skipEntry = False;
	#}
	
	def startElement(self, name, attrs): #{
		if name == 'section': #{
			self.inSection = True;
		#}

		if name == 'e':  #{
			self.inTag = 'e';
			self.restriction = attrs.get('r');

			if direction == 'lr': #{
				self.curSLR = attrs.get('slr');
				if self.curSLR == None: #{
					self.curSLR = 0;	
				#}
				self.curSLR = int(self.curSLR);
			elif direction == 'rl': #{
				self.curSRL = attrs.get('srl');
				if self.curSRL == None: #{
					self.curSRL = 0;	
				#}
				self.curSRL = int(self.curSRL);
			#}

		elif name == 'p': #{
			self.inTag = 'p';
		elif name == 'l': #{
			self.inTag = 'l';
			self.curSide = 'l';
		elif name == 'r': #{
			self.inTag = 'r';
			self.curSide = 'r';
		elif name == 'i': #{
			self.inTag = 'i';
		elif name == 'b': #{
			self.inTag = 'b';
			if self.curSide == 'l': #{
				self.left_string = self.left_string + ' ';
			elif self.curSide == 'r': #{
				self.right_string = self.right_string + ' ';
			#}
		elif name == 's': #{
			self.inTag = 's';
			if not self.inSection: #{
				return;
			#}
			if self.curSide == 'l': #{
				self.left_string = self.left_string + '<' + attrs.get('n') + '>';
			elif self.curSide == 'r': #{
				self.right_string = self.right_string + '<' + attrs.get('n') + '>';
			#}
		elif name == 'g': #{
			self.inTag = 'g';
			if self.curSide == 'l': #{
				self.left_string = self.left_string + '#';
			elif self.curSide == 'r': #{
				self.right_string = self.right_string + '#';
			#}
		else: #{
			self.inTag = '';
		#}
	#}

	def characters (self, c): #{ 
		if self.inSection == False: #{
			return;
		#}
		if self.curSide == 'l' and not c.isspace(): #{
			self.left_string = self.left_string + c;
		elif self.curSide == 'r' and not c.isspace(): #{
			self.right_string = self.right_string + c;
		#}
	#}

	def endElement(self, name): #{
		if name == 'section': #{
			self.inSection = False;
		#}
		if (name == 'e') and (self.inSection == True): #{
			if self.restriction == 'LR' and direction == 'rl': #{
				self.left_string = '';
				self.right_string = '';
				self.curSide = '';
				self.restriction = '';
				return;		
			elif self.restriction == 'RL' and direction == 'lr': #{
				self.left_string = '';
				self.right_string = '';
				self.curSide = '';
				self.restriction = '';
				return;		
			#}
			left_string_pos = '';
			right_string_pos = '';
			if self.left_string.count('<') > 0 and self.right_string.count('<') > 0: #{
				left_string_pos = self.left_string.split('<')[1].replace('>', '');
				right_string_pos = self.right_string.split('<')[1].replace('>', '');
			#}

			if left_string_pos in kategoriak and right_string_pos in kategoriak: #{
					
				if direction == 'lr': #{ 
#					print 'slr:' , self.curSLR , ':' , self.left_string , ':' , self.right_string;
					if self.left_string not in atable: #{
						atable[self.left_string] = [];
					#}
					row = self.left_string.split('<');
					new_left_string = row[0] + ':' + str(self.curSLR) + '<' + '<'.join(row[1:]);
					if (self.curSLR, new_left_string, self.right_string) not in atable[self.left_string]: #{
						atable[self.left_string].append((self.curSLR, new_left_string, self.right_string));
					#}
				elif direction == 'rl': #{
#					print 'srl:' , self.curSRL , ':' , self.left_string , ':' , self.right_string;
					if self.right_string not in atable: #{
						atable[self.right_string] = [];
					#}
					row = self.right_string.split('<');
					new_right_string = row[0] + ':' + str(self.curSRL) + '<' + '<'.join(row[1:]);
					if (self.curSRL, new_right_string, self.left_string) not in atable[self.right_string]: #{
						atable[self.right_string].append((self.curSRL, new_right_string, self.left_string));
					#}
				#}
			#}
			self.left_string = '';
			self.right_string = '';
			self.curSide = '';
		#}
		if name == 'e': #{
			self.restriction = '';
		#}
	#}
#}

parser = make_parser();
curHandler = BidixHandler();
parser.setContentHandler(curHandler);
parser.parse(open(bidix));

seen = {};
count = 0;
total_errors = 0;
akatsek = '';
for key in atable: #{
	if len(atable[key]) < 2: #{
		continue;
	#}
	seen[key] = {};
	for alt in atable[key]: #{
		if alt[0] in seen[key]: #{
			akatsek = akatsek + 'ERROR: Bilingual dictionary contains multiple entries with the same translation index.\n';
			akatsek = akatsek + str(count) + ' / ' + str(len(atable)) + '\n';
			akatsek = akatsek + key + '\n';
			akatsek = akatsek + str(alt) + '\n';
			total_errors = total_errors + 1;
		else: #{
			seen[key][alt[0]] = alt[0];
		#}

		print >> sys.stderr, key , alt, seen[key];
	#}
	count = count + 1;
#}

if total_errors > 0: #{
	print akatsek ; 
	print 'ERROR: There are ' + str(total_errors) + ' bilingual dictionary errors, please fix these before running.';
	sys.exit(-1);
#}

# 
#venja<n><f> (0, u'venja<n><f>', u'habit<n>')
#venja<n><f> (1, u'venja:1<n><f>', u'custom<n>')
#flot<n><nt> (0, u'flot<n><nt>', u'floating<n>')
#flot<n><nt> (1, u'flot:1<n><nt>', u'float<n>')
#flot<n><nt> (2, u'flot:2<n><nt>', u'melted fat<n>')
#greni<n><nt> (0, u'greni<n><nt>', u'pine<n>')
#greni<n><nt> (1, u'greni:1<n><nt>', u'lair<n>')
## 

def process_lexical_unit(lu, ambigs): #{
	super = '';
	lema = '';
	cat = '';

	if ambigs == -1: #{
		return ambigs;
	#}

	seen_bar = False;
	first_tag = False;
	for c in lu.decode('utf-8'): #{
		if c == '^': #{
			continue;
		#}
		if c == '/': #{
			seen_bar = True;
			continue;
		#}
		if c == '<': #{
			first_tag = True;
			continue;
		#}
		if c == '>': #{
			break;
		#}
		if seen_bar == False: #{
			super = super + c;
		elif seen_bar == True and first_tag == False: #{
			lema = lema + c;
		elif seen_bar == True and first_tag == True: #{
			cat = cat + c;
		#}
	#}	

	busca = lema + '<' + cat + '>';

	res = {};
	curr = 0;
	if busca in atable and len(atable[busca]) > 1: #{
#		print >>sys.stderr , '>>' , busca
		for alt in atable[busca]: #{
#			print >>sys.stderr , '>>>' , alt; 
			if curr not in res: #{
				res[curr] = '';
			#}
			res[curr] = lu.replace(busca, alt[1]);
			curr = curr + 1;
		#}
	else: #{
		res = {0: lu};
	#}

	if len(res) > 1: #{
		new_paths = {};
		for lu in res: #{
			for path in ambigs: #{
				new_paths[path + res[lu]] = ambigs[path] + res[lu];
				
			#}
			if len(new_paths) > 100: #{
				return -1;
			#}
		#}
		ambigs = new_paths;
		del new_paths;
	else: #{
		for path in ambigs: #{
			ambigs[path] = ambigs[path] + res[0];
		#}
	#}
	gc.collect();
	return ambigs;
#}

ambigs = {};
ambigs[''] = '';
ambig_tokens = [];
lu_count = 0;

c = sys.stdin.read(1);

broken = False;
xx = 0;
while c != '': #{

	if c == '^': #{
		in_word = True;
	#}
	if c == '$': #{
		lu_count = lu_count + 1;	
		lu = lu + c;
		oldc = 0;
		newc = 0;
		if type(ambigs) == type({}): #{
			oldc = len(ambigs);
		#}
		ambigs = process_lexical_unit(lu, ambigs);
		if type(ambigs) == type({}): #{	
			newc = len(ambigs);
			if newc > oldc: #{
				ambig_tokens.append(str(lu_count));
			#}
		#}
		if ambigs == -1: #{
			broken = True;
		#}
		in_word = False;
		lu = '';
		c = sys.stdin.read(1);
		if c == '^': #{
			in_word = True;
		#}
	#}
	if c.isspace(): #{
		if c == '\n': #{
			idx = 0;
			if ambigs == -1: #{
				sys.stdout.flush();
				broken = False;
				del ambigs;
				ambigs = {};
				ambigs[''] = '';
			elif len(ambigs) > 1 and broken == False: #{
				keys = ambigs.keys();
				keys.sort();
				#keys.reverse();
				for sentence in keys: #{
					if  ambigs[sentence] != '': #{
						sys.stdout.write('[' + str(lineno) + ':' + str(idx) + ':' + ','.join(ambig_tokens) + ':' + str(lu_count) + ' ||\t].[] ' + ambigs[sentence] + c);
						idx = idx + 1;
					#}
				#}
				lineno = lineno + 1;
			#}

			b = len(str(lineno) + "/" + str(xx) + " (" + str(len(ambigs)) + ") ") * "\b";
			sys.stderr.write(b + str(lineno) + "/" + str(xx) + " (" + str(len(ambigs)) + ")");
			xx = xx + 1;
			sys.stdout.flush();
			broken = False;
			del ambigs;
			ambigs = {};
			ambigs[''] = '';
			lu_count = 0;
			ambig_tokens = [];

		elif broken == False: #{
			for sentence in ambigs: #{
				ambigs[sentence] = ambigs[sentence] + c;
			#}
		#}
	#}

	if in_word == True: #{
		lu = lu + c;
	#}
	
	c = sys.stdin.read(1);
#}
