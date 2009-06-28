#!/usr/bin/python2.5
# coding=utf-8
# -*- encoding: utf-8 -*-

''' This script writes pronoun speling format to stdout '''

import sys, string, codecs, MySQLdb;

#sys.stdin = codecs.getreader('utf-8')(sys.stdin);
#sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
#sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

try:
	#conn = MySQLdb.connect (host = "localhost", user = "root", passwd = "root", db = "bengali_conjugator", use_unicode = True)
	conn = MySQLdb.connect (host = "localhost", user = "root", passwd = "root", db = "bengali_conjugator")

	cursor = conn.cursor ()
	cursor.execute ('SET CHARACTER SET utf8')
	
	#cursor.execute ('select lemma, surface, person, number, gcase, pos, tag, gender from pronoun_source')
	cursor.execute ('select lemma, surface, person, number, gcase, pos, tag, gender, animacy, tag2 from pronoun_source')
	
	rows = cursor.fetchall()
	
	for row in rows:
		lemma, surface, person, number, gcase, pos, tag, gender, animacy, tag2 = row

		#print lemma, surface, person, number, gcase, pos, tag, gender, animacy, tag2
		#print surface + "; " + lemma + ";",
		sys.stdout.write(surface + "; " + lemma + "; ")
		if person != 'all':
			sys.stdout.write(person + ".")
		if tag != '':
			sys.stdout.write(tag + ".")
		if tag2 != '':
			sys.stdout.write(tag2 + ".")
		if animacy != '':
			sys.stdout.write(animacy + ".")
		sys.stdout.write(gender  + "." + number + "." + gcase + "; " + pos)
		
		sys.stdout.write("\n")
	
	cursor.close ()
	conn.close ()

except MySQLdb.Error, e:
	print "Error %d: %s" % (e.args[0], e.args[1])
	sys.exit (1)

