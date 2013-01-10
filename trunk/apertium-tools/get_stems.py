#!/usr/bin/env python3
'''Usage: python3 <name>.py bidict_url
   Output: prints number of stems in that bidict.

   Issues: If bidict encoding is not convertable to utf-8, returns -1
'''
import sys, urllib.request
import xml.etree.ElementTree as xml

def get_stems(uri):
	bidict = ""
	if "http" in uri:
		try:
			bidict = str((urllib.request.urlopen(uri)).read(), 'utf-8')
		except:
			return -1

	else:
		bidict = (open(uri, 'r')).read()

	tree = xml.fromstring(bidict)
	words_list = []
	for child in tree.findall("*[@id='main']/e//l"):
		current_word = child.text
		if current_word not in words_list:
			words_list.append(current_word)

	return(len(words_list))


if __name__ == "__main__":
	uri = sys.argv[1]
	print(get_stems(uri))
