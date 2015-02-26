#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-


## ----------------------------------------------------------------------------##
## irregular verbs
## ----------------------------------------------------------------------------##


def irregular_forms(stem): #{

	forms = {};
	paradigm = 'S__فتح/ه';

	if stem == 'رأى' : #{
		forms['actv.past.p3.m.sg'] = [('رأى', '-', '-'), ('رآ', paradigm, '-')];
		forms['actv.past.p3.f.sg'] = [('رأت', '-', '-'), ('رأت', paradigm, '-')];
		forms['actv.past.p2.m.sg'] = [('رأيت', '-', '-'), ('رأيت', paradigm, '-')];
		forms['actv.past.p2.f.sg'] = [('رأيت', '-', '-'), ('رأيت', paradigm, '-')];
		forms['actv.past.p1.mf.sg'] = [('رأيت', '-', '-'), ('رأيت', paradigm, '-')];

		forms['actv.past.p3.m.du'] = [('رأيا', '-', '-'), ('رأيا', paradigm, '-')];
		forms['actv.past.p3.f.du'] = [('رأتا', '-', '-'), ('رأتا', paradigm, '-')];
		forms['actv.past.p2.mf.du'] = [('رأيتما', '-', '-'), ('رأيتما', paradigm, '-')];

		forms['actv.past.p3.m.pl'] = [('رأوا', '-', '-'), ('رأو', paradigm, '-')];
		forms['actv.past.p3.f.pl'] = [('رأين', '-', '-'), ('رأين', paradigm, '-')];
		forms['actv.past.p2.m.pl'] = [('رأيتم', '-', '-'), ('رأيتمو', paradigm, '-')];
		forms['actv.past.p2.f.pl'] = [('رأيتن', '-', '-'), ('رأيتن', paradigm, '-')];
		forms['actv.past.p1.mf.pl'] = [('رأينا', '-', '-'), ('رأينا', paradigm, '-')];


		forms['pasv.past.p3.m.sg'] = [('رئي', '-', 'LR'), ('رُئي', '-', 'RL')];
		forms['pasv.past.p3.f.sg'] = [('رئيت', '-', '-'), ('رُئيت', '-', 'RL')];
		forms['pasv.past.p2.m.sg'] = [('رئيت', '-', 'LR'), ('رُئيت', '-', 'RL')];
		forms['pasv.past.p2.f.sg'] = [('رئيت', '-', 'LR'), ('رُئيت', '-', 'RL')];
		forms['pasv.past.p1.mf.sg'] = [('رئيت', '-', 'LR'), ('رُئيت', '-', 'RL')];

		forms['pasv.past.p3.m.du'] = [('رئيا', '-', 'LR'), ('رُئيا', '-', 'RL')];
		forms['pasv.past.p3.f.du'] = [('رئيتا', '-', 'LR'), ('رُئيتا', '-', 'RL')];
		forms['pasv.past.p2.mf.du'] = [('رئيتما', '-', 'LR'), ('رُئيتما', '-', 'RL')];

		forms['pasv.past.p3.m.pl'] = [('رؤوا', '-', 'LR'), ('رُؤوا', '-', 'RL')];
		forms['pasv.past.p3.f.pl'] = [('رئين', '-', 'LR'), ('رُئين', '-', 'RL')];
		forms['pasv.past.p2.m.pl'] = [('رئيتم', '-', 'LR'), ('رُئيتم', '-', 'RL')];
		forms['pasv.past.p2.f.pl'] = [('رئيتن', '-', 'LR'), ('رُئيتن', '-', 'RL')];
		forms['pasv.past.p1.mf.pl'] = [('رئينا', '-', 'LR'), ('رُئينا', '-', 'RL')];


		forms['actv.pres.p3.m.sg'] = [('يرى', '-', '-'), ('يرا', paradigm, '-')];
		forms['actv.pres.p3.f.sg'] = [('ترى', '-', '-'), ('ترا', paradigm, '-')];
		forms['actv.pres.p2.m.sg'] = [('ترى', '-', '-'), ('ترا', paradigm, '-')];
		forms['actv.pres.p2.f.sg'] = [('ترين', '-', '-'), ('تري', paradigm, '-')];
		forms['actv.pres.p1.mf.sg'] = [('أرى', '-', '-'), ('أرا', paradigm, '-')];
	
		forms['actv.pres.p3.m.du'] = [('يريان', '-', '-'), ('يريا', paradigm, '-')];
		forms['actv.pres.p3.f.du'] = [('تريان', '-', '-'), ('تريا', paradigm, '-')];
		forms['actv.pres.p2.mf.du'] = [('تريان', '-', '-'), ('تريا', paradigm, '-')];

		forms['actv.pres.p3.m.pl'] = [('يرون', '-', '-'), ('يرو', paradigm, '-')];
		forms['actv.pres.p3.f.pl'] = [('يرين', '-', '-'), ('يرين', paradigm, '-')];
		forms['actv.pres.p2.m.pl'] = [('ترون', '-', '-'), ('ترو', paradigm, '-')];
		forms['actv.pres.p2.f.pl'] = [('ترين', '-', '-'), ('ترين', paradigm, '-')];
		forms['actv.pres.p1.mf.pl'] = [('نرى', '-', '-'), ('نرا', paradigm, '-')];


		forms['pasv.pres.p3.m.sg'] = [('يرى', '-', 'LR'), ('يُرى', '-', 'RL')];
		forms['pasv.pres.p3.f.sg'] = [('ترى', '-', 'LR'), ('تُرى', '-', 'RL')];
		forms['pasv.pres.p2.m.sg'] = [('ترى', '-', 'LR'), ('تُرى', '-', 'RL')];
		forms['pasv.pres.p2.f.sg'] = [('ترين', '-', 'LR'), ('تُرين', '-', 'RL')];
		forms['pasv.pres.p1.mf.sg'] = [('أرى', '-', 'LR'), ('اُرى', '-', 'RL')];

		forms['pasv.pres.p3.m.du'] = [('يريان', '-', 'LR'), ('يُريان', '-', 'RL')];
		forms['pasv.pres.p3.f.du'] = [('تريان', '-', 'LR'), ('تُريان', '-', 'RL')];
		forms['pasv.pres.p2.mf.du'] = [('تريان', '-', 'LR'), ('تُريان', '-', 'RL')];

		forms['pasv.pres.p3.m.pl'] = [('يرون', '-', 'LR'), ('يُرون', '-', 'RL')];
		forms['pasv.pres.p3.f.pl'] = [('يرين', '-', 'LR'), ('يُرين', '-', 'RL')];
		forms['pasv.pres.p2.m.pl'] = [('ترون', '-', 'LR'), ('تُرون', '-', 'RL')];
		forms['pasv.pres.p2.f.pl'] = [('ترين', '-', 'LR'), ('تُرين', '-', 'RL')];
		forms['pasv.pres.p1.mf.pl'] = [('نرى', '-', 'LR'), ('نُرى', '-', 'RL')];


		forms['actv.subjun.p3.m.sg'] = [('يرى', '-', '-'), ('يرا', paradigm, '-')];
		forms['actv.subjun.p3.f.sg'] = [('ترى', '-', '-'), ('ترا', paradigm, '-')];
		forms['actv.subjun.p2.m.sg'] = [('ترى', '-', '-'), ('ترا', paradigm, '-')];
		forms['actv.subjun.p2.f.sg'] = [('تري', '-', '-'), ('تري', paradigm, '-')];
		forms['actv.subjun.p1.mf.sg'] = [('أرى', '-', '-'), ('أرا', paradigm, '-')];
	
		forms['actv.subjun.p3.m.du'] = [('يريا', '-', '-'), ('يريا', paradigm, '-')];
		forms['actv.subjun.p3.f.du'] = [('تريا', '-', '-'), ('تريا', paradigm, '-')];
		forms['actv.subjun.p2.mf.du'] = [('تريا', '-', '-'), ('تريا', paradigm, '-')];

		forms['actv.subjun.p3.m.pl'] = [('يروا', '-', '-'), ('يرو', paradigm, '-')];
		forms['actv.subjun.p3.f.pl'] = [('يرين', '-', '-'), ('يرين', paradigm, '-')];
		forms['actv.subjun.p2.m.pl'] = [('تروا', '-', '-'), ('ترو', paradigm, '-')];
		forms['actv.subjun.p2.f.pl'] = [('ترين', '-', '-'), ('ترين', paradigm, '-')];
		forms['actv.subjun.p1.mf.pl'] = [('نرى', '-', '-'), ('نرا', paradigm, '-')];


		forms['pasv.subjun.p3.m.sg'] = [('يرى', '-', 'LR'), ('يُرى', '-', 'RL')];
		forms['pasv.subjun.p3.f.sg'] = [('ترى', '-', 'LR'), ('تُرى', '-', 'RL')];
		forms['pasv.subjun.p2.m.sg'] = [('ترى', '-', 'LR'), ('تُرى', '-', 'RL')];
		forms['pasv.subjun.p2.f.sg'] = [('تري', '-', 'LR'), ('تُري', '-', 'RL')];
		forms['pasv.subjun.p1.mf.sg'] = [('أرى', '-', 'LR'), ('اُرى', '-', 'RL')];

		forms['pasv.subjun.p3.m.du'] = [('يريا', '-', 'LR'), ('يُريا', '-', 'RL')];
		forms['pasv.subjun.p3.f.du'] = [('تريا', '-', 'LR'), ('تُريا', '-', 'RL')];
		forms['pasv.subjun.p2.mf.du'] = [('تريا', '-', 'LR'), ('تُريا', '-', 'RL')];

		forms['pasv.subjun.p3.m.pl'] = [('يروا', '-', 'LR'), ('يُروا', '-', 'RL')];
		forms['pasv.subjun.p3.f.pl'] = [('يرين', '-', 'LR'), ('يُرين', '-', 'RL')];
		forms['pasv.subjun.p2.m.pl'] = [('تروا', '-', 'LR'), ('تُروا', '-', 'RL')];
		forms['pasv.subjun.p2.f.pl'] = [('ترين', '-', 'LR'), ('تُرين', '-', 'RL')];
		forms['pasv.subjun.p1.mf.pl'] = [('نرى', '-', 'LR'), ('نُرى', '-', 'RL')];


		forms['actv.apocop.p3.m.sg'] = [('ير', '-', '-'), ('ير', paradigm, '-')];
		forms['actv.apocop.p3.f.sg'] = [('تر', '-', '-'), ('تر', paradigm, '-')];
		forms['actv.apocop.p2.m.sg'] = [('تر', '-', '-'), ('تر', paradigm, '-')];
		forms['actv.apocop.p2.f.sg'] = [('تري', '-', '-'), ('تري', paradigm, '-')];
		forms['actv.apocop.p1.mf.sg'] = [('أر', '-', '-'), ('أر', paradigm, '-')];
	
		forms['actv.apocop.p3.m.du'] = [('يريا', '-', '-'), ('يريا', paradigm, '-')];
		forms['actv.apocop.p3.f.du'] = [('تريا', '-', '-'), ('تريا', paradigm, '-')];
		forms['actv.apocop.p2.mf.du'] = [('تريا', '-', '-'), ('تريا', paradigm, '-')];

		forms['actv.apocop.p3.m.pl'] = [('يروا', '-', '-'), ('يرو', paradigm, '-')];
		forms['actv.apocop.p3.f.pl'] = [('يرين', '-', '-'), ('يرين', paradigm, '-')];
		forms['actv.apocop.p2.m.pl'] = [('تروا', '-', '-'), ('ترو', paradigm, '-')];
		forms['actv.apocop.p2.f.pl'] = [('ترين', '-', '-'), ('ترين', paradigm, '-')];
		forms['actv.apocop.p1.mf.pl'] = [('نر', '-', '-'), ('نر', paradigm, '-')];


		forms['pasv.apocop.p3.m.sg'] = [('ير', '-', 'LR'), ('يُر', '-', 'RL')];
		forms['pasv.apocop.p3.f.sg'] = [('تر', '-', 'LR'), ('تُر', '-', 'RL')];
		forms['pasv.apocop.p2.m.sg'] = [('تر', '-', 'LR'), ('تُر', '-', 'RL')];
		forms['pasv.apocop.p2.f.sg'] = [('تري', '-', 'LR'), ('تُري', '-', 'RL')];
		forms['pasv.apocop.p1.mf.sg'] = [('أر', '-', 'LR'), ('اُر', '-', 'RL')];

		forms['pasv.apocop.p3.m.du'] = [('يريا', '-', 'LR'), ('يُريا', '-', 'RL')];
		forms['pasv.apocop.p3.f.du'] = [('تريا', '-', 'LR'), ('تُريا', '-', 'RL')];
		forms['pasv.apocop.p2.mf.du'] = [('تريا', '-', 'LR'), ('تُريا', '-', 'RL')];

		forms['pasv.apocop.p3.m.pl'] = [('يروا', '-', 'LR'), ('يُروا', '-', 'RL')];
		forms['pasv.apocop.p3.f.pl'] = [('يرين', '-', 'LR'), ('يُرين', '-', 'RL')];
		forms['pasv.apocop.p2.m.pl'] = [('تروا', '-', 'LR'), ('تُروا', '-', 'RL')];
		forms['pasv.apocop.p2.f.pl'] = [('ترين', '-', 'LR'), ('تُرين', '-', 'RL')];
		forms['pasv.apocop.p1.mf.pl'] = [('نر', '-', 'LR'), ('نُر', '-', 'RL')];


		forms['actv.imp.p2.m.sg'] = [('ر', '-', '-'), ('ره', '-', 'LR'), ('ر', paradigm, '-')];
		forms['actv.imp.p2.f.sg'] = [('ري', '-', '-'), ('ري', paradigm, '-')];
		forms['actv.imp.p2.mf.du'] = [('ريا', '-', '-'), ('ريا', paradigm, '-')];
		forms['actv.imp.p2.m.pl'] = [('روا', '-', '-'), ('رو', paradigm, '-')];
		forms['actv.imp.p2.f.pl'] = [('رين', '-', '-'), ('رين', paradigm, '-')];


		forms['pprs.m.sg'] = [('را', 'S__جز/ء', '-')] ;
		forms['pprs.f.sg'] = [('رائي', 'S__كلم/ة', '-')] ;
		forms['pprs.m.du'] = [('رائي', 'S__كتاب/ان', '-')] ;
		forms['pprs.f.du'] = [('رائيت', 'S__كتاب/ان', '-')] ;
		forms['pprs.m.pl'] = [('راؤ', 'S__مهندس/ون', '-')] ;
		forms['pprs.f.pl'] = [('رائيات', 'S__كلمات/', '-')] ;


		forms['pp.m.sg'] = [('مرئي', 'S__بيت/', '-')] ;
		forms['pp.f.sg'] = [('مرئي', 'S__كلم/ة', '-')] ;
		forms['pp.m.du'] = [('مرئي', 'S__كتاب/ان', '-')] ;
		forms['pp.f.du'] = [('مرئيت', 'S__كتاب/ان', '-')] ;
		forms['pp.m.pl'] = [('مرئي', 'S__مهندس/ون', '-')] ;
		forms['pp.f.pl'] = [('مرئيات', 'S__كلمات/', '-')] ;

	#}

	elif stem == 'جاء' : #{
		# TODO: hamza orthography when suffixes added!
		forms['actv.past.p3.m.sg'] = [('جاء', '-', '-'), ('جاء', paradigm, '-')];
		forms['actv.past.p3.f.sg'] = [('جاءت', '-', '-'), ('جاءت', paradigm, '-')];
		forms['actv.past.p2.m.sg'] = [('جئت', '-', '-'), ('جئت', paradigm, '-')];
		forms['actv.past.p2.f.sg'] = [('جئت', '-', '-'), ('جئت', paradigm, '-')];
		forms['actv.past.p1.mf.sg'] = [('جئت', '-', '-'), ('جئت', paradigm, '-')];

		forms['actv.past.p3.m.du'] = [('جاءا', '-', '-'), ('جاءا', paradigm, '-')];
		forms['actv.past.p3.f.du'] = [('جاءتا', '-', '-'), ('جاءتا', paradigm, '-')];
		forms['actv.past.p2.mf.du'] = [('جئتما', '-', '-'), ('جئتما', paradigm, '-')];

		forms['actv.past.p3.m.pl'] = [('جاؤوا', '-', '-'), ('جاؤو', paradigm, '-')];
		forms['actv.past.p3.f.pl'] = [('جئن', '-', '-'), ('جئن', paradigm, '-')];
		forms['actv.past.p2.m.pl'] = [('جئتم', '-', '-'), ('جئتمو', paradigm, '-')];
		forms['actv.past.p2.f.pl'] = [('جئتن', '-', '-'), ('جئتن', paradigm, '-')];
		forms['actv.past.p1.mf.pl'] = [('جئنا', '-', '-'), ('جئينا', paradigm, '-')];


#		forms['pasv.past.p3.m.sg'] = [('جيء', '-', '-')];
#		forms['pasv.past.p3.f.sg'] = [('جيئت', '-', '-')];
#		forms['pasv.past.p2.m.sg'] = [('جئت', '-', '-')];
#		forms['pasv.past.p2.f.sg'] = [('جئت', '-', '-')];
#		forms['pasv.past.p1.mf.sg'] = [('جئت', '-', '-')];

#		forms['pasv.past.p3.m.du'] = [('جيءا', '-', '-')];
#		forms['pasv.past.p3.f.du'] = [('جيئتا', '-', '-')];
#		forms['pasv.past.p2.mf.du'] = [('جيئتما', '-', '-')];

#		forms['pasv.past.p3.m.pl'] = [('جيئوا', '-', '-')];
#		forms['pasv.past.p3.f.pl'] = [('جئن', '-', '-')];
#		forms['pasv.past.p2.m.pl'] = [('جئتم', '-', '-')];
#		forms['pasv.past.p2.f.pl'] = [('جئتن', '-', '-')];
		forms['pasv.past.p1.mf.pl'] = [('جئنا', '-', '-')];


		forms['actv.pres.p3.m.sg'] = [('يجيء', '-', '-'), ('يجيء', paradigm, '-')];
		forms['actv.pres.p3.f.sg'] = [('تجيء', '-', '-'), ('تجيء', paradigm, '-')];
		forms['actv.pres.p2.m.sg'] = [('تجيء', '-', '-'), ('تجيء', paradigm, '-')];
		forms['actv.pres.p2.f.sg'] = [('تجيئين', '-', '-'), ('تجيئي', paradigm, '-')];
		forms['actv.pres.p1.mf.sg'] = [('أجيء', '-', '-'), ('أجيء', paradigm, '-')];
	
		forms['actv.pres.p3.m.du'] = [('يجيئان', '-', '-'), ('يجيئا', paradigm, '-')];
		forms['actv.pres.p3.f.du'] = [('تجيئان', '-', '-'), ('تجيئا', paradigm, '-')];
		forms['actv.pres.p2.mf.du'] = [('تجيئان', '-', '-'), ('تجيئا', paradigm, '-')];

		forms['actv.pres.p3.m.pl'] = [('يجيئون', '-', '-'), ('يجيئو', paradigm, '-')];
		forms['actv.pres.p3.f.pl'] = [('يجئن', '-', '-'), ('يجئن', paradigm, '-')];
		forms['actv.pres.p2.m.pl'] = [('تجيئون', '-', '-'), ('تجيئو', paradigm, '-')];
		forms['actv.pres.p2.f.pl'] = [('تجئن', '-', '-'), ('تجئن', paradigm, '-')];
		forms['actv.pres.p1.mf.pl'] = [('نجيء', '-', '-'), ('نجيء', paradigm, '-')];


#		forms['pasv.pres.p3.m.sg'] = [('يجاء', '-', '-')];
#		forms['pasv.pres.p3.f.sg'] = [('تجاء', '-', '-')];
#		forms['pasv.pres.p2.m.sg'] = [('تجاء', '-', '-')];
#		forms['pasv.pres.p2.f.sg'] = [('تجاين', '-', '-')];
#		forms['pasv.pres.p1.mf.sg'] = [('أجاء', '-', '-')];

#		forms['pasv.pres.p3.m.du'] = [('يجاءان', '-', '-')];
#		forms['pasv.pres.p3.f.du'] = [('تجاءان', '-', '-')];
#		forms['pasv.pres.p2.mf.du'] = [('تجاءان', '-', '-')];

#		forms['pasv.pres.p3.m.pl'] = [('يجاؤون', '-', '-')];
#		forms['pasv.pres.p3.f.pl'] = [('يجأن', '-', '-')];
#		forms['pasv.pres.p2.m.pl'] = [('تجاؤون', '-', '-')];
#		forms['pasv.pres.p2.f.pl'] = [('تجان', '-', '-')];
#		forms['pasv.pres.p1.mf.pl'] = [('نجاء', '-', '-')];


		forms['actv.subjun.p3.m.sg'] = [('يجيء', '-', '-'), ('يجيء', paradigm, '-')];
		forms['actv.subjun.p3.f.sg'] = [('تجيء', '-', '-'), ('تجيء', paradigm, '-')];
		forms['actv.subjun.p2.m.sg'] = [('تجيء', '-', '-'), ('تجيء', paradigm, '-')];
		forms['actv.subjun.p2.f.sg'] = [('تجيئي', '-', '-'), ('تجيئي', paradigm, '-')];
		forms['actv.subjun.p1.mf.sg'] = [('أجيء', '-', '-'), ('أجيء', paradigm, '-')];
	
		forms['actv.subjun.p3.m.du'] = [('يجيئا', '-', '-'), ('يجيئا', paradigm, '-')];
		forms['actv.subjun.p3.f.du'] = [('تجيئا', '-', '-'), ('تجيئا', paradigm, '-')];
		forms['actv.subjun.p2.mf.du'] = [('تجيئا', '-', '-'), ('تجيئا', paradigm, '-')];

		forms['actv.subjun.p3.m.pl'] = [('يجيئوا', '-', '-'), ('يجيئو', paradigm, '-')];
		forms['actv.subjun.p3.f.pl'] = [('يجئن', '-', '-'), ('يجئن', paradigm, '-')];
		forms['actv.subjun.p2.m.pl'] = [('تجيئوا', '-', '-'), ('تجيئو', paradigm, '-')];
		forms['actv.subjun.p2.f.pl'] = [('تجئن', '-', '-'), ('تجئن', paradigm, '-')];
		forms['actv.subjun.p1.mf.pl'] = [('نجيء', '-', '-'), ('نجيء', paradigm, '-')];


#		forms['pasv.subjun.p3.m.sg'] = [('يجاء', '-', '-')];
#		forms['pasv.subjun.p3.f.sg'] = [('تجاء', '-', '-')];
#		forms['pasv.subjun.p2.m.sg'] = [('تجاء', '-', '-')];
#		forms['pasv.subjun.p2.f.sg'] = [('تجائي', '-', '-')];
#		forms['pasv.subjun.p1.mf.sg'] = [('أجاء', '-', '-')];

#		forms['pasv.subjun.p3.m.du'] = [('يجاءا', '-', '-')];
#		forms['pasv.subjun.p3.f.du'] = [('تجاءا', '-', '-')];
#		forms['pasv.subjun.p2.mf.du'] = [('تجاءا', '-', '-')];

#		forms['pasv.subjun.p3.m.pl'] = [('يجاؤوا', '-', '-')];
#		forms['pasv.subjun.p3.f.pl'] = [('يجأن', '-', '-')];
#		forms['pasv.subjun.p2.m.pl'] = [('تجاؤوا', '-', '-')];
#		forms['pasv.subjun.p2.f.pl'] = [('تجان', '-', '-')];
#		forms['pasv.subjun.p1.mf.pl'] = [('نجاء', '-', '-')];


		forms['actv.apocop.p3.m.sg'] = [('يجئ', '-', '-'), ('يجئ', paradigm, '-')];
		forms['actv.apocop.p3.f.sg'] = [('تجئ', '-', '-'), ('تجئ', paradigm, '-')];
		forms['actv.apocop.p2.m.sg'] = [('تجئ', '-', '-'), ('تجئ', paradigm, '-')];
		forms['actv.apocop.p2.f.sg'] = [('تجيئي', '-', '-'), ('تجيئي', paradigm, '-')];
		forms['actv.apocop.p1.mf.sg'] = [('أجئ', '-', '-'), ('أجئ', paradigm, '-')];
	
		forms['actv.apocop.p3.m.du'] = [('يجيئا', '-', '-'), ('يجيئا', paradigm, '-')];
		forms['actv.apocop.p3.f.du'] = [('تجيئا', '-', '-'), ('تجيئا', paradigm, '-')];
		forms['actv.apocop.p2.mf.du'] = [('تجيئا', '-', '-'), ('تجيئا', paradigm, '-')];

		forms['actv.apocop.p3.m.pl'] = [('يجيئوا', '-', '-'), ('يجيئو', paradigm, '-')];
		forms['actv.apocop.p3.f.pl'] = [('يجئن', '-', '-'), ('يجئن', paradigm, '-')];
		forms['actv.apocop.p2.m.pl'] = [('تجيئوا', '-', '-'), ('تجيئو', paradigm, '-')];
		forms['actv.apocop.p2.f.pl'] = [('تجئن', '-', '-'), ('تجئن', paradigm, '-')];
		forms['actv.apocop.p1.mf.pl'] = [('نجئ', '-', '-'), ('نجئ', paradigm, '-')];


#		forms['pasv.apocop.p3.m.sg'] = [('يجأ', '-', '-')];
#		forms['pasv.apocop.p3.f.sg'] = [('تجأ', '-', '-')];
#		forms['pasv.apocop.p2.m.sg'] = [('تجأ', '-', '-')];
#		forms['pasv.apocop.p2.f.sg'] = [('تجئي', '-', '-')];
#		forms['pasv.apocop.p1.mf.sg'] = [('أجأ', '-', '-')];

#		forms['pasv.apocop.p3.m.du'] = [('يجاءا', '-', '-')];
#		forms['pasv.apocop.p3.f.du'] = [('تجاءا', '-', '-')];
#		forms['pasv.apocop.p2.mf.du'] = [('تجاءا', '-', '-')];

#		forms['pasv.apocop.p3.m.pl'] = [('يجاؤوا', '-', '-')];
#		forms['pasv.apocop.p3.f.pl'] = [('يجأن', '-', '-')];
#		forms['pasv.apocop.p2.m.pl'] = [('تجاؤوا', '-', '-')];
#		forms['pasv.apocop.p2.f.pl'] = [('تجان', '-', '-')];
#		forms['pasv.apocop.p1.mf.pl'] = [('نجأ', '-', '-')];


		forms['actv.imp.p2.m.sg'] = [('جئ', '-', '-'), ('جئ', paradigm, '-')];
		forms['actv.imp.p2.f.sg'] = [('ريئي', '-', '-'), ('ريئي', paradigm, '-')];
		forms['actv.imp.p2.mf.du'] = [('جيئا', '-', '-'), ('جيئا', paradigm, '-')];
		forms['actv.imp.p2.m.pl'] = [('جيئوا', '-', '-'), ('جيئو', paradigm, '-')];
		forms['actv.imp.p2.f.pl'] = [('جئن', '-', '-'), ('جئن', paradigm, '-')];


		forms['pprs.m.sg'] = [('جا', 'S__جز/ء', '-')] ;
		forms['pprs.f.sg'] = [('جائي', 'S__كلم/ة', '-')] ;
		forms['pprs.m.du'] = [('جائي', 'S__كتاب/ان', '-')] ;
		forms['pprs.f.du'] = [('جائيت', 'S__كتاب/ان', '-')] ;
		forms['pprs.m.pl'] = [('جاؤ', 'S__مهندس/ون', '-')] ;
		forms['pprs.f.pl'] = [('جائيات', 'S__كلمات/', '-')] ;


#		forms['pp.m.sg'] = [('مجي', 'S__جز/ء', '-')] ;
#		forms['pp.f.sg'] = [('مجيئ', 'S__كلم/ة', '-')] ;
#		forms['pp.m.du'] = [('مجي', 'S__جز/ءان', '-')] ;
#		forms['pp.f.du'] = [('مجيئت', 'S__كلم/ة', '-')] ;
#		forms['pp.m.pl'] = [('مجيئ', 'S__مهندس/ون', '-')] ;
#		forms['pp.f.pl'] = [('مجيئات', 'S__كلمات/', '-')] ;

	#}

	elif stem == 'أن' : #{
		forms['actv.past.p3.m.sg'] = [('أن', '-', '-')];
		forms['actv.past.p3.f.sg'] = [('أنت', '-', '-')];
		forms['actv.past.p2.m.sg'] = [('أننت', '-', '-')];
		forms['actv.past.p2.f.sg'] = [('أننت', '-', '-')];
		forms['actv.past.p1.mf.sg'] = [('أننت', '-', '-')];

		forms['actv.past.p3.m.du'] = [('أنا', '-', '-')];
		forms['actv.past.p3.f.du'] = [('أنتا', '-', '-')];
		forms['actv.past.p2.mf.du'] = [('أننتما', '-', '-')];

		forms['actv.past.p3.m.pl'] = [('أنوا', '-', '-')];
		forms['actv.past.p3.f.pl'] = [('أنن', '-', '-')];
		forms['actv.past.p2.m.pl'] = [('أننتم', '-', '-')];
		forms['actv.past.p2.f.pl'] = [('أننتن', '-', '-')];
		forms['actv.past.p1.mf.pl'] = [('أننا', '-', '-')];


		forms['actv.pres.p3.m.sg'] = [('يئن', '-', '-')];
		forms['actv.pres.p3.f.sg'] = [('تئن', '-', '-')];
		forms['actv.pres.p2.m.sg'] = [('تئن', '-', '-')];
		forms['actv.pres.p2.f.sg'] = [('تئنين', '-', '-')];
		forms['actv.pres.p1.mf.sg'] = [('أئن', '-', '-')];
	
		forms['actv.pres.p3.m.du'] = [('يئنان', '-', '-')];
		forms['actv.pres.p3.f.du'] = [('تئنان', '-', '-')];
		forms['actv.pres.p2.mf.du'] = [('تئنان', '-', '-')];

		forms['actv.pres.p3.m.pl'] = [('يئنون', '-', '-')];
		forms['actv.pres.p3.f.pl'] = [('يئنن', '-', '-')];
		forms['actv.pres.p2.m.pl'] = [('تئنون', '-', '-')];
		forms['actv.pres.p2.f.pl'] = [('تئنن', '-', '-')];
		forms['actv.pres.p1.mf.pl'] = [('نئن', '-', '-')];


		forms['actv.subjun.p3.m.sg'] = [('يئن', '-', '-')];
		forms['actv.subjun.p3.f.sg'] = [('تئن', '-', '-')];
		forms['actv.subjun.p2.m.sg'] = [('تئن', '-', '-')];
		forms['actv.subjun.p2.f.sg'] = [('تئني', '-', '-')];
		forms['actv.subjun.p1.mf.sg'] = [('أئن', '-', '-')];
	
		forms['actv.subjun.p3.m.du'] = [('يئنا', '-', '-')];
		forms['actv.subjun.p3.f.du'] = [('تئنا', '-', '-')];
		forms['actv.subjun.p2.mf.du'] = [('تئنا', '-', '-')];

		forms['actv.subjun.p3.m.pl'] = [('يئنوا', '-', '-')];
		forms['actv.subjun.p3.f.pl'] = [('يئنن', '-', '-')];
		forms['actv.subjun.p2.m.pl'] = [('تئنوا', '-', '-')];
		forms['actv.subjun.p2.f.pl'] = [('تئنن', '-', '-')];
		forms['actv.subjun.p1.mf.pl'] = [('نئن', '-', '-')];


		forms['actv.apocop.p3.m.sg'] = [('يئن', '-', '-')];
		forms['actv.apocop.p3.f.sg'] = [('تئن', '-', '-')];
		forms['actv.apocop.p2.m.sg'] = [('تئن', '-', '-')];
		forms['actv.apocop.p2.f.sg'] = [('تئني', '-', '-')];
		forms['actv.apocop.p1.mf.sg'] = [('أئن', '-', '-')];
	
		forms['actv.apocop.p3.m.du'] = [('يئنا', '-', '-')];
		forms['actv.apocop.p3.f.du'] = [('تئنا', '-', '-')];
		forms['actv.apocop.p2.mf.du'] = [('تئنا', '-', '-')];

		forms['actv.apocop.p3.m.pl'] = [('يئنوا', '-', '-')];
		forms['actv.apocop.p3.f.pl'] = [('يئنن', '-', '-')];
		forms['actv.apocop.p2.m.pl'] = [('تئنوا', '-', '-')];
		forms['actv.apocop.p2.f.pl'] = [('تئنن', '-', '-')];
		forms['actv.apocop.p1.mf.pl'] = [('نئن', '-', '-')];



		forms['actv.imp.p2.m.sg'] = [('إينن', '-', '-'), ('أن', '-', 'LR')];
		forms['actv.imp.p2.f.sg'] = [('إيننى', '-', '-'), ('أني', '-', 'LR')];
		forms['actv.imp.p2.mf.du'] = [('إيننا', '-', '-'), ('أنا', '-', 'LR')];
		forms['actv.imp.p2.m.pl'] = [('أيننوا', '-', '-'), ('أنوا', '-', 'LR')];
		forms['actv.imp.p2.f.pl'] = [('إينن', '-', '-')];


		forms['pprs.m.sg'] = [('آن', 'S__بيت/', '-')] ;
		forms['pprs.f.sg'] = [('آن', 'S__كلم/ة', '-')] ;
		forms['pprs.m.du'] = [('آن', 'S__كتاب/ان', '-')] ;
		forms['pprs.f.du'] = [('آنت', 'S__كتاب/ان', '-')] ;
		forms['pprs.m.pl'] = [('آن', 'S__مهندس/ون', '-')] ;
		forms['pprs.f.pl'] = [('آنات', 'S__كلمات/', '-')] ;

	#}



	return forms;
#}



## ----------------------------------------------------------------------------##
## main
## ----------------------------------------------------------------------------##


def main(stem): #{

        return irregular_forms(stem['stem']);

#}

