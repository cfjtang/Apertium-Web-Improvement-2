#!/usr/bin/env python3
import os
import sys
import pprint
import argparse

#!/usr/bin/env python

#langs = ["xal", "chv", "tat", "kaz", "kaz2", "alt", "bua", "kir", "tgk", "tyv", "kaa", "gag", "kum", "aze", "kjh"]   #POSSIBLE languages, kaz2 is a second kaz translation of the Bible
def todict(langs):
    langData = {}   #this is a dictionary
    for lang in langs:

       langData[lang] = {}
       with open("%s.dat" % lang) as databaseFile:
           for line in databaseFile :
               if line.strip(): 
                   (english, target) = line.split(',')
                   langData[lang][english] = target.strip()
    
    return langData

def main():
    if __name__ == '__main__':
        parser = argparse.ArgumentParser(description='This script generates a dictionary from a .dat file in trunk/apertium-tools')
        parser.add_argument('datfile', metavar='i', help='Languages (3 letter iso code) separated by a comma, make sure the corresponding .dat files exist')
        args = vars(parser.parse_args())
        if "," in args['datfile']:
            langs=args['datfile'].split(",")
        else:
            langs=[args['datfile']]       
        langDict=todict(langs)
        pprint.pprint(langDict)

        
main()
