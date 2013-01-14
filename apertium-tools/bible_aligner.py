#!/usr/bin/env python3
import os, sys, re, argparse
import getBookNames

#Gets common verses between both bibles
def get_common_verses(book, bible1, bible2): #return 2d list, list[x][0] = lang1 verse, list[x][1] = lang2 verse
	#First read off all the book:
	book1 = []
	flag = False

	for verse in bible1:
		if verse == "\n":
			flag = False
		if flag:
			book1.append(verse)
		if book in verse:
			flag = True

	book2 = []
	flag = False
	for verse in bible2:
		if verse == "\n":
			flag = False
		if flag:
			book2.append(verse)
		if book in verse:
			flag = True

	#Now just get the numbered ones
	numbered_verses1 = []
	for i in book1:
		if re.findall('^\d', i) != []:
			numbered_verses1.append(i)
	numbered_verses2 = []
	for i in book2:
		if re.findall('^\d', i) != []:
			numbered_verses2.append(i)

	#Finally, get all with the _same_ numbers
	return_me = []
	for line1 in numbered_verses1:
		line1s_number = re.findall('^(\d+)', line1)[0]
		for line2 in numbered_verses2:
			line2s_number = re.findall('^(\d+)', line2)[0]
			if line1s_number == line2s_number:
				return_me.append([line1, line2])
				break
	return(return_me)



#XML header
header = '''<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE tmx SYSTEM "tmx14.dtd">
<tmx version="1.4">
	<header segtype="sentence" o-tmf="UTF-8" adminlang="en" srclang="en" datatype="PlainText"/>
	<body>
'''

#format: print(entry % (lang1, entry in lang1, lang2, entry in lang2))
entry = '''
		<tu>
			<tuv xml:lang="%s">
				<seg>%s</seg>
			</tuv>
			<tuv xml:lang="%s">
				<seg>%s</seg>
			</tuv>
		</tu>
'''

#XML footer
footer = '''
	</body>
</tmx>
'''

######## END STRING FORMATS #########


parser = argparse.ArgumentParser(description='Name of two text files.')
parser.add_argument("file_1", help="Path to the first file")
parser.add_argument("l1",help="Language of first file")
parser.add_argument("file_2", help="Path to the second file")
parser.add_argument("l2",help="Lagnuage of the second file")
parser.add_argument("--verbose", help="Shows output",action="store_true")
parser.add_argument("--outputFile",help="Name of the output tmx file, specify as fileName.tmx")
args = parser.parse_args()

########### END ARGPARSE ############

#Get inputs:
input_file_1 = (open(args.file_1, 'r').read()).split("\n")
lang1 = args.l1

input_file_2 = (open(args.file_2, 'r').read()).split("\n")
lang2 = args.l2


#get book names and translate them to english:
book_names = getBookNames.todict([lang1, lang2])

#lang1:
for lang1_book_names in book_names[lang1].items(): #[0] == eng, [1] == lang1
	for i in range(0, len(input_file_1)):
		input_file_1[i] = input_file_1[i].replace(lang1_book_names[1], lang1_book_names[0])
#lang2:
for lang2_book_names in book_names[lang2].items(): #[0] == eng, [1] == lang2
	for i in range(0, len(input_file_2)):
		input_file_2[i] = input_file_2[i].replace(lang2_book_names[1], lang2_book_names[0])


#Get which books are common to both langs:
common_books = []
for book in book_names[lang1].keys():
	if book in "".join(input_file_1) and book in "".join(input_file_2):
		common_books.append(book)


#Now take these common books and output them:
#header
print(header)

#main entries
for book in common_books:
	#Get common verses in a list
	common_verses = get_common_verses(book, input_file_1, input_file_2)

	#Output a new entry
	for i in range(0, len(common_verses)):
		print(entry % (lang1, common_verses[i][0], lang2, common_verses[i][1]))
#footer
print(footer)
