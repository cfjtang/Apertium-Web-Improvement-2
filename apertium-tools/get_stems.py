#!/usr/bin/env python3
'''Usage: python3 <name>.py bidict_url
   Output: prints number of stems in that bidict.

   Issues: If page encoding is not convertable to utf-8, returns -1
'''
import sys, urllib.request, re, itertools

def get_stems(uri):
	bidict = ""
	if "http" in uri:
		try:
			bidict = str((urllib.request.urlopen(uri)).read(), 'utf-8')
		except:
			return -1

	else:
		bidict = (open(uri, 'r')).read()

	words_list = []
	temp = re.findall('<l>(.*?)<s', bidict, re.DOTALL|re.MULTILINE)
	for i in temp:
		if i not in words_list:
			words_list.append(i)

	return(len(words_list))


if __name__ == "__main__":
	uri = sys.argv[1]
	print(get_stems(uri))
