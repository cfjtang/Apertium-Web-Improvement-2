#!/usr/bin/env python3

from datetime import date
import lxml.html
import http.client
#import pprint

xal=[]
chv=[]
eng=[]
langData={}
langs={"xal","chv"}
for lang in langs:
    langData[lang]={}

def getPage(conn, url):
    conn.request("GET", url)
    res = conn.getresponse()
    if res.status != 200:
        print(url, res.status, res.reason)
        return
    contents = res.read().decode('utf-8')
    doc = lxml.html.fromstring(contents)
    conn.close()
    return doc


def output():
    tofile=""
    length=len(eng)
    for lang in langs:
        i=0      
        for i in range(0, length):     
            if lang=="xal":
                line=eng[i].strip()+","+xal[i].strip()
                tofile+=eng[i].strip()+","+xal[i].strip()+"\n"
            else:
                line=eng[i].strip()+","+chv[i].strip()
                tofile+=eng[i].strip()+","+chv[i].strip()+"\n"
            (english, target) = line.split(',')
            langData[lang][english] = target
            i+=1
        outputfile= lang+".dat"
        open(outputfile,"w").write(tofile)
        tofile=""


def main():
    
    start=0
    conn=http.client.HTTPConnection('www.gospelgo.com')

    url1="http://gospelgo.com/a/kalmyk_nt.htm"
    url2="http://gospelgo.com/u/chuvash_nt.htm"
    url3="http://gospelgo.com/e/English%20KJV%20Bible%20-%20Unicode.htm"
    urls=[url1, url2, url3]
    for url in urls:
  
        html = getPage(conn, url)

        for el in html.findall(".//a") :

            if "href" not in el.attrib:

             if el.text and "Matthew 1" in el.text and "English" in url:
                 start=2
             elif el.text and "English" not in url:
                 start=1
             if el.text and (start == 2 or start == 1)  and "Unicode" not in el.text:
                #detect lang & save into appropriate array/list
                if "kalmyk" in url:
                    xal.append(el.text)

                elif "chuvash" in url:
                    chv.append(el.text)

                elif "English" in url:
                    eng.append(el.text)

                if start != 2:
                    start=0
    output()

main()
