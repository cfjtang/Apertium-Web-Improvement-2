#!/usr/bin/python2.5
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, string, codecs, xml, re, Ft, md5, cStringIO;
from Ft.Xml.Domlette import EntityReader;
from Ft.Xml.Domlette import NonvalidatingReader;
from Ft.Xml.Domlette import Print, PrettyPrint;
from Ft.Xml.XPath import Evaluate;

sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

class Tag: #{
	name = None;

	def __init__(self, _name, _list): #{
		self.name = _name;
		self.list = _list;

		for s in self.list: #{
			print '% ' + s;
		#}
	#}

	def get_list(self): #{
		return self.list;
	#}
#}

class Paradigm: #{
	name = None;
	parameters = None;

	def __init__(self, _name): #{
		self.name = _name;
		self.stems = [];
		self.parameters = [];
	#}

	def add_gloss(self, _gloss): #{
		self.gloss = _gloss;
	#}

	def add_stem(self, _stem, _symlist): #{
		#print >> sys.stderr, 'add_stem(' + _stem + ', ' + _symlist + ')';
		self.stems.append((_stem, _symlist));
	#}

	def add_parameter(self, _parameter): #{
		self.parameters.append(_parameter);
	#}

	def get_stems(self): #{
		#print >> sys.stderr, self.stems;
		return self.stems;
	#}
#}

class Dictionary: #{
	display = None;
	language = None;
	file = None;
	side = None;
	alternatives = None;
	lemmas = None; 

	def __init__(self, _side, _language, _file, _doc, _alternatives, _tags, _templates): #{
		self.display = {};
		self.language = _language;
		self.file = _file;
		self.doc = _doc;
		self.side = _side;
		self.paradigms = {};
		self.glosses = {};
		self.lemmas = {};
		self.alternatives = _alternatives;
		self.tags = _tags;
		self.hashes = {};
		self.templates = _templates;

		if _side == 'bidix': #{
			self.hashes_left = {};
			self.hashes_right = {};
		#}
	#}

	def get_lemmas(self): #{
		return self.lemmas;
	#}

	def set_lemmas(self, _lemmas): #{
		self.lemmas = _lemmas;
	#}

	def add_lemma(self, _lemma): #{
		self.lemmas[_lemma] = _lemma;
	#}

	def get_alternatives(self): #{
		return self.alternatives;
	#}

	def set_alternatives(self, _alternatives): #{
		self.alternatives = _alternatives;
	#}

	def get_tags(self): #{
		return self.tags;
	#}

	def get_tag_by_tag(self, _tag): #{
		return self.tags[_tag];
	#}

	def get_paradigms(self): #{
		return self.paradigms;
	#}

	def get_glosses(self): #{
		return self.glosses;
	#}

	def get_paradigms_by_tag(self, _tag): #{
		return self.paradigms[_tag];
	#}

	def get_paradigm(self, _name, _tag): #{
		paradigm = None;

		paradigm = self.paradigms[_tag].get(_name);
		
		if paradigm == None: #{
			print >> sys.stderr, 'We didn`t find the paradigm in the hash';
			for _paradigm in self.paradigms[_tag].values(): #{
				if _paradigm.name == _name: #{	
					paradigm = _paradigm;	
				#}
			#}

			if paradigm == None: #{
				return None;
			#}
		#}

		# paradigm stems already loaded
		if len(paradigm.stems) > 0: #{
			return paradigm;
		#}

		print >> sys.stderr, 'get_paradigm ' , paradigm.name , _name;
		path = ".//pardef[@n='" + _name + "']";
		res = self.doc.xpath(path)[0];
		
		for entrada in Ft.Xml.XPath.Evaluate('.//e', contextNode=res): #{
			symlist = '';

			pair = Ft.Xml.XPath.Evaluate('.//p', contextNode=entrada)[0]; 
			left = Ft.Xml.XPath.Evaluate('.//l', contextNode=pair)[0].firstChild; 

			if type(left) != type(None): #{
				left = Ft.Xml.XPath.Evaluate('.//l', contextNode=pair)[0].firstChild.nodeValue;
			#}

			if type(left) == type(None): #{
				left = '';
			#}

			right = Ft.Xml.XPath.Evaluate('.//r', contextNode=pair)[0]; 

			for symbol in Ft.Xml.XPath.Evaluate('.//s', contextNode=right): #{
				if symlist != '': #{
					symlist = symlist + '.' + symbol.getAttributeNS(None, 'n');
				#}
				if symlist == '': #{
					symlist = symlist + symbol.getAttributeNS(None, 'n');
				#}
			#}

			print >> sys.stderr, 'get_paradigm ' , left , symlist;
			paradigm.add_stem(left, symlist);
		#}

		return paradigm;
	#}

	def hash_paradigm(self, _paradigm, _tag): #{
                paradigm_hash = [];
		current_paradigm = _paradigm.getAttributeNS(None, 'n');
		current_category = _tag;

                for entrada in Ft.Xml.XPath.Evaluate('.//e', contextNode=_paradigm): #{
                        restriction = entrada.getAttributeNS(None, 'r');
                        if type(restriction) == None: #{
                                restriction = '';
                        #}

                        symbols = '';

                        for symbol in  Ft.Xml.XPath.Evaluate('.//s', contextNode=entrada): #{
                                symbols = symbols + symbol.getAttributeNS(None, 'n') + '.';
                        #}

                        paradigm_hash.append((restriction, symbols));
                #}
                m = md5.new();
                m.update(str(set(paradigm_hash)));
                key = current_category + '.' + m.hexdigest();

                #print >> sys.stderr, 'generate_hash: ' + current_category + '.' + m.hexdigest() , current_paradigm;

		return current_category + '.' + m.hexdigest();
	#}

	def set_paradigms_by_tag(self, _tag): #{
		print self.side + ' set_paradigms_by_tag(' + _tag + ')';
		paradigms = self.doc.xpath('//pardef');
		self.paradigms[_tag] = {};
		needle = '.*__' + _tag + '$';
		patron = re.compile(needle);
		for paradigm in paradigms: #{
			n = paradigm.getAttributeNS(None, 'n');
			if(patron.match(n)): #{
				p = Paradigm(n);
				self.paradigms[_tag][n] = p;
				self.hashes[n.decode('utf-8')] = self.hash_paradigm(paradigm, _tag);
			#}
		#}

		print self.side + '  set ' , len(self.paradigms[_tag]) , 'paradigms';
	#}

	def set_display(self, _tag, _mode): #{
		if _mode == None or _mode == '': #{
			self.display[_tag] = 'all';
		#}

		if _mode != None and _mode != '': #{
			self.display[_tag] = _mode;
		#}
	#}

	def get_display_by_tag(self, _tag): #{
		if _tag in self.display: #{
			return self.display[_tag];
		#}

		return 'all';
	#}

	def get_displays(self): #{
		return self.display;
	#}


	def add_gloss(self, _tag, _paradigm, _gloss): #{
		self.glosses[_paradigm] = _gloss;
	#}

        def generate_monodix_entrada(self, _lemma, _paradigm, _restriction, _comment, _author, _alternative): #{
                incondicional = self.incondicional(_lemma, _paradigm);

		print >> sys.stderr, 'lemma: ' + _lemma + ', paradigm: ' + _paradigm + ', comment: ' + _comment + ', author: ' + _author, 'alternative: ' + _alternative;

		if _lemma in self.lemmas: #{
			return 'Lemma already exists';
		#}

		entrada = '';
		if (_restriction == "none" or _restriction == '') and (_alternative == "none" or _alternative == ''): #{
			entrada = entrada + '<e lm="' + _lemma + '" a="' + _author + '" c="">' + "\n";
		elif (_alternative == "none" or _alternative == '') and (_restriction != "none" or _restriction != ''): #{
			entrada = entrada + '<e r="' + _restriction + '" lm="' + _lemma + '" a="' + _author + '" c="">' + "\n";
		elif (_restriction == "none" or _restriction == '') and (_alternative != "none" or _alternative != ''): #{
			entrada = entrada + '<e lm="' + _lemma + '" a="' + _author + '" alt="' + _alternative + '" c="">' + "\n";
		else: #{
			entrada = entrada + '<e alt="' + _alternative + '" r="' + _restriction + '" lm="' + _lemma + '" a="' + _author + '" c="">' + "\n";
		#}

		entrada = entrada + '  <i>' + incondicional.replace(' ', '<b/>') + '</i>' + "\n";
		entrada = entrada + '  <par n="' + _paradigm + '"/>' + "\n";
		entrada = entrada + '</e>';

                if _comment != '': #{
			entrada = entrada +  '<!-- ' + _comment + ' -->' + "\n";
			entrada = entrada.replace('c=""', 'c="' + _comment + '"');
                #}

		print >> sys.stderr, entrada;

                return entrada;
        #}

        def generate_generic_bidix_entrada(self, _lemma1, _lemma2, _tag, _restriction, _comment, _author, _alternative): #{
                entrada = '';

		if (_restriction == "none" or _restriction == '') and (_alternative == "none" or _alternative == ''): #{
			entrada = entrada + '<e a="' + _author + '" c="">' + "\n";
		elif (_alternative == "none" or _alternative == '') and (_restriction != "none" or _restriction != ''): #{
			entrada = entrada + '<e r="' + _restriction + '" a="' + _author + '" c="">' + "\n";
		elif (_restriction == "none" or _restriction == '') and (_alternative != "none" or _alternative != ''): #{
			entrada = entrada + '<e a="' + _author + '" alt="' + _alternative + '" c="">' + "\n";
                else: #{
                        entrada = entrada + '<e alt="' + _alternative + '" r="' + _restriction + '" a="' + _author + '" c="">' + "\n";
                #}

		_symbol_list_left = '<s n="' + _tag + '"/>';
		_symbol_list_right = '<s n="' + _tag + '"/>';

                entrada = entrada + '  <p>' + "\n";
                entrada = entrada + '    <l>' + _lemma1.replace(' ', '<b/>') + _symbol_list_left + '</l>' + "\n";
                entrada = entrada + '    <r>' + _lemma2.replace(' ', '<b/>') + _symbol_list_right + '</r>' + "\n";
                entrada = entrada + '  </p>' + "\n";
                entrada = entrada + '</e>' + "\n";

                if _comment != '': #{
                        entrada = entrada + '<!-- ' + _comment + ' -->' + "\n";
			entrada = entrada.replace('c=""', 'c="' + _comment + '"');
                #}

                print >> sys.stderr, entrada;

                return entrada;
	#}

        def generate_bidix_entrada(self, _lemma1, _lemma2, _paradigm1, _paradigm2, _tag, _restriction, _comment, _author, _alternative): #{
		print >> sys.stderr,  'generate_bidix_entrada (' + self.side + ')';

		if _lemma1 == '' or _lemma2 == '' or _paradigm1 == None or _paradigm2 == None: #
			return '';
		#}

		if _tag == 'n' or _tag == 'adj' or _tag == 'np': #{
			hash_left = self.hashes_left[_paradigm1.name];
			hash_right = self.hashes_right[_paradigm2.name];
			print >> sys.stderr, 'left: ' + hash_left + '; right: ' + hash_right + '; n. templates: ' + str(len(self.templates));
	
			if hash_left not in self.templates: #{
				print >> sys.stderr, 'left hash not found in templates: ' + hash_left;
				return self.generate_generic_bidix_entrada(_lemma1, _lemma2, _tag, _restriction, _comment, _author, _alternative); 
			#}

			if hash_right not in self.templates[hash_left]: #{
				print >> sys.stderr, 'right hash not found in templates[' + hash_left + ']: ' + hash_right;
				for key in self.templates[hash_left]: #{
					print >>sys.stderr, '* ' + key; 
				#}
				return self.generate_generic_bidix_entrada(_lemma1, _lemma2, _tag, _restriction, _comment, _author, _alternative); 
			#}
	
			entrada = self.templates[hash_left][hash_right];
			entrada = entrada.replace('lemma1', _lemma1).replace('lemma2', _lemma2);
			if len(_alternative) > 0: #{
				entrada = entrada.replace('<e ', '<e alt="' + _alternative + '" ');
				entrada = entrada.replace('<e>', '<e alt="' + _alternative + '">');
			#}

                	print >> sys.stderr, entrada;
			
			return entrada;
		#}

                return self.generate_generic_bidix_entrada(_lemma1, _lemma2, _tag, _restriction, _comment, _author, _alternative);
        #}

        def incondicional(self, _lemma, _paradigm): #{
                if _paradigm.count('/') < 1: #{
                        return _lemma;
                #}

                paradigm = _paradigm.decode('utf-8');
                bar_pos = paradigm.find('/');
                und_pos = paradigm.find('_');
                chr_str = (und_pos - bar_pos) - 1;
                l = _lemma.decode('utf-8');
                r = l[0:(len(l) - chr_str)];

                return r.encode('utf-8');
        #}

	def append(self, _entrada, _lemma_left, _lemma_right): #{
		if self.side == 'right': #{
			print >> sys.stderr, 'Adding right lemma to hash';
			self.add_lemma(_lemma_right);
		#}
		if self.side == 'left': #{
			print >> sys.stderr, 'Adding left lemma to hash';
			self.add_lemma(_lemma_left);
		#}

		print >> sys.stderr, '> ' , self.file;
		print >> sys.stderr, self.side + ' append(';
		print >> sys.stderr, _entrada;
		print >> sys.stderr, ')';

		for section in self.doc.xpath('.//section'): #{
			print >> sys.stderr , '+ section : ' + section.getAttributeNS(None, 'id');
			if section.getAttributeNS(None, 'id') == 'main': #{
				print >> sys.stderr , 'Appending to section....';
				insertion_point = section;
				child_doc = EntityReader.parseString(_entrada.encode('utf-8'), 'urn:bogus:dummy');
				for child_node in child_doc.xpath('.//e'): #{
					insertion_point.appendChild(child_node);
				#}
				print >> sys.stderr , 'Appended.';
			#}
		#}
	#}

	def commit(self): #{
		print >> sys.stderr, 'Writing out DOM to ' + self.file;
		f = open(self.file, 'w');
		Print(self.doc, stream=f);
		f.close();
		print >> sys.stderr, 'Written.';

	#}
#}

class Pair: #{
	name = None;	

	def __init__(self, _working, _name, _parent): #{
		self.working = _working;
		self.name = _name;
		self.parent = _parent;
		self.dictionary = {};
		self.cache = {};
		self.tags = {};
		self.templates = {};

		self.populate();
	#}

	def populate(self): #{
		print self.name + ' populate()';
		for dictionary in self.parent.xpath('.//dictionary'): #{
			current_dict = dictionary.getAttributeNS(None, 'n');
			side = dictionary.getAttributeNS(None, 'side');
			filename = dictionary.getAttributeNS(None, 'file');
			filename = self.working + '/cache/' + self.name + '/' + filename;

			print ' % (' + current_dict + ') ' + side + ', ' + filename;

			alternatives = {};

			for alt in dictionary.xpath('.//alt'): #{
				alt_n = alt.getAttributeNS(None, 'n');
				alternatives[alt_n] = alt.getAttributeNS(None, 'c');
				print '  ++ alternative: ' + alt_n + ' (' + alternatives[alt_n] + ')';
			#}

			doc = NonvalidatingReader.parseUri('file:///' + filename);
			self.dictionary[side] = Dictionary(side, current_dict, filename, doc, alternatives, self.tags, self.templates);
		#}
		self.dictionary['bidix'].hashes_left = self.dictionary['left'].hashes;
		self.dictionary['bidix'].hashes_right = self.dictionary['right'].hashes;

		self.get_existent_lemmas(self.dictionary['left'], self.dictionary['right'], self.dictionary['bidix'].doc);

		print 'Lemmas: ' , len(self.dictionary['left'].get_lemmas()) , len(self.dictionary['right'].get_lemmas());
	#}

	def dictionary(self, _side): #{
		return self.dictionaries[_side];
	#}

	#
	#	Get the lemmas which already exist in the bilingual dictionary
	#	and assign them to the left and right dictionaries.
	# 
	def get_existent_lemmas(self, _left, _right, _bidix_doc): #{
		print >> sys.stderr, 'get_existent_lemmas()';

		for entrada in _bidix_doc.xpath('/dictionary/section[@id="main"]/e'): #{
			p = Ft.Xml.XPath.Evaluate('.//p', contextNode=entrada);
			if len(p) > 0: #{
				l = Ft.Xml.XPath.Evaluate('.//l', contextNode=p[0]);
				r = Ft.Xml.XPath.Evaluate('.//r', contextNode=p[0]);

				#print >> sys.stderr , 'l[0]: ' , l[0].firstChild
				if l[0].firstChild != type(None): #{
					lemma_left = getattr(l[0], 'nodeValue', '')
					#lemma_left = l[0].firstChild.nodeValue;	
				else: #{
					lemma_left = '';
				#}
				#print >> sys.stderr , 'r[0]: ' , r[0].firstChild
				if r[0].firstChild != type(None): #{
					lemma_right = getattr(r[0], 'nodeValue', '')
					#lemma_right = r[0].firstChild.nodeValue;
				else: #{
					lemma_right = '';
				#}

				if lemma_right == '' and lemma_left == '': #{
					continue;
				#}

				_left.add_lemma(lemma_left);
				_right.add_lemma(lemma_right);
			#}
			i = Ft.Xml.XPath.Evaluate('.//i', contextNode=entrada);
			if len(i) > 0: #{
				lemma = i[0].firstChild.nodeValue;
				_left.add_lemma(lemma);
				_right.add_lemma(lemma);
			#}
		#}
	#}

	def set_templates(self, _templates): #{
		print >> sys.stderr , 'Loaded ' + str(len(_templates)) + ' templates';
		self.templates = _templates;

		self.dictionary['left'].templates = _templates;
		self.dictionary['bidix'].templates = _templates;
		self.dictionary['right'].templates = _templates;
	#}

	def add_tag(self, _name, _list): #{
		print 'add_tag(' + _name + ')';
		self.tags[_name] = Tag(_name, _list);
		self.dictionary['left'].set_paradigms_by_tag(_name);
		self.dictionary['right'].set_paradigms_by_tag(_name);
	#}

	def get_tags(self): #{
		return self.tags;
	#}

	def commit(self): #{
		print >> sys.stderr , self.name + ' commit()';

		self.dictionary['left'].commit();
		self.dictionary['bidix'].commit();
		self.dictionary['right'].commit();
	#}
#}
