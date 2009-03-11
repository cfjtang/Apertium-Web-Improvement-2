#!/usr/bin/python

from xml.sax import make_parser
from xml.sax.handler import ContentHandler

import sys, codecs;

sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

class Global: #{
	tus = [];
#}

class TU: #{
	def __init__(self, tuid, type, note, lang1, lang2, seg1, seg2, pair): #{
		self.tuid = tuid;
		self.type = type;
		self.note = note;
		self.lang1 = lang1;
		self.lang2 = lang2;
		self.seg1 = seg1;
		self.seg2 = seg2;
		self.pair = pair;
	#}
#}

class TMXHandler(ContentHandler): #{
	
	def __init__ (self): #{
		Global.tus = [];
		self.inTag = '';
		self.note = '';
		self.tuid = '';
		self.type = '';
		self.cur_pair = set();	
		self.cur_lang = '';
		self.seg = {};
	#} 

	def startElement(self, name, attrs): #{

		if name == 'tu':  #{
			self.cur_pair = set();	
			self.inTag = 'tu';
			self.tuid = attrs.get('tuid','');
			self.type = attrs.get('datatype','');
		elif name == 'note': #{
			self.inTag = 'note';
			self.note = "";
		elif name == 'tuv': #{
			self.inTag = 'tuv';
			self.cur_lang = attrs.get('xml:lang', '');
			self.cur_pair.add(self.cur_lang);
			if self.cur_lang not in self.seg: #{
				self.seg[self.cur_lang] = '';
			#}
		elif name == 'seg': #{
			self.inTag = 'seg';
			self.seg[self.cur_lang] = '';
		#}
	#}

	def characters (self, c): #{
		if self.inTag == 'note': #{
			self.note += c;
		elif self.inTag == 'seg': #{
			self.seg[self.cur_lang] += c;
		#}
	#}

	def endElement(self, name): #{
		if name == 'tu': #{
#			print '  <tu tuid="' + self.tuid + '" datatype="' + self.type + '">';
#			print '    <note>' + self.note.strip() + '</note>';
#			for lang in self.cur_pair: #{			
#				print '    <tuv xml:lang="' + lang + '">';
#				print '      <seg>' + self.seg[lang].strip() + '</seg>';	
#				print '    </tuv>';
			#}
#			print '  </tu>';

			lang1 = list(self.cur_pair)[0];
			lang2 = list(self.cur_pair)[1];

			tu = TU(self.tuid, self.type, self.note, lang1, lang2, self.seg[lang1], self.seg[lang2], self.cur_pair);

			Global.tus.append(tu);
		#}
	#}
#}


# Compare TUs
#
# Returns '1' if they are equivalent, '0' if they are not.
#
def identity(tu1): #{
	seg = set([tu1.seg1, tu1.seg2]);
	lang = set([tu1.lang1, tu1.lang2]);

	return str(seg) +  str(lang);
#}


def uniq(seq, idfun=None):  #{
	# order preserving
	if idfun is None: #{
		def idfun(x): return x
	#}
	seen = {}
	result = []
	for item in seq: #{
		marker = idfun(item)
		if marker in seen: #{
			continue;
		#}
		seen[marker] = 1
		result.append(item)
	#}
	return result
#}

parser = make_parser();

if len(sys.argv) < 2: #{
	print 'Usage: tmx-uniq.py <file>';
	print '';
	sys.exit(-1);
#}

curHandler = TMXHandler();

parser.setContentHandler(curHandler);

parser.parse(open(sys.argv[1]));

print >> sys.stderr, 'Total:', len(Global.tus);
Global.tus = uniq(Global.tus, idfun=identity);
print >> sys.stderr, 'Unique:' , len(Global.tus);

print '<?xml version="1.0" encoding="UTF-8"?>';
print '<tmx version="1.4">';
print '<body>';

for tu in Global.tus: #{

	print '  <tu tuid="' + tu.tuid + '" datatype="' + tu.type + '">';
	print '    <note>' + tu.note.strip() + '</note>';
	print '    <tuv xml:lang="' + tu.lang1 + '">';
	print '      <seg>' + tu.seg1.strip() + '</seg>';        
	print '    </tuv>';
	print '    <tuv xml:lang="' + tu.lang2 + '">';
	print '      <seg>' + tu.seg2.strip() + '</seg>';        
	print '    </tuv>';
	print '  </tu>';
#}

print '</body>';
print '</tmx>';
