#!/opt/local/bin/python2.5
"""
An implementation of:
Improving Translation Quality by Discarding Most of the Phrasetable (Johnson, Martin, Foster, Kuhn 2007)

DEPRECATED (to be reworked, TODO): use fpruning

Usage: 
    -d debug symbols
    -i input file
    -o ouput file
"""
# Copyright (C) 2009 
# Author: Gabriel Synnaeve
# License: http://www.opensource.org/licenses/PythonSoftFoundation.php

import sys, getopt, math #, guppy
#h = guppy.hpy()

def Usage():
    print "./pruning.py phrase-table [-d][-h][-o outputfile][-c]"
    sys.exit(0)

try:
    opts, args = getopt.getopt(sys.argv[1:],'di:o:hc')
except getopt.GetoptError:
    Usage()
    sys.exit(2)
debug = 0
enrich = 0
for o, a in opts:
    if o == '-h':
        Usage()
    elif o == '-d':
        debug = 1
    elif o == '-i':
        inputname = a
    elif o == '-o':
        outputname = a
    elif o == '-c':
        enrich = 1
    else:
        assert False, "unhandled option"

if enrich:
    import enrichment 
else:
    from fisher import FisherExactTest
    fish = FisherExactTest()

#file = open(sys.argv[1])
file = open(inputname)

count_s = {}
count_t = {}
count_st = {}
lines = {}

def count(line):
    #table = line.replace('#','').replace('[','').replace(']','')\
    #        .replace('?','').replace('!','').replace(',','')\
    #        .replace('--','').replace('  ',' ').strip().split('|||') 
            # seems to be faster than with the RE '#\[\]?!'
    table = line.split('|||') 
    table[0] = ' '+table[0].strip()+' ' # for the word at the extremities
    table[1] = ' '+table[1].strip()+' ' # because ' ' is our world delimiter
    source = table[0]
    target = table[1]
    if len(source) > 5 and len(target) > 5:
        if source in count_s:
            count_s[source] +=  1
        else: 
            count_s[source] =  1
        if target in count_t:
            count_t[target] +=  1
        else:
            count_t[target] =  1
        st = (source, target)
        if st in count_st:
            count_st[st] +=  1
        else:
            count_st[st] =  1
        if st in lines:
            lines[st].append(N)
        else:
            lines[st] = [N]

#map(count, file)
N = 0
for line in file:
    N += 1
    count(line)
file.close()

for k in count_s.iterkeys():
    tmp = 0
    for (s, v) in count_st.iteritems():
        if k in s[0] and k != s[0]:
            tmp += v
    count_s[k] += tmp

for k in count_t.iterkeys():
    tmp = 0
    for (t, v) in count_st.iteritems():
        if k in t[1] and k != t[1]:
            tmp += v
    count_t[k] += tmp

next_count_st = {}
for (ks, kt) in count_st.iterkeys():
    tmp = 0
    for ((s, t), v) in count_st.iteritems():
    #file = open(inputname)
    #for line in file:
        #table = line.split('|||')
        #s = ' '+table[0].strip()+' ' 
        #t = ' '+table[1].strip()+' ' 
        if ks in s and len(ks) < len(s) and kt in t and len(kt) < len(t):
            #count_st[(ks, kt)] += 1
            tmp += v
    #file.close()
    next_count_st[(ks, kt)] = tmp + count_st[(ks, kt)]
count_st = next_count_st

if debug:
    #print h.heap()
    print '===========count_s============='
    for k,v in count_s.iteritems():
        print str(k) + ' --> ' + str(v)
    print '===========count_t============='
    for k,v in count_t.iteritems():
        print str(k) + ' --> ' + str(v)
    print '===========count_st============='
    for k,v in count_st.iteritems():
        print str(k) + ' --> ' + str(v)
    print '============================'

delete = []
threshold = math.log(N) - 0.01
if debug:
    print ">>> Threshold :", 
    print threshold
for k in count_st.iterkeys():
    try:
        #print -math.log( fish.pvalue(count_st[k], count_s[k[0]], \
        #        count_t[k[1]], N) [1] ) 
        #print k
        if count_st[k] > count_s[k[0]]:
            print k
            print count_st[k],
            print count_s[k[0]]
            sys.exit(2)
        if enrich:
            val = enrichment.fisher_exact_test(count_st[k], count_s[k[0]], \
                count_t[k[1]], N) [1]
        else:
            val = fish.pvalue(count_st[k], count_s[k[0]], \
                count_t[k[1]], N) [1]
        if -math.log(val) > threshold:
            for l in lines[k]:
                delete.append(l)
    except OverflowError:
        # The value is so low that even your mo****
        # Discard this entry
        for l in lines[k]:
            delete.append(l)

delete.sort() 

if debug:
    print ">>> Lines to delete:",
    print delete

file = open(inputname)
outputfile = open(outputname, 'w')
i = 0
for line in file:
    i += 1
    if not i in delete:
        outputfile.write(line)
outputfile.close()

