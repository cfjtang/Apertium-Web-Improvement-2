#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import xmlrpclib, sys;

#proxy = xmlrpclib.ServerProxy("http://localhost:6173/RPC2");
proxy = xmlrpclib.ServerProxy("http://localhost:8080/RPC2");

for pair in proxy.languagePairs(): #{
	sys.stdout.write(pair["srcLang"] + "-" + pair["destLang"] + " ");
#}
print "";
