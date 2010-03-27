#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import xmlrpclib, sys;

proxy = xmlrpclib.ServerProxy("http://localhost:6173/RPC2");

sys.stdout.write('* Enabled pairs: ');
count = 0;
for pair in proxy.languagePairs():
	sys.stdout.write(pair["srcLang"] + "-" + pair["destLang"] + " ");
	count = count + 1;

	if count == 10:
		count = 0;
		print '';
		sys.stdout.write('    ');

print "";

res = proxy.translate({'text':'the cat is on the table', 'srcLang':'nn', 'destLang':'nb', 'markUnknownWords':False});

print res['translation'];
