#!/usr/bin/env python3
'''Usage: python3 <name>.py dict_url
   Output: prints number of stems in that dict.

   Issues: If dict encoding is not convertable to utf-8, returns -1
'''
import sys, urllib.request
import xml.etree.ElementTree as xml

def get_stems(uri):
        dictX = ""
        if "http" in uri:
                try:
                        dictX = str((urllib.request.urlopen(uri)).read(), 'utf-8')
                except:
                        return -1

        else:
                dictX = (open(uri, 'r')).read()

        try:
                tree = xml.fromstring(dictX)
        except:
                return -1
        
        bi = -1
        if(len(tree.findall("pardefs")) > 0):
                bi = False
        else:
                bi = True #bilingual dicts don't have pardefs section
                
        if(bi):
            return(len(tree.findall("*[@id='main']/e//l")))
        else:
            return(len(tree.findall("*[@id='main']/*[@lm]")))


if __name__ == "__main__":
        uri = sys.argv[1]
        print(get_stems(uri))
