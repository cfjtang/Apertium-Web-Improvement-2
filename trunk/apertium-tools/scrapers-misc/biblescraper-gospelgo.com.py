#!/usr/bin/python3

# biblescraper.py
# Copyright 2012 Daniel Huang <danielhonline dot com>
# Copyright 2012 Jonathan North Washington [jnw dot name]
# http://gospelgo.com/bibles.htm Bible scraper
#
# WARNING
# ONLY USE THIS SCRIPT WITH PERMISSION FROM http://gospelgo.com/ ADMINISTRATORS
# UNAUTHORIZED ACCESS OF http://gospelgo.com/ IS ILLEGAL IN MOST COUNTRIES!!!
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

import urllib.request, urllib.parse, urllib.error
import re
import argparse
import os.path
import sys
import codecs
from os.path import exists
import sys

dfname=""
outputfile=""
i =0
isFile=False


enterbody=0
tofile=""
skip=False
withfirsttitle=0
has_loc=False
loc = ""
islinkbible=False
i=0
lines1=""


def processdata(url):
    count=0
    enterbody=0
    start=0
    linenum=0
    global tofile

    withfirsttitle=0

    source = urllib.request.urlopen(str(url))  
    
    done=0 #checks to see if loop is done
    output ="" #output to file
    start = 0 #checks to see if started
    #check encoding
    if "uzbek" in urll:
        encoding='latin-1'
    elif "tuvnt." in urll:
        encoding='windows-1251'
    else:
        encoding='utf-8'

    for line2 in source: #for every verse
        line=(str(line2, encoding)).strip()
        if done==1: #break out of multiple levels
            enterbody=0
            break;



        if "<body>" in line.lower(): #enter body
             enterbody=1
        elif "<p><a>" in line : #if title                
              title = (line[line.rfind("<a>")+3:line.rfind("</a>")]).strip()
             
              if start ==0:  
                                                
                   tofile =tofile+ title.strip() 
              else:                                              
                   tofile =tofile+ "\n\n" + title.strip()
                   
                   linenum = 0
                   withfirsttitle=1
              start=1
                 
        elif (encoding=="windows-1251"  and "<b>" in line or "</b><p>" in line) or ("<p class" in line and start ==1) or (enterbody ==1 and start !=1 and "blue" in line) : #i : #if subtitle follow title or subtitle only             
   
                title = re.compile(r'<[^<]*?/?>').sub('', line)  
                if withfirsttitle:                                   
                    tofile =tofile+ "\n" + title.strip()
                else:
                    if start ==0:
                        tofile =tofile+"\n"+  title.strip()
                    else:
                        
                        if  "(" in title:
                            tofile =tofile+ "\n" + title.strip()
                        else :
                            tofile =tofile+ "\n\n" + title.strip()
                        linenum = 0  
                    start=1   
           
            
        elif "<p>" is line and "<a" not in line and line.__len__()<5 and start == 1 or ("<!--" in line and not islinkbible): #if line = <p>, end the bible scraping loop
                 done = 1
                 startbody=0            
                 linenum = 0

        else: #verse

                   format5 =re.match('\d+:\d+', line)
                   if start==1 and "<b>" in line and linenum ==0 and format5 :  # for num:num in the begining of the verse case, first line of the verse

                       tmpline=re.compile(r'<b>(.*?)</b>').search(line)  #get text between <b></b>
      
                       length= len(str(tmpline.group(1)))
                       if "<p>" in line:
                            remainline = line[length+6: int(len(line))].strip()
                       else:
                         remainline = line[length+3: int(len(line))].strip()
 
                         stripedline = re.compile(r'<[^<]*?/?>').sub('', remainline) #rest part of string need parsed                  
                         tofile=tofile+"\n" + str(tmpline.group(1)) + re.sub(r'(\d+)', '\n\\1', stripedline)                      
                         linenum = 1  #first line of the verse
                   elif start ==1 and "<p><a name" not in line: #if started and not an html a link, print the verse

                        linenum = 2
                        stripedline = re.compile(r'<[^<]*?/?>').sub('', line)

                        format1 =re.match('\(\d+:\d+-\d+\)', stripedline) 
                        format2=re.match('\(\d+:\d+\)', stripedline)
                        format3 =re.match('\d+:\d+', stripedline)       
                        format4 =re.match('\(\d+:\d+-\d+:\d+\)', stripedline)

                        if format3 and  "uzbek" in urll:

                            index= stripedline.index(":")
                            first= stripedline[0:index]
                            second=stripedline[index+1:index+2]
             
                            tofile= tofile+  " "+ first
               
                            stripedline= second + " " +stripedline[index+3:len(stripedline)]
           
                       

	
                        if format1 or format2 or format4:

                            tofile =tofile+ "\n" + stripedline +" "
                                                                          
                        else:  # handle sentense has numbers
  
                             curr=0
                             tmpline2 =""                         
                             needappend =0
                             numb=0
                             #handling numbers in paragraphs
                             for m in re.finditer(r"\d+", stripedline):

                                 if m.start() is 0 or stripedline[m.start()-2:m.start()-1]  == "." or stripedline[m.start()-4:m.start()-3] =="." or stripedline[m.start()-2:m.start()-1] =="?" or stripedline[m.start()-2:m.start()-1] == "!" or stripedline[m.start()-2:m.start()-1] =="," or stripedline[m.start()-2:m.start()-1] ==":" or stripedline[m.start()-2:m.start()-1] ==":":  
                                     if needappend: 
                                         tofile= tofile+stripedline[curr:m.start()-1]     
                                         needappend=0
                                         tmpline2= stripedline[m.start():m.end()]
                                         curr= m.end()+1
                                     else:
                                         tmpline2 =tmpline2+ stripedline[curr:m.end()]      
                                         curr=m.end()
   
                                 else:
                                     tofile=tofile+ re.sub(r'(\d+)', '\n\\1', tmpline2)            
                                     tofile= tofile + stripedline[ curr:m.end() ]      
                                     curr=m.end()
                                     tmpline2 =""  # clear line
                                     needappend=1
                             tmpline2=tmpline2+stripedline[curr:len(stripedline)]
                             tofile=tofile+ re.sub(r'(\d+)', '\n\\1', tmpline2)    
     


#print(sys.version_info)
#argparser
parser = argparse.ArgumentParser(description='This script scrapes Bible translations into a txt file')
parser.add_argument('-i','--input', help='enter name of url file', required=True)
parser.add_argument('-o','--output', help='enter name of datafile(s)', required=False)
parser.add_argument('-d','--output_loc', help="enter directory to put output file in", required=False)

args = vars(parser.parse_args())

#checks if -d is empty
if args['output_loc'] is not None:
    has_loc=True

    loc = str(args['output_loc'])
    if not loc.endswith("/"):
        loc=loc+"/"

#checks input
if "http://" in args['input'] : #if url
    urll=args['input']
    #URL case need to check if link including other links for books
   
    lines = [urll]
    #read url contents first to check if url contains bible links
       
else: #if file
    if os.path.exists(args['input']):
        ins = open( args['input'], "r" )
        lines=ins.readlines()
        isFile=True
    else:
        sys.exit(0)
    

#output file
#file name: no output not file
if (args['output'] == None and isFile==False): #and not islinkbible:
    outputfile=urll[urll.rfind('/')+1:urll.__len__()]+".txt"

# file name yes output not file
if args['output'] != None and isFile==False:  # and not islinkible:
    outputfile=args['output']

          
                     
for urll in lines: #loop for every url
        skip = 0

        if "/bible" in urll or "tuvnt." in urll:
            islinkbible=True

        #check if line starts with #
        if str(urll).strip().startswith("#") :
            skip=True

        if str(urll).strip() is "\n" or str(urll).strip() is "":
            skip = True
        #generate url if file
        if not skip and isFile==True and urll is not "" or (islinkbible is True and i==0 and isFile==True and not skip):
            
            dfname=urll[urll.rfind('/')+1:urll.__len__()]
            outputfile=dfname+".txt"
            #remove special chars from name
        #generate outputfile name
        if not skip or (not skip and islinkbible is True and i==0):
            outputfile1=outputfile.replace("%","")
             
            tempname = re.sub(r'.htm', '', outputfile1)
            outputfile = re.sub(r'.html', '', tempname)

            outputfile = re.sub("\n",'',outputfile)
            outputfile = re.sub(" ",'',outputfile)
            outputfile = re.sub(" ",'',outputfile)
           
            #if os.path.exists(outputfile):      
            print("Scraping " + urll.strip()) 
            if not os.path.exists(loc) and loc:
                os.makedirs(loc) 

        if not skip and os.path.exists(os.path.join(loc,outputfile)) and islinkbible is False :
            print(outputfile+" already exists, do nothing." + loc)
            skip=True
       
        
        elif not skip and str(outputfile).strip() is not "" and islinkbible is False:
            if has_loc is True:

                file = open(str(os.path.join(loc,outputfile)), "w")
            else:

                file = open(str(os.path.join(loc,outputfile)), "w") #creates new data file
 
        elif islinkbible:

            if i==0 and os.path.exists(os.path.join(loc,outputfile)):
                print(outputfile+" already exists, do nothing.")
                skip=True
                break;               
            elif i==0:
                file = open(str(os.path.join(loc,outputfile)), "a")
        
        if not skip:
  
            if not islinkbible:
                processdata(urll)
          
            else:
                if "uzbek" or "tuvnt." in urll:
                       encoding='latin-1'
 
                else:
                       encoding='utf-8' 
  
                source1 = urllib.request.urlopen(urll)
                html = source1.read()
                line3=(str(html, "latin-1"))
               
                line4 = re.findall('.*?\<a href="(.*?)\>.*?', line3)    

                if 1:               
                          line4 = re.sub(r'\[', '', str(line4))
                          line4 = re.sub(r'\]', '', str(line4))
                          line4 = re.sub(r'\"', '', str(line4))
                          line4 = re.sub(r'\'', '', str(line4))  
                          a = line4.split(",")
      

                          islinkbible= True

                          for myline in a :
                                if "http" in myline and "htm" in myline and "statcounter" not in myline:
                                    processdata(myline)
                                    i=i+1


        if (islinkbible and not skip) or (islinkbible and tofile !=""):

            file.write(tofile)  	
            file.close()
            islinkbible=False
            tofile=""
            i=0
                     
        elif not skip and not islinkbible and tofile !="":

            file.write(tofile)
            file.close()
            tofile=""
            startbody=0
            start=0

        elif islinkbible:
               startbody=0
               start=0
               i=i+1
        if islinkbible is False:
            skip=False


