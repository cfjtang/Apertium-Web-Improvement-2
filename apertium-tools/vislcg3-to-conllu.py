import sys, re;

rword = re.compile('"<(.+)>"');
rbase = re.compile('"(.+)"');
rcateg = re.compile(' ([_A-Za-z0-9]+)');
rnode = re.compile('#([0-9]+)->');
rparent = re.compile('->([0-9]+)');
rfunc = re.compile('@(.+) #');

# Input:
#	"<Қала>"
#		"қала" n nom @nmod #1->2
#	"<халқы>"
#		"халық" n px3sp nom @subj #2->3
#	"<58,3%>"
#		"58,3" num percent nom @main #3->0
#			"е" cop aor p3 sg @cop #4->3
#	"<.>"
#		"." sent @punct #5->3
#	

# Output:
#	1	Қала	кала	_	n	nom	2	nmod
#	2	халқы	халық	_	n	px3sp|nom	3	nsubj
#	3-4	58,3%	_	_	_	_	_	_
#	3	58,3	58,3	_	num	percent|nom	0	root
#	4	_	е	_	cop	aor|p3|sg	3	cop
#	5	.	.	_	sent	_	3	punct

def trykk(buffer, tokcount): #{
	llong = buffer.count('\n') - 2;
	tokcount = tokcount + 1; 
	print('!!!',tokcount,'!!!', buffer,file=sys.stderr);
	index = '';
	if llong > 0: #{
		index = str(tokcount) + '-' + str(tokcount+llong);
	else: #{
		index = str(tokcount);
	#}
	buffer = buffer.split('\n');
	ord = rword.findall(buffer[0])[0];
	if llong == 0: #{
		lem = rbase.findall(buffer[1])[0];
		categs = rcateg.findall(buffer[1]);
		mor = rparent.findall(buffer[1])[0];
		etiqueta = rfunc.findall(buffer[1])[0];
		pos = '_'
		if len(categs) > 0: #{
			pos = categs[0];
		#}
		msd = '_';
		if len(categs) > 1: #{
			msd = '';
			for i in categs[1:]: #{
				msd = msd + i.strip() + '|';
			#}
			msd = msd.strip('|');
		#}
		print('%s\t%s\t%s\t_\t%s\t%s\t%s\t%s' % (index,ord,lem, pos, msd, mor, etiqueta));	
	else: #{
		print('%s\t%s\t_\t_\t_\t_' % (index,ord));	
		nindex = tokcount;
		for llinia in buffer[1:]: #{
			if llinia == '': continue;
			lem = rbase.findall(llinia)[0];	
			categs = rcateg.findall(llinia);	
			mor = rparent.findall(llinia)[0];
			etiqueta = rfunc.findall(llinia)[0];
			pos = '_'
			if len(categs) > 0: #{
				pos = categs[0];
			#}
			msd = '_';
			if len(categs) > 1: #{
				msd = '';
				for i in categs[1:]: #{
					msd = msd + i.strip() + '|';
				#}
				msd = msd.strip('|');
			#}
			
			print('%d\t_\t%s\t_\t%s\t%s\t%s\t%s' % (nindex,lem, pos, msd, mor, etiqueta));	
			nindex = nindex + 1;
		#}
	#}
	return tokcount;
#}

incohort = False;
buffer = '';
sentcount = 0;
tokcount = 0;
for line in sys.stdin.readlines(): #{
	#print('X', line)

	if line[0] == ';': #{
		continue;
	#}

	if line.strip() == '': #{
		tokcount = trykk(buffer, tokcount);
		buffer = '';
		incohort = False;	
		tokcount = 0;
		sentcount = sentcount + 1;
		print('');
		continue;
	#}	


	if line[0] == '"' and line[1] == '<' and buffer != '': #{
		tokcount = trykk(buffer, tokcount);
		buffer = '';
		incohort = True;
		buffer = buffer + line;
		continue;
	#}

	if line.strip()[0] == '"': #{
		buffer = buffer + line;	
	#}

#}

print(sentcount);
