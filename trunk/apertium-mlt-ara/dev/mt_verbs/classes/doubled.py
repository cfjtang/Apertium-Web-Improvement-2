#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-


## ----------------------------------------------------------------------------##
## doubled
## ----------------------------------------------------------------------------##


def doubled_consonant_forms (form_sg, form_sg_suff, r, ek, tv): #{

	if ek == 'ok' : #{
		if tv == 'iv' : #{
		 	forms = [(form_sg, '-', r),
		 		 (form_sg_suff, 'S__qtalt/x', r),
				 (form_sg_suff, 'S__xrobt/ilha', r),
				 (form_sg_suff, 'S__xrobt/ilhiex', r)];
		#}
		else : #{
			forms = [(form_sg, '-', r),
				 (form_sg_suff, 'S__qtalt/x', r),
				 (form_sg_suff, 'S__xrobt/ni', r),
				 (form_sg_suff, 'S__xrobt/nix', r),
				 (form_sg_suff, 'S__xrobt/ilha', r),
				 (form_sg_suff, 'S__xrobt/ilhiex', r),
				 (form_sg_suff, 'S__qtaltu/hielha', r),
				 (form_sg_suff, 'S__qtaltu/hielhiex', r)];
		#}
	else : #{
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
	#}

	return forms;
#}


def doubled_vowel_forms (form_pl, form_pl_suff, r, tv): #{

	if tv == 'iv' : #{
	 	forms = [(form_pl, '-', r),
	 		 (form_pl_suff, 'S__qtalt/x', r),
			 (form_pl_suff, 'S__qtaltu/lha', r),
			 (form_pl_suff, 'S__qtaltu/lhiex', r)];
	#}
	else : #{
		forms = [(form_pl, '-', r),
			 (form_pl_suff, 'S__qtalt/x', r),
			 (form_pl_suff, 'S__qtaltu/ni', r),
			 (form_pl_suff, 'S__qtaltu/nix', r),
			 (form_pl_suff, 'S__qtaltu/lha', r),
			 (form_pl_suff, 'S__qtaltu/lhiex', r),
			 (form_pl_suff, 'S__qtaltu/hielha', r),
			 (form_pl_suff, 'S__qtaltu/hielhiex', r)];
	#}

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 1
## ----------------------------------------------------------------------------##


# no ek/ok?
def doubled_patt1_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};
	ek = 'ek';

	forms['past.p3.m.sg'] = doubled_consonant_forms (r[0] + v[0] + r[1] + r[2], r[0] + v[0] + r[1] + r[2], '-', ek, tv);
	forms['past.p3.f.sg'] = doubled_consonant_forms (r[0] + v[0] + r[1] + r[2] + 'et', r[0] + v[0] + r[1] + r[2] + 'it', '-', ek, tv);
	forms['past.p2.mf.sg'] = doubled_consonant_forms (r[0] + v[0] + r[1] + r[2] + 'ejt', r[0] + v[0] + r[1] + r[2] + 'ejt', '-', ek, tv);
	forms['past.p1.mf.sg'] = doubled_consonant_forms (r[0] + v[0] + r[1] + r[2] + 'ejt', r[0] + v[0] + r[1] + r[2] + 'ejt', '-', ek, tv);
	forms['past.p3.mf.pl'] = doubled_vowel_forms (r[0] + v[0] + r[1] + r[2] + 'u', r[0] + v[0] + r[1] + r[2] + 'u', '-', tv);
	forms['past.p3.mf.pl'] += doubled_vowel_forms (r[0] + v[0] + r[1] + r[2] + 'ew', r[0] + v[0] + r[1] + r[2] + 'ew', 'LR', tv);
	forms['past.p2.mf.pl'] = doubled_vowel_forms (r[0] + v[0] + r[1] + r[2] + 'ejtu', r[0] + v[0] + r[1] + r[2] + 'ejtu', '-', tv);
	forms['past.p1.mf.pl'] = doubled_vowel_forms (r[0] + v[0] + r[1] + r[2] + 'ejna', r[0] + v[0] + r[1] + r[2] + 'ejna', '-', tv);

	return forms;
#}


# ek/ok done
def doubled_patt1_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'o' :
		ek = 'ok';
	else :
		ek = 'ek';

	forms['pres.p3.m.sg'] = doubled_consonant_forms ('i' + r[0] + v[1] + r[1] + r[2], 'i' + r[0] + v[1] + r[1] + r[2], 'LR', ek, tv);		# irodd
	forms['pres.p3.m.sg'] += doubled_consonant_forms ('j' + r[0] + v[1] + r[1] + r[2], 'j' + r[0] + v[1] + r[1] + r[2], 'LR', ek, tv); 		# jrodd
	forms['pres.p3.m.sg'] += doubled_consonant_forms ('i' + r[0] + v[1] + r[1] + r[2], 'i' + r[0] + v[1] + r[1] + r[2], 'RL', ek, tv);		# ~irodd

	forms['pres.p3.f.sg'] = doubled_consonant_forms ('t' + r[0] + v[1] + r[1] + r[2], 't' + r[0] + v[1] + r[1] + r[2], '-', ek, tv);		# trodd

	forms['pres.p2.mf.sg'] = doubled_consonant_forms ('t' + r[0] + v[1] + r[1] + r[2], 't' + r[0] + v[1] + r[1] + r[2], '-', ek, tv);			# trodd

	forms['pres.p1.mf.sg'] = doubled_consonant_forms ('in' + r[0] + v[1] + r[1] + r[2], 'in' + r[0] + v[1] + r[1] + r[2], 'LR', ek, tv);		# inrodd
	forms['pres.p1.mf.sg'] += doubled_consonant_forms ('i' + r[0] + r[0] + v[1] + r[1] + r[2], 'i' + r[0] + r[0] + v[1] + r[1] + r[2], 'LR', ek, tv);		# irrodd
	forms['pres.p1.mf.sg'] += doubled_consonant_forms ('in' + r[0] + r[0] + v[1] + r[1] + r[2], 'in' + r[0] + r[0] + v[1] + r[1] + r[2], 'RL', ek, tv);	# irrodd

	forms['pres.p3.mf.pl'] = doubled_vowel_forms ('i' + r[0] + v[1] + r[1] + r[2] + 'u', 'i' + r[0] + v[1] + r[1] + r[2] + 'u', 'LR', tv);		# iroddu
	forms['pres.p3.mf.pl'] += doubled_vowel_forms ('j' + r[0] + v[1] + r[1] + r[2] + 'u', 'j' + r[0] + v[1] + r[1] + r[2] + 'u', 'LR', tv); 	# jroddu
	forms['pres.p3.mf.pl'] += doubled_vowel_forms ('i' + r[0] + v[1] + r[1] + r[2] + 'u', 'i' + r[0] + v[1] + r[1] + r[2] + 'u', 'RL', tv);	# ~iroddu

	forms['pres.p2.mf.pl'] = doubled_vowel_forms ('t' + r[0] + v[1] + r[1] + r[2] + 'u', 't' + r[0] + v[1] + r[1] + r[2] + 'u', '-', tv);		# troddu

	forms['pres.p1.mf.pl'] = doubled_vowel_forms ('in' + r[0] + v[1] + r[1] + r[2] + 'u', 'in' + r[0] + v[1] + r[1] + r[2] + 'u', 'LR', tv);		# inroddu
	forms['pres.p1.mf.pl'] += doubled_vowel_forms ('i' + r[0] + r[0] + v[1] + r[1] + r[2] + 'u', 'i' + r[0] + r[0] + v[1] + r[1] + r[2] + 'u', 'LR', tv);	# irroddu
	forms['pres.p1.mf.pl'] += doubled_vowel_forms ('in' + r[0] + r[0] + v[1] + r[1] + r[2] + 'u', 'in' + r[0] + r[0] + v[1] + r[1] + r[2] + 'u', 'RL', tv);	# irroddu


	return forms;
#}


# ek/ok done
def doubled_patt1_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'o' :
		ek = 'ok';
	else :
		ek = 'ek';	

	suffix = 'u'

 	forms['imp.p2.mf.sg'] = doubled_consonant_forms (r[0] + v[1] + r[1] + r[2], r[0] + v[1] + r[1] + r[2], '-', ek, tv);
	forms['imp.p2.mf.pl'] = doubled_vowel_forms (r[0] + v[1] + r[1] + r[2] + suffix , r[0] + v[1] + r[1] + r[2] + suffix , '-', tv);

	return forms;
#}


def doubled_patt1_pp(root, vowels, pref): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};
	
	forms['pp.m.sg'] = [(pref + r[0] + r[1] + 'u' + r[2], '-', '-')];
	forms['pp.f.sg'] = [(pref + r[0] + r[1] + 'u' + r[2] + 'a', '-', '-')];
	forms['pp.mf.pl'] = [(pref + r[0] + r[1] + 'u' + r[2] + 'in', '-', '-')];

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 7a
## ----------------------------------------------------------------------------##


# no ek/ok?
def doubled_patt7a_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};
	ek = 'ek';

	forms['past.p3.m.sg'] = doubled_consonant_forms ('n' + r[0] + v[0] + r[1] + r[2], 'n' + r[0] + v[0] + r[1] + r[2], '-', ek, tv);
	forms['past.p3.m.sg'] += doubled_consonant_forms ('in' + r[0] + v[0] + r[1] + r[2], 'in' + r[0] + v[0] + r[1] + r[2], 'LR', ek, tv);
	forms['past.p3.f.sg'] = doubled_consonant_forms ('n' + r[0] + v[0] + r[1] + r[2] + 'et', 'n' + r[0] + v[0] + r[1] + r[2] + 'it', '-', ek, tv);
	forms['past.p3.f.sg'] += doubled_consonant_forms ('in' + r[0] + v[0] + r[1] + r[2] + 'et', 'in' + r[0] + v[0] + r[1] + r[2] + 'it', 'LR', ek, tv);
	forms['past.p2.mf.sg'] = doubled_consonant_forms ('n' + r[0] + v[0] + r[1] + r[2] + 'ejt', 'n' + r[0] + v[0] + r[1] + r[2] + 'ejt', '-', ek, tv);
	forms['past.p2.mf.sg'] += doubled_consonant_forms ('in' + r[0] + v[0] + r[1] + r[2] + 'ejt', 'in' + r[0] + v[0] + r[1] + r[2] + 'ejt', 'LR', ek, tv);
	forms['past.p1.mf.sg'] = doubled_consonant_forms ('n' + r[0] + v[0] + r[1] + r[2] + 'ejt', 'n' + r[0] + v[0] + r[1] + r[2] + 'ejt', '-', ek, tv);
	forms['past.p1.mf.sg'] += doubled_consonant_forms ('in' + r[0] + v[0] + r[1] + r[2] + 'ejt', 'in' + r[0] + v[0] + r[1] + r[2] + 'ejt', 'LR', ek, tv);
	forms['past.p3.mf.pl'] = doubled_vowel_forms ('n' + r[0] + v[0] + r[1] + r[2] + 'u', 'n' + r[0] + v[0] + r[1] + r[2] + 'u', '-', tv);
	forms['past.p3.mf.pl'] += doubled_vowel_forms ('in' + r[0] + v[0] + r[1] + r[2] + 'ew', 'in' + r[0] + v[0] + r[1] + r[2] + 'ew', 'LR', tv);
	forms['past.p2.mf.pl'] = doubled_vowel_forms ('n' + r[0] + v[0] + r[1] + r[2] + 'ejtu', 'n' + r[0] + v[0] + r[1] + r[2] + 'ejtu', '-', tv);
	forms['past.p2.mf.pl'] += doubled_vowel_forms ('in' + r[0] + v[0] + r[1] + r[2] + 'ejtu', 'in' + r[0] + v[0] + r[1] + r[2] + 'ejtu', 'LR', tv);
	forms['past.p1.mf.pl'] = doubled_vowel_forms ('n' + r[0] + v[0] + r[1] + r[2] + 'ejna', 'n' + r[0] + v[0] + r[1] + r[2] + 'ejna', '-', tv);
	forms['past.p1.mf.pl'] += doubled_vowel_forms ('in' + r[0] + v[0] + r[1] + r[2] + 'ejna', 'in' + r[0] + v[0] + r[1] + r[2] + 'ejna', 'LR', tv);

	return forms;
#}


# ek/ok done
def doubled_patt7a_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'o' :
		ek = 'ok';
	else :
		ek = 'ek';

	forms['pres.p3.m.sg'] = doubled_consonant_forms ('jin' + r[0] + v[1] + r[1] + r[2], 'jin' + r[0] + v[1] + r[1] + r[2], '-', ek, tv); 
	forms['pres.p3.f.sg'] = doubled_consonant_forms ('tin' + r[0] + v[1] + r[1] + r[2], 'tin' + r[0] + v[1] + r[1] + r[2], '-', ek, tv);	
	forms['pres.p2.mf.sg'] = doubled_consonant_forms ('tin' + r[0] + v[1] + r[1] + r[2], 'tin' + r[0] + v[1] + r[1] + r[2], '-', ek, tv);	
	forms['pres.p1.mf.sg'] = doubled_consonant_forms ('nin' + r[0] + v[1] + r[1] + r[2], 'nin' + r[0] + v[1] + r[1] + r[2], '-', ek, tv);	

	forms['pres.p3.mf.pl'] = doubled_vowel_forms ('jin' + r[0] + v[1] + r[1] + r[2] + 'u', 'jin' + r[0] + v[1] + r[1] + r[2] + 'u', '-', tv);
	forms['pres.p2.mf.pl'] = doubled_vowel_forms ('tin' + r[0] + v[1] + r[1] + r[2] + 'u', 'tin' + r[0] + v[1] + r[1] + r[2] + 'u', '-', tv);
	forms['pres.p1.mf.pl'] = doubled_vowel_forms ('nin' + r[0] + v[1] + r[1] + r[2] + 'u', 'nin' + r[0] + v[1] + r[1] + r[2] + 'u', '-', tv);


	return forms;
#}


# ek/ok done
def doubled_patt7a_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'o' :
		ek = 'ok';
	else :
		ek = 'ek';	

	suffix = 'u'

 	forms['imp.p2.mf.sg'] = doubled_consonant_forms ('in' + r[0] + v[1] + r[1] + r[2], 'in' + r[0] + v[1] + r[1] + r[2], '-', ek, tv);
	forms['imp.p2.mf.pl'] = doubled_vowel_forms ('in' + r[0] + v[1] + r[1] + r[2] + suffix , 'in' + r[0] + v[1] + r[1] + r[2] + suffix , '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 7b
## ----------------------------------------------------------------------------##


# no ek/ok?
def doubled_patt7b_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};
	ek = 'ek';

	forms['past.p3.m.sg'] = doubled_consonant_forms ('nt' + r[0] + v[0] + r[1] + r[2], 'nt' + r[0] + v[0] + r[1] + r[2], '-', ek, tv);
	forms['past.p3.m.sg'] += doubled_consonant_forms ('int' + r[0] + v[0] + r[1] + r[2], 'int' + r[0] + v[0] + r[1] + r[2], 'LR', ek, tv);
	forms['past.p3.f.sg'] = doubled_consonant_forms ('nt' + r[0] + v[0] + r[1] + r[2] + 'et', 'nt' + r[0] + v[0] + r[1] + r[2] + 'it', '-', ek, tv);
	forms['past.p3.f.sg'] += doubled_consonant_forms ('int' + r[0] + v[0] + r[1] + r[2] + 'et', 'int' + r[0] + v[0] + r[1] + r[2] + 'it', 'LR', ek, tv);
	forms['past.p2.mf.sg'] = doubled_consonant_forms ('nt' + r[0] + v[0] + r[1] + r[2] + 'ejt', 'nt' + r[0] + v[0] + r[1] + r[2] + 'ejt', '-', ek, tv);
	forms['past.p2.mf.sg'] += doubled_consonant_forms ('int' + r[0] + v[0] + r[1] + r[2] + 'ejt', 'int' + r[0] + v[0] + r[1] + r[2] + 'ejt', 'LR', ek, tv);
	forms['past.p1.mf.sg'] = doubled_consonant_forms ('nt' + r[0] + v[0] + r[1] + r[2] + 'ejt', 'nt' + r[0] + v[0] + r[1] + r[2] + 'ejt', '-', ek, tv);
	forms['past.p1.mf.sg'] += doubled_consonant_forms ('int' + r[0] + v[0] + r[1] + r[2] + 'ejt', 'int' + r[0] + v[0] + r[1] + r[2] + 'ejt', 'LR', ek, tv);
	forms['past.p3.mf.pl'] = doubled_vowel_forms ('nt' + r[0] + v[0] + r[1] + r[2] + 'u', 'nt' + r[0] + v[0] + r[1] + r[2] + 'u', '-', tv);
	forms['past.p3.mf.pl'] += doubled_vowel_forms ('int' + r[0] + v[0] + r[1] + r[2] + 'ew', 'int' + r[0] + v[0] + r[1] + r[2] + 'ew', 'LR', tv);
	forms['past.p2.mf.pl'] = doubled_vowel_forms ('nt' + r[0] + v[0] + r[1] + r[2] + 'ejtu', 'nt' + r[0] + v[0] + r[1] + r[2] + 'ejtu', '-', tv);
	forms['past.p2.mf.pl'] += doubled_vowel_forms ('int' + r[0] + v[0] + r[1] + r[2] + 'ejtu', 'int' + r[0] + v[0] + r[1] + r[2] + 'ejtu', 'LR', tv);
	forms['past.p1.mf.pl'] = doubled_vowel_forms ('nt' + r[0] + v[0] + r[1] + r[2] + 'ejna', 'nt' + r[0] + v[0] + r[1] + r[2] + 'ejna', '-', tv);
	forms['past.p1.mf.pl'] += doubled_vowel_forms ('int' + r[0] + v[0] + r[1] + r[2] + 'ejna', 'int' + r[0] + v[0] + r[1] + r[2] + 'ejna', 'LR', tv);

	return forms;
#}


# ek/ok done
def doubled_patt7b_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'o' :
		ek = 'ok';
	else :
		ek = 'ek';

	forms['pres.p3.m.sg'] = doubled_consonant_forms ('jint' + r[0] + v[1] + r[1] + r[2], 'jint' + r[0] + v[1] + r[1] + r[2], '-', ek, tv); 
	forms['pres.p3.f.sg'] = doubled_consonant_forms ('tint' + r[0] + v[1] + r[1] + r[2], 'tint' + r[0] + v[1] + r[1] + r[2], '-', ek, tv);	
	forms['pres.p2.mf.sg'] = doubled_consonant_forms ('tint' + r[0] + v[1] + r[1] + r[2], 'tint' + r[0] + v[1] + r[1] + r[2], '-', ek, tv);	
	forms['pres.p1.mf.sg'] = doubled_consonant_forms ('nint' + r[0] + v[1] + r[1] + r[2], 'nint' + r[0] + v[1] + r[1] + r[2], '-', ek, tv);	

	forms['pres.p3.mf.pl'] = doubled_vowel_forms ('jint' + r[0] + v[1] + r[1] + r[2] + 'u', 'jint' + r[0] + v[1] + r[1] + r[2] + 'u', '-', tv);
	forms['pres.p2.mf.pl'] = doubled_vowel_forms ('tint' + r[0] + v[1] + r[1] + r[2] + 'u', 'tint' + r[0] + v[1] + r[1] + r[2] + 'u', '-', tv);
	forms['pres.p1.mf.pl'] = doubled_vowel_forms ('nint' + r[0] + v[1] + r[1] + r[2] + 'u', 'nint' + r[0] + v[1] + r[1] + r[2] + 'u', '-', tv);


	return forms;
#}


# ek/ok done
def doubled_patt7b_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'o' :
		ek = 'ok';
	else :
		ek = 'ek';	

	suffix = 'u'

 	forms['imp.p2.mf.sg'] = doubled_consonant_forms ('int' + r[0] + v[1] + r[1] + r[2], 'int' + r[0] + v[1] + r[1] + r[2], '-', ek, tv);
	forms['imp.p2.mf.pl'] = doubled_vowel_forms ('int' + r[0] + v[1] + r[1] + r[2] + suffix , 'int' + r[0] + v[1] + r[1] + r[2] + suffix , '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 8a
## ----------------------------------------------------------------------------##


# no ek/ok?
def doubled_patt8a_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};
	ek = 'ek';

# TODO: LR forms only for some verbs (depends on r[0])

	forms['past.p3.m.sg'] = doubled_consonant_forms (r[0] + 't' + v[0] + r[1] + r[2], r[0] + 't' + v[0] + r[1] + r[2], '-', ek, tv);
	forms['past.p3.m.sg'] += doubled_consonant_forms ('i' + r[0] + 't' + v[0] + r[1] + r[2], 'i' + r[0] + 't' + v[0] + r[1] + r[2], 'LR', ek, tv);
	forms['past.p3.f.sg'] = doubled_consonant_forms (r[0] + 't' + v[0] + r[1] + r[2] + 'et', r[0] + 't' + v[0] + r[1] + r[2] + 'it', '-', ek, tv);
	forms['past.p3.f.sg'] += doubled_consonant_forms ('i' + r[0] + 't' + v[0] + r[1] + r[2] + 'et', 'i' + r[0] + 't' + v[0] + r[1] + r[2] + 'it', 'LR', ek, tv);
	forms['past.p2.mf.sg'] = doubled_consonant_forms (r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', '-', ek, tv);
	forms['past.p2.mf.sg'] += doubled_consonant_forms ('i' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', 'i' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', 'LR', ek, tv);
	forms['past.p1.mf.sg'] = doubled_consonant_forms (r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', '-', ek, tv);
	forms['past.p1.mf.sg'] += doubled_consonant_forms ('i' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', 'i' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', 'LR', ek, tv);
	forms['past.p3.mf.pl'] = doubled_vowel_forms (r[0] + 't' + v[0] + r[1] + r[2] + 'u', r[0] + 't' + v[0] + r[1] + r[2] + 'u', '-', tv);
	forms['past.p3.mf.pl'] += doubled_vowel_forms ('i' + r[0] + 't' + v[0] + r[1] + r[2] + 'ew', 'i' + r[0] + 't' + v[0] + r[1] + r[2] + 'ew', 'LR', tv);
	forms['past.p2.mf.pl'] = doubled_vowel_forms (r[0] + 't' + v[0] + r[1] + r[2] + 'ejtu', r[0] + 't' + v[0] + r[1] + r[2] + 'ejtu', '-', tv);
	forms['past.p2.mf.pl'] += doubled_vowel_forms ('i' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejtu', 'i' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejtu', 'LR', tv);
	forms['past.p1.mf.pl'] = doubled_vowel_forms (r[0] + 't' + v[0] + r[1] + r[2] + 'ejna', r[0] + 't' + v[0] + r[1] + r[2] + 'ejna', '-', tv);
	forms['past.p1.mf.pl'] += doubled_vowel_forms ('i' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejna', 'i' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejna', 'LR', tv);

	return forms;
#}


# ek/ok done
def doubled_patt8a_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'o' :
		ek = 'ok';
	else :
		ek = 'ek';

	forms['pres.p3.m.sg'] = doubled_consonant_forms ('ji' + r[0] + 't' + v[1] + r[1] + r[2], 'ji' + r[0] + 't' + v[1] + r[1] + r[2], '-', ek, tv); 
	forms['pres.p3.f.sg'] = doubled_consonant_forms ('ti' + r[0] + 't' + v[1] + r[1] + r[2], 'ti' + r[0] + 't' + v[1] + r[1] + r[2], '-', ek, tv);	
	forms['pres.p2.mf.sg'] = doubled_consonant_forms ('ti' + r[0] + 't' + v[1] + r[1] + r[2], 'ti' + r[0] + 't' + v[1] + r[1] + r[2], '-', ek, tv);	
	forms['pres.p1.mf.sg'] = doubled_consonant_forms ('ni' + r[0] + 't' + v[1] + r[1] + r[2], 'ni' + r[0] + 't' + v[1] + r[1] + r[2], '-', ek, tv);	

	forms['pres.p3.mf.pl'] = doubled_vowel_forms ('ji' + r[0] + 't' + v[1] + r[1] + r[2] + 'u', 'ji' + r[0] + 't' + v[1] + r[1] + r[2] + 'u', '-', tv);
	forms['pres.p2.mf.pl'] = doubled_vowel_forms ('ti' + r[0] + 't' + v[1] + r[1] + r[2] + 'u', 'ti' + r[0] + 't' + v[1] + r[1] + r[2] + 'u', '-', tv);
	forms['pres.p1.mf.pl'] = doubled_vowel_forms ('ni' + r[0] + 't' + v[1] + r[1] + r[2] + 'u', 'ni' + r[0] + 't' + v[1] + r[1] + r[2] + 'u', '-', tv);


	return forms;
#}


# ek/ok done
def doubled_patt8a_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'o' :
		ek = 'ok';
	else :
		ek = 'ek';	

	suffix = 'u'

 	forms['imp.p2.mf.sg'] = doubled_consonant_forms ('i' + r[0] + 't' + v[1] + r[1] + r[2], 'i' + r[0] + 't' + v[1] + r[1] + r[2], '-', ek, tv);
	forms['imp.p2.mf.pl'] = doubled_vowel_forms ('i' + r[0] + 't' + v[1] + r[1] + r[2] + suffix , 'i' + r[0] + 't' + v[1] + r[1] + r[2] + suffix , '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 8b
## ----------------------------------------------------------------------------##


# no ek/ok?
def doubled_patt8b_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};
	ek = 'ek';


# TODO: LR forms for some verbs only

	forms['past.p3.m.sg'] = doubled_consonant_forms ('n' + r[0] + 't' + v[0] + r[1] + r[2], 'n' + r[0] + 't' + v[0] + r[1] + r[2], '-', ek, tv);
	forms['past.p3.m.sg'] += doubled_consonant_forms ('in' + r[0] + 't' + v[0] + r[1] + r[2], 'in' + r[0] + 't' + v[0] + r[1] + r[2], 'LR', ek, tv);
	forms['past.p3.f.sg'] = doubled_consonant_forms ('n' + r[0] + 't' + v[0] + r[1] + r[2] + 'et', 'n' + r[0] + 't' + v[0] + r[1] + r[2] + 'it', '-', ek, tv);
	forms['past.p3.f.sg'] += doubled_consonant_forms ('in' + r[0] + 't' + v[0] + r[1] + r[2] + 'et', 'in' + r[0] + 't' + v[0] + r[1] + r[2] + 'it', 'LR', ek, tv);
	forms['past.p2.mf.sg'] = doubled_consonant_forms ('n' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', 'n' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', '-', ek, tv);
	forms['past.p2.mf.sg'] += doubled_consonant_forms ('in' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', 'in' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', 'LR', ek, tv);
	forms['past.p1.mf.sg'] = doubled_consonant_forms ('n' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', 'n' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', '-', ek, tv);
	forms['past.p1.mf.sg'] += doubled_consonant_forms ('in' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', 'in' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejt', 'LR', ek, tv);
	forms['past.p3.mf.pl'] = doubled_vowel_forms ('n' + r[0] + 't' + v[0] + r[1] + r[2] + 'u', 'n' + r[0] + 't' + v[0] + r[1] + r[2] + 'u', '-', tv);
	forms['past.p3.mf.pl'] += doubled_vowel_forms ('in' + r[0] + 't' + v[0] + r[1] + r[2] + 'ew', 'in' + r[0] + 't' + v[0] + r[1] + r[2] + 'ew', 'LR', tv);
	forms['past.p2.mf.pl'] = doubled_vowel_forms ('n' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejtu', 'n' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejtu', '-', tv);
	forms['past.p2.mf.pl'] += doubled_vowel_forms ('in' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejtu', 'in' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejtu', 'LR', tv);
	forms['past.p1.mf.pl'] = doubled_vowel_forms ('n' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejna', 'n' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejna', '-', tv);
	forms['past.p1.mf.pl'] += doubled_vowel_forms ('in' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejna', 'in' + r[0] + 't' + v[0] + r[1] + r[2] + 'ejna', 'LR', tv);

	return forms;
#}


# ek/ok done
def doubled_patt8b_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'o' :
		ek = 'ok';
	else :
		ek = 'ek';

	forms['pres.p3.m.sg'] = doubled_consonant_forms ('jin' + r[0] + 't' + v[1] + r[1] + r[2], 'jin' + r[0] + 't' + v[1] + r[1] + r[2], '-', ek, tv); 
	forms['pres.p3.f.sg'] = doubled_consonant_forms ('tin' + r[0] + 't' + v[1] + r[1] + r[2], 'tin' + r[0] + 't' + v[1] + r[1] + r[2], '-', ek, tv);	
	forms['pres.p2.mf.sg'] = doubled_consonant_forms ('tin' + r[0] + 't' + v[1] + r[1] + r[2], 'tin' + r[0] + 't' + v[1] + r[1] + r[2], '-', ek, tv);	
	forms['pres.p1.mf.sg'] = doubled_consonant_forms ('nin' + r[0] + 't' + v[1] + r[1] + r[2], 'nin' + r[0] + 't' + v[1] + r[1] + r[2], '-', ek, tv);	

	forms['pres.p3.mf.pl'] = doubled_vowel_forms ('jin' + r[0] + 't' + v[1] + r[1] + r[2] + 'u', 'jin' + r[0] + 't' + v[1] + r[1] + r[2] + 'u', '-', tv);
	forms['pres.p2.mf.pl'] = doubled_vowel_forms ('tin' + r[0] + 't' + v[1] + r[1] + r[2] + 'u', 'tin' + r[0] + 't' + v[1] + r[1] + r[2] + 'u', '-', tv);
	forms['pres.p1.mf.pl'] = doubled_vowel_forms ('nin' + r[0] + 't' + v[1] + r[1] + r[2] + 'u', 'nin' + r[0] + 't' + v[1] + r[1] + r[2] + 'u', '-', tv);


	return forms;
#}


# ek/ok done
def doubled_patt8b_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'o' :
		ek = 'ok';
	else :
		ek = 'ek';	

	suffix = 'u'

 	forms['imp.p2.mf.sg'] = doubled_consonant_forms ('in' + r[0] + 't' + v[1] + r[1] + r[2], 'in' + r[0] + 't' + v[1] + r[1] + r[2], '-', ek, tv);
	forms['imp.p2.mf.pl'] = doubled_vowel_forms ('in' + r[0] + 't' + v[1] + r[1] + r[2] + suffix , 'in' + r[0] + 't' + v[1] + r[1] + r[2] + suffix , '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##


def main(stem): #{

	forms = {};

	if stem['theme'] == '1' : #{

		forms = doubled_patt1_past(stem['root'], stem['vowel_perf'], stem['trans']);
		if stem['vowel_impf'] != '' : #{
			forms.update(doubled_patt1_pres(stem['root'], stem['vowel_impf'], stem['trans']));
		#	if stem['trans'] == 'tv':
			forms.update(doubled_patt1_imp(stem['root'], stem['vowel_impf'], stem['trans']));
		#}
		if stem['pp'] != '' : #{
			forms.update(doubled_patt1_pp(stem['root'], stem['vowel_perf'], stem['pp']));
		#}


	if stem['theme'] == '7a' : #{

		forms = doubled_patt7a_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(doubled_patt7a_pres(stem['root'], stem['vowel_perf'], stem['trans']));
		forms.update(doubled_patt7a_imp(stem['root'], stem['vowel_perf'], stem['trans']));

	#}


	if stem['theme'] == '7b' : #{

		forms = doubled_patt7b_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(doubled_patt7b_pres(stem['root'], stem['vowel_perf'], stem['trans']));
		forms.update(doubled_patt7b_imp(stem['root'], stem['vowel_perf'], stem['trans']));

	#}


	if stem['theme'] == '8a' : #{

		forms = doubled_patt8a_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(doubled_patt8a_pres(stem['root'], stem['vowel_perf'], stem['trans']));
		forms.update(doubled_patt8a_imp(stem['root'], stem['vowel_perf'], stem['trans']));

	#}


	if stem['theme'] == '8b' : #{

		forms = doubled_patt8b_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(doubled_patt8b_pres(stem['root'], stem['vowel_perf'], stem['trans']));
		forms.update(doubled_patt8b_imp(stem['root'], stem['vowel_perf'], stem['trans']));

	#}


	return forms;

#}
