# coding=UTF-8
# A script to adapt verbs which have the same meaning, but not the same analyses
# Note: the script does not generate the paradigms

import sys, subprocess, string
from lxml import etree
from subprocess import Popen, PIPE

if len(sys.argv) != 3:
    print ('\nA script to adapt bidix entries for verbs')
    print ('Usage:\n')
    print ('python '+sys.argv[0]+' sourcedix targetfile\n')
    sys.exit()

dixSourceFile=open(sys.argv[1],'r')
dixTargetFile=open(sys.argv[2],'w')

drvo = etree.parse(dixSourceFile)

dictionary=drvo.getroot();

def isAVerb(p):
    s=p.find("l").find("s");
    if s==None or s.attrib["n"]!="vblex":
        return False
    else:
        return True

for section in dictionary.iter("section"):
    for entri in section.iter("e"):
        par=entri.find("p")
        if not isAVerb(par):
            continue
        else: # If we indeed have found a verb
            paradigma="_"
            
            glagol=unicode(par.find("l").text)
            glagolot=unicode(par.find("r").text)

            p=subprocess.Popen(['echo '+glagol+' | apertium -d .. sh-mk-morph '],shell=True,stdout=subprocess.PIPE, close_fds=True);
            analiza = unicode (p.communicate()[0])
        
            q=subprocess.Popen(['echo '+glagolot+' | lt-proc ../mk-sh.automorf.bin'],shell=True,stdout=subprocess.PIPE, close_fds=True);
            analizata = unicode (q.communicate()[0])
            
            print analiza +':'+ analizata
            
            if string.find(analiza,'<perf>') !=-1: paradigma+="perf_"
            if string.find(analiza,'<imperf>') !=-1: paradigma+="imperf_"                       
            if string.find(analiza,'<iv>') !=-1: paradigma+="iv_"
            if string.find(analiza,'<tv>') !=-1: paradigma+="tv_"
            if string.find(analiza,'<ref>') !=-1: paradigma+="ref_"
            paradigma+="to_"
            if string.find(analizata,'<perf>') !=-1: paradigma+="perf_"
            if string.find(analizata,'<imperf>') !=-1: paradigma+="imperf_"                       
            if string.find(analizata,'<iv>') !=-1: paradigma+="iv_"
            if string.find(analizata,'<tv>') !=-1: paradigma+="tv_"
            
            eljement=etree.SubElement(entri,'par',n=paradigma)

drvo.write(dixTargetFile,encoding='utf-8')        
