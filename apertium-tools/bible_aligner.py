#!/usr/bin/env python3
import os, sys, re, argparse
import getBookNames

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
	#Get all the data between that books beginning and the next line break
	file1_data = []
	file2_data = []
	flag = False

	#for file1
	for line in input_file_1:
		if line == "\n":
			flag = False

		if flag:
			if re.findall('^\d', line) != []: #regex to check if first char is digit. messy....
				file1_data.append(line)
		if book in line:
			flag = True

	#RESET FLAG!
	flag = False
	#for file2
	for line in input_file_2:
		if line == "\n":
			flag = False

		if flag:
			if re.findall('^\d', line) != []: #regex to check if first char is digit. messy....
				file2_data.append(line)
		if book in line:
			flag = True

	#Finally, output a new entry
	for i in range(0, len(file1_data)):
		print(entry % (lang1, file1_data[i], lang2, file2_data[i]))
#footer
print(footer)
