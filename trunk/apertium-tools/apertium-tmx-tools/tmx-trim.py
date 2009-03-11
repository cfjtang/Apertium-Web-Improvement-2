#!/usr/bin/python

from xml.sax import make_parser
from xml.sax.handler import ContentHandler

import sys, codecs;
import curses.ascii;

class TMXHandler(ContentHandler):
	
	def __init__ (self): #{
		self.inTag = '';
		self.note = '';
		self.tuid = '';
		self.type = '';
		self.cur_pair = set();	
		self.cur_lang = '';
		self.seg = {};
	#} 

	def samePunctuation(self): #{
		punct = [];

		for lang in self.cur_pair: #{
			tmp = [];
			for c in self.seg[lang].strip(): #{
				try: #{
					c = c.encode('ascii');
				except: #{
					c = 'a';
				#}	

				if curses.ascii.ispunct(c): #{
					tmp.append(c);
				#}
			#}			
			punct.append(tmp);
		#}

		if len(punct[0]) > (len(punct[1]) + 2) or len(punct[0]) < (len(punct[1]) - 1): #{
			return False;
		#}

		return True;
	#}
	
	# if either of the segments don't include alphabetic characters, we want to discard the TU
	def containsAlphabetic(self): #{
		alphabet = set(['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'z', 'y', 'x', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'Z', 'Y', 'X']);
		for lang in self.cur_pair: #{                   
			if set(self.seg[lang].strip()).intersection(alphabet) == set(): #{
				return False;	
			#}
		#}

		return True;
	#}

	# if the segments are the same, we want to discard the TU
	def segmentsDifferent(self): #{
		segs = []
		for lang in self.seg: #{
			segs.append(self.seg[lang]);
		#}

		if segs[0].strip() == segs[1].strip(): #{
			return False;
		#}
		
		return True;
	#}

	# If the length ratio is really skewed, we want to discard the TU
	def lengthSimilar(self): #{
		segs = []
		for lang in self.seg: #{
			segs.append(self.seg[lang]);
		#}
		
		len1 = float(len(segs[0]));
		len2 = float(len(segs[1]));

		skew = float((len1 / len2) * 100.0);

		if(skew > 150.0 or skew < 70.0): #{
			return False;
		#}

		return True;
	#}

	#
	#	In this function we put lots of nice tests to see if 
	#	the TU is good or not, returns False (bad), True (good)
	#
	def goodTU(self): #{
		return (self.segmentsDifferent() and self.containsAlphabetic() and self.lengthSimilar() and self.samePunctuation());
	#}


	###############################################################
	#
	#	XML PARSING
	#

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
		if name == 'tu' and self.goodTU(): #{
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

if len(sys.argv) < 1: #{
	print 'Usage: tmx-trim.py <file>';
	print '';
	sys.exit(-1);
#}

curHandler = TMXHandler();

parser.setContentHandler(curHandler);

print '<?xml version="1.0" encoding="UTF-8"?>';
print '<tmx version="1.4">';
print '<body>';

parser.parse(open(sys.argv[1]));

print '</body>';
print '</tmx>';

