#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-


## ----------------------------------------------------------------------------##
## hollow verbs:
## weak second root consontant: و (w) or ي (y)
## ----------------------------------------------------------------------------##


def hollow_past_actv(base, base_t, base_n, tv): #{

	forms = {};

	if tv == 'iv' : #{
		forms['actv.past.p3.m.sg'] = [(base, '-', '-')];
		forms['actv.past.p3.f.sg'] = [(base + 'ت', '-', '-')];
		forms['actv.past.p2.m.sg'] = [(base_t + 'ت', '-', '-')];
		forms['actv.past.p2.f.sg'] = [(base_t + 'ت', '-', '-')];
		forms['actv.past.p1.mf.sg'] = [(base_t + 'ت', '-', '-')];

		forms['actv.past.p3.m.du'] = [(base + 'ا', '-', '-')];
		forms['actv.past.p3.f.du'] = [(base + 'تا', '-', '-')];
		forms['actv.past.p2.mf.du'] = [(base_t + 'تما', '-', '-')];

		forms['actv.past.p3.m.pl'] = [(base + 'وا', '-', '-')];
		forms['actv.past.p3.f.pl'] = [(base_n + 'ن', '-', '-')];
		forms['actv.past.p2.m.pl'] = [(base_t + 'تم', '-', '-')];
		forms['actv.past.p2.f.pl'] = [(base_t + 'تن', '-', '-')];
		forms['actv.past.p1.mf.pl'] = [(base_n + 'نا', '-', '-')];
	#}
	else : #{
		forms['actv.past.p3.m.sg'] = [(base, '-', '-'), (base, 'S__فتح/ه', '-')];
		forms['actv.past.p3.f.sg'] = [(base + 'ت', '-', '-'), (base + 'ت', 'S__فتح/ه', '-')];
		forms['actv.past.p2.m.sg'] = [(base_t + 'ت', '-', '-'), (base_t + 'ت', 'S__فتح/ه', '-')];
		forms['actv.past.p2.f.sg'] = [(base_t + 'ت', '-', '-'), (base_t + 'ت', 'S__فتح/ه', '-')];
		forms['actv.past.p1.mf.sg'] = [(base_t + 'ت', '-', '-'), (base_t + 'ت', 'S__فتح/ه', '-')];

		forms['actv.past.p3.m.du'] = [(base + 'ا', '-', '-'), (base + 'ا', 'S__فتح/ه', '-')];
		forms['actv.past.p3.f.du'] = [(base + 'تا', '-', '-'), (base + 'تا', 'S__فتح/ه', '-')];
		forms['actv.past.p2.mf.du'] = [(base_t + 'تما', '-', '-'), (base_t + 'تما', 'S__فتح/ه', '-')];

		forms['actv.past.p3.m.pl'] = [(base + 'وا', '-', '-'), (base + 'و', 'S__فتح/ه', '-')];
		forms['actv.past.p3.f.pl'] = [(base_n + 'ن', '-', '-'), (base_n + 'ن', 'S__فتح/ه', '-')];
		forms['actv.past.p2.m.pl'] = [(base_t + 'تم', '-', '-'), (base_t + 'تمو', 'S__فتح/ه', '-')];
		forms['actv.past.p2.f.pl'] = [(base_t + 'تن', '-', '-'), (base_t + 'تن', 'S__فتح/ه', '-')];
		forms['actv.past.p1.mf.pl'] = [(base_n + 'نا', '-', '-'), (base_n + 'نا', 'S__فتح/ه', '-')];
	#}

	return forms;
#}


def hollow_past_pasv(base, base_t, base_n): #{

	forms = {};

	forms['pasv.past.p3.m.sg'] = [(base, '-', '-')];
	forms['pasv.past.p3.f.sg'] = [(base + 'ت', '-', '-')];
	forms['pasv.past.p2.m.sg'] = [(base_t + 'ت', '-', '-')];
	forms['pasv.past.p2.f.sg'] = [(base_t + 'ت', '-', '-')];
	forms['pasv.past.p1.mf.sg'] = [(base_t + 'ت', '-', '-')];
	forms['pasv.past.p3.m.du'] = [(base + 'ا', '-', '-')];
	forms['pasv.past.p3.f.du'] = [(base + 'تا', '-', '-')];
	forms['pasv.past.p2.mf.du'] = [(base_t + 'تما', '-', '-')];

	forms['pasv.past.p3.m.pl'] = [(base + 'وا', '-', '-')];
	forms['pasv.past.p3.f.pl'] = [(base_n + 'ن', '-', '-')];
	forms['pasv.past.p2.m.pl'] = [(base_t + 'تم', '-', '-')];
	forms['pasv.past.p2.f.pl'] = [(base_t + 'تن', '-', '-')];
	forms['pasv.past.p1.mf.pl'] = [(base_n + 'نا', '-', '-')];

	return forms;
#}


def hollow_pres_actv(base, base_n, tv): #{

	forms = {};

	if tv == 'iv' : #{
		forms['actv.pres.p3.m.sg'] = [('ي' + base, '-', '-')];
		forms['actv.pres.p3.f.sg'] = [('ت' + base, '-', '-')];
		forms['actv.pres.p2.m.sg'] = [('ت' + base, '-', '-')];
		forms['actv.pres.p2.f.sg'] = [('ت' + base + 'ين', '-', '-')];
		forms['actv.pres.p1.mf.sg'] = [('أ' + base, '-', '-')];

		forms['actv.pres.p3.m.du'] = [('ي' + base + 'ان', '-', '-')];
		forms['actv.pres.p3.f.du'] = [('ت' + base + 'ان', '-', '-')];
		forms['actv.pres.p2.mf.du'] = [('ت' + base + 'ان', '-', '-')];

		forms['actv.pres.p3.m.pl'] = [('ي' + base + 'ون', '-', '-')];
		forms['actv.pres.p3.f.pl'] = [('ي' + base_n + 'ن', '-', '-')];
		forms['actv.pres.p2.m.pl'] = [('ت' + base + 'ون', '-', '-')];
		forms['actv.pres.p2.f.pl'] = [('ت' + base_n + 'ن', '-', '-')];
		forms['actv.pres.p1.mf.pl'] = [('ن' + base, '-', '-')];
	#}
	else : #{
		forms['actv.pres.p3.m.sg'] = [('ي' + base, '-', '-'), ('ي' + base, 'S__فتح/ه', '-')];
		forms['actv.pres.p3.f.sg'] = [('ت' + base, '-', '-'), ('ت' + base, 'S__فتح/ه', '-')];
		forms['actv.pres.p2.m.sg'] = [('ت' + base, '-', '-'), ('ت' + base, 'S__فتح/ه', '-')];
		forms['actv.pres.p2.f.sg'] = [('ت' + base + 'ين', '-', '-'), ('ت' + base + 'ي', 'S__فتح/ه', '-')];
		forms['actv.pres.p1.mf.sg'] = [('أ' + base, '-', '-'), ('أ' + base, 'S__فتح/ه', '-')];
	
		forms['actv.pres.p3.m.du'] = [('ي' + base + 'ان', '-', '-'), ('ي' + base + 'ا', 'S__فتح/ه', '-')];
		forms['actv.pres.p3.f.du'] = [('ت' + base + 'ان', '-', '-'), ('ت' + base + 'ا', 'S__فتح/ه', '-')];
		forms['actv.pres.p2.mf.du'] = [('ت' + base + 'ان', '-', '-'), ('ت' + base + 'ا', 'S__فتح/ه', '-')];

		forms['actv.pres.p3.m.pl'] = [('ي' + base + 'ون', '-', '-'), ('ي' + base + 'و', 'S__فتح/ه', '-')];
		forms['actv.pres.p3.f.pl'] = [('ي' + base_n + 'ن', '-', '-'), ('ي' + base_n + 'ن', 'S__فتح/ه', '-')];
		forms['actv.pres.p2.m.pl'] = [('ت' + base + 'ون', '-', '-'), ('ت' + base + 'و', 'S__فتح/ه', '-')];
		forms['actv.pres.p2.f.pl'] = [('ت' + base_n + 'ن', '-', '-'), ('ت' + base_n + 'ن', 'S__فتح/ه', '-')];
		forms['actv.pres.p1.mf.pl'] = [('ن' + base, '-', '-'), ('ن' + base, 'S__فتح/ه', '-')];

	#}

	return forms;
#}



def hollow_pres_pasv(base, base_n): #{

	forms = {};

	forms['pasv.pres.p3.m.sg'] = [('ي' + base, '-', '-')];
	forms['pasv.pres.p3.f.sg'] = [('ت' + base, '-', '-')];
	forms['pasv.pres.p2.m.sg'] = [('ت' + base, '-', '-')];
	forms['pasv.pres.p2.f.sg'] = [('ت' + base + 'ين', '-', '-')];
	forms['pasv.pres.p1.mf.sg'] = [('أ' + base, '-', '-')];

	forms['pasv.pres.p3.m.du'] = [('ي' + base + 'ان', '-', '-')];
	forms['pasv.pres.p3.f.du'] = [('ت' + base + 'ان', '-', '-')];
	forms['pasv.pres.p2.mf.du'] = [('ت' + base + 'ان', '-', '-')];

	forms['pasv.pres.p3.m.pl'] = [('ي' + base + 'ون', '-', '-')];
	forms['pasv.pres.p3.f.pl'] = [('ي' + base_n + 'ن', '-', '-')];
	forms['pasv.pres.p2.m.pl'] = [('ت' + base + 'ون', '-', '-')];
	forms['pasv.pres.p2.f.pl'] = [('ت' + base_n + 'ن', '-', '-')];
	forms['pasv.pres.p1.mf.pl'] = [('ن' + base, '-', '-')];

	return forms;
#}



def hollow_subjun_actv(base, base_n, tv): #{

	forms = {};

	if tv == 'iv' : #{
		forms['actv.subjun.p3.m.sg'] = [('ي' + base, '-', '-')];
		forms['actv.subjun.p3.f.sg'] = [('ت' + base, '-', '-')];
		forms['actv.subjun.p2.m.sg'] = [('ت' + base, '-', '-')];
		forms['actv.subjun.p2.f.sg'] = [('ت' + base + 'ي', '-', '-')];
		forms['actv.subjun.p1.mf.sg'] = [('أ' + base, '-', '-')];

		forms['actv.subjun.p3.m.du'] = [('ي' + base + 'ا', '-', '-')];
		forms['actv.subjun.p3.f.du'] = [('ت' + base + 'ا', '-', '-')];
		forms['actv.subjun.p2.mf.du'] = [('ت' + base + 'ا', '-', '-')];

		forms['actv.subjun.p3.m.pl'] = [('ي' + base + 'وا', '-', '-')];
		forms['actv.subjun.p3.f.pl'] = [('ي' + base_n + 'ن', '-', '-')];
		forms['actv.subjun.p2.m.pl'] = [('ت' + base + 'وا', '-', '-')];
		forms['actv.subjun.p2.f.pl'] = [('ت' + base_n + 'ن', '-', '-')];
		forms['actv.subjun.p1.mf.pl'] = [('ن' + base, '-', '-')];
	#}
	else : #{
		forms['actv.subjun.p3.m.sg'] = [('ي' + base, '-', '-'), ('ي' + base, 'S__فتح/ه', '-')];
		forms['actv.subjun.p3.f.sg'] = [('ت' + base, '-', '-'), ('ت' + base, 'S__فتح/ه', '-')];
		forms['actv.subjun.p2.m.sg'] = [('ت' + base, '-', '-'), ('ت' + base, 'S__فتح/ه', '-')];
		forms['actv.subjun.p2.f.sg'] = [('ت' + base + 'ي', '-', '-'), ('ت' + base + 'ي', 'S__فتح/ه', '-')];
		forms['actv.subjun.p1.mf.sg'] = [('أ' + base, '-', '-'), ('أ' + base, 'S__فتح/ه', '-')];
	
		forms['actv.subjun.p3.m.du'] = [('ي' + base + 'ا', '-', '-'), ('ي' + base + 'ا', 'S__فتح/ه', '-')];
		forms['actv.subjun.p3.f.du'] = [('ت' + base + 'ا', '-', '-'), ('ت' + base + 'ا', 'S__فتح/ه', '-')];
		forms['actv.subjun.p2.mf.du'] = [('ت' + base + 'ا', '-', '-'), ('ت' + base + 'ا', 'S__فتح/ه', '-')];

		forms['actv.subjun.p3.m.pl'] = [('ي' + base + 'وا', '-', '-'), ('ي' + base + 'و', 'S__فتح/ه', '-')];
		forms['actv.subjun.p3.f.pl'] = [('ي' + base_n + 'ن', '-', '-'), ('ي' + base_n + 'ن', 'S__فتح/ه', '-')];
		forms['actv.subjun.p2.m.pl'] = [('ت' + base + 'وا', '-', '-'), ('ت' + base + 'و', 'S__فتح/ه', '-')];
		forms['actv.subjun.p2.f.pl'] = [('ت' + base_n + 'ن', '-', '-'), ('ت' + base_n + 'ن', 'S__فتح/ه', '-')];
		forms['actv.subjun.p1.mf.pl'] = [('ن' + base, '-', '-'), ('ن' + base, 'S__فتح/ه', '-')];
	#}

	return forms;
#}


def hollow_subjun_pasv(base, base_n): #{

	forms = {};

	forms['pasv.subjun.p3.m.sg'] = [('ي' + base, '-', '-')];
	forms['pasv.subjun.p3.f.sg'] = [('ت' + base, '-', '-')];
	forms['pasv.subjun.p2.m.sg'] = [('ت' + base, '-', '-')];
	forms['pasv.subjun.p2.f.sg'] = [('ت' + base + 'ي', '-', '-')];
	forms['pasv.subjun.p1.mf.sg'] = [('أ' + base, '-', '-')];

	forms['pasv.subjun.p3.m.du'] = [('ي' + base + 'ا', '-', '-')];
	forms['pasv.subjun.p3.f.du'] = [('ت' + base + 'ا', '-', '-')];
	forms['pasv.subjun.p2.mf.du'] = [('ت' + base + 'ا', '-', '-')];

	forms['pasv.subjun.p3.m.pl'] = [('ي' + base + 'وا', '-', '-')];
	forms['pasv.subjun.p3.f.pl'] = [('ي' + base_n + 'ن', '-', '-')];
	forms['pasv.subjun.p2.m.pl'] = [('ت' + base + 'وا', '-', '-')];
	forms['pasv.subjun.p2.f.pl'] = [('ت' + base_n + 'ن', '-', '-')];
	forms['pasv.subjun.p1.mf.pl'] = [('ن' + base, '-', '-')];

	return forms;
#}


def hollow_apocop_actv(base_short, base_long, base_n, tv): #{

	forms = {};

	if tv == 'iv' : #{
		forms['actv.apocop.p3.m.sg'] = [('ي' + base_short, '-', '-')];
		forms['actv.apocop.p3.f.sg'] = [('ت' + base_short, '-', '-')];
		forms['actv.apocop.p2.m.sg'] = [('ت' + base_short, '-', '-')];
		forms['actv.apocop.p2.f.sg'] = [('ت' + base_long + 'ي', '-', '-')];
		forms['actv.apocop.p1.mf.sg'] = [('أ' + base_short, '-', '-')];

		forms['actv.apocop.p3.m.du'] = [('ي' + base_long + 'ا', '-', '-')];
		forms['actv.apocop.p3.f.du'] = [('ت' + base_long + 'ا', '-', '-')];
		forms['actv.apocop.p2.mf.du'] = [('ت' + base_long + 'ا', '-', '-')];

		forms['actv.apocop.p3.m.pl'] = [('ي' + base_long + 'وا', '-', '-')];
		forms['actv.apocop.p3.f.pl'] = [('ي' + base_n + 'ن', '-', '-')];
		forms['actv.apocop.p2.m.pl'] = [('ت' + base_long + 'وا', '-', '-')];
		forms['actv.apocop.p2.f.pl'] = [('ت' + base_n + 'ن', '-', '-')];
		forms['actv.apocop.p1.mf.pl'] = [('ن' + base_short, '-', '-')];
	#}
	else : #{
		forms['actv.apocop.p3.m.sg'] = [('ي' + base_short, '-', '-'), ('ي' + base_short, 'S__فتح/ه', '-')];
		forms['actv.apocop.p3.f.sg'] = [('ت' + base_short, '-', '-'), ('ت' + base_short, 'S__فتح/ه', '-')];
		forms['actv.apocop.p2.m.sg'] = [('ت' + base_short, '-', '-'), ('ت' + base_short, 'S__فتح/ه', '-')];
		forms['actv.apocop.p2.f.sg'] = [('ت' + base_long + 'ي', '-', '-'), ('ت' + base_long + 'ي', 'S__فتح/ه', '-')];
		forms['actv.apocop.p1.mf.sg'] = [('أ' + base_short, '-', '-'), ('أ' + base_short, 'S__فتح/ه', '-')];
	
		forms['actv.apocop.p3.m.du'] = [('ي' + base_long + 'ا', '-', '-'), ('ي' + base_long + 'ا', 'S__فتح/ه', '-')];
		forms['actv.apocop.p3.f.du'] = [('ت' + base_long + 'ا', '-', '-'), ('ت' + base_long + 'ا', 'S__فتح/ه', '-')];
		forms['actv.apocop.p2.mf.du'] = [('ت' + base_long + 'ا', '-', '-'), ('ت' + base_long + 'ا', 'S__فتح/ه', '-')];

		forms['actv.apocop.p3.m.pl'] = [('ي' + base_long + 'وا', '-', '-'), ('ي' + base_long + 'و', 'S__فتح/ه', '-')];
		forms['actv.apocop.p3.f.pl'] = [('ي' + base_n + 'ن', '-', '-'), ('ي' + base_n + 'ن', 'S__فتح/ه', '-')];
		forms['actv.apocop.p2.m.pl'] = [('ت' + base_long + 'وا', '-', '-'), ('ت' + base_long + 'و', 'S__فتح/ه', '-')];
		forms['actv.apocop.p2.f.pl'] = [('ت' + base_n + 'ن', '-', '-'), ('ت' + base_n + 'ن', 'S__فتح/ه', '-')];
		forms['actv.apocop.p1.mf.pl'] = [('ن' + base_short, '-', '-'), ('ن' + base_short, 'S__فتح/ه', '-')];
	#}

	return forms;
#}


def hollow_apocop_pasv(base_short, base_long, base_n): #{

	forms = {};

	forms['pasv.apocop.p3.m.sg'] = [('ي' + base_short, '-', '-')];
	forms['pasv.apocop.p3.f.sg'] = [('ت' + base_short, '-', '-')];
	forms['pasv.apocop.p2.m.sg'] = [('ت' + base_short, '-', '-')];
	forms['pasv.apocop.p2.f.sg'] = [('ت' + base_long + 'ي', '-', '-')];
	forms['pasv.apocop.p1.mf.sg'] = [('أ' + base_short, '-', '-')];

	forms['pasv.apocop.p3.m.du'] = [('ي' + base_long + 'ا', '-', '-')];
	forms['pasv.apocop.p3.f.du'] = [('ت' + base_long + 'ا', '-', '-')];
	forms['pasv.apocop.p2.mf.du'] = [('ت' + base_long + 'ا', '-', '-')];

	forms['pasv.apocop.p3.m.pl'] = [('ي' + base_long + 'وا', '-', '-')];
	forms['pasv.apocop.p3.f.pl'] = [('ي' + base_n + 'ن', '-', '-')];
	forms['pasv.apocop.p2.m.pl'] = [('ت' + base_long + 'وا', '-', '-')];
	forms['pasv.apocop.p2.f.pl'] = [('ت' + base_n + 'ن', '-', '-')];
	forms['pasv.apocop.p1.mf.pl'] = [('ن' + base_short, '-', '-')];

	return forms;
#}


def hollow_imp(base_short, base_long, base_n, tv): #{

	# passive voice?

	forms = {};

	if tv == 'iv' : #{
		forms['actv.imp.p2.m.sg'] = [(base_short, '-', '-')];
		forms['actv.imp.p2.f.sg'] = [(base_long + 'ي', '-', '-')];
		forms['actv.imp.p2.mf.du'] = [(base_long + 'ا', '-', '-')];
		forms['actv.imp.p2.m.pl'] = [(base_long + 'وا', '-', '-')];
		forms['actv.imp.p2.f.pl'] = [(base_n + 'ن', '-', '-')];
	#}
	else : #{
		forms['actv.imp.p2.m.sg'] = [(base_short, '-', '-'), (base_short, 'S__فتح/ه', '-')];
		forms['actv.imp.p2.f.sg'] = [(base_long + 'ي', '-', '-'), (base_long + 'ي', 'S__فتح/ه', '-')];
		forms['actv.imp.p2.mf.du'] = [(base_long + 'ا', '-', '-'), (base_long + 'ا', 'S__فتح/ه', '-')];
		forms['actv.imp.p2.m.pl'] = [(base_long + 'وا', '-', '-'), (base_long + 'و', 'S__فتح/ه', '-')];
		forms['actv.imp.p2.f.pl'] = [(base_n + 'ن', '-', '-'), (base_n + 'ن', 'S__فتح/ه', '-')];
	#}

	return forms ; 
#}


def hollow_pprs(base, r): #{

        forms = {};

	forms['pprs.m.sg'] = [(base, 'S__بيت/', r)] ;
	forms['pprs.f.sg'] = [(base, 'S__كلم/ة', r)] ;
	forms['pprs.m.pl'] = [(base, 'S__مهندس/ون', r)] ;
	forms['pprs.f.pl'] = [(base + 'ات', 'S__كلمات/', r)] ;

	return forms;
#}


def hollow_pp(base, r): #{

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


def hollow_patt1_past(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ا' + r[2];

	if r[2] == 'ت' :
		base_t = r[0];
	else :
		base_t = r[0] + r[2];

	if r[2] == 'ن' :
		base_n = r[0];
	else :
		base_n = r[0] + r[2];

	forms = hollow_past_actv(base, base_t, base_n, tv);
	if (tv == 'tv') : #{
		base = r[0] + 'ي' + r[2];
		forms.update(hollow_past_pasv(base, base_t, base_n));
	#}

	return forms;
#}


def hollow_patt1_pres(root, subtype, tv): #{
	r = root.split('-'); # radicals

	pres_vowel = subtype;
 
	base = r[0] + pres_vowel + r[2];
	base_short = r[0] + r[2];

	if r[2] == 'ن' :
		base_n = r[0];
	else :
		base_n = r[0] + r[2];


	forms = hollow_pres_actv(base, base_n, tv);
	forms.update(hollow_subjun_actv(base, base_n, tv));
	forms.update(hollow_apocop_actv(base_short, base, base_n, tv));
	forms.update(hollow_imp(base_short, base, base_n, tv)); 

	if (tv == 'tv') : #{
		base = r[0] + 'ا' + r[2];
		forms.update(hollow_pres_pasv(base, base_n));
		forms.update(hollow_subjun_pasv(base, base_n));
		forms.update(hollow_apocop_pasv(base_short, base, base_n));
	#}

	return forms;
#}


def hollow_patt1_pprs(root): #{
	r = root.split('-'); # radicals

	return hollow_pprs(r[0] + 'ائ' + r[2], '-');
#}


def hollow_patt1_pp(root): #{
	r = root.split('-'); # radicals

	return hollow_pp('م' + r[0] + 'و' + r[2], '-');
#}



## ----------------------------------------------------------------------------##
## pattern 2
## ----------------------------------------------------------------------------##


def hollow_patt2_past(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + r[1] + r[2];

	if r[2] == 'ت' :
		base_t = r[0] + r[1];
	else :
		base_t = r[0] + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = r[0] + r[1];
	else :
		base_n = r[0] + r[1] + r[2];

	forms = hollow_past_actv(base, base_t, base_n, tv);
	if (tv == 'tv') :
		forms.update(hollow_past_pasv(base, base_t, base_n));

	return forms;
#}


def hollow_patt2_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = r[0] + r[1];
	else :
		base_n = r[0] + r[1] + r[2];

	forms = hollow_pres_actv(base, base_n, tv);
	forms.update(hollow_subjun_actv(base, base_n, tv));
	forms.update(hollow_apocop_actv(base, base, base_n, tv));
	forms.update(hollow_imp(base, base, base_n, tv)); 
	if (tv == 'tv') :
		forms.update(hollow_pres_pasv(base, base_n));
		forms.update(hollow_subjun_pasv(base, base_n));
		forms.update(hollow_apocop_pasv(base, base, base_n));

	return forms;
#}


def hollow_patt2_pp(root): #{
	r = root.split('-'); # radicals

	return hollow_pp('م' + r[0] + r[1] + r[2], '-');
#}


def hollow_patt2_pprs(root): #{
	r = root.split('-'); # radicals

	return hollow_pprs('م' + r[0] + r[1] + r[2], '-');
#}


## ----------------------------------------------------------------------------##
## pattern 3
## ----------------------------------------------------------------------------##


def hollow_patt3_past(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ا' + r[1] + r[2];

	if r[2] == 'ت' :
		base_t = r[0] + 'ا' + r[1];
	else :
		base_t = r[0] + 'ا' + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = r[0] + 'ا' + r[1];
	else :
		base_n = r[0] + 'ا' + r[1] + r[2];

	forms = hollow_past_actv(base, base_t, base_n, tv);
	if (tv == 'tv') : #{
		base = r[0] + 'و' + r[1] + r[2];

		if r[2] == 'ت' :
			base_t = r[0] + 'و' + r[1];
		else :
			base_t = r[0] + 'و' + r[1] + r[2];

		if r[2] == 'ن' :
			base_n = r[0] + 'و' + r[1];
		else :
			base_n = r[0] + 'و' + r[1] + r[2];

		forms.update(hollow_past_pasv(base, base_t, base_n));
	#}

	return forms;
#}


def hollow_patt3_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ا' + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = r[0] + 'ا' + r[1];
	else :
		base_n = r[0] + 'ا' + r[1] + r[2];

	forms = hollow_pres_actv(base, base_n, tv);
	forms.update(hollow_subjun_actv(base, base_n, tv));
	forms.update(hollow_apocop_actv(base, base, base_n, tv));
	forms.update(hollow_imp(base, base, base_n, tv)); 
	if (tv == 'tv') : #{
		forms.update(hollow_pres_pasv(base, base_n));
		forms.update(hollow_subjun_pasv(base, base_n));
		forms.update(hollow_apocop_pasv(base, base, base_n));
	#}

	return forms;
#}


def hollow_patt3_pp(root): #{
	r = root.split('-'); # radicals

	return hollow_pp('م' + r[0] + 'ا' + r[1] + r[2], '-');
#}


def hollow_patt3_pprs(root): #{
	r = root.split('-'); # radicals

	return hollow_pprs('م' + r[0] + 'ا' + r[1] + r[2], '-');
#}


## ----------------------------------------------------------------------------##
## pattern 4
## ----------------------------------------------------------------------------##


def hollow_patt4_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'أ' + r[0] + 'ا' + r[2];

	if r[2] == 'ت' :
		base_t = 'أ' + r[0];
	else :
		base_t = 'أ' + r[0] + r[2];

	if r[2] == 'ن' :
		base_n = 'أ' + r[0];
	else :
		base_n = 'أ' + r[0] + r[2];

	forms = hollow_past_actv(base, base_t, base_n, tv);
	if (tv == 'tv') : #{
		base = 'أ' + r[0] + 'ي' + r[2];
		forms.update(hollow_past_pasv(base, base_t, base_n));
	#}

	return forms;
#}


def hollow_patt4_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ي' + r[2];

	if r[2] == 'ن' :
		base_n = r[0];
	else :
		base_n = r[0] + r[2];

	forms = hollow_pres_actv(base, base_n, tv);
	if (tv == 'tv') : #{
		base = r[0] + 'ا' + r[2];
		forms.update(hollow_pres_pasv(base, base_n));
	#}

	return forms;
#}


def hollow_patt4_subjun(root, tv): #{
	r = root.split('-'); # radicals

	base = r[0] + 'ي' + r[2];

	if r[2] == 'ن' :
		base_n = r[0];
	else :
		base_n = r[0] + r[2];

	forms = hollow_subjun_actv(base, base_n, tv);
	if (tv == 'tv') : #{
		base = r[0] + 'ا' + r[2];
		forms.update(hollow_subjun_pasv(base, base_n));
	#}

	return forms;
#}


def hollow_patt4_apocop(root, tv): #{
	r = root.split('-'); # radicals

	base_short = r[0] + r[2];
	base_long = r[0] + 'ي' + r[2];

	if r[2] == 'ن' :
		base_n = r[0];
	else :
		base_n = r[0] + r[2];

	forms = hollow_apocop_actv(base_short, base_long, base_n, tv);
	if (tv == 'tv') : #{
		base_long = r[0] + 'ا' + r[2];
		forms.update(hollow_apocop_pasv(base_short, base_long, base_n));
	#}

	return forms;
#}


def hollow_patt4_imp(root, tv): #{
	r = root.split('-'); # radicals

	base_short = 'أ' + r[0] + r[2];
	base_long = 'أ' + r[0] + 'ي' + r[2];

	if r[2] == 'ن' :
		base_n = 'أ' + r[0];
	else :
		base_n = 'أ' + r[0] + r[2];

	return hollow_imp(base_short, base_long, base_n, tv); 
#}


def hollow_patt4_pp(root): #{
	r = root.split('-'); # radicals

	return hollow_pp('م' + r[0] + 'ي' + r[2], '-');
#}


def hollow_patt4_pprs(root): #{
	r = root.split('-'); # radicals

	return hollow_pprs('م' + r[0] + 'ا' + r[2], '-');
#}


## ----------------------------------------------------------------------------##
## pattern 5
## ----------------------------------------------------------------------------##


def hollow_patt5_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + r[1] + r[2];

	if r[2] == 'ت' :
		base_t = 'ت' + r[0] + r[1];
	else :
		base_t = 'ت' + r[0] + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = 'ت' + r[0] + r[1];
	else :
		base_n = 'ت' + r[0] + r[1] + r[2];

	forms = hollow_past_actv(base, base_t, base_n, tv);
	if (tv == 'tv') : #{
		forms.update(hollow_past_pasv(base, base_t, base_n));
	#}

	return forms;
#}


def hollow_patt5_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = 'ت' + r[0] + r[1];
	else :
		base_n = 'ت' + r[0] + r[1] + r[2];

	forms = hollow_pres_actv(base, base_n, tv);
	if (tv == 'tv') : #{
		forms.update(hollow_pres_pasv(base, base_n));
	#}

	return forms;
#}


def hollow_patt5_subjun(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = 'ت' + r[0] + r[1];
	else :
		base_n = 'ت' + r[0] + r[1] + r[2];

	forms = hollow_subjun_actv(base, base_n, tv);
	if (tv == 'tv') : #{
		forms.update(hollow_subjun_pasv(base, base_n));
	#}

	return forms;
#}


def hollow_patt5_apocop(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = 'ت' + r[0] + r[1];
	else :
		base_n = 'ت' + r[0] + r[1] + r[2];

	forms = hollow_subjun_actv(base, base, base_n, tv);
	if (tv == 'tv') : #{
		forms.update(hollow_subjun_pasv(base, base, base_n));
	#}

	return forms;
#}


def hollow_patt5_imp(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = 'ت' + r[0] + r[1];
	else :
		base_n = 'ت' + r[0] + r[1] + r[2];

	return hollow_imp(base, base, base_n, tv); 
#}


def hollow_patt5_pp(root): #{
	r = root.split('-'); # radicals

	return hollow_pp('مت' + r[0] + r[1] + r[2], '-');
#}


# rare
def hollow_patt5_pprs(root): #{
	r = root.split('-'); # radicals

	return hollow_pprs('مت' + r[0] + r[1] + r[2], '-');
#}


## ----------------------------------------------------------------------------##
## pattern 6
## ----------------------------------------------------------------------------##


def hollow_patt6_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + 'ا' + r[1] + r[2];

	if r[2] == 'ت' :
		base_t = 'ت' + r[0] + 'ا' + r[1];
	else :
		base_t = 'ت' + r[0] + 'ا' + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = 'ت' + r[0] + 'ا' + r[1];
	else :
		base_n = 'ت' + r[0] + 'ا' + r[1] + r[2];

	forms = hollow_past_actv(base, base_t, base_n, tv);
	if (tv == 'tv') : #{
		base = 'ت' + r[0] + 'و' + r[1] + r[2];

		if r[2] == 'ت' :
			base_t = 'ت' + r[0] + 'و' + r[1];
		else :
			base_t = 'ت' + r[0] + 'و' + r[1] + r[2];

		if r[2] == 'ن' :
			base_n = 'ت' + r[0] + 'و' + r[1];
		else :
			base_n = 'ت' + r[0] + 'و' + r[1] + r[2];

		forms.update(hollow_past_pasv(base, base_t, base_n));
	#}

	return forms;
#}


def hollow_patt6_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + 'ا' + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = 'ت' + r[0] + 'ا' + r[1];
	else :
		base_n = 'ت' + r[0] + 'ا' + r[1] + r[2];

	forms = hollow_pres_actv(base, base_n, tv);
	if (tv == 'tv') : #{
		forms.update(hollow_pres_pasv(base, base_n));
	#}

	return forms;
#}


def hollow_patt6_subjun(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + 'ا' + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = 'ت' + r[0] + 'ا' + r[1];
	else :
		base_n = 'ت' + r[0] + 'ا' + r[1] + r[2];

	forms = hollow_subjun_actv(base, base_n, tv);
	if (tv == 'tv') : #{
		forms.update(hollow_subjun_pasv(base, base_n));
	#}

	return forms;
#}


def hollow_patt6_apocop(root, tv): #{
	r = root.split('-'); # radicals

	forms = {};

	base = 'ت' + r[0] + 'ا' + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = 'ت' + r[0] + 'ا' + r[1];
	else :
		base_n = 'ت' + r[0] + 'ا' + r[1] + r[2];

	forms = hollow_apocop_actv(base, base, base_n, tv);
	if (tv == 'tv') : #{
		forms.update(hollow_apocop_pasv(base, base, base_n));
	#}

	return forms;
#}


def hollow_patt6_imp(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ت' + r[0] + 'ا' + r[1] + r[2];

	if r[2] == 'ن' :
		base_n = 'ت' + r[0] + 'ا' + r[1];
	else :
		base_n = 'ت' + r[0] + 'ا' + r[1] + r[2];

	return hollow_imp(base, base, base_n, tv); 
#}


def hollow_patt6_pp(root): #{
	r = root.split('-'); # radicals

	return hollow_pp('مت' + r[0] + 'ا' + r[1] + r[2], '-');
#}


def hollow_patt6_pprs(root): #{
	r = root.split('-'); # radicals

	return hollow_pprs('مت' + r[0] + 'ا' + r[1] + r[2], '-');
#}


## ----------------------------------------------------------------------------##
## pattern 7
## ----------------------------------------------------------------------------##


def hollow_patt7_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ان' + r[0] + 'ا' + r[2];

	if r[2] == 'ت' :
		base_t = 'ان' + r[0];
	else :
		base_t = 'ان' + r[0] + r[2];

	if r[2] == 'ن' :
		base_n = 'ان' + r[0];
	else :
		base_n = 'ان' + r[0] + r[2];

	return hollow_past_actv(base, base_t, base_n, tv);

	return forms;
#}


def hollow_patt7_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ن' + r[0] + 'ا' + r[2];

	if r[2] == 'ن' :
		base_n = 'ن' + r[0];
	else :
		base_n = 'ن' + r[0] + r[2];


	return hollow_pres_actv(base, base_n, tv);
#}


def hollow_patt7_subjun(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ن' + r[0] + 'ا' + r[2];

	if r[2] == 'ن' :
		base_n = 'ن' + r[0];
	else :
		base_n = 'ن' + r[0] + r[2];

	return hollow_subjun_actv(base, base_n, tv);
#}


def hollow_patt7_apocop(root, tv): #{
	r = root.split('-'); # radicals

	base_short = 'ن' + r[0] + r[2];
	base_long = 'ن' + r[0] + 'ا' + r[2];

	if r[2] == 'ن' :
		base_n = 'ن' + r[0] + r[1];
	else :
		base_n = 'ن' + r[0] + r[1] + r[2];

	return hollow_apocop_actv(base_short, base_long, base_n, tv);
#}


def hollow_patt7_imp(root, tv): #{
	r = root.split('-'); # radicals

	base_short = 'ن' + r[0] + r[2];
	base_long = 'ن' + r[0] + 'ا' + r[2];

	if r[2] == 'ن' :
		base_n = 'ان' + r[0] + r[1];
	else :
		base_n = 'ان' + r[0] + r[1] + r[2];

	return hollow_imp(base_short, base_long, base_n, tv); 
#}


def hollow_patt7_pprs(root): #{
	r = root.split('-'); # radicals

	return hollow_pprs('من' + r[0] + 'ا' + r[2], '-');
#}


## ----------------------------------------------------------------------------##
## pattern 8
## ----------------------------------------------------------------------------##


def hollow_patt8_past(root, tv): #{
	r = root.split('-'); # radicals

	base_start = 'ا' + r[0] + 'ت';
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base_start = 'ا' + r[0];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base_start = 'ا' + r[0] + 'ط';
	elif (r[0] == 'ز') :
		base_start = 'ا' + r[0] + 'د';
	
	base = base_start + 'ا' + r[2];

	if r[2] == 'ت' :
		base_t = base_start;
	else :
		base_t = base_start + r[2];

	if r[2] == 'ن' :
		base_n = base_start;
	else :
		base_n = base_start + r[2];


	forms = hollow_past_actv(base, base_t, base_n, tv);
	if (tv == 'tv') : #{
		forms.update(hollow_past_pasv(base, base_t, base_n));
	#}

	return forms;
#}


def hollow_patt8_pres(root, tv): #{
	r = root.split('-'); # radicals

	base_start = r[0] + 'ت';
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base_start = r[0];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base_start = r[0] + 'ط';
	elif (r[0] == 'ز') :
		base_start = r[0] + 'د';
	
	base = base_start + 'ا' + r[2];

	if r[2] == 'ن' :
		base_n = base_start;
	else :
		base_n = base_start + r[2];

	forms = hollow_pres_actv(base, base_n, tv);
	if (tv == 'tv') : #{
		forms.update(hollow_pres_pasv(base, base_n));
	#}

	return forms;
#}


def hollow_patt8_subjun(root, tv): #{
	r = root.split('-'); # radicals

	base_start = r[0] + 'ت';
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base_start = r[0];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base_start = r[0] + 'ط';
	elif (r[0] == 'ز') :
		base_start = r[0] + 'د';
	
	base = base_start + 'ا' + r[2];

	if r[2] == 'ن' :
		base_n = base_start + r[1];
	else :
		base_n = base_start + r[1] + r[2];

	forms = hollow_subjun_actv(base, base_n, tv);
	if (tv == 'tv') : #{
		forms.update(hollow_subjun_pasv(base, base_n));
	#}

	return forms;
#}


def hollow_patt8_apocop(root, tv): #{
	r = root.split('-'); # radicals

	base_start = r[0] + 'ت';
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base_start = r[0];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base_start = r[0] + 'ط';
	elif (r[0] == 'ز') :
		base_start = r[0] + 'د';
	
	base_short = base_start + r[2];
	base_long = base_start + 'ا' + r[2];

	if r[2] == 'ن' :
		base_n = base_start + r[1];
	else :
		base_n = base_start + r[1] + r[2];

	forms = hollow_apocop_actv(base_short, base_long, base_n, tv);
	if (tv == 'tv') : #{
		forms.update(hollow_apocop_pasv(base_short, base_long, base_n));
	#}

	return forms;
#}


def hollow_patt8_imp(root, tv): #{
	r = root.split('-'); # radicals

	base_start = 'ا' + r[0] + 'ت';
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base_start = 'ا' + r[0];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base_start = 'ا' + r[0] + 'ط';
	elif (r[0] == 'ز') :
		base_start = 'ا' + r[0] + 'د';

	base_short = base_start + r[2];
	base_long = base_start + 'ا' + r[2];

	if r[2] == 'ن' :
		base_n = base_start + r[1];
	else :
		base_n = base_start + r[1] + r[2];

	return hollow_imp(base_short, base_long, base_n, tv); 
#}


def hollow_patt8_pp(root): #{
	r = root.split('-'); # radicals

	base = 'م' + r[0] + 'ت' + 'ا' + r[2];
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base = 'م' + r[0] + 'ا' + r[2];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base = 'م' + r[0] + 'ط' + 'ا' + r[2];
	elif (r[0] == 'ز') :
		base = 'م' + r[0] + 'د' + 'ا' + r[2];

	return hollow_pp(base, '-');
#}


def hollow_patt8_pprs(root): #{
	r = root.split('-'); # radicals

	base = 'م' + r[0] + 'ت' + 'ا' + r[2];
	# with ذ assimilation may be partial (د)
	if (r[0] == 'ط') or (r[0] == 'د') or (r[0] == 'ذ') or (r[0] == 'ظ') :
		base = 'م' + r[0] + 'ا' + r[2];
	elif (r[0] == 'ص') or (r[0] == 'ض') :
		base = 'م' + r[0] + 'ط' + 'ا' + r[2];
	elif (r[0] == 'ز') :
		base = 'م' + r[0] + 'د' + 'ا' + r[2];

	return hollow_pprs(base, '-');
#}



## ----------------------------------------------------------------------------##
## pattern 10
## ----------------------------------------------------------------------------##


def hollow_patt10_past(root, tv): #{
	r = root.split('-'); # radicals

	base = 'است' + r[0] + 'ا' + r[2];

	if r[2] == 'ت' :
		base_t = 'است' + r[0];
	else :
		base_t = 'است' + r[0] + r[2];

	if r[2] == 'ن' :
		base_n = 'است' + r[0];
	else :
		base_n = 'است' + r[0] + r[2];

	forms = hollow_past_actv(base, base_t, base_n, tv);
	if (tv == 'tv') : #{
		base = 'است' + r[0] + 'ي' + r[2];
		forms.update(hollow_past_pasv(base, base_t, base_n));
	#}

	return forms;
#}


def hollow_patt10_pres(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ست' + r[0] + 'ي' + r[2];

	if r[2] == 'ن' :
		base_n = 'ست' + r[0];
	else :
		base_n = 'ست' + r[0] + r[2];

	forms = hollow_pres_actv(base, base_n, tv);
	if (tv == 'tv') : #{
		base = 'ست' + r[0] + 'ا' + r[2];
		forms.update(hollow_pres_pasv(base, base_n));
	#}

	return forms;
#}


def hollow_patt10_subjun(root, tv): #{
	r = root.split('-'); # radicals

	base = 'ست' + r[0] + 'ي' + r[2];

	if r[2] == 'ن' :
		base_n = 'ست' + r[0] + r[1];
	else :
		base_n = 'ست' + r[0] + r[1] + r[2];

	forms = hollow_subjun_actv(base, base_n, tv);
	if (tv == 'tv') : #{
		base = 'ست' + r[0] + 'ا' + r[2];
		forms.update(hollow_subjun_pasv(base, base_n));
	#}

	return forms;
#}


def hollow_patt10_apocop(root, tv): #{
	r = root.split('-'); # radicals

	base_short = 'ست' + r[0] + r[2];
	base_long = 'ست' + r[0] + 'ي' + r[2];

	if r[2] == 'ن' :
		base_n = 'ست' + r[0];
	else :
		base_n = 'ست' + r[0] + r[2];

	forms = hollow_apocop_actv(base_short, base_long, base_n, tv);
	if (tv == 'tv') : #{
		base_long = 'ست' + r[0] + 'ا' + r[2];
		forms.update(hollow_apocop_pasv(base_short, base_long, base_n));
	#}

	return forms;
#}


def hollow_patt10_imp(root, tv): #{
	r = root.split('-'); # radicals

	base_short = 'است' + r[0] + r[2];
	base_long = 'است' + r[0] + 'ي' + r[2];

	if r[2] == 'ن' :
		base_n = 'است' + r[0];
	else :
		base_n = 'است' + r[0] + r[2];

	return hollow_imp(base_short, base_long, base_n, tv); 
#}


def hollow_patt10_pp(root): #{
	r = root.split('-'); # radicals

	return hollow_pp('مست' + r[0] + 'ي' + r[2], '-');
#}


def hollow_patt10_pprs(root): #{
	r = root.split('-'); # radicals

	return hollow_pprs('مست' + r[0] + 'ا' + r[2], '-');
#}



## ----------------------------------------------------------------------------##
## main
## ----------------------------------------------------------------------------##


def main(stem): #{

	forms = {};

	# present vowel!
	if stem['theme'] == '1' : #{
		forms.update(hollow_patt1_past(stem['root'], stem['trans']));
		forms.update(hollow_patt1_pres(stem['root'], stem['subtype'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(hollow_patt1_pp(stem['root']));
		forms.update(hollow_patt1_pprs(stem['root']));
	#}
	elif stem['theme'] == '2' : #{
		forms.update(hollow_patt2_past(stem['root'], stem['trans']));
		forms.update(hollow_patt2_pres(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(hollow_patt2_pp(stem['root']));
		forms.update(hollow_patt2_pprs(stem['root']));
	#}
	elif stem['theme'] == '3' : #{
		forms.update(hollow_patt3_past(stem['root'], stem['trans']));
		forms.update(hollow_patt3_pres(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(hollow_patt3_pp(stem['root']));
		forms.update(hollow_patt3_pprs(stem['root']));
	#}
	elif stem['theme'] == '4' : #{
		forms.update(hollow_patt4_past(stem['root'], stem['trans']));
		forms.update(hollow_patt4_pres(stem['root'], stem['trans']));
		forms.update(hollow_patt4_subjun(stem['root'], stem['trans']));
		forms.update(hollow_patt4_apocop(stem['root'], stem['trans']));
		forms.update(hollow_patt4_imp(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(hollow_patt4_pp(stem['root']));
		forms.update(hollow_patt4_pprs(stem['root']));
	#}
	elif stem['theme'] == '5' : #{
		forms.update(hollow_patt5_past(stem['root'], stem['trans']));
		forms.update(hollow_patt5_pres(stem['root'], stem['trans']));
		forms.update(hollow_patt5_subjun(stem['root'], stem['trans']));
		forms.update(hollow_patt5_apocop(stem['root'], stem['trans']));
		forms.update(hollow_patt5_imp(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(hollow_patt5_pp(stem['root']));
		forms.update(hollow_patt5_pprs(stem['root']));
	#}
	elif stem['theme'] == '6' : #{
		forms.update(hollow_patt6_past(stem['root'], stem['trans']));
		forms.update(hollow_patt6_pres(stem['root'], stem['trans']));
		forms.update(hollow_patt6_subjun(stem['root'], stem['trans']));
		forms.update(hollow_patt6_apocop(stem['root'], stem['trans']));
		forms.update(hollow_patt6_imp(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(hollow_patt6_pp(stem['root']));
		forms.update(hollow_patt6_pprs(stem['root']));
	#}
	elif stem['theme'] == '7' : #{
		forms.update(hollow_patt7_past(stem['root'], stem['trans']));
		forms.update(hollow_patt7_pres(stem['root'], stem['trans']));
		forms.update(hollow_patt7_subjun(stem['root'], stem['trans']));
		forms.update(hollow_patt7_apocop(stem['root'], stem['trans']));
		forms.update(hollow_patt7_imp(stem['root'], stem['trans']));
		forms.update(hollow_patt7_pprs(stem['root']));
	#}
	elif stem['theme'] == '8' : #{
		forms.update(hollow_patt8_past(stem['root'], stem['trans']));
		forms.update(hollow_patt8_pres(stem['root'], stem['trans']));
		forms.update(hollow_patt8_subjun(stem['root'], stem['trans']));
		forms.update(hollow_patt8_apocop(stem['root'], stem['trans']));
		forms.update(hollow_patt8_imp(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(hollow_patt8_pp(stem['root']));
		forms.update(hollow_patt8_pprs(stem['root']));
	#}
	elif stem['theme'] == '10' : #{
		forms.update(hollow_patt10_past(stem['root'], stem['trans']));
		forms.update(hollow_patt10_pres(stem['root'], stem['trans']));
		forms.update(hollow_patt10_subjun(stem['root'], stem['trans']));
		forms.update(hollow_patt10_apocop(stem['root'], stem['trans']));
		forms.update(hollow_patt10_imp(stem['root'], stem['trans']));
                if stem['trans'] == 'tv':
			forms.update(hollow_patt10_pp(stem['root']));
		forms.update(hollow_patt10_pprs(stem['root']));
	#}

	return forms;

#}

