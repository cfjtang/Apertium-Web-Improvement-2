#!/usr/bin/env python3
import re
import codecs
import http.client
import argparse
from bs4 import BeautifulSoup
import os.path
import sys

domain="www.eastturkistan.net"
urltemplate="http://www.eastturkistan.net/uygurche/islam/%s"
url = "http://www.eastturkistan.net/uygurche/islam/quran.htm"

#argparser
parser = argparse.ArgumentParser(description='This script scrapes the Quran from http://www.eastturkistan.net/uygurche/islam/quran.htm')
parser.add_argument('filename', metavar='o', help='Name of file to scrape to (output file)', nargs='?')

args = vars(parser.parse_args())

if args['filename'] is not None:
    filename=args['filename']
else:
    filename="eastturkistan.net-quran.txt"

print("Scraping to "+filename)

if os.path.isfile(filename):
    sys.exit("Error: "+filename+" already exists")

def getPage(url):
    conn = http.client.HTTPConnection(domain)
    conn.request("GET", url)
    res = conn.getresponse()
    if res.status != 200:
        print(url, res.status, res.reason)
        return
    contents = res.read()
    soup = BeautifulSoup(contents)    
    return soup

def getallcontents(url):
    output="" 
    badTokens=['\t', '\n']
    content=getPage(url)
    for titles in content.find_all('p'):    #read all titles and urls from main page        
        if "margin-top:" not in str(titles) and "center" not in str(titles):
            link=titles.a.get('href')       #get url
            link= urltemplate%link
            title=''.join(c for c in titles.text if not c in badTokens)   
            if output=="":
                output=output+title+'\n'
            else:
                output=output+'\n'+title+'\n'

            page = getPage(link)  
            
            for content in page.find_all('p'):    #read all chapter contents based on link get from main page
                if "http:" not in str(content) and "E-Mail:" not in str(content) and not content.find('b') and not content.find('i'):
                    text=content.text.strip()
                    if '\n' in text and "қуран керим" not in text:    #do replacement for preparing sentence segment
                            text=''.join(c for c in text if not c in badTokens)               
                            text=text.replace('].','].\n').replace(". ",".\n").replace('Уйғурчә тәрҗимиси','')                                                 
                            sentences=re.split('(?<=\].)[\s]',text)
                            for sentence in sentences:
                                num=sentence[sentence.rfind('[')+1:sentence.rfind(']')]
                                
                                
                                sentence=sentence[0:sentence.rfind('[')]+'.'

                                if sentence[len(sentence)-2:len(sentence)-1]==" ":
                                    sentence=sentence[0:len(sentence)-2]
                                if sentence[len(sentence)-1:len(sentence)]!=".":
                                    sentence=sentence+"."
                                if sentence.strip()!="." and "(." not in sentence.strip():
                                    output=output+num+ '. ' + sentence.strip()+'\n'

    #output file
        OUTPUT_FILE = codecs.open(filename, "w", "utf-8")       
        OUTPUT_FILE.write(output)
    OUTPUT_FILE.close()

def main():    
   getallcontents(url)

main()
