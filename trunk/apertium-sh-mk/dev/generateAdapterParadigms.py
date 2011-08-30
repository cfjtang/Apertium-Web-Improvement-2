# coding=UTF-8
# A script to generate adapter paradigms for all names generated in verbAlign.py


import sys, subprocess, string
from lxml import etree
from subprocess import Popen, PIPE
from copy import deepcopy

if len(sys.argv) != 2:
    print ('\nA script to generate paradigms according to the name assigned by verbAlign.py')
    print ('Usage:\n')
    print ('python '+sys.argv[0]+' verbAlign_output_file\n')
    sys.exit()

# Function definitions

def LeftRightHandSide(name):
    lhs=[]
    rhs=[]

    # Left hand side    
    if name.startswith('_perf'): lhs.append('perf')
    if name.startswith('_imperf') or name.startswith('_perf_imperf'): lhs.append('imperf')    
    if name.find('_iv_to_')!=-1 or name.find('_iv_tv_to_')!=-1 or name.find('_iv_tv_ref_to_')!=-1 or name.find('_iv_ref_to_')!=-1: lhs.append('iv')
    if name.find('_tv_to_')!=-1 or name.find('_tv_ref_to_')!=-1: lhs.append('tv')
    if name.find('_ref_to_')!=-1: lhs.append('ref')
    
    # Right hand side
    if name.find('_to_perf_')!=-1: rhs.append('perf')
    if name.find('_to_imperf_')!=-1 or name.find('_to_perf_imperf')!=-1: rhs.append('imperf')    
    if name.endswith('iv_\n') or name.endswith('iv_tv_\n'): rhs.append('iv')
    if name.endswith('tv_\n'): rhs.append('tv')
    
    return (lhs,rhs)

paradigme=open(sys.argv[1])

# The elements
perf=etree.Element("s",n='perf')
imperf=etree.Element("s",n='imperf')
iv=etree.Element("s",n='iv')
tv=etree.Element("s",n='tv')
ref=etree.Element("s",n='ref')


drvo=etree.Element("root")
pardefs=etree.SubElement(drvo,"pardefs")

for paradigma in paradigme:
    pardef=etree.SubElement(pardefs,'pardef',n=unicode(paradigma.rstrip('\n')))
    
    (lhs,rhs)=LeftRightHandSide(paradigma)
    
    #print paradigma + " <==> "
    #print lhs
    #print rhs
    #print
   
    e=etree.SubElement(pardef,'e')
    i=etree.SubElement(e,'i')
    
    nastavakL=""    
    nastavakR=""
    if 'perf' in lhs: nastavakL+='perf_'
    if 'perf' in rhs: nastavakR+='perf_'
    if 'imperf' in lhs: nastavakL+='imperf_'
    if 'imperf' in rhs: nastavakR+='imperf_'        
    nastavakL+="lhs"
    nastavakR+="rhs"    
    pleva=etree.SubElement(e,'par',n=nastavakL)
    pleva=etree.SubElement(e,'par',n=nastavakR)
    
    nastavakL=""    
    nastavakR=""
    if 'iv' in lhs: nastavakL+='iv_'    
    if 'iv' in rhs: nastavakR+='iv_'
    if 'tv' in lhs: nastavakL+='tv_'    
    if 'tv' in rhs: nastavakR+='tv_'
    if 'ref' in lhs: nastavakL+='ref_'
    nastavakL+="lhs"
    nastavakR+="rhs"    
    pleva=etree.SubElement(e,'par',n=nastavakL)
    pleva=etree.SubElement(e,'par',n=nastavakR)
    
    #print etree.tostring(pardefs)
    
print etree.tostring(pardefs)
