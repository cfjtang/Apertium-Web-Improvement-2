#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import xmlrpclib, sys;

proxy = xmlrpclib.ServerProxy("http://localhost:6173/RPC2");
#proxy = xmlrpclib.ServerProxy("http://localhost:8080/RPC2");

sys.stdout.write('* Enabled pairs: ');
count = 0;
for pair in proxy.languagePairs(): #{
	sys.stdout.write(pair["srcLang"] + "-" + pair["destLang"] + " ");
	count = count + 1;

	if count == 10: #{
		count = 0;
		print '';
		sys.stdout.write('    ');
	#}
#}
print "";

res = proxy.translate('<i>una prova</i>', 'ca', 'en', 'html');

print '* <i>una prova</i> → ' + res['translation'];
