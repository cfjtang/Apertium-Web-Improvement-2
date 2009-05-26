#!/opt/local/bin/python2.5

# Copyright (C) 2009 
# Author: Gabriel Synnaeve 
# License: http://www.opensource.org/licenses/PythonSoftFoundation.php (MIT)

# Usage: ./replace.py file1 file2 will create:
#  - file1[:last]".rep."file1[last]
#  - file2[:last]".rep."file2[last]
# that will be filtered versions of file1 and file2 without:
#  - numbers
#  - empty phrases
#  - the content of "rep_list[]"

import sys, re

try:
    n1 = sys.argv[1]
    n2 = sys.argv[2]
except:
    print "Usage: ./replace.py file1 file2"
    sys.exit(-1)

f1 = open(n1)
f2 = open(n2)
t1 = n1.split('.')
t2 = n2.split('.')
t1[len(t1)-1] = 'rep.' + t1[len(t1)-1] 
t2[len(t2)-1] = 'rep.' + t2[len(t2)-1] 
on1 = '.'.join(t1)
on2 = '.'.join(t2)
of1 = open(on1, 'w')
of2 = open(on2, 'w')
num = re.compile('[0-9]')
empty = re.compile('^$')
######## <<< The replacement list to be edited >>> ########
rep_list = ['&quo','&am','&nbs','</s>','<s>','*',';','`','###','--',
        '!','?','.',':',',','[',']','(',')','- ',' -','  ']
avoid_quote = re.compile("'[^s|ch]")

try:
    for l1 in f1:
        l2 = f2.next()
        if re.search(num, l1) or re.search(num, l2):
            continue
        for item in rep_list:
            l1 = l1.replace(item,'')
            l2 = l2.replace(item,'')
        l1 = avoid_quote.sub('', l1)
        l2 = avoid_quote.sub('', l2)
        if re.search(empty, l1) or re.search(empty, l2) or\
                len(l1.split()) <= 1 or len(l2.split()) <= 1:
            continue
        of1.write(l1.replace('  ',' '))
        of2.write(l2.replace('  ',' '))

finally:
    f1.close()
    f2.close()
    of1.close()
    of2.close()

print "written: " 
print on1
print on2

