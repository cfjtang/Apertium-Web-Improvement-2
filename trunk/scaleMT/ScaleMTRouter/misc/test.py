#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import xmlrpclib, sys;

#proxy = xmlrpclib.ServerProxy("http://localhost:6173/RPC2");
proxy = xmlrpclib.ServerProxy("http://localhost:8080/ScaleMTRouter/xmlrpc");

#sys.stdout.write('* Enabled pairs: ');
#count = 0;
#for pair in proxy.languagePairs():
#	sys.stdout.write(pair["srcLang"] + "-" + pair["destLang"] + " ");
#	count = count + 1;
#
#	if count == 10:
#		count = 0;
#		print '';
#		sys.stdout.write('    ');

print "";

res = proxy.translate.translate('Una prueba', 'es', 'ca','ashbfasli');

print '* Una prueba → ' + res;

f=open('input.rtf', 'rb');
fileContent=f.read();
f.close();

fileResult=proxy.translate.translateDocument(xmlrpclib.Binary(fileContent),'es','ca','rtf','ashbfasli');
f2 = open('/tmp/output.rtf','w');
f2.write(fileResult.data);
f2.close();

