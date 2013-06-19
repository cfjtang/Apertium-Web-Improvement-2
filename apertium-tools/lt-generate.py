#!/usr/bin/python3

# Script to read in an ambiguous lexical unit and replace each of the analyses with the corresponding surface form.
#
# Input:
#
#    ^sabiedrisko/sabiedrisks<adj><sint><m><sg><acc><def>/sabiedrisks<adj><sint><m><sg><voc><def>/sabiedrisks<adj><sint><m><pl><gen><def>/sabiedrisks<adj><sint><f><sg><acc><def>/sabiedrisks<adj><sint><f><sg><voc><def>/sabiedrisks<adj><sint><f><pl><gen><def>$
#
# Output:
#
#    ^sabiedrisko/sabiedrisko/sabiedriskā/sabiedriskais$
#
# Usage: 
# 
#       generate.py <bin file>
# 

import subprocess, sys;

def generate(line):
	#print('generate:', line);
	proc.stdin.write(bytes(line, "utf-8"));
	proc.stdin.write(bytes('\0', "utf-8"));
	proc.stdin.write(bytes('\0', "utf-8"));
	proc.stdin.flush();
	d = proc.stdout.read(1);
	d = proc.stdout.read(1);
	buf = b'';
	while d != '\0': #{
		buf = buf + d;
		if d == b'\0': #{
			break;
		#}
		d = proc.stdout.read(1);
	#}
	#print(str(buf, "utf-8"));
	return str(buf, "utf-8");
#}


escaped = False;
inside = False;
first = True;
unitat = '';
buf = '';
lexical_forms = set();
generated_forms = set();
surface_form = '';

cmd = "lt-proc -z -g %s" % sys.argv[1];

proc = subprocess.Popen(cmd.split(),stdout=subprocess.PIPE,stderr=subprocess.PIPE,stdin=subprocess.PIPE)
proc.stdin.write(bytes('\0', "utf-8"));

c = sys.stdin.read(1);

while c != '': #{
	if c == '\\' and not escaped and inside: #{
		unitat = unitat + c;	
		c = sys.stdin.read(1);	
		unitat = unitat + c;	
		c = sys.stdin.read(1);	
		continue;
	#}

	if c == '\\' and not escaped and not inside: #{

		sys.stdout.write(c);
		c = sys.stdin.read(1);	
		sys.stdout.write(c);
		c = sys.stdin.read(1);	
		continue;
	#}

	if c == '^' and not escaped: #{
		inside = True;
		first = True;
		c = sys.stdin.read(1);	
		continue;
	#}

	if c == '/' and not escaped: #{
		if first == True: #{
			print('1 buf:',buf, file=sys.stderr);
			first = False;
			surface_form = buf;
		else: #{
			newsf = generate('^' + buf + '$').strip(' \u0000');
			print('. buf:',buf, file=sys.stderr);
			print('. newsf:',newsf, file=sys.stderr);
			lexical_forms.add(buf);
			generated_forms.add(newsf);
		#}
		buf = '';
		c = sys.stdin.read(1);	
		continue;

	#}

	if c == '$' and not escaped: #{
		if buf != '':
			newsf = generate('^' + buf + '$').strip(' \u0000');
			print('. buf:',buf, file=sys.stderr);
			print('. newsf:',newsf, file=sys.stderr);
			lexical_forms.add(buf);
			generated_forms.add(newsf);
			buf = '';

		inside = False;

		print(generated_forms, lexical_forms, file=sys.stderr);

		generated_forms = list(generated_forms);
		lexical_forms = list(lexical_forms);

		if len(lexical_forms) == 0: #{
			sys.stdout.write('^');
			sys.stdout.write(surface_form);
			sys.stdout.write('/');
			sys.stdout.write(surface_form);
			sys.stdout.write('$');
		else: #{
			sys.stdout.write('^');
			sys.stdout.write(surface_form);
			sys.stdout.write('/');
			for form in generated_forms: #{
				sys.stdout.write(form);
				if form != generated_forms[-1]: #{
					sys.stdout.write('/');
				#}	
			#}
			sys.stdout.write('$');
		#}		

		unitat = '';
		buf = '';
		lexical_forms = set();
		generated_forms = set();
		surface_form = '';

		c = sys.stdin.read(1);	
		continue;
	#}

	if inside: #{
		unitat = unitat + c;
		buf = buf + c;
	else: 
		sys.stdout.write(c);
	#}



	c = sys.stdin.read(1);
#}
