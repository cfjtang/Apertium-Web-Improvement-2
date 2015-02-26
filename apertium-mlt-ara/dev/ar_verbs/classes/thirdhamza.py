#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-


## ----------------------------------------------------------------------------##
## thirdhamza verbs
## ----------------------------------------------------------------------------##


def thirdhamza_past_actv(base, base_a, base_u, tv): #{

	forms = {};
	paradigm = 'S__فتح/ه';

	if tv == 'iv' : #{
		forms['actv.past.p3.m.sg'] = [(base, '-', '-')];
		forms['actv.past.p3.f.sg'] = [(base + 'ت', '-', '-')];
		forms['actv.past.p2.m.sg'] = [(base + 'ت', '-', '-')];
		forms['actv.past.p2.f.sg'] = [(base + 'ت', '-', '-')];
		forms['actv.past.p1.mf.sg'] = [(base + 'ت', '-', '-')];

		# no additional ا here
		forms['actv.past.p3.m.du'] = [(base_a, '-', '-')];
		forms['actv.past.p3.f.du'] = [(base + 'تا', '-', '-')];
		forms['actv.past.p2.mf.du'] = [(base + 'تما', '-', '-')];

		forms['actv.past.p3.m.pl'] = [(base_u + 'وا', '-', '-')];
		forms['actv.past.p3.f.pl'] = [(base + 'ن', '-', '-')];
		forms['actv.past.p2.m.pl'] = [(base + 'تم', '-', '-')];
		forms['actv.past.p2.f.pl'] = [(base + 'تن', '-', '-')];
		forms['actv.past.p1.mf.pl'] = [(base + 'نا', '-', '-')];
	#}
	else : #{
		forms['actv.past.p3.m.sg'] = [(base, '-', '-'), (base, paradigm, '-')];
		forms['actv.past.p3.f.sg'] = [(base + 'ت', '-', '-'), (base + 'ت', paradigm, '-')];
		forms['actv.past.p2.m.sg'] = [(base + 'ت', '-', '-'), (base + 'ت', paradigm, '-')];
		forms['actv.past.p2.f.sg'] = [(base + 'ت', '-', '-'), (base + 'ت', paradigm, '-')];
		forms['actv.past.p1.mf.sg'] = [(base + 'ت', '-', '-'), (base + 'ت', paradigm, '-')];

		forms['actv.past.p3.m.du'] = [(base_a, '-', '-'), (base_a + 'ا', paradigm, '-')];
		forms['actv.past.p3.f.du'] = [(base + 'تا', '-', '-'), (base + 'تا', paradigm, '-')];
		forms['actv.past.p2.mf.du'] = [(base + 'تما', '-', '-'), (base + 'تما', paradigm, '-')];

		forms['actv.past.p3.m.pl'] = [(base_u + 'وا', '-', '-'), (base_u + 'و', paradigm, '-')];
		forms['actv.past.p3.f.pl'] = [(base + 'ن', '-', '-'), (base + 'ن', paradigm, '-')];
		forms['actv.past.p2.m.pl'] = [(base + 'تم', '-', '-'), (base + 'تمو', paradigm, '-')];
		forms['actv.past.p2.f.pl'] = [(base + 'تن', '-', '-'), (base + 'تن', paradigm, '-')];
		forms['actv.past.p1.mf.pl'] = [(base + 'نا', '-', '-'), (base + 'نا', paradigm, '-')];
	#}

	return forms;
#}


# only one form - always final ئ here
def thirdhamza_past_pasv(base): #{

	forms = {};

	forms['pasv.past.p3.m.sg'] = [(base, '-', '-')];
	forms['pasv.past.p3.f.sg'] = [(base + 'ت', '-', '-')];
	forms['pasv.past.p2.m.sg'] = [(base + 'ت', '-', '-')];
	forms['pasv.past.p2.f.sg'] = [(base + 'ت', '-', '-')];
	forms['pasv.past.p1.mf.sg'] = [(base + 'ت', '-', '-')];

	forms['pasv.past.p3.m.du'] = [(base + 'ا', '-', '-')];
	forms['pasv.past.p3.f.du'] = [(base + 'تا', '-', '-')];
	forms['pasv.past.p2.mf.du'] = [(base + 'تما', '-', '-')];

	forms['pasv.past.p3.m.pl'] = [(base + 'وا', '-', '-')];
	forms['pasv.past.p3.f.pl'] = [(base + 'ن', '-', '-')];
	forms['pasv.past.p2.m.pl'] = [(base + 'تم', '-', '-')];
	forms['pasv.past.p2.f.pl'] = [(base + 'تن', '-', '-')];
	forms['pasv.past.p1.mf.pl'] = [(base + 'نا', '-', '-')];

	return forms;
#}


def thirdhamza_pres_actv(base, base_i, base_a, base_u, tv): #{

# LR 3 pl m forms? 

	forms = {};
	paradigm = 'S__فتح/ه';

	if tv == 'iv' : #{
		forms['actv.pres.p3.m.sg'] = [('ي' + base, '-', '-')];
		forms['actv.pres.p3.f.sg'] = [('ت' + base, '-', '-')];
		forms['actv.pres.p2.m.sg'] = [('ت' + base, '-', '-')];
		forms['actv.pres.p2.f.sg'] = [('ت' + base_i + 'ين', '-', '-')];
		forms['actv.pres.p1.mf.sg'] = [('أ' + base, '-', '-')];

		# no additional ا
		forms['actv.pres.p3.m.du'] = [('ي' + base_a + 'ن', '-', '-')];
		forms['actv.pres.p3.f.du'] = [('ت' + base_a + 'ن', '-', '-')];
		forms['actv.pres.p2.mf.du'] = [('ت' + base_a + 'ن', '-', '-')];

		forms['actv.pres.p3.m.pl'] = [('ي' + base_u + 'ون', '-', '-')];
		forms['actv.pres.p3.f.pl'] = [('ي' + base + 'ن', '-', '-')];
		forms['actv.pres.p2.m.pl'] = [('ت' + base_u + 'ون', '-', '-')];
		forms['actv.pres.p2.f.pl'] = [('ت' + base + 'ن', '-', '-')];
		forms['actv.pres.p1.mf.pl'] = [('ن' + base, '-', '-')];
	#}
	else : #{
		forms['actv.pres.p3.m.sg'] = [('ي' + base, '-', '-'), ('ي' + base, paradigm, '-')];
		forms['actv.pres.p3.f.sg'] = [('ت' + base, '-', '-'), ('ت' + base, paradigm, '-')];
		forms['actv.pres.p2.m.sg'] = [('ت' + base, '-', '-'), ('ت' + base, paradigm, '-')];
		forms['actv.pres.p2.f.sg'] = [('ت' + base_i + 'ين', '-', '-'), ('ت' + base_i + 'ي', paradigm, '-')];
		forms['actv.pres.p1.mf.sg'] = [('أ' + base, '-', '-'), ('أ' + base, paradigm, '-')];
	
		forms['actv.pres.p3.m.du'] = [('ي' + base_a + 'ن', '-', '-'), ('ي' + base_a, paradigm, '-')];
		forms['actv.pres.p3.f.du'] = [('ت' + base_a + 'ن', '-', '-'), ('ت' + base_a, paradigm, '-')];
		forms['actv.pres.p2.mf.du'] = [('ت' + base_a + 'ن', '-', '-'), ('ت' + base_a, paradigm, '-')];

		forms['actv.pres.p3.m.pl'] = [('ي' + base_u + 'ون', '-', '-'), ('ي' + base_u + 'و', paradigm, '-')];
		forms['actv.pres.p3.f.pl'] = [('ي' + base + 'ن', '-', '-'), ('ي' + base + 'ن', paradigm, '-')];
		forms['actv.pres.p2.m.pl'] = [('ت' + base_u + 'ون', '-', '-'), ('ت' + base_u + 'و', paradigm, '-')];
		forms['actv.pres.p2.f.pl'] = [('ت' + base + 'ن', '-', '-'), ('ت' + base + 'ن', paradigm, '-')];
		forms['actv.pres.p1.mf.pl'] = [('ن' + base, '-', '-'), ('ن' + base, paradigm, '-')];

	#}

	return forms;
#}



def thirdhamza_pres_pasv(base): #{

	forms = {};

	forms['pasv.pres.p3.m.sg'] = [('ي' + base + 'أ', '-', 'LR'), ('يُ' + base + 'أ', '-', 'RL')];
	forms['pasv.pres.p3.f.sg'] = [('ت' + base + 'أ', '-', 'LR'), ('تُ' + base + 'أ', '-', 'RL')];
	forms['pasv.pres.p2.m.sg'] = [('ت' + base + 'أ', '-', 'LR'), ('تُ' + base + 'أ', '-', 'RL')];
	forms['pasv.pres.p2.f.sg'] = [('ت' + base + 'ئين', '-', 'LR'), ('تُ' + base + 'ئين', '-', 'RL')];
	forms['pasv.pres.p1.mf.sg'] = [('أ' + base + 'أ', '-', 'LR'), ('اُ' + base + 'أ', '-', 'RL')];

	forms['pasv.pres.p3.m.du'] = [('ي' + base + 'آن', '-', 'LR'), ('يُ' + base + 'آن', '-', 'RL')];
	forms['pasv.pres.p3.f.du'] = [('ت' + base + 'آن', '-', 'LR'), ('تُ' + base + 'آن', '-', 'RL')];
	forms['pasv.pres.p2.mf.du'] = [('ت' + base + 'آن', '-', 'LR'), ('تُ' + base + 'آن', '-', 'RL')];

	forms['pasv.pres.p3.m.pl'] = [('ي' + base + 'ؤون', '-', 'LR'), ('يُ' + base + 'ؤون', '-', 'RL')];
	forms['pasv.pres.p3.f.pl'] = [('ي' + base + 'أن', '-', 'LR'), ('يُ' + base + 'أن', '-', 'RL')];
	forms['pasv.pres.p2.m.pl'] = [('ت' + base + 'ؤون', '-', 'LR'), ('تُ' + base + 'ؤون', '-', 'RL')];
	forms['pasv.pres.p2.f.pl'] = [('ت' + base + 'أن', '-', 'LR'), ('تُ' + base + 'أن', '-', 'RL')];
	forms['pasv.pres.p1.mf.pl'] = [('ن' + base + 'أ', '-', 'LR'), ('نُ' + base + 'أ', '-', 'RL')];

	return forms;
#}



def thirdhamza_subjun_actv(base, base_i, base_a, base_u, tv): #{

	forms = {};

	paradigm = 'S__فتح/ه';

	if tv == 'iv' : #{
		forms['actv.subjun.p3.m.sg'] = [('ي' + base, '-', '-')];
		forms['actv.subjun.p3.f.sg'] = [('ت' + base, '-', '-')];
		forms['actv.subjun.p2.m.sg'] = [('ت' + base, '-', '-')];
		forms['actv.subjun.p2.f.sg'] = [('ت' + base_i + 'ي', '-', '-')];
		forms['actv.subjun.p1.mf.sg'] = [('أ' + base, '-', '-')];

		forms['actv.subjun.p3.m.du'] = [('ي' + base_a, '-', '-')];
		forms['actv.subjun.p3.f.du'] = [('ت' + base_a, '-', '-')];
		forms['actv.subjun.p2.mf.du'] = [('ت' + base_a, '-', '-')];

		forms['actv.subjun.p3.m.pl'] = [('ي' + base_u + 'وا', '-', '-')];
		forms['actv.subjun.p3.f.pl'] = [('ي' + base + 'ن', '-', '-')];
		forms['actv.subjun.p2.m.pl'] = [('ت' + base_u + 'وا', '-', '-')];
		forms['actv.subjun.p2.f.pl'] = [('ت' + base + 'ن', '-', '-')];
		forms['actv.subjun.p1.mf.pl'] = [('ن' + base, '-', '-')];
	#}
	else : #{
		forms['actv.subjun.p3.m.sg'] = [('ي' + base, '-', '-'), ('ي' + base, paradigm, '-')];
		forms['actv.subjun.p3.f.sg'] = [('ت' + base, '-', '-'), ('ت' + base, paradigm, '-')];
		forms['actv.subjun.p2.m.sg'] = [('ت' + base, '-', '-'), ('ت' + base, paradigm, '-')];
		forms['actv.subjun.p2.f.sg'] = [('ت' + base_i + 'ي', '-', '-'), ('ت' + base_i + 'ي', paradigm, '-')];
		forms['actv.subjun.p1.mf.sg'] = [('أ' + base, '-', '-'), ('أ' + base, paradigm, '-')];
	
		forms['actv.subjun.p3.m.du'] = [('ي' + base_a, '-', '-'), ('ي' + base_a, paradigm, '-')];
		forms['actv.subjun.p3.f.du'] = [('ت' + base_a, '-', '-'), ('ت' + base_a, paradigm, '-')];
		forms['actv.subjun.p2.mf.du'] = [('ت' + base_a, '-', '-'), ('ت' + base_a, paradigm, '-')];

		forms['actv.subjun.p3.m.pl'] = [('ي' + base_u + 'وا', '-', '-'), ('ي' + base_u + 'و', paradigm, '-')];
		forms['actv.subjun.p3.f.pl'] = [('ي' + base + 'ن', '-', '-'), ('ي' + base + 'ن', paradigm, '-')];
		forms['actv.subjun.p2.m.pl'] = [('ت' + base_u + 'وا', '-', '-'), ('ت' + base_u + 'و', paradigm, '-')];
		forms['actv.subjun.p2.f.pl'] = [('ت' + base + 'ن', '-', '-'), ('ت' + base + 'ن', paradigm, '-')];
		forms['actv.subjun.p1.mf.pl'] = [('ن' + base, '-', '-'), ('ن' + base, paradigm, '-')];
	#}

	return forms;
#}


def thirdhamza_subjun_pasv(base): #{

	forms = {};

	forms['pasv.subjun.p3.m.sg'] = [('ي' + base + 'أ', '-', 'LR'), ('يُ' + base + 'أ', '-', 'RL')];
	forms['pasv.subjun.p3.f.sg'] = [('ت' + base + 'أ', '-', 'LR'), ('تُ' + base + 'أ', '-', 'RL')];
	forms['pasv.subjun.p2.m.sg'] = [('ت' + base + 'أ', '-', 'LR'), ('تُ' + base + 'أ', '-', 'RL')];
	forms['pasv.subjun.p2.f.sg'] = [('ت' + base + 'ئي', '-', 'LR'), ('تُ' + base + 'ئي', '-', 'RL')];
	forms['pasv.subjun.p1.mf.sg'] = [('أ' + base + 'أ', '-', 'LR'), ('اُ' + base + 'أ', '-', 'RL')];

	forms['pasv.subjun.p3.m.du'] = [('ي' + base + 'آ', '-', 'LR'), ('يُ' + base + 'آ', '-', 'RL')];
	forms['pasv.subjun.p3.f.du'] = [('ت' + base + 'آ', '-', 'LR'), ('تُ' + base + 'آ', '-', 'RL')];
	forms['pasv.subjun.p2.mf.du'] = [('ت' + base + 'آ', '-', 'LR'), ('تُ' + base + 'آ', '-', 'RL')];

	forms['pasv.subjun.p3.m.pl'] = [('ي' + base + 'ؤوا', '-', 'LR'), ('يُ' + base + 'ؤوا', '-', 'RL')];
	forms['pasv.subjun.p3.f.pl'] = [('ي' + base + 'أن', '-', 'LR'), ('يُ' + base + 'أن', '-', 'RL')];
	forms['pasv.subjun.p2.m.pl'] = [('ت' + base + 'ؤوا', '-', 'LR'), ('تُ' + base + 'ؤوا', '-', 'RL')];
	forms['pasv.subjun.p2.f.pl'] = [('ت' + base + 'أن', '-', 'LR'), ('تُ' + base + 'أن', '-', 'RL')];
	forms['pasv.subjun.p1.mf.pl'] = [('ن' + base + 'أ', '-', 'LR'), ('نُ' + base + 'أ', '-', 'RL')];

	return forms;
#}


def thirdhamza_apocop_actv(base, base_i, base_a, base_u, tv): #{

	forms = {};
	paradigm = 'S__فتح/ه';

	if tv == 'iv' : #{
		forms['actv.apocop.p3.m.sg'] = [('ي' + base, '-', '-')];
		forms['actv.apocop.p3.f.sg'] = [('ت' + base, '-', '-')];
		forms['actv.apocop.p2.m.sg'] = [('ت' + base, '-', '-')];
		forms['actv.apocop.p2.f.sg'] = [('ت' + base_i + 'ي', '-', '-')];
		forms['actv.apocop.p1.mf.sg'] = [('أ' + base, '-', '-')];

		forms['actv.apocop.p3.m.du'] = [('ي' + base_a, '-', '-')];
		forms['actv.apocop.p3.f.du'] = [('ت' + base_a, '-', '-')];
		forms['actv.apocop.p2.mf.du'] = [('ت' + base_a, '-', '-')];

		forms['actv.apocop.p3.m.pl'] = [('ي' + base_u + 'وا', '-', '-')];
		forms['actv.apocop.p3.f.pl'] = [('ي' + base + 'ن', '-', '-')];
		forms['actv.apocop.p2.m.pl'] = [('ت' + base_u + 'وا', '-', '-')];
		forms['actv.apocop.p2.f.pl'] = [('ت' + base + 'ن', '-', '-')];
		forms['actv.apocop.p1.mf.pl'] = [('ن' + base, '-', '-')];
	#}
	else : #{
		forms['actv.apocop.p3.m.sg'] = [('ي' + base, '-', '-'), ('ي' + base, paradigm, '-')];
		forms['actv.apocop.p3.f.sg'] = [('ت' + base, '-', '-'), ('ت' + base, paradigm, '-')];
		forms['actv.apocop.p2.m.sg'] = [('ت' + base, '-', '-'), ('ت' + base, paradigm, '-')];
		forms['actv.apocop.p2.f.sg'] = [('ت' + base_i + 'ي', '-', '-'), ('ت' + base_i + 'ي', paradigm, '-')];
		forms['actv.apocop.p1.mf.sg'] = [('أ' + base, '-', '-'), ('أ' + base, paradigm, '-')];
	
		forms['actv.apocop.p3.m.du'] = [('ي' + base_a, '-', '-'), ('ي' + base_a, paradigm, '-')];
		forms['actv.apocop.p3.f.du'] = [('ت' + base_a, '-', '-'), ('ت' + base_a, paradigm, '-')];
		forms['actv.apocop.p2.mf.du'] = [('ت' + base_a, '-', '-'), ('ت' + base_a, paradigm, '-')];

		forms['actv.apocop.p3.m.pl'] = [('ي' + base_u + 'وا', '-', '-'), ('ي' + base_u + 'و', paradigm, '-')];
		forms['actv.apocop.p3.f.pl'] = [('ي' + base + 'ن', '-', '-'), ('ي' + base + 'ن', paradigm, '-')];
		forms['actv.apocop.p2.m.pl'] = [('ت' + base_u + 'وا', '-', '-'), ('ت' + base_u + 'و', paradigm, '-')];
		forms['actv.apocop.p2.f.pl'] = [('ت' + base + 'ن', '-', '-'), ('ت' + base + 'ن', paradigm, '-')];
		forms['actv.apocop.p1.mf.pl'] = [('ن' + base, '-', '-'), ('ن' + base, paradigm, '-')];
	#}

	return forms;
#}


def thirdhamza_apocop_pasv(base): #{

	forms = {};

	forms['pasv.apocop.p3.m.sg'] = [('ي' + base + 'أ', '-', 'LR'), ('يُ' + base + 'أ', '-', 'RL')];
	forms['pasv.apocop.p3.f.sg'] = [('ت' + base + 'أ', '-', 'LR'), ('تُ' + base + 'أ', '-', 'RL')];
	forms['pasv.apocop.p2.m.sg'] = [('ت' + base + 'أ', '-', 'LR'), ('تُ' + base + 'أ', '-', 'RL')];
	forms['pasv.apocop.p2.f.sg'] = [('ت' + base + 'ئي', '-', 'LR'), ('تُ' + base + 'ئي', '-', 'RL')];
	forms['pasv.apocop.p1.mf.sg'] = [('أ' + base + 'أ', '-', 'LR'), ('اُ' + base + 'أ', '-', 'RL')];

	forms['pasv.apocop.p3.m.du'] = [('ي' + base + 'آ', '-', 'LR'), ('يُ' + base + 'آ', '-', 'RL')];
	forms['pasv.apocop.p3.f.du'] = [('ت' + base + 'آ', '-', 'LR'), ('تُ' + base + 'آ', '-', 'RL')];
	forms['pasv.apocop.p2.mf.du'] = [('ت' + base + 'آ', '-', 'LR'), ('تُ' + base + 'آ', '-', 'RL')];

	forms['pasv.apocop.p3.m.pl'] = [('ي' + base + 'ؤوا', '-', 'LR'), ('يُ' + base + 'ؤوا', '-', 'RL')];
	forms['pasv.apocop.p3.f.pl'] = [('ي' + base + 'أن', '-', 'LR'), ('يُ' + base + 'أن', '-', 'RL')];
	forms['pasv.apocop.p2.m.pl'] = [('ت' + base + 'ؤوا', '-', 'LR'), ('تُ' + base + 'ؤوا', '-', 'RL')];
	forms['pasv.apocop.p2.f.pl'] = [('ت' + base + 'أن', '-', 'LR'), ('تُ' + base + 'أن', '-', 'RL')];
	forms['pasv.apocop.p1.mf.pl'] = [('ن' + base + 'أ', '-', 'LR'), ('نُ' + base + 'أ', '-', 'RL')];

	return forms;
#}


def thirdhamza_imp(base, base_i, base_a, base_u, tv): #{

	# passive voice?

	forms = {};

	paradigm = 'S__فتح/ه';

	if tv == 'iv' : #{
		forms['actv.imp.p2.m.sg'] = [(base, '-', '-')];
		forms['actv.imp.p2.f.sg'] = [(base_i + 'ي', '-', '-')];
		forms['actv.imp.p2.mf.du'] = [(base_a, '-', '-')];
		forms['actv.imp.p2.m.pl'] = [(base_u + 'وا', '-', '-')];
		forms['actv.imp.p2.f.pl'] = [(base + 'ن', '-', '-')];
	#}
	else : #{
		forms['actv.imp.p2.m.sg'] = [(base, '-', '-'), (base, paradigm, '-')];
		forms['actv.imp.p2.f.sg'] = [(base_i + 'ي', '-', '-'), (base_i + 'ي', paradigm, '-')];
		forms['actv.imp.p2.mf.du'] = [(base_a, '-', '-'), (base_a, paradigm, '-')];
		forms['actv.imp.p2.m.pl'] = [(base_u + 'وا', '-', '-'), (base_u + 'و', paradigm, '-')];
		forms['actv.imp.p2.f.pl'] = [(base + 'ن', '-', '-'), (base + 'ن', paradigm, '-')];
	#}

	return forms ; 
#}


def thirdhamza_pprs(base, r): #{

        forms = {};

	forms['pprs.m.sg'] = [(base, 'S__قار/ئ', r)] ;
	forms['pprs.f.sg'] = [(base + 'ئ', 'S__كلم/ة', r)] ;
	forms['pprs.m.pl'] = [(base + 'ئ', 'S__مهندس/ون', r)] ;
	forms['pprs.f.pl'] = [(base + 'ئات', 'S__كلمات/', r)] ;

	return forms;
#}


def thirdhamza_pp(base, r): #{

        forms = {};

	forms['pp.m.sg'] = [(base, 'S__/أ', r)] ;
	forms['pp.f.sg'] = [(base + 'أ', 'S__كلم/ة', r)] ;
	forms['pp.m.pl'] = [(base + 'ؤ', 'S__مهندس/ون', r)] ;  # TODO: fix 
	forms['pp.f.pl'] = [(base + 'آت', 'S__كلمات/', r)] ;

	return forms;
#}




## ----------------------------------------------------------------------------##
## pattern 1
## ----------------------------------------------------------------------------##


def thirdhamza_patt1_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	if v[1] == 'a' : #{
		r2 = 'أ';
		r2a = 'آ';
		r2u = 'ؤ';
	#}
	elif v[1] == 'i' : #{
		r2 = 'ئ';
		r2a = 'ئا';
		r2u = 'ئ';
	#}
	else : #{ # v[1] == 'u'
		r2 = 'ؤ';
		r2a = 'ؤا';
		r2u = 'ؤ';
	#}

	base = r[0] + r[1] + r2;
	base_a = r[0] + r[1] + r2a;
	base_u = r[0] + r[1] + r2u;

	forms = thirdhamza_past_actv(base, base_a, base_u, tv);
	if (tv == 'tv') : #{
		base = r[0] + r[1] + 'ئ';
		forms.update(thirdhamza_past_pasv(base));
	#}

	return forms;
#}



def thirdhamza_patt1_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	if v[1] == 'a' : #{
		r2 = 'أ';
		r2i = 'ئ';
		r2a = 'آ';
		r2u = 'ؤ';
	#}
	elif v[1] == 'i' : #{
		r2 = 'ئ';
		r2i = 'ئ';
		r2a = 'ئا';
		r2u = 'ئ';
	#}
	else : #{    # v[1] == 'u'
		r2 = 'ؤ';
		r2i = 'ئ';
		r2a = 'ؤا';
		r2u = 'ؤ';
	#}

	base = r[0] + r[1] + r2;
	base_i = r[0] + r[1] + r2i;
	base_a = r[0] + r[1] + r2a;
	base_u = r[0] + r[1] + r2u;

	forms = thirdhamza_pres_actv(base, base_i, base_a, base_u, tv);
	forms.update(thirdhamza_subjun_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_apocop_actv(base, base_i, base_a, base_u, tv));

	base = 'ا' + r[0] + r[1] + r2;
	base_i = 'ا' + r[0] + r[1] + r2i;
	base_a = 'ا' + r[0] + r[1] + r2a;
	base_u = 'ا' + r[0] + r[1] + r2u;

	forms.update(thirdhamza_imp(base, base_i, base_a, base_u, tv));

	if (tv == 'tv') : #{

		base = r[0] + r[1];

		forms.update(thirdhamza_pres_pasv(base));
		forms.update(thirdhamza_subjun_pasv(base));
		forms.update(thirdhamza_apocop_pasv(base));
	#}

	return forms;
#}



def thirdhamza_patt1_pprs(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pprs(r[0] + 'ا' + r[1], '-');
#}


def thirdhamza_patt1_pp(root): #{
	r = root.split('-'); # radicals

	forms = {};

	forms['pp.m.sg'] = [('م' + r[0] + r[1] + 'و', 'S__جز/ء', '-')] ;
	forms['pp.f.sg'] = [('م' + r[0] + r[1] + 'وأ', 'S__كلم/ة', '-')] ;
	forms['pp.m.pl'] = [('م' + r[0] + r[1] + 'وؤ', 'S__مهندس/ون', '-')] ;  # TODO: fix
	forms['pp.f.pl'] = [('م' + r[0] + r[1] + 'وآت', 'S__كلمات/', '-')] ;

	return forms;

#}


## ----------------------------------------------------------------------------##
## pattern 2
## ----------------------------------------------------------------------------##


def thirdhamza_patt2_past(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + r[1] + 'أ';
	base_a = r[0] + r[1] + 'آ';
	base_u = r[0] + r[1] + 'ؤ';

	forms = thirdhamza_past_actv(base, base_a, base_u, tv);
	if (tv == 'tv') : #{
		base = r[0] + r[1] + 'ئ';

		forms.update(thirdhamza_past_pasv(base));
	#}

	return forms;
#}


def thirdhamza_patt2_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + r[1] + 'ئ';
	base_i = r[0] + r[1] + 'ئ';
	base_a = r[0] + r[1] + 'ئا';
	base_u = r[0] + r[1] + 'ئ';

	forms = thirdhamza_pres_actv(base, base_i, base_a, base_u, tv);
	forms.update(thirdhamza_subjun_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_apocop_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_imp(base, base_i, base_a, base_u, tv));

	if (tv == 'tv') : #{

		base = r[0] + r[1];

		forms.update(thirdhamza_subjun_pasv(base));
		forms.update(thirdhamza_pres_pasv(base));
		forms.update(thirdhamza_apocop_pasv(base));
	#}

	return forms;
#}



def thirdhamza_patt2_pprs(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pprs('م' + r[0] + r[1], '-');
#}


def thirdhamza_patt2_pp(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pp('م' + r[0] + r[1], '-');
#}


## ----------------------------------------------------------------------------##
## pattern 3
## ----------------------------------------------------------------------------##


def thirdhamza_patt3_past(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ا' + r[1] + 'أ';
	base_a = r[0] + 'ا' + r[1] + 'آ';
	base_u = r[0] + 'ا' + r[1] + 'ؤ';

	forms = thirdhamza_past_actv(base, base_a, base_u, tv);
	if (tv == 'tv') : #{
		base = r[0] + 'وئ' + r[1] + 'ئ';

		forms.update(thirdhamza_past_pasv(base));
	#}

	return forms;
#}


def thirdhamza_patt3_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ا' + r[1] + 'ئ';
	base_i = r[0] + 'ا' + r[1] + 'ئ';
	base_a = r[0] + 'ا' + r[1]  + 'ئا';
	base_u = r[0] + 'ا' + r[1] + 'ئ';

	forms = thirdhamza_pres_actv(base, base_i, base_a, base_u, tv);
	forms.update(thirdhamza_subjun_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_apocop_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_imp(base, base_i, base_a, base_u, tv));

	if (tv == 'tv') : #{

		base = r[0] + 'ا' + r[1];

		forms.update(thirdhamza_subjun_pasv(base));
		forms.update(thirdhamza_pres_pasv(base));
		forms.update(thirdhamza_apocop_pasv(base));
	#}

	return forms;
#}



def thirdhamza_patt3_pprs(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pprs('م' + r[0] + 'ا' + r[1], '-');
#}


def thirdhamza_patt3_pp(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pp('م' + r[0] + 'ا' + r[1], '-');
#}


## ----------------------------------------------------------------------------##
## pattern 4
## ----------------------------------------------------------------------------##


def thirdhamza_patt4_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'أ' + r[0] + r[1] + 'أ';
	base_a = 'أ' + r[0] + r[1] + 'آ';
	base_u = 'أ' + r[0] + r[1] + 'ؤ';

	forms = thirdhamza_past_actv(base, base_a, base_u, tv);
	if (tv == 'tv') : #{
		base = 'أ' + r[0] + r[1] + 'ئ';

		forms.update(thirdhamza_past_pasv(base));
	#}

	return forms;
#}


def thirdhamza_patt4_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + r[1] + 'ئ';
	base_i = r[0] + r[1] + 'ئ';
	base_a = r[0] + r[1] + 'ئا';
	base_u = r[0] + r[1] + 'ئ';

	forms = thirdhamza_pres_actv(base, base_i, base_a, base_u, tv);
	forms.update(thirdhamza_subjun_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_apocop_actv(base, base_i, base_a, base_u, tv));

	base = 'أ' + r[0] + r[1] + 'ئ';
	base_i = 'أ' + r[0] + r[1] + 'ئ';
	base_a = 'أ' + r[0] + r[1] + 'ئا';
	base_u = 'أ' + r[0] + r[1] + 'ئ';

	forms.update(thirdhamza_imp(base, base_i, base_a, base_u, tv));

	if (tv == 'tv') : #{

		base = r[0] + r[1];

		forms.update(thirdhamza_subjun_pasv(base));
		forms.update(thirdhamza_pres_pasv(base));
		forms.update(thirdhamza_apocop_pasv(base));
	#}

	return forms;
#}


def thirdhamza_patt4_pprs(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pprs('م' + r[0] + r[1], '-');
#}


def thirdhamza_patt4_pp(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pp('م' + r[0] + r[1], '-');
#}


## ----------------------------------------------------------------------------##
## pattern 5
## ----------------------------------------------------------------------------##


def thirdhamza_patt5_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + r[1] + 'أ';
	base_a = 'ت' + r[0] + r[1] + 'آ';
	base_u = 'ت' + r[0] + r[1] + 'ؤ';

	forms = thirdhamza_past_actv(base, base_a, base_u, tv);
	if (tv == 'tv') : #{
		base = 'ت' + r[0] + r[1] + 'ئ';

		forms.update(thirdhamza_past_pasv(base));
	#}

	return forms;
#}


def thirdhamza_patt5_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + r[1] + 'أ';
	base_i = 'ت' + r[0] + r[1] + 'ئ';
	base_a = 'ت' + r[0] + r[1] + 'آ';
	base_u = 'ت' + r[0] + r[1] + 'ؤ';

	forms = thirdhamza_pres_actv(base, base_i, base_a, base_u, tv);
	forms.update(thirdhamza_subjun_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_apocop_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_imp(base, base_i, base_a,base_u, tv));

	if (tv == 'tv') : #{
		base = 'ت' + r[0] + r[1];

		forms.update(thirdhamza_subjun_pasv(base));
		forms.update(thirdhamza_pres_pasv(base));
		forms.update(thirdhamza_apocop_pasv(base));
	#}

	return forms;
#}


def thirdhamza_patt5_pprs(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pprs('مت' + r[0] + r[1], '-');
#}


# rare
def thirdhamza_patt5_pp(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pp('مت' + r[0] + r[1], '-');
#}


## ----------------------------------------------------------------------------##
## pattern 6
## ----------------------------------------------------------------------------##


def thirdhamza_patt6_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + 'ا' + r[1] + 'أ';
	base_a = 'ت' + r[0] + 'ا' + r[1] + 'آ';
	base_u = 'ت' + r[0] + 'ا' + r[1] + 'ؤ';

	forms = thirdhamza_past_actv(base, base_a, base_u, tv);
	if (tv == 'tv') : #{
		base = 'ت' + r[0] + 'و' + r[1] + 'ئ';

		forms.update(thirdhamza_past_pasv(base));
	#}

	return forms;
#}


def thirdhamza_patt6_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + 'ا' + r[1] + 'أ';
	base_i = 'ت' + r[0] + 'ا' + r[1] + 'ئ';
	base_a = 'ت' + r[0] + 'ا' + r[1] + 'آ';
	base_u = 'ت' + r[0] + 'ا' + r[1] + 'ؤ';

	forms = thirdhamza_pres_actv(base, base_i, base_a, base_u, tv);
	forms.update(thirdhamza_subjun_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_apocop_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_imp(base, base_i, base_a, base_u, tv));

	if (tv == 'tv') : #{
		base = 'ت' + r[0] + 'ا' + r[1];

		forms.update(thirdhamza_subjun_pasv(base));
		forms.update(thirdhamza_pres_pasv(base));
		forms.update(thirdhamza_apocop_pasv(base));
	#}

	return forms;
#}


def thirdhamza_patt6_pprs(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pprs('مت' + r[0] + 'ا' + r[1], '-');
#}


def thirdhamza_patt6_pp(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pp('مت' + r[0] + 'ا' + r[1], '-');
#}


## ----------------------------------------------------------------------------##
## pattern 7
## ----------------------------------------------------------------------------##


def thirdhamza_patt7_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ان' + r[0] + r[1] + 'أ';
	base_a = 'ان' + r[0] + r[1] + 'آ';
	base_u = 'ان' + r[0] + r[1] + 'ؤ';

	return thirdhamza_past_actv(base, base_a, base_u, tv);

	return forms;
#}


def thirdhamza_patt7_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ن' + r[0] + r[1] + 'ئ';
	base_i = 'ن' + r[0] + r[1] + 'ئ';
	base_a = 'ن' + r[0] + r[1] + 'ئا';
	base_u = 'ن' + r[0] + r[1] + 'ئ';

	forms = thirdhamza_pres_actv(base, base_i, base_a, base_u, tv);
	forms.update(thirdhamza_subjun_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_apocop_actv(base, base_i, base_a, base_u, tv));

	base = 'ان' + r[0] + r[1] + 'ئ';
	base_i = 'ان' + r[0] + r[1] + 'ئ';
	base_a = 'ان' + r[0] + r[1] + 'ئا';
	base_u = 'ان' + r[0] + r[1] + 'ئ';

	forms.update(thirdhamza_imp(base, base_i, base_a, base_u, tv));

	return forms;

#}


def thirdhamza_patt7_pprs(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pprs('من' + r[0] + r[1], '-');
#}


## ----------------------------------------------------------------------------##
## pattern 8
## ----------------------------------------------------------------------------##


def thirdhamza_patt8_past(root, tv): #{
	r = root.split('-'); # radicals

	base_start = 'ا' + r[0] + 'ت';
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base_start = 'ا' + r[0];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base_start = 'ا' + r[0] + 'ط';
	elif (r[0] == 'ز') :
		base_start = 'ا' + r[0] + 'د';
	
	base = base_start + r[1] + 'أ';
	base_a = base_start + r[1] + 'آ';
	base_u = base_start + r[1] + 'ؤ';

	forms = thirdhamza_past_actv(base, base_a, base_u, tv);
	if (tv == 'tv') : #{
		base = base_start + r[1] + 'ئ';

		forms.update(thirdhamza_past_pasv(base));
	#}

	return forms;
#}


def thirdhamza_patt8_pres(root, tv): #{
	r = root.split('-'); # radicals

	base_start = r[0] + 'ت';
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base_start = r[0];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base_start = r[0] + 'ط';
	elif (r[0] == 'ز') :
		base_start = r[0] + 'د';

	base = base_start + r[1] + 'ئ';
	base_i = base_start + r[1] + 'ئ';
	base_a = base_start + r[1] + 'ئا';
	base_u = base_start + r[1] + 'ئ';

	forms = thirdhamza_pres_actv(base, base_i, base_a, base_u, tv);
	forms.update(thirdhamza_subjun_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_apocop_actv(base, base_i, base_a, base_u, tv));

	base = 'ا' + base_start + r[1] + 'ئ';
	base_i = 'ا' + base_start + r[1] + 'ئ';
	base_a = 'ا' + base_start + r[1] + 'ئا';
	base_u = 'ا' + base_start + r[1] + 'ئ';

	forms.update(thirdhamza_imp(base, base_i, base_a, base_u, tv));

	if (tv == 'tv') : #{
		base = base_start + r[1];

		forms.update(thirdhamza_subjun_pasv(base));
		forms.update(thirdhamza_pres_pasv(base));
		forms.update(thirdhamza_apocop_pasv(base));
	#}

	return forms;

#}


def thirdhamza_patt8_pprs(root): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ت' + r[1];
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base = r[0] + r[1];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base = r[0] + 'ط' + r[1];
	elif (r[0] == 'ز') :
		base = r[0] + 'د' + r[1];

	return thirdhamza_pprs('م' + base, '-');
#}


def thirdhamza_patt8_pp(root): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ت' + r[1];
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base = r[0] + r[1];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base = r[0] + 'ط' + r[1];
	elif (r[0] == 'ز') :
		base = r[0] + 'د' + r[1];

	return thirdhamza_pp('م' + base, '-');
#}



## ----------------------------------------------------------------------------##
## pattern 10
## ----------------------------------------------------------------------------##


def thirdhamza_patt10_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'است' + r[0] + r[1] + 'أ';
	base_a = 'است' + r[0] + r[1] + 'آ';
	base_u = 'است' + r[0] + r[1] + 'ؤ';

	forms = thirdhamza_past_actv(base, base_a, base_u, tv);
	if (tv == 'tv') : #{
		base = 'است' + r[0] + r[1] + 'ئ';
		forms.update(thirdhamza_past_pasv(base));
	#}

	return forms;
#}


def thirdhamza_patt10_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ست' + r[0] + r[1] + 'ئ';
	base_i = 'ست' + r[0] + r[1] + 'ئ';
	base_a = 'ست' + r[0] + r[1] + 'ئا';
	base_u = 'ست' + r[0] + r[1] + 'ئ';

	forms = thirdhamza_pres_actv(base, base_i, base_a, base_u, tv);
	forms.update(thirdhamza_subjun_actv(base, base_i, base_a, base_u, tv));
	forms.update(thirdhamza_apocop_actv(base, base_i, base_a, base_u, tv));

	base = 'است' + r[0] + r[1] + 'ئ';
	base_i = 'است' + r[0] + r[1] + 'ئ';
	base_a = 'است' + r[0] + r[1] + 'ئا';
	base_u = 'است' + r[0] + r[1] + 'ئ';

	forms.update(thirdhamza_imp(base, base_I, base_a, base_u, tv));

	if (tv == 'tv') : #{

		base = 'ست' + r[0] + r[1];

		forms.update(thirdhamza_subjun_pasv(base));
		forms.update(thirdhamza_pres_pasv(base));
		forms.update(thirdhamza_apocop_pasv(base));
	#}


	return forms;
#}


def thirdhamza_patt10_pprs(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pprs('مست' + r[0] + r[1], '-');
#}


def thirdhamza_patt10_pp(root): #{
	r = root.split('-'); # radicals

	return thirdhamza_pp('مست' + r[0] + r[1], '-');
#}



## ----------------------------------------------------------------------------##
## main
## ----------------------------------------------------------------------------##


def main(stem): #{

	forms = {};

	if stem['theme'] == '1' : #{
		forms.update(thirdhamza_patt1_past(stem['root'], stem['vowels_perf'], stem['trans']));
		forms.update(thirdhamza_patt1_pres(stem['root'], stem['vowels_impf'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(thirdhamza_patt1_pp(stem['root']));
		forms.update(thirdhamza_patt1_pprs(stem['root']));
	#}
	elif stem['theme'] == '2' : #{
		forms.update(thirdhamza_patt2_past(stem['root'], stem['trans']));
		forms.update(thirdhamza_patt2_pres(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(thirdhamza_patt2_pp(stem['root']));
		forms.update(thirdhamza_patt2_pprs(stem['root']));
	#}
	elif stem['theme'] == '3' : #{
		forms.update(thirdhamza_patt3_past(stem['root'], stem['trans']));
		forms.update(thirdhamza_patt3_pres(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(thirdhamza_patt3_pp(stem['root']));
		forms.update(thirdhamza_patt3_pprs(stem['root']));
	#}
	elif stem['theme'] == '4' : #{
		forms.update(thirdhamza_patt4_past(stem['root'], stem['trans']));
		forms.update(thirdhamza_patt4_pres(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(thirdhamza_patt4_pp(stem['root']));
		forms.update(thirdhamza_patt4_pprs(stem['root']));
	#}
	elif stem['theme'] == '5' : #{
		forms.update(thirdhamza_patt5_past(stem['root'], stem['trans']));
		forms.update(thirdhamza_patt5_pres(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(thirdhamza_patt5_pp(stem['root']));
		forms.update(thirdhamza_patt5_pprs(stem['root']));
	#}
	elif stem['theme'] == '6' : #{
		forms.update(thirdhamza_patt6_past(stem['root'], stem['trans']));
		forms.update(thirdhamza_patt6_pres(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(thirdhamza_patt6_pp(stem['root']));
		forms.update(thirdhamza_patt6_pprs(stem['root']));
	#}
	elif stem['theme'] == '7' : #{
		forms.update(thirdhamza_patt7_past(stem['root'], stem['trans']));
		forms.update(thirdhamza_patt7_pres(stem['root'], stem['trans']));
		forms.update(thirdhamza_patt7_pprs(stem['root']));
	#}
	elif stem['theme'] == '8' : #{
		forms.update(thirdhamza_patt8_past(stem['root'], stem['trans']));
		forms.update(thirdhamza_patt8_pres(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(thirdhamza_patt8_pp(stem['root']));
		forms.update(thirdhamza_patt8_pprs(stem['root']));
	#}
	elif stem['theme'] == '10' : #{
		forms.update(thirdhamza_patt10_past(stem['root'], stem['trans']));
		forms.update(thirdhamza_patt10_pres(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(thirdhamza_patt10_pp(stem['root']));
		forms.update(thirdhamza_patt10_pprs(stem['root']));
	#}

	return forms;

#}

