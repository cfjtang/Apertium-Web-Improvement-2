#!/usr/bin/env python3

import json
import get_all_lang_pairs

header = 'where	lg1	direction	lg2	stems	created	updated\n'
template = '%s	%s	%s	%s	%s	%s	%s\n'

def outToTsv():
	with open('pairs.tsv', 'w') as outFile:
		outFile.write(header)
		for pair in get_all_lang_pairs.main():
			lgTuple = (pair['repo'], pair['lg1'], pair['direction'], pair['lg2'], pair['stems'], pair['created'], pair['last_updated'])
			outFile.write(template % lgTuple)

def outToJson():
	allPairs = []
	with open('pairs.json', 'w') as outFile:
		for pair in get_all_lang_pairs.main():
			print(len(allPairs), pair['repo'], pair['lg1'], pair['lg2'])
			allPairs.append(pair)
		outFile.write(json.dumps(allPairs))

if __name__ == '__main__':
	outToJson()
