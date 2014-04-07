#!/usr/bin/env python3
'''Usage: python3 <name>.py dict_url
   Output: prints number of stems in that dict.

   Issues: If dict encoding is not convertable to utf-8, returns -1
'''
import sys, urllib.request
import xml.etree.ElementTree as xml
import argparse, urllib.request

def print_info(uri, bidix=None):
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

    if bidix is not None:
        bi = bidix
    else:
        bi = len(tree.findall("pardefs")) == 0 #bilingual dicts don't have pardefs section -- not necessarily true? check /trunk/apertium-en-es/apertium-en-es.en-es.dix
            
    if(bi):
        print('Stems: %s ' % len(tree.findall("*[@id='main']/e//l")))
    else:
        print('Stems: %s' % len(tree.findall("section/*[@lm]")))  # there can be sections other than id="main"
        if tree.find('pardefs') is not None:
            print('Paradigms: %s' % len(tree.find('pardefs').findall("pardef")))

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Count unique stems in a monolingual or bilingual dictionary (lttoolbox dix format)")
    parser.add_argument('-b', '--bidix', help="forces counter to assume bidix", action='store_true', default=False)
    parser.add_argument('uri', help="uri to a dix file")
    args = parser.parse_args()

    #uri = sys.argv[1]
    if 'http' in args.uri:
        try:
            if args.bidix:
                print_info(str((urllib.request.urlopen(args.uri)).read(), 'utf-8'), bidix=True)
            else:
                print_info(str((urllib.request.urlopen(args.uri)).read(), 'utf-8'))
        except urllib.error.HTTPError:
            logging.critical('Dictionary %s not found' % args.uri)
            sys.exit(-1)
    else:
        if args.bidix:
            print_info(args.uri, bidix=True)
        print_info(args.uri)
