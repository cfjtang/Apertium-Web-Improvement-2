#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-


## ----------------------------------------------------------------------------##
## irregular
## ----------------------------------------------------------------------------##



def irreg_consonant_forms (form_sg, form_sg_suff, r, ek): #{

	if ek == 'ok' :
		forms = [(form_sg, '-', r),
			 (form_sg_suff, 'S__qtalt/x', r),
			 (form_sg_suff, 'S__xrobt/ni', r),
			 (form_sg_suff, 'S__xrobt/nix', r),
			 (form_sg_suff, 'S__xrobt/ilha', r),
			 (form_sg_suff, 'S__xrobt/ilhiex', r),
			 (form_sg_suff, 'S__qtaltu/hielha', r),
			 (form_sg_suff, 'S__qtaltu/hielhiex', r)];
	else :
		forms = [(form_sg, '-', r),
			 (form_sg_suff, 'S__qtalt/x', r),
			 (form_sg_suff, 'S__qtalt/ni', r),
			 (form_sg_suff, 'S__qtalt/nix', r),
			 (form_sg_suff, 'S__qtalt/ilha', r),
			 (form_sg_suff, 'S__qtalt/ilhiex', r),
			 (form_sg_suff, 'S__qtaltu/hielha', r),
			 (form_sg_suff, 'S__qtaltu/hielhiex', r)];

	return forms;
#}


def irreg_iv_consonant_forms (form_sg, form_sg_suff, r, ek): #{

	if ek == 'ok' :
		forms = [(form_sg, '-', r),
			 (form_sg_suff, 'S__qtalt/x', r),
			 (form_sg_suff, 'S__xrobt/ilha', r),
			 (form_sg_suff, 'S__xrobt/ilhiex', r)];
	else :
		forms = [(form_sg, '-', r),
			 (form_sg_suff, 'S__qtalt/x', r),
			 (form_sg_suff, 'S__qtalt/ilha', r),
			 (form_sg_suff, 'S__qtalt/ilhiex', r)];

	return forms;
#}


def irreg_vowel_forms (form_pl, form_pl_suff, r): #{

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


def irreg_iv_vowel_forms (form_pl, form_pl_suff, r): #{

	forms = [(form_pl, '-', r),
		 (form_pl_suff, 'S__qtalt/x', r),
		 (form_pl_suff, 'S__qtaltu/lha', r),
		 (form_pl_suff, 'S__qtaltu/lhiex', r)];

	return forms;
#}


def irregular_forms (stem): #{

	forms = {};

	if stem == 'ta' : #{
		forms['past.p3.m.sg'] = irreg_vowel_forms('ta', 'ta', '-');
		forms['past.p3.f.sg'] = irreg_consonant_forms('tat', 'tat', '-', 'ek');
		forms['past.p2.mf.sg'] = irreg_consonant_forms('tajt', 'tajt', '-', 'ek');	
		forms['past.p1.mf.sg'] = irreg_consonant_forms('tajt', 'tajt', '-', 'ek');
		forms['past.p3.mf.pl'] = irreg_vowel_forms('taw', 'taw', '-');
		forms['past.p2.mf.pl'] = irreg_vowel_forms('tajtu', 'tajtu', '-');
		forms['past.p1.mf.pl'] = irreg_vowel_forms('tajna', 'tajnie', '-');

		forms['pres.p3.m.sg'] = irreg_vowel_forms('jagħti', 'jagħti', '-');
		forms['pres.p3.f.sg'] = irreg_vowel_forms('tagħti', 'tagħti', '-');
		forms['pres.p2.mf.sg'] = irreg_vowel_forms('tagħti', 'tagħti', '-');
		forms['pres.p1.mf.sg'] = irreg_vowel_forms('nagħti', 'nagħti', '-');
		forms['pres.p3.mf.pl'] = irreg_vowel_forms('jagħtu', 'jagħtu', '-');
		forms['pres.p2.mf.pl'] = irreg_vowel_forms('tagħtu', 'tagħtu', '-');
		forms['pres.p1.mf.pl'] = irreg_vowel_forms('nagħtu', 'nagħtu', '-');

		forms['imp.p2.mf.sg'] = irreg_vowel_forms('agħti', 'agħti', '-');
		forms['imp.p2.mf.pl'] = irreg_vowel_forms('agħtu', 'agħtu', '-');

		forms['pp.m.sg'] = [('mogħti', '-', '-')] ;
		forms['pp.f.sg'] = [('mogħtija', '-', '-')] ;
		forms['pp.mf.pl'] = [('mogħtijin', '-', '-')] ;
	#}

	elif stem == 'ħa' : #{
		# ħa, but ħad-ha
		# what with negation suffix? 
		# only ma ħadx or both ma ħadx and ma hax? 
		forms['past.p3.m.sg'] = irreg_consonant_forms('ħa', 'ħad', '-', 'ek');
		forms['past.p3.f.sg'] = irreg_consonant_forms('ħadet', 'ħadit', '-', 'ek');
		forms['past.p2.mf.sg'] = irreg_consonant_forms('ħadt', 'ħadt', '-', 'ek');	
		forms['past.p1.mf.sg'] = irreg_consonant_forms('ħadt', 'ħadt', '-', 'ek');
		forms['past.p3.mf.pl'] = irreg_vowel_forms('ħadu', 'ħadu', '-');
		forms['past.p2.mf.pl'] = irreg_vowel_forms('ħadtu', 'ħadtu', '-');
		forms['past.p1.mf.pl'] = irreg_vowel_forms('ħadna', 'ħadnie', '-');

		# hm. jieħu with suff: jiħdu-? jieħdu-? jiħu-? jieħu-?
		# oh. jeħu-! TYM 201
		# min jagħti jieħu, min ma jagħtix ma jeħux
		forms['pres.p3.m.sg'] = irreg_vowel_forms('jieħu', 'jeħu', '-');
		forms['pres.p3.f.sg'] = irreg_vowel_forms('tieħu', 'teħu', '-');
		forms['pres.p2.mf.sg'] = irreg_vowel_forms('tieħu', 'teħu', '-');
		forms['pres.p1.mf.sg'] = irreg_vowel_forms('nieħu', 'neħu', '-');
		# so here: jieħdu with suffixes - jeħdu-?
		# we have
		# 2 ^jeħduh/*jeħduh$, 1 ^jeħduli/*jeħduli$, 1 ^jeħduhulhom/*jeħduhulhom$
		# but we also have
		# 1 ^nieħduh/*nieħduh$, 1 ^jieħdux/*jieħdux$, 1 ^jieħduh/*jieħduh$
		# so probably there should also be LR-only form jieħdu-
		forms['pres.p3.mf.pl'] = irreg_vowel_forms('jieħdu', 'jeħdu', '-');
		forms['pres.p3.mf.pl'] += irreg_vowel_forms('jieħdu', 'jieħdu', 'LR');
		forms['pres.p2.mf.pl'] = irreg_vowel_forms('tieħdu', 'teħdu', '-');
		forms['pres.p2.mf.pl'] = irreg_vowel_forms('tieħdu', 'tieħdu', '-');
		forms['pres.p1.mf.pl'] = irreg_vowel_forms('nieħdu', 'neħdu', '-');
		forms['pres.p1.mf.pl'] = irreg_vowel_forms('nieħdu', 'nieħdu', '-');

		forms['imp.p2.mf.sg'] = irreg_consonant_forms('ħu', 'ħud', '-', 'ek');
		forms['imp.p2.mf.pl'] = irreg_vowel_forms('ħudu', 'ħudu', '-');

		forms['pp.m.sg'] = [('meħud', '-', '-')] ;
		forms['pp.f.sg'] = [('meħuda', '-', '-')] ;
		forms['pp.mf.pl'] = [('meħudin', '-', '-')] ;
	#}

	elif stem == 'ra' : #{
		forms['past.p3.m.sg'] = irreg_vowel_forms('ra', 'ra', '-');
		forms['past.p3.f.sg'] = irreg_consonant_forms('rat', 'rat', '-', 'ek');
		forms['past.p2.mf.sg'] = irreg_consonant_forms('rajt', 'rajt', '-', 'ek');	
		forms['past.p1.mf.sg'] = irreg_consonant_forms('rajt', 'rajt', '-', 'ek');
		forms['past.p3.mf.pl'] = irreg_vowel_forms('raw', 'raw', '-');
		forms['past.p2.mf.pl'] = irreg_vowel_forms('rajtu', 'rajtu', '-');
		forms['past.p1.mf.pl'] = irreg_vowel_forms('rajna', 'rajnie', '-');

		forms['pres.p3.m.sg'] = irreg_vowel_forms('jara', 'jara', '-');
		forms['pres.p3.f.sg'] = irreg_vowel_forms('tara', 'tara', '-');
		forms['pres.p2.mf.sg'] = irreg_vowel_forms('tara', 'tara', '-');
		forms['pres.p1.mf.sg'] = irreg_vowel_forms('nara', 'nara', '-');
		forms['pres.p3.mf.pl'] = irreg_vowel_forms('jaraw', 'jaraw', '-');
		forms['pres.p2.mf.pl'] = irreg_vowel_forms('taraw', 'taraw', '-');
		forms['pres.p1.mf.pl'] = irreg_vowel_forms('naraw', 'naraw', '-');

		forms['imp.p2.mf.sg'] = irreg_vowel_forms('ara', 'ara', '-');
		forms['imp.p2.mf.pl'] = irreg_vowel_forms('araw', 'araw', '-');

		forms['pp.m.sg'] = [('muri', '-', '-')] ;
		forms['pp.f.sg'] = [('murija', '-', '-')] ;
		forms['pp.mf.pl'] = [('murijin', '-', '-')] ;
	#}

	elif stem == 'qal' : #{
		# qal-hu-li or qal-u-li? 
		forms['past.p3.m.sg'] = irreg_consonant_forms('qal', 'qal', '-', 'ek');
		forms['past.p3.f.sg'] = irreg_consonant_forms('qalet', 'qalit', '-', 'ek');
		forms['past.p2.mf.sg'] = irreg_consonant_forms('għedt', 'għedt', '-', 'ek');
		forms['past.p2.mf.sg'] += irreg_consonant_forms('għidt', 'għidt', 'LR', 'ek');	
		forms['past.p1.mf.sg'] = irreg_consonant_forms('għedt', 'għedt', '-', 'ek');
		forms['past.p1.mf.sg'] += irreg_consonant_forms('għidt', 'għidt', 'LR', 'ek');

		forms['past.p3.mf.pl'] = irreg_vowel_forms('qalu', 'qalu', '-');
		forms['past.p2.mf.pl'] = irreg_vowel_forms('għedtu', 'għedtu', '-');
		forms['past.p2.mf.pl'] += irreg_vowel_forms('għidtu', 'għidtu', 'LR');
		forms['past.p1.mf.pl'] = irreg_vowel_forms('għedna', 'għednie', '-');
		forms['past.p1.mf.pl'] += irreg_vowel_forms('għidna', 'għidnie', 'LR');

		forms['pres.p3.m.sg'] = irreg_consonant_forms('igħid', 'igħid', 'LR', 'ek');
		forms['pres.p3.m.sg'] += irreg_consonant_forms('jgħid', 'jgħid', 'LR', 'ek');
		forms['pres.p3.m.sg'] += irreg_consonant_forms('igħid', 'igħid', 'RL', 'ek');

		forms['pres.p3.f.sg'] = irreg_consonant_forms('tgħid', 'tgħid', '-', 'ek');
		forms['pres.p2.mf.sg'] = irreg_consonant_forms('tgħid', 'tgħid', '-', 'ek');
		forms['pres.p1.mf.sg'] = irreg_consonant_forms('ngħid', 'ngħid', '-', 'ek');

		forms['pres.p3.mf.pl'] = irreg_vowel_forms('igħidu', 'igħidu', 'LR');
		forms['pres.p3.mf.pl'] += irreg_vowel_forms('jgħidu', 'jgħidu', 'LR');
		forms['pres.p3.mf.pl'] += irreg_vowel_forms('igħidu', 'igħidu', 'RL');

		forms['pres.p2.mf.pl'] = irreg_vowel_forms('tgħidu', 'tgħidu', '-');
		forms['pres.p1.mf.pl'] = irreg_vowel_forms('ngħidu', 'ngħidu', '-');

		forms['imp.p2.mf.sg'] = irreg_consonant_forms('għed', 'għed', '-', 'ek');
		forms['imp.p2.mf.sg'] += irreg_consonant_forms('għid', 'għid', '-', 'ek');
		forms['imp.p2.mf.pl'] = irreg_consonant_forms('għedu', 'għedu', '-', 'ek');
		forms['imp.p2.mf.pl'] += irreg_consonant_forms('għidu', 'għidu', '-', 'ek');

		# also mequl/moqul?
		forms['pp.m.sg'] = [('maqul', '-', '-')] ;
		forms['pp.f.sg'] = [('maqula', '-', '-')] ;
		forms['pp.mf.pl'] = [('maqulin', '-', '-')] ;
	#}

	elif stem == 'ġie' : #{
		# with suffixes: hemm ġiethom darba waħda li raw
		# meta tiġik, ħudha - hm - 
		# 'do not miss a good opportunity when it comes your way'
		# also 2 ^ġietni/*ġietni$, 1 ^ġietu/*ġietu$, 1 ^ġiethom/*ġiethom$

		# what about i.o. andd d.o. + i.o.?
		forms['past.p3.m.sg'] = irreg_vowel_forms('ġie', 'ġie', '-');
		forms['past.p3.f.sg'] = irreg_consonant_forms('ġiet', 'ġiet', '-', 'ek');
		forms['past.p2.mf.sg'] = irreg_consonant_forms('ġejt', 'ġejt', '-', 'ek');	
		forms['past.p1.mf.sg'] = irreg_consonant_forms('ġejt', 'ġejt', '-', 'ek');
		forms['past.p3.mf.pl'] = irreg_vowel_forms('ġew', 'ġew', '-');
		forms['past.p2.mf.pl'] = irreg_vowel_forms('ġejtu', 'ġejtu', '-');
		forms['past.p1.mf.pl'] = irreg_vowel_forms('ġejna', 'ġejnie', '-');

		forms['pres.p3.m.sg'] = irreg_vowel_forms('jiġi', 'jiġi', '-');
		forms['pres.p3.f.sg'] = irreg_vowel_forms('tiġi', 'tiġi', '-');
		forms['pres.p2.mf.sg'] = irreg_vowel_forms('tiġi', 'tiġi', '-');
		forms['pres.p1.mf.sg'] = irreg_vowel_forms('niġi', 'niġi', '-');
		forms['pres.p3.mf.pl'] = irreg_vowel_forms('jiġu', 'jiġu', '-');
		forms['pres.p2.mf.pl'] = irreg_vowel_forms('tiġu', 'tiġu', '-');
		forms['pres.p1.mf.pl'] = irreg_vowel_forms('niġu', 'niġu', '-');

		forms['imp.p2.mf.sg'] = irreg_vowel_forms('ejja', 'ejja', '-');
		forms['imp.p2.mf.pl'] = irreg_vowel_forms('ejjew', 'ejjew', '-');

		forms['pprs.m.sg'] = [('ġej', '-', '-')] ;
		forms['pprs.f.sg'] = [('ġejja', '-', '-')] ;
		forms['pprs.mf.pl'] = [('ġejjin', '-', '-')] ;
	#}

	elif stem == 'mar' : #{
		# it's possible to use mar + d.o.
		# but no sign of mar + d.o. suffix

		forms['past.p3.m.sg'] = irreg_consonant_forms('mar', 'mar', '-', 'ek');
		forms['past.p3.f.sg'] = irreg_consonant_forms('marret', 'marrit', '-', 'ek');
		forms['past.p2.mf.sg'] = irreg_consonant_forms('mort', 'mort', '-', 'ok');	
		forms['past.p1.mf.sg'] = irreg_consonant_forms('mort', 'mort', '-', 'ok');
		forms['past.p3.mf.pl'] = irreg_vowel_forms('marru', 'marru', '-');
		forms['past.p2.mf.pl'] = irreg_vowel_forms('mortu', 'mortu', '-');
		forms['past.p1.mf.pl'] = irreg_vowel_forms('morna', 'mornie', '-');

		forms['pres.p3.m.sg'] = irreg_consonant_forms('imur', 'imur', 'LR', 'ek');
		forms['pres.p3.m.sg'] += irreg_consonant_forms('jmur', 'jmur', 'LR', 'ek');
		forms['pres.p3.m.sg'] += irreg_consonant_forms('imur', 'imur', 'RL', 'ek');

		forms['pres.p3.f.sg'] = irreg_consonant_forms('tmur', 'tmur', '-', 'ek');
		forms['pres.p2.mf.sg'] = irreg_consonant_forms('tmur', 'tmur', '-', 'ek');

		# there is also 1 ^inmorru/*inmorru$
		forms['pres.p1.mf.sg'] = irreg_consonant_forms('immur', 'immur', 'LR', 'ek');
		forms['pres.p1.mf.sg'] += irreg_consonant_forms('mmur', 'mmur', 'LR', 'ek');
		forms['pres.p1.mf.sg'] += irreg_consonant_forms('immur', 'immur', 'RL', 'ek');

		forms['pres.p3.mf.pl'] = irreg_vowel_forms('imorru', 'imorru', 'LR');
		forms['pres.p3.mf.pl'] += irreg_vowel_forms('jmorru', 'jmorru', 'LR');
		forms['pres.p3.mf.pl'] += irreg_vowel_forms('imorru', 'imorru', 'RL');

		forms['pres.p2.mf.pl'] = irreg_vowel_forms('tmorru', 'tmorru', '-');

		forms['pres.p1.mf.pl'] = irreg_vowel_forms('immorru', 'immorru', 'LR');
		forms['pres.p1.mf.pl'] += irreg_vowel_forms('mmorru', 'mmorru', 'LR');
		forms['pres.p1.mf.pl'] += irreg_vowel_forms('immorru', 'immorru', 'RL');

		forms['imp.p2.mf.sg'] = irreg_consonant_forms('mur', 'mur', '-', 'ek');
		forms['imp.p2.mf.pl'] = irreg_vowel_forms('morru', 'morru', '-');

	#}

	elif stem == 'kiel' : #{

		# kiel-u, kiel-x? not kilu, kilx? or maybe kielu, but kilha?
		forms['past.p3.m.sg'] = irreg_consonant_forms('kiel', 'kiel', '-', 'ek');
		forms['past.p3.f.sg'] = irreg_consonant_forms('kielet', 'kielit', '-', 'ek');
		forms['past.p2.mf.sg'] = irreg_consonant_forms('kilt', 'kilt', '-', 'ek');	
		forms['past.p1.mf.sg'] = irreg_consonant_forms('kilt', 'kilt', '-', 'ek');
		forms['past.p3.mf.pl'] = irreg_vowel_forms('kielu', 'kielu', '-');
		forms['past.p2.mf.pl'] = irreg_vowel_forms('kiltu', 'kiltu', '-');
		forms['past.p1.mf.pl'] = irreg_vowel_forms('kilna', 'kilnie', '-');

		# 1 ^jiekolhom/*jiekolhom$  1 ^jiekolhielu/*jiekolhielu$
		forms['pres.p3.m.sg'] = irreg_consonant_forms('jiekol', 'jiekol', '-', 'ok');
		forms['pres.p3.f.sg'] = irreg_consonant_forms('tiekol', 'tiekol', '-', 'ok');
		forms['pres.p2.mf.sg'] = irreg_consonant_forms('tiekol', 'tiekol', '-', 'ok');
		forms['pres.p1.mf.sg'] = irreg_consonant_forms('niekol', 'niekol', '-', 'ok');

		forms['pres.p3.mf.pl'] = irreg_vowel_forms('jieklu', 'jieklu', '-');
		forms['pres.p2.mf.pl'] = irreg_vowel_forms('tieklu', 'tieklu', '-');
		forms['pres.p1.mf.pl'] = irreg_vowel_forms('nieklu', 'nieklu', '-');

		forms['imp.p2.mf.sg'] = irreg_consonant_forms('kul', 'kul', '-', 'ek');
		forms['imp.p2.mf.pl'] = irreg_vowel_forms('kulu', 'kulu', '-');

		forms['pp.m.sg'] = [('mikul', '-', '-')] ;
		forms['pp.f.sg'] = [('mikula', '-', '-')] ;
		forms['pp.mf.pl'] = [('mikulin', '-', '-')] ;

	#}


	elif stem == 'wera' : #{
		forms['past.p3.m.sg'] = irreg_vowel_forms('wera', 'wera', '-');
		forms['past.p3.f.sg'] = irreg_consonant_forms('uriet', 'uriet', '-', 'ek');
		forms['past.p2.mf.sg'] = irreg_consonant_forms('urejt', 'urejt', '-', 'ek');	
		forms['past.p1.mf.sg'] = irreg_consonant_forms('urejt', 'urejt', '-', 'ek');
		forms['past.p3.mf.pl'] = irreg_vowel_forms('urew', 'urew', '-');
		forms['past.p2.mf.pl'] = irreg_vowel_forms('urejtu', 'urejtu', '-');
		forms['past.p1.mf.pl'] = irreg_vowel_forms('urejna', 'urejnie', '-');

		forms['pres.p3.m.sg'] = irreg_vowel_forms('juri', 'juri', '-');
		forms['pres.p3.f.sg'] = irreg_vowel_forms('turi', 'turi', '-');
		forms['pres.p2.mf.sg'] = irreg_vowel_forms('turi', 'turi', '-');
		forms['pres.p1.mf.sg'] = irreg_vowel_forms('nuri', 'nuri', '-');
		forms['pres.p3.mf.pl'] = irreg_vowel_forms('juru', 'juru', '-');
		forms['pres.p2.mf.pl'] = irreg_vowel_forms('turu', 'turu', '-');
		forms['pres.p1.mf.pl'] = irreg_vowel_forms('nuru', 'nuru', '-');

		forms['imp.p2.mf.sg'] = irreg_vowel_forms('uri', 'uri', '-');
		forms['imp.p2.mf.pl'] = irreg_vowel_forms('uru', 'uru', '-');

		forms['pp.m.sg'] = [('muri', '-', '-')] ;
		forms['pp.f.sg'] = [('murija', '-', '-')] ;
		forms['pp.mf.pl'] = [('murijin', '-', '-')] ;

	#}

	# needs checking
	elif stem == 'ntwera' : #{
		# shouldn't it be ('ntwera', 'ntwerie', ...)
		# in other words: ma ntwerax? or rather ma ntweriex?
		forms['past.p3.m.sg'] = irreg_iv_vowel_forms('ntwera', 'ntwera', '-');
		forms['past.p3.m.sg'] += irreg_iv_vowel_forms('intwera', 'intwera', '-');
		forms['past.p3.f.sg'] = irreg_iv_consonant_forms('ntweriet', 'ntweriet', '-', 'ek');
		forms['past.p3.f.sg'] += irreg_iv_consonant_forms('intweriet', 'intweriet', '-', 'ek');
		forms['past.p2.mf.sg'] = irreg_iv_consonant_forms('ntwerejt', 'ntwerejt', '-', 'ek');	
		forms['past.p2.mf.sg'] += irreg_iv_consonant_forms('intwerejt', 'intwerejt', '-', 'ek');	
		forms['past.p1.mf.sg'] = irreg_iv_consonant_forms('ntwerejt', 'ntwerejt', '-', 'ek');
		forms['past.p1.mf.sg'] += irreg_iv_consonant_forms('intwerejt', 'intwerejt', '-', 'ek');
		forms['past.p3.mf.pl'] = irreg_iv_vowel_forms('ntwerew', 'ntwerew', '-');
		forms['past.p3.mf.pl'] += irreg_iv_vowel_forms('intwerew', 'intwerew', '-');
		forms['past.p2.mf.pl'] = irreg_iv_vowel_forms('ntwerejtu', 'ntwerejtu', '-');
		forms['past.p2.mf.pl'] += irreg_iv_vowel_forms('intwerejtu', 'intwerejtu', '-');
		forms['past.p1.mf.pl'] = irreg_iv_vowel_forms('ntwerejna', 'ntwerejnie', '-');
		forms['past.p1.mf.pl'] += irreg_iv_vowel_forms('intwerejna', 'intwerejnie', '-');

		forms['pres.p3.m.sg'] = irreg_iv_vowel_forms('jintwera', 'jintwera', '-');
		forms['pres.p3.f.sg'] = irreg_iv_vowel_forms('tintwera', 'tintwera', '-');
		forms['pres.p2.mf.sg'] = irreg_iv_vowel_forms('tintwera', 'tintwera', '-');
		forms['pres.p1.mf.sg'] = irreg_iv_vowel_forms('nintwera', 'nintwera', '-');
		forms['pres.p3.mf.pl'] = irreg_iv_vowel_forms('jintwerew', 'jintwerew', '-');
		forms['pres.p2.mf.pl'] = irreg_iv_vowel_forms('tintwerew', 'tintwerew', '-');
		forms['pres.p1.mf.pl'] = irreg_iv_vowel_forms('nintwerew', 'nintwerew', '-');

		forms['imp.p2.mf.sg'] = irreg_vowel_forms('intwera', 'intwera', '-');
		forms['imp.p2.mf.pl'] = irreg_vowel_forms('intweru', 'intweru', '-');

	#}

	elif stem == 'jaf' : #{


# past forms are not right, once generated should be corrected and not generated any more
# (problems: kien jaf, kien jaf+ha, ma kien+x jaf, ma kien+x jaf+ha)

 		forms['past.p3.m.sg'] = irreg_consonant_forms('kien<b/>jaf', 'kien<b/>jaf', '-', 'ek');
		forms['past.p3.f.sg'] = irreg_consonant_forms('kienet<b/>taf', 'kienet<b/>taf', '-', 'ek');
		forms['past.p2.mf.sg'] = irreg_consonant_forms('kont<b/>taf', 'kont<b/>taf', '-', 'ek');	
		forms['past.p1.mf.sg'] = irreg_consonant_forms('kont<b/>naf', 'kont<b/>naf', '-', 'ek');
		forms['past.p3.mf.pl'] = irreg_vowel_forms('kienu<b/>jafu', 'kienu<b/>jafu', '-');
		forms['past.p2.mf.pl'] = irreg_vowel_forms('kontu<b/>tafu', 'kontu<b/>tafu', '-');
		forms['past.p1.mf.pl'] = irreg_vowel_forms('konna<b/>nafu', 'konna<b/>nafu', '-');

		# 1 ^jiekolhom/*jiekolhom$  1 ^jiekolhielu/*jiekolhielu$
		forms['pres.p3.m.sg'] = irreg_consonant_forms('jaf', 'jaf', '-', 'ek');
		forms['pres.p3.f.sg'] = irreg_consonant_forms('taf', 'taf', '-', 'ek');
		forms['pres.p2.mf.sg'] = irreg_consonant_forms('taf', 'taf', '-', 'ek');
		forms['pres.p1.mf.sg'] = irreg_consonant_forms('naf', 'naf', '-', 'ek');

		forms['pres.p3.mf.pl'] = irreg_vowel_forms('jafu', 'jafu', '-');
		forms['pres.p2.mf.pl'] = irreg_vowel_forms('tafu', 'tafu', '-');
		forms['pres.p1.mf.pl'] = irreg_vowel_forms('nafu', 'nafu', '-');

		forms['imp.p2.mf.sg'] = irreg_consonant_forms('af', 'af', '-', 'ek');
		forms['imp.p2.mf.pl'] = irreg_vowel_forms('afu', 'afu', '-');

	#}


	elif stem == 'emmen' : #{
		forms['past.p3.m.sg'] = [('emmen', '-', '-'),
					 ('emmen', 'S__qtalt/x', '-')];
		forms['past.p3.f.sg'] = [('emmnet', '-', '-'),
					 ('emmnit', 'S__qtalt/x', '-')];
		forms['past.p2.mf.sg'] =  [('emmint', '-', '-'),
					('emmint', 'S__qtalt/x', '-')];	
		forms['past.p1.mf.sg'] =  [('emmint', '-', '-'),
					('emmint', 'S__qtalt/x', '-')];
		forms['past.p3.mf.pl'] =  [('emmnu', '-', '-'),
					('emmnu', 'S__qtalt/x', '-')];
		forms['past.p2.mf.pl'] =  [('emmintu', '-', '-'),
					('emminntu', 'S__qtalt/x', '-')];
		forms['past.p1.mf.pl'] =  [('emminna', '-', '-'),
					('emminnie', 'S__qtalt/x', '-')];

		forms['pres.p3.m.sg'] = [('jemmen', '-', '-'),
					 ('jemmen', 'S__qtalt/x', '-')];
		forms['pres.p3.f.sg'] = [('temmen', '-', '-'),
					 ('temmen', 'S__qtalt/x', '-')];
		forms['pres.p2.mf.sg'] =  [('temmen', '-', '-'),
					('temmen', 'S__qtalt/x', '-')];
		forms['pres.p1.mf.sg'] =  [('nemmen', '-', '-'),
					('nemmen', 'S__qtalt/x', '-')];
		forms['pres.p3.mf.pl'] =  [('jemmnu', '-', '-'),
					('jemmnu', 'S__qtalt/x', '-')];
		forms['pres.p2.mf.pl'] =  [('temmnu', '-', '-'),
					('temmnu', 'S__qtalt/x', '-')];
		forms['pres.p1.mf.pl'] =  [('nemmnu', '-', '-'),
					('nemmnu', 'S__qtalt/x', '-')];

		forms['imp.p2.mf.sg'] = [('emmen', '-', '-'),
				      ('emmen', 'S__qtalt/x', '-')];
		forms['imp.p2.mf.pl'] = [('emmnu', '-', '-'),
				      ('emmnu', 'S__qtalt/x', '-')];

		forms['pp.m.sg'] = [('mwemmen', '-', '-')] ;
		forms['pp.f.sg'] = [('mwemmna', '-', '-')] ;
		forms['pp.mf.pl'] = [('mwemmnin', '-', '-')] ;

	#}


	elif stem == 'kien' : #{
		forms['past.p3.m.sg'] = [('kien', '-', '-'),
					 ('kien', 'S__qtalt/x', '-')];
		forms['past.p3.f.sg'] = [('kienet', '-', '-'),
					 ('kinit', 'S__qtalt/x', '-'),
					 ('kienit', 'S__qtalt/x', 'LR')];
		forms['past.p2.mf.sg'] =  [('kont', '-', '-'),
					('kont', 'S__qtalt/x', '-')];	
		forms['past.p1.mf.sg'] =  [('kont', '-', '-'),
					('kont', 'S__qtalt/x', '-')];
		forms['past.p3.mf.pl'] =  [('kienu', '-', '-'),
					('kienu', 'S__qtalt/x', '-')];
		forms['past.p2.mf.pl'] =  [('kontu', '-', '-'),
					('kontu', 'S__qtalt/x', '-')];
		forms['past.p1.mf.pl'] =  [('konna', '-', '-'),
					('konnie', 'S__qtalt/x', '-')];

		forms['pres.p3.m.sg'] = [('ikun', '-', 'LR'),
					 ('jkun', '-', 'LR'),
					 ('ikun', '-', 'RL'),
					 ('ikun', 'S__qtalt/x', 'LR'), 
					 ('jkun', 'S__qtalt/x', 'LR'),
					 ('ikun', 'S__qtalt/x', 'RL')];
		forms['pres.p3.f.sg'] = [('tkun', '-', '-'),
					 ('tkun', 'S__qtalt/x', '-')];
		forms['pres.p2.mf.sg'] =  [('tkun', '-', '-'),
					('tkun', 'S__qtalt/x', '-')];
		forms['pres.p1.mf.sg'] =  [('inkun', '-', 'LR'),
					('nkun', '-', 'LR'),
					('inkun', '-', 'RL'),
					('inkun', 'S__qtalt/x', 'LR'), 
					('nkun', 'S__qtalt/x', 'LR'),
					('inkun', 'S__qtalt/x', 'RL')];
		forms['pres.p3.mf.pl'] =  [('ikunu', '-', 'LR'),
					('jkunu', '-', 'LR'),
					('ikunu', '-', 'RL'),
					('ikunu', 'S__qtalt/x', 'LR'), 
					('jkunu', 'S__qtalt/x', 'LR'),
					('ikunu', 'S__qtalt/x', 'RL')];
		forms['pres.p2.mf.pl'] =  [('tkunu', '-', '-'),
					('tkunu', 'S__qtalt/x', '-')];
		forms['pres.p1.mf.pl'] =  [('inkunu', '-', 'LR'),
					('nkunu', '-', 'LR'),
					('inkunu', '-', 'RL'),
					('inkunu', 'S__qtalt/x', 'LR'), 
					('nkunu', 'S__qtalt/x', 'LR'),
					('inkunu', 'S__qtalt/x', 'RL')];

		# I'm guessing here
		forms['imp.p2.mf.sg'] = [('kun', '-', '-'),
				      ('kun', 'S__qtalt/x', '-')];
		forms['imp.p2.mf.pl'] = [('kunu', '-', '-'),
				      ('kunu', 'S__qtalt/x', '-')];

	#}

	elif stem == 'kellu' : #{
		forms['past.p3.m.sg'] = [('kellu', '-', '-'),
					 ('kellu', 'S__qtalt/x', '-')];
		forms['past.p3.f.sg'] = [('kellha', '-', '-'),
					 ('kellhie', 'S__qtalt/x', '-')];
		forms['past.p2.mf.sg'] =  [('kellek', '-', '-'),
					('kellek', 'S__qtalt/x', '-')];	
		forms['past.p1.mf.sg'] =  [('kelli', '-', '-'),
					('kelli', 'S__qtalt/x', '-')];
		forms['past.p3.mf.pl'] =  [('kellhom', '-', '-'),
					('kellhom', 'S__qtalt/x', '-')];
		forms['past.p2.mf.pl'] =  [('kellkom', '-', '-'),
					('kellkom', 'S__qtalt/x', '-')];
		forms['past.p1.mf.pl'] =  [('kellna', '-', '-'),
					('kellnie', 'S__qtalt/x', '-')];

		forms['pres.p3.m.sg'] = [('ikollu', '-', 'LR'),
					 ('jkollu', '-', 'LR'),
					 ('ikollu', '-', 'RL'),
					 ('ikollu', 'S__qtalt/x', 'LR'), 
					 ('jkollu', 'S__qtalt/x', 'LR'),
					 ('ikollu', 'S__qtalt/x', 'RL')];
		forms['pres.p3.f.sg'] = [('ikollha', '-', 'LR'),
					 ('jkollha', '-', 'LR'),
					 ('ikollha', '-', 'RL'),
					 ('ikollhie', 'S__qtalt/x', 'LR'), 
					 ('jkollhie', 'S__qtalt/x', 'LR'),
					 ('ikollhie', 'S__qtalt/x', 'RL')];
		forms['pres.p2.mf.sg'] =  [('ikollok', '-', 'LR'),
					('jkollok', '-', 'LR'),
					('ikollok', '-', 'RL'),
					('ikollok', 'S__qtalt/x', 'LR'), 
					('jkollok', 'S__qtalt/x', 'LR'),
					('ikollok', 'S__qtalt/x', 'RL')];
		forms['pres.p1.mf.sg'] =  [('ikolli', '-', 'LR'),
					('jkolli', '-', 'LR'),
					('ikolli', '-', 'RL'),
					('ikolli', 'S__qtalt/x', 'LR'), 
					('jkolli', 'S__qtalt/x', 'LR'),
					('ikolli', 'S__qtalt/x', 'RL')];
		forms['pres.p3.mf.pl'] =  [('ikollhom', '-', 'LR'),
					('jkollhom', '-', 'LR'),
					('ikollhom', '-', 'RL'),
					('ikollhom', 'S__qtalt/x', 'LR'), 
					('jkollhom', 'S__qtalt/x', 'LR'),
					('ikollhom', 'S__qtalt/x', 'RL')];
		forms['pres.p2.mf.pl'] =  [('ikollkom', '-', 'LR'),
					('jkollkom', '-', 'LR'),
					('ikollkom', '-', 'RL'),
					('ikollkom', 'S__qtalt/x', 'LR'), 
					('jkollkom', 'S__qtalt/x', 'LR'),
					('ikollkom', 'S__qtalt/x', 'RL')];
		forms['pres.p1.mf.pl'] =  [('ikollna', '-', 'LR'),
					('jkollna', '-', 'LR'),
					('ikollna', '-', 'RL'),
					('ikollnie', 'S__qtalt/x', 'LR'), 
					('jkollnie', 'S__qtalt/x', 'LR'),
					('ikollnie', 'S__qtalt/x', 'RL')];

	#}

	return forms;

#}



def main(stem): #{

	return irregular_forms(stem['stem']);

#}


