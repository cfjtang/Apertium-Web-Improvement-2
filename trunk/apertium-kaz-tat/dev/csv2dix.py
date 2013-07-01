#!/usr/bin/python3
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys;

# The generic transliteration table:
# - In all tables, longest strings at the top.
ttable = {
	'ä':'ә',
	'ņ':'ң',
	'a':'а',
	'b':'б',
	'c':'дж',
	'ç':'ч',
	'd':'д',
	'e':'е',
	'ş':'ш',
	'f':'ф',
	'g':'г',
	'i':'и',
	'j':'ж',
	'k':'к',
	'l':'л',
	'm':'м',
	'n':'н',
	'o':'о',
	'p':'п',
	'r':'р',
	's':'с',
	't':'т',
	'u':'у',
	'v':'в',
	'y':'й',
	'z':'з',
	'ı':'ы',
	'\'':'ь',
	'h̦':'һ'
};

# Repair errors in Kazakh transliteration
kazfix = {
	'йу':'ю',
	'йа':'я',
	'йе':'е',
	'йо':'ё',
	'ö':'ө',
	'ü':'ү',
	'ğ':'ғ',
	'q':'қ'
};

# Repair errors in Tatar transliteration
tatfix = {
	'йу':'ю',
	'йа':'я',
	'йе':'е',
	'йо':'ё',
	'ö':'ө',
	'ü':'ү',
	'ğ':'г',
	'q':'к'
};

# Repair errors in Russian transliteration
rusfix = {
	'рьа':'ря',
	'сьа':'ся',
	'мьа':'мя',
	'ийе':'ие',
	'ойе':'ое',
	'ü':'ю',
	'ö':'ё',
	'йу':'ю',
	'йа':'я',
	'йе':'е',
	'йо':'ё',
	'тс':'ц',
	'һ':'х'
};

def t(s, v): #{
	out = s;
	for c in ttable: #{
		out = out.replace(c, ttable[c]);
	#}

	fix = {};
	if v == 'kaz': #{
		fix = kazfix;
	elif v == 'tat': #{
		fix = tatfix;
	elif v == 'rus': #{
		fix = rusfix;
	#}

	for c in fix: #{
		out = out.replace(c, fix[c]);
	#}

	return out;
#}

def guess_pos(s): #{
	pos = '<s n="unk"/>';

	end_n = ['ность', 'ство', 'ция', 'ние'];
	end_a = ['ный', 'ний'];	
	end_v = ['ить', 'ать', 'ться'];	

	for i in end_n: #{
		if s.count(i) > 0: #{
			pos = '<s n="n"/>';
		#}
	#}

	for i in end_a: #{
		if s.count(i) > 0: #{
			pos = '<s n="adj"/>';
		#}
	#}

	for i in end_v: #{
		if s.count(i) > 0: #{
			pos = '<s n="v"/><s n="TD"/>';
		#}
	#}

	return pos;
#}

#jen̦ilüv,cin̦ilü,snova
#jergilikti,"bu urındağı","zemnoye t'agoteniye"
#"sıkak (ädebiyet)","açı kölü",mestnıy
#jer,cir,pobedit'

for line in open(sys.argv[1]).readlines(): #{
	if line.count(',') < 1: #{
		continue;
	#}

	row = line.strip().split(','); 
	
	kaz = t(row[0], 'kaz');
	tat = t(row[1], 'tat');
	rus = t(row[2], 'rus');

	pos = guess_pos(rus);	

	print('<e><p><l>%s%s</l><r>%s%s</r></p></e> <!-- "%s" -->' % (kaz, pos, tat, pos, rus));
#}
