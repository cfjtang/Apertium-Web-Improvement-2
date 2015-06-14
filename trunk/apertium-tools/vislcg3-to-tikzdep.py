#!/usr/bin/python3

import sys, re;

header = """
\documentclass[landscape,a5paper,12pt]{minimal}
\\usepackage{tikz-dependency} 

\\usepackage{fontspec}
\\usepackage{xunicode}
\\usepackage{xltxtra}
\\usepackage[margin=1.5in,landscape]{geometry}

\setromanfont{Times New Roman}

\\newfontfamily\qgmk[Scale=MatchLowercase,Letters=SmallCaps]{Linux Libertine O}

\\begin{document}

\\resizebox{\\textwidth}{!}{%
\centering
\\begin{dependency}[edge horizontal padding=5pt]
   \\begin{deptext}[column sep=0.3cm]


""";

footer = """
\end{dependency}
}

\end{document}
""";

###############################################################################

rword = re.compile('"<(.+)>"');
rbase = re.compile('""(.+)"');
rcateg = re.compile(' \w+ ');
rnode = re.compile('#([0-9]+)->');
rparent = re.compile('->([0-9]+)');
rfunc = re.compile('@(.+) #');

# words[0] = ('Город', 'город', 'n', (1,2,'subj'))
words = {};

buffer = '';
state = 0;
pos = 0;
for line in sys.stdin.readlines(): #{
	if line[0] == '"' and buffer != '': #{
		print(buffer,file=sys.stderr);
		word = rword.findall(buffer)[0];
		lema = rbase.findall(buffer)[0];
		categ = rcateg.findall(buffer)[0];
		dep = (rnode.findall(buffer)[0], rparent.findall(buffer)[0], rfunc.findall(buffer)[0].strip('><→←'));
		#print(dep, file=sys.stderr);
		words[pos] = (word,lema,categ,dep);
		#print(words[pos],file=sys.stderr);
		buffer = '';	
		pos = pos + 1;
		state = 0; 
	#}		
	
	buffer = buffer + line.strip();

	state = state + 1;
#}
word = rword.findall(buffer)[0];
lema = rbase.findall(buffer)[0];
categ = rcateg.findall(buffer)[0];
dep = (rnode.findall(buffer)[0],rparent.findall(buffer)[0], rfunc.findall(buffer)[0].strip('><→←'));
words[pos] = (word,lema,categ,dep);

print(buffer,file=sys.stderr);

print(header);

## Print the sentence 

for j in range(0,3): #{
	last = False;
	for word in words: #{
	
		if word == len(words)-1: #{
			last = True;
		#}
		if j == 2: #{
			sys.stdout.write("\\texttt{"+str(words[word][j].strip())+"}");
		else: #{
			sys.stdout.write(str(words[word][j]).replace('%', '\%'));
		#}
		if not last: #{
			sys.stdout.write(' \& ');
		else: #{
			print(" \\\\");
		#}
	#}
#}

print("   \end{deptext}");

## Print the graph

seenRoot = False;
rootNode = '';
for word in words: #{
	#print(word, words[word], file=sys.stderr);
	if words[word][3][1] == "0" and not seenRoot: #{
		print("\deproot{%s}{\qgmk{root}}" % words[word][3][0]);
		rootNode = words[word][3][0];
		seenRoot = True;
		continue;
	if words[word][3][1] == "0" and seenRoot: #{
		print("\depedge[edge unit distance=1.5ex,edge below]{%s}{%s}{\qgmk{%s}}" % (words[word][3][0], rootNode, "x"));
	else: #{
		print("\depedge{%s}{%s}{\qgmk{%s}}" % (words[word][3][0], words[word][3][1], words[word][3][2].lower()));
	#}

#}

print(footer);
