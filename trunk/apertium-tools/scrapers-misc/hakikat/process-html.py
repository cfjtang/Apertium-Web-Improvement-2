import sys;

def replace_entities(s): #{
	o = s;

	o = o.replace('&gt;', '>');
	o = o.replace('&hellip;', '…');
	o = o.replace('&laquo;', '»');
	o = o.replace('&lt;', '<');
	o = o.replace('&nbsp;', '');
	o = o.replace('&ndash;', '–');
	o = o.replace('&mdash;', '—');
	o = o.replace('&quot;', '"');
	o = o.replace('&raquo;', '«');

	
	return o;
#}

printy = False;
for line in sys.stdin.readlines(): #{

	if line.count('<div class="text">') > 0: #{
		printy = True;
	#}
	if line.count('</div>') > 0: #{
		printy = False;
	#}

	if printy: #{
		print(replace_entities(line.strip()));
	#}
#}
