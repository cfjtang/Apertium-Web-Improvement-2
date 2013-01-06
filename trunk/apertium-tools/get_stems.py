#!/usr/bin/env python3
'''Usage: python3 <name>.py bidict_url
   Output: prints number of stems in that bidict.
'''
import sys, urllib.request, re, itertools

def main():
	url = sys.argv[1]
	bidict = ((urllib.request.urlopen(url)).read()).decode('utf-8')

	bidict_list = [i.strip() for i in bidict.split("\n")]
	bidict_list = [i for i in bidict_list if ("<e>" in i and "<l>" in i and "<s" in i)]
	words_list  = [re.findall('<l>(.*?)<s', i)[0] for i in bidict_list]

	#Get rid of duplicates:
	words_list.sort()
	unique_words_list = list(i for i,_ in itertools.groupby(words_list))
	print(len(unique_words_list))


if __name__ == "__main__":
	main()
