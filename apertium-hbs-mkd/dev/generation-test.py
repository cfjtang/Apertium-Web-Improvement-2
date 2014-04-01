#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, commands, tempfile, os;

sys.stdin  = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

TEMPDIR = '/tmp/';

if len(sys.argv) < 4: #{
	print 'generation-test.py <dir> <mode> <corpus>'; 
	sys.exit(-1);
#}

direc = sys.argv[1];
mode = sys.argv[2];
corp = sys.argv[3];

command = 'cat ' + corp + ' | apertium -d ' + direc + ' ' + mode + '-postchunk > ' + TEMPDIR + mode + '.gentest.raw';
commands.getstatusoutput(command);

raw = codecs.getreader('utf-8')(file(TEMPDIR + mode + '.gentest.raw'));

counts = {};

outf = file(TEMPDIR + mode + '.gentest.transfer', 'w+');
c = raw.read(1);
inWord = False;
buf = '';
while c: #{
	if c == '^': #{
		inWord = True;
	#}
	if c == '$': #{
		outf.write(buf + '$\n');
		if buf in counts: #{
			counts[buf] = counts[buf] + 1;
		else: #{
			counts[buf] = 0;
		#}
		buf = '';
		inWord = False;
	#}
	if inWord == True: #{
		buf = buf + c;
		#sys.stdout.write(c);
	#}
		
	c = raw.read(1);
#}

os.remove(TEMPDIR + mode + '.gentest.raw');

command = 'cat ' + TEMPDIR + mode + '.gentest.transfer | sort -f | uniq -c | sort -gr > ' + TEMPDIR + mode + '.gentest.stripped'; 
commands.getstatusoutput(command);

command = 'cat ' + TEMPDIR + mode + '.gentest.stripped | lt-proc -d ../' + mode + '.autogen.bin > ' + TEMPDIR + mode + '.gentest.surface';
commands.getstatusoutput(command);

command = 'cat ' + TEMPDIR + mode + '.gentest.stripped | sed "s/^ *[0-9]* \^/^/g" > ' + TEMPDIR + mode + '.gentest.nofreq';
commands.getstatusoutput(command);

command = 'paste ' + TEMPDIR + mode + '.gentest.surface ' + TEMPDIR + mode + '.gentest.nofreq > ' + TEMPDIR + mode + '.generation.errors.txt';
commands.getstatusoutput(command);

command = 'cat ' + TEMPDIR + mode + '.generation.errors.txt | grep -v "#" | grep "\/" > ' + TEMPDIR + mode + '.multiform';
commands.getstatusoutput(command);

command = 'cat ' + TEMPDIR + mode + '.generation.errors.txt | grep "[0-9] #.*\/" > ' + TEMPDIR + mode + '.multibidix';
commands.getstatusoutput(command);

command = 'cat ' + TEMPDIR + mode + '.generation.errors.txt | grep "[0-9] #" | grep -v "\/" > ' + TEMPDIR + mode + '.tagmismatch';
commands.getstatusoutput(command);

print "";
print "===============================================================================";
print "Multiple surface forms for a single lexical form";
print "===============================================================================";
for line in file(TEMPDIR + mode + '.multiform').read().split('\n'): #{
	print line;
#}
print "";
print "===============================================================================";
print "Multiple bidix entries for a single source language lexical form";
print "===============================================================================";
for line in file(TEMPDIR + mode + '.multibidix').read().split('\n'): #{
	print line;
#}

print "";
print "===============================================================================";
print "Tag mismatch between transfer and generation";
print "==============================================================================="; 
for line in file(TEMPDIR + mode + '.tagmismatch').read().split('\n'): #{
	print line;
#}

print "";
print "===============================================================================";
print "Summary";
print "===============================================================================";
command = 'wc -l ' + TEMPDIR + mode + '.multiform ' + TEMPDIR + mode + '.multibidix ' + TEMPDIR + mode + '.tagmismatch ';
v = commands.getstatusoutput(command);
print v[1];

