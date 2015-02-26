#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-


## ----------------------------------------------------------------------------##
## quadriliteral = strong pattern 2
## ----------------------------------------------------------------------------##


# no ek/ok problems
def quad_consonant_forms (form_sg, form_sg_suff, r, tv): #{

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


def quad_e_consonant_forms (pres_sg, pres_sg_long, pres_sg_short, r, tv): #{
# a-e, e-e, i-e verbs: forms ending with e + cons (past.p3.sg.m, pers.sg)

	if tv == 'iv' : #{
	 	forms = [(pres_sg, '-', r),
	 		 (pres_sg_long, 'S__qtalt/x', r),
			 (pres_sg_long, 'S__fetaħ/li', r),
			 (pres_sg_short, 'S__fetħ/ilha', r),
			 (pres_sg_long, 'S__fetaħ/lix', r),
			 (pres_sg_short, 'S__fetħ/ilhiex', r)];
	#}
	else : #{
		forms = [(pres_sg, '-', r),
			 (pres_sg_long, 'S__qtalt/x', r),
			 (pres_sg_long, 'S__fetaħ/ni', r),
			 (pres_sg_short, 'S__fetħ/ek', r),
			 (pres_sg_long, 'S__fetaħ/nix', r),
			 (pres_sg_short, 'S__fetħ/ekx', r),
			 (pres_sg_long, 'S__fetaħ/li', r),
			 (pres_sg_short, 'S__fetħ/ilha', r),
			 (pres_sg_long, 'S__fetaħ/lix', r),
			 (pres_sg_short, 'S__fetħ/ilhiex', r),
			 (pres_sg_long, 'S__qtaltu/hielha', r),
			 (pres_sg_long, 'S__qtaltu/hielhiex', r)];
	#}

	return forms;
#}


def quad_vowel_forms (form_pl, form_pl_suff, r, tv): #{

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


def quad_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};


	# no suffix
	past = r[0] + v[0] + r[1] + r[2] + v[1] + r[3]; # dardar, kexkex (past.p3.sg.m)

	# with suffix(es)
	if v[1] == 'e' : #{
		past_long = r[0] + v[0] + r[1] + r[2] + 'i' + r[3]; # kexkixni (past.p3.sg.m + prn.p1.mf.sg)
	else :
		past_long = r[0] + v[0] + r[1] + r[2] + v[1] + r[3]; # dardarni (past.p3.sg.m + prn.p1.mf.sg)

	past_short = r[0] + v[0] + r[1] + r[2] + r[3]; # dardrek, kexkxek (past.p3.sg.m + prn.p2.mf.sg)


	forms['past.p3.m.sg'] = quad_e_consonant_forms (past, past_long, past_short, '-', tv);
	forms['past.p3.f.sg'] = quad_consonant_forms (past_short + 'et', past_short + 'it', '-', tv);
	forms['past.p2.mf.sg'] = quad_consonant_forms (past_long + 't', past_long + 't', '-', tv);
	forms['past.p1.mf.sg'] = quad_consonant_forms (past_long + 't', past_long + 't', '-', tv);

	forms['past.p3.mf.pl'] = quad_vowel_forms (past_short + 'u', past_short + 'u', '-', tv);
	forms['past.p2.mf.pl'] = quad_vowel_forms (past_long + 'tu', past_long + 'tu', '-', tv);
	forms['past.p1.mf.pl'] = quad_vowel_forms (past_long + 'na', past_long + 'nie', '-', tv);


	return forms;
#}

	
def quad_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};


	# no suffix
	pres = r[0] + v[0] + r[1] + r[2] + v[1] + r[3]; # idardar, ikexkex

	# with suffix(es)
	if v[1] == 'e' : #{
		pres_long = r[0] + v[0] + r[1] + r[2] + 'i' + r[3]; #ikexkixni
	else :
		pres_long = r[0] + v[0] + r[1] + r[2] + v[1] + r[3]; #idardarni

	pres_short = r[0] + v[0] + r[1] + r[2] + r[3]; # ikexkxek, idardrek


	forms['pres.p3.m.sg'] = quad_e_consonant_forms ('i' + pres, 'i' + pres_long, 'i' + pres_short, 'LR', tv);		# idardar
	forms['pres.p3.m.sg'] += quad_e_consonant_forms ('j' + pres, 'j' + pres_long, 'j' + pres_short, 'LR', tv); 	# jdardar
	forms['pres.p3.m.sg'] += quad_e_consonant_forms ('i' + pres, 'i' + pres_long, 'i' + pres_short, 'RL', tv);	# ~idardar

	# Oh no. Only some verbs have assimilation  here:
	# iddardar, but itkexkex
	# let me guess what should be assimilated
	# ċ, d, ġ, s, x, ż ?
	forms['pres.p3.f.sg'] = quad_e_consonant_forms ('i' + r[0] + pres, 'i' + r[0] + pres_long, 'i' + r[0] + pres_short, 'LR', tv);	# iddardar
	forms['pres.p3.f.sg'] += quad_e_consonant_forms (r[0] + pres, r[0] + pres_long, r[0] + pres_short, 'LR', tv);	# ddardar
	forms['pres.p3.f.sg'] = quad_e_consonant_forms ('it' + pres, 'it' + pres_long, 'it' + pres_short, 'LR', tv);	# iddardar
	forms['pres.p3.f.sg'] += quad_e_consonant_forms ('t' + pres, 't' + pres_long, 't' + pres_short, 'LR', tv);	# ddardar
	forms['pres.p3.f.sg'] += quad_e_consonant_forms ('i' + r[0] + pres, 'i' + r[0] + pres_long, 'i' + r[0] + pres_short, 'RL', tv);	# ~iddardar

	forms['pres.p2.mf.sg'] = quad_e_consonant_forms ('i' + r[0] + pres, 'i' + r[0] + pres_long, 'i' + r[0] + pres_short, 'LR', tv);	# iddardar
	forms['pres.p2.mf.sg'] += quad_e_consonant_forms (r[0] + pres, r[0] + pres_long, r[0] + pres_short, 'LR', tv);	# ddardar
	forms['pres.p2.mf.sg'] = quad_e_consonant_forms ('it' + pres, 'it' + pres_long, 'it' + pres_short, 'LR', tv);	# iddardar
	forms['pres.p2.mf.sg'] += quad_e_consonant_forms ('t' + pres, 't' + pres_long, 't' + pres_short, 'LR', tv);	# ddardar
	forms['pres.p2.mf.sg'] += quad_e_consonant_forms ('i' + r[0] + pres, 'i' + r[0] + pres_long, 'i' + r[0] + pres_short, 'RL', tv);	# ~iddardar

	forms['pres.p1.mf.sg'] = quad_e_consonant_forms ('in' + pres, 'in' + pres_long, 'in' + pres_short, 'LR', tv);		# indardar
	forms['pres.p1.mf.sg'] += quad_e_consonant_forms ('n' + pres, 'n' + pres_long, 'n' + pres_short, 'LR', tv);		# ndardar
	forms['pres.p1.mf.sg'] += quad_e_consonant_forms ('in' + pres, 'in' + pres_long, 'in' + pres_short, 'RL', tv);	# indardar



	forms['pres.p3.mf.pl'] = quad_vowel_forms ('i' + pres_short + 'u', 'i' + pres_short + 'u', 'LR', tv);		# idardru
	forms['pres.p3.mf.pl'] += quad_vowel_forms ('j' + pres_short + 'u', 'j' + pres_short + 'u', 'LR', tv); 		# jdardru
	forms['pres.p3.mf.pl'] += quad_vowel_forms ('i' + pres_short + 'u', 'i' + pres_short + 'u', 'RL', tv);		# ~idardru

	forms['pres.p2.mf.pl'] = quad_vowel_forms ('i' + r[0] + pres_short + 'u', 'i' + r[0] + pres_short + 'u', 'LR', tv);	# iddardru
	forms['pres.p2.mf.pl'] += quad_vowel_forms (r[0] + pres_short + 'u', r[0] + pres_short + 'u', 'LR', tv);		# ddardru
	forms['pres.p2.mf.pl'] = quad_vowel_forms ('it' + pres_short + 'u', 'it' + pres_short + 'u', 'LR', tv);	# iddardru
	forms['pres.p2.mf.pl'] += quad_vowel_forms ('t' + pres_short + 'u', 't' + pres_short + 'u', 'LR', tv);		# ddardru
	forms['pres.p2.mf.pl'] += quad_vowel_forms ('i' + r[0] + pres_short + 'u', 'i' + r[0] + pres_short + 'u', 'RL', tv);	# ~iddardru

	forms['pres.p1.mf.pl'] = quad_vowel_forms ('in' + pres_short + 'u', 'in' + pres_short + 'u', 'LR', tv);		# indardru
	forms['pres.p1.mf.pl'] += quad_vowel_forms ('n' + pres_short + 'u', 'in' + pres_short + 'u', 'LR', tv);		# indardru
	forms['pres.p1.mf.pl'] += quad_vowel_forms ('in' + pres_short + 'u', 'in' + pres_short + 'u', 'RL', tv);		# indardru

	return forms;
#}


def quad_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	# no suffix
	pres = r[0] + v[0] + r[1] + r[2] + v[1] + r[3]; # idardar, ikexkex

	# with suffix(es)
	if v[1] == 'e' : #{
		pres_long = r[0] + v[0] + r[1] + r[2] + 'i' + r[3]; #ikexkixni
	else :
		pres_long = r[0] + v[0] + r[1] + r[2] + v[1] + r[3]; #idardarni

	pres_short = r[0] + v[0] + r[1] + r[2] + r[3]; # ikexkxek, idardrek

	
	suffix = 'u';

	forms['imp.p2.mf.sg'] = quad_e_consonant_forms (pres, pres_long, pres_short, '-', tv);
	forms['imp.p2.mf.pl'] = quad_vowel_forms (pres_short + 'u', pres_short + 'u', '-', tv);

	return forms;
#}


# check if all quad1/strong2 have pp and if prefix is always 'm' (and 'im')

def quad_pp(root, vowels, pref): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	forms['pp.m.sg'] = [('m' + r[0] + v[0] + r[1] + r[2] + v[1] + r[3], '-', '-'),
			    ('im' + r[0] + v[0] + r[1] + r[2] + v[1] + r[3], '-', 'LR')];	# (i)mdardar
	forms['pp.f.sg'] = [('m' + r[0] + v[0] + r[1] + r[2] + r[3] + 'a', '-', '-'),
			    ('im' + r[0] + v[0] + r[1] + r[2] + r[3] + 'a', '-', 'LR')];	# (i)mdardra
	forms['pp.mf.pl'] = [('m' + r[0] + v[0] + r[1] + r[2] + r[3] + 'in', '-', '-'),
			     ('im' + r[0] + v[0] + r[1] + r[2] + r[3] + 'in', '-', 'LR')];	# (i)mdardrin

	return forms;
#}


## ----------------------------------------------------------------------------##
## quadriliteral pattern 2 = strong pattern 5
## ----------------------------------------------------------------------------##


def quad_patt2_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	prefix_cons = 't';
	if (r[0] == 'ċ' or r[0] == 'd' or r[0] == 'ġ' or r[0] == 'n' or r[0] == 's' or r[0] == 'x'  or r[0] == 'ż' or r[0] == 'z') : #{
	# assimilation
		prefix_cons = r[0];

	# no suffix
	past = prefix_cons + r[0] + v[0] + r[1] + r[2] + v[1] + r[3]; # tfakkar, ssellef (past.p3.sg.m)

	# with suffix(es)
	if v[1] == 'e' : #{
		past_long = prefix_cons + r[0] + v[0] + r[1] + r[2] + 'i' + r[3]; # ssellifni (past.p3.sg.m + prn.p1.mf.sg)
	else :
		past_long = prefix_cons + r[0] + v[0] + r[1] + r[2] + v[1] + r[3]; # tfakkarni (past.p3.sg.m + prn.p1.mf.sg, doesn't really make sense)

	past_short = prefix_cons + r[0] + v[0] + r[1] + r[2] + r[3]; # tfakkrek, ssellfek (past.p3.sg.m + prn.p2.mf.sg)


	forms['past.p3.m.sg'] = quad_e_consonant_forms ('i' + past, 'i' + past_long, 'i' + past_short, 'LR', tv);
	forms['past.p3.m.sg'] += quad_e_consonant_forms (past, past_long, past_short, 'LR', tv);
	forms['past.p3.m.sg'] += quad_e_consonant_forms ('i' + past, 'i' + past_long, 'i' +  past_short, 'RL', tv);

	forms['past.p3.f.sg'] = quad_consonant_forms ('i' + past_short + 'et', 'i' + past_short + 'it', 'LR', tv);
	forms['past.p3.f.sg'] += quad_consonant_forms (past_short + 'et', past_short + 'it', 'LR', tv);
	forms['past.p3.f.sg'] += quad_consonant_forms ('i' + past_short + 'et', 'i' + past_short + 'it', 'RL', tv);

	forms['past.p2.mf.sg'] = quad_consonant_forms ('i' + past_long + 't', 'i' + past_long + 't', 'LR', tv);
	forms['past.p2.mf.sg'] += quad_consonant_forms (past_long + 't', past_long + 't', 'LR', tv);
	forms['past.p2.mf.sg'] += quad_consonant_forms ('i' + past_long + 't', 'i' + past_long + 't', 'RL', tv);

	forms['past.p1.mf.sg'] = quad_consonant_forms ('i' + past_long + 't', 'i' + past_long + 't', 'LR', tv);
	forms['past.p1.mf.sg'] += quad_consonant_forms (past_long + 't', past_long + 't', 'LR', tv);
	forms['past.p1.mf.sg'] += quad_consonant_forms ('i' + past_long + 't', 'i' + past_long + 't', 'RL', tv);

	forms['past.p3.mf.pl'] = quad_vowel_forms ('i' + past_short + 'u', 'i' + past_short + 'u', 'LR', tv);
	forms['past.p3.mf.pl'] += quad_vowel_forms (past_short + 'u', past_short + 'u', 'LR', tv);
	forms['past.p3.mf.pl'] += quad_vowel_forms ('i' + past_short + 'u', 'i' + past_short + 'u', 'RL', tv);

	forms['past.p2.mf.pl'] = quad_vowel_forms ('i' + past_long + 'tu', 'i' + past_long + 'tu', 'LR', tv);
	forms['past.p2.mf.pl'] += quad_vowel_forms (past_long + 'tu', past_long + 'tu', 'LR', tv);
	forms['past.p2.mf.pl'] += quad_vowel_forms ('i' + past_long + 'tu', 'i' + past_long + 'tu', 'RL', tv);

	forms['past.p1.mf.pl'] = quad_vowel_forms ('i' + past_long + 'na', 'i' + past_long + 'nie', 'LR', tv);
	forms['past.p1.mf.pl'] += quad_vowel_forms (past_long + 'na', past_long + 'nie', 'LR', tv);
	forms['past.p1.mf.pl'] += quad_vowel_forms ('i' + past_long + 'na', 'i' + past_long + 'nie', 'RL', tv);

	return forms;
#}


def quad_patt2_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	prefix_cons = 't';
	if (r[0] == 'ċ' or r[0] == 'd' or r[0] == 'ġ' or r[0] == 'n' or r[0] == 's' or r[0] == 'x'  or r[0] == 'ż' or r[0] == 'z') : #{
	# assimilation
		prefix_cons = r[0];

	# no suffix
	pres = prefix_cons + r[0] + v[0] + r[1] + r[2] + v[1] + r[3]; # (i)tfakkar, (i)ssellef

	# with suffix(es)
	if v[1] == 'e' : #{
		pres_long = prefix_cons + r[0] + v[0] + r[1] + r[2] + 'i' + r[3]; # (i)ssellif-ni
	else :
		pres_long = prefix_cons + r[0] + v[0] + r[1] + r[2] + v[1] + r[3]; # (i)tfakkar-ni

	pres_short = prefix_cons + r[0] + v[0] + r[1] + r[2] + r[3]; # (i)tfarrk-u, (i)ssellf-u


	forms['pres.p3.m.sg'] = quad_e_consonant_forms ('ji' + pres, 'ji' + pres_long, 'ji' + pres_short, '-', tv);		# 
#	forms['pres.p3.m.sg'] += quad_e_consonant_forms ('j' + pres, 'j' + pres_long, 'j' + pres_short, 'LR'); 	# jdardar
#	forms['pres.p3.m.sg'] += quad_e_consonant_forms ('i' + pres, 'i' + pres_long, 'i' + pres_short, 'RL');	# ~idardar

	forms['pres.p3.f.sg'] = quad_e_consonant_forms ('ti' + pres, 'ti' + pres_long, 'ti' + pres_short, '-', tv);	#
#	forms['pres.p3.f.sg'] += quad_e_consonant_forms (r[0] + pres, r[0] + pres_long, r[0] + pres_short, 'LR');	# ddardar
#	forms['pres.p3.f.sg'] += quad_e_consonant_forms ('i' + r[0] + pres, 'i' + r[0] + pres_long, 'i' + r[0] + pres_short, 'RL');	# ~iddardar

	forms['pres.p2.mf.sg'] = quad_e_consonant_forms ('ti' + pres, 'ti' + pres_long, 'ti' + pres_short, '-', tv);	# 
#	forms['pres.p2.mf.sg'] += quad_e_consonant_forms (r[0] + pres, r[0] + pres_long, r[0] + pres_short, 'LR');	# ddardar
#	forms['pres.p2.mf.sg'] += quad_e_consonant_forms ('i' + r[0] + pres, 'i' + r[0] + pres_long, 'i' + r[0] + pres_short, 'RL');	# ~iddardar

	forms['pres.p1.mf.sg'] = quad_e_consonant_forms ('ni' + pres, 'ni' + pres_long, 'ni' + pres_short, '-', tv);		# 
#	forms['pres.p1.mf.sg'] += quad_e_consonant_forms ('n' + pres, 'n' + pres_long, 'n' + pres_short, 'LR');		# ndardar
#	forms['pres.p1.mf.sg'] += quad_e_consonant_forms ('in' + pres, 'in' + pres_long, 'in' + pres_short, 'RL');	# indardar


	forms['pres.p3.mf.pl'] = quad_vowel_forms ('ji' + pres_short + 'u', 'ji' + pres_short + 'u', '-', tv);		# 
#	forms['pres.p3.mf.pl'] += quad_vowel_forms ('j' + pres_short + 'u', 'j' + pres_short + 'u', 'LR'); 		# jdardru
#	forms['pres.p3.mf.pl'] += quad_vowel_forms ('i' + pres_short + 'u', 'i' + pres_short + 'u', 'RL');		# ~idardru

	forms['pres.p2.mf.pl'] = quad_vowel_forms ('ti' + pres_short + 'u', 'ti' + pres_short + 'u', '-', tv);	# 
#	forms['pres.p2.mf.pl'] += quad_vowel_forms (r[0] + pres_short + 'u', r[0] + pres_short + 'u', 'LR');		# ddardru
#	forms['pres.p2.mf.pl'] += quad_vowel_forms ('i' + r[0] + pres_short + 'u', 'i' + r[0] + pres_short + 'u', 'RL');	# ~iddardru

	forms['pres.p1.mf.pl'] = quad_vowel_forms ('ni' + pres_short + 'u', 'ni' + pres_short + 'u', '-', tv);		# 
#	forms['pres.p1.mf.pl'] += quad_vowel_forms ('n' + pres_short + 'u', 'in' + pres_short + 'u', 'LR');		# indardru
#	forms['pres.p1.mf.pl'] += quad_vowel_forms ('in' + pres_short + 'u', 'in' + pres_short + 'u', 'RL');		# indardru

	return forms;
#}


def quad_patt2_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	prefix_cons = 't';
	if (r[0] == 'ċ' or r[0] == 'd' or r[0] == 'ġ' or r[0] == 'n' or r[0] == 's' or r[0] == 'x'  or r[0] == 'ż' or r[0] == 'z') : #{
	# assimilation
		prefix_cons = 'i' + r[0];

	# no suffix
	pres = prefix_cons + r[0] + v[0] + r[1] + r[2] + v[1] + r[3]; # idardar, ikexkex

	# with suffix(es)
	if v[1] == 'e' : #{
		pres_long = prefix_cons + r[0] + v[0] + r[1] + r[2] + 'i' + r[3]; #ikexkixni
	else :
		pres_long = prefix_cons + r[0] + v[0] + r[1] + r[2] + v[1] + r[3]; #idardarni

	pres_short = prefix_cons + r[0] + v[0] + r[1] + r[2] + r[3]; # ikexkxek, idardrek

	
	suffix = 'u';

	forms['imp.p2.mf.sg'] = quad_e_consonant_forms (pres, pres_long, pres_short, '-', tv);
	forms['imp.p2.mf.pl'] = quad_vowel_forms (pres_short + 'u', pres_short + 'u', '-', tv);

	return forms;
#}


def quad_patt2_pp(root, vowels, pref): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	prefix_cons = 't';
	if (r[0] == 'ċ' or r[0] == 'd' or r[0] == 'ġ' or r[0] == 'n' or r[0] == 's' or r[0] == 'x'  or r[0] == 'ż' or r[0] == 'z') : #{
	# assimilation
		prefix_cons = 'i' + r[0];

	forms['pp.m.sg'] = [(pref + prefix_cons + r[0] + v[0] + r[1] + r[2] + v[1] + r[3], '-', '-')];	# mitkellem
	forms['pp.f.sg'] = [(pref + prefix_cons + r[0] + v[0] + r[1] + r[2] + r[3] + 'a', '-', '-')];	# mitkellma
	forms['pp.mf.pl'] = [(pref + prefix_cons + r[0] + v[0] + r[1] + r[2] + r[3] + 'in', '-', '-')];	# mitkellmin

	return forms;
#}



def main(stem): #{

	forms = {};

	if stem['theme'] == '1' : #{

		forms = quad_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(quad_pres(stem['root'], stem['vowel_perf'], stem['trans']));
#		if stem['trans'] == 'tv':
		forms.update(quad_imp(stem['root'], stem['vowel_perf'], stem['trans']));
		if stem['pp'] != '' : #{
			forms.update(quad_pp(stem['root'], stem['vowel_perf'], stem['pp']));
	#}

	elif stem['theme'] == '2' : #{
		forms = quad_patt2_past(stem['root'], stem['vowel_perf'], stem['trans']);
		forms.update(quad_patt2_pres(stem['root'], stem['vowel_perf'], stem['trans']));
#		if stem['trans'] == 'tv':
		forms.update(quad_patt2_imp(stem['root'], stem['vowel_perf'], stem['trans']));
		if stem['pp'] != '' : #{
			forms.update(quad_patt2_pp(stem['root'], stem['vowel_perf'], stem['pp']));
		#}

	#}

	return forms;

#}
