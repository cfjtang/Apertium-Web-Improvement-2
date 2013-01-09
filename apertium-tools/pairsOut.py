#!/usr/bin/env python3

import get_all_lang_pairs

header = 'where	lg1	direction	lg2	stems	created	updated\n'
template = '%s	%s	%s	%s	%s	%s	%s\n'

with open('pairs.tsv', 'w') as outFile:
	outFile.write(header)
	for pair in get_all_lang_pairs.main():
		lgTuple = (pair['repo'], pair['lg1'], pair['direction'], pair['lg2'], pair['stems'], pair['created'], pair['last_updated'])
		outFile.write(template % lgTuple)
