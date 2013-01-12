#!/usr/bin/env python3
import os
import sys
#import pprint


langs = ["xal", "chv", "tat", "kaz", "kaz2", "alt", "bua", "kir", "tgk", "tyv", "kaa"]   #languages, kaz2 is a second kaz translation of the Bible

langData = {}   #this is a dictionary

def main():

    for lang in langs:

       langData[lang] = {}
       with open("%s.dat" % lang) as databaseFile:
           for line in databaseFile :
               if line.strip(): 
                   (english, target) = line.split(',')
                   langData[lang][english] = target.strip()
    #pprint.pprint(langData)
main()
