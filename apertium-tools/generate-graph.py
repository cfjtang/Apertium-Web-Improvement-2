#
# generate-graph.py
# Generates a .dot file of the available language pairs in a given directory
# by finding all the modes in modes.xml files with 'install="yes"'.
#
import sys, codecs, commands;

dirlist = ' '.join(sys.argv[1:]);

cmd = "find " + dirlist + " -name 'modes.xml' | xargs cat | grep 'install=\"yes\"' | grep -v '\-.*\-' | grep -v '_' | sort -u ";

out = commands.getstatusoutput(cmd);

dirs = [];
both = [];
single = [];

for line in out[1].split('\n'): #{
	pair = line.split('"')[1];
	
	dirs.append(pair);

#}

for pair in dirs: #{
	pair_row = pair.split('-');
	lr = pair_row[0] + '-' + pair_row[1];
	rl = pair_row[1] + '-' + pair_row[0];

	if lr in dirs and rl in dirs:
		if lr not in both and rl not in both: #{
			both.append(pair);
		#}
	else: 
		single.append(pair);
	#}
#}


print '''
digraph G {
        graph [ratio=fill];
        graph [center="1"];
        ratio = 0.66;
	/*rankdir = "LR";*/
        edge [arrowsize = 0.5];
	splines = true;
	node [shape=circle];
''';

for pair in both: #{
	print '\t' + pair.replace('-', ' -> ') + ' [dir=both] ;';
#}

for pair in single: #{
	print '\t' + pair.replace('-', ' -> ') + ';';
#}

print '}';
