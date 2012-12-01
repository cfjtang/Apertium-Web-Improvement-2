#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import xml.etree.ElementTree as ET

sys.setrecursionlimit(20000)

dictionary = sys.argv[1]

if len(sys.argv) > 2:
        freqList = sys.argv[2]
else:
        freqList = False

if freqList:
        try:
                fList = open(freqList, 'r')
        except:
                print(freqList + " not found.", file=sys.stderr)
                sys.exit(-1)
        wordFreqs = {}

        for wordPair in fList:
                #print(wordPair, file=sys.stderr)
                wordPairAsList = wordPair.split()
                freq = wordPairAsList[0]
                w = " ".join(wordPairAsList[1:])
                wordFreqs[w] = int(freq)

        fList.close()

doc = ET.parse(dictionary)
root = doc.getroot()

paradigms = {}

def return_symlist(symlist): 
        if len(symlist) < 1: 
                return ''
        
        if symlist[0] == '.': 
                symlist = symlist[1:]
        
        symlist = symlist.replace(':', '.')
        output = ''

        for symbol in symlist.split('.'): 
                output = output + '<s n="' + symbol + '"/>'
        
        return output

def roll_right(name): 
        bar_idx = name.find('/') + 1
        udr_idx = name.find('_')

        rstring = name[bar_idx:udr_idx]

        if name.find('/') == -1: 
                rstring = ''
        
        return rstring; 

def compare_paradigms(paradigm1, paradigm2): 
        # paradigm,stem,symbols !! paradigm,stem,symbols        
        common = 0

        if len(paradigm1) != len(paradigm2): 
                return False

        for pair1 in paradigm1: 
                for pair2 in paradigm2: 
                        if pair1 == pair2: 
                                common = common + 1

        if common == len(paradigm1): 
                return True
                
        return False

count = 0

for pardefs in root.findall('pardefs'):
        for node in pardefs.findall('pardef'):
                pardef = node.get('n')

                if pardef not in paradigms: 
                        paradigms[pardef] = []

                count += 1
                if count % 1000 == 0: 
                        print(count , pardef, file=sys.stderr)
                
                for child in node.findall('e'):
                        direc = child.get('r')
                        for pair in child.findall('p'): 
                                suffix = ''
                                left = pair.find('l')

                                if left is not None: 
                                        suffix = left.text
                                else: 
                                        suffix = ''
                                symbols = ''
                                right =  pair.find('r')
                                for sym in right.findall('s'): 
                                        symbol = ''
                                        if sym is not None: 
                                                symbol = sym.get('n')
                                        
                                        symbols += '.' + symbol
                                
                                p = (suffix, symbols, direc)

                                paradigms[pardef].append(p)
#Sort paradigms by usage according to the frequency list file, if it exists.
#If the word is not in the frequency list, default to zero.
#If there's no list, use the length of the word instead.

def parSort(p):
        if freqList:
                uLoc = p.find("__")
                p2 = p[:uLoc]
                if p2 in wordFreqs:
                        return wordFreqs[p2]
                else:
                        return 0 #default to 0 if word isn't in the list
        else:
                return -len(p)

                
sorted_paradigms = sorted(paradigms, key=parSort, reverse=True)

duplicates = {}

def strip_duplicates(paradigms, duplicates, current): 
#       print 'paradigms: ' , len(paradigms)
        
        for paradigm1 in paradigms.copy(): 
                if paradigm1 in paradigms and current in paradigms and current != paradigm1: 
                        if current not in duplicates: 
                                duplicates[current] = []
                        
                        if compare_paradigms(paradigms[paradigm1],
        paradigms[current]) and\
        roll_right(paradigm1) == roll_right(current): 
#                               print paradigms[paradigm1] , '; ' , paradigms[current]
                                duplicates[current].append(paradigm1)
                                del paradigms[paradigm1]
                                strip_duplicates(paradigms, duplicates, current)

#z = copy.deepcopy(paradigms)
for k in sorted_paradigms: 
        print(k, file=sys.stderr)
        strip_duplicates(paradigms, duplicates, k)


print('Paradigms: ' , len(paradigms), file=sys.stderr)

print('---', file=sys.stderr)

print('<dictionary>')
print('  <pardefs>')
for paradigm in paradigms: 
        bar_idx = paradigm.find('/') + 1
        udr_idx = paradigm.find('_')

        print('    <pardef n="' + paradigm + '">')

        #pair is a bit of a misnomer-it's actually a triple now
        for pair in paradigms[paradigm]: 
            out = ''
            if pair[2] is None:
                    out += '      <e><p>'
            else:
                    out += '      <e r="%s"><p>' % pair[2]
            if pair[0] is None or pair[0] == 'None': 
                    out = out + '<l/>'
            else: 
                    out = out + '          <l>%s</l>' % (pair[0])
            
            rpost = paradigm[bar_idx:udr_idx]
            if '/' not in paradigm: 
                    rpost = ''
            
            out += '<r>' + rpost + return_symlist(pair[1]) + '</r></p></e>'
            print (out)                
        
        print('    </pardef>')

print('  </pardefs>')
print('  <section id="main" type="standard">')

d = open(dictionary, 'r')

output = ''
for line in d: 
        if '<e lm="' in line: 
                output += line

total = 0

for paradigm in duplicates:
        for p in duplicates[paradigm]:
                print('+ ' , p , ' → ' , paradigm, file=sys.stderr)
                output = output.replace('n="' + p + '"', 'n="' + paradigm + '"')
        
        total += len(duplicates[paradigm]) + 1

print(output)

print('  </section>')
print('</dictionary>')


print('---', file=sys.stderr)
print('duplicates: ' , len(duplicates) , '; total: ' , total, file=sys.stderr)
print('paradigms: ' , len(paradigms), file=sys.stderr)
#print >> sys.stderr, 'z: ' , len(z)
