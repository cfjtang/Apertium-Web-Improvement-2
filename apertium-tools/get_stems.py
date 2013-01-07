#!/usr/bin/env python3
'''Usage: python3 <name>.py bidict_url
   Output: prints number of stems in that bidict.
'''
import sys, urllib.request, re, itertools

def get_stems(uri):
	bidict = ""
	if "http" in uri:
		bidict = ((urllib.request.urlopen(uri)).read()).decode('utf-8')
	else:
		bidict = (open(uri, 'r')).read()

	bidict_list = [i.strip() for i in bidict.split("\n")]
	bidict_list = [i for i in bidict_list if ("<e>" in i and "<l>" in i and "<s" in i)]
	words_list  = [re.findall('<l>(.*?)<s', i)[0] for i in bidict_list]

	#Get rid of duplicates:
	words_list.sort()
	unique_words_list = list(i for i,_ in itertools.groupby(words_list))
	return(len(unique_words_list))


if __name__ == "__main__":
	uri = sys.argv[1]
	print(get_stems(uri))
