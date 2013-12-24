#!/usr/bin/env python3
'''Usage: python3 <name>.py dict_url
   Output: prints number of stems in that dict.

   Issues: If dict encoding is not convertable to utf-8, returns -1
'''
import sys, urllib.request
import xml.etree.ElementTree as xml

def print_info(uri):
    dictX = ""
    if "http" in uri:
        try:
            dictX = str((urllib.request.urlopen(uri)).read(), 'utf-8')
        except:
            return print(-1)

    else:
        dictX = (open(uri, 'r')).read()
    try:
        tree = xml.fromstring(dictX)
    except:
        return print(-1)
    
    bi = len(tree.findall("pardefs")) == 0 #bilingual dicts don't have pardefs section -- not necessarily true? check /trunk/apertium-en-es/apertium-en-es.en-es.dix
            
    if(bi):
        print('Stems: %s ' % len(tree.findall("*[@id='main']/e//l")))
    else:
        print('Stems: %s' % len(tree.findall("*[@id='main']/*[@lm]")))
        if tree.find('pardefs'):
            print('Paradigms: %s' % len(tree.find('pardefs').findall("pardef")))

if __name__ == "__main__":
    uri = sys.argv[1]
    print_info(uri)
