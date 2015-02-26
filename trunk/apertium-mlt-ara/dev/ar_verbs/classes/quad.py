#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-


## ----------------------------------------------------------------------------##
## quad verbs
## ----------------------------------------------------------------------------##


# TODO : add past LR forms like أحبيت
def quad_past_actv(base, base_t, base_n, tv, r): #{

	forms = {};
	paradigm = 'S__فتح/ه';

	if tv == 'iv' : #{
		forms['actv.past.p3.m.sg'] = [(base, '-', r)];
		forms['actv.past.p3.f.sg'] = [(base + 'ت', '-', r)];
		forms['actv.past.p2.m.sg'] = [(base_t + 'ت', '-', r)];
		forms['actv.past.p2.f.sg'] = [(base_t + 'ت', '-', r)];
		forms['actv.past.p1.mf.sg'] = [(base_t + 'ت', '-', r)];

		forms['actv.past.p3.m.du'] = [(base + 'ا', '-', r)];
		forms['actv.past.p3.f.du'] = [(base + 'تا', '-', r)];
		forms['actv.past.p2.mf.du'] = [(base_t + 'تما', '-', r)];

		forms['actv.past.p3.m.pl'] = [(base + 'وا', '-', r)];
		forms['actv.past.p3.f.pl'] = [(base_n + 'ن', '-', r)];
		forms['actv.past.p2.m.pl'] = [(base_t + 'تم', '-', r)];
		forms['actv.past.p2.f.pl'] = [(base_t + 'تن', '-', r)];
		forms['actv.past.p1.mf.pl'] = [(base_n + 'نا', '-', r)];
	#}
	else : #{
		forms['actv.past.p3.m.sg'] = [(base, '-', r), (base, paradigm, r)];
		forms['actv.past.p3.f.sg'] = [(base + 'ت', '-', r), (base + 'ت', paradigm, r)];
		forms['actv.past.p2.m.sg'] = [(base_t + 'ت', '-', r), (base_t + 'ت', paradigm, r)];
		forms['actv.past.p2.f.sg'] = [(base_t + 'ت', '-', r), (base_t + 'ت', paradigm, r)];
		forms['actv.past.p1.mf.sg'] = [(base_t + 'ت', '-', r), (base_t + 'ت', paradigm, r)];

		forms['actv.past.p3.m.du'] = [(base + 'ا', '-', r), (base + 'ا', paradigm, r)];
		forms['actv.past.p3.f.du'] = [(base + 'تا', '-', r), (base + 'تا', paradigm, r)];
		forms['actv.past.p2.mf.du'] = [(base_t + 'تما', '-', r), (base_t + 'تما', paradigm, r)];

		forms['actv.past.p3.m.pl'] = [(base + 'وا', '-', r), (base + 'و', paradigm, r)];
		forms['actv.past.p3.f.pl'] = [(base_n + 'ن', '-', r), (base_n + 'ن', paradigm, r)];
		forms['actv.past.p2.m.pl'] = [(base_t + 'تم', '-', r), (base_t + 'تمو', paradigm, r)];
		forms['actv.past.p2.f.pl'] = [(base_t + 'تن', '-', r), (base_t + 'تن', paradigm, r)];
		forms['actv.past.p1.mf.pl'] = [(base_n + 'نا', '-', r), (base_n + 'نا', paradigm, r)];
	#}

	return forms;
#}


def quad_past_pasv(base, base_t, base_n, r): #{

	forms = {};

	rlbase = base[0: 2] + 'ُ' + base[2: ];
	rlbase_t = base_t[0: 2] + 'ُ' + base_t[2: ];
	rlbase_n = base_n[0: 2] + 'ُ' + base_n[2: ];	


	forms['pasv.past.p3.m.sg'] = [(base, '-', 'LR'), (rlbase, '-', 'RL')];
	forms['pasv.past.p3.f.sg'] = [(base + 'ت', '-', 'LR'), (rlbase + 'ت', '-', 'RL')];
	forms['pasv.past.p2.m.sg'] = [(base_t + 'ت', '-', 'LR'), (rlbase_t + 'ت', '-', 'RL')];
	forms['pasv.past.p2.f.sg'] = [(base_t + 'ت', '-', 'LR'), (rlbase_t + 'ت', '-', 'RL')];
	forms['pasv.past.p1.mf.sg'] = [(base_t + 'ت', '-', 'LR'), (rlbase_t + 'ت', '-', 'RL')];

	forms['pasv.past.p3.m.du'] = [(base + 'ا', '-', 'LR'), (rlbase + 'ا', '-', 'RL')];
	forms['pasv.past.p3.f.du'] = [(base + 'تا', '-', 'LR'), (rlbase + 'تا', '-', 'RL')];
	forms['pasv.past.p2.mf.du'] = [(base_t + 'تما', '-', 'LR'), (rlbase_t + 'تما', '-', 'RL')];

	forms['pasv.past.p3.m.pl'] = [(base + 'وا', '-', 'LR'), (rlbase + 'وا', '-', 'RL')];
	forms['pasv.past.p3.f.pl'] = [(base_n + 'ن', '-', 'LR'), (rlbase_n + 'ن', '-', 'RL')];
	forms['pasv.past.p2.m.pl'] = [(base_t + 'تم', '-', 'LR'), (rlbase_t + 'تم', '-', 'RL')];
	forms['pasv.past.p2.f.pl'] = [(base_t + 'تن', '-', 'LR'), (rlbase_t + 'تن', '-', 'RL')];
	forms['pasv.past.p1.mf.pl'] = [(base_n + 'نا', '-', 'LR'), (rlbase_n + 'نا', '-', 'RL')];

	return forms;
#}


def quad_pres_actv(base, base_n, tv, r): #{

	forms = {};
	paradigm = 'S__فتح/ه';

	if tv == 'iv' : #{
		forms['actv.pres.p3.m.sg'] = [('ي' + base, '-', r)];
		forms['actv.pres.p3.f.sg'] = [('ت' + base, '-', r)];
		forms['actv.pres.p2.m.sg'] = [('ت' + base, '-', r)];
		forms['actv.pres.p2.f.sg'] = [('ت' + base + 'ين', '-', r)];
		forms['actv.pres.p1.mf.sg'] = [('أ' + base, '-', r)];

		forms['actv.pres.p3.m.du'] = [('ي' + base + 'ان', '-', r)];
		forms['actv.pres.p3.f.du'] = [('ت' + base + 'ان', '-', r)];
		forms['actv.pres.p2.mf.du'] = [('ت' + base + 'ان', '-', r)];

		forms['actv.pres.p3.m.pl'] = [('ي' + base + 'ون', '-', r)];
		forms['actv.pres.p3.f.pl'] = [('ي' + base_n + 'ن', '-', r)];
		forms['actv.pres.p2.m.pl'] = [('ت' + base + 'ون', '-', r)];
		forms['actv.pres.p2.f.pl'] = [('ت' + base_n + 'ن', '-', r)];
		forms['actv.pres.p1.mf.pl'] = [('ن' + base, '-', r)];
	#}
	else : #{
		forms['actv.pres.p3.m.sg'] = [('ي' + base, '-', r), ('ي' + base, paradigm, r)];
		forms['actv.pres.p3.f.sg'] = [('ت' + base, '-', r), ('ت' + base, paradigm, r)];
		forms['actv.pres.p2.m.sg'] = [('ت' + base, '-', r), ('ت' + base, paradigm, r)];
		forms['actv.pres.p2.f.sg'] = [('ت' + base + 'ين', '-', r), ('ت' + base + 'ي', paradigm, r)];
		forms['actv.pres.p1.mf.sg'] = [('أ' + base, '-', r), ('أ' + base, paradigm, r)];
	
		forms['actv.pres.p3.m.du'] = [('ي' + base + 'ان', '-', r), ('ي' + base + 'ا', paradigm, r)];
		forms['actv.pres.p3.f.du'] = [('ت' + base + 'ان', '-', r), ('ت' + base + 'ا', paradigm, r)];
		forms['actv.pres.p2.mf.du'] = [('ت' + base + 'ان', '-', r), ('ت' + base + 'ا', paradigm, r)];

		forms['actv.pres.p3.m.pl'] = [('ي' + base + 'ون', '-', r), ('ي' + base + 'و', paradigm, r)];
		forms['actv.pres.p3.f.pl'] = [('ي' + base_n + 'ن', '-', r), ('ي' + base_n + 'ن', paradigm, r)];
		forms['actv.pres.p2.m.pl'] = [('ت' + base + 'ون', '-', r), ('ت' + base + 'و', paradigm, r)];
		forms['actv.pres.p2.f.pl'] = [('ت' + base_n + 'ن', '-', r), ('ت' + base_n + 'ن', paradigm, r)];
		forms['actv.pres.p1.mf.pl'] = [('ن' + base, '-', r), ('ن' + base, paradigm, r)];

	#}

	return forms;
#}


def quad_pres_pasv(base, base_n, r): #{

	forms = {};

	forms['pasv.pres.p3.m.sg'] = [('ي' + base, '-', 'LR'), ('يُ' + base, '-', 'RL')];
	forms['pasv.pres.p3.f.sg'] = [('ت' + base, '-', 'LR'), ('تُ' + base, '-', 'RL')];
	forms['pasv.pres.p2.m.sg'] = [('ت' + base, '-', 'LR'), ('تُ' + base, '-', 'RL')];
	forms['pasv.pres.p2.f.sg'] = [('ت' + base + 'ين', '-', 'LR'), ('تُ' + base + 'ين', '-', 'RL')];
	forms['pasv.pres.p1.mf.sg'] = [('أ' + base, '-', 'LR'), ('اُ' + base, '-', 'RL')];

	forms['pasv.pres.p3.m.du'] = [('ي' + base + 'ان', '-', 'LR'), ('يُ' + base + 'ان', '-', 'RL')];
	forms['pasv.pres.p3.f.du'] = [('ت' + base + 'ان', '-', 'LR'), ('تُ' + base + 'ان', '-', 'RL')];
	forms['pasv.pres.p2.mf.du'] = [('ت' + base + 'ان', '-', 'LR'), ('تُ' + base + 'ان', '-', 'RL')];

	forms['pasv.pres.p3.m.pl'] = [('ي' + base + 'ون', '-', 'LR'), ('يُ' + base + 'ون', '-', 'RL')];
	forms['pasv.pres.p3.f.pl'] = [('ي' + base_n + 'ن', '-', 'LR'), ('يُ' + base_n + 'ن', '-', 'RL')];
	forms['pasv.pres.p2.m.pl'] = [('ت' + base + 'ون', '-', 'LR'), ('تُ' + base + 'ون', '-', 'RL')];
	forms['pasv.pres.p2.f.pl'] = [('ت' + base_n + 'ن', '-', 'LR'), ('تُ' + base_n + 'ن', '-', 'RL')];
	forms['pasv.pres.p1.mf.pl'] = [('ن' + base, '-', 'LR'), ('نُ' + base, '-', 'RL')];

	return forms;
#}



def quad_subjun_actv(base, base_n, tv, r): #{

	forms = {};
	paradigm = 'S__فتح/ه';

	if tv == 'iv' : #{
		forms['actv.subjun.p3.m.sg'] = [('ي' + base, '-', r)];
		forms['actv.subjun.p3.f.sg'] = [('ت' + base, '-', r)];
		forms['actv.subjun.p2.m.sg'] = [('ت' + base, '-', r)];
		forms['actv.subjun.p2.f.sg'] = [('ت' + base + 'ي', '-', r)];
		forms['actv.subjun.p1.mf.sg'] = [('أ' + base, '-', r)];

		forms['actv.subjun.p3.m.du'] = [('ي' + base + 'ا', '-', r)];
		forms['actv.subjun.p3.f.du'] = [('ت' + base + 'ا', '-', r)];
		forms['actv.subjun.p2.mf.du'] = [('ت' + base + 'ا', '-', r)];

		forms['actv.subjun.p3.m.pl'] = [('ي' + base + 'وا', '-', r)];
		forms['actv.subjun.p3.f.pl'] = [('ي' + base_n + 'ن', '-', r)];
		forms['actv.subjun.p2.m.pl'] = [('ت' + base + 'وا', '-', r)];
		forms['actv.subjun.p2.f.pl'] = [('ت' + base_n + 'ن', '-', r)];
		forms['actv.subjun.p1.mf.pl'] = [('ن' + base, '-', r)];
	#}
	else : #{
		forms['actv.subjun.p3.m.sg'] = [('ي' + base, '-', r), ('ي' + base, paradigm, r)];
		forms['actv.subjun.p3.f.sg'] = [('ت' + base, '-', r), ('ت' + base, paradigm, r)];
		forms['actv.subjun.p2.m.sg'] = [('ت' + base, '-', r), ('ت' + base, paradigm, r)];
		forms['actv.subjun.p2.f.sg'] = [('ت' + base + 'ي', '-', r), ('ت' + base + 'ي', paradigm, r)];
		forms['actv.subjun.p1.mf.sg'] = [('أ' + base, '-', r), ('أ' + base, paradigm, r)];
	
		forms['actv.subjun.p3.m.du'] = [('ي' + base + 'ا', '-', r), ('ي' + base + 'ا', paradigm, r)];
		forms['actv.subjun.p3.f.du'] = [('ت' + base + 'ا', '-', r), ('ت' + base + 'ا', paradigm, r)];
		forms['actv.subjun.p2.mf.du'] = [('ت' + base + 'ا', '-', r), ('ت' + base + 'ا', paradigm, r)];

		forms['actv.subjun.p3.m.pl'] = [('ي' + base + 'وا', '-', r), ('ي' + base + 'و', paradigm, r)];
		forms['actv.subjun.p3.f.pl'] = [('ي' + base_n + 'ن', '-', r), ('ي' + base_n + 'ن', paradigm, r)];
		forms['actv.subjun.p2.m.pl'] = [('ت' + base + 'وا', '-', r), ('ت' + base + 'و', paradigm, r)];
		forms['actv.subjun.p2.f.pl'] = [('ت' + base_n + 'ن', '-', r), ('ت' + base_n + 'ن', paradigm, r)];
		forms['actv.subjun.p1.mf.pl'] = [('ن' + base, '-', r), ('ن' + base, paradigm, r)];
	#}

	return forms;
#}


def quad_subjun_pasv(base, base_n, r): #{

	forms = {};

	forms['pasv.subjun.p3.m.sg'] = [('ي' + base, '-', 'LR'), ('يُ' + base, '-', 'RL')];
	forms['pasv.subjun.p3.f.sg'] = [('ت' + base, '-', 'LR'), ('تُ' + base, '-', 'RL')];
	forms['pasv.subjun.p2.m.sg'] = [('ت' + base, '-', 'LR'), ('تُ' + base, '-', 'RL')];
	forms['pasv.subjun.p2.f.sg'] = [('ت' + base + 'ي', '-', 'LR'), ('تُ' + base + 'ي', '-', 'RL')];
	forms['pasv.subjun.p1.mf.sg'] = [('أ' + base, '-', 'LR'), ('اُ' + base, '-', 'RL')];

	forms['pasv.subjun.p3.m.du'] = [('ي' + base + 'ا', '-', 'LR'), ('يُ' + base + 'ا', '-', 'RL')];
	forms['pasv.subjun.p3.f.du'] = [('ت' + base + 'ا', '-', 'LR'), ('تُ' + base + 'ا', '-', 'RL')];
	forms['pasv.subjun.p2.mf.du'] = [('ت' + base + 'ا', '-', 'LR'), ('تُ' + base + 'ا', '-', 'RL')];

	forms['pasv.subjun.p3.m.pl'] = [('ي' + base + 'وا', '-', 'LR'), ('يُ' + base + 'وا', '-', 'RL')];
	forms['pasv.subjun.p3.f.pl'] = [('ي' + base_n + 'ن', '-', 'LR'), ('يُ' + base_n + 'ن', '-', 'RL')];
	forms['pasv.subjun.p2.m.pl'] = [('ت' + base + 'وا', '-', 'LR'), ('تُ' + base + 'وا', '-', 'RL')];
	forms['pasv.subjun.p2.f.pl'] = [('ت' + base_n + 'ن', '-', 'LR'), ('تُ' + base_n + 'ن', '-', 'RL')];
	forms['pasv.subjun.p1.mf.pl'] = [('ن' + base, '-', 'LR'), ('نُ' + base, '-', 'RL')];

	return forms;
#}



# TODO: add LR long forms
def quad_apocop_actv(base, base_n, suftype, tv, r): #{

	forms = {};

	paradigm = 'S__فتح/ه';
	paradigm_cons = 'S__فتح/ه';

	if suftype == 'n' :
		paradigm_cons = 'S__يلون/ه';
	elif suftype == 'k' :
		paradigm_cons = 'S__يترك/ه';
	elif suftype == 'h' :
		paradigm_cons = 'S__يشبه/ه';

	if tv == 'iv' : #{
		forms['actv.apocop.p3.m.sg'] = [('ي' + base, '-', r)];
		forms['actv.apocop.p3.f.sg'] = [('ت' + base, '-', r)];
		forms['actv.apocop.p2.m.sg'] = [('ت' + base, '-', r)];
		forms['actv.apocop.p2.f.sg'] = [('ت' + base + 'ي', '-', r)];
		forms['actv.apocop.p1.mf.sg'] = [('أ' + base, '-', r)];

		forms['actv.apocop.p3.m.du'] = [('ي' + base + 'ا', '-', r)];
		forms['actv.apocop.p3.f.du'] = [('ت' + base + 'ا', '-', r)];
		forms['actv.apocop.p2.mf.du'] = [('ت' + base + 'ا', '-', r)];

		forms['actv.apocop.p3.m.pl'] = [('ي' + base + 'وا', '-', r)];
		forms['actv.apocop.p3.f.pl'] = [('ي' + base_n + 'ن', '-', r)];
		forms['actv.apocop.p2.m.pl'] = [('ت' + base + 'وا', '-', r)];
		forms['actv.apocop.p2.f.pl'] = [('ت' + base_n + 'ن', '-', r)];
		forms['actv.apocop.p1.mf.pl'] = [('ن' + base, '-', r)];
	#}
	else : #{
		forms['actv.apocop.p3.m.sg'] = [('ي' + base, '-', r), ('ي' + base, paradigm_cons, r)];
		forms['actv.apocop.p3.f.sg'] = [('ت' + base, '-', r), ('ت' + base, paradigm_cons, r)];
		forms['actv.apocop.p2.m.sg'] = [('ت' + base, '-', r), ('ت' + base, paradigm_cons, r)];
		forms['actv.apocop.p2.f.sg'] = [('ت' + base + 'ي', '-', r), ('ت' + base + 'ي', paradigm, r)];
		forms['actv.apocop.p1.mf.sg'] = [('أ' + base, '-', r), ('أ' + base, paradigm_cons, r)];
	
		forms['actv.apocop.p3.m.du'] = [('ي' + base + 'ا', '-', r), ('ي' + base + 'ا', paradigm, r)];
		forms['actv.apocop.p3.f.du'] = [('ت' + base + 'ا', '-', r), ('ت' + base + 'ا', paradigm, r)];
		forms['actv.apocop.p2.mf.du'] = [('ت' + base + 'ا', '-', r), ('ت' + base + 'ا', paradigm, r)];

		forms['actv.apocop.p3.m.pl'] = [('ي' + base + 'وا', '-', r), ('ي' + base + 'و', paradigm, r)];
		forms['actv.apocop.p3.f.pl'] = [('ي' + base_n + 'ن', '-', r), ('ي' + base_n + 'ن', paradigm, r)];
		forms['actv.apocop.p2.m.pl'] = [('ت' + base + 'وا', '-', r), ('ت' + base + 'و', paradigm, r)];
		forms['actv.apocop.p2.f.pl'] = [('ت' + base_n + 'ن', '-', r), ('ت' + base_n + 'ن', paradigm, r)];
		forms['actv.apocop.p1.mf.pl'] = [('ن' + base, '-', r), ('ن' + base, paradigm_cons, r)];
	#}

	return forms;
#}


def quad_apocop_pasv(base, base_n, r): #{

	forms = {};

	forms['pasv.apocop.p3.m.sg'] = [('ي' + base, '-', 'LR'), ('يُ' + base, '-', 'RL')];
	forms['pasv.apocop.p3.f.sg'] = [('ت' + base, '-', 'LR'), ('تُ' + base, '-', 'RL')];
	forms['pasv.apocop.p2.m.sg'] = [('ت' + base, '-', 'LR'), ('تُ' + base, '-', 'RL')];
	forms['pasv.apocop.p2.f.sg'] = [('ت' + base + 'ي', '-', 'LR'), ('تُ' + base + 'ي', '-', 'RL')];
	forms['pasv.apocop.p1.mf.sg'] = [('أ' + base, '-', 'LR'), ('اُ' + base, '-', 'RL')];

	forms['pasv.apocop.p3.m.du'] = [('ي' + base + 'ا', '-', 'LR'), ('يُ' + base + 'ا', '-', 'RL')];
	forms['pasv.apocop.p3.f.du'] = [('ت' + base + 'ا', '-', 'LR'), ('تُ' + base + 'ا', '-', 'RL')];
	forms['pasv.apocop.p2.mf.du'] = [('ت' + base + 'ا', '-', 'LR'), ('تُ' + base + 'ا', '-', 'RL')];

	forms['pasv.apocop.p3.m.pl'] = [('ي' + base + 'وا', '-', 'LR'), ('يُ' + base + 'وا', '-', 'RL')];
	forms['pasv.apocop.p3.f.pl'] = [('ي' + base_n + 'ن', '-', 'LR'), ('يُ' + base_n + 'ن', '-', 'RL')];
	forms['pasv.apocop.p2.m.pl'] = [('ت' + base + 'وا', '-', 'LR'), ('تُ' + base + 'وا', '-', 'RL')];
	forms['pasv.apocop.p2.f.pl'] = [('ت' + base_n + 'ن', '-', 'LR'), ('تُ' + base_n + 'ن', '-', 'RL')];
	forms['pasv.apocop.p1.mf.pl'] = [('ن' + base, '-', 'LR'), ('نُ' + base, '-', 'RL')];

	return forms;
#}



# TODO : add LR long forms
def quad_imp(base, base_n, suftype, tv, r): #{

	# passive voice?

	forms = {};

	paradigm = 'S__فتح/ه';
	paradigm_cons = 'S__فتح/ه';

	if suftype == 'n' :
		paradigm_cons = 'S__يلون/ه';
	elif suftype == 'k' :
		paradigm_cons = 'S__يترك/ه';
	elif suftype == 'h' :
		paradigm_cons = 'S__يشبه/ه';


	if tv == 'iv' : #{
		forms['actv.imp.p2.m.sg'] = [(base, '-', r)];
		forms['actv.imp.p2.f.sg'] = [(base + 'ي', '-', r)];
		forms['actv.imp.p2.mf.du'] = [(base + 'ا', '-', r)];
		forms['actv.imp.p2.m.pl'] = [(base + 'وا', '-', r)];
		forms['actv.imp.p2.f.pl'] = [(base_n + 'ن', '-', r)];
	#}
	else : #{
		forms['actv.imp.p2.m.sg'] = [(base, '-', r), (base, paradigm_cons, r)];
		forms['actv.imp.p2.f.sg'] = [(base + 'ي', '-', r), (base + 'ي', paradigm, r)];
		forms['actv.imp.p2.mf.du'] = [(base + 'ا', '-', r), (base + 'ا', paradigm, r)];
		forms['actv.imp.p2.m.pl'] = [(base + 'وا', '-', r), (base + 'و', paradigm, r)];
		forms['actv.imp.p2.f.pl'] = [(base_n + 'ن', '-', r), (base_n + 'ن', paradigm, r)];
	#}

	return forms ; 
#}


def quad_pprs(base, r): #{

        forms = {};

	forms['pprs.m.sg'] = [(base, 'S__بيت/', r)] ;
	forms['pprs.f.sg'] = [(base, 'S__كلم/ة', r)] ;
	forms['pprs.m.pl'] = [(base, 'S__مهندس/ون', r)] ;
	forms['pprs.f.pl'] = [(base + 'ات', 'S__كلمات/', r)] ;

	return forms;
#}


def quad_pp(base, r): #{

        forms = {};

	forms['pp.m.sg'] = [(base, 'S__بيت/', r)] ;
	forms['pp.f.sg'] = [(base, 'S__كلم/ة', r)] ;
	forms['pp.m.pl'] = [(base, 'S__مهندس/ون', r)] ;
	forms['pp.f.pl'] = [(base + 'ات', 'S__كلمات/', r)] ;

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 1
## ----------------------------------------------------------------------------##


def quad_patt1_past(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + r[1] + r[2] + r[3];

	if r[3] == 'ت' :
		base_t = r[0] + r[1] + r[2];
	else :
		base_t = r[0] + r[1] + r[2] + r[3];

	if r[3] == 'ن' :
		base_n = r[0] + r[1] + r[2];
	else :
		base_n = r[0] + r[1] + r[2] + r[3];

	forms = quad_past_actv(base, base_t, base_n, tv, '-');
	if (tv == 'tv') :
		forms.update(quad_past_pasv(base, base_t, base_n, '-'));

	return forms;
#}



def quad_patt1_impf(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + r[1] + r[2] + r[3];

	if r[3] == 'ن' :
		base_n = r[0] + r[1] + r[2];
	else :
		base_n = r[0] + r[1] + r[2] + r[3];


        suftype = '-';
        if r[3] == 'ن' :
                suftype = 'n';
        elif r[3] == 'ك' :
                suftype = 'k';
        elif r[3] == 'ه' : 
                suftype = 'h'

	forms = quad_pres_actv(base, base_n, tv, '-');
	forms.update(quad_subjun_actv(base, base_n, tv, '-'));
	forms.update(quad_apocop_actv(base, base_n, suftype, tv, '-'));
	forms.update(quad_imp(base, base_n, suftype, tv, '-')); 
	if (tv == 'tv') : #{
		forms.update(quad_pres_pasv(base, base_n, '-'));
		forms.update(quad_subjun_pasv(base, base_n, '-'));
		forms.update(quad_apocop_pasv(base, base_n, '-'));
	#}
	
	return forms;
#}


def quad_patt1_part(root, tv): #{
	r = root.split('-'); # radicals

	base = 'م' + r[0] + r[1] + r[2] + r[3];

	forms = quad_pprs(base, '-');
	if tv == 'tv' :
		forms.update(quad_pp(base, '-'));

	return forms;
#}


## ----------------------------------------------------------------------------##
## main
## ----------------------------------------------------------------------------##


def main(stem): #{

	forms = {};

	if stem['theme'] == '1' : #{
		forms.update(quad_patt1_past(stem['root'], stem['trans']));
		forms.update(quad_patt1_impf(stem['root'], stem['trans']));
		forms.update(quad_patt1_part(stem['root'], stem['trans']));
	#}

	return forms;

#}

