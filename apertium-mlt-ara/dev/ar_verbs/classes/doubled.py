#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-


## ----------------------------------------------------------------------------##
## doubled verbs
## ----------------------------------------------------------------------------##


# TODO : add past LR forms like أحبيت
def doubled_past_actv(base, base_t, base_n, tv, r): #{

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


def doubled_past_pasv(base, base_t, base_n, r): #{

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


def doubled_pres_actv(base, base_n, tv, r): #{

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


def doubled_pres_pasv(base, base_n, r): #{

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



def doubled_subjun_actv(base, base_n, tv, r): #{

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


def doubled_subjun_pasv(base, base_n, r): #{

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
def doubled_apocop_actv(base, base_n, suftype, tv, r): #{

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


def doubled_apocop_pasv(base, base_n, r): #{

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
def doubled_imp(base, base_n, suftype, tv, r): #{

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


def doubled_pprs(base, r): #{

        forms = {};

	forms['pprs.m.sg'] = [(base, 'S__بيت/', r)] ;
	forms['pprs.f.sg'] = [(base, 'S__كلم/ة', r)] ;
	forms['pprs.m.pl'] = [(base, 'S__مهندس/ون', r)] ;
	forms['pprs.f.pl'] = [(base + 'ات', 'S__كلمات/', r)] ;

	return forms;
#}


def doubled_pp(base, r): #{

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


def doubled_patt1_past(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + r[1];

	if r[1] == 'ت' :
		base_t = r[0] + r[1];
	else :
		base_t = r[0] + r[1] + r[1];

	if r[1] == 'ن' :
		base_n = r[0] + r[1];
	else :
		base_n = r[0] + r[1] + r[1];

	forms = doubled_past_actv(base, base_t, base_n, tv, '-');
	if (tv == 'tv') :
		forms.update(doubled_past_pasv(base, base_t, base_n, '-'));

	return forms;
#}



def doubled_patt1_impf(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + r[1];

	if r[1] == 'ن' :
		base_n = r[0] + r[1];
	else :
		base_n = r[0] + r[1] + r[1];


# for long apocop & imp forms
#        suftype = '-';
#        if r[1] == 'ن' :
#                suftype = 'n';
#        elif r[1] == 'ك' :
#                suftype = 'k';
#        elif r[1] == 'ه' : 
#                suftype = 'h'

	forms = doubled_pres_actv(base, base_n, tv, '-');
	forms.update(doubled_subjun_actv(base, base_n, tv, '-'));
	forms.update(doubled_apocop_actv(base, base_n, '-', tv, '-'));
	if (tv == 'tv') : #{
		forms.update(doubled_pres_pasv(base, base_n, '-'));
		forms.update(doubled_subjun_pasv(base, base_n, '-'));
		forms.update(doubled_apocop_pasv(base, base_n, '-'));
	#}

	base = 'ا' + base;
	base_n = 'ا' + base_n;

	forms.update(doubled_imp(base, base_n, '-', tv, '-')); 
	
	return forms;
#}


def doubled_patt1_part(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ا' + r[1];

	forms = doubled_pprs(base, '-');
	if tv == 'tv' :
		forms.update(doubled_pp(base, '-'));

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 2
## ----------------------------------------------------------------------------##


def doubled_patt2_past(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + r[1] + r[1];

	if r[1] == 'ت' :
		base_t = r[0] + r[1];
	else :
		base_t = r[0] + r[1] + r[1];

	if r[1] == 'ن' :
		base_n = r[0] + r[1];
	else :
		base_n = r[0] + r[1] + r[1];

	forms = doubled_past_actv(base, base_t, base_n, tv, '-');
	if (tv == 'tv') :
		forms.update(doubled_past_pasv(base, base_t, base_n, '-'));

	return forms;
#}


def doubled_patt2_impf(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + r[1] + r[1];

	if r[1] == 'ن' :
		base_n = r[0] + r[1];
	else :
		base_n = r[0] + r[1] + r[1];


# for long apocop & imp forms
#        suftype = '-';
#        if r[1] == 'ن' :
#                suftype = 'n';
#        elif r[1] == 'ك' :
#                suftype = 'k';
#        elif r[1] == 'ه' : 
#                suftype = 'h'

	forms = doubled_pres_actv(base, base_n, tv, '-');
	forms.update(doubled_subjun_actv(base, base_n, tv, '-'));
	forms.update(doubled_apocop_actv(base, base_n, '-', tv, '-'));
	forms.update(doubled_imp(base, base_n, '-', tv, '-')); 
	if (tv == 'tv') : #{
		forms.update(doubled_pres_pasv(base, base_n, '-'));
		forms.update(doubled_subjun_pasv(base, base_n, '-'));
		forms.update(doubled_apocop_pasv(base, base_n, '-'));
	#}
	
	return forms;
#}



def doubled_patt2_part(root, tv): #{
	r = root.split('-'); # radicals

	base = 'م' + r[0] + r[1] + r[1];

	forms = doubled_pprs(base, '-');
	if tv == 'tv' :
		forms.update(doubled_pp(base, '-'));

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 3
## ----------------------------------------------------------------------------##


# TODO : add long patt 3 verbs (as subtype)
def doubled_patt3_past(root, subtype, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ا' + r[1];

	if r[1] == 'ت' :
		base_t = r[0] + 'ا' + r[1];
	else :
		base_t = r[0] + 'ا' + r[1] + r[1];

	if r[1] == 'ن' :
		base_n = r[0] + 'ا' + r[1];
	else :
		base_n = r[0] + 'ا' + r[1] + r[1];

	forms = doubled_past_actv(base, base_t, base_n, tv, '-');
	if (tv == 'tv') : #{
		# always long
		base = r[0] + 'و' + r[1] + r[1];

		if r[1] == 'ت' :
			base_t = r[0] + 'و' + r[1];
		else :
			base_t = r[0] + 'و' + r[1] + r[1];

		if r[1] == 'ن' :
			base_n = r[0] + 'و' + r[1];
		else :
			base_n = r[0] + 'و' + r[1] + r[1];

		forms.update(doubled_past_pasv(base, base_t, base_n, '-'));
	#}

	return forms;
#}


def doubled_patt3_impf(root, subtype, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ا' + r[1];

	if r[1] == 'ن' :
		base_n = r[0] + 'ا' + r[1];
	else :
		base_n = r[0] + 'ا' + r[1] + r[1];


# for long apocop & imp forms
#        suftype = '-';
#        if r[1] == 'ن' :
#                suftype = 'n';
#        elif r[1] == 'ك' :
#                suftype = 'k';
#        elif r[1] == 'ه' : 
#                suftype = 'h'

	forms = doubled_pres_actv(base, base_n, tv, '-');
	forms.update(doubled_subjun_actv(base, base_n, tv, '-'));
	forms.update(doubled_apocop_actv(base, base_n, '-', tv, '-'));
	forms.update(doubled_imp(base, base_n, '-', tv, '-')); 
	if (tv == 'tv') : #{
		forms.update(doubled_pres_pasv(base, base_n, '-'));
		forms.update(doubled_subjun_pasv(base, base_n, '-'));
		forms.update(doubled_apocop_pasv(base, base_n, '-'));
	#}
	
	return forms;
#}




# TODO subtype
def doubled_patt3_part(root, subtype, tv): #{
	r = root.split('-'); # radicals

	base = 'م' + r[0] + 'ا' + r[1];

	forms = doubled_pprs(base, '-');
	if tv == 'tv' :
		forms.update(doubled_pp(base, '-'));

	return forms;
#}


## ----------------------------------------------------------------------------##
## pattern 4
## ----------------------------------------------------------------------------##


def doubled_patt4_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'أ' + r[0] + r[1];

	if r[1] == 'ت' :
		base_t = 'أ' + r[0] + r[1];
	else :
		base_t = 'أ' + r[0] + r[1] + r[1];

	if r[1] == 'ن' :
		base_n = 'أ' + r[0] + r[1];
	else :
		base_n = 'أ' + r[0] + r[1] + r[1];

	forms = doubled_past_actv(base, base_t, base_n, tv, '-');
	if (tv == 'tv') : #{
		forms.update(doubled_past_pasv(base, base_t, base_n, '-'));
	#}

	return forms;
#}


def doubled_patt4_impf(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + r[1];

	if r[1] == 'ن' :
		base_n = r[0] + r[1];
	else :
		base_n = r[0] + r[1] + r[1];


# for long apocop & imp forms
#        suftype = '-';
#        if r[1] == 'ن' :
#                suftype = 'n';
#        elif r[1] == 'ك' :
#                suftype = 'k';
#        elif r[1] == 'ه' : 
#                suftype = 'h'

	forms = doubled_pres_actv(base, base_n, tv, '-');
	forms.update(doubled_subjun_actv(base, base_n, tv, '-'));
	forms.update(doubled_apocop_actv(base, base_n, '-', tv, '-'));
	if (tv == 'tv') : #{
		forms.update(doubled_pres_pasv(base, base_n, '-'));
		forms.update(doubled_subjun_pasv(base, base_n, '-'));
		forms.update(doubled_apocop_pasv(base, base_n, '-'));
	#}

	base = 'أ' + base;
	base_n = 'أ' + base_n;
	forms.update(doubled_imp(base, base_n, '-', tv, '-')); 

	return forms;
#}


def doubled_patt4_part(root, tv): #{
	r = root.split('-'); # radicals

	base = 'م' + r[0] + r[1];

	forms = doubled_pprs(base, '-');
	if tv == 'tv' :
		forms.update(doubled_pp(base, '-'));

	return forms;
#}


## ----------------------------------------------------------------------------##
## pattern 5
## ----------------------------------------------------------------------------##


def doubled_patt5_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + r[1] + r[1];

	if r[1] == 'ت' :
		base_t = 'ت' + r[0] + r[1];
	else :
		base_t = 'ت' + r[0] + r[1] + r[1];

	if r[1] == 'ن' :
		base_n = 'ت' + r[0] + r[1];
	else :
		base_n = 'ت' + r[0] + r[1] + r[1];

	forms = doubled_past_actv(base, base_t, base_n, tv, '-');
	if (tv == 'tv') : #{
		forms.update(doubled_past_pasv(base, base_t, base_n, '-'));
	#}

	return forms;
#}


def doubled_patt5_impf(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + r[1] + r[1];

	if r[1] == 'ن' :
		base_n = 'ت' + r[0] + r[1];
	else :
		base_n = 'ت' + r[0] + r[1] + r[1];


# for long apocop & imp forms
#        suftype = '-';
#        if r[1] == 'ن' :
#                suftype = 'n';
#        elif r[1] == 'ك' :
#                suftype = 'k';
#        elif r[1] == 'ه' : 
#                suftype = 'h'

	forms = doubled_pres_actv(base, base_n, tv, '-');
	forms.update(doubled_subjun_actv(base, base_n, tv, '-'));
	forms.update(doubled_apocop_actv(base, base_n, '-', tv, '-'));
	forms.update(doubled_imp(base, base_n, '-', tv, '-')); 
	if (tv == 'tv') : #{
		forms.update(doubled_pres_pasv(base, base_n, '-'));
		forms.update(doubled_subjun_pasv(base, base_n, '-'));
		forms.update(doubled_apocop_pasv(base, base_n, '-'));
	#}

	return forms;
#}



def doubled_patt5_part(root, tv): #{
	r = root.split('-'); # radicals

	base = 'مت' + r[0] + r[1] + r[1];

	forms = doubled_pprs(base, '-');
	if tv == 'tv' :
		forms.update(doubled_pp(base, '-'));

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 6
## ----------------------------------------------------------------------------##


# TODO : add long patt 6 verbs (as subtype)
def doubled_patt6_past(root, subtype, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + 'ا' + r[1];

	if r[1] == 'ت' :
		base_t = 'ت' + r[0] + 'ا' + r[1];
	else :
		base_t = 'ت' + r[0] + 'ا' + r[1] + r[1];

	if r[1] == 'ن' :
		base_n = 'ت' + r[0] + 'ا' + r[1];
	else :
		base_n = 'ت' + r[0] + 'ا' + r[1] + r[1];

	forms = doubled_past_actv(base, base_t, base_n, tv, '-');
	if (tv == 'tv') : #{
		# always long
		base = 'ت' + r[0] + 'و' + r[1] + r[1];

		if r[1] == 'ت' :
			base_t = 'ت' + r[0] + 'و' + r[1];
		else :
			base_t = 'ت' + r[0] + 'و' + r[1] + r[1];

		if r[1] == 'ن' :
			base_n = 'ت' + r[0] + 'و' + r[1];
		else :
			base_n = 'ت' + r[0] + 'و' + r[1] + r[1];

		forms.update(doubled_past_pasv(base, base_t, base_n, '-'));
	#}

	return forms;
#}


# TODO subtype
def doubled_patt6_impf(root, subtype, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + 'ا' + r[1];

	if r[1] == 'ن' :
		base_n = 'ت' + r[0] + 'ا' + r[1];
	else :
		base_n = 'ت' + r[0] + 'ا' + r[1] + r[1];


# for long apocop & imp forms
#        suftype = '-';
#        if r[1] == 'ن' :
#                suftype = 'n';
#        elif r[1] == 'ك' :
#                suftype = 'k';
#        elif r[1] == 'ه' : 
#                suftype = 'h'

	forms = doubled_pres_actv(base, base_n, tv, '-');
	forms.update(doubled_subjun_actv(base, base_n, tv, '-'));
	forms.update(doubled_apocop_actv(base, base_n, '-', tv, '-'));
	forms.update(doubled_imp(base, base_n, '-', tv, '-')); 
	if (tv == 'tv') : #{
		forms.update(doubled_pres_pasv(base, base_n, '-'));
		forms.update(doubled_subjun_pasv(base, base_n, '-'));
		forms.update(doubled_apocop_pasv(base, base_n, '-'));
	#}

	return forms;
#}


# TODO subtype
def doubled_patt6_part(root, subtype, tv): #{
	r = root.split('-'); # radicals

	base = 'مت' + r[0] + 'ا' + r[1];

	forms = doubled_pprs(base, '-');
	if tv == 'tv' :
		forms.update(doubled_pp(base, '-'));

	return forms;
#}


## ----------------------------------------------------------------------------##
## pattern 7
## ----------------------------------------------------------------------------##


def doubled_patt7_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ان' + r[0] + r[1];

	if r[1] == 'ت' :
		base_t = 'ان' + r[0] + r[1];
	else :
		base_t = 'ان' + r[0] + r[1] + r[1];

	if r[1] == 'ن' :
		base_n = 'ان' + r[0] + r[1];
	else :
		base_n = 'ان' + r[0] + r[1] + r[1];

	return doubled_past_actv(base, base_t, base_n, tv, '-');

	return forms;
#}


def doubled_patt7_impf(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ن' + r[0] + r[1];

	if r[1] == 'ن' :
		base_n = 'ن' + r[0] + r[1];
	else :
		base_n = 'ن' + r[0] + r[1] + r[1];


# for long apocop & imp forms
#        suftype = '-';
#        if r[1] == 'ن' :
#                suftype = 'n';
#        elif r[1] == 'ك' :
#                suftype = 'k';
#        elif r[1] == 'ه' : 
#                suftype = 'h'

	forms = doubled_pres_actv(base, base_n, tv, '-');
	forms.update(doubled_subjun_actv(base, base_n, tv, '-'));
	forms.update(doubled_apocop_actv(base, base_n, '-', tv, '-'));
#	if (tv == 'tv') : #{
#		forms.update(doubled_pres_pasv(base, base_n, '-'));
#		forms.update(doubled_subjun_pasv(base, base_n, '-'));
#		forms.update(doubled_apocop_pasv(base, base_n, '-'));
#	#}

	base = 'ا' + base;
	base_n = 'ا' + base_n;
	forms.update(doubled_imp(base, base_n, '-', tv, '-')); 


	return forms;
#}



def doubled_patt7_part(root, tv): #{
	r = root.split('-'); # radicals

	base =  'من' + r[0] + r[1];

	forms = doubled_pprs(base, '-');
#	if tv == 'tv' :
#		forms.update(doubled_pp(base, '-'));

	return forms;
#}


## ----------------------------------------------------------------------------##
## pattern 8
## ----------------------------------------------------------------------------##


# TODO : subtypes for different types of assimilation? 
def doubled_patt8_past(root, subtype, tv): #{
	r = root.split('-'); # radicals

	base_start = 'ا' + r[0] + 'ت';
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base_start = 'ا' + r[0];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base_start = 'ا' + r[0] + 'ط';
	elif (r[0] == 'ز') :
		base_start = 'ا' + r[0] + 'د';
	
	base = base_start + r[1];

	if r[1] == 'ت' :
		base_t = base_start + r[1];
	else :
		base_t = base_start + r[1] + r[1];

	if r[1] == 'ن' :
		base_n = base_start + r[1];
	else :
		base_n = base_start + r[1] + r[1];


	forms = doubled_past_actv(base, base_t, base_n, tv, '-');
	if (tv == 'tv') : #{
		forms.update(doubled_past_pasv(base, base_t, base_n, '-'));
	#}

	return forms;
#}


def doubled_patt8_impf(root, subtype, tv): #{
	r = root.split('-'); # radicals

	base_start = r[0] + 'ت';
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base_start = r[0];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base_start = r[0] + 'ط';
	elif (r[0] == 'ز') :
		base_start = r[0] + 'د';
	
	base = base_start + r[1];

	if r[1] == 'ن' :
		base_n = base_start + r[1];
	else :
		base_n = base_start + r[1] + r[1];


# for long apocop & imp forms
#        suftype = '-';
#        if r[1] == 'ن' :
#                suftype = 'n';
#        elif r[1] == 'ك' :
#                suftype = 'k';
#        elif r[1] == 'ه' : 
#                suftype = 'h'

	forms = doubled_pres_actv(base, base_n, tv, '-');
	forms.update(doubled_subjun_actv(base, base_n, tv, '-'));
	forms.update(doubled_apocop_actv(base, base_n, '-', tv, '-'));
	if (tv == 'tv') : #{
		forms.update(doubled_pres_pasv(base, base_n, '-'));
		forms.update(doubled_subjun_pasv(base, base_n, '-'));
		forms.update(doubled_apocop_pasv(base, base_n, '-'));
	#}

	base = 'ا' + base;
	base_n = 'ا' + base_n;
	forms.update(doubled_imp(base, base_n, '-', tv, '-')); 

	return forms;
#}



# TODO subtype
def doubled_patt8_part(root, subtype, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ت' + r[1] + r[2];
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base = r[0] + r[1] + r[2];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base = r[0] + 'ط' + r[1] + r[2];
	elif (r[0] == 'ز') :
		base = r[0] + 'د' + r[1] + r[2];

	base =  'م' + base + r[1];

	forms = doubled_pprs(base, '-');
	if tv == 'tv' :
		forms.update(doubled_pp(base, '-'));

	return forms;
#}


## ----------------------------------------------------------------------------##
## pattern 10
## ----------------------------------------------------------------------------##


def doubled_patt10_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'است' + r[0] + r[1];

	if r[1] == 'ت' :
		base_t = 'است' + r[0] + r[1];
	else :
		base_t = 'است' + r[0] + r[1] + r[1];

	if r[1] == 'ن' :
		base_n = 'است' + r[0] + r[1];
	else :
		base_n = 'است' + r[0] + r[1] + r[1];

	forms = doubled_past_actv(base, base_t, base_n, tv, '-');
	if (tv == 'tv') : #{
		forms.update(doubled_past_pasv(base, base_t, base_n, '-'));
	#}

	return forms;
#}


def doubled_patt10_impf(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ست' + r[0] + r[1];

	if r[1] == 'ن' :
		base_n = 'ست' + r[0] + r[1];
	else :
		base_n = 'ست' + r[0] + r[1] + r[1];


# for long apocop & imp forms
#        suftype = '-';
#        if r[1] == 'ن' :
#                suftype = 'n';
#        elif r[1] == 'ك' :
#                suftype = 'k';
#        elif r[1] == 'ه' : 
#                suftype = 'h'

	forms = doubled_pres_actv(base, base_n, tv, '-');
	forms.update(doubled_subjun_actv(base, base_n, tv, '-'));
	forms.update(doubled_apocop_actv(base, base_n, '-', tv, '-'));
	if (tv == 'tv') : #{
		forms.update(doubled_pres_pasv(base, base_n, '-'));
		forms.update(doubled_subjun_pasv(base, base_n, '-'));
		forms.update(doubled_apocop_pasv(base, base_n, '-'));
	#}

	base = 'ا' + base;
	base_n = 'ا' + base_n;
	forms.update(doubled_imp(base, base_n, '-', tv, '-')); 

	return forms;
#}


def doubled_patt10_part(root, tv): #{
	r = root.split('-'); # radicals

	base =  'مست' + r[0] + r[1];

	forms = doubled_pprs(base, '-');
	if tv == 'tv' :
		forms.update(doubled_pp(base, '-'));

	return forms;
#}


## ----------------------------------------------------------------------------##
## main
## ----------------------------------------------------------------------------##


def main(stem): #{

	forms = {};

	if stem['theme'] == '1' : #{
		forms.update(doubled_patt1_past(stem['root'], stem['trans']));
		forms.update(doubled_patt1_impf(stem['root'], stem['trans']));
		forms.update(doubled_patt1_part(stem['root'], stem['trans']));
	#}
	elif stem['theme'] == '2' : #{
		forms.update(doubled_patt2_past(stem['root'], stem['trans']));
		forms.update(doubled_patt2_impf(stem['root'], stem['trans']));
		forms.update(doubled_patt2_part(stem['root'], stem['trans']));
	#}
	elif stem['theme'] == '3' : #{
		forms.update(doubled_patt3_past(stem['root'], stem['subtype'], stem['trans']));
		forms.update(doubled_patt3_impf(stem['root'], stem['subtype'], stem['trans']));
		forms.update(doubled_patt3_part(stem['root'], stem['subtype'], stem['trans']));
	#}
	elif stem['theme'] == '4' : #{
		forms.update(doubled_patt4_past(stem['root'], stem['trans']));
		forms.update(doubled_patt4_impf(stem['root'], stem['trans']));
		forms.update(doubled_patt4_part(stem['root'], stem['trans']));
	#}
	elif stem['theme'] == '5' : #{
		forms.update(doubled_patt5_past(stem['root'], stem['trans']));
		forms.update(doubled_patt5_impf(stem['root'], stem['trans']));
		forms.update(doubled_patt5_part(stem['root'], stem['trans']));
	#}
	elif stem['theme'] == '6' : #{
		forms.update(doubled_patt6_past(stem['root'], stem['subtype'], stem['trans']));
		forms.update(doubled_patt6_impf(stem['root'], stem['subtype'], stem['trans']));
		forms.update(doubled_patt6_part(stem['root'], stem['subtype'], stem['trans']));
	#}
	elif stem['theme'] == '7' : #{
		forms.update(doubled_patt7_past(stem['root'], stem['trans']));
		forms.update(doubled_patt7_impf(stem['root'], stem['trans']));
		forms.update(doubled_patt7_part(stem['root'], stem['trans']));
	#}
	elif stem['theme'] == '8' : #{
		forms.update(doubled_patt8_past(stem['root'], stem['subtype'], stem['trans']));
		forms.update(doubled_patt8_impf(stem['root'], stem['subtype'], stem['trans']));
		forms.update(doubled_patt8_part(stem['root'], stem['subtype'], stem['trans']));
	#}
	elif stem['theme'] == '10' : #{
		forms.update(doubled_patt10_past(stem['root'], stem['trans']));
		forms.update(doubled_patt10_impf(stem['root'], stem['trans']));
		forms.update(doubled_patt10_part(stem['root'], stem['trans']));
	#}

	return forms;

#}

