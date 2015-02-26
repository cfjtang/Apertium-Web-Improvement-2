#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-


## ----------------------------------------------------------------------------##
## weak
## ----------------------------------------------------------------------------##


# no ek/ok
def weak_consonant_forms (form_sg, form_sg_suff, r, tv): #{
# only past.p3.sg.f have different forms with and without suffixes

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


def weak_vowel_forms (form_pl, form_pl_suff, r, tv): #{
# different with/without suffix forms only for past.p1.mf.pl

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


def weak_patt1_pprs(root): #{
	r = root.split('-'); # radicals

	forms = {};
	
	forms['pprs.m.sg'] = [(r[0] + 'ie' + r[1] + 'i', '-', '-')] ;
	forms['pprs.f.sg'] = [(r[0] + 'ie' + r[1] + 'ja', '-', '-')] ;
	forms['pprs.mf.pl'] = [(r[0] + 'e' + r[1] + 'jin', '-', '-')] ;

	return forms;
#}


def weak_patt1_pp(root, vowels, pref): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};
	
	forms['pp.m.sg'] = [(pref + r[0] + r[1] + 'i', '-', '-')] ;
	forms['pp.f.sg'] = [(pref + r[0] + r[1] + 'ija', '-', '-')] ;
	forms['pp.mf.pl'] = [(pref + r[0] + r[1] + 'ijin', '-', '-')] ;

	return forms;
#}



def weak_patt1_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[0] == 'e' and v[1] == 'a': #{

		forms['past.p3.m.sg'] = weak_vowel_forms(r[0] + v[0] + r[1] + v[1], r[0] + r[1] + 'ie', '-', tv);

		# This form is obtained by the elision of the first vowel of the 
		# stem and the change of the second vowel to 'ie' (for e-a) and
		# 'a' for (a-a)
		# rmiet - ma rmitx? or ma rmietx?
		forms['past.p3.f.sg'] = weak_consonant_forms(r[0] + r[1] + 'iet', r[0] + r[1] + 'it', '-', tv);
	#}
	else : #{
		forms['past.p3.m.sg'] = weak_vowel_forms(r[0] + v[0] + r[1] + v[1], r[0] + r[1] + 'a', '-', tv);
		# qratu? or qritu?
		forms['past.p3.f.sg'] = weak_consonant_forms(r[0] + r[1] + 'at', r[0] + r[1] + 'at', '-', tv);
	#}

	# This form is obtained by omission of the two vowels in the stem
	# and addition of the dipthong 'ej' (for e-a) and 'aj' (for a-a)
	forms['past.p2.mf.sg'] = weak_consonant_forms(r[0] + r[1] + v[0] + 'jt', r[0] + r[1] + v[0] + 'jt', '-', tv);	
	forms['past.p1.mf.sg'] = weak_consonant_forms(r[0] + r[1] + v[0] + 'jt', r[0] + r[1] + v[0] + 'jt', '-', tv);	

	# This form is obtained by omission of the two vowels in the vocalic
	# sequence + dipthong 'ew' or 'aw'
	# before suffixed pronouns, 'ew' and 'aw' are treated like vowels
	forms['past.p3.mf.pl'] = weak_vowel_forms(r[0] + r[1] + v[0] + 'w', r[0] + r[1] + v[0] + 'w', '-', tv);
	forms['past.p2.mf.pl'] = weak_vowel_forms(r[0] + r[1] + v[0] + 'jtu', r[0] + r[1] + v[0] + 'jtu', '-', tv);
	forms['past.p1.mf.pl'] = weak_vowel_forms(r[0] + r[1] + v[0] + 'jna', r[0] + r[1] + v[0] + 'jnie', '-', tv);

	return forms;
#}


def weak_patt1_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_vowel = v[1];
	# That's only my guess
	if v[0] == 'e' and v[1] == 'a': 
		presuff_vowel = 'ie';

	# no first vowel elision in forms with suffixes? 
	forms['pres.p3.m.sg'] = weak_vowel_forms('j' + v[0] + r[0] + r[1] + v[1] , 'j' + v[0] + r[0] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p3.f.sg'] = weak_vowel_forms('t' + v[0] + r[0] + r[1] + v[1] , 't' + v[0] + r[0] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p2.mf.sg'] = weak_vowel_forms('t' + v[0] + r[0] + r[1] + v[1] , 't' + v[0] + r[0] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p1.mf.sg'] = weak_vowel_forms('n' + v[0] + r[0] + r[1] + v[1] , 'n' + v[0] + r[0] + r[1] + presuff_vowel , '-', tv);
	
	if vowels == 'a-a': #{
		suffix =  'aw';
	elif v[1] == 'i' :
		suffix = 'u';
	else :
#	elif vowels == 'e-a' or vowels == 'i-a' : #{   
		suffix =  'ew';
	#}
										# QaRaJ		BeDaJ		MeXaJ	ReMaJ
										# a-a		i-a		i-i	a-i
	forms['pres.p3.mf.pl'] = weak_vowel_forms('j' + v[0] + r[0] + r[1] + suffix, 'j' + v[0] + r[0] + r[1] + suffix, '-', tv);	# j-aQRa-w	j-iBDe-w	j-iMXu	j-aRMu
	forms['pres.p2.mf.pl'] = weak_vowel_forms('t' + v[0] + r[0] + r[1] + suffix, 't' + v[0] + r[0] + r[1] + suffix, '-', tv);	# t-aQRa-w
	forms['pres.p1.mf.pl'] = weak_vowel_forms('n' + v[0] + r[0] + r[1] + suffix, 'n' + v[0] + r[0] + r[1] + suffix, '-', tv);	# n-aQRa-w

	return forms;
#}


def weak_patt1_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_sg_vowel = v[1];
	# That's only my guess
	if v[0] == 'e' and v[1] == 'a': 
		presuff_sg_vowel = 'ie';
	
	if vowels == 'a-a': #{
		pl_suffix =  'aw';
	elif v[1] == 'i' :
		pl_suffix = 'u'
	else :
#	elif vowels == 'e-a': #{    # 'i-a'?
		pl_suffix =  'ew';

	forms['imp.p2.mf.sg'] = weak_vowel_forms(v[0] + r[0] + r[1] + v[1] , v[0] + r[0] + r[1] + presuff_sg_vowel , '-', tv);
	forms['imp.p2.mf.pl'] = weak_vowel_forms(v[0] + r[0] + r[1] + pl_suffix , v[0] + r[0] + r[1] + pl_suffix , '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 2
## ----------------------------------------------------------------------------##


def weak_patt2_pprs(root, pp_v): #{
	r = root.split('-'); # radicals

	forms = {};

# sic!	
# also, it's not always 'e' - e.g. 'ħalla' and 'mħolli'

	forms['pp.m.sg'] = [('m' + r[0] + pp_v + r[1] + r[1] + 'i', '-', '-'), ('m' + r[0] + 'e' + r[1] + r[1] + 'i', '-', 'LR')] ;
	forms['pp.f.sg'] = [('m' + r[0] + pp_v + r[1] + r[1] + 'ija', '-', '-'), ('m' + r[0] + 'e' + r[1] + r[1] + 'ija', '-', 'LR')] ;
	forms['pp.mf.pl'] = [('m' + r[0] + pp_v + r[1] + r[1] + 'ijin', '-', '-'), ('m' + r[0] + 'e' + r[1] + r[1] + 'ijin', '-', 'LR')] ;

	return forms;
#}


def weak_patt2_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	forms['past.p3.m.sg'] = weak_vowel_forms(r[0] + v[0] + r[1] + r[1] + v[1], r[0] + v[0] + r[1] + r[1] + 'ie', '-', tv);
	forms['past.p3.f.sg'] = weak_consonant_forms(r[0] + v[0] + r[1] + r[1] + 'iet', r[0] + v[0] + r[1] + r[1] + 'it', '-', tv);
	forms['past.p2.mf.sg'] = weak_consonant_forms(r[0] + v[0] + r[1] + r[1] + 'ejt', r[0] + v[0] + r[1] + r[1] + 'ejt', '-', tv);	
	forms['past.p1.mf.sg'] = weak_consonant_forms(r[0] + v[0] + r[1] + r[1] + 'ejt', r[0] + v[0] + r[1] + r[1] + 'ejt', '-', tv);	

	forms['past.p3.mf.pl'] = weak_vowel_forms(r[0] + v[0] + r[1] + r[1] + 'ew', r[0] + v[0] + r[1] + r[1] + 'ew', '-', tv);
	forms['past.p2.mf.pl'] = weak_vowel_forms(r[0] + v[0] + r[1] + r[1] + 'ejtu', r[0] + v[0] + r[1] + r[1] + 'ejtu', '-', tv);
	forms['past.p1.mf.pl'] = weak_vowel_forms(r[0] + v[0] + r[1] + r[1] + 'ejna', r[0] + v[0] + r[1] + r[1] + 'ejnie', '-', tv);

	return forms;
#}


def weak_patt2_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_vowel = v[1];

	# no first vowel elision in forms with suffixes? 
	forms['pres.p3.m.sg'] = weak_vowel_forms('i' + r[0] + v[0] + r[1] + r[1] + v[1] , 'i' + r[0] + v[0] + r[1] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p3.m.sg'] += weak_vowel_forms('j' + r[0] + v[0] + r[1] + r[1] + v[1] , 'j' + r[0] + v[0] + r[1] + r[1] + presuff_vowel , 'LR', tv);
	forms['pres.p3.f.sg'] = weak_vowel_forms('t' + r[0] + v[0] + r[1] + r[1] + v[1] , 't' + r[0] + v[0] + r[1] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p2.mf.sg'] = weak_vowel_forms('t' + r[0] + v[0] + r[1] + r[1] + v[1] , 't' + r[0] + v[0] + r[1] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p1.mf.sg'] = weak_vowel_forms('in' + r[0] + v[0] + r[1] + r[1] + v[1] , 'in' + r[0] + v[0] + r[1] + r[1] + presuff_vowel , '-', tv);
	
	suffix = 'u'

	forms['pres.p3.mf.pl'] = weak_vowel_forms('i' + r[0] + v[0] + r[1] + r[1] + suffix, 'i' + r[0] + v[0] + r[1] + r[1] + suffix, '-', tv);
	forms['pres.p3.mf.pl'] += weak_vowel_forms('j' + r[0] + v[0] + r[1] + r[1] + suffix, 'j' + r[0] + v[0] + r[1] + r[1] + suffix, 'LR', tv);
	forms['pres.p2.mf.pl'] = weak_vowel_forms('t' + r[0] + v[0] + r[1] + r[1] + suffix, 't' + r[0] + v[0] + r[1] + r[1] + suffix, '-', tv);	
	forms['pres.p1.mf.pl'] = weak_vowel_forms('in' + r[0] + v[0] + r[1] + r[1] + suffix, 'in' + r[0] + v[0] + r[1] + r[1] + suffix, '-', tv);

	return forms;
#}


def weak_patt2_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_sg_vowel = v[1];
	
	pl_suffix = 'u'

	forms['imp.p2.mf.sg'] = weak_vowel_forms(r[0] + v[0] + r[1] + r[1] + v[1], r[0] + v[0] + r[1] + r[1] + presuff_sg_vowel , '-', tv);
	forms['imp.p2.mf.pl'] = weak_vowel_forms(r[0] + v[0] + r[1] + r[1] + pl_suffix , r[0] + v[0] + r[1] + r[1] + pl_suffix , '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 5
## ----------------------------------------------------------------------------##


def weak_patt5_pprs(root): #{
	r = root.split('-'); # radicals

	forms = {};
	
	forms['pp.m.sg'] = [('m' + r[0] + 'e' + r[1] + r[1] + 'i', '-', '-'), ('m' + r[0] + 'e' + r[1] + r[1] + 'i', '-', 'LR')] ;
	forms['pp.f.sg'] = [('m' + r[0] + 'e' + r[1] + r[1] + 'ija', '-', '-'), ('m' + r[0] + 'e' + r[1] + r[1] + 'ija', '-', 'LR')] ;
	forms['pp.mf.pl'] = [('m' + r[0] + 'e' + r[1] + r[1] + 'ijin', '-', '-'), ('m' + r[0] + 'e' + r[1] + r[1] + 'ijin', '-', 'LR')] ;

	return forms;
#}


def weak_patt5_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

        prefix_cons = 't';
        if (r[0] == 'ċ' or r[0] == 'd' or r[0] == 'ġ' or r[0] == 'n' or r[0] == 's' or r[0] == 'x'  or r[0] == 'ż' or r[0] == 'z') : #{
        # assimilation
                prefix_cons = 'i' + r[0];


	forms['past.p3.m.sg'] = weak_vowel_forms(prefix_cons + r[0] + v[0] + r[1] + r[1] + v[1], prefix_cons + r[0] + v[0] + r[1] + r[1] + 'ie', '-', tv);
	forms['past.p3.f.sg'] = weak_consonant_forms(prefix_cons + r[0] + v[0] + r[1] + r[1] + 'iet', prefix_cons + r[0] + v[0] + r[1] + r[1] + 'it', '-', tv);
	forms['past.p2.mf.sg'] = weak_consonant_forms(prefix_cons + r[0] + v[0] + r[1] + r[1] + 'ejt', prefix_cons + r[0] + v[0] + r[1] + r[1] + 'ejt', '-', tv);	
	forms['past.p1.mf.sg'] = weak_consonant_forms(prefix_cons + r[0] + v[0] + r[1] + r[1] + 'ejt', prefix_cons + r[0] + v[0] + r[1] + r[1] + 'ejt', '-', tv);	

	forms['past.p3.mf.pl'] = weak_vowel_forms(prefix_cons + r[0] + v[0] + r[1] + r[1] + 'ew', prefix_cons + r[0] + v[0] + r[1] + r[1] + 'ew', '-', tv);
	forms['past.p2.mf.pl'] = weak_vowel_forms(prefix_cons + r[0] + v[0] + r[1] + r[1] + 'ejtu', prefix_cons + r[0] + v[0] + r[1] + r[1] + 'ejtu', '-', tv);
	forms['past.p1.mf.pl'] = weak_vowel_forms(prefix_cons + r[0] + v[0] + r[1] + r[1] + 'ejna', prefix_cons + r[0] + v[0] + r[1] + r[1] + 'ejnie', '-', tv);

	return forms;
#}


def weak_patt5_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

        prefix_cons = 't';
        if (r[0] == 'ċ' or r[0] == 'd' or r[0] == 'ġ' or r[0] == 'n' or r[0] == 
's' or r[0] == 'x'  or r[0] == 'ż' or r[0] == 'z') : #{
        # assimilation
                prefix_cons = r[0];


	presuff_vowel = v[1];
	if v[1] == 'a': 
		presuff_vowel = 'ie';

	# no first vowel elision in forms with suffixes? 
	forms['pres.p3.m.sg'] = weak_vowel_forms('ji' + prefix_cons + r[0] + v[0] + r[1] + r[1] + v[1] , 'ji' + prefix_cons + r[0] + v[0] + r[1] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p3.f.sg'] = weak_vowel_forms('ti' + prefix_cons + r[0] + v[0] + r[1] + r[1] + v[1] , 'ti' + prefix_cons + r[0] + v[0] + r[1] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p2.mf.sg'] = weak_vowel_forms('ti' + prefix_cons + r[0] + v[0] + r[1] + r[1] + v[1] , 'ti' + prefix_cons + r[0] + v[0] + r[1] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p1.mf.sg'] = weak_vowel_forms('ni' + prefix_cons + r[0] + v[0] + r[1] + r[1] + v[1] , 'ni' + prefix_cons + r[0] + v[0] + r[1] + r[1] + presuff_vowel , '-', tv);
	
	if vowels == 'a-a': 
		suffix =  'aw';
	elif v[1] == 'i' :
		suffix = 'u'
	else : 
		suffix =  'ew';

	forms['pres.p3.mf.pl'] = weak_vowel_forms('ji' + prefix_cons + r[0] + v[0] + r[1] + r[1] + suffix, 'ji' + prefix_cons + r[0] + v[0] + r[1] + r[1] + suffix, '-', tv);
	forms['pres.p2.mf.pl'] = weak_vowel_forms('ti' + prefix_cons + r[0] + v[0] + r[1] + r[1] + suffix, 'ti' + prefix_cons + r[0] + v[0] + r[1] + r[1] + suffix, '-', tv);	
	forms['pres.p1.mf.pl'] = weak_vowel_forms('ni' + prefix_cons + r[0] + v[0] + r[1] + r[1] + suffix, 'ni' + prefix_cons + r[0] + v[0] + r[1] + r[1] + suffix, '-', tv);

	return forms;
#}


def weak_patt5_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

        prefix_cons = 't';
        if (r[0] == 'ċ' or r[0] == 'd' or r[0] == 'ġ' or r[0] == 'n' or r[0] == 's' or r[0] == 'x'  or r[0] == 'ż' or r[0] == 'z') : #{
        # assimilation
                prefix_cons = r[0];

	presuff_sg_vowel = v[1];
	if v[1] == 'a': 
		presuff_sg_vowel = 'ie';


	if vowels == 'a-a': 
		pl_suffix =  'aw';
	elif v[1] == 'i' :
		pl_suffix = 'u'
	else : 
		pl_suffix =  'ew';


	forms['imp.p2.mf.sg'] = weak_vowel_forms(prefix_cons + r[0] + v[0] + r[1] + r[1] + v[1], 't' + r[0] + v[0] + r[1] + r[1] + presuff_sg_vowel , '-', tv);
	forms['imp.p2.mf.pl'] = weak_vowel_forms(prefix_cons + r[0] + v[0] + r[1] + r[1] + pl_suffix , 't' + r[0] + v[0] + r[1] + r[1] + pl_suffix , '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 7a
## ----------------------------------------------------------------------------##


def weak_patt7a_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[0] == 'e' and v[1] == 'a': #{

		forms['past.p3.m.sg'] = weak_vowel_forms('n' + r[0] + v[0] + r[1] + v[1], 'n' + r[0] + r[1] + 'ie', '-', tv);
		forms['past.p3.m.sg'] += weak_vowel_forms('in' + r[0] + v[0] + r[1] + v[1], 'in' + r[0] + r[1] + 'ie', 'LR', tv);
		forms['past.p3.f.sg'] = weak_consonant_forms('n' + r[0] + r[1] + 'iet', 'n' + r[0] + r[1] + 'it', '-', tv);
		forms['past.p3.f.sg'] += weak_consonant_forms('in' + r[0] + r[1] + 'iet', 'in' + r[0] + r[1] + 'it', 'LR', tv);
	#}
	else : #{
		forms['past.p3.m.sg'] = weak_vowel_forms('n' + r[0] + v[0] + r[1] + v[1], 'n' + r[0] + r[1] + 'a', '-', tv);
		forms['past.p3.m.sg'] += weak_vowel_forms('in' + r[0] + v[0] + r[1] + v[1], 'in' + r[0] + r[1] + 'a', 'LR', tv);
		forms['past.p3.f.sg'] = weak_consonant_forms('n' + r[0] + r[1] + 'at', 'n' + r[0] + r[1] + 'at', '-', tv);
		forms['past.p3.f.sg'] += weak_consonant_forms('in' + r[0] + r[1] + 'at', 'in' + r[0] + r[1] + 'at', 'LR', tv);
	#}

	# This form is obtained by the omission of the two vowels in the stem
	# and the addition of the dipthong 'ej' (for e-a) and 'aj' (for a-a)
	forms['past.p2.mf.sg'] = weak_consonant_forms('n' + r[0] + r[1] + v[0] + 'jt', 'n' + r[0] + r[1] + v[0] + 'jt', '-', tv);	
	forms['past.p2.mf.sg'] += weak_consonant_forms('in' + r[0] + r[1] + v[0] + 'jt', 'in' + r[0] + r[1] + v[0] + 'jt', 'LR', tv);	
	forms['past.p1.mf.sg'] = weak_consonant_forms('n' + r[0] + r[1] + v[0] + 'jt', 'n' + r[0] + r[1] + v[0] + 'jt', '-', tv);	
	forms['past.p1.mf.sg'] += weak_consonant_forms('in' + r[0] + r[1] + v[0] + 'jt', 'in' + r[0] + r[1] + v[0] + 'jt', 'LR', tv);	

	# This form is obtained by the omission of the two vowels in the vocalic
	# sequence + dipthong 'ew' or 'aw'
	# with attached pronouns 'ew' and 'aw'
	# are also treated like vowels
	forms['past.p3.mf.pl'] = weak_vowel_forms('n' + r[0] + r[1] + v[0] + 'w', 'n' + r[0] + r[1] + v[0] + 'w', '-', tv);
	forms['past.p3.mf.pl'] += weak_vowel_forms('in' + r[0] + r[1] + v[0] + 'w', 'in' + r[0] + r[1] + v[0] + 'w', 'LR', tv);
	forms['past.p2.mf.pl'] = weak_vowel_forms('n' + r[0] + r[1] + v[0] + 'jtu', 'n' + r[0] + r[1] + v[0] + 'jtu', '-', tv);
	forms['past.p2.mf.pl'] += weak_vowel_forms('in' + r[0] + r[1] + v[0] + 'jtu', 'in' + r[0] + r[1] + v[0] + 'jtu', 'LR', tv);
	forms['past.p1.mf.pl'] = weak_vowel_forms('n' + r[0] + r[1] + v[0] + 'jna', 'n' + r[0] + r[1] + v[0] + 'jnie', '-', tv);
	forms['past.p1.mf.pl'] += weak_vowel_forms('in' + r[0] + r[1] + v[0] + 'jna', 'in' + r[0] + r[1] + v[0] + 'jnie', 'LR', tv);

	return forms;
#}


def weak_patt7a_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_vowel = v[1];
	# That's only my guess
	if v[0] == 'e' and v[1] == 'a': 
		presuff_vowel = 'ie';

	# no first vowel elision in forms with suffixes? 
	forms['pres.p3.m.sg'] = weak_vowel_forms('jin' + r[0] + v[0] + r[1] + v[1] , 'jin' + r[0] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p3.f.sg'] = weak_vowel_forms('tin' + r[0] + v[0] + r[1] + v[1] , 'tin' + r[0] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p2.mf.sg'] = weak_vowel_forms('tin' + r[0] + v[0] + r[1] + v[1] , 'tin' + r[0] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p1.mf.sg'] = weak_vowel_forms('nin' + r[0] + v[0] + r[1] + v[1] , 'nin' + r[0] + r[1] + presuff_vowel , '-', tv);
	
	if vowels == 'a-a':
		suffix =  'aw';
	elif v[1] == 'i' :
		suffix = 'u'
	else:
		suffix =  'ew';

	forms['pres.p3.mf.pl'] = weak_vowel_forms('jin' + r[0] + r[1] + suffix, 'jin' + r[0] + r[1] + suffix, '-', tv);	
	forms['pres.p2.mf.pl'] = weak_vowel_forms('tin' + r[0] + r[1] + suffix, 'tin' + r[0] + r[1] + suffix, '-', tv);	
	forms['pres.p1.mf.pl'] = weak_vowel_forms('nin' + r[0] + r[1] + suffix, 'nin' + r[0] + r[1] + suffix, '-', tv);	

	return forms;
#}


def weak_patt7a_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_sg_vowel = v[1];
	# That's only my guess
	if v[0] == 'e' and v[1] == 'a': 
		presuff_sg_vowel = 'ie';

	if vowels == 'a-a':
		pl_suffix =  'aw';
	elif v[1] == 'i' :
		pl_suffix = 'u'
	else:
		pl_suffix =  'ew';
	
	# also 'n' prefix/only 'n' or 'in' prefix?
	forms['imp.p2.mf.sg'] = weak_vowel_forms('in' + r[0] + v[0] + r[1] + v[1] , 'in' + r[0] + v[0] + r[1] + presuff_sg_vowel , '-', tv);
	forms['imp.p2.mf.pl'] = weak_vowel_forms('in' + r[0] + v[0] + r[1] + pl_suffix , 'in' + r[0] + v[0] + r[1] + pl_suffix , '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 7b
## ----------------------------------------------------------------------------##


def weak_patt7b_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[0] == 'e' and v[1] == 'a': #{

		forms['past.p3.m.sg'] = weak_vowel_forms('nt' + r[0] + v[0] + r[1] + v[1], 'nt' + r[0] + r[1] + 'ie', '-', tv);
		forms['past.p3.m.sg'] += weak_vowel_forms('int' + r[0] + v[0] + r[1] + v[1], 'int' + r[0] + r[1] + 'ie', 'LR', tv);
		forms['past.p3.f.sg'] = weak_consonant_forms('nt' + r[0] + r[1] + 'iet', 'nt' + r[0] + r[1] + 'it', '-', tv);
		forms['past.p3.f.sg'] += weak_consonant_forms('int' + r[0] + r[1] + 'iet', 'int' + r[0] + r[1] + 'it', 'LR', tv);
	#}
	else : #{
		forms['past.p3.m.sg'] = weak_vowel_forms('nt' + r[0] + v[0] + r[1] + v[1], 'nt' + r[0] + r[1] + 'a', '-', tv);
		forms['past.p3.m.sg'] += weak_vowel_forms('int' + r[0] + v[0] + r[1] + v[1], 'int' + r[0] + r[1] + 'a', 'LR', tv);
		forms['past.p3.f.sg'] = weak_consonant_forms('nt' + r[0] + r[1] + 'at', 'nt' + r[0] + r[1] + 'at', '-', tv);
		forms['past.p3.f.sg'] += weak_consonant_forms('int' + r[0] + r[1] + 'at', 'int' + r[0] + r[1] + 'at', 'LR', tv);
	#}

	# This form is obtained by the omission of the two vowels in the stem
	# and the addition of the dipthong 'ej' (for e-a) and 'aj' (for a-a)
	forms['past.p2.mf.sg'] = weak_consonant_forms('nt' + r[0] + r[1] + v[0] + 'jt', 'nt' + r[0] + r[1] + v[0] + 'jt', '-', tv);	
	forms['past.p2.mf.sg'] += weak_consonant_forms('int' + r[0] + r[1] + v[0] + 'jt', 'int' + r[0] + r[1] + v[0] + 'jt', 'LR', tv);	
	forms['past.p1.mf.sg'] = weak_consonant_forms('nt' + r[0] + r[1] + v[0] + 'jt', 'nt' + r[0] + r[1] + v[0] + 'jt', '-', tv);	
	forms['past.p1.mf.sg'] += weak_consonant_forms('int' + r[0] + r[1] + v[0] + 'jt', 'int' + r[0] + r[1] + v[0] + 'jt', 'LR', tv);	

	# This form is obtained by the omission of the two vowels in the vocalic
	# sequence + dipthong 'ew' or 'aw'
	# with attached pronouns 'ew' and 'aw'
	# are also treated like vowels
	forms['past.p3.mf.pl'] = weak_vowel_forms('nt' + r[0] + r[1] + v[0] + 'w', 'nt' + r[0] + r[1] + v[0] + 'w', '-', tv);
	forms['past.p3.mf.pl'] += weak_vowel_forms('int' + r[0] + r[1] + v[0] + 'w', 'int' + r[0] + r[1] + v[0] + 'w', 'LR', tv);
	forms['past.p2.mf.pl'] = weak_vowel_forms('nt' + r[0] + r[1] + v[0] + 'jtu', 'nt' + r[0] + r[1] + v[0] + 'jtu', '-', tv);
	forms['past.p2.mf.pl'] += weak_vowel_forms('int' + r[0] + r[1] + v[0] + 'jtu', 'int' + r[0] + r[1] + v[0] + 'jtu', 'LR', tv);
	forms['past.p1.mf.pl'] = weak_vowel_forms('nt' + r[0] + r[1] + v[0] + 'jna', 'nt' + r[0] + r[1] + v[0] + 'jnie', '-', tv);
	forms['past.p1.mf.pl'] += weak_vowel_forms('int' + r[0] + r[1] + v[0] + 'jna', 'int' + r[0] + r[1] + v[0] + 'jnie', 'LR', tv);

	return forms;
#}


def weak_patt7b_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_vowel = v[1];
	# That's only my guess
	if v[0] == 'e' and v[1] == 'a': 
		presuff_vowel = 'ie';

	# no first vowel elision in forms with suffixes? 
	forms['pres.p3.m.sg'] = weak_vowel_forms('jint' + r[0] + v[0] + r[1] + v[1] , 'jint' + r[0] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p3.f.sg'] = weak_vowel_forms('tint' + r[0] + v[0] + r[1] + v[1] , 'tint' + r[0] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p2.mf.sg'] = weak_vowel_forms('tint' + r[0] + v[0] + r[1] + v[1] , 'tint' + r[0] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p1.mf.sg'] = weak_vowel_forms('nint' + r[0] + v[0] + r[1] + v[1] , 'nint' + r[0] + r[1] + presuff_vowel , '-', tv);

	if vowels == 'a-a':
		suffix =  'aw';
	elif v[1] == 'i' :
		suffix = 'u'
	else:
		suffix =  'ew';
	

	forms['pres.p3.mf.pl'] = weak_vowel_forms('jint' + r[0] + r[1] + suffix, 'jint' + r[0] + r[1] + suffix, '-', tv);	
	forms['pres.p2.mf.pl'] = weak_vowel_forms('tint' + r[0] + r[1] + suffix, 'tint' + r[0] + r[1] + suffix, '-', tv);	
	forms['pres.p1.mf.pl'] = weak_vowel_forms('nint' + r[0] + r[1] + suffix, 'nint' + r[0] + r[1] + suffix, '-', tv);	

	return forms;
#}


def weak_patt7b_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_sg_vowel = v[1];
	# That's only my guess
	if v[0] == 'e' and v[1] == 'a': 
		presuff_sg_vowel = 'ie';

	if vowels == 'a-a':
		pl_suffix =  'aw';
	elif v[1] == 'i' :
		pl_suffix = 'u'
	else:
		pl_suffix =  'ew';
	

	# also 'nt' prefix/only 'nt' or 'int' prefix?
	forms['imp.p2.mf.sg'] = weak_vowel_forms('int' + r[0] + v[0] + r[1] + v[1] , 'int' + r[0] + v[0] + r[1] + presuff_sg_vowel , '-', tv);
	forms['imp.p2.mf.pl'] = weak_vowel_forms('int' + r[0] + v[0] + r[1] + pl_suffix , 'int' + r[0] + v[0] + r[1] + pl_suffix , '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 8a
## ----------------------------------------------------------------------------##


def weak_patt8a_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

# TODO: only some verbs with additional 'i' ?

	if v[0] == 'e' and v[1] == 'a': #{

		forms['past.p3.m.sg'] = weak_vowel_forms(r[0] + 't' + v[0] + r[1] + v[1], r[0] + 't' + r[1] + 'ie', '-', tv);
		forms['past.p3.m.sg'] += weak_vowel_forms('i' + r[0] + 't' + v[0] + r[1] + v[1], 'i' + r[0] + 't' + r[1] + 'ie', 'LR', tv);
		forms['past.p3.f.sg'] = weak_consonant_forms(r[0] + 't' + r[1] + 'iet', r[0] + 't' + r[1] + 'it', '-', tv);
		forms['past.p3.f.sg'] += weak_consonant_forms('i' + r[0] + 't' + r[1] + 'iet', 'i' + r[0] + 't' + r[1] + 'it', 'LR', tv);
	#}
	else : #{
		forms['past.p3.m.sg'] = weak_vowel_forms(r[0] + 't' + v[0] + r[1] + v[1], r[0] + 't' + r[1] + 'a', '-', tv);
		forms['past.p3.m.sg'] += weak_vowel_forms('i' + r[0] + 't' + v[0] + r[1] + v[1], 'i' + r[0] + 't' + r[1] + 'a', 'LR', tv);
		forms['past.p3.f.sg'] = weak_consonant_forms(r[0] + 't' + r[1] + 'at', r[0] + 't' + r[1] + 'at', '-', tv);
		forms['past.p3.f.sg'] += weak_consonant_forms('i' + r[0] + 't' + r[1] + 'at', 'i' + r[0] + 't' + r[1] + 'at', 'LR', tv);
	#}

	# This form is obtained by the omission of the two vowels in the stem
	# and the addition of the dipthong 'ej' (for e-a) and 'aj' (for a-a)
	forms['past.p2.mf.sg'] = weak_consonant_forms(r[0] + 't' + r[1] + v[0] + 'jt', r[0] + 't' + r[1] + v[0] + 'jt', '-', tv);	
	forms['past.p2.mf.sg'] += weak_consonant_forms('i' + r[0] + 't' + r[1] + v[0] + 'jt', 'i' + r[0] + 't' + r[1] + v[0] + 'jt', 'LR', tv);	
	forms['past.p1.mf.sg'] = weak_consonant_forms(r[0] + 't' + r[1] + v[0] + 'jt', r[0] + 't' + r[1] + v[0] + 'jt', '-', tv);	
	forms['past.p1.mf.sg'] += weak_consonant_forms('i' + r[0] + 't' + r[1] + v[0] + 'jt', 'i' + r[0] + 't' + r[1] + v[0] + 'jt', 'LR', tv);	

	# This form is obtained by the omission of the two vowels in the vocalic
	# sequence + dipthong 'ew' or 'aw'
	# with attached pronouns 'ew' and 'aw'
	# are also treated like vowels
	forms['past.p3.mf.pl'] = weak_vowel_forms(r[0] + 't' + r[1] + v[0] + 'w', r[0] + 't' + r[1] + v[0] + 'w', '-', tv);
	forms['past.p3.mf.pl'] += weak_vowel_forms('i' + r[0] + 't' + r[1] + v[0] + 'w', 'i' + r[0] + 't' + r[1] + v[0] + 'w', 'LR', tv);
	forms['past.p2.mf.pl'] = weak_vowel_forms(r[0] + 't' + r[1] + v[0] + 'jtu', r[0] + 't' + r[1] + v[0] + 'jtu', '-', tv);
	forms['past.p2.mf.pl'] += weak_vowel_forms('i' + r[0] + 't' + r[1] + v[0] + 'jtu', 'i' + r[0] + 't' + r[1] + v[0] + 'jtu', 'LR', tv);
	forms['past.p1.mf.pl'] = weak_vowel_forms(r[0] + 't' + r[1] + v[0] + 'jna', r[0] + 't' + r[1] + v[0] + 'jnie', '-', tv);
	forms['past.p1.mf.pl'] += weak_vowel_forms('i' + r[0] + 't' + r[1] + v[0] + 'jna', 'i' + r[0] + 't' + r[1] + v[0] + 'jnie', 'LR', tv);

	return forms;
#}


def weak_patt8a_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_vowel = v[1];
	# That's only my guess
	if v[0] == 'e' and v[1] == 'a': 
		presuff_vowel = 'ie';

	# no first vowel elision in forms with suffixes? 
	forms['pres.p3.m.sg'] = weak_vowel_forms('ji' + r[0] + 't' + v[0] + r[1] + v[1] , 'ji' + r[0] + 't' + r[1] + presuff_vowel , '-', tv);
	forms['pres.p3.f.sg'] = weak_vowel_forms('ti' + r[0] + 't' + v[0] + r[1] + v[1] , 'ti' + r[0] + 't' + r[1] + presuff_vowel , '-', tv);
	forms['pres.p2.mf.sg'] = weak_vowel_forms('ti' + r[0] + 't' + v[0] + r[1] + v[1] , 'ti' + r[0] + 't' + r[1] + presuff_vowel , '-', tv);
	forms['pres.p1.mf.sg'] = weak_vowel_forms('ni' + r[0] + 't' + v[0] + r[1] + v[1] , 'ni' + r[0] + 't' + r[1] + presuff_vowel , '-', tv);

	if vowels == 'a-a':
		suffix =  'aw';
	elif v[1] == 'i' :
		suffix = 'u'
	else:
		suffix =  'ew';
	

	forms['pres.p3.mf.pl'] = weak_vowel_forms('ji' + r[0] + 't' + r[1] + suffix, 'ji' + r[0] + 't' + r[1] + suffix, '-', tv);	
	forms['pres.p2.mf.pl'] = weak_vowel_forms('ti' + r[0] + 't' + r[1] + suffix, 'ti' + r[0] + 't' + r[1] + suffix, '-', tv);	
	forms['pres.p1.mf.pl'] = weak_vowel_forms('ni' + r[0] + 't' + r[1] + suffix, 'ni' + r[0] + 't' + r[1] + suffix, '-', tv);	

	return forms;
#}


def weak_patt8a_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_sg_vowel = v[1];
	# That's only my guess
	if v[0] == 'e' and v[1] == 'a': 
		presuff_sg_vowel = 'ie';

	if vowels == 'a-a':
		pl_suffix =  'aw';
	elif v[1] == 'i' :
		pl_suffix = 'u'
	else:
		pl_suffix =  'ew';
	

	# also 'n' prefix/only 'n' or 'in' prefix?
	forms['imp.p2.mf.sg'] = weak_vowel_forms('i' + r[0] + 't' + v[0] + r[1] + v[1] , 'i' + r[0] + 't' + v[0] + r[1] + presuff_sg_vowel , '-', tv);
	forms['imp.p2.mf.pl'] = weak_vowel_forms('i' + r[0] + 't' + v[0] + r[1] + pl_suffix , 'i' + r[0] + 't' + v[0] + r[1] + pl_suffix , '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 8b
## ----------------------------------------------------------------------------##


def weak_patt8b_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

# TODO: only some verbs with 'i' ?

	if v[0] == 'e' and v[1] == 'a': #{

		forms['past.p3.m.sg'] = weak_vowel_forms('n' + r[0] + 't' + v[0] + r[1] + v[1], 'n' + r[0] + 't' + r[1] + 'ie', '-', tv);
		forms['past.p3.m.sg'] += weak_vowel_forms('in' + r[0] + 't' + v[0] + r[1] + v[1], 'in' + r[0] + 't' + r[1] + 'ie', 'LR', tv);
		forms['past.p3.f.sg'] = weak_consonant_forms('n' + r[0] + 't' + r[1] + 'iet', 'n' + r[0] + 't' + r[1] + 'it', '-', tv);
		forms['past.p3.f.sg'] += weak_consonant_forms('in' + r[0] + 't' + r[1] + 'iet', 'in' + r[0] + 't' + r[1] + 'it', 'LR', tv);
	#}
	else : #{
		forms['past.p3.m.sg'] = weak_vowel_forms('n' + r[0] + 't' + v[0] + r[1] + v[1], 'n' + r[0] + 't' + r[1] + 'a', '-', tv);
		forms['past.p3.m.sg'] += weak_vowel_forms('in' + r[0] + 't' + v[0] + r[1] + v[1], 'in' + r[0] + 't' + r[1] + 'a', 'LR', tv);
		forms['past.p3.f.sg'] = weak_consonant_forms('n' + r[0] + 't' + r[1] + 'at', 'n' + r[0] + 't' + r[1] + 'at', '-', tv);
		forms['past.p3.f.sg'] += weak_consonant_forms('in' + r[0] + 't' + r[1] + 'at', 'in' + r[0] + 't' + r[1] + 'at', 'LR', tv);
	#}

	# This form is obtained by the omission of the two vowels in the stem
	# and the addition of the dipthong 'ej' (for e-a) and 'aj' (for a-a)
	forms['past.p2.mf.sg'] = weak_consonant_forms('n' + r[0] + 't' + r[1] + v[0] + 'jt', 'n' + r[0] + 't' + r[1] + v[0] + 'jt', '-', tv);	
	forms['past.p2.mf.sg'] += weak_consonant_forms('in' + r[0] + 't' + r[1] + v[0] + 'jt', 'in' + r[0] + 't' + r[1] + v[0] + 'jt', 'LR', tv);	
	forms['past.p1.mf.sg'] = weak_consonant_forms('n' + r[0] + 't' + r[1] + v[0] + 'jt', 'n' + r[0] + 't' + r[1] + v[0] + 'jt', '-', tv);	
	forms['past.p1.mf.sg'] += weak_consonant_forms('in' + r[0] + 't' + r[1] + v[0] + 'jt', 'in' + r[0] + 't' + r[1] + v[0] + 'jt', 'LR', tv);	

	# This form is obtained by the omission of the two vowels in the vocalic
	# sequence + dipthong 'ew' or 'aw'
	# with attached pronouns 'ew' and 'aw'
	# are also treated like vowels
	forms['past.p3.mf.pl'] = weak_vowel_forms('n' + r[0] + 't' + r[1] + v[0] + 'w', 'n' + r[0] + 't' + r[1] + v[0] + 'w', '-', tv);
	forms['past.p3.mf.pl'] += weak_vowel_forms('in' + r[0] + 't' + r[1] + v[0] + 'w', 'in' + r[0] + 't' + r[1] + v[0] + 'w', 'LR', tv);
	forms['past.p2.mf.pl'] = weak_vowel_forms('n' + r[0] + 't' + r[1] + v[0] + 'jtu', 'n' + r[0] + 't' + r[1] + v[0] + 'jtu', '-', tv);
	forms['past.p2.mf.pl'] += weak_vowel_forms('in' + r[0] + 't' + r[1] + v[0] + 'jtu', 'in' + r[0] + 't' + r[1] + v[0] + 'jtu', 'LR', tv);
	forms['past.p1.mf.pl'] = weak_vowel_forms('n' + r[0] + 't' + r[1] + v[0] + 'jna', 'n' + r[0] + 't' + r[1] + v[0] + 'jnie', '-', tv);
	forms['past.p1.mf.pl'] += weak_vowel_forms('in' + r[0] + 't' + r[1] + v[0] + 'jna', 'in' + r[0] + 't' + r[1] + v[0] + 'jnie', 'LR', tv);

	return forms;
#}


def weak_patt8b_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_vowel = v[1];
	# That's only my guess
	if v[0] == 'e' and v[1] == 'a': 
		presuff_vowel = 'ie';

	# no first vowel elision in forms with suffixes? 
	forms['pres.p3.m.sg'] = weak_vowel_forms('jin' + r[0] + 't' + v[0] + r[1] + v[1] , 'jin' + r[0] + 't' + r[1] + presuff_vowel , '-', tv);
	forms['pres.p3.f.sg'] = weak_vowel_forms('tin' + r[0] + 't' + v[0] + r[1] + v[1] , 'tin' + r[0] + 't' + r[1] + presuff_vowel , '-', tv);
	forms['pres.p2.mf.sg'] = weak_vowel_forms('tin' + r[0] + 't' + v[0] + r[1] + v[1] , 'tin' + r[0] + 't' + r[1] + presuff_vowel , '-', tv);
	forms['pres.p1.mf.sg'] = weak_vowel_forms('nin' + r[0] + 't' + v[0] + r[1] + v[1] , 'nin' + r[0] + 't' + r[1] + presuff_vowel , '-', tv);

	if vowels == 'a-a':
		suffix =  'aw';
	elif v[1] == 'i' :
		suffix = 'u'
	else:
		suffix =  'ew';
	

	forms['pres.p3.mf.pl'] = weak_vowel_forms('jin' + r[0] + 't' + r[1] + suffix, 'jin' + r[0] + 't' + r[1] + suffix, '-', tv);	
	forms['pres.p2.mf.pl'] = weak_vowel_forms('tin' + r[0] + 't' + r[1] + suffix, 'tin' + r[0] + 't' + r[1] + suffix, '-', tv);	
	forms['pres.p1.mf.pl'] = weak_vowel_forms('nin' + r[0] + 't' + r[1] + suffix, 'nin' + r[0] + 't' + r[1] + suffix, '-', tv);	

	return forms;
#}


def weak_patt8b_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_sg_vowel = v[1];
	# That's only my guess
	if v[0] == 'e' and v[1] == 'a': 
		presuff_sg_vowel = 'ie';

	if vowels == 'a-a':
		pl_suffix =  'aw';
	elif v[1] == 'i' :
		pl_suffix = 'u'
	else:
		pl_suffix =  'ew';
	

	# also 'nt' prefix/only 'nt' or 'int' prefix?
	forms['imp.p2.mf.sg'] = weak_vowel_forms('in' + r[0] + 't' + v[0] + r[1] + v[1] , 'in' + r[0] + 't' + v[0] + r[1] + presuff_sg_vowel , '-', tv);
	forms['imp.p2.mf.pl'] = weak_vowel_forms('in' + r[0] + 't' + v[0] + r[1] + pl_suffix , 'in' + r[0] + 't' + v[0] + r[1] + pl_suffix , '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
## pattern 10
## ----------------------------------------------------------------------------##


def weak_patt10_pp(root, pp_v): #{
	r = root.split('-'); # radicals

	forms = {};
	
# TODO: not always 'o', right?

	forms['pp.m.sg'] = [('mist' + pp_v + r[0]  + r[1] + 'i', '-', '-')] ;
	forms['pp.f.sg'] = [('mist' + pp_v + r[0] + r[1] + 'ija', '-', '-')] ;
	forms['pp.mf.pl'] = [('mist' + pp_v + r[0] + r[1] + 'ijin', '-', '-')] ;

	return forms;
#}


def weak_patt10_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	forms['past.p3.m.sg'] = weak_vowel_forms('sta' + r[0] + r[1] + v[1], 'sta' + r[0] + r[1] + 'ie', '-', tv);
	forms['past.p3.m.sg'] += weak_vowel_forms('ista' + r[0] + r[1] + v[1], 'ista' + r[0] + r[1] + 'ie', 'LR', tv);
	forms['past.p3.f.sg'] = weak_consonant_forms('sta' + r[0] + r[1] + 'iet', 'sta' + r[0] + r[1] + 'it', '-', tv);
	forms['past.p3.f.sg'] += weak_consonant_forms('ista' + r[0] + r[1] + 'iet', 'ista' + r[0] + r[1] + 'it', 'LR', tv);
	forms['past.p2.mf.sg'] = weak_consonant_forms('sta' + r[0] + r[1] + 'ejt', 'sta' + r[0] + r[1] + 'ejt', '-', tv);	
	forms['past.p2.mf.sg'] += weak_consonant_forms('ista' + r[0] + r[1] + 'ejt', 'ista' + r[0] + r[1] + 'ejt', 'LR', tv);	
	forms['past.p1.mf.sg'] = weak_consonant_forms('sta' + r[0] + r[1] + 'ejt', 'sta' + r[0] + r[1] + 'ejt', '-', tv);	
	forms['past.p1.mf.sg'] += weak_consonant_forms('ista' + r[0] + r[1] + 'ejt', 'ista' + r[0] + r[1] + 'ejt', 'LR', tv);	

	forms['past.p3.mf.pl'] = weak_vowel_forms('sta' + r[0] + r[1] + 'ew', 'sta' + r[0] + r[1] + 'ew', '-', tv);
	forms['past.p3.mf.pl'] += weak_vowel_forms('ista' + r[0] + r[1] + 'ew', 'ista' + r[0] + r[1] + 'ew', 'LR', tv);
	forms['past.p2.mf.pl'] = weak_vowel_forms('sta' + r[0] + r[1] + 'ejtu', 'sta' + r[0] + r[1] + 'ejtu', '-', tv);
	forms['past.p2.mf.pl'] += weak_vowel_forms('ista' + r[0] + r[1] + 'ejtu', 'ista' + r[0] + r[1] + 'ejtu', 'LR', tv);
	forms['past.p1.mf.pl'] = weak_vowel_forms('sta' + r[0] + r[1] + 'ejna', 'sta' + r[0] + r[1] + 'ejnie', '-', tv);
	forms['past.p1.mf.pl'] += weak_vowel_forms('ista' + r[0] + r[1] + 'ejna', 'ista' + r[0] + r[1] + 'ejnie', '-', tv);

	return forms;
#}


def weak_patt10_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_vowel = v[1];
	if v[1] == 'a': 
		presuff_vowel = 'ie';

	# no first vowel elision in forms with suffixes? 
	forms['pres.p3.m.sg'] = weak_vowel_forms('jist' + v[0] + r[0] + r[1] + v[1] , 'jist' + v[0] + r[0] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p3.f.sg'] = weak_vowel_forms('tist' + v[0] + r[0] + r[1] + v[1] , 'tist' + v[0] + r[0] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p2.mf.sg'] = weak_vowel_forms('tist' + v[0] + r[0] + r[1] + v[1] , 'tist' + v[0] + r[0] + r[1] + presuff_vowel , '-', tv);
	forms['pres.p1.mf.sg'] = weak_vowel_forms('nist' + v[0] + r[0] + r[1] + v[1] , 'nist' + v[0] + r[0] + r[1] + presuff_vowel , '-', tv);

	if vowels == 'a-a':
		suffix =  'aw';
	elif v[1] == 'i' :
		suffix = 'u'
	else:
		suffix =  'ew';
	

	forms['pres.p3.mf.pl'] = weak_vowel_forms('jist' + v[0] + r[0] + r[1] + suffix, 'jist' + v[0] + r[0] + r[1] + suffix, '-', tv);
	forms['pres.p2.mf.pl'] = weak_vowel_forms('tist' + v[0] + r[0] + r[1] + suffix, 'tist' + v[0] + r[0] + r[1] + suffix, '-', tv);	
	forms['pres.p1.mf.pl'] = weak_vowel_forms('nist' + v[0] + r[0] + r[1] + suffix, 'nist' + v[0] + r[0] + r[1] + suffix, '-', tv);

	return forms;
#}


def weak_patt10_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	presuff_sg_vowel = v[1];
	if v[1] == 'a': 
		presuff_sg_vowel = 'ie';

	if vowels == 'a-a':
		pl_suffix =  'aw';
	elif v[1] == 'i' :
		pl_suffix = 'u'
	else:
		pl_suffix =  'ew';


	forms['imp.p2.mf.sg'] = weak_vowel_forms('st' + v[0] + r[0] + r[1] + v[1], 'st' + v[0] + r[0] + r[1] + presuff_sg_vowel , '-', tv);
	forms['imp.p2.mf.pl'] = weak_vowel_forms('st' + v[0] + r[0] + r[1] + pl_suffix , 'st' + v[0] + r[0] + r[1] + pl_suffix , '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##


def main(stem): #{

	forms = {};

	if stem['theme'] == '1' : #{

		forms = weak_patt1_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(weak_patt1_pres(stem['root'], stem['vowel_impf'], stem['trans']));
#		if stem['trans'] == 'tv' :
		forms.update(weak_patt1_imp(stem['root'], stem['vowel_impf'], stem['trans']));
		if stem['pp'] != '' : #{
			forms.update(weak_patt1_pp(stem['root'], stem['vowel_perf'], stem['pp']));
# pprs
	#}


	if stem['theme'] == '2' : #{

		forms = weak_patt2_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(weak_patt2_pres(stem['root'], stem['vowel_impf'], stem['trans']));
		forms.update(weak_patt2_imp(stem['root'], stem['vowel_impf'], stem['trans']));
		if stem['pp'] != '' : #{
			forms.update(weak_patt2_pprs(stem['root'], stem['pp']));
	#}


	if stem['theme'] == '5' : #{

		forms = weak_patt5_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(weak_patt5_pres(stem['root'], stem['vowel_impf'], stem['trans']));
		forms.update(weak_patt5_imp(stem['root'], stem['vowel_impf'], stem['trans']));
		forms.update(weak_patt5_pprs(stem['root']));
	#}


	if stem['theme'] == '7a' : #{

		forms = weak_patt7a_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(weak_patt7a_pres(stem['root'], stem['vowel_perf'], stem['trans']));
		forms.update(weak_patt7a_imp(stem['root'], stem['vowel_perf'], stem['trans']));
	#}


	if stem['theme'] == '7b' : #{

		forms = weak_patt7b_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(weak_patt7b_pres(stem['root'], stem['vowel_perf'], stem['trans']));
		forms.update(weak_patt7b_imp(stem['root'], stem['vowel_perf'], stem['trans']));
	#}


	if stem['theme'] == '8a' : #{

		forms = weak_patt8a_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(weak_patt8a_pres(stem['root'], stem['vowel_perf'], stem['trans']));
		forms.update(weak_patt8a_imp(stem['root'], stem['vowel_perf'], stem['trans']));
	#}


	if stem['theme'] == '8b' : #{

		forms = weak_patt8b_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(weak_patt8b_pres(stem['root'], stem['vowel_perf'], stem['trans']));
		forms.update(weak_patt8b_imp(stem['root'], stem['vowel_perf'], stem['trans']));
	#}


	if stem['theme'] == '10' : #{

		forms = weak_patt10_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(weak_patt10_pres(stem['root'], stem['vowel_impf'], stem['trans']));
		forms.update(weak_patt10_imp(stem['root'], stem['vowel_impf'], stem['trans']));
		if stem['trans'] == 'tv' and stem['pp'] != '' : #{
			forms.update(weak_patt10_pp(stem['root'], stem['pp']));  # pp in fact

	#}


	return forms;

#}

