#!/usr/bin/python3
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, string, codecs, xml, os, re, hashlib 
import xml.etree.ElementTree as ET

categories = ['n', 'adj', 'np'];

if len(sys.argv) < 4: #{
	print('Usage: python generate-bidix-templates.py <left monodix> <bidix> <right monodix>');
	sys.exit(-1);
#}

left_file  =  sys.argv[1];
bidix_file =  sys.argv[2];
right_file =  sys.argv[3];

left = ET.parse(left_file);
bidix = ET.parse(bidix_file);
right = ET.parse(right_file);
	
def generate_monodix_hash(context): #{
	print('# generate_monodix_hash', file=sys.stderr);
	path = './/pardef';
	paradigms = {};
	for paradigm in context.findall(path): #{
		current_paradigm = paradigm.attrib['n'];
		current_category = '';
		ignoring = 1;
		for tag in categories: #{
			needle = '.*__' + tag + '$';
			patron = re.compile(needle);
			if(patron.match(current_paradigm)): #{
				current_category = tag;
				ignoring = 0;
			#}
		#}
	
		if ignoring == 1: #{
			continue;
		#}
	
		paradigm_hash = [];
		for entrada in paradigm.findall('.//e'): #{
			restriction = '';
			if 'r' in entrada.attrib: #{
				restriction = entrada.attrib['r'];
			#}
	
			symbols = '';
	
			for symbol in  entrada.findall('.//s'): #{
				symbols = symbols + symbol.attrib['n'] + '.';
			#}
	
			paradigm_hash.append((restriction, symbols));
		#}
		m = hashlib.md5(str(set(paradigm_hash)).encode('utf-8'));
		key = current_category + '.' + m.hexdigest();
		if key not in paradigms: #{
			paradigms[key] = [];
		#}


		print('generate_monodix_hash: ' + current_category + '.' + m.hexdigest() , current_paradigm, file=sys.stderr);
		paradigms[key].append(current_paradigm); 
	#}

	return paradigms;
#}

def generate_entry_list(context, paradigms): #{
	path = './/section[@id="main"]/e';
	entries = {};
	for entry in context.findall(path): #{
		lema = entry.attrib['lm'];
		pars = entry.findall('.//par');
		if len(pars) >= 1: #{
			par = pars[0].attrib['n'];
			for hash in paradigms: #{
				if par in paradigms[hash]: #{
					if lema not in entries: #{
						entries[lema] = {};
					#}
					category = hash.split('.')[0];
					entries[lema][category] = hash;

					print('## generate_entry_list:', lema + '.' + category, ';', par, ';',  hash, file=sys.stderr);
				#}
			#}
		#}
	#}

	return entries;
#}

def retrieve_lemma(entry, side): #{

	full_lemma = '';
	for kid in side[0].itertext(): #{
		full_lemma = full_lemma + ' ' + kid;
	#}

	return full_lemma.strip();
#}

def retrieve_category(entry, side): #{
	for kid in side[0].findall('.//s'): #{
		return kid.attrib['n'];
	#}
	return '';
#}

def equal_entries(entry1, entry2): #{
	equal = False;

	entrada1 = ET.tostring(entry1, encoding='utf-8', method='xml')

	entrada2 = ET.tostring(entry2, encoding='utf-8', method='xml')

	print('--', file=sys.stderr);
	print('entrada1: ', entrada1, file=sys.stderr);
	print('entrada2: ', entrada2, file=sys.stderr);
	print('--', file=sys.stderr);

	if entrada1 == entrada2: #{
		equal = True;
	#}

	print(equal, file=sys.stderr);
	print('--', file=sys.stderr);

	return equal;
#}

def entry_exists(existing, new): #{
	print('@ existing:', type(existing), existing, file=sys.stderr);
	print('@ new:', type(new), new, file=sys.stderr);

	existing_ent = '<doc>' + existing + '</doc>';
	new_ent = '<doc>' + new + '</doc>';

	print(' %%%%%%%%%%%%%% ', file=sys.stderr);
	print(' %% existing_ent %% ', file=sys.stderr);
	print(' ' , existing_ent, file=sys.stderr);
	print(' %% new_ent %% ', file=sys.stderr);
	print(' ' , new_ent, file=sys.stderr);
	print(' %%%%%%%%%%%%%% ', file=sys.stderr);

	existing_doc = ET.fromstring(existing_ent);
	new_doc = ET.fromstring(new_ent);

	for node in existing_doc.findall('.//e'): #{
		for new_node in new_doc.findall('.//e'): #{
			if equal_entries(node, new_node) == True: #{
				return True;
			#}
		#}
	#}

	return False;
#}

def generate_templates(context, left_entries, right_entries): #{
	print('# generate_templates', file=sys.stderr);
	print(left_entries, file=sys.stderr);
	print(right_entries, file=sys.stderr);
	print('#####');
	path = './/section[@id="main"]/e';
	template_matrix = {};

	for entry in context.findall(path): #{
		if 'a' in entry.attrib: del entry.attrib['a'];
		if 'c' in entry.attrib: del entry.attrib['c'];
		if 'slr' in entry.attrib: del entry.attrib['slr'];
		if 'srl' in entry.attrib: del entry.attrib['srl'];
		if 'alt' in entry.attrib: del entry.attrib['alt'];

		if len(entry.findall('.//i')) > 0: #{
			continue;
		#}
		left = entry.findall('.//l');
		right = entry.findall('.//r');

		left_lemma = retrieve_lemma(entry, left);
		right_lemma = retrieve_lemma(entry, right);

		left_symbol = retrieve_category(entry, left);
		right_symbol = retrieve_category(entry, right);

		if left_symbol == '' or right_symbol == '': #{
			print('@ No category found in the bidix for ' , left_lemma , ':' , right_lemma, file=sys.stderr);
			continue;
		#}

		if left_symbol not in categories and right_symbol not in categories: #{
			continue;
		#}

		print('# generate_templates: ' + left_lemma , left_symbol , ':' , right_lemma, right_symbol, file=sys.stderr);

		if left_lemma not in left_entries: #{
			#print('@ Key not found [l]: ' , left_lemma , '(' + str(len(left_entries)) + ')', left_entries, file=sys.stderr);
			continue;
		#}
		try:
			left_hash = left_entries[left_lemma][left_symbol];
		except:
			continue;

		if right_lemma not in right_entries: #{
			#print('@ Key not found [r]: ' , right_lemma , '(' + str(len(right_entries)) + ')', right_entries, file=sys.stderr);
			continue;
		#}
		try:
			right_hash = right_entries[right_lemma][right_symbol];
		except: 
			continue;

		if left_hash not in template_matrix: #{
			template_matrix[left_hash] = {};
		#}

		if right_hash not in template_matrix[left_hash]: #{
			template_matrix[left_hash][right_hash] = {};
		#}

		bidix_hash = left_lemma + '.' + left_hash + ':' + right_lemma + '.' + right_hash;
		#bidix_hash = left_hash + ':' + right_hash;

		entrada = ET.tostring(entry, encoding='utf-8', method='xml').decode('utf-8');
		entrada = entrada.replace('<b/>', ' ').replace(left_lemma, 'lemma1').replace(right_lemma, 'lemma2').strip();

		if bidix_hash not in template_matrix[left_hash][right_hash]: #{
			template_matrix[left_hash][right_hash][bidix_hash] = '';
			template_matrix[left_hash][right_hash][bidix_hash] = template_matrix[left_hash][right_hash][bidix_hash] + '\n' + entrada;
		else: #{
			if entry_exists(template_matrix[left_hash][right_hash][bidix_hash], entrada) != True: #{
				template_matrix[left_hash][right_hash][bidix_hash] = template_matrix[left_hash][right_hash][bidix_hash] + '\n' +  entrada;
			#}
		#}

		print('@', template_matrix[left_hash][right_hash][bidix_hash], file=sys.stderr);
	#}
	
	templates = {};

	for left in template_matrix: #{
		for right in template_matrix[left]: #{
			for bidix in template_matrix[left][right]: #{
				# limpiador.n.b82ab3a79f3fd8ea98cde45a4ed183e0:netejador.n.b82ab3a79f3fd8ea98cde45a4ed183e0
				col = bidix.split(':');
				hash_left = col[0].split('.')[1] + '.' + col[0].split('.')[2];
				hash_right = col[1].split('.')[1] + '.' + col[1].split('.')[2];
				hash = hash_left + ':' + hash_right;
				comment = col[0].split('.')[0] + '.' + col[0].split('.')[1] + ':' + col[1].split('.')[0] + '.' + col[1].split('.')[1];

				if left not in templates: #{
					templates[left] = {};
				#}

				if right not in templates[left]: #{
					#templates[left][right] = {};

					templates[left][right] = '';
				#}

				#templates[left][right][hash] = template_matrix[left][right][bidix] + '<!-- ' + comment + ' -->';

				templates[left][right] = template_matrix[left][right][bidix] + '<!-- ' + comment + ' -->';
			#}
		#}
	#}

	print('<templates>')
	for left in templates: #{
		print('  <left id="' + left + '">');
		for right in templates[left]: #{
			print('    <right id="' + right + '">');
			#for bidix in templates[left][right]: #{
			#print(*** ' , bidix , ' ***');
			col = bidix.split(':');
			#print('      <template posl="' + col[0].split('.')[0] + '" posr="' + col[1].split('.')[0] +  '">');
			print('      <template>');

			#print(templates[left][right][bidix]);

			print(templates[left][right]);
			print('      </template>');

			#}
			print('    </right>')
		#}
		print('  </left>');
	#}
	print('</templates>')
#}

left_paradigms = generate_monodix_hash(left);
right_paradigms = generate_monodix_hash(right);

left_entries = generate_entry_list(left, left_paradigms);
right_entries = generate_entry_list(right, right_paradigms);

templates = generate_templates(bidix, left_entries, right_entries);

#eof
