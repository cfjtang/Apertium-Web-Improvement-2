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
    enterbody=0
    global tofile
    ischapter=0
    titlep=""  
    withfirsttitle=0
    newchapter=1
    num8=1

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

        if ("uzbek" in urll or "turkmen" in urll)  and "HTML><HEAD><TITLE>" in line and enterbody ==0:
               chapter= line[0:30]
               chapter = re.compile(r'<[^<]*?/?>').sub('', line) 
               if "-" in chapter:
                  match = re.finditer('-', chapter)
                  for m in match:
                     chapter= chapter[m.start()+2: len(chapter)]  
                     enterbody=1
                     ischapter=1
               elif ":" in chapter:
                  match = re.finditer(':', chapter)
                  for m in match:
                     chapter= chapter[m.start()+2: len(chapter)]  
                     enterbody=1
                     ischapter=1

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
        elif "<p><b>" in line and "uzbek" in urll: # if title for some files like uz psalms
              title = (line[line.rfind("<b>")+3:line.rfind("</b>")]).strip()
              if start ==0:  
                         
                   tofile =tofile+"" + title.strip()
                  
                   
              else:                                              
                   tofile =tofile+ "\n\n" + title.strip()
                   
                   linenum = 0
                   withfirsttitle=1
              start=1
        elif (encoding=="windows-1251"  and "<b>" in line or "</b><p>" in line) or ("<p class" in line and start ==1 and "turkmen" not in urll) or (enterbody ==1 and start !=1 and "blue" in line) : #i : #if subtitle follow title or subtitle only                           

            title = re.compile(r'<[^<]*?/?>').sub('', line)  
            
            if "uzbek" not in urll and "turkmen" not in urll:
                if withfirsttitle:                                   
                    tofile =tofile+ "\n" + title.strip()
                else:
                    if start ==0:
                        tofile =tofile+"\n"+  title.strip()
                    else:
                        
                        if  "(" in title:
                            tofile =tofile+ "\n" + title.strip()
             
                        else :
                            tofile =tofile+ "\n\n"+ title.strip()
 
                        linenum = 0  
                    start=1   
            else:
                titlep= title.strip() 

                start=1
            
        elif "<p>" is line and "<a" not in line and line.__len__()<5 and start == 1 or ("<!--" in line and not islinkbible) or ("<script type=" in line and ("uzbek" in urll or "turkmen" in urll )): #if line = <p>, end the bible scraping loop
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
                        

                        if format3 and  ("uzbek" in urll or "turkmen" in urll) or  ("16:1" in stripedline and "turkmen" in urll)or("30:1" in stripedline and "turkmen" in urll  ) :                           
                            tt= stripedline.index(":")                          
                            first= stripedline[0:tt]
                            second=stripedline[tt+1:tt+2]                         
            
                            if tofile=="": 
                                tofile= tofile + chapter.strip() + " " +first

                            else:
                                if chapter:
                                   tofile= tofile + "\n"+"\n" + chapter.strip() + " " +first 
                                else:
                                   tofile= tofile + "\n"+"\n" + " " +first 
                            if titlep=="":
                                 if "uzbek" in url:
                                     tofile = tofile 
                                 else:

                                     tofile=tofile
                            elif tofile!="" and   newchapter==1:
                          
                                tofile= tofile +"\n" + titlep.strip() 
                                
                                titlep = ""
                                
                                newchapter=0  
                            else:

                                    tofile = tofile  + "\n" + titlep                                    
                                    titlep = ""
                                
                            stripedline= second + " " +stripedline[tt+3:len(stripedline)]
                        
                        elif format3 is None and titlep !="" and ("uzbek" in urll or "turkmen" in urll) :
                            if "uzbek" in urll:
                               tofile = tofile  + "\n" + titlep

                            else:
                               tofile = tofile  + "\n" + titlep
                               
                            titlep = ""
       

	
                        if format1 or format2 or format4:
                            stripedline=re.sub('/“','"',stripedline) #sub unknown chars
                            tofile =tofile+ "\n" + stripedline
                                                                          
                        else:  # handle sentence has numbers
                             curr=0
                             tmpline2 =""                         
                             needappend =0

                             #handling numbers in paragraphs
                             for m in re.finditer(r"\d+", stripedline):
                                 ok=False 
 
                                 num1=stripedline[m.start():m.end()]
                     
                            
                                 if (stripedline[m.end():m.end()+1] == ":" or stripedline[m.end():m.end()+1] == "-" ) and "kirghiz_nt" in urll : 
                                     
                                         ok= False
                                 elif ( "kirghiz_nt" in urll and str(num1) == str(int(num8)+2) and (int(num1) == 4 and stripedline[m.start()-1:m.start()] != "-" and "kirghiz_nt" in urll)):
                                       
                                         ok= True
                                 elif (stripedline[m.end():m.end()+1] == ":" or stripedline[m.end():m.end()+1] == "-") and "kirghiz_nt" in urll:   #special
                                      
                                         ok= False
                                 
                                 elif str(num8) != '' and str(num8) != ' ' :
                                   if str(num1) == str(int(num8)+1):

                                       if stripedline[m.start()-1:m.start()] == "-" and "kirghiz_nt" in urll:
                                     
                                          ok= False
                                       elif int(num1) ==47 and stripedline[m.end()+1:m.end()+2] == "," and "kirghiz_nt" in urll:
                                        
                                          ok= False

                                       else:
                                   
                                                                     
                                         ok= True
                                       num8= num1       
                          
                               
                                 if ( (str(num1)!="601")  and (( m.start() is 0 or stripedline[m.start()-2:m.start()-1]  == "." or stripedline[m.start()-4:m.start()-3] =="." or stripedline[m.start()-2:m.start()-1] =="?" or stripedline[m.start()-2:m.start()-1] == "!" or (stripedline[m.start()-2:m.start()-1] =="," and "kirghiz" not in urll) or stripedline[m.start()-2:m.start()-1] ==":" or stripedline[m.start()-4:m.start()-3] =="!" or stripedline[m.start()-4:m.start()-3] ==":" or stripedline[m.start()-4:m.start()-3] =="?" or stripedline[m.start()-4:m.start()-3] =="," or stripedline[m.start()-2:m.start()-1] ==":" or stripedline[m.start()-3:m.start()-2] =="." or stripedline[m.start()-4:m.start()-3] =="." or (stripedline[1:2] =="9" and "turkmen" in urll)or stripedline[m.start()-2:m.start()-1] =="»" or stripedline[m.start()-3:m.start()-2] =="." or stripedline[0:1] ==" " or stripedline[m.start()-2:m.start()-1] =="\"" or stripedline[m.start()-2:m.start()-1] ==";" or stripedline[m.start()-2:m.start()-1] == "—"  or stripedline[m.start()-2:m.start()-1] == "«" or stripedline[m.start()-1:m.start()-0] == "." or (stripedline[m.start()-2:m.start()-1] == "'" and "gen" not in urll)  or stripedline[m.start()-3:m.start()-2] =="?" or stripedline[m.start()-1:m.start()-0] =="?" or  stripedline[m.start()-1:m.start()-0] =="," or stripedline[m.start()-1:m.start()-0] == "—" or stripedline[m.start()-1:m.start()-0] == "«" or stripedline[m.start()-3:m.start()-2] == "!" or (ok == True and "uzbek/bible" not in urll)  ) or (ok == True and "uzbek/gen" not in urll)))  :  #  ok=true and uzbek not in urll ???????
                                  
                                     num8= num1
                                     head=1  
                                     
                                     if ( stripedline.startswith("-")  ):
                   
                                        stripedline= stripedline[1:len(stripedline)]
                                     if needappend: 
                                         
                                         tofile= tofile+stripedline[curr:m.start()-1] 
        
                                         needappend=0
                                         tmpline2= stripedline[m.start():m.end()] +" "  #?????
     
                                         curr= m.end()+1
                                     else:

                                         if (stripedline[m.end():m.end()+1]) ==":":
                                            tmpline2 = tmpline2 + stripedline[curr:m.end()-3]
                                            tofile= tofile+stripedline[m.end()-2:m.end()+3]

                                            curr= m.end()+3
                                         else:
                                            tmpline2 = tmpline2 + stripedline[curr:m.end() +1]   
                               
                                            curr=m.end()+1

                                 else:

                                     tofile=tofile+ re.sub(r'(\d+)', '\n\\1', tmpline2.strip() + " ")                          
              
                                     tofile= tofile  + stripedline[ curr:m.end() ].strip()  # added "
     
                                     curr=m.end()
                                     tmpline2 =""  # clear line
                                     needappend=1
                             tmpline2=tmpline2+stripedline[curr:len(stripedline)]  ##

                             tofile=tofile+ re.sub(r'(\d+)', '\n\\1', tmpline2) + " "   
     


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
        print("Input file not found, exit and do nothing")
        sys.exit(0)
    

#output file
#file name: no output not file
if (args['output'] == None and isFile==False): #and not islinkbible:
    if ("uzbek" in urll or "turkmen" in urll) and "bible" in urll:
           filename = urll[22:len(urll)]
           match = re.finditer('/', filename)
           for m in match:
              outputfile=filename[0:m.start()]+".txt"
    else:
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
             if ("uzbek" in urll or "turkmen" in urll) and "bible" in urll:
                  filename = urll[22:len(urll)]
          
                  match = re.finditer('/', filename)
                  for m in match:
                      outputfile=filename[0:m.start()]+".txt"
             else:
                   dfname=urll[urll.rfind('/')+1:urll.__len__()]
                   outputfile=dfname+".txt"
            #remove special chars from name
        #generate outputfile name
        if (not skip or (not skip and islinkbible is True and i==0)) and ("uzbek" not in urll or "turkmen" not in urll):
            outputfile1=outputfile.replace("%","")
             
            tempname = re.sub(r'.htm', '', outputfile1)
            outputfile = re.sub(r'.html', '', tempname)

            outputfile = re.sub("\n",'',outputfile)
            outputfile = re.sub(" ",'',outputfile)
            outputfile = re.sub(" ",'',outputfile)
           
            #if os.path.exists(outputfile):      
            print("Scraping " + urll.strip() + " to " + outputfile) 
            if not os.path.exists(loc) and loc:
                os.makedirs(loc) 

        if not skip and os.path.exists(os.path.join(loc,outputfile)) and islinkbible is False :
            print(outputfile+" already exists, do nothing.")
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
                islinkbible = False
               # break;               
            elif i==0:
                file = open(str(os.path.join(loc,outputfile)), "a")
        
        if not skip:
  
            if not islinkbible:
                processdata(urll)
          
            else:
                if "uzbek" or "tuvnt." in urll:
                       encoding='utf-8'
 
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


