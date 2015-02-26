#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-


##-----------------------------------------------------------------------------##

# KaTaB		qasam, waqaf, qara(j), ġara(j), 
# KaTeB		qabeż, ħareġ
# KeTeB		xegħel, fehem, qered
# KeTaB		seraq, wera(j), ġema', ġama'
# KiTeB		kiser, wiżen
# KoToB		qorob, għolob
# KoTaB		għola(j), għoxa(j), ħola(j)


## ----------------------------------------------------------------------------##
## strong verbs
## ----------------------------------------------------------------------------##



def strong_pres_sg_forms (pres_sg, pres_sg_long, pres_sg_short, r, ok, lok, tv): #{

	if tv == 'iv' : #{
	 	forms = [(pres_sg, '-', r),
	 		 (pres_sg_long, 'S__qtalt/x', r), # was: just pres_sg
			 (pres_sg_short, 'S__fetħ/ilha', r),
			 (pres_sg_short, 'S__fetħ/ilhiex', r)];
		if lok == 'lok' : #{
			forms += [(pres_sg_long, 'S__xorob/li', r),
				  (pres_sg_long, 'S__xorob/lix', r)];
		#}
		else : #{
			forms += [(pres_sg_long, 'S__fetaħ/li', r),
				  (pres_sg_long, 'S__fetaħ/lix', r)];
		#}
	#}
	else : #{
		forms = [(pres_sg, '-', r),
			 (pres_sg_long, 'S__qtalt/x', r),  # same here
			 (pres_sg_long, 'S__fetaħ/ni', r),
			 (pres_sg_long, 'S__fetaħ/nix', r),
			 (pres_sg_short, 'S__fetħ/ilha', r),
			 (pres_sg_short, 'S__fetħ/ilhiex', r),
			 (pres_sg_long, 'S__qtaltu/hielha', r),
			 (pres_sg_long, 'S__qtaltu/hielhiex', r)];

		if ok == 'ok' : #{
			 forms += [(pres_sg_short, 'S__xorb/ok', r),
			           (pres_sg_short, 'S__xorb/okx', r)];
		#}
		else : #{
			 forms += [(pres_sg_short, 'S__fetħ/ek', r),
			 	   (pres_sg_short, 'S__fetħ/ekx', r)];
		#}

		if lok == 'lok' : #{
			forms += [(pres_sg_long, 'S__xorob/li', r),
				  (pres_sg_long, 'S__xorob/lix', r)];
		#}
		else : #{
			forms += [(pres_sg_long, 'S__fetaħ/li', r),
				  (pres_sg_long, 'S__fetaħ/lix', r)];
		#}
	#}

	return forms;
#}


def strong_pres_pl_forms (pres_pl, r, tv): #{

	if tv == 'iv' : #{
	 	forms = [(pres_pl, '-', r),
	 		 (pres_pl, 'S__qtalt/x', r),
			 (pres_pl, 'S__qtaltu/lha', r),
			 (pres_pl, 'S__qtaltu/lhiex', r)];
	#}
	else : #{
		forms = [(pres_pl, '-', r),
			 (pres_pl, 'S__qtalt/x', r),
			 (pres_pl, 'S__qtaltu/ni', r),
			 (pres_pl, 'S__qtaltu/nix', r),
			 (pres_pl, 'S__qtaltu/lha', r),
			 (pres_pl, 'S__qtaltu/lhiex', r),
			 (pres_pl, 'S__qtaltu/hielha', r),
			 (pres_pl, 'S__qtaltu/hielhiex', r)];
	#}

	return forms;
#}



def strong_patt3_pres_pl_forms (pres_pl, pres_pl_short, r, tv): #{

	if tv == 'iv' : #{
	 	forms = [(pres_pl, '-', r),
	 		 (pres_pl_short, 'S__qtalt/x', r),
			 (pres_pl_short, 'S__qtaltu/lha', r),
			 (pres_pl_short, 'S__qtaltu/lhiex', r)];
	#}
	else : #{
		forms = [(pres_pl, '-', r),
			 (pres_pl_short, 'S__qtalt/x', r),
			 (pres_pl_short, 'S__qtaltu/ni', r),
			 (pres_pl_short, 'S__qtaltu/nix', r),
			 (pres_pl_short, 'S__qtaltu/lha', r),
			 (pres_pl_short, 'S__qtaltu/lhiex', r),
			 (pres_pl_short, 'S__qtaltu/hielha', r),
			 (pres_pl_short, 'S__qtaltu/hielhiex', r)];
	#}

	return forms;
#}



# all past sg forms end with a consonant

def strong_past_p3_sg_m_forms (past_sg, past_sg_long, past_sg_short, r, ok, lok, tv): #{
# past.p3.sg.m
# sometimes 3 different forms of past.p3.sg.m are used: kiteb, kitib, kitb
# sometimes only 2: fetaħ, fetaħ, fetħ

	if tv == 'iv' : #{
	 	forms = [(past_sg, '-', r),
	 		 (past_sg_long, 'S__qtalt/x', r),
			 (past_sg_short, 'S__fetħ/ilha', r),
			 (past_sg_short, 'S__fetħ/ilhiex', r)];
		if lok == 'lok' : #{
			forms += [(past_sg_long, 'S__xorob/li', r),
			 	  (past_sg_long, 'S__xorob/lix', r)];
		#}
		else : #{
			forms += [(past_sg_long, 'S__fetaħ/li', r),
			 	  (past_sg_long, 'S__fetaħ/lix', r)];
		#}
	#}
	else : #{
		forms = [(past_sg, '-', r),
# seems it's 'ma kitibx', not 'ma kitebx'
# add (past_sg, 'S__qtalt/x', 'LR')? (not correct)
			 (past_sg_long, 'S__qtalt/x', r),
			 (past_sg_long, 'S__fetaħ/ni', r),
			 (past_sg_long, 'S__fetaħ/nix', r),
			 (past_sg_short, 'S__fetħ/ilha', r),
			 (past_sg_short, 'S__fetħ/ilhiex', r),
			 (past_sg_long, 'S__qtaltu/hielha', r),
			 (past_sg_long, 'S__qtaltu/hielhiex', r)];
	
		if ok == 'ok' : #{
			forms += [(past_sg_short, 'S__xorb/ok', r),
				  (past_sg_short, 'S__xorb/okx', r)];
		#}
		else : #{
			forms += [(past_sg_short, 'S__fetħ/ek', r),
				  (past_sg_short, 'S__fetħ/ekx', r)];
		#}

		if lok == 'lok' : #{
			forms += [(past_sg_long, 'S__xorob/li', r),
			 	  (past_sg_long, 'S__xorob/lix', r)];
		#}
		else : #{
			forms += [(past_sg_long, 'S__fetaħ/li', r),
			 	  (past_sg_long, 'S__fetaħ/lix', r)];
		#}
	#}

	return forms;
#}


def strong_past_sg_forms (past_sg, past_sg_suff, r, ok, tv): #{
# past.p3.f.sg, past.2.sg, past.1.sg
# for past.p3.sg.f different forms with and without suffixes: kitbet, kitbit
# for past.p2.mf.sg and past.p1.mf.sg only one form: ktibt

	if tv == 'iv' : #{
		if ok == 'ok' : #{
			forms = [(past_sg, '-', r),
			 	 (past_sg_suff, 'S__qtalt/x', r),
			 	 (past_sg_suff, 'S__xrobt/ilha', r),
			 	 (past_sg_suff, 'S__xrobt/ilhiex', r)];
		#}
		else : #{
			forms = [(past_sg, '-', r),
				 (past_sg_suff, 'S__qtalt/x', r),
				 (past_sg_suff, 'S__qtalt/ilha', r),
				 (past_sg_suff, 'S__qtalt/ilhiex', r)];
		#}
	#}
	else : #{
		if ok == 'ok' : #{
			forms = [(past_sg, '-', r),
# add (past_sg_suff, 'S__qtalt/x', 'LR')? (not correct)
			 	 (past_sg_suff, 'S__qtalt/x', r),
			 	 (past_sg_suff, 'S__xrobt/ni', r),
			 	 (past_sg_suff, 'S__xrobt/nix', r),
			 	 (past_sg_suff, 'S__xrobt/ilha', r),
			 	 (past_sg_suff, 'S__xrobt/ilhiex', r),
			 	 (past_sg_suff, 'S__qtaltu/hielha', r),
				 (past_sg_suff, 'S__qtaltu/hielhiex', r)];
		#}
		else : #{
			forms = [(past_sg, '-', r),
# add (past_sg_suff, 'S__qtalt/x', 'LR')? (not correct)
				 (past_sg_suff, 'S__qtalt/x', r),
				 (past_sg_suff, 'S__qtalt/ni', r),
				 (past_sg_suff, 'S__qtalt/nix', r),
				 (past_sg_suff, 'S__qtalt/ilha', r),
				 (past_sg_suff, 'S__qtalt/ilhiex', r),
				 (past_sg_suff, 'S__qtaltu/hielha', r),
				 (past_sg_suff, 'S__qtaltu/hielhiex', r)];
		#}
	#}

	return forms;
#}


# all past pl forms end with a vowel
def strong_past_pl_forms (past_pl, past_pl_suff, r, tv): #{

	if tv == 'iv' : #{
	 	forms = [(past_pl, '-', r),
	 		 (past_pl_suff, 'S__qtalt/x', r),
			 (past_pl_suff, 'S__qtaltu/lha', r),
			 (past_pl_suff, 'S__qtaltu/lhiex', r)];
	#}
	else : #{
		forms = [(past_pl, '-', r),
			 (past_pl_suff, 'S__qtalt/x', r),
			 (past_pl_suff, 'S__qtaltu/ni', r),
			 (past_pl_suff, 'S__qtaltu/nix', r),
			 (past_pl_suff, 'S__qtaltu/lha', r),
			 (past_pl_suff, 'S__qtaltu/lhiex', r),
			 (past_pl_suff, 'S__qtaltu/hielha', r),
			 (past_pl_suff, 'S__qtaltu/hielhiex', r)];
	#}

	return forms;
#}



## ----------------------------------------------------------------------------##
##  pattern 1
## ----------------------------------------------------------------------------##


def strong_patt1_pp(root, vowels, pref): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	# mudlam - is it a general mu-CC-a-C pattern?
	if pref == 'mu' : #{
		forms['pp.m.sg'] = [(pref + r[0] + r[1] + 'a' + r[2], '-', '-')] ;
		forms['pp.f.sg'] = [(pref + r[0] + r[1] + 'a' + r[2] + 'a', '-', '-')] ;
		forms['pp.mf.pl'] = [(pref + r[0] + r[1] + 'a' + r[2] + 'in', '-', '-')] ;
	#}
	else :#{
		forms['pp.m.sg'] = [(pref + r[0] + r[1] + 'u' + r[2], '-', '-')] ;
		forms['pp.f.sg'] = [(pref + r[0] + r[1] + 'u' + r[2] + 'a', '-', '-')] ;
		forms['pp.mf.pl'] = [(pref + r[0] + r[1] + 'u' + r[2] + 'in', '-', '-')] ;
	#}

	return forms;
#}


def strong_patt1_pprs(root, vowels): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};
	
	forms['pprs.m.sg'] = [(r[0] + 'ie' + r[1] + 'e' + r[2] , '-', '-')] ;
	# 79 ^qiegħda/qagħad<vblex><pprs><f><sg>$
	# but also 52 ^qegħda/*qegħda$
	# add LR form with first vowel 'e'?
	forms['pprs.f.sg'] = [(r[0] + 'ie' + r[1] + r[2] + 'a' , '-', '-')] ;
	# qegħdin, not qiegħdin
	forms['pprs.mf.pl'] = [(r[0] + 'e' + r[1] + r[2] + 'in' , '-', '-')] ;

	return forms;
#}




def strong_patt1_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels


	forms = {};
	ok = 'ek';
	lok = 'lek';


	if r[0] == 'w' :
		first_r = '';
	else :
		first_r = r[0];


	if v[1] == 'e' :
		vowel_preconssuff = 'i';
	else :
		vowel_preconssuff = v[1];

	if r[2] == 'għ' :
		pres_sg = v[0] + first_r + r[1] + v[1];
	else :
		pres_sg = v[0] + first_r + r[1] + v[1] + r[2];
	
	pres_sg_long = v[0] + first_r + r[1] + vowel_preconssuff + r[2];
	if vowel_preconssuff == 'o' :
		lok = 'lok';


# no:    128 ^jagħmlu/*jagħmlu$  etc.

	# When the second radical of the verb is 'l', 'm', 'n' or 'għ', a euphonic 
	# vowel must be inserted
	# but only (?) if r[0] != 'għ':
	# 128 ^jagħmlu/għamel<vblex><pres><p3><pl>/għamel<vblex><pres><p3><m><sg>+u<prn><p3><m><sg>$
	# but 24 ^jilagħbu/lagħab<vblex><pres><p3><pl>/lagħab<vblex><pres><p3><m><sg>+u<prn><p3><m><sg>$
	if (r[1] == 'l' or r[1] == 'm' or r[1] == 'n' or r[1] == 'għ') and (r[0] != 'għ') : #{
		pres_sg_short = v[0] + first_r + vowel_preconssuff + r[1] + r[2];
		if vowel_preconssuff == 'o' :
			ok = 'ok';
		pres_pl = v[0] + first_r + vowel_preconssuff + r[1] + r[2] + 'u'; # WHAT IF r[0] == 'w'?
	#}
	else : #{
		pres_sg_short = v[0] + first_r + r[1] + r[2];
		if v[0] == 'o' :
			ok = 'ok';
		pres_pl = v[0] + first_r + r[1] + r[2] + 'u';
	#}


	forms['pres.p3.m.sg'] = strong_pres_sg_forms(pres_sg, pres_sg_long, pres_sg_short, 'LR', ok, lok, tv);
	forms['pres.p3.m.sg'] += strong_pres_sg_forms('j' + pres_sg, 'j' + pres_sg_long, 'j' + pres_sg_short, 'LR', ok, lok, tv);
	forms['pres.p3.m.sg'] += strong_pres_sg_forms(pres_sg, pres_sg_long, pres_sg_short, 'RL', ok, lok, tv);

	forms['pres.p3.f.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p2.mf.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p1.mf.sg'] = strong_pres_sg_forms('n' + pres_sg, 'n' + pres_sg_long, 'n' + pres_sg_short, '-', ok, lok, tv);


	forms['pres.p3.mf.pl'] = strong_pres_pl_forms(pres_pl, 'LR', tv); # iDĦL-u iFTĦ-u
	forms['pres.p3.mf.pl'] += strong_pres_pl_forms('j' + pres_pl, 'LR', tv); # j-iDĦL-u j-iFTĦ-u
	forms['pres.p3.mf.pl'] += strong_pres_pl_forms(pres_pl, 'RL', tv); # iDĦL-u iFTĦ-u
	forms['pres.p2.mf.pl'] = strong_pres_pl_forms('t' + pres_pl, '-', tv);
	forms['pres.p1.mf.pl'] = strong_pres_pl_forms('n' + pres_pl, '-', tv);

	return forms;
#}



def strong_patt1_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels
	
	forms = {};

	ok = 'ek';
	lok = 'lek';

	if r[0] == 'w' :
		first_r = '';
	else :
		first_r = r[0];


	if v[1] == 'e' :
		vowel_preconssuff = 'i';
	else :
		vowel_preconssuff = v[1];


	if r[2] == 'għ' :
		pres_sg = v[0] + r[0] + r[1] + v[1];
	else :
		pres_sg = v[0] + r[0] + r[1] + v[1] + r[2];


	pres_sg_long = v[0] + first_r + r[1] + vowel_preconssuff + r[2];
	if vowel_preconssuff == 'o' :
		lok = 'lok';


	# When the second radical of the verb is 'l', 'm', 'n' or 'għ', a euphonic 
	# vowel must be inserted
	if r[1] == 'l' or r[1] == 'm' or r[1] == 'n' or r[1] == 'għ': #{
		pres_sg_short = v[0] + first_r + r[1] + vowel_preconssuff + r[2];
		if vowel_preconssuff == 'o' :
			ok = 'ok';
		pres_pl = v[0] + first_r + vowel_preconssuff + r[1] + r[2] + 'u'; # WHAT IF r[0] == 'w'?
	#}
	else : #{
		pres_sg_short = v[0] + first_r + r[1] + r[2];
		if v[0] == 'o' :
			ok = 'ok';
		pres_pl = v[0] + first_r + r[1] + r[2] + 'u';
	#}


	forms['imp.p2.mf.sg'] = strong_pres_sg_forms(pres_sg, pres_sg_long, pres_sg_short, '-', ok, lok, tv);
	forms['imp.p2.mf.pl'] = strong_pres_pl_forms(pres_pl, '-', tv);

	return forms ; 
#}



def strong_patt1_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'e' :
		vowel_preconssuff =  'i';
	else :
		vowel_preconssuff = v[1];
	

	if vowel_preconssuff == 'o' :
		ok = 'ok';
	else :
		ok = 'ek';


	if r[2] == 'għ' : #{
		final_r_p3_sg_m_free = '';
		final_r_p2_p1 = 'j';
	#}
	else : #{
		final_r_p3_sg_m_free = r[2];
		final_r_p2_p1 = r[2];
	#}


	# If the first radical is 'w' or 'għ' then we have a full disyllabic form
	if r[0] == 'w' or r[0] == 'għ': #{ 
		first_v_p2_p1 = v[0];
	else :
		first_v_p2_p1 = '';
	#}

# precons_form? (base for p2.mf.sg, p1.mf.sg, p2.mf.pl, p1.mf.pl)

	forms['past.p3.m.sg'] = strong_past_p3_sg_m_forms(r[0] + v[0] + r[1] + v[1] + final_r_p3_sg_m_free, r[0] + v[0] + r[1] + vowel_preconssuff + r[2], r[0] + v[0] + r[1] + r[2], '', ok, 'l' + ok, tv);

	if r[2] == 'għ' :
		forms['past.p3.f.sg'] = strong_past_sg_forms(r[0] + v[0] + r[1] + r[2] + 'et', r[0] + v[0] + r[1] + r[2] + 'et', '-', 'ek', tv); # -et after għ remains -et before suffixes? (semgħetu, qatgħetli qalbi); no ek/ok problems here
	elif v[1] == 'o' :
		forms['past.p3.f.sg'] = strong_past_sg_forms(r[0] + v[0] + r[1] + r[2] + 'ot', r[0] + v[0] + r[1] + r[2] + 'it', '-', 'ok', tv); # għoġbot, but għoġbitni, għoġbitek, għoġbitu,...
	else :
		forms['past.p3.f.sg'] = strong_past_sg_forms(r[0] + v[0] + r[1] + r[2] + 'et', r[0] + v[0] + r[1] + r[2] + 'it', '-', 'ek', tv);	# Omit second vowel of stem word

	forms['past.p2.mf.sg'] = strong_past_sg_forms(r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);	# Omit first vowel of stem word
	forms['past.p1.mf.sg'] = strong_past_sg_forms(r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);	# Omit first vowel of stem word

	forms['past.p3.mf.pl'] = strong_past_pl_forms(r[0] + v[0] + r[1] + r[2] + 'u', r[0] + v[0] + r[1] + r[2] + 'u', '-', tv);	# Omit second vowel of stem word

	forms['past.p2.mf.pl'] = strong_past_pl_forms(r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', '-', tv);	# Omit first vowel of stem word
	forms['past.p1.mf.pl'] = strong_past_pl_forms(r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'na', r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'nie', '-', tv);	# Omit first vowel of stem word

	return forms;
#}



## ----------------------------------------------------------------------------##
##  pattern 2
## oh, just use quadriliteral pattern 1!
## ----------------------------------------------------------------------------##


## ----------------------------------------------------------------------------##
##  pattern 3a bierek, ibierek and wieġeb, i/jwieġeb
## ----------------------------------------------------------------------------##


# pp, not pprs, according to TYM p. 155 
def strong_patt3a_pprs(root): #{
	r = root.split('-'); # radicals

	forms = {};
	
	forms['pp.m.sg'] = [('m' + r[0] + 'ie' + r[1] + 'e' + r[2] , '-', '-')] ;
	forms['pp.m.sg'] += [('im' + r[0] + 'ie' + r[1] + 'e' + r[2] , '-', 'LR')] ;
	forms['pp.f.sg'] = [('m' + r[0] + 'ie' + r[1] + r[2] + 'a' , '-', '-')] ;
	forms['pp.f.sg'] += [('im' + r[0] + 'ie' + r[1] + r[2] + 'a' , '-', 'LR')] ;
	forms['pp.mf.pl'] = [('m' + r[0] + 'e' + r[1] + r[2] + 'in' , '-', '-')] ;
	forms['pp.mf.pl'] += [('im' + r[0] + 'e' + r[1] + r[2] + 'in' , '-', 'LR')] ;

	return forms;
#}




def strong_patt3a_pres(root, tv): #{
	r = root.split('-'); # radicals

	forms = {};
	ok = 'ek';
	lok = 'lek';

	pres_sg = r[0] + 'ie' + r[1] + 'e' + r[2]; 
	pres_sg_long = r[0] + 'e' + r[1] + 'i' + r[2];
	pres_sg_short = r[0] + 'ie' + r[1] + r[2];
	pres_pl = r[0] + 'ie' + r[1] + r[2] + 'u'; 
	pres_pl_short = r[0] + 'e' + r[1] + r[2] + 'u'; 

	forms['pres.p3.m.sg'] = strong_pres_sg_forms('i' + pres_sg, 'i' + pres_sg_long, 'i' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p3.m.sg'] += strong_pres_sg_forms('j' + pres_sg, 'j' + pres_sg_long, 'j' + pres_sg_short, 'LR', ok, lok, tv);

	forms['pres.p3.f.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p2.mf.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p1.mf.sg'] = strong_pres_sg_forms('n' + pres_sg, 'n' + pres_sg_long, 'n' + pres_sg_short, '-', ok, lok, tv);


	forms['pres.p3.mf.pl'] = strong_patt3_pres_pl_forms('i' + pres_pl, 'i' + pres_pl_short, '-', tv);
	forms['pres.p3.mf.pl'] += strong_patt3_pres_pl_forms('j' + pres_pl, 'j' + pres_pl_short, 'LR', tv); 
	forms['pres.p2.mf.pl'] = strong_patt3_pres_pl_forms('t' + pres_pl, 't' + pres_pl, '-', tv);
	forms['pres.p1.mf.pl'] = strong_patt3_pres_pl_forms('n' + pres_pl, 'n' + pres_pl, '-', tv);

	return forms;
#}



def strong_patt3a_imp(root, tv): #{
	r = root.split('-'); # radicals
	
	forms = {};
	ok = 'ek';
	lok = 'lek';

	pres_sg = r[0] + 'ie' + r[1] + 'e' + r[2]; 
	pres_sg_long = r[0] + 'e' + r[1] + 'i' + r[2];
	pres_sg_short = r[0] + 'ie' + r[1] + r[2];
	pres_pl = r[0] + 'ie' + r[1] + r[2] + 'u'; 
	pres_pl_short = r[0] + 'e' + r[1] + r[2] + 'u'; 

	forms['imp.p2.mf.sg'] = strong_pres_sg_forms(pres_sg, pres_sg_long, pres_sg_short, '-', ok, lok, tv);
	forms['imp.p2.mf.pl'] = strong_patt3_pres_pl_forms(pres_pl, pres_pl_short, '-', tv);

	return forms ; 
#}



def strong_patt3a_past(root, tv): #{
	r = root.split('-'); # radicals

	forms = {};

	ok = 'ek';
	lok = 'lek';

	# I guess that the first vowel never disappears completely, as it's patt. 3

	forms['past.p3.m.sg'] = strong_past_p3_sg_m_forms(r[0] + 'ie' + r[1] + 'e' + r[2], r[0] + 'e' + r[1] + 'i' + r[2], r[0] + 'ie' + r[1] + r[2], '', ok, 'lok', tv);
	forms['past.p3.f.sg'] = strong_past_sg_forms(r[0] + 'ie' + r[1] + r[2] + 'et', r[0] + 'e' + r[1] + r[2] + 'it', '-', 'ek', tv);

	forms['past.p2.mf.sg'] = strong_past_sg_forms(r[0] + 'e' + r[1] + 'i' + r[2] + 't', r[0] + 'e' + r[1] + 'i' + r[2] + 't', '-', ok, tv);	
	forms['past.p1.mf.sg'] = strong_past_sg_forms(r[0] + 'e' + r[1] + 'i' + r[2] + 't', r[0] + 'e' + r[1] + 'i' + r[2] + 't', '-', ok, tv);	

	forms['past.p3.mf.pl'] = strong_past_pl_forms(r[0] + 'ie' + r[1] + r[2] + 'u', r[0] + 'e' + r[1] + r[2] + 'u', '-', tv);

	forms['past.p2.mf.pl'] = strong_past_pl_forms(r[0] + 'e' + r[1] + 'i' + r[2] + 'tu', r[0] + 'e' + r[1] + 'i' + r[2] + 'tu', '-', tv);	
	forms['past.p1.mf.pl'] = strong_past_pl_forms(r[0] + 'e' + r[1] + 'i' + r[2] + 'na', r[0] + 'e' + r[1] + 'i' + r[2] + 'nie', '-', tv);	

	return forms;
#}



## ----------------------------------------------------------------------------##
##  pattern 3b ħares, i/jħares
## ----------------------------------------------------------------------------##


def strong_patt3b_pp(root): #{
	r = root.split('-'); # radicals

	forms = {};
	
	forms['pp.m.sg'] = [('m' + r[0] + 'a' + r[1] + 'e' + r[2] , '-', '-')] ;
	forms['pp.m.sg'] += [('im' + r[0] + 'a' + r[1] + 'e' + r[2] , '-', 'LR')] ;
	forms['pp.f.sg'] = [('m' + r[0] + 'a' + r[1] + r[2] + 'a' , '-', '-')] ;
	forms['pp.f.sg'] += [('im' + r[0] + 'a' + r[1] + r[2] + 'a' , '-', 'LR')] ;
	forms['pp.mf.pl'] = [('m' + r[0] + 'a' + r[1] + r[2] + 'in' , '-', '-')] ;
	forms['pp.mf.pl'] += [('im' + r[0] + 'a' + r[1] + r[2] + 'in' , '-', 'LR')] ;

	return forms;
#}


# TODO: second vowel handling !!


def strong_patt3b_pres(root, tv): #{
	r = root.split('-'); # radicals

	forms = {};
	ok = 'ek';
	lok = 'lek';

	pres_sg = r[0] + 'a' + r[1] + 'e' + r[2]; 
	pres_sg_long = r[0] + 'a' + r[1] + 'i' + r[2];
	pres_sg_short = r[0] + 'a' + r[1] + r[2];
	pres_pl = r[0] + 'a' + r[1] + r[2] + 'u'; 
	pres_pl_short = r[0] + 'a' + r[1] + r[2] + 'u'; 

	forms['pres.p3.m.sg'] = strong_pres_sg_forms('i' + pres_sg, 'i' + pres_sg_long, 'i' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p3.m.sg'] += strong_pres_sg_forms('j' + pres_sg, 'j' + pres_sg_long, 'j' + pres_sg_short, 'LR', ok, lok, tv);

	forms['pres.p3.f.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p2.mf.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p1.mf.sg'] = strong_pres_sg_forms('n' + pres_sg, 'n' + pres_sg_long, 'n' + pres_sg_short, '-', ok, lok, tv);


	forms['pres.p3.mf.pl'] = strong_patt3_pres_pl_forms('i' + pres_pl, 'i' + pres_pl_short, '-', tv);
	forms['pres.p3.mf.pl'] += strong_patt3_pres_pl_forms('j' + pres_pl, 'j' + pres_pl_short, 'LR', tv); 
	forms['pres.p2.mf.pl'] = strong_patt3_pres_pl_forms('t' + pres_pl, 't' + pres_pl, '-', tv);
	forms['pres.p1.mf.pl'] = strong_patt3_pres_pl_forms('n' + pres_pl, 'n' + pres_pl, '-', tv);

	return forms;
#}



def strong_patt3b_imp(root, tv): #{
	r = root.split('-'); # radicals
	
	forms = {};
	ok = 'ek';
	lok = 'lek';

	pres_sg = r[0] + 'a' + r[1] + 'e' + r[2]; 
	pres_sg_long = r[0] + 'a' + r[1] + 'i' + r[2];
	pres_sg_short = r[0] + 'a' + r[1] + r[2];
	pres_pl = r[0] + 'a' + r[1] + r[2] + 'u'; 
	pres_pl_short = r[0] + 'a' + r[1] + r[2] + 'u'; 

	forms['imp.p2.mf.sg'] = strong_pres_sg_forms(pres_sg, pres_sg_long, pres_sg_short, '-', ok, lok, tv);
	forms['imp.p2.mf.pl'] = strong_patt3_pres_pl_forms(pres_pl, pres_pl_short, '-', tv);

	return forms ; 
#}



def strong_patt3b_past(root, tv): #{
	r = root.split('-'); # radicals

	forms = {};

	ok = 'ek';
	lok = 'lek';

	# I guess that the first vowel never disappears completely, as it's patt. 3

	forms['past.p3.m.sg'] = strong_past_p3_sg_m_forms(r[0] + 'a' + r[1] + 'e' + r[2], r[0] + 'a' + r[1] + 'i' + r[2], r[0] + 'a' + r[1] + r[2], '', ok, 'lok', tv);
	forms['past.p3.f.sg'] = strong_past_sg_forms(r[0] + 'a' + r[1] + r[2] + 'et', r[0] + 'a' + r[1] + r[2] + 'it', '-', 'ek', tv);

	forms['past.p2.mf.sg'] = strong_past_sg_forms(r[0] + 'a' + r[1] + 'i' + r[2] + 't', r[0] + 'a' + r[1] + 'i' + r[2] + 't', '-', ok, tv);	
	forms['past.p1.mf.sg'] = strong_past_sg_forms(r[0] + 'a' + r[1] + 'i' + r[2] + 't', r[0] + 'a' + r[1] + 'i' + r[2] + 't', '-', ok, tv);	

	forms['past.p3.mf.pl'] = strong_past_pl_forms(r[0] + 'a' + r[1] + r[2] + 'u', r[0] + 'a' + r[1] + r[2] + 'u', '-', tv);

	forms['past.p2.mf.pl'] = strong_past_pl_forms(r[0] + 'a' + r[1] + 'i' + r[2] + 'tu', r[0] + 'a' + r[1] + 'i' + r[2] + 'tu', '-', tv);	
	forms['past.p1.mf.pl'] = strong_past_pl_forms(r[0] + 'a' + r[1] + 'i' + r[2] + 'na', r[0] + 'a' + r[1] + 'i' + r[2] + 'nie', '-', tv);	

	return forms;
#}



## ----------------------------------------------------------------------------##
##  pattern 6a twieġeb, i/jtwieġeb
## ----------------------------------------------------------------------------##

# TODO: TYM p. 159 - list of forms in common use
# pp, not pprs, according to TYM p. 155 
#def strong_patt3a_pprs(root): #{
#	r = root.split('-'); # radicals
#
#	forms = {};
#	
#	forms['pprs.m.sg'] = [('m' + r[0] + 'ie' + r[1] + 'e' + r[2] , '-', '-')] ;
#	forms['pprs.m.sg'] = [('im' + r[0] + 'ie' + r[1] + 'e' + r[2] , '-', 'LR')] ;
#	forms['pprs.f.sg'] = [('m' + r[0] + 'ie' + r[1] + r[2] + 'a' , '-', '-')] ;
#	forms['pprs.f.sg'] = [('im' + r[0] + 'ie' + r[1] + r[2] + 'a' , '-', 'LR')] ;
#	forms['pprs.mf.pl'] = [('m' + r[0] + 'e' + r[1] + r[2] + 'in' , '-', '-')] ;
#	forms['pprs.mf.pl'] = [('im' + r[0] + 'e' + r[1] + r[2] + 'in' , '-', 'LR')] ;
#
#	return forms;
#}




def strong_patt6a_pres(root, tv): #{
	r = root.split('-'); # radicals

	forms = {};
	ok = 'ek';
	lok = 'lek';

	pres_sg = 'it' + r[0] + 'ie' + r[1] + 'e' + r[2]; 
	pres_sg_long = 'it' + r[0] + 'e' + r[1] + 'i' + r[2];
	pres_sg_short = 'it' + r[0] + 'ie' + r[1] + r[2];
	pres_pl = 'it' + r[0] + 'ie' + r[1] + r[2] + 'u'; 
	pres_pl_short = 'it' + r[0] + 'e' + r[1] + r[2] + 'u'; 

	forms['pres.p3.m.sg'] = strong_pres_sg_forms('j' + pres_sg, 'j' + pres_sg_long, 'j' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p3.f.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p2.mf.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p1.mf.sg'] = strong_pres_sg_forms('n' + pres_sg, 'n' + pres_sg_long, 'n' + pres_sg_short, '-', ok, lok, tv);

	forms['pres.p3.mf.pl'] = strong_patt3_pres_pl_forms('j' + pres_pl, 'j' + pres_pl_short, '-', tv); 
	forms['pres.p2.mf.pl'] = strong_patt3_pres_pl_forms('t' + pres_pl, 't' + pres_pl, '-', tv);
	forms['pres.p1.mf.pl'] = strong_patt3_pres_pl_forms('n' + pres_pl, 'n' + pres_pl, '-', tv);

	return forms;
#}



def strong_patt6a_imp(root, tv): #{
	r = root.split('-'); # radicals
	
	forms = {};
	ok = 'ek';
	lok = 'lek';

	pres_sg = 't' + r[0] + 'ie' + r[1] + 'e' + r[2]; 
	pres_sg_long = 't' + r[0] + 'e' + r[1] + 'i' + r[2];
	pres_sg_short = 't' + r[0] + 'ie' + r[1] + r[2];
	pres_pl = 't' + r[0] + 'ie' + r[1] + r[2] + 'u'; 
	pres_pl_short = 't' + r[0] + 'e' + r[1] + r[2] + 'u'; 

	forms['imp.p2.mf.sg'] = strong_pres_sg_forms(pres_sg, pres_sg_long, pres_sg_short, '-', ok, lok, tv);
	forms['imp.p2.mf.pl'] = strong_patt3_pres_pl_forms(pres_pl, pres_pl_short, '-', tv);

	return forms ; 
#}



def strong_patt6a_past(root, tv): #{
	r = root.split('-'); # radicals

	forms = {};

	ok = 'ek';
	lok = 'lek';

	# I guess that the first vowel never disappears completely, as it's patt. 6

	forms['past.p3.m.sg'] = strong_past_p3_sg_m_forms('t' + r[0] + 'ie' + r[1] + 'e' + r[2], 't' + r[0] + 'e' + r[1] + 'i' + r[2], 't' + r[0] + 'ie' + r[1] + r[2], '', ok, 'lok', tv);
	forms['past.p3.f.sg'] = strong_past_sg_forms('t' + r[0] + 'ie' + r[1] + r[2] + 'et', 't' + r[0] + 'e' + r[1] + r[2] + 'it', '-', 'ek', tv);

	forms['past.p2.mf.sg'] = strong_past_sg_forms('t' + r[0] + 'e' + r[1] + 'i' + r[2] + 't', 't' + r[0] + 'e' + r[1] + 'i' + r[2] + 't', '-', ok, tv);	
	forms['past.p1.mf.sg'] = strong_past_sg_forms('t' + r[0] + 'e' + r[1] + 'i' + r[2] + 't', 't' + r[0] + 'e' + r[1] + 'i' + r[2] + 't', '-', ok, tv);	

	forms['past.p3.mf.pl'] = strong_past_pl_forms('t' + r[0] + 'ie' + r[1] + r[2] + 'u', 't' + r[0] + 'e' + r[1] + r[2] + 'u', '-', tv);

	forms['past.p2.mf.pl'] = strong_past_pl_forms('t' + r[0] + 'e' + r[1] + 'i' + r[2] + 'tu', 't' + r[0] + 'e' + r[1] + 'i' + r[2] + 'tu', '-', tv);	
	forms['past.p1.mf.pl'] = strong_past_pl_forms(r[0] + 'e' + r[1] + 'i' + r[2] + 'na', 't' + r[0] + 'e' + r[1] + 'i' + r[2] + 'nie', '-', tv);	

	return forms;
#}



## ----------------------------------------------------------------------------##
##  pattern 7a - that is, 'n' prefix
## ----------------------------------------------------------------------------##


def strong_patt7a_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels


	forms = {};
	ok = 'ek';
	lok = 'lek';


	if r[0] == 'w' :
		first_r = 't';
	else :
		first_r = r[0];


	if v[1] == 'e' :
		vowel_preconssuff = 'i';
	else :
		vowel_preconssuff = v[1];

	if r[2] == 'għ' :
		pres_sg = 'in' + first_r + v[0] + r[1] + v[1];  # right?
	else :
		pres_sg = 'in' + first_r + v[0] + r[1] + v[1] + r[2];
	
	pres_sg_long = 'in' + first_r + v[0] + r[1] + vowel_preconssuff + r[2];
	if vowel_preconssuff == 'o' :
		lok = 'lok';

	pres_sg_short = 'in' + first_r + v[0] + r[1] + r[2];
	if v[0] == 'o' :
		ok = 'ok';
	pres_pl = 'in' + first_r + v[0] + r[1] + r[2] + 'u';


	forms['pres.p3.m.sg'] = strong_pres_sg_forms('j' + pres_sg, 'j' + pres_sg_long, 'j' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p3.f.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p2.mf.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p1.mf.sg'] = strong_pres_sg_forms('n' + pres_sg, 'n' + pres_sg_long, 'n' + pres_sg_short, '-', ok, lok, tv);


	forms['pres.p3.mf.pl'] = strong_pres_pl_forms('j' + pres_pl, '-', tv);
	forms['pres.p2.mf.pl'] = strong_pres_pl_forms('t' + pres_pl, '-', tv);
	forms['pres.p1.mf.pl'] = strong_pres_pl_forms('n' + pres_pl, '-', tv);

	return forms;
#}



def strong_patt7a_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels
	
	forms = {};

	ok = 'ek';
	lok = 'lek';

	if r[0] == 'w' :
		first_r = 't';
	else :
		first_r = r[0];


	if v[1] == 'e' :
		vowel_preconssuff = 'i';
	else :
		vowel_preconssuff = v[1];

	if r[2] == 'għ' :
		pres_sg = 'in' + first_r + v[0] + r[1] + v[1];
	else :
		pres_sg = 'in' + first_r + v[0] + r[1] + v[1] + r[2];


	pres_sg_long = 'in' + first_r + v[0] + r[1] + vowel_preconssuff + r[2];
	if vowel_preconssuff == 'o' :
		lok = 'lok';


	pres_sg_short = 'in' + first_r + v[0] + r[1] + r[2];
	if v[0] == 'o' :
		ok = 'ok';
	pres_pl = 'in' + first_r + v[0] + r[1] + r[2] + 'u';


	forms['imp.p2.mf.sg'] = strong_pres_sg_forms(pres_sg, pres_sg_long, pres_sg_short, '-', ok, lok, tv);
	forms['imp.p2.mf.pl'] = strong_pres_pl_forms(pres_pl, '-', tv);

	return forms ; 
#}



def strong_patt7a_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'e' :
		vowel_preconssuff =  'i';
	else :
		vowel_preconssuff = v[1];
	

	if vowel_preconssuff == 'o' :
		ok = 'ok';
	else :
		ok = 'ek';


	if r[2] == 'għ' : #{
		final_r_p3_sg_m_free = '';
		final_r_p2_p1 = 'j';
	#}
	else : #{
		final_r_p3_sg_m_free = r[2];
		final_r_p2_p1 = r[2];
	#}


	# If the first radical is 'w' or 'għ' then we have a full disyllabic form
	if r[0] == 'għ': #{   # not with 'w' 
		first_v_p2_p1 = v[0];
	else :
		first_v_p2_p1 = '';
	#}

	# TODO: if r[0] == 'w', then r[0] = 't'

# precons_form? (base for p2.mf.sg, p1.mf.sg, p2.mf.pl, p1.mf.pl)

	forms['past.p3.m.sg'] = strong_past_p3_sg_m_forms('n' + r[0] + v[0] + r[1] + v[1] + final_r_p3_sg_m_free, 'n' + r[0] + v[0] + r[1] + vowel_preconssuff + r[2], 'n' + r[0] + v[0] + r[1] + r[2], '', ok, 'l' + ok, tv);
	forms['past.p3.m.sg'] += strong_past_p3_sg_m_forms('in' + r[0] + v[0] + r[1] + v[1] + final_r_p3_sg_m_free, 'in' + r[0] + v[0] + r[1] + vowel_preconssuff + r[2], 'in' + r[0] + v[0] + r[1] + r[2], 'LR', ok, 'l' + ok, tv);

	if r[2] == 'għ' :
		forms['past.p3.f.sg'] = strong_past_sg_forms('n' + r[0] + v[0] + r[1] + r[2] + 'et', 'n' + r[0] + v[0] + r[1] + r[2] + 'et', '-', 'ek', tv); 
		forms['past.p3.f.sg'] += strong_past_sg_forms('in' + r[0] + v[0] + r[1] + r[2] + 'et', 'in' + r[0] + v[0] + r[1] + r[2] + 'et', 'LR', 'ek', tv); 
	elif v[1] == 'o' :
		forms['past.p3.f.sg'] = strong_past_sg_forms('n' + r[0] + v[0] + r[1] + r[2] + 'ot', 'n' + r[0] + v[0] + r[1] + r[2] + 'it', '-', 'ok', tv); 
		forms['past.p3.f.sg'] += strong_past_sg_forms('in' + r[0] + v[0] + r[1] + r[2] + 'ot', 'in' + r[0] + v[0] + r[1] + r[2] + 'it', 'LR', 'ok', tv); 
	else :
		forms['past.p3.f.sg'] = strong_past_sg_forms('n' + r[0] + v[0] + r[1] + r[2] + 'et', 'n' + r[0] + v[0] + r[1] + r[2] + 'it', '-', 'ek', tv);
		forms['past.p3.f.sg'] += strong_past_sg_forms('in' + r[0] + v[0] + r[1] + r[2] + 'et', 'in' + r[0] + v[0] + r[1] + r[2] + 'it', 'LR', 'ek', tv);

	forms['past.p2.mf.sg'] = strong_past_sg_forms('n' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'n' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);
	forms['past.p2.mf.sg'] += strong_past_sg_forms('in' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'in' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'LR', ok, tv);
	forms['past.p1.mf.sg'] = strong_past_sg_forms('n' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'n' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);
	forms['past.p1.mf.sg'] += strong_past_sg_forms('in' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'in' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);

	forms['past.p3.mf.pl'] = strong_past_pl_forms('n' + r[0] + v[0] + r[1] + r[2] + 'u', 'n' + r[0] + v[0] + r[1] + r[2] + 'u', '-', tv);
	forms['past.p3.mf.pl'] += strong_past_pl_forms('in' + r[0] + v[0] + r[1] + r[2] + 'u', 'in' + r[0] + v[0] + r[1] + r[2] + 'u', 'LR', tv);

	forms['past.p2.mf.pl'] = strong_past_pl_forms('n' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', 'n' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', '-', tv);
	forms['past.p2.mf.pl'] += strong_past_pl_forms('in' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', 'in' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', 'LR', tv);
	forms['past.p1.mf.pl'] = strong_past_pl_forms('n' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'na', 'n' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'nie', '-', tv);
	forms['past.p1.mf.pl'] += strong_past_pl_forms('in' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'na', 'in' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'nie', '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
##  pattern 7b - that is, 'nt' prefix
## ----------------------------------------------------------------------------##


def strong_patt7b_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels


	forms = {};
	ok = 'ek';
	lok = 'lek';


	if r[0] == 'w' :
		first_r = 't';
	else :
		first_r = r[0];


	if v[1] == 'e' :
		vowel_preconssuff = 'i';
	else :
		vowel_preconssuff = v[1];

	if r[2] == 'għ' :
		pres_sg = 'int' + first_r + v[0] + r[1] + v[1];  # right?
	else :
		pres_sg = 'int' + first_r + v[0] + r[1] + v[1] + r[2];
	
	pres_sg_long = 'int' + first_r + v[0] + r[1] + vowel_preconssuff + r[2];
	if vowel_preconssuff == 'o' :
		lok = 'lok';

	pres_sg_short = 'int' + first_r + v[0] + r[1] + r[2];
	if v[0] == 'o' :
		ok = 'ok';
	pres_pl = 'int' + first_r + v[0] + r[1] + r[2] + 'u';


	forms['pres.p3.m.sg'] = strong_pres_sg_forms('j' + pres_sg, 'j' + pres_sg_long, 'j' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p3.f.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p2.mf.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p1.mf.sg'] = strong_pres_sg_forms('n' + pres_sg, 'n' + pres_sg_long, 'n' + pres_sg_short, '-', ok, lok, tv);


	forms['pres.p3.mf.pl'] = strong_pres_pl_forms('j' + pres_pl, '-', tv);
	forms['pres.p2.mf.pl'] = strong_pres_pl_forms('t' + pres_pl, '-', tv);
	forms['pres.p1.mf.pl'] = strong_pres_pl_forms('n' + pres_pl, '-', tv);

	return forms;
#}



def strong_patt7b_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels
	
	forms = {};

	ok = 'ek';
	lok = 'lek';

	if r[0] == 'w' :
		first_r = 't';
	else :
		first_r = r[0];


	if v[1] == 'e' :
		vowel_preconssuff = 'i';
	else :
		vowel_preconssuff = v[1];

	if r[2] == 'għ' :
		pres_sg = 'int' + first_r + v[0] + r[1] + v[1];
	else :
		pres_sg = 'int' + first_r + v[0] + r[1] + v[1] + r[2];


	pres_sg_long = 'int' + first_r + v[0] + r[1] + vowel_preconssuff + r[2];
	if vowel_preconssuff == 'o' :
		lok = 'lok';


	pres_sg_short = 'int' + first_r + v[0] + r[1] + r[2];
	if v[0] == 'o' :
		ok = 'ok';
	pres_pl = 'int' + first_r + v[0] + r[1] + r[2] + 'u';


	forms['imp.p2.mf.sg'] = strong_pres_sg_forms(pres_sg, pres_sg_long, pres_sg_short, '-', ok, lok, tv);
	forms['imp.p2.mf.pl'] = strong_pres_pl_forms(pres_pl, '-', tv);

	return forms ; 
#}



def strong_patt7b_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'e' :
		vowel_preconssuff =  'i';
	else :
		vowel_preconssuff = v[1];
	

	if vowel_preconssuff == 'o' :
		ok = 'ok';
	else :
		ok = 'ek';


	if r[2] == 'għ' : #{
		final_r_p3_sg_m_free = '';
		final_r_p2_p1 = 'j';
	#}
	else : #{
		final_r_p3_sg_m_free = r[2];
		final_r_p2_p1 = r[2];
	#}


	# If the first radical is 'w' or 'għ' then we have a full disyllabic form
	if r[0] == 'għ': #{   # not with 'w' 
		first_v_p2_p1 = v[0];
	else :
		first_v_p2_p1 = '';
	#}

	# TODO: if r[0] == 'w', then r[0] = 't'

# precons_form? (base for p2.mf.sg, p1.mf.sg, p2.mf.pl, p1.mf.pl)

	forms['past.p3.m.sg'] = strong_past_p3_sg_m_forms('nt' + r[0] + v[0] + r[1] + v[1] + final_r_p3_sg_m_free, 'nt' + r[0] + v[0] + r[1] + vowel_preconssuff + r[2], 'n' + r[0] + v[0] + r[1] + r[2], '', ok, 'l' + ok, tv);
	forms['past.p3.m.sg'] += strong_past_p3_sg_m_forms('int' + r[0] + v[0] + r[1] + v[1] + final_r_p3_sg_m_free, 'int' + r[0] + v[0] + r[1] + vowel_preconssuff + r[2], 'in' + r[0] + v[0] + r[1] + r[2], 'LR', ok, 'l' + ok, tv);

	if r[2] == 'għ' :
		forms['past.p3.f.sg'] = strong_past_sg_forms('nt' + r[0] + v[0] + r[1] + r[2] + 'et', 'nt' + r[0] + v[0] + r[1] + r[2] + 'et', '-', 'ek', tv); 
		forms['past.p3.f.sg'] += strong_past_sg_forms('int' + r[0] + v[0] + r[1] + r[2] + 'et', 'int' + r[0] + v[0] + r[1] + r[2] + 'et', 'LR', 'ek', tv); 
	elif v[1] == 'o' :
		forms['past.p3.f.sg'] = strong_past_sg_forms('nt' + r[0] + v[0] + r[1] + r[2] + 'ot', 'nt' + r[0] + v[0] + r[1] + r[2] + 'it', '-', 'ok', tv); 
		forms['past.p3.f.sg'] += strong_past_sg_forms('int' + r[0] + v[0] + r[1] + r[2] + 'ot', 'int' + r[0] + v[0] + r[1] + r[2] + 'it', 'LR', 'ok', tv); 
	else :
		forms['past.p3.f.sg'] = strong_past_sg_forms('nt' + r[0] + v[0] + r[1] + r[2] + 'et', 'nt' + r[0] + v[0] + r[1] + r[2] + 'it', '-', 'ek', tv);
		forms['past.p3.f.sg'] += strong_past_sg_forms('int' + r[0] + v[0] + r[1] + r[2] + 'et', 'int' + r[0] + v[0] + r[1] + r[2] + 'it', 'LR', 'ek', tv);

	forms['past.p2.mf.sg'] = strong_past_sg_forms('nt' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'nt' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);
	forms['past.p2.mf.sg'] += strong_past_sg_forms('int' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'int' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'LR', ok, tv);
	forms['past.p1.mf.sg'] = strong_past_sg_forms('nt' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'nt' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);
	forms['past.p1.mf.sg'] += strong_past_sg_forms('int' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'int' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);

	forms['past.p3.mf.pl'] = strong_past_pl_forms('nt' + r[0] + v[0] + r[1] + r[2] + 'u', 'nt' + r[0] + v[0] + r[1] + r[2] + 'u', '-', tv);
	forms['past.p3.mf.pl'] += strong_past_pl_forms('int' + r[0] + v[0] + r[1] + r[2] + 'u', 'int' + r[0] + v[0] + r[1] + r[2] + 'u', 'LR', tv);

	forms['past.p2.mf.pl'] = strong_past_pl_forms('nt' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', 'nt' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', '-', tv);
	forms['past.p2.mf.pl'] += strong_past_pl_forms('int' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', 'int' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', 'LR', tv);
	forms['past.p1.mf.pl'] = strong_past_pl_forms('nt' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'na', 'nt' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'nie', '-', tv);
	forms['past.p1.mf.pl'] += strong_past_pl_forms('int' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'na', 'int' + r[0] + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'nie', '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
##  pattern 8a - that is, 't' infix
## ----------------------------------------------------------------------------##


def strong_patt8a_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels


	forms = {};
	ok = 'ek';
	lok = 'lek';


#	if r[0] == 'w' :
#		first_r = 't';
#	else :
	first_r = r[0] + 't';


	if v[1] == 'e' :
		vowel_preconssuff = 'i';
	else :
		vowel_preconssuff = v[1];

	if r[2] == 'għ' :
		pres_sg = 'i' + first_r + v[0] + r[1] + v[1];  # right?
	else :
		pres_sg = 'i' + first_r + v[0] + r[1] + v[1] + r[2];
	
	pres_sg_long = 'i' + first_r + v[0] + r[1] + vowel_preconssuff + r[2];
	if vowel_preconssuff == 'o' :
		lok = 'lok';

	pres_sg_short = 'i' + first_r + v[0] + r[1] + r[2];
	if v[0] == 'o' :
		ok = 'ok';
	pres_pl = 'i' + first_r + v[0] + r[1] + r[2] + 'u';


	forms['pres.p3.m.sg'] = strong_pres_sg_forms('j' + pres_sg, 'j' + pres_sg_long, 'j' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p3.f.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p2.mf.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p1.mf.sg'] = strong_pres_sg_forms('n' + pres_sg, 'n' + pres_sg_long, 'n' + pres_sg_short, '-', ok, lok, tv);


	forms['pres.p3.mf.pl'] = strong_pres_pl_forms('j' + pres_pl, '-', tv);
	forms['pres.p2.mf.pl'] = strong_pres_pl_forms('t' + pres_pl, '-', tv);
	forms['pres.p1.mf.pl'] = strong_pres_pl_forms('n' + pres_pl, '-', tv);

	return forms;
#}



def strong_patt8a_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels
	
	forms = {};

	ok = 'ek';
	lok = 'lek';

#	if r[0] == 'w' :
#		first_r = 't';
#	else :
	first_r = r[0] + 't';


	if v[1] == 'e' :
		vowel_preconssuff = 'i';
	else :
		vowel_preconssuff = v[1];

	if r[2] == 'għ' :
		pres_sg = 'i' + first_r + v[0] + r[1] + v[1];
	else :
		pres_sg = 'i' + first_r + v[0] + r[1] + v[1] + r[2];


	pres_sg_long = 'i' + first_r + v[0] + r[1] + vowel_preconssuff + r[2];
	if vowel_preconssuff == 'o' :
		lok = 'lok';


	pres_sg_short = 'i' + first_r + v[0] + r[1] + r[2];
	if v[0] == 'o' :
		ok = 'ok';
	pres_pl = 'i' + first_r + v[0] + r[1] + r[2] + 'u';


	forms['imp.p2.mf.sg'] = strong_pres_sg_forms(pres_sg, pres_sg_long, pres_sg_short, '-', ok, lok, tv);
	forms['imp.p2.mf.pl'] = strong_pres_pl_forms(pres_pl, '-', tv);

	return forms ; 
#}



def strong_patt8a_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'e' :
		vowel_preconssuff =  'i';
	else :
		vowel_preconssuff = v[1];
	

	if vowel_preconssuff == 'o' :
		ok = 'ok';
	else :
		ok = 'ek';


	if r[2] == 'għ' : #{
		final_r_p3_sg_m_free = '';
		final_r_p2_p1 = 'j';
	#}
	else : #{
		final_r_p3_sg_m_free = r[2];
		final_r_p2_p1 = r[2];
	#}


	# If the first radical is 'w' or 'għ' then we have a full disyllabic form
	if r[0] == 'għ': #{   # not with 'w' 
		first_v_p2_p1 = v[0];
	else :
		first_v_p2_p1 = '';
	#}

	# TODO: if r[0] == 'w', then r[0] = 't'

# precons_form? (base for p2.mf.sg, p1.mf.sg, p2.mf.pl, p1.mf.pl)

	forms['past.p3.m.sg'] = strong_past_p3_sg_m_forms(r[0] + 't' + v[0] + r[1] + v[1] + final_r_p3_sg_m_free, r[0] + 't' + v[0] + r[1] + vowel_preconssuff + r[2], r[0] + 't' + v[0] + r[1] + r[2], '', ok, 'l' + ok, tv);
	forms['past.p3.m.sg'] += strong_past_p3_sg_m_forms('i' + r[0] + 't' + v[0] + r[1] + v[1] + final_r_p3_sg_m_free, 'i' + r[0] + 't' + v[0] + r[1] + vowel_preconssuff + r[2], 'i' + r[0] + 't' + v[0] + r[1] + r[2], 'LR', ok, 'l' + ok, tv);

	if r[2] == 'għ' :
		forms['past.p3.f.sg'] = strong_past_sg_forms(r[0] + 't' + v[0] + r[1] + r[2] + 'et', r[0] + 't' + v[0] + r[1] + r[2] + 'et', '-', 'ek', tv); 
		forms['past.p3.f.sg'] += strong_past_sg_forms('i' + r[0] + 't' + v[0] + r[1] + r[2] + 'et', 'i' + r[0] + 't' + v[0] + r[1] + r[2] + 'et', 'LR', 'ek', tv); 
	elif v[1] == 'o' :
		forms['past.p3.f.sg'] = strong_past_sg_forms(r[0] + 't' + v[0] + r[1] + r[2] + 'ot', r[0] + 't' + v[0] + r[1] + r[2] + 'it', '-', 'ok', tv); 
		forms['past.p3.f.sg'] += strong_past_sg_forms('i' + r[0] + 't' + v[0] + r[1] + r[2] + 'ot', 'i' + r[0] + 't' + v[0] + r[1] + r[2] + 'it', 'LR', 'ok', tv); 
	else :
		forms['past.p3.f.sg'] = strong_past_sg_forms(r[0] + 't' + v[0] + r[1] + r[2] + 'et', r[0] + 't' + v[0] + r[1] + r[2] + 'it', '-', 'ek', tv);
		forms['past.p3.f.sg'] += strong_past_sg_forms('i' + r[0] + 't' + v[0] + r[1] + r[2] + 'et', 'i' + r[0] + 't' + v[0] + r[1] + r[2] + 'it', 'LR', 'ek', tv);

	forms['past.p2.mf.sg'] = strong_past_sg_forms(r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);
	forms['past.p2.mf.sg'] += strong_past_sg_forms('i' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'i' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'LR', ok, tv);
	forms['past.p1.mf.sg'] = strong_past_sg_forms(r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);
	forms['past.p1.mf.sg'] += strong_past_sg_forms('i' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'i' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);

	forms['past.p3.mf.pl'] = strong_past_pl_forms(r[0] + 't' + v[0] + r[1] + r[2] + 'u', r[0] + 't' + v[0] + r[1] + r[2] + 'u', '-', tv);
	forms['past.p3.mf.pl'] += strong_past_pl_forms('i' + r[0] + 't' + v[0] + r[1] + r[2] + 'u', 'i' + r[0] + 't' + v[0] + r[1] + r[2] + 'u', 'LR', tv);

	forms['past.p2.mf.pl'] = strong_past_pl_forms(r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', '-', tv);
	forms['past.p2.mf.pl'] += strong_past_pl_forms('i' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', 'i' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', 'LR', tv);
	forms['past.p1.mf.pl'] = strong_past_pl_forms(r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'na', r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'nie', '-', tv);
	forms['past.p1.mf.pl'] += strong_past_pl_forms('i' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'na', 'i' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'nie', '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##
##  pattern 8b - that is, 'n' prefix + 't' infix
## ----------------------------------------------------------------------------##


def strong_patt8b_pres(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels


	forms = {};
	ok = 'ek';
	lok = 'lek';


#	if r[0] == 'w' :
#		first_r = 't';
#	else :
	first_r = r[0] + 't';


	if v[1] == 'e' :
		vowel_preconssuff = 'i';
	else :
		vowel_preconssuff = v[1];

	if r[2] == 'għ' :
		pres_sg = 'in' + first_r + v[0] + r[1] + v[1];  # right?
	else :
		pres_sg = 'in' + first_r + v[0] + r[1] + v[1] + r[2];
	
	pres_sg_long = 'in' + first_r + v[0] + r[1] + vowel_preconssuff + r[2];
	if vowel_preconssuff == 'o' :
		lok = 'lok';

	pres_sg_short = 'in' + first_r + v[0] + r[1] + r[2];
	if v[0] == 'o' :
		ok = 'ok';
	pres_pl = 'in' + first_r + v[0] + r[1] + r[2] + 'u';


	forms['pres.p3.m.sg'] = strong_pres_sg_forms('j' + pres_sg, 'j' + pres_sg_long, 'j' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p3.f.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p2.mf.sg'] = strong_pres_sg_forms('t' + pres_sg, 't' + pres_sg_long, 't' + pres_sg_short, '-', ok, lok, tv);
	forms['pres.p1.mf.sg'] = strong_pres_sg_forms('n' + pres_sg, 'n' + pres_sg_long, 'n' + pres_sg_short, '-', ok, lok, tv);


	forms['pres.p3.mf.pl'] = strong_pres_pl_forms('j' + pres_pl, '-', tv);
	forms['pres.p2.mf.pl'] = strong_pres_pl_forms('t' + pres_pl, '-', tv);
	forms['pres.p1.mf.pl'] = strong_pres_pl_forms('n' + pres_pl, '-', tv);

	return forms;
#}



def strong_patt8b_imp(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels
	
	forms = {};

	ok = 'ek';
	lok = 'lek';

#	if r[0] == 'w' :
#		first_r = 't';
#	else :
	first_r = r[0] + 't';


	if v[1] == 'e' :
		vowel_preconssuff = 'i';
	else :
		vowel_preconssuff = v[1];

	if r[2] == 'għ' :
		pres_sg = 'in' + first_r + v[0] + r[1] + v[1];
	else :
		pres_sg = 'in' + first_r + v[0] + r[1] + v[1] + r[2];


	pres_sg_long = 'in' + first_r + v[0] + r[1] + vowel_preconssuff + r[2];
	if vowel_preconssuff == 'o' :
		lok = 'lok';


	pres_sg_short = 'in' + first_r + v[0] + r[1] + r[2];
	if v[0] == 'o' :
		ok = 'ok';
	pres_pl = 'in' + first_r + v[0] + r[1] + r[2] + 'u';


	forms['imp.p2.mf.sg'] = strong_pres_sg_forms(pres_sg, pres_sg_long, pres_sg_short, '-', ok, lok, tv);
	forms['imp.p2.mf.pl'] = strong_pres_pl_forms(pres_pl, '-', tv);

	return forms ; 
#}



def strong_patt8b_past(root, vowels, tv): #{
	r = root.split('-'); # radicals
	v = vowels.split('-'); # vowels

	forms = {};

	if v[1] == 'e' :
		vowel_preconssuff =  'i';
	else :
		vowel_preconssuff = v[1];
	

	if vowel_preconssuff == 'o' :
		ok = 'ok';
	else :
		ok = 'ek';


	if r[2] == 'għ' : #{
		final_r_p3_sg_m_free = '';
		final_r_p2_p1 = 'j';
	#}
	else : #{
		final_r_p3_sg_m_free = r[2];
		final_r_p2_p1 = r[2];
	#}


	# If the first radical is 'w' or 'għ' then we have a full disyllabic form
	if r[0] == 'għ': #{   # not with 'w' 
		first_v_p2_p1 = v[0];
	else :
		first_v_p2_p1 = '';
	#}

	# TODO: if r[0] == 'w', then r[0] = 't'

# precons_form? (base for p2.mf.sg, p1.mf.sg, p2.mf.pl, p1.mf.pl)

	forms['past.p3.m.sg'] = strong_past_p3_sg_m_forms('n' + r[0] + 't' + v[0] + r[1] + v[1] + final_r_p3_sg_m_free, 'n' + r[0] + 't' + v[0] + r[1] + vowel_preconssuff + r[2], 'n' + r[0] + 't' + v[0] + r[1] + r[2], '', ok, 'l' + ok, tv);
	forms['past.p3.m.sg'] += strong_past_p3_sg_m_forms('in' + r[0] + 't' + v[0] + r[1] + v[1] + final_r_p3_sg_m_free, 'in' + r[0] + 't' + v[0] + r[1] + vowel_preconssuff + r[2], 'in' + r[0] + 't' + v[0] + r[1] + r[2], 'LR', ok, 'l' + ok, tv);

	if r[2] == 'għ' :
		forms['past.p3.f.sg'] = strong_past_sg_forms('n' + r[0] + 't' + v[0] + r[1] + r[2] + 'et', 'n' + r[0] + 't' + v[0] + r[1] + r[2] + 'et', '-', 'ek', tv); 
		forms['past.p3.f.sg'] += strong_past_sg_forms('in' + r[0] + 't' + v[0] + r[1] + r[2] + 'et', 'in' + r[0] + 't' + v[0] + r[1] + r[2] + 'et', 'LR', 'ek', tv); 
	elif v[1] == 'o' :
		forms['past.p3.f.sg'] = strong_past_sg_forms('n' + r[0] + 't' + v[0] + r[1] + r[2] + 'ot', 'n' + r[0] + 't' + v[0] + r[1] + r[2] + 'it', '-', 'ok', tv); 
		forms['past.p3.f.sg'] += strong_past_sg_forms('in' + r[0] + 't' + v[0] + r[1] + r[2] + 'ot', 'in' + r[0] + 't' + v[0] + r[1] + r[2] + 'it', 'LR', 'ok', tv); 
	else :
		forms['past.p3.f.sg'] = strong_past_sg_forms('n' + r[0] + 't' + v[0] + r[1] + r[2] + 'et', 'n' + r[0] + 't' + v[0] + r[1] + r[2] + 'it', '-', 'ek', tv);
		forms['past.p3.f.sg'] += strong_past_sg_forms('in' + r[0] + 't' + v[0] + r[1] + r[2] + 'et', 'in' + r[0] + 't' + v[0] + r[1] + r[2] + 'it', 'LR', 'ek', tv);

	forms['past.p2.mf.sg'] = strong_past_sg_forms('n' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'n' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);
	forms['past.p2.mf.sg'] += strong_past_sg_forms('in' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'in' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'LR', ok, tv);
	forms['past.p1.mf.sg'] = strong_past_sg_forms('n' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'n' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);
	forms['past.p1.mf.sg'] += strong_past_sg_forms('in' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', 'in' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 't', '-', ok, tv);

	forms['past.p3.mf.pl'] = strong_past_pl_forms('n' + r[0] + 't' + v[0] + r[1] + r[2] + 'u', 'n' + r[0] + 't' + v[0] + r[1] + r[2] + 'u', '-', tv);
	forms['past.p3.mf.pl'] += strong_past_pl_forms('in' + r[0] + 't' + v[0] + r[1] + r[2] + 'u', 'in' + r[0] + 't' + v[0] + r[1] + r[2] + 'u', 'LR', tv);

	forms['past.p2.mf.pl'] = strong_past_pl_forms('n' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', 'n' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', '-', tv);
	forms['past.p2.mf.pl'] += strong_past_pl_forms('in' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', 'in' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'tu', 'LR', tv);
	forms['past.p1.mf.pl'] = strong_past_pl_forms('n' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'na', 'n' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'nie', '-', tv);
	forms['past.p1.mf.pl'] += strong_past_pl_forms('in' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'na', 'in' + r[0] + 't' + first_v_p2_p1 + r[1] + vowel_preconssuff + final_r_p2_p1 + 'nie', '-', tv);

	return forms;
#}



## ----------------------------------------------------------------------------##


def main(stem): #{

	forms = {};

	if stem['theme'] == '1' : #{
		forms.update(strong_patt1_past(stem['root'], stem['vowel_perf'], stem['trans']));

		if stem['trans'] == 'iv' : #or stem['stem'] in tv_with_pprs:
			forms.update(strong_patt1_pprs(stem['root'], stem['vowel_perf']));

		if stem['pp'] != '' : 
			forms.update(strong_patt1_pp(stem['root'], stem['vowel_perf'], stem['pp']));

		if stem['vowel_impf'] != '': 
			forms.update(strong_patt1_pres(stem['root'], stem['vowel_impf'], stem['trans']));
#			if stem['trans'] == 'tv':
			forms.update(strong_patt1_imp(stem['root'], stem['vowel_impf'], stem['trans']));

	#}

	if stem['theme'] == '3a' : #{
		forms.update(strong_patt3a_past(stem['root'],  stem['trans']));
		forms.update(strong_patt3a_pprs(stem['root']));
		forms.update(strong_patt3a_pres(stem['root'], stem['trans']));
		forms.update(strong_patt3a_imp(stem['root'], stem['trans']));

	#}


	if stem['theme'] == '3b' : #{
		forms.update(strong_patt3b_past(stem['root'],  stem['trans']));
		forms.update(strong_patt3b_pp(stem['root']));
		forms.update(strong_patt3b_pres(stem['root'], stem['trans']));
		forms.update(strong_patt3b_imp(stem['root'], stem['trans']));

	#}


	if stem['theme'] == '6a' : #{
		forms.update(strong_patt3a_past(stem['root'],  stem['trans']));
		forms.update(strong_patt3a_pprs(stem['root']));
		forms.update(strong_patt3a_pres(stem['root'], stem['trans']));
		forms.update(strong_patt3a_imp(stem['root'], stem['trans']));

	#}


	if stem['theme'] == '7a' : #{
		forms.update(strong_patt7a_past(stem['root'],  stem['vowel_perf'], stem['trans']));
		forms.update(strong_patt7a_pres(stem['root'], stem['vowel_perf'], stem['trans']));
		forms.update(strong_patt7a_imp(stem['root'], stem['vowel_perf'], stem['trans']));

	#}


	if stem['theme'] == '7b' : #{
		forms.update(strong_patt7b_past(stem['root'],  stem['vowel_perf'], stem['trans']));
		forms.update(strong_patt7b_pres(stem['root'], stem['vowel_perf'], stem['trans']));
		forms.update(strong_patt7b_imp(stem['root'], stem['vowel_perf'], stem['trans']));

	#}


	if stem['theme'] == '8a' : #{
		forms.update(strong_patt8a_past(stem['root'],  stem['vowel_perf'], stem['trans']));
		forms.update(strong_patt8a_pres(stem['root'], stem['vowel_perf'], stem['trans']));
		forms.update(strong_patt8a_imp(stem['root'], stem['vowel_perf'], stem['trans']));

	#}


	if stem['theme'] == '8b' : #{
		forms.update(strong_patt8b_past(stem['root'],  stem['vowel_perf'], stem['trans']));
		forms.update(strong_patt8b_pres(stem['root'], stem['vowel_perf'], stem['trans']));
		forms.update(strong_patt8b_imp(stem['root'], stem['vowel_perf'], stem['trans']));

	#}

	return forms;

#}

