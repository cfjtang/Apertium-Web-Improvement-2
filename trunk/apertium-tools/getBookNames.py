#!/usr/bin/env python3
import os
import sys
#import pprint


langs = ["xal", "chv"]   #languages

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
