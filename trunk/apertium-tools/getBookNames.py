#!/usr/bin/env python3
import os
import sys
#import pprint

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
