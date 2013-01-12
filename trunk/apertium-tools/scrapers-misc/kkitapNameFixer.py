#!/usr/bin/env python3

import sys
import fileinput
import sys

if len(sys.argv) < 2:
    files="kaz.bible.kkitap.txt"
else:
    files=sys.argv[1]

for line in fileinput.input(files, inplace=True):
   # for line in lines:
    #print(lines)
    i=0
    j=0
    line=line.strip()
    if "Патшалықтар 1" in line or "Патшалықтар 2" in line:
        #line="1 Самуил 1"
        
        if i<26:
            line=line.replace("Патшалықтар 1","Самуил 1").strip()
            line=line.replace("Патшалықтар 2","Самуил 2").strip()
        i+=1
    elif "Патшалықтар 3" in line or "Патшалықтар 4" in line:

        line=line.replace("Патшалықтар 3","Патшалықтар 1")
        line=line.replace("Патшалықтар 4","Патшалықтар 2")
    print(line)

    #print("".join(lines))
