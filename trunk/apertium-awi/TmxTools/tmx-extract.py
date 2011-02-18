#!/usr/bin/python

from xml.sax import make_parser
from xml.sax.handler import ContentHandler

import sys;

class TMXHandler(ContentHandler):
	
	def __init__ (self, slang, tlang): #{
		self.pair = set([slang, tlang]);
		self.inTag = '';
		self.note = '';
		self.tuid = '';
		self.type = '';
		self.cur_pair = set();	
		self.cur_lang = '';
		self.seg = {};
		self.seg[slang] = '';
		self.seg[tlang] = '';
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
		elif name == 'seg': #{
			self.inTag = 'seg';
			if self.cur_lang in self.pair: #{
				self.seg[self.cur_lang] = '';
			#}
		#}
	#}

	def characters (self, c): #{
		if self.inTag == 'note': #{
			self.note += c;
		elif self.inTag == 'seg' and self.cur_lang in self.pair: #{
			self.seg[self.cur_lang] += c;
		#}
	#}

	def endElement(self, name): #{
		if name == 'tu' and self.pair == self.cur_pair: #{
			print '  <tu tuid="' + self.tuid + '" datatype="' + self.type + '">';
			print '    <note>' + self.note.strip() + '</note>';
			for lang in self.cur_pair: #{			
				print '    <tuv xml:lang="' + lang + '">';
				print '      <seg>' + self.seg[lang].strip() + '</seg>';	
				print '    </tuv>';
			#}
			print '  </tu>';
		#}
	#}
#}

parser = make_parser();

if len(sys.argv) < 3: #{
	print 'Usage: tmx-extract.py <file> <slang> <tlang>';
	print '';
	sys.exit(-1);
#}

curHandler = TMXHandler(sys.argv[2], sys.argv[3]);

parser.setContentHandler(curHandler);

parser.parse(open(sys.argv[1]));


