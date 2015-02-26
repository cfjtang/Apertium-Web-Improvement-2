#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-


## ----------------------------------------------------------------------------##
## loan
## ----------------------------------------------------------------------------##


# ???
#def loan_pprs(root): #{
#}


# separate loan_pp_double_cons? (with and without euphonic i, LR/RL)
def loan_pp(pp): #{

	forms = {};
	
	forms['pp.m.sg'] = [(pp, '-', '-')] ;
	forms['pp.f.sg'] = [(pp + 'a', '-', '-')] ;
	forms['pp.mf.pl'] = [(pp + 'i', '-', '-')] ;

	return forms;
#}



# ek/ok, lek/lok??
def loan_consonant_forms (form_sg, form_sg_suff, r, tv): #{
# only past.p3.sg.f has different forms with and without suffixes

	if tv == 'iv' : #{
	 	forms = [(form_sg, '-', r),
	 		 (form_sg_suff, 'S__qtalt/x', r),
			 (form_sg_suff, 'S__qtalt/ilha', r),
			 (form_sg_suff, 'S__qtalt/ilhiex', r)];
	#}
	else : #{
		forms = [(form_sg, '-', r),
			 (form_sg_suff, 'S__qtalt/x', r),
			 (form_sg_suff, 'S__qtalt/ni', r),
			 (form_sg_suff, 'S__qtalt/nix', r),
			 (form_sg_suff, 'S__qtalt/ilha', r),
			 (form_sg_suff, 'S__qtalt/ilhiex', r),
			 (form_sg_suff, 'S__qtaltu/hielha', r),
			 (form_sg_suff, 'S__qtaltu/hielhiex', r)];
	#}

	return forms;
#}



def loan_vowel_forms (form_pl, form_pl_suff, r, tv): #{
# different with/without suffix forms only for past.p1.mf.pl

# jittradíxxi: both ma jittradíx and ma jittradixxíx!!!
# but there is only one form ending with -ixxix or -ixxax in the corpus:
# tissostitwixxix

# oh, but there are also forms like
#      1 ^ttrasferixxiet/*ttrasferixxiet$
#      1 ^stabbilixxiet/*stabbilixxiet$
#      1 ^rreaġixxiet/*rreaġixxiet$
#      1 ^ipprojbixxiet/*ipprojbixxiet$
#      1 ^esegwixxiet/*esegwixxiet$
#      1 ^esebixxiet/*esebixxiet$
#      1 ^aġixxiet/*aġixxiet$
# also with pron. suffixes - only 2:
#      1 ^jissostitwixxihom/*jissostitwixxihom$
#      1 ^issostwixxihom/*issostwixxihom$

# so what? LR for ixx + suffixes ?

	if tv == 'iv' : 
		forms = [(form_pl, '-', r),
			 (form_pl_suff, 'S__qtalt/x', r),
			 (form_pl_suff, 'S__qtaltu/lha', r),
			 (form_pl_suff, 'S__qtaltu/lhiex', r)];
	else :
		forms = [(form_pl, '-', r),
			 (form_pl_suff, 'S__qtalt/x', r),
			 (form_pl_suff, 'S__qtaltu/ni', r),
			 (form_pl_suff, 'S__qtaltu/nix', r),
			 (form_pl_suff, 'S__qtaltu/lha', r),
			 (form_pl_suff, 'S__qtaltu/lhiex', r),
			 (form_pl_suff, 'S__qtaltu/hielha', r),
			 (form_pl_suff, 'S__qtaltu/hielhiex', r)];

	return forms;
#}



def loan_past_forms(forms, stem, ixx, vowel_pres, r, tv): #{

	if vowel_pres == 'i': #{
		# stabilixxa, but stabilieni, stabiliek
		forms['past.p3.m.sg'] += loan_vowel_forms(stem + ixx + 'a', stem + 'ie', r, tv);
		# falliet - ma fallitx? or ma fallietx?
		forms['past.p3.f.sg'] += loan_consonant_forms(stem + 'iet', stem + 'it', r, tv);
		forms['past.p2.mf.sg'] += loan_consonant_forms(stem + 'ejt', stem + 'ejt', r, tv);	
		forms['past.p1.mf.sg'] += loan_consonant_forms(stem + 'ejt', stem + 'ejt', r, tv);
		forms['past.p3.mf.pl'] += loan_vowel_forms(stem + 'ew', stem + 'ew', r, tv);
		forms['past.p2.mf.pl'] += loan_vowel_forms(stem + 'ejtu', stem + 'ejtu', r, tv);
		forms['past.p1.mf.pl'] += loan_vowel_forms(stem + 'ejna', stem + 'ejnie', r, tv);
	#}
	else : #{   vowel_pres == a
		forms['past.p3.m.sg'] += loan_vowel_forms(stem + ixx + 'a', stem + 'a', r, tv);
		forms['past.p3.f.sg'] += loan_consonant_forms(stem + 'at', stem + 'at', r, tv);
		forms['past.p2.mf.sg'] += loan_consonant_forms(stem + 'ajt', stem + 'ajt', r, tv);	
		forms['past.p1.mf.sg'] += loan_consonant_forms(stem + 'ajt', stem + 'ajt', r, tv);
		forms['past.p3.mf.pl'] += loan_vowel_forms(stem + 'aw', stem + 'aw', r, tv);
		forms['past.p2.mf.pl'] += loan_vowel_forms(stem + 'ajtu', stem + 'ajtu', r, tv);
		forms['past.p1.mf.pl'] += loan_vowel_forms(stem + 'ajna', stem + 'ajnie', r, tv);
	#}

	return forms;
#}



def loan_past_double_cons(base, ixx, vowel_pres, tv): #{

	forms = {};

	forms['past.p3.m.sg'] = [];
	forms['past.p3.f.sg'] = [];
	forms['past.p2.mf.sg'] = [];
	forms['past.p1.mf.sg'] = [];
	forms['past.p3.mf.pl'] = [];
	forms['past.p2.mf.pl'] = []; 
	forms['past.p1.mf.pl'] = []; 

	loan_past_forms(forms, base, ixx, vowel_pres, 'LR', tv);
	loan_past_forms(forms, 'i' + base, ixx, vowel_pres, 'LR', tv);
#	loan_past_forms(forms, first_cons + base, ixx, vowel_pres, 'LR');
#	loan_past_forms(forms, 'i' + first_cons + base, ixx, vowel_pres, 'LR');
	loan_past_forms(forms, 'i' + base, ixx, vowel_pres, 'RL', tv);

	return forms;
#}



def loan_past(base, ixx, vowel_pres, tv): #{

	forms = {};

	forms['past.p3.m.sg'] = [];
	forms['past.p3.f.sg'] = [];
	forms['past.p2.mf.sg'] = [];
	forms['past.p1.mf.sg'] = [];
	forms['past.p3.mf.pl'] = [];
	forms['past.p2.mf.pl'] = []; 
	forms['past.p1.mf.pl'] = []; 

	loan_past_forms(forms, base, ixx, vowel_pres, '-', tv);

	return forms;
#}



def loan_pres_first_vowel(stem, ixx, vowel_pres, tv): #{

	forms = {};

	if vowel_pres == 'i' : #{
		sg_suffix = 'i';
		pl_suffix = 'u';
	#}
	else : #{ # vowel_pres == 'a'
		sg_suffix = 'a';
		pl_suffix = "aw";
	#}


	first_vowel = stem[0];
	if first_vowel == 'i' : #{
	# indika, jindika
	# (+ jistabilixxa, but jistabilini)
		forms['pres.p3.m.sg'] = loan_vowel_forms(stem + ixx + sg_suffix, stem + sg_suffix, 'LR', tv);
		forms['pres.p3.m.sg'] += loan_vowel_forms('j' + stem + ixx + sg_suffix, 'j' + stem + sg_suffix, 'LR', tv);
		forms['pres.p3.m.sg'] += loan_vowel_forms(stem + ixx + sg_suffix, stem + sg_suffix, 'RL', tv);

		forms['pres.p3.mf.pl'] = loan_vowel_forms(stem + ixx + pl_suffix, stem + pl_suffix, 'LR', tv);
		forms['pres.p3.mf.pl'] += loan_vowel_forms('j' + stem + ixx + pl_suffix, 'j' + stem + pl_suffix, 'LR', tv);
		forms['pres.p3.mf.pl'] += loan_vowel_forms(stem + ixx + pl_suffix, stem + pl_suffix, 'RL', tv);
	#}
	else : #{
		forms['pres.p3.m.sg'] = loan_vowel_forms('j' + stem + ixx + sg_suffix, 'j' + stem + sg_suffix, '-', tv);
		forms['pres.p3.mf.pl'] = loan_vowel_forms('j' + stem + ixx + pl_suffix, 'j' + stem + pl_suffix, '-', tv);
	#}

	forms['pres.p3.f.sg'] = loan_vowel_forms('t' + stem + ixx + sg_suffix, 't' + stem + sg_suffix, '-', tv);
	forms['pres.p2.mf.sg'] = loan_vowel_forms('t' + stem + ixx + sg_suffix, 't' + stem + sg_suffix, '-', tv)
	forms['pres.p1.mf.sg'] = loan_vowel_forms('n' + stem + ixx + sg_suffix, 'n' + stem + sg_suffix, '-', tv);

	forms['pres.p2.mf.pl'] = loan_vowel_forms('t' + stem + ixx + pl_suffix, 't' + stem + pl_suffix, '-', tv);
	forms['pres.p1.mf.pl'] = loan_vowel_forms('n' + stem + ixx  + pl_suffix, 'n' + stem + pl_suffix, '-', tv);

	return forms;

#}



def loan_pres_first_cons(stem, ixx, vowel_pres, tv): #{

	forms = {};

	if vowel_pres == 'i' : #{
		sg_suffix = 'i';
		pl_suffix = 'u';
	#}
	else : #{ # vowel_pres == 'a'
		sg_suffix = 'a';
		pl_suffix = "aw";
	#}

	first_cons = stem[0];

	forms['pres.p3.m.sg'] = loan_vowel_forms('i' + stem + ixx + sg_suffix, 'i' + stem + sg_suffix, 'LR', tv);
	forms['pres.p3.m.sg'] += loan_vowel_forms('j' + stem + ixx + sg_suffix, 'j' + stem + sg_suffix, 'LR', tv);
	forms['pres.p3.m.sg'] += loan_vowel_forms('i' + stem + ixx + sg_suffix, 'i' + stem + sg_suffix, 'RL', tv);

	forms['pres.p3.f.sg'] = loan_vowel_forms('t' + stem + ixx + sg_suffix, 't' + stem + sg_suffix, 'LR', tv);
	forms['pres.p3.f.sg'] += loan_vowel_forms(first_cons + stem + ixx + sg_suffix, first_cons + stem + sg_suffix, 'LR', tv);
	forms['pres.p3.f.sg'] += loan_vowel_forms('it' + stem + ixx + sg_suffix, 'it' + stem + sg_suffix, 'LR', tv);
	forms['pres.p3.f.sg'] += loan_vowel_forms('i' + first_cons + stem + ixx + sg_suffix, 'i' + first_cons + stem + sg_suffix, 'LR', tv);
	forms['pres.p3.f.sg'] += loan_vowel_forms('it' + stem + ixx + sg_suffix, 'it' + stem + sg_suffix, 'RL', tv);

	forms['pres.p2.mf.sg'] = loan_vowel_forms('t' + stem + ixx + sg_suffix, 't' + stem + sg_suffix, 'LR', tv);
	forms['pres.p2.mf.sg'] += loan_vowel_forms(first_cons + stem + ixx + sg_suffix, first_cons + stem + sg_suffix, 'LR', tv);
	forms['pres.p2.mf.sg'] += loan_vowel_forms('it' + stem + ixx + sg_suffix, 'it' + stem + sg_suffix, 'LR', tv);
	forms['pres.p2.mf.sg'] += loan_vowel_forms('i' + first_cons + stem + ixx + sg_suffix, 'i' + first_cons + stem + sg_suffix, 'LR', tv);
	forms['pres.p2.mf.sg'] += loan_vowel_forms('it' + stem + ixx + sg_suffix, 'it' + stem + sg_suffix, 'RL', tv);

	forms['pres.p1.mf.sg'] = loan_vowel_forms('n' + stem + ixx + sg_suffix, 'n' + stem + sg_suffix, 'LR', tv);
	forms['pres.p1.mf.sg'] += loan_vowel_forms('in' + stem + ixx + sg_suffix, 'in' + stem + sg_suffix, 'LR', tv);
	forms['pres.p1.mf.sg'] += loan_vowel_forms('in' + stem + ixx + sg_suffix, 'in' + stem + sg_suffix, 'RL', tv);

	forms['pres.p3.mf.pl'] = loan_vowel_forms('i' + stem + ixx + pl_suffix, 'i' + stem + pl_suffix, 'LR', tv);
	forms['pres.p3.mf.pl'] += loan_vowel_forms('j' + stem + ixx + pl_suffix, 'j' + stem + pl_suffix, 'LR', tv);
	forms['pres.p3.mf.pl'] += loan_vowel_forms('i' + stem + ixx + pl_suffix, 'i' + stem + pl_suffix, 'RL', tv);

	forms['pres.p2.mf.pl'] = loan_vowel_forms('t' + stem + ixx + pl_suffix, 't' + stem + pl_suffix, 'LR', tv);
	forms['pres.p2.mf.pl'] += loan_vowel_forms(first_cons + stem + ixx + pl_suffix, first_cons + stem + pl_suffix, 'LR', tv);
	forms['pres.p2.mf.pl'] += loan_vowel_forms('it' + stem + ixx + pl_suffix, 'it' + stem + pl_suffix, 'LR', tv);
	forms['pres.p2.mf.pl'] += loan_vowel_forms('i' + first_cons + stem + ixx + pl_suffix, 'i' + first_cons + stem + pl_suffix, 'LR', tv);
	forms['pres.p2.mf.pl'] += loan_vowel_forms('it' + stem + ixx + pl_suffix, 'it' + stem + pl_suffix, 'RL', tv);

	forms['pres.p1.mf.pl'] = loan_vowel_forms('n' + stem + ixx  + pl_suffix, 'n' + stem + pl_suffix, 'LR', tv);
	forms['pres.p1.mf.pl'] += loan_vowel_forms('in' + stem + ixx  + pl_suffix, 'in' + stem + pl_suffix, 'LR', tv);
	forms['pres.p1.mf.pl'] += loan_vowel_forms('in' + stem + ixx  + pl_suffix, 'in' + stem + pl_suffix, 'RL', tv);

	return forms;

#}



def loan_pres_two_cons(stem, ixx, vowel_pres, tv): #{
# two or double

	forms = {};

	if vowel_pres == 'i' : #{
		sg_suffix = 'i';
		pl_suffix = 'u';
	#}
	else : #{ # vowel_pres == 'a'
		sg_suffix = 'a';
		pl_suffix = "aw";
	#}

	# only 1 form? that may be too optimistic 
	forms['pres.p3.m.sg'] = loan_vowel_forms('ji' + stem + ixx + sg_suffix, 'ji' + stem + sg_suffix, '-', tv);
	forms['pres.p3.f.sg'] = loan_vowel_forms('ti' + stem + ixx + sg_suffix, 'ti' + stem + sg_suffix, '-', tv);
	forms['pres.p2.mf.sg'] = loan_vowel_forms('ti' + stem + ixx + sg_suffix, 'ti' + stem + sg_suffix, '-', tv);
	forms['pres.p1.mf.sg'] = loan_vowel_forms('ni' + stem + ixx + sg_suffix, 'ni' + stem + sg_suffix, '-', tv);

	forms['pres.p3.mf.pl'] = loan_vowel_forms('ji' + stem + ixx + pl_suffix, 'ji' + stem + pl_suffix, '-', tv);
	forms['pres.p2.mf.pl'] = loan_vowel_forms('ti' + stem + ixx + pl_suffix, 'ti' + stem + pl_suffix, '-', tv);
	forms['pres.p1.mf.pl'] = loan_vowel_forms('ni' + stem + ixx  + pl_suffix, 'ni' + stem + pl_suffix, '-', tv);

	return forms;

#}


# separate loan_imp_double_cons? (with euphonic i)
def loan_imp(stem, ixx, vowel_pres, tv): #{

	forms = {};

#	if subtype == 'double_cons' :  # or maybe both with and without 'i'?
#		stem = 'i' + stem;
#
#	if tv == 'tv': #{
	if vowel_pres == 'i': #{
		# jistabilixxa, but jistabilini
		forms['imp.p2.mf.sg'] = loan_vowel_forms(stem + ixx + 'i', stem + 'i', '-', tv);
		forms['imp.p2.mf.pl'] = loan_vowel_forms(stem + ixx + 'u', stem + 'u', '-', tv);
	#}
	else : #{   vowel_pres == a
		forms['imp.p2.mf.sg'] = loan_vowel_forms(stem + ixx + 'a', stem + 'a', '-', tv);
		forms['imp.p2.mf.pl'] = loan_vowel_forms(stem + ixx + 'aw', stem + 'aw', '-', tv);
	#}

	return forms;

#}



def main(stem): #{

	forms = {};

	ixx = stem['ixx'];
#	if 'ixx' != '':
#		ixx = stem['ixx'];   # ixx = 'ixx';

	if stem['subtype'] == 'first_vowel': #{
		forms = loan_past(stem['base'], ixx, stem['vowel_impf'], stem['trans']);
		forms.update(loan_pres_first_vowel(stem['base'], ixx, stem['vowel_impf'], stem['trans']));
		forms.update(loan_imp(stem['base'], ixx, stem['vowel_impf'], stem['trans']));
	#}

	elif stem['subtype'] == 'first_cons': #{
		forms = loan_past(stem['base'], ixx, stem['vowel_impf'], stem['trans']);
		forms.update(loan_pres_first_cons(stem['base'], ixx, stem['vowel_impf'], stem['trans']));
		forms.update(loan_imp(stem['base'], ixx, stem['vowel_impf'], stem['trans']));
	#}

	elif stem['subtype'] == 'two_cons' : #{
		forms = loan_past(stem['base'], ixx, stem['vowel_impf'], stem['trans']);
		forms.update(loan_pres_two_cons(stem['base'], ixx, stem['vowel_impf'], stem['trans']));
		forms.update(loan_imp(stem['base'], ixx, stem['vowel_impf'], stem['trans']));
	#}

	elif stem['subtype'] == 'double_cons' : #{
		forms = loan_past_double_cons(stem['base'], ixx, stem['vowel_impf'], stem['trans']);
		forms.update(loan_pres_two_cons(stem['base'], ixx, stem['vowel_impf'], stem['trans']));
		forms.update(loan_imp('i' + stem['base'], ixx, stem['vowel_impf'], stem['trans']));
	#}

	if stem['pp'] != '' : #{
		forms.update(loan_pp(stem['pp']));
# pprs
	#}

	return forms;

#}
